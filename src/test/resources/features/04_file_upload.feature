Feature: Uploading files
  POST /api/upload stores files in the shared folder via multipart form data.

  Scenario: 38 - Uploading a text file succeeds
    When I upload a text file named "qa-upload-38.txt" with content "first automated upload"
    Then the response status is 200
    And the JSON field "ok" is true

  Scenario: 39 - Upload response lists the saved file
    When I upload a text file named "qa-upload-39.txt" with content "saved list check"
    Then the upload response lists 1 saved files

  Scenario: 40 - Uploaded file appears in the file list
    When I upload a text file named "qa-upload-40.txt" with content "listed after upload"
    Then the uploaded file is in the file list

  Scenario: 41 - Uploaded file downloads back with the same content
    When I upload a text file named "qa-upload-41.txt" with content "round trip content 123"
    Then the uploaded file downloads with content "round trip content 123"

  Scenario: 42 - Uploading the same name twice never overwrites
    When I upload a text file named "qa-dupe.txt" with content "first copy"
    Then uploading the same name again saves a different name

  Scenario: 43 - Uploading a CSV file succeeds
    When I upload a text file named "qa-data.csv" with content "a,b,c\n1,2,3"
    Then the response status is 200
    And the uploaded file is in the file list

  Scenario: 44 - Uploading a markdown file succeeds
    When I upload a text file named "qa-notes.md" with content "# QA Notes"
    Then the response status is 200
    And the uploaded file is in the file list

  Scenario: 45 - Uploading a large text file succeeds
    When I upload a binary file named "qa-large.txt" with 50000 bytes
    Then the response status is 200
    And the uploaded file is in the file list

  Scenario: 46 - Uploading an empty file succeeds
    When I upload a text file named "qa-empty.txt" with content ""
    Then the response status is 200
    And the uploaded file is in the file list

  Scenario: 47 - Uploading a file with spaces in its name succeeds
    When I upload a text file named "qa my file.txt" with content "spacey name"
    Then the response status is 200
    And the uploaded file is in the file list

  Scenario: 48 - Uploading binary content succeeds
    When I upload a binary file named "qa-binary.bin" with 1024 bytes
    Then the response status is 200
    And the uploaded file is in the file list

  Scenario: 49 - Two files in one upload request are both saved
    When I upload two text files named "qa-multi-a.txt" and "qa-multi-b.txt" in one request
    Then the response status is 200
    And the upload response lists 2 saved files

  Scenario: 50 - Upload without multipart data is rejected
    When I POST plain text "not a file" to "/api/upload"
    Then the response status is 404
