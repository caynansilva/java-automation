@PK_007
Feature: PK_007 - Validate HTTP metadata and content type

  Scenario: Validate response metadata
    Given I request Pokemon "pikachu"
    Then the response status code should be 200
    And the response should have JSON content type
