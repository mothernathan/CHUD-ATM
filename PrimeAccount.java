package com.atmbanksimulator;
// done by tony
public class PrimeAccount extends BankAccount{
    private int overdraftLimit = 0; //put it in negatives

    public PrimeAccount(int overdraftLimit, String accNumber, String accPasswd, int balance) {
        super(accNumber, accPasswd, balance); // inherits BankAccount variables
        this.overdraftLimit = overdraftLimit; // initialises 'overdraftLimit' variable
    }

    public String withdraw(int amount) {
        if ((getBalance() - amount) < overdraftLimit) {
            System.out.println("overdraft limit reached"); // test message
            return "OVERDRAFT"; //This allows the program to go into overdraft
        }
        else {
            super.subtractBalance(amount); // made a new set method to subtract the balance to maintain encapsulation
            return "SUCCESS";
        }
    }
}
