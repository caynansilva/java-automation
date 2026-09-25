@PK_002
Feature: PK_002 - Get Pokemon by ID

  Scenario: Get Pokemon by ID
    Given I request Pokemon with ID 25
    Then the Pokemon name should be "pikachu"
    And the Pokemon ID should be 25
    And the response status code should be 200
