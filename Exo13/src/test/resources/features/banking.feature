Feature: Banking operations

  Scenario: Creation of a new account
    When I create an account for "Alice"
    Then the account should exist with a balance of 0

  Scenario: Deposit money into an account
    Given an account for "Bob" with a balance of 100
    When I deposit 50 into the account of "Bob"
    Then the balance of "Bob" should be 150

  Scenario: Successful withdrawal
    Given an account for "Carol" with a balance of 200
    When I withdraw 80 from the account of "Carol"
    Then the balance of "Carol" should be 120

  Scenario: Refused withdrawal due to insufficient funds
    Given an account for "Dave" with a balance of 50
    When I try to withdraw 100 from the account of "Dave"
    Then the withdrawal should be rejected for insufficient funds

  Scenario: Transfer between two accounts
    Given an account for "Eve" with a balance of 300
    And an account for "Frank" with a balance of 100
    When I transfer 150 from "Eve" to "Frank"
    Then the balance of "Eve" should be 150
    And the balance of "Frank" should be 250

  Scenario: Transfer refused due to insufficient funds
    Given an account for "Grace" with a balance of 50
    And an account for "Henry" with a balance of 0
    When I try to transfer 100 from "Grace" to "Henry"
    Then the transfer should be rejected for insufficient funds
