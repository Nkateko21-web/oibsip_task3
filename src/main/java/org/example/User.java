package org.example;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class User {
    private String userId;
    private String pin;
    private double balance;
    private ArrayList<Transaction> transactionHistory;

    public User(String userId, String pin) {
        this.userId = userId;
        this.pin = pin;
        this.transactionHistory = new ArrayList<>();
    }

    public String getUserId() {
        return userId;
    }

    public String getPin() {
        return pin;
    }

    public double getBalance() {
        return  balance;
    }

    public ArrayList<Transaction> getTransactionHistory() {
        return transactionHistory;
    }

    public void deposit(double amount) {
        this.balance += amount;
        transactionHistory.add(new Transaction(amount, LocalDateTime.now()));
    }

    public void withdraw(double amount) {
        if (this.balance >= amount) {
            this.balance -= amount;
            transactionHistory.add(new Transaction(-amount, LocalDateTime.now()));
        } else {
            System.out.println("Insufficient funds");
        }
    }

    public void transfer(User recipient, double amount) {
        if (balance >= amount) {
            this.balance -= amount;
            recipient.deposit(amount);

            recipient.getTransactionHistory().add(new Transaction(amount, LocalDateTime.now()));
            transactionHistory.add(new Transaction(-amount, LocalDateTime.now()));
        } else {
            System.out.println("Insufficient funds");
        }
    }
}
