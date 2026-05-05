package com.atmbanksimulator;

public class SavingsAccount extends BankAccount {
    private double interestRate = 0;

    public SavingsAccount(double interestRate, String accNumber, String accPasswd, int balance) {
        super(accNumber, accPasswd, balance); // inherits BankAccount variables
        this.interestRate = interestRate; // initialises 'interestRate' variable
    }

    // Usual withdrawal method
    public String withdraw(int amount) {
        if (amount < 0 || getBalance() < amount) {
            System.out.println("Student fail"); // test message
            return "INSUFFICIENT";
        }
        else {
            super.subtractBalance(amount); // made a new set method to subtract the balance to maintain encapsulation
            return "SUCCESS";
        }
    }

    // deposit the amount of money into this account.
    // Return true if successful,or false if the amount is negative
    public boolean deposit( int amount ) {
        if (amount < 0) {
            return false;
        }
        else {
            super.addBalance(amount);  // new method to add amount to balance
            addInterest(); // adds the interest
            return true;
        }
    }
    // FOR NOW: Adds interest for each deposit
    public void addInterest() {
        double interest = getBalance() * interestRate; // calculates current gained
        System.out.println("Interest added " + interest); // test message
        super.addBalance((int) Math.round(interest)); // converts double to int
    }
}
