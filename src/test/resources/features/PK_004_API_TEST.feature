@PK_004
Feature: PK_004 - Validate Pokemon response contract

  Scenario: Validate Pokemon response contract
    Given I request Pokemon "pikachu"
    Then the Pokemon response contract should be valid
