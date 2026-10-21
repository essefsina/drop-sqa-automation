Feature: AI search and index status
  Ask/AI search endpoints answer with a stable result shape whether or not
  the local model stack is fully set up on the machine.

  Scenario: 88 - AI status reports ok
    When I GET "/api/aistatus"
    Then the response status is 200
    And the JSON field "ok" is true

  Scenario: 89 - AI status reports index counters
    When I GET "/api/aistatus"
    Then the JSON field "indexed" is a number
    And the JSON field "files" is a number
    And the JSON field "pending" is a number

  Scenario: 90 - AI status counts the seeded files
    When I GET "/api/aistatus"
    Then the JSON field "files" equals "5"

  Scenario: 91 - AI status reports whether the model is ready
    When I GET "/api/aistatus"
    Then the JSON field "model_ready" is present

  Scenario: 92 - AI search answers with a results list
    When I GET "/api/aisearch?q=banana"
    Then the response status is 200
    And the JSON field "results" is a list

  Scenario: 93 - AI search with an empty query answers cleanly
    When I GET "/api/aisearch?q="
    Then the response status is 200
    And the JSON field "results" is a list

  Scenario: 94 - AI search with a nonsense query answers cleanly
    When I GET "/api/aisearch?q=zzqqxxyynomatch"
    Then the response status is 200
    And the JSON field "results" is a list
