package org.example;

import java.time.LocalDateTime;
import java.util.Date;

public class Transaction {

    private Date date;
    private double amount;

    public Transaction(double amount, LocalDateTime now) {
        this.date = new Date();
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }
}
