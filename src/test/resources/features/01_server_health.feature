Feature: DropIt server health and shell page
  The server boots, serves its web UI shell and its PWA manifest.

  Scenario: 01 - Homepage loads successfully
    Given the DropIt server is running
    When I GET "/"
    Then the response status is 200

  Scenario: 02 - Homepage has the DropIt title
    When I GET "/"
    Then the homepage title is "DropIt"

  Scenario: 03 - Homepage shows the running version
    When I GET "/"
    Then the response status is 200
    And the response body contains "2.7.71"

  Scenario: 04 - Homepage is served as HTML
    When I GET "/"
    Then the response content type contains "text/html"

  Scenario: 05 - Manifest names the app DropIt
    When I GET "/manifest.json"
    Then the response status is 200
    And the JSON field "name" equals "DropIt"

  Scenario: 06 - Manifest short name is DropIt
    When I GET "/manifest.json"
    Then the JSON field "short_name" equals "DropIt"

  Scenario: 07 - Manifest starts at the root URL
    When I GET "/manifest.json"
    Then the JSON field "start_url" equals "/"

  Scenario: 08 - Manifest uses standalone display
    When I GET "/manifest.json"
    Then the JSON field "display" equals "standalone"

  Scenario: 09 - Status endpoint reports a device count
    When I GET "/api/status"
    Then the response status is 200
    And the JSON field "devices" is a number

  Scenario: 10 - App icon is served as an image
    When I GET "/icon.png"
    Then the response status is 200
    And the response content type contains "image/png"
