Feature: Deleting files
  POST /api/delete and /api/delete_all remove files from the shared folder.

  Scenario: 51 - Deleting an uploaded file succeeds
    When I upload a text file named "qa-del-51.txt" with content "delete me"
    And I delete the uploaded file
    Then the response status is 200
    And the JSON field "ok" is true

  Scenario: 52 - Deleted file leaves the file list
    When I upload a text file named "qa-del-52.txt" with content "delete me too"
    And I delete the uploaded file
    Then the file list does not contain "qa-del-52.txt"

  Scenario: 53 - Deleted file can no longer be downloaded
    When I upload a text file named "qa-del-53.txt" with content "gone soon"
    And I delete the uploaded file
    And I GET "/dl/qa-del-53.txt"
    Then the response status is 404

  Scenario: 54 - Deleting a missing file returns 404
    When I delete the file "qa-never-existed.txt"
    Then the response status is 404
    And the JSON field "ok" is false

  Scenario: 55 - Deleting with an empty name returns 404
    When I delete the file ""
    Then the response status is 404

  Scenario: 56 - Deleting with invalid JSON returns 400
    When I POST raw JSON "{not json" to "/api/delete"
    Then the response status is 400

  Scenario: 57 - Delete cannot traverse outside the shared folder
    When I delete the file "../dropit.py"
    Then the traversal target file was not deleted

  Scenario: 58 - Delete all removes every shared file
    When I upload a text file named "qa-del-58.txt" with content "bulk delete"
    And I delete all files
    Then the response status is 200
    And the file list is empty

  Scenario: 59 - Delete all reports how many files it removed
    When I delete all files
    Then the JSON field "ok" is true
    And the JSON field "deleted" is a number

  Scenario: 60 - Delete all on an empty folder reports zero
    When I delete all files twice
    Then the JSON field "deleted" equals "0"
