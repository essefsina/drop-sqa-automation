Feature: Downloading shared files
  GET /dl/<name> returns the exact bytes of a shared file, with Range support.

  Scenario: 26 - Download returns the exact file content
    When I GET "/dl/hello.txt"
    Then the response status is 200
    And the response body contains "banana keyword search file"

  Scenario: 27 - Downloaded invoice contains its total
    When I GET "/dl/invoice.txt"
    Then the response body contains "Total due: 42 dollars"

  Scenario: 28 - Downloaded markdown contains its heading
    When I GET "/dl/meeting.md"
    Then the response body contains "Meeting Notes"

  Scenario: 29 - Downloaded CSV contains its rows
    When I GET "/dl/data.csv"
    Then the response body contains "Alice"

  Scenario: 30 - Empty file downloads with an empty body
    When I GET "/dl/empty.txt"
    Then the response status is 200
    And the response body is empty

  Scenario: 31 - Missing file returns 404
    When I GET "/dl/does-not-exist.txt"
    Then the response status is 404

  Scenario: 32 - Path traversal cannot escape the shared folder
    When I GET "/dl/..%2Fdropit.py"
    Then the response status is 404

  Scenario: 33 - URL-encoded file name downloads
    When I GET "/dl/hello%2Etxt"
    Then the response status is 200
    And the response body contains "Hello DropIt automation"

  Scenario: 34 - Text download is served as plain text
    When I GET "/dl/hello.txt"
    Then the response content type contains "text/plain"

  Scenario: 35 - Range request returns a partial body
    When I GET "/dl/hello.txt" with range "bytes=0-4"
    Then the response status is 206
    And the response body is exactly "Hello"

  Scenario Outline: 36-37 - Seeded documents download successfully
    When I GET "/dl/<name>"
    Then the response status is 200

    Examples:
      | name        |
      | invoice.txt |
      | meeting.md  |
