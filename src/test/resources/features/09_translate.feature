Feature: Local translation endpoint
  POST /api/aitranslate translates foreign text to English fully on-device.
  English input short-circuits as already_english; bad input is rejected.

  Scenario: 95 - English text is reported as already English
    When I translate the text "the quick brown fox jumps over the lazy dog and runs through the forest"
    Then the response status is 200
    And the JSON field "ok" is true
    And the JSON field "src" equals "en"
    And the JSON field "message" equals "already_english"

  Scenario: 96 - English text is returned unchanged
    When I translate the text "hello world this is a simple english sentence for the test"
    Then the JSON field "text" equals "hello world this is a simple english sentence for the test"

  Scenario: 97 - Translating empty text is rejected
    When I translate the text ""
    Then the response status is 400
    And the JSON field "message" equals "empty"

  Scenario: 98 - Translating whitespace-only text is rejected
    When I translate the text "   "
    Then the response status is 400

  Scenario: 99 - Translating with no text field is rejected
    When I POST raw JSON "{}" to "/api/aitranslate"
    Then the response status is 400
    And the JSON field "message" equals "empty"

  Scenario: 100 - Translating an over-long text is rejected
    When I translate a text of 20001 characters
    Then the response status is 400
    And the JSON field "message" equals "too_long"
