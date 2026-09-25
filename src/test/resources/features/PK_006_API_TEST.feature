@PK_006
Feature: PK_006 - Validate Pokemon abilities

  Scenario: Validate Pikachu abilities
    Given I request Pokemon "pikachu"
    Then the Pokemon should have ability "static"
