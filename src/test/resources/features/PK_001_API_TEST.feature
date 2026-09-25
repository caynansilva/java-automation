@PK_001
Feature: PK_001 - Get Pokemon by name

  Scenario: Get Pokemon by name
    Given I request Pokemon "pikachu"
    Then the Pokemon name should contain "pikachu"
    And the response status code should be 200
