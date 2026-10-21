Feature: Shared text notes
  Notes are created, edited, downloaded and deleted through the notes API.

  Scenario: 61 - Creating a note succeeds
    When I create a note with text "automation note sixty one"
    Then the note creation succeeded with an id

  Scenario: 62 - Created note appears in the notes list
    When I create a note with text "listed note"
    Then the notes list contains the created note

  Scenario: 63 - Two created notes are both listed
    When I create a note with text "first of two"
    And I create a note with text "second of two"
    Then the notes list has at least 2 entries

  Scenario: 64 - Note entries carry id, text and created fields
    Then every note entry has id, text and created fields

  Scenario: 65 - Editing a note updates its text
    When I create a note with text "before edit"
    And I edit the created note to "after edit"
    Then the response status is 200
    And the notes list contains the created note

  Scenario: 66 - Editing a missing note returns 404
    When I edit a note with id "n-does-not-exist" to "nobody home"
    Then the response status is 404

  Scenario: 67 - Editing a note to empty text returns 404
    When I create a note with text "will try to blank me"
    And I edit the created note to ""
    Then the response status is 404

  Scenario: 68 - Deleting a note succeeds
    When I create a note with text "delete this note"
    And I delete the created note
    Then the response status is 200
    And the JSON field "ok" is true

  Scenario: 69 - Deleted note leaves the notes list
    When I create a note with text "short lived note"
    And I delete the created note
    Then the notes list does not contain the created note

  Scenario: 70 - Deleting a missing note returns 404
    When I delete a note with id "n-does-not-exist"
    Then the response status is 404

  Scenario: 71 - Downloading a note returns its text
    When I create a note with text "downloadable note body"
    And I download the created note
    Then the response status is 200
    And the response body contains "downloadable note body"

  Scenario: 72 - Downloading a missing note returns 404
    When I download a note with id "n-does-not-exist"
    Then the response status is 404

  Scenario: 73 - Creating an empty note is rejected
    When I POST raw JSON "{\"text\":\"\"}" to "/api/snippet"
    Then the response status is 400

  Scenario: 74 - Creating a whitespace-only note is rejected
    When I POST raw JSON "{\"text\":\"   \"}" to "/api/snippet"
    Then the response status is 400

  Scenario: 75 - Deleting all notes empties the list
    When I create 2 notes with prefix "bulk note"
    And I delete all notes
    Then the response status is 200
    And the notes list is empty
