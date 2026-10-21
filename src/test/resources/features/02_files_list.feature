Feature: Listing shared files
  GET /api/files describes every file in the shared folder.

  Scenario: 11 - File list loads successfully
    Given the DropIt server is running
    When I GET "/api/files"
    Then the response status is 200

  Scenario: 12 - File list is a JSON array
    When I GET "/api/files"
    Then the response is a JSON array with at least 5 items

  Scenario: 13 - Seeded text file is listed
    Then the file list contains "hello.txt"

  Scenario: 14 - Seeded invoice is listed
    Then the file list contains "invoice.txt"

  Scenario: 15 - Seeded markdown file is listed
    Then the file list contains "meeting.md"

  Scenario: 16 - Seeded CSV file is listed
    Then the file list contains "data.csv"

  Scenario: 17 - Every file entry carries name, size, mtime and ai fields
    Then every file entry has name, size, mtime and ai fields

  Scenario: 18 - Empty file reports size zero
    Then the file "empty.txt" has size 0

  Scenario: 19 - Text file reports a positive size
    Then the file "hello.txt" has size greater than 0

  Scenario: 20 - Invoice reports a positive size
    Then the file "invoice.txt" has size greater than 0

  Scenario Outline: 21-25 - Each seeded file is listed
    Then the file list contains "<name>"

    Examples:
      | name        |
      | hello.txt   |
      | invoice.txt |
      | meeting.md  |
      | data.csv    |
      | empty.txt   |
