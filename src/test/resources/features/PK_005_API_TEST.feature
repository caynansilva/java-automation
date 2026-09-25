@PK_005
Feature: PK_005 - Validate Pokemon types

  Scenario: Validate Bulbasaur types
    Given I request Pokemon "bulbasaur"
    Then the Pokemon should have type "grass"
    And the Pokemon should have type "poison"
