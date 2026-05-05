package com.atmbanksimulator;
public class StudentAccount extends BankAccount {
    private int withdrawLimit = 0;
 // done by toby

    // Contructor method for the Student account sub-class
    // Takes the attributes from BankAccount super class
    // New attribute 'withdrawLimit' is unique to the student account
    public StudentAccount(int withdrawLimit, String accNumber, String accPasswd , int balance) {
        super(accNumber, accPasswd, balance); // inherits super class attributes
        this.withdrawLimit = withdrawLimit; // initialises withdrawLimit
    }

    // Withdraw money from this account.
    // Returns true if successful, or false if the amount is negative or exceeds the current balance.
    public String withdraw( int amount ) {
        if (amount < 0 || getBalance() < amount) {
            System.out.println("Student fail"); // test message
            return "INSUFFICIENT";
        }
        if (amount > withdrawLimit) {
            System.out.println("Cannot withdraw more than its limit"); // test message
            return "LIMIT";
        }
        else {
            super.subtractBalance(amount); // made a new set method to subtract the balance to maintain encapsulation
            withdrawLimit = withdrawLimit - amount; // Withdraw limit will be subtracted by balance till it reaches 0
            System.out.println(withdrawLimit); // Test message
            return "SUCCESS";
        }
    }

}
