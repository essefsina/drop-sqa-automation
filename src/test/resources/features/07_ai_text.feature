Feature: AI text extraction and eligibility
  The AI endpoints extract readable text from files and report which files
  carry text, without needing the search model for plain documents.

  Scenario: 76 - File text returns the full text of a text file
    When I GET "/api/aifiletext?name=hello.txt"
    Then the response status is 200
    And the JSON field "ok" is true
    And the response body contains "banana keyword search file"
    And the response body contains "second line of the hello file"

  Scenario: 77 - File text returns the invoice contents
    When I GET "/api/aifiletext?name=invoice.txt"
    Then the JSON field "ok" is true
    And the response body contains "Total due: 42 dollars"

  Scenario: 78 - File text returns markdown contents
    When I GET "/api/aifiletext?name=meeting.md"
    Then the JSON field "ok" is true
    And the response body contains "roadmap, budget and hiring plan"

  Scenario: 79 - File text for a missing file reports not_found
    When I GET "/api/aifiletext?name=missing.txt"
    Then the JSON field "ok" is false
    And the JSON field "message" equals "not_found"

  Scenario: 80 - File text for an empty file reports no_text
    When I GET "/api/aifiletext?name=empty.txt"
    Then the JSON field "ok" is false
    And the JSON field "message" equals "no_text"

  Scenario: 81 - AI check finds text in a text file
    When I GET "/api/aicheck?name=hello.txt"
    Then the JSON field "ok" is true
    And the JSON field "has_text" is true

  Scenario: 82 - AI check finds no text in an empty file
    When I GET "/api/aicheck?name=empty.txt"
    Then the JSON field "has_text" is false

  Scenario: 83 - AI check for a missing file reports no text
    When I GET "/api/aicheck?name=missing.txt"
    Then the JSON field "ok" is false
    And the JSON field "has_text" is false

  Scenario: 84 - File info reports English text for a text file
    When I GET "/api/aifileinfo?name=hello.txt"
    Then the JSON field "ok" is true
    And the JSON field "has_text" is true
    And the JSON field "lang" equals "en"

  Scenario: 85 - File info reports no text for an empty file
    When I GET "/api/aifileinfo?name=empty.txt"
    Then the JSON field "has_text" is false

  Scenario: 86 - File info for a missing file still answers cleanly
    When I GET "/api/aifileinfo?name=missing.txt"
    Then the response status is 200
    And the JSON field "has_text" is false

  Scenario: 87 - OCR endpoint answers with a clean result shape
    When I GET "/api/aiocr?name=hello.txt"
    Then the response status is 200
    And the JSON field "ok" is present
