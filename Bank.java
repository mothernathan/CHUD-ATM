package com.atmbanksimulator;

// ===== 📚🌐Bank (Domain / Service / Business Logic) =====

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

// Bank class: a simple implementation of a bank, containing a list of bank accounts
// and has a currently logged-in account (loggedInAccount).
public class Bank {

    // Instance variables storing bank information
    private int maxAccounts = 10;                       // Maximum number of accounts the bank can hold
    private int numAccounts = 0;                        // Current number of accounts in the bank
    private BankAccount[] accounts = new BankAccount[maxAccounts];  // Array to hold BankAccount objects
    private BankAccount loggedInAccount = null;         // Currently logged-in account ('null' if no one is logged in)


    //Bao doing the 3 attempts ting
    private int loginAttempts = 0;
    private boolean accountLocked = false;

    //Bao's getters <3
    public boolean isLocked() {
        return accountLocked;
    }
    public int getLoginAttempts() {
        return loginAttempts;
    }

    // a method to create new BankAccount - this is known as a 'factory method' and is a more
    // flexible way to do it than just using the 'new' keyword directly.
    public BankAccount makeBankAccount(String accNumber, String accPasswd, int balance) {
        return new BankAccount(accNumber, accPasswd, balance);
    }

    // a method to create a new student account
    public BankAccount makeStudentAccount(int withdrawLimit, String accNumber, String accPasswd, int balance) {
        return new StudentAccount(withdrawLimit, accNumber, accPasswd, balance);
    }

    public BankAccount makePrimeAccount(int overdraftLimit, String accNumber, String accPasswd, int balance) {
        return new PrimeAccount(overdraftLimit, accNumber, accPasswd, balance);
    }

    public BankAccount makeSavingsAccount(double interestRate, String accNumber, String accPasswd, int balance) {
        return new SavingsAccount(interestRate, accNumber, accPasswd, balance);
    }

    // a method to add a new bank account to the bank - it returns true if it succeeds
    // or false if it fails (because the bank is 'full')
    public boolean addBankAccount(BankAccount a) {
        if (numAccounts < maxAccounts) {
            accounts[numAccounts] = a;
            numAccounts++ ;
            return true;
        } else {
            return false;
        }
    }

    // Variant of addBankAccount: creates a BankAccount and adds it in one step.
    // This is an example of method overloading: two methods can share the same name
    // if they have different parameter lists.
    public boolean addBankAccount(String accNumber, String accPasswd, int balance) {
        return addBankAccount(makeBankAccount(accNumber, accPasswd, balance));
    }

    //Method to add student account as an object in Main.java
    public boolean addStudentAccount(int withdrawLimit, String accNumber, String accPasswd, int balance) {
        return addBankAccount(makeStudentAccount(withdrawLimit, accNumber, accPasswd, balance));
    }

    //Method to add prime account as an object
    public boolean addPrimeAccount(int overdraftLimit, String accNumber, String accPasswd, int balance) {
        return addBankAccount(makePrimeAccount(overdraftLimit, accNumber, accPasswd, balance));
    }

    //Method to add prime account as an object
    public boolean addSavingsAccount(double interestRate, String accNumber, String accPasswd, int balance) {
        return addBankAccount(makeSavingsAccount(interestRate, accNumber, accPasswd, balance));
    }

    // Check whether the given accountNumber and password match an existing BankAccount.
    // If successful, set 'loggedInAccount' to that account and return true.
    // Otherwise, set 'loggedInAccount' to null and return false.
    public boolean login(String accountNumber, String password) {
        // bao here again hi
        if (accountLocked) {
            return false;
        }

        logout(); // logout of any previous loggedInAccount

        // Search the accounts array to find a BankAccount with a matching accountNumber and password.
        // - If found, set 'loggedInAccount' to that account and return true.
        // - If not found, reset 'loggedInAccount' to null and return false.
        for (BankAccount b: accounts) {
            if (b!=null && b.getAccNumber().equals(accountNumber) && b.getaccPasswd().equals(password)) {
                // found the right account
                // b!=null will disregard the empty spaces in the bank accounts array
                //This is because bank accounts array has 10 spaces,
                // and 5 has been made prior
                loggedInAccount = b;
                loggedInAccount.loadTransactions();
                loginAttempts = 0;
                return true;
            }
        }
        loginAttempts++;
        if (loginAttempts >= 3) {
            accountLocked = true;
        }
        return false;
    }

    // Log out of the currently logged-in account, if any
    public void logout() {
        if (loggedIn()) {
            loggedInAccount = null;
        }
    }

    // Check whether the bank currently has a logged-in account
    public boolean loggedIn() {
        if (loggedInAccount == null) {
            return false;
        } else {
            return true;
        }
    }

    // Attempt to deposit money into the currently logged-in account
    // by calling the deposit method of the BankAccount object
    public boolean deposit(int amount)
    {
        if (loggedIn()) {
            return loggedInAccount.deposit(amount);
        } else {
            return false;
        }
    }


    // Attempt to withdraw money from the currently logged-in account
    // by calling the withdraw method of the BankAccount object
    public String withdraw(int amount)
    {
        if (loggedIn()) {
            return loggedInAccount.withdraw(amount);
        } else {
            return "FALSE";
        }
    }

    // get the currently logged-in account balance
    // by calling the getBalance method of the BankAccount object
    public int getBalance()
    {
        if (loggedIn()) {
            return loggedInAccount.getBalance();
        } else {
            return -1; // use -1 as an indicator of an error
        }
    }

    //Checks for account number duplicates
    public boolean duplicatorChecker(String accNumber) {
        for (BankAccount b : accounts) {
            if (b != null && b.getAccNumber().equals(accNumber)) {
                return true;
                // if b exists and has the same number as the account its currently checking
                // then it is a duplicate, return true
            }
        }
        return false;
    }

    //Takes the current logged in account and runs the set method
    //to change the password
    public void changePassword(String password) {
        loggedInAccount.setAccPasswd(password);
    }

    public boolean transfer(String accountnumber,int amount){//Written by Toby
        if (!loggedIn()){
            return false;

        }
        if (accountnumber.equals(loggedInAccount.getAccNumber())) { //
            return false;
        }
        BankAccount recepient = null;
        for (BankAccount b : accounts) {
            if (b != null && b.getAccNumber().equals(accountnumber)) {
                recepient = b;
                break;
            }

            }
        if (recepient != null){
            String result = loggedInAccount.transferOut(amount);
            if (result.equals("SUCCESS")) {
                recepient.transferIn(amount);
                return true;
            }

        }
                return false;

    }
    public BankAccount getLoggedInAccount()
    {
        if (loggedIn()) {
            return loggedInAccount;
        } else {
            return null ;
        }
    }
    public String getMiniStatement() {
        if (loggedIn()) {
            return loggedInAccount.getMiniStatement();
        }
        return "Not logged in.";
    }

    //saves accounts to a text file
    public void saveAccounts() {
        try (FileWriter fw = new FileWriter("accounts.txt")) {
            for (int i = 0; i < numAccounts; i++) {
                BankAccount b = accounts[i];
                fw.write(b.getAccNumber() + "," + b.getaccPasswd() + "," + b.getBalance() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving accounts: " + e.getMessage());
        }
    }

    // reads file and restores the state of accounts
    public void loadAccounts() {
        try (BufferedReader br = new BufferedReader(new FileReader("accounts.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                addBankAccount(parts[0], parts[1], Integer.parseInt(parts[2]));
            }
        } catch (IOException e) {
            System.out.println("No saved accounts found, using defaults.");
        }
    }

    //get method to retrieve number of accounts
    public int getNumAccounts() {
        return numAccounts;
    }
}



