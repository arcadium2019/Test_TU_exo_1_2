package org.example.exo13.model;

import java.util.UUID;

public class BankAccount {
    private final String number;
    private final String holder;
    private double balance;

    public BankAccount(String holder) {
        this.number = UUID.randomUUID().toString();
        this.holder = holder;
        this.balance = 0.0;
    }

    public BankAccount(String number, String holder) {
        this.number = number;
        this.holder = holder;
        this.balance = 0.0;
    }

    public String getNumber()  { return number; }
    public String getHolder()  { return holder; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
}
