package com.atmbanksimulator;
import java.io.File;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

// ===== 📚🌐BankAccount (Domain / Service / Business Logic) =====

// BankAccount class:
// - Stores instance variables for account number, password, and balance
// - Provides methods to withdraw, deposit, check balance, etc.
public class BankAccount {
    private String accNumber = "";
    private String accPasswd ="";
    private int balance;

    public BankAccount() {}
    public BankAccount(String a, String p, int b) {
        accNumber = a;
        accPasswd = p;
        balance = b;
    }

    // Withdraw money from this account.
    // Returns true if successful, or false if the amount is negative or exceeds the current balance.
    // CHANGE: withdraw() is now a string so it can be easier to communicate with the UI model
    // by allowing more ways to return the function, instead of 'True' or 'False'
    // This allows to make the UI to display other error messages
    public String withdraw( int amount ) {
        if (amount < 0 || balance < amount) {
            return "INSUFFICIENT";
        } else {
            balance = balance - amount;// subtract amount from balance
            recordTransaction("Withdraw", amount);
            return "SUCCESS";
        }
    }

    // deposit the amount of money into this account.
    // Return true if successful,or false if the amount is negative
    public boolean deposit( int amount ) {
        if (amount < 0) {
            return false;
        } else {
            balance = balance + amount;  // add amount to balance
            recordTransaction("Deposit", amount);
            return true;
        }
    }

    // Getter for the account balance
    // Returns the current balance of this account
    public int getBalance() {
        return balance;
    }

    // Getter for the account number
    public String getAccNumber() {
        return accNumber;
    }
    // Getter for the account password
    public String getaccPasswd() {
        return accPasswd;
    }

    // addition by nathan: This maintains encapsulation, and prevents the need for 'balance' to be a public variable
    public void subtractBalance(int amount) {
        balance = balance - amount;
    }

    public void addBalance (int amount) {
        balance = balance + amount;
    }

    //Set method to change account password
    public void setAccPasswd(String newPasswd) {
        accPasswd=newPasswd;
        System.out.println("Account password has been changed");
    }

    //Method to write statements to a file
    //creates a text file if it doesn't exist yet
    private ArrayList<Transaction> transactions = new ArrayList<>(); //tony
    public void recordTransaction(String type, int amount) {
        transactions.add(new Transaction(type, amount, balance));
        if (transactions.size() > 10) {
            transactions.remove(0);
        }
        System.out.println("Writing to: " + new File("Transactions/ReceiptAccount_" + accNumber + ".txt").getAbsolutePath());
        try (FileWriter fw = new FileWriter("Transactions/ReceiptAccount_" + accNumber + ".txt", true)) {
            fw.write(transactions.get(transactions.size() - 1).toFileString() + "\n");
        } catch (IOException e) {
            System.out.println("Error saving transaction: " + e.getMessage());
        }
    }

    public void loadTransactions() { //tony
        transactions.clear();
        try (BufferedReader br = new BufferedReader(new FileReader("Transactions/ReceiptAccount_" + accNumber + ".txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                transactions.add(Transaction.fromFileString(line));
            }
        } catch (IOException e) {
            System.out.println("No transaction history found for account: " + accNumber);
        }
    }

    public String getMiniStatement() { //tony
        if (transactions.isEmpty()) {
            return "No transactions yet.";
        }
        StringBuilder sb = new StringBuilder("--- Mini Statement ---\n");
        for (int i = transactions.size() - 1; i >= 0; i--) {
            sb.append(transactions.get(i).toString()).append("\n");
        }
        return sb.toString();
    }

    public String transferOut(int amount) {
        if (balance < amount) return "INSUFFICIENT";
        balance -= amount;
        recordTransaction("Transfer Out", amount);
        return "SUCCESS";
    }

    public void transferIn(int amount) {
        balance += amount;
        recordTransaction("Transfer In", amount);
    }
}

