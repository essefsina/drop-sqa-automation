# DropIt SQA Automation

**100 automated use cases** for [DropIt](https://github.com/essefsina/DropIt) — the WiFi phone↔PC file-transfer app — written in **Java 17** with **Cucumber (Gherkin)** and **RestAssured**, run with JUnit 5 / Maven.

Nothing is mocked. The suite boots a **real DropIt server** (`dropit.py`) on a test port against a throwaway shared folder seeded with known files, then drives it over HTTP exactly the way the phone browser, desktop app and Android app do.

## What is covered (100 use cases)

| Feature file | Area | Cases |
|---|---|---|
| `01_server_health.feature` | Homepage, version, PWA manifest, status, icon | 10 |
| `02_files_list.feature` | `GET /api/files` listing, fields, sizes | 15 |
| `03_file_download.feature` | `/dl/<name>` content, 404s, traversal guard, HTTP Range | 12 |
| `04_file_upload.feature` | Multipart upload, duplicates, binary, multi-file | 13 |
| `05_file_delete.feature` | Delete, delete-all, bad input, traversal guard | 10 |
| `06_notes.feature` | Note create / edit / download / delete lifecycle | 15 |
| `07_ai_text.feature` | `/api/aifiletext`, `/api/aicheck`, `/api/aifileinfo`, `/api/aiocr` | 12 |
| `08_ai_search.feature` | `/api/aisearch` + `/api/aistatus` contracts | 7 |
| `09_translate.feature` | `/api/aitranslate` English short-circuit + validation | 6 |
| **Total** | | **100** |

The full numbered catalog is in [USE_CASES.md](USE_CASES.md).

## Design notes

- **Isolated**: a fresh shared folder per run (`target/test-shared`), re-seeded before every scenario, so destructive cases (`delete_all`) cannot break later ones.
- **Self-cleaning**: an `@After` hook deletes every note/file a scenario created, even on failure.
- **Safe**: the user's real notes store is backed up before the suite and restored after it.
- **Honest AI tests**: search/translate assertions target the endpoints' *contracts* (status, JSON shape, validation, English detection), which hold whether or not the optional ONNX model stack is installed on the test machine — no fake-green assertions on model output.

## Run it

Prerequisites: JDK 17+, Maven 3.9+, Python 3, and a DropIt source tree.

```bash
# Option A — DropIt checked out beside this repo (../dropit/dropit.py) — just run:
mvn test

# Option B — point at any DropIt source:
DROPIT_PY=/path/to/DropIt/src/dropit.py mvn test
# or
mvn test -Ddropit.py=/path/to/DropIt/src/dropit.py
```

Reports land in `target/cucumber-report.html` and `target/cucumber.json`; the server's own log is `target/dropit-server.log`.

### GitHub Actions

The workflow in `.github/workflows/test.yml` checks out both this repo and `essefsina/DropIt`, then runs `mvn test` on every push and pull request.

## Layout

```
src/test/java/com/essef/dropit/
  Hooks.java            boots/stops the real server, seeds + restores state
  World.java            per-scenario context (last response, created ids)
  StepDefinitions.java  all Gherkin glue (real HTTP via RestAssured)
  RunCucumberTest.java  JUnit Platform suite entry point
src/test/resources/features/   the 100 use cases in Gherkin
```
