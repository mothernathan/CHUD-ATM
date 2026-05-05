package com.atmbanksimulator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Transaction {
    private String type;        // "Deposit", "Withdraw", "Transfer Out", "Transfer In"
    private int amount;
    private int balanceAfter;
    private String timestamp;

    public Transaction(String type, int amount, int balanceAfter) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;

        this.timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    @Override
    public String toString() {
        return timestamp + "  |  " + type + "  £" + amount + "  |  Bal: £" + balanceAfter;
    }
    public String toFileString() {
        return timestamp + "," + type + "," + amount + "," + balanceAfter;
    }
    public static Transaction fromFileString(String line) {
        String[] parts = line.split(",");
        Transaction t = new Transaction(parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
        t.timestamp = parts[0];
        return t;
    }

}