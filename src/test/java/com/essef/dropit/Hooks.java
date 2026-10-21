package com.essef.dropit;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.AfterAll;
import io.restassured.RestAssured;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Boots a REAL DropIt server (dropit.py) on a test port against a throwaway
 * shared folder, seeded with known files. The server under test is never
 * mocked: every scenario talks HTTP to the genuine app.
 *
 * Where dropit.py comes from, first match wins:
 *   1. -Ddropit.py=<path>  or  env DROPIT_PY
 *   2. ../dropit/dropit.py            (sibling checkout, the local dev layout)
 *   3. dropit-server/src/dropit.py    (the GitHub Actions layout)
 *   4. ~/workspace/dropit/dropit.py   (this machine's live source tree)
 * If none exists but a server already answers on the test port, it is used
 * as-is and left running.
 *
 * The user's real notes store (~/.config/DropIt/notes.json on Linux,
 * %APPDATA%\DropIt\notes.json on Windows) is backed up before the suite and
 * restored afterwards, because the notes API is global to the app data dir.
 */
public class Hooks {

    static final int PORT = Integer.getInteger("dropit.port", 8765);
    static final String BASE = "http://127.0.0.1:" + PORT;

    static final Map<String, String> SEEDS = new LinkedHashMap<>();
    static {
        SEEDS.put("hello.txt",
                "Hello DropIt automation\nbanana keyword search file\nsecond line of the hello file\n");
        SEEDS.put("invoice.txt",
                "Invoice INV-2026-001\nTotal due: 42 dollars\nCustomer: Automation Tester\n");
        SEEDS.put("meeting.md",
                "# Meeting Notes\nDiscuss roadmap, budget and hiring plan.\nAction items for the QA team.\n");
        SEEDS.put("data.csv",
                "id,name,score\n1,Alice,91\n2,Bob,85\n3,Carol,77\n");
        SEEDS.put("empty.txt", "");
    }

    private static Process serverProcess;
    private static Path sharedDir;
    private static Path notesBackup;
    private static boolean booted;

    private final World world;

    public Hooks(World world) {
        this.world = world;
    }

    static Path sharedDir() {
        return sharedDir;
    }

    @BeforeAll
    public static void bootServer() throws Exception {
        RestAssured.baseURI = BASE;
        sharedDir = Paths.get(System.getProperty("user.dir"), "target", "test-shared");
        deleteRecursively(sharedDir);
        Files.createDirectories(sharedDir);
        seedFiles();

        backupNotes();

        if (serverAnswers()) {
            booted = true; // external server already running: use it, don't kill it
            return;
        }
        Path py = findDropitPy();
        if (py == null) {
            throw new IllegalStateException(
                    "dropit.py not found. Set -Ddropit.py=<path> or env DROPIT_PY, "
                            + "or start a DropIt server on port " + PORT + " first.");
        }
        Path log = Paths.get(System.getProperty("user.dir"), "target", "dropit-server.log");
        Files.createDirectories(log.getParent());
        ProcessBuilder pb = new ProcessBuilder(
                pythonExe(), py.toString(), "--port", String.valueOf(PORT),
                "--dir", sharedDir.toString());
        pb.directory(py.getParent().toFile()); // dropit_ai / dropit_mt imports live beside dropit.py
        pb.redirectErrorStream(true);
        pb.redirectOutput(log.toFile());
        serverProcess = pb.start();

        long deadline = System.currentTimeMillis() + 30_000;
        while (System.currentTimeMillis() < deadline) {
            if (!serverProcess.isAlive()) {
                throw new IllegalStateException("DropIt server exited early; log: "
                        + Files.readString(log, StandardCharsets.UTF_8));
            }
            if (serverAnswers()) {
                booted = true;
                return;
            }
            Thread.sleep(300);
        }
        throw new IllegalStateException("DropIt server did not answer within 30s; log: "
                + Files.readString(log, StandardCharsets.UTF_8));
    }

    @AfterAll
    public static void shutdownServer() throws Exception {
        if (serverProcess != null) {
            serverProcess.destroy();
            serverProcess.waitFor();
            serverProcess = null;
        }
        restoreNotes();
        booted = false;
    }

    /** Before every scenario: make sure the seed files exist again, so a
     *  destructive scenario (delete_all) can never break the next one. */
    @Before
    public void reseed() throws IOException {
        if (booted) {
            seedFiles();
        }
    }

    /** After every scenario: remove whatever the scenario created through the
     *  API, even when it failed halfway. */
    @After
    public void cleanup() {
        for (String id : world.createdNoteIds) {
            try {
                RestAssured.given().contentType("application/json")
                        .body("{\"id\":\"" + id + "\"}")
                        .post("/api/notes/delete");
            } catch (Exception ignored) {
            }
        }
        for (String name : world.createdFileNames) {
            try {
                RestAssured.given().contentType("application/json")
                        .body("{\"name\":\"" + name.replace("\\", "\\\\").replace("\"", "\\\"") + "\"}")
                        .post("/api/delete");
            } catch (Exception ignored) {
            }
        }
    }

    static void seedFiles() throws IOException {
        Files.createDirectories(sharedDir);
        for (Map.Entry<String, String> e : SEEDS.entrySet()) {
            Path p = sharedDir.resolve(e.getKey());
            if (!Files.exists(p)) {
                Files.writeString(p, e.getValue(), StandardCharsets.UTF_8);
            }
        }
    }

    private static boolean serverAnswers() {
        try {
            HttpURLConnection c = (HttpURLConnection) new URL(BASE + "/api/status").openConnection();
            c.setConnectTimeout(1000);
            c.setReadTimeout(1000);
            return c.getResponseCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    private static String pythonExe() {
        String os = System.getProperty("os.name", "").toLowerCase();
        return os.contains("win") ? "python" : "python3";
    }

    private static Path findDropitPy() {
        String prop = System.getProperty("dropit.py");
        if (prop != null && Files.isRegularFile(Paths.get(prop))) {
            return Paths.get(prop);
        }
        String env = System.getenv("DROPIT_PY");
        if (env != null && Files.isRegularFile(Paths.get(env))) {
            return Paths.get(env);
        }
        Path userDir = Paths.get(System.getProperty("user.dir"));
        Path home = Paths.get(System.getProperty("user.home"));
        Path[] candidates = {
                userDir.resolve("../dropit/dropit.py").normalize(),
                userDir.resolve("dropit-server/src/dropit.py"),
                userDir.resolve("dropit-server/dropit.py"),
                home.resolve("workspace/dropit/dropit.py"),
                home.resolve("workspace/gh/DropIt/src/dropit.py"),
        };
        for (Path p : candidates) {
            if (Files.isRegularFile(p)) {
                return p;
            }
        }
        return null;
    }

    private static Path notesPath() {
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            Path base = appData != null ? Paths.get(appData)
                    : Paths.get(System.getProperty("user.home"));
            return base.resolve("DropIt").resolve("notes.json");
        }
        return Paths.get(System.getProperty("user.home"), ".config", "DropIt", "notes.json");
    }

    private static void backupNotes() throws IOException {
        Path notes = notesPath();
        notesBackup = Paths.get(System.getProperty("user.dir"), "target", "notes-backup.json");
        Files.createDirectories(notesBackup.getParent());
        if (Files.isRegularFile(notes)) {
            Files.copy(notes, notesBackup, StandardCopyOption.REPLACE_EXISTING);
        } else {
            Files.deleteIfExists(notesBackup);
        }
    }

    private static void restoreNotes() throws IOException {
        Path notes = notesPath();
        if (notesBackup != null && Files.isRegularFile(notesBackup)) {
            Files.createDirectories(notes.getParent());
            Files.copy(notesBackup, notes, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void deleteRecursively(Path dir) throws IOException {
        if (!Files.exists(dir)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(dir)) {
            walk.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException ignored) {
                }
            });
        }
    }
}
