Feature: Library reservations and loans — MédiaCity

  Scenario: Reserve an unavailable book
    Given a book "The Pragmatic Programmer" is currently borrowed
    And a member "Alice" is registered
    When "Alice" reserves "The Pragmatic Programmer"
    Then the reservation for "Alice" on "The Pragmatic Programmer" is confirmed

  Scenario: Multiple reservations on the same book
    Given a book "Clean Code" is currently borrowed
    And a member "Bob" is registered
    And a member "Carol" is registered
    When "Bob" reserves "Clean Code"
    And "Carol" reserves "Clean Code"
    Then there are 2 reservations for "Clean Code"

  Scenario: Return a reserved book makes it available for the next reserver
    Given a member "Dave" is registered
    And a book "Refactoring" exists and is available
    And "Dave" has borrowed "Refactoring"
    And a member "Eve" is registered
    And "Eve" has reserved "Refactoring"
    When "Dave" returns "Refactoring"
    Then "Refactoring" is available again
    And "Eve" is first in the reservation queue for "Refactoring"

  Scenario: Reservation refused for a suspended member
    Given a book "Domain-Driven Design" is currently borrowed
    And a member "Frank" is registered and suspended
    When "Frank" tries to reserve "Domain-Driven Design"
    Then the reservation is rejected because the member is suspended

  Scenario: Late return generates a penalty of 0.15 euro per day
    Given a member "Grace" is registered
    And a book "Design Patterns" exists and is available
    When "Grace" borrows "Design Patterns" on "2026-01-01"
    And "Grace" returns "Design Patterns" on "2026-02-01"
    Then the penalty for "Grace" loan is 1.50 euros
