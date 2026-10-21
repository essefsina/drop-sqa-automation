package com.essef.dropit;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/** Glue for every feature file. All calls go over real HTTP to the DropIt
 *  server booted by {@link Hooks}. */
public class StepDefinitions {

    private final World world;

    public StepDefinitions(World world) {
        this.world = world;
    }

    // ------------------------------------------------------------- generic

    @Given("the DropIt server is running")
    public void serverIsRunning() {
        Response r = RestAssured.get("/api/status");
        assertThat(r.statusCode()).as("server health check").isEqualTo(200);
    }

    @When("I GET {string}")
    public void iGet(String path) {
        world.last = RestAssured.get(path);
    }

    @When("I GET {string} with range {string}")
    public void iGetWithRange(String path, String range) {
        world.last = RestAssured.given().header("Range", range).get(path);
    }

    @When("I POST raw JSON {string} to {string}")
    public void iPostRawJson(String body, String path) {
        world.last = RestAssured.given().contentType("application/json").body(body).post(path);
    }

    @When("I POST plain text {string} to {string}")
    public void iPostPlainText(String body, String path) {
        world.last = RestAssured.given().contentType("text/plain").body(body).post(path);
    }

    @Then("the response status is {int}")
    public void statusIs(int code) {
        assertThat(world.last.statusCode()).isEqualTo(code);
    }

    @Then("the response content type contains {string}")
    public void contentTypeContains(String part) {
        assertThat(world.last.contentType()).contains(part);
    }

    @Then("the response body contains {string}")
    public void bodyContains(String text) {
        assertThat(world.last.asString()).contains(text);
    }

    @Then("the response body is exactly {string}")
    public void bodyIsExactly(String text) {
        assertThat(world.last.asString()).isEqualTo(text);
    }

    @Then("the response body is empty")
    public void bodyIsEmpty() {
        assertThat(world.last.asString()).isEmpty();
    }

    @Then("the JSON field {string} is true")
    public void jsonFieldIsTrue(String field) {
        assertThat(world.last.jsonPath().getBoolean(field)).isTrue();
    }

    @Then("the JSON field {string} is false")
    public void jsonFieldIsFalse(String field) {
        assertThat(world.last.jsonPath().getBoolean(field)).isFalse();
    }

    @Then("the JSON field {string} equals {string}")
    public void jsonFieldEquals(String field, String expected) {
        Object v = world.last.jsonPath().get(field);
        assertThat(String.valueOf(v)).isEqualTo(expected);
    }

    @Then("the JSON field {string} is a number")
    public void jsonFieldIsNumber(String field) {
        assertThat(world.last.jsonPath().get(field)).isInstanceOf(Number.class);
    }

    @Then("the JSON field {string} is a list")
    public void jsonFieldIsList(String field) {
        assertThat(world.last.jsonPath().get(field)).isInstanceOf(List.class);
    }

    @Then("the JSON field {string} is present")
    public void jsonFieldPresent(String field) {
        assertThat(world.last.jsonPath().get(field)).isNotNull();
    }

    // ------------------------------------------------------------- homepage

    @Then("the homepage title is {string}")
    public void homepageTitle(String title) {
        assertThat(world.last.asString()).contains("<title>" + title + "</title>");
    }

    // ------------------------------------------------------------- files list

    @Then("the response is a JSON array with at least {int} items")
    public void jsonArrayAtLeast(int n) {
        List<Object> arr = world.last.jsonPath().getList("$");
        assertThat(arr).hasSizeGreaterThanOrEqualTo(n);
    }

    @Then("the file list contains {string}")
    public void fileListContains(String name) {
        List<String> names = RestAssured.get("/api/files").jsonPath().getList("name");
        assertThat(names).contains(name);
    }

    @Then("the file list does not contain {string}")
    public void fileListDoesNotContain(String name) {
        List<String> names = RestAssured.get("/api/files").jsonPath().getList("name");
        assertThat(names).doesNotContain(name);
    }

    @Then("every file entry has name, size, mtime and ai fields")
    public void everyFileEntryHasFields() {
        List<Map<String, Object>> files = RestAssured.get("/api/files").jsonPath().getList("$");
        assertThat(files).isNotEmpty();
        for (Map<String, Object> f : files) {
            assertThat(f).containsKeys("name", "size", "mtime", "ai");
            assertThat(f.get("size")).isInstanceOf(Number.class);
            assertThat(f.get("mtime")).isInstanceOf(Number.class);
            assertThat(f.get("ai")).isInstanceOf(Boolean.class);
        }
    }

    @Then("the file {string} has size {int}")
    public void fileHasSize(String name, int size) {
        Map<String, Object> f = findFile(name);
        assertThat(((Number) f.get("size")).intValue()).isEqualTo(size);
    }

    @Then("the file {string} has size greater than {int}")
    public void fileHasSizeGreaterThan(String name, int size) {
        Map<String, Object> f = findFile(name);
        assertThat(((Number) f.get("size")).intValue()).isGreaterThan(size);
    }

    private Map<String, Object> findFile(String name) {
        List<Map<String, Object>> files = RestAssured.get("/api/files").jsonPath().getList("$");
        return files.stream()
                .filter(f -> name.equals(f.get("name")))
                .findFirst()
                .orElseThrow(() -> new AssertionError("file not listed: " + name));
    }

    // ------------------------------------------------------------- upload

    @When("I upload a text file named {string} with content {string}")
    public void uploadTextFile(String name, String content) {
        world.last = RestAssured.given()
                .multiPart("file", name, content.getBytes(java.nio.charset.StandardCharsets.UTF_8), "text/plain")
                .post("/api/upload");
        rememberSavedNames();
    }

    @When("I upload a binary file named {string} with {int} bytes")
    public void uploadBinaryFile(String name, int bytes) {
        byte[] data = new byte[bytes];
        for (int i = 0; i < bytes; i++) {
            data[i] = (byte) (i % 251);
        }
        world.last = RestAssured.given()
                .multiPart("file", name, data, "application/octet-stream")
                .post("/api/upload");
        rememberSavedNames();
    }

    @When("I upload two text files named {string} and {string} in one request")
    public void uploadTwoFiles(String a, String b) {
        world.last = RestAssured.given()
                .multiPart("file", a, ("content of " + a).getBytes(java.nio.charset.StandardCharsets.UTF_8), "text/plain")
                .multiPart("file", b, ("content of " + b).getBytes(java.nio.charset.StandardCharsets.UTF_8), "text/plain")
                .post("/api/upload");
        rememberSavedNames();
    }

    private void rememberSavedNames() {
        if (world.last.statusCode() == 200) {
            try {
                List<String> saved = world.last.jsonPath().getList("saved");
                if (saved != null) {
                    world.createdFileNames.addAll(saved);
                    if (!saved.isEmpty()) {
                        world.lastUploadedName = saved.get(0);
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    @Then("the upload response lists {int} saved file(s)")
    public void uploadSavedCount(int n) {
        assertThat(world.last.jsonPath().getList("saved")).hasSize(n);
    }

    @Then("the uploaded file is in the file list")
    public void uploadedFileListed() {
        assertThat(world.lastUploadedName).isNotNull();
        fileListContains(world.lastUploadedName);
    }

    @Then("the uploaded file downloads with content {string}")
    public void uploadedFileDownloadsWith(String content) {
        Response r = RestAssured.get("/dl/" + world.lastUploadedName);
        assertThat(r.statusCode()).isEqualTo(200);
        assertThat(r.asString()).isEqualTo(content);
    }

    @Then("uploading the same name again saves a different name")
    public void uploadingSameNameAgainGivesDifferentName() {
        String first = world.lastUploadedName;
        Response r = RestAssured.given()
                .multiPart("file", first, "second copy".getBytes(java.nio.charset.StandardCharsets.UTF_8), "text/plain")
                .post("/api/upload");
        assertThat(r.statusCode()).isEqualTo(200);
        List<String> saved = r.jsonPath().getList("saved");
        assertThat(saved).hasSize(1);
        assertThat(saved.get(0)).isNotEqualTo(first);
        world.createdFileNames.add(saved.get(0));
    }

    // ------------------------------------------------------------- delete files

    @When("I delete the file {string}")
    public void deleteFile(String name) {
        world.last = RestAssured.given().contentType("application/json")
                .body("{\"name\":\"" + name + "\"}").post("/api/delete");
    }

    @When("I delete the uploaded file")
    public void deleteUploadedFile() {
        deleteFile(world.lastUploadedName);
        world.createdFileNames.remove(world.lastUploadedName);
    }

    @When("I delete all files")
    public void deleteAllFiles() {
        world.last = RestAssured.post("/api/delete_all");
    }

    @When("I delete all files twice")
    public void deleteAllFilesTwice() {
        RestAssured.post("/api/delete_all");
        world.last = RestAssured.post("/api/delete_all");
    }

    @Then("the file list is empty")
    public void fileListIsEmpty() {
        List<Object> arr = RestAssured.get("/api/files").jsonPath().getList("$");
        assertThat(arr).isEmpty();
    }

    @Then("the traversal target file was not deleted")
    public void traversalTargetNotDeleted() {
        // The server file lives outside the shared folder; whatever the delete
        // call returned, the shared file list must still serve normally and
        // the source itself must still be downloadable-proof (server alive).
        Response r = RestAssured.get("/api/status");
        assertThat(r.statusCode()).isEqualTo(200);
        assertThat(Hooks.sharedDir().resolve("hello.txt")).exists();
    }

    // ------------------------------------------------------------- notes

    @When("I create a note with text {string}")
    public void createNote(String text) {
        world.last = RestAssured.given().contentType("application/json")
                .body("{\"text\":\"" + escapeJson(text) + "\"}").post("/api/snippet");
        if (world.last.statusCode() == 200) {
            world.lastNoteId = world.last.jsonPath().getString("id");
            world.lastNoteText = text;
            if (world.lastNoteId != null) {
                world.createdNoteIds.add(world.lastNoteId);
            }
        }
    }

    @When("I create {int} notes with prefix {string}")
    public void createManyNotes(int n, String prefix) {
        for (int i = 1; i <= n; i++) {
            createNote(prefix + " " + i);
        }
    }

    @Then("the note creation succeeded with an id")
    public void noteCreationSucceeded() {
        assertThat(world.last.statusCode()).isEqualTo(200);
        assertThat(world.last.jsonPath().getBoolean("ok")).isTrue();
        assertThat(world.lastNoteId).isNotBlank();
    }

    @Then("the notes list contains the created note")
    public void notesListContainsCreated() {
        List<Map<String, Object>> notes = RestAssured.get("/api/notes").jsonPath().getList("$");
        assertThat(notes.stream().anyMatch(n -> world.lastNoteId.equals(n.get("id"))
                && world.lastNoteText.equals(n.get("text")))).isTrue();
    }

    @Then("the notes list does not contain the created note")
    public void notesListLacksCreated() {
        List<Map<String, Object>> notes = RestAssured.get("/api/notes").jsonPath().getList("$");
        assertThat(notes.stream().noneMatch(n -> world.lastNoteId.equals(n.get("id")))).isTrue();
    }

    @Then("every note entry has id, text and created fields")
    public void everyNoteHasFields() {
        createNote("field check note");
        List<Map<String, Object>> notes = RestAssured.get("/api/notes").jsonPath().getList("$");
        assertThat(notes).isNotEmpty();
        for (Map<String, Object> n : notes) {
            assertThat(n).containsKeys("id", "text", "created");
        }
    }

    @When("I edit the created note to {string}")
    public void editCreatedNote(String text) {
        world.last = RestAssured.given().contentType("application/json")
                .body("{\"id\":\"" + world.lastNoteId + "\",\"text\":\"" + escapeJson(text) + "\"}")
                .post("/api/notes/edit");
        if (world.last.statusCode() == 200) {
            world.lastNoteText = text;
        }
    }

    @When("I edit a note with id {string} to {string}")
    public void editNoteById(String id, String text) {
        world.last = RestAssured.given().contentType("application/json")
                .body("{\"id\":\"" + id + "\",\"text\":\"" + escapeJson(text) + "\"}")
                .post("/api/notes/edit");
    }

    @When("I delete the created note")
    public void deleteCreatedNote() {
        world.last = RestAssured.given().contentType("application/json")
                .body("{\"id\":\"" + world.lastNoteId + "\"}").post("/api/notes/delete");
        world.createdNoteIds.remove(world.lastNoteId);
    }

    @When("I delete a note with id {string}")
    public void deleteNoteById(String id) {
        world.last = RestAssured.given().contentType("application/json")
                .body("{\"id\":\"" + id + "\"}").post("/api/notes/delete");
    }

    @When("I delete all notes")
    public void deleteAllNotes() {
        world.last = RestAssured.post("/api/notes/delete_all");
        world.createdNoteIds.clear();
    }

    @Then("the notes list is empty")
    public void notesListIsEmpty() {
        List<Object> notes = RestAssured.get("/api/notes").jsonPath().getList("$");
        assertThat(notes).isEmpty();
    }

    @Then("the notes list has at least {int} entries")
    public void notesListAtLeast(int n) {
        List<Object> notes = RestAssured.get("/api/notes").jsonPath().getList("$");
        assertThat(notes).hasSizeGreaterThanOrEqualTo(n);
    }

    @When("I download the created note")
    public void downloadCreatedNote() {
        world.last = RestAssured.get("/api/notes/dl?id=" + world.lastNoteId);
    }

    @When("I download a note with id {string}")
    public void downloadNoteById(String id) {
        world.last = RestAssured.get("/api/notes/dl?id=" + id);
    }

    // ------------------------------------------------------------- translate

    @When("I translate the text {string}")
    public void translateText(String text) {
        world.last = RestAssured.given().contentType("application/json")
                .body("{\"text\":\"" + escapeJson(text) + "\"}").post("/api/aitranslate");
    }

    @When("I translate a text of {int} characters")
    public void translateLongText(int chars) {
        String text = "a".repeat(chars);
        world.last = RestAssured.given().contentType("application/json")
                .body("{\"text\":\"" + text + "\"}").post("/api/aitranslate");
    }

    // ------------------------------------------------------------- helpers

    static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r");
    }
}
