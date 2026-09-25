@PK_003
Feature: PK_003 - Unknown Pokemon returns 404

  Scenario: Unknown Pokemon returns 404
    Given I request Pokemon "pokemon-that-does-not-exist-automation-xyz"
    Then the response status code should be 404
