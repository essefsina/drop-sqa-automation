# DropIt — 100 Automated Use Cases (Catalog)

Every case below is an executable Cucumber scenario in `src/test/resources/features/`.
Scenario Outlines run once per Examples row, and each row is counted as its own use case.

| # | Use case | File |
|---|---|---|
| 01 | Homepage loads successfully | `01_server_health.feature` |
| 02 | Homepage has the DropIt title | `01_server_health.feature` |
| 03 | Homepage shows the running version | `01_server_health.feature` |
| 04 | Homepage is served as HTML | `01_server_health.feature` |
| 05 | Manifest names the app DropIt | `01_server_health.feature` |
| 06 | Manifest short name is DropIt | `01_server_health.feature` |
| 07 | Manifest starts at the root URL | `01_server_health.feature` |
| 08 | Manifest uses standalone display | `01_server_health.feature` |
| 09 | Status endpoint reports a device count | `01_server_health.feature` |
| 10 | App icon is served as an image | `01_server_health.feature` |
| 11 | File list loads successfully | `02_files_list.feature` |
| 12 | File list is a JSON array | `02_files_list.feature` |
| 13 | Seeded text file is listed | `02_files_list.feature` |
| 14 | Seeded invoice is listed | `02_files_list.feature` |
| 15 | Seeded markdown file is listed | `02_files_list.feature` |
| 16 | Seeded CSV file is listed | `02_files_list.feature` |
| 17 | Every file entry carries name, size, mtime and ai fields | `02_files_list.feature` |
| 18 | Empty file reports size zero | `02_files_list.feature` |
| 19 | Text file reports a positive size | `02_files_list.feature` |
| 20 | Invoice reports a positive size | `02_files_list.feature` |
| 21 | Seeded file is listed: hello.txt | `02_files_list.feature` |
| 22 | Seeded file is listed: invoice.txt | `02_files_list.feature` |
| 23 | Seeded file is listed: meeting.md | `02_files_list.feature` |
| 24 | Seeded file is listed: data.csv | `02_files_list.feature` |
| 25 | Seeded file is listed: empty.txt | `02_files_list.feature` |
| 26 | Download returns the exact file content | `03_file_download.feature` |
| 27 | Downloaded invoice contains its total | `03_file_download.feature` |
| 28 | Downloaded markdown contains its heading | `03_file_download.feature` |
| 29 | Downloaded CSV contains its rows | `03_file_download.feature` |
| 30 | Empty file downloads with an empty body | `03_file_download.feature` |
| 31 | Missing file returns 404 | `03_file_download.feature` |
| 32 | Path traversal cannot escape the shared folder | `03_file_download.feature` |
| 33 | URL-encoded file name downloads | `03_file_download.feature` |
| 34 | Text download is served as plain text | `03_file_download.feature` |
| 35 | Range request returns a partial body | `03_file_download.feature` |
| 36 | Seeded document downloads successfully: invoice.txt | `03_file_download.feature` |
| 37 | Seeded document downloads successfully: meeting.md | `03_file_download.feature` |
| 38 | Uploading a text file succeeds | `04_file_upload.feature` |
| 39 | Upload response lists the saved file | `04_file_upload.feature` |
| 40 | Uploaded file appears in the file list | `04_file_upload.feature` |
| 41 | Uploaded file downloads back with the same content | `04_file_upload.feature` |
| 42 | Uploading the same name twice never overwrites | `04_file_upload.feature` |
| 43 | Uploading a CSV file succeeds | `04_file_upload.feature` |
| 44 | Uploading a markdown file succeeds | `04_file_upload.feature` |
| 45 | Uploading a large text file succeeds | `04_file_upload.feature` |
| 46 | Uploading an empty file succeeds | `04_file_upload.feature` |
| 47 | Uploading a file with spaces in its name succeeds | `04_file_upload.feature` |
| 48 | Uploading binary content succeeds | `04_file_upload.feature` |
| 49 | Two files in one upload request are both saved | `04_file_upload.feature` |
| 50 | Upload without multipart data is rejected | `04_file_upload.feature` |
| 51 | Deleting an uploaded file succeeds | `05_file_delete.feature` |
| 52 | Deleted file leaves the file list | `05_file_delete.feature` |
| 53 | Deleted file can no longer be downloaded | `05_file_delete.feature` |
| 54 | Deleting a missing file returns 404 | `05_file_delete.feature` |
| 55 | Deleting with an empty name returns 404 | `05_file_delete.feature` |
| 56 | Deleting with invalid JSON returns 400 | `05_file_delete.feature` |
| 57 | Delete cannot traverse outside the shared folder | `05_file_delete.feature` |
| 58 | Delete all removes every shared file | `05_file_delete.feature` |
| 59 | Delete all reports how many files it removed | `05_file_delete.feature` |
| 60 | Delete all on an empty folder reports zero | `05_file_delete.feature` |
| 61 | Creating a note succeeds | `06_notes.feature` |
| 62 | Created note appears in the notes list | `06_notes.feature` |
| 63 | Two created notes are both listed | `06_notes.feature` |
| 64 | Note entries carry id, text and created fields | `06_notes.feature` |
| 65 | Editing a note updates its text | `06_notes.feature` |
| 66 | Editing a missing note returns 404 | `06_notes.feature` |
| 67 | Editing a note to empty text returns 404 | `06_notes.feature` |
| 68 | Deleting a note succeeds | `06_notes.feature` |
| 69 | Deleted note leaves the notes list | `06_notes.feature` |
| 70 | Deleting a missing note returns 404 | `06_notes.feature` |
| 71 | Downloading a note returns its text | `06_notes.feature` |
| 72 | Downloading a missing note returns 404 | `06_notes.feature` |
| 73 | Creating an empty note is rejected | `06_notes.feature` |
| 74 | Creating a whitespace-only note is rejected | `06_notes.feature` |
| 75 | Deleting all notes empties the list | `06_notes.feature` |
| 76 | File text returns the full text of a text file | `07_ai_text.feature` |
| 77 | File text returns the invoice contents | `07_ai_text.feature` |
| 78 | File text returns markdown contents | `07_ai_text.feature` |
| 79 | File text for a missing file reports not_found | `07_ai_text.feature` |
| 80 | File text for an empty file reports no_text | `07_ai_text.feature` |
| 81 | AI check finds text in a text file | `07_ai_text.feature` |
| 82 | AI check finds no text in an empty file | `07_ai_text.feature` |
| 83 | AI check for a missing file reports no text | `07_ai_text.feature` |
| 84 | File info reports English text for a text file | `07_ai_text.feature` |
| 85 | File info reports no text for an empty file | `07_ai_text.feature` |
| 86 | File info for a missing file still answers cleanly | `07_ai_text.feature` |
| 87 | OCR endpoint answers with a clean result shape | `07_ai_text.feature` |
| 88 | AI status reports ok | `08_ai_search.feature` |
| 89 | AI status reports index counters | `08_ai_search.feature` |
| 90 | AI status counts the seeded files | `08_ai_search.feature` |
| 91 | AI status reports whether the model is ready | `08_ai_search.feature` |
| 92 | AI search answers with a results list | `08_ai_search.feature` |
| 93 | AI search with an empty query answers cleanly | `08_ai_search.feature` |
| 94 | AI search with a nonsense query answers cleanly | `08_ai_search.feature` |
| 95 | English text is reported as already English | `09_translate.feature` |
| 96 | English text is returned unchanged | `09_translate.feature` |
| 97 | Translating empty text is rejected | `09_translate.feature` |
| 98 | Translating whitespace-only text is rejected | `09_translate.feature` |
| 99 | Translating with no text field is rejected | `09_translate.feature` |
| 100 | Translating an over-long text is rejected | `09_translate.feature` |
