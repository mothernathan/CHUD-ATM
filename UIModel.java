package com.atmbanksimulator;

// ===== 🧠 UIModel (Brain) =====

// The UIModel represents all the actual content and functionality of the app
// For the ATM, it keeps track of the information shown in the display
// (the laMsg and two tfInput boxes), and the interaction with the bank, executes
// commands provided by the controller and tells the view to update when
// something changes
public class UIModel {
    View view; // Reference to the View (part of the MVC setup)
    private Bank bank; // The ATM communicates with this Bank
    soundManager soundManager; //nathan: references the sound manager class file

    // The ATM UIModel can be in one of three states:
    // 1. Waiting for an account number
    // 2. Waiting for a password
    // 3. Logged in (ready to process requests for the logged-in account)
    // We represent each state with a String constant.
    // The 'final' keyword ensures these values cannot be changed.
    private final String STATE_ACCOUNT_NO = "account_no";
    private final String STATE_PASSWORD = "password";
    private final String STATE_LOGGED_IN = "logged_in";
    private final String STATE_GOODBYE = "goodbye"; //bao
    private final String STATE_LOCKED = "locked"; //bao
    private final String STATE_TRANSFER_ACC = "transfer_acc";//Toby
    private final String STATE_TRANSFER_SND = "transfer_amt";//Toby
    // This is for adding new accounts
    // final ensures the string won't be changed and remains a constant
    private final String NEW_ACCOUNT_NO = "new_account_no";
    private final String NEW_PASSWORD = "new_password";
    private final String NEW_BALANCE = "new_balance";
    //NEW_ACCOUNT_TYPE gives the user the choice of account type they want
    //1=basic 2=student 3=prime 4=savings
    private final String NEW_ACCOUNT_TYPE = "new_account_type";

    //This is a state when the user wants to change password
    //User enters a new password
    //User needs to confirm their new password
    private final String CHANGE_PASSWORD_NEW = "change_password_new";
    private final String CHANGE_PASSWORD_CONFIRM = "change_password_confirm";

    private final String CONFIRMATION = "confirm";

    private String changedPasswd = "";
    private String newAccType = "";

    private String transferTargetAcc = "";

    private String newAccNumber = "";
    private String newAccPasswd = "";
    private int newAccBalance = 0;

    // Variables representing the state and data of the ATM UIModel
    private String state = STATE_ACCOUNT_NO;    // Current state of the ATM
    private String accNumber = "";         // Account number being typed
    private String accPasswd = "";         // Password being typed

    // Variables shown on the View display
    private String message;                // Message label text
    private String numberPadInput;         // Current number displayed in the TextField (as a string)
    private String result;                 // Contents of the TextArea (may be multiple lines)

    private String pendingAction = null; // this string determines what type of processes need confirming
    //e.g "LOGOUT" tells pendingConfirmation() that it needs to confirm whether to log out or not

    private String AccountInput;
    // UIModel constructor: pass a Bank object that the ATM interacts with
    public UIModel(Bank bank) {
        this.bank = bank;
    }

    // Initialize the ATM UIModel: this method is called by Main when starting the app
    // - Set state to STATE_ACCOUNT_NO
    // - Clear the numberPadInput - numbers displayed in the TextField
    // - Display the welcome message and user instructions

    //variable that holds the amount put into the numpad
    private int amount = 0;

    //nathan: transfer int
    private int amounttransferred = 0;


    public void initialise() {
        soundManager.playMenuMusic();
        setState(STATE_ACCOUNT_NO);
        numberPadInput = "";
        message = "Welcome to CHUD ATM";
        result = "Enter your account number\nFollowed by \"Ent\"\nTo create a new account, press \"New\"\nWhen you are done, press (L/O = Logout/Return)";
        update();
    }

    private void locked() {
        setState(STATE_LOCKED);
        soundManager.playError();
        numberPadInput ="";
        message = "Account locked";
        result = "Too many failed attempts.\nThis account has been locked.\nPlease log out of this session and contact your bank.";
    }
    // Reset the ATM UIModel after an invalid action or logout:
    // - Set state to STATE_ACCOUNT_NO
    // - Clear the numberPadInput
    // - Display the provided message and user instructions
    private void reset(String msg) {
        setState(STATE_ACCOUNT_NO);
        numberPadInput = "";
        message = msg;
        result = "Enter your account number\nFollowed by \"Ent\"\nTo create a new account, press \"New\"\nWhen you are done, press (L/O = Logout/Return)";
    }

    // Change the ATM state and print a debug message whenever the state changes
    private void setState(String newState)
    {
        if ( !state.equals(newState) )
        {
            String oldState = state;
            state = newState;
            System.out.println("UIModel::setState: changed state from "+ oldState + " to " + newState);
        }
    }

    // These process**** methods are called by the Controller
    // in response to specific button presses on the GUI.

    // Handle a number button press: append the digit to numberPadInput
    public void processNumber(String numberOnButton) {
        // Optional extension:
        // Improve feedback by showing what the number is being entered for based on the current state.
        // e.g.  if state is STATE_ACCOUNT_NO, display "Receiving Account Number, Beep 5 received"
        if (state.equals(STATE_LOCKED)) return; //bao
        numberPadInput += numberOnButton;
        message = "Beep! " + numberOnButton + " received";
        update();
    }

    // Handle the Clear button: reset the current number stored in numberPadInput
    public void processClear() {
        // Optional extension:
        // Improve feedback by showing what was cleared depending on the current state.
        // e.g. if state is STATE_ACCOUNT_NO, display "Account Number cleared: 123"
        if (!numberPadInput.isEmpty()) {
            numberPadInput = "";
            message = "Input Cleared";
            update();
        }
    }

    // Handle the Enter button.
    // This is a more complex method: pressing Enter causes the ATM to change state,
    // progressing from STATE_ACCOUNT_NO → STATE_PASSWORD → STATE_LOGGED_IN,
    // and back to STATE_ACCOUNT_NO when logging out.

    public void processEnter()
    {
        if (state.equals(STATE_LOCKED)) return; //bao
        // The action depends on the current ATM state
        switch ( state ) {
            case STATE_ACCOUNT_NO:
                // Waiting for a complete account number
                // If nothing was entered, reset with "Invalid Account Number"
                if (numberPadInput.equals("")) {
                    soundManager.playError(); //error sound
                    message = "Invalid Account Number";
                    reset(message);
                } else {
                    soundManager.playSuccess(); //success when you move onto account password
                    // Save the entered number as accNumber, clear numberPadInput,
                    // update the state to expect password, and provide instructions
                    accNumber = numberPadInput;
                    numberPadInput = "";
                    setState(STATE_PASSWORD);
                    message = "Account Number Accepted";
                    result = "Now enter your password\nFollowed by \"Ent\"";
                }
                break;



            case STATE_PASSWORD: //Bao remade the case STATE_PASSWORD
                if (bank.isLocked()) {
                    locked();
                    break;
                }
                accPasswd = numberPadInput;
                numberPadInput = "";
                if (bank.login(accNumber, accPasswd)) {
                    soundManager.playSuccess(); //success sound when you log in
                    // Successful login: change state to STATE_LOGGED_IN and provide instructions
                    setState(STATE_LOGGED_IN);
                    message = "Logged In";
                    result = "Now enter the amount\nThen press (Dep = Deposit, W/D = Withdraw)\n(Bal = Balance) to check you current balance\n(ChP = Change Password) to change your password\n(TRN = Transfer) to bank transfer\n(STM = Statement) To show and print a receipt of transactions";
                } else { //hi bao here again
                    int attemptsLeft = 3 - bank.getLoginAttempts();
                    if (bank.isLocked()) {
                        locked(); //bao
                    } else {
                        soundManager.playError(); //error sound
                        message = "Login failed: " + attemptsLeft + " attempt(s) remaining.";
                        reset(message);
                    }
                }
                break;

            // NEW account creation cases
            // Starts off with:
            // Choose Account Type -> New account number -> New password -> Starting balance
            // NEW_ACCOUNT_TYPE is set when 'New' is pressed
            case NEW_ACCOUNT_TYPE:
                newAccType = numberPadInput;
                numberPadInput = "";
                if (newAccType.equals("1")||newAccType.equals("2")||newAccType.equals("3")||newAccType.equals("4")) {
                    soundManager.playSuccess();
                    setState(NEW_ACCOUNT_NO);
                    message = "Account type selected";
                    result = "Enter your new account number";
                }
                else {
                    soundManager.playError(); //error sound
                    message = "Invalid account type";
                    numberPadInput = "";
                }
                break;


            case NEW_ACCOUNT_NO:
                if (numberPadInput.equals("")) {
                    soundManager.playError(); //error sound
                    message = "Invalid Account Number";
                    numberPadInput = "";

                } else if (bank.duplicatorChecker(numberPadInput)) {
                    soundManager.playError(); //error sound
                    message = "Account Already Exists!";
                    numberPadInput = "";

                } else if (numberPadInput.length() <= 4) {
                    soundManager.playError(); //error sound
                    message = "Account number needs to be 5 characters";
                    numberPadInput = "";
                    //Rule:account number must be 5 characters no more no less heh
                } else {
                    soundManager.playSuccess(); //success sound
                    newAccNumber = numberPadInput;
                    numberPadInput = "";
                    setState(NEW_PASSWORD);
                    message = "Account Number Accepted";
                    result = "Now enter a password\nFollowed by \"Ent\"";
                }
                break;

            // This is the next step of account creation
            //after new account number
            case NEW_PASSWORD:
                if (numberPadInput.equals("")) {
                    soundManager.playError(); //error sound
                    message = "Invalid Password";
                    numberPadInput = "";
                }
                else if (numberPadInput.length() < 4) {
                    soundManager.playError(); //error sound
                    message = "Invalid: Password is too short";
                    numberPadInput = "";
                }
                else {
                    soundManager.playSuccess(); //success sound
                    newAccPasswd = numberPadInput;
                    numberPadInput = "";
                    setState(NEW_BALANCE);
                    message = "Password Accepted";
                    result = "Now enter starting balance\nFollowed by \"Ent\"";
                }
                break;

            // This is the last step,
            // The user can add their starting balance
            //then creates bank account once amount is valid
            case NEW_BALANCE: // like the shoe company
                int amount = parseValidAmount(numberPadInput);
                if (amount <= 0) {
                    soundManager.playError(); //error sound
                    message = "Invalid Balance";
                    numberPadInput = "";
                }
                else {
                    // basic account
                    if (newAccType.equals("1")) {
                        bank.addBankAccount(newAccNumber, newAccPasswd, amount);
                    }
                    // student account
                    else if (newAccType.equals("2")) {
                        bank.addStudentAccount(200, newAccNumber, newAccPasswd, amount);
                    }
                    // prime account
                    else if (newAccType.equals("3")) {
                        bank.addPrimeAccount(200, newAccNumber, newAccPasswd, amount);
                    }
                    // savings account
                    else if (newAccType.equals("4")) {
                        bank.addSavingsAccount(0.05, newAccNumber, newAccPasswd, amount);
                    }
                    soundManager.playSuccess(); //success sound
                    message = "Account Created! Please log in";
                    bank.saveAccounts(); // saves state of account.txt
                    reset(message);
                }
                break;

            //change password process start
            //current password needs to be inputted to ensure authentication
            case CHANGE_PASSWORD_NEW:
                if (numberPadInput.equals("")) {
                    soundManager.playError(); //error sound
                    message = "Invalid Password";
                    numberPadInput = "";
                }
                if (numberPadInput.equals(accPasswd)) {
                    soundManager.playError(); //error sound
                    message = "Invalid: You've entered your old password";
                    numberPadInput = "";
                }
                // ensures password is at least 5 characters long
                else if (numberPadInput.length() < 4) {
                    soundManager.playError(); //error sound
                    message = "Invalid: Password too short";
                    numberPadInput = "";
                }
                else {
                    soundManager.playSuccess(); //success sound
                    changedPasswd = numberPadInput;
                    numberPadInput = "";
                    setState(CHANGE_PASSWORD_CONFIRM);
                    message = "Password Accepted";
                    result = "Now enter your new password again\nto confirm";
                }
                break;

            // change password confirmation
            case CHANGE_PASSWORD_CONFIRM:
                if (numberPadInput.equals(changedPasswd)) {
                    soundManager.playSuccess(); //success sound
                    setState(STATE_LOGGED_IN);
                    bank.changePassword(changedPasswd);
                    numberPadInput = ""; // clear number pad input
                    message = "Password has been successfully changed";
                    result = "Now enter the amount\nThen press (Dep = Deposit, W/D = Withdraw)\n(Bal = Balance) to check you current balance\n(ChP = Change Password) to change your password\n(TRN = Transfer) to bank transfer\n(STM = Statement) To show and print a receipt of transactions";
                    changedPasswd = "";
                    bank.saveAccounts(); // saves state of account.txt
                }
                else {
                    soundManager.playError(); //error sound
                    message = "Invalid Password";
                    numberPadInput = "";
                }
                break;

            //confirmation for withdrawal and bank transfer
            case CONFIRMATION:
                //withdrawal
                if (pendingAction.equals("WITHDRAW"))
                {
                    if (numberPadInput.equals("1")) {
                        numberPadInput = ""; // clear number pad input
                        setState(STATE_LOGGED_IN);
                        processWithdraw();
                    }
                    else if (numberPadInput.equals("2")){
                        soundManager.playError(); //error sound
                        setState(STATE_LOGGED_IN);
                        numberPadInput = ""; // clear number pad input
                        message = "You did not withdraw";
                        result = "Now enter the amount\nThen press (Dep = Deposit, W/D = Withdraw)\n(Bal = Balance) to check you current balance\n(ChP = Change Password) to change your password\n(TRN = Transfer) to bank transfer\n(STM = Statement) To show and print a receipt of transactions";
                    }
                    else {
                        soundManager.playError(); //error sound
                        setState(STATE_LOGGED_IN);
                        numberPadInput = ""; // clear number pad input
                        message = "Invalid input, you did not withdraw";
                        result = "Now enter the amount\nThen press (Dep = Deposit, W/D = Withdraw)\n(Bal = Balance) to check you current balance\n(ChP = Change Password) to change your password\n(TRN = Transfer) to bank transfer\n(STM = Statement) To show and print a receipt of transactions";
                    }
                    pendingAction = ""; // resets pendingAction string
                }
                //bank transfer
                if (pendingAction.equals("TRANSFER"))
                {
                    if (numberPadInput.equals("1")) {
                        if (bank.transfer(transferTargetAcc, amounttransferred)) {
                            message = "Transaction was successful";
                            result = amounttransferred+" was sent\nYour current balance is: "+bank.getBalance()+"\nNow enter the amount\nThen press (Dep = Deposit, W/D = Withdraw)\n(Bal = Balance) to check you current balance\n(ChP = Change Password) to change your password\n(TRN = Transfer) to bank transfer\n(STM = Statement) To show and print a receipt of transactions";
                            soundManager.playSuccess();
                            transferTargetAcc = "";
                            numberPadInput = "";
                            setState(STATE_LOGGED_IN);
                            bank.saveAccounts(); // saves state of account.txt
                        }
                        else {
                            result = "Transfer was not successful";
                            message = "Insufficient Funds";
                            soundManager.playError();
                            transferTargetAcc = "";
                            numberPadInput = "";
                            setState(STATE_LOGGED_IN);
                        }
                    }
                    else if (numberPadInput.equals("2")) {
                        soundManager.playError(); //error sound
                        setState(STATE_LOGGED_IN);
                        numberPadInput = ""; // clear number pad input
                        transferTargetAcc = "";
                        message = "You did not transfer";
                        result = "Now enter the amount\nThen press (Dep = Deposit, W/D = Withdraw)\n(Bal = Balance) to check you current balance\n(ChP = Change Password) to change your password\n(TRN = Transfer) to bank transfer\n(STM = Statement) To show and print a receipt of transactions";
                    }
                    else {
                        soundManager.playError(); //error sound
                        setState(STATE_LOGGED_IN);
                        numberPadInput = ""; // clear number pad input
                        transferTargetAcc = "";
                        message = "Invalid input, you did not transfer";
                        result = "Now enter the amount\nThen press (Dep = Deposit, W/D = Withdraw)\n(Bal = Balance) to check you current balance\n(ChP = Change Password) to change your password\n(TRN = Transfer) to bank transfer\n(STM = Statement) To show and print a receipt of transactions";
                    }
                    pendingAction = ""; // resets pendingAction string
                }

                break;

            case STATE_TRANSFER_ACC: // toby account transfer
                transferTargetAcc = numberPadInput;


                    if (transferTargetAcc.equals("")){
                        message = "Transfer Error: Invalid account number";
                        result = "Account Number Invalid\nNow enter the amount\nThen press (Dep = Deposit, W/D = Withdraw)\n(Bal = Balance) to check you current balance\n(ChP = Change Password) to change your password\n(TRN = Transfer) to bank transfer\n(STM = Statement) To show and print a receipt of transactions";
                        transferTargetAcc = "";
                        setState(STATE_LOGGED_IN);

                    }

                    else if (transferTargetAcc.equals(bank.getLoggedInAccount().getAccNumber())){
                        message ="Transfer Error: Cannot send money to yourself";
                        result= "Cannot send money to yourself \nPlease enter a valid account number different to your own\nNow enter the amount\nThen press (Dep = Deposit, W/D = Withdraw)\n(Bal = Balance) to check you current balance\n(ChP = Change Password) to change your password\n(TRN = Transfer) to bank transfer\n(STM = Statement) To show and print a receipt of transactions";
                        soundManager.playError();
                        transferTargetAcc = "";
                        setState(STATE_LOGGED_IN);

                    }
                    else if (!bank.duplicatorChecker(transferTargetAcc)){
                        message ="Transfer Error: Invalid account number";
                        result = "This account does not exist or the account number was wrong\nNow enter the amount\nThen press (Dep = Deposit, W/D = Withdraw)\n(Bal = Balance) to check you current balance\n(ChP = Change Password) to change your password\n(TRN = Transfer) to bank transfer\n(STM = Statement) To show and print a receipt of transactions";
                        soundManager.playError();
                        transferTargetAcc = "";
                        setState(STATE_LOGGED_IN);

                    }
                    else {
                        soundManager.playSuccess();
                        setState(STATE_TRANSFER_SND);
                        pendingAction = "TRANSFER";
                        message = "Account Number Accepted";
                        result = "Please enter the amount to send to account number: "+transferTargetAcc+"\nYour current balance is: "+ bank.getBalance();



                   }
                numberPadInput = "";
                break;

            case STATE_TRANSFER_SND:

                amounttransferred = Integer.parseInt(numberPadInput);
                if (amounttransferred > 0) {
                    soundManager.playSuccess();
                    setState(CONFIRMATION);
                    numberPadInput = "";
                    message = "Transfer Confirmation";
                    result = "Are you sure you want to transfer £"+amounttransferred+" to account number: "+transferTargetAcc+"?\n(1 = Yes, 2 = No)";
                }
                else {
                    soundManager.playError();
                    numberPadInput = "";
                    message = "Transfer Invalid";
                    result = "Cannot transfer zero or less\nNow enter the amount\nThen press (Dep = Deposit, W/D = Withdraw)\n(Bal = Balance) to check you current balance\n(ChP = Change Password) to change your password\n(TRN = Transfer) to bank transfer\n(STM = Statement) To show and print a receipt of transactions";
                }
                break;


            case STATE_LOGGED_IN:
            default:
                // Do nothing for other states (user is already logged in)
        }

        update(); // Refresh the GUI to show messages and input
    }

    /**
     * Parses a string into a valid transaction amount.
     * - If the string is empty, invalid, or consists only of zeros, returns 0.
     * - Otherwise, returns the integer value.
     *
     * Purpose:
     * Helper method for validating user-entered amounts in transactions (Deposit, Withdraw, etc.).
     *
     * Note: If you later add features like Transfer, this method can be reused.
     */
    private int parseValidAmount(String number) {
        if (number.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return 0; // Invalid input -> treated as 0
        }
    }

    // Handle the Balance button:
    // - If the user is logged in, retrieve the current balance and update messages/results accordingly
    // - Otherwise, reset the ATM and display an error message
    public void processBalance() {
        if (state.equals(STATE_LOCKED)) return; //bao
        if (state.equals(CONFIRMATION)) { //nathan
            soundManager.playError();
            return;
        }
        if (state.equals(STATE_TRANSFER_ACC)) {
            soundManager.playError();
            return;
        }
        if (state.equals(STATE_TRANSFER_SND)) {
            soundManager.playError();
            return;
        }
        if (state.equals(STATE_LOGGED_IN) ) {
            soundManager.playClick(); //click sound
            numberPadInput = "";
            message = "Balance Available";
            result = "Your Balance is: " + bank.getBalance();
        } else {
            soundManager.playError(); //error sound
            reset("You are not logged in");
        }
        update();
    }

    //nathan
    //confirmation for withdrawal
    // CHANGE: when withdraw button is pressed,
    // this function is called first
    //instead of //processWithdraw()
    public void withdrawConfirm() {
        if (state.equals(STATE_LOCKED)) return; //from bao
        if (state.equals(STATE_LOGGED_IN)) {
            amount = parseValidAmount(numberPadInput);
            if (amount > 0) {
                soundManager.playClick(); //click sound
                numberPadInput = ""; // clear number pad input
                message = "Are you sure you want to withdraw?";
                result = "Withdraw?\n1 = Yes\n2 = No";
                setState(CONFIRMATION);
                pendingAction = "WITHDRAW";
            }
            else {
                soundManager.playError(); //error sound
                message = "Invalid Amount";
                result = "Now enter the amount\nThen press (Dep = Deposit, W/D = Withdraw)\n(Bal = Balance) to check you current balance\n(ChP = Change Password) to change your password\n(TRN = Transfer) to bank transfer\n(STM = Statement) To show and print a receipt of transactions";
                numberPadInput = "";
            }
        }
        else {
            soundManager.playError(); //error sound
            message = "You are not logged in";
        }
        update();
    }

    // Handle the Withdraw button:
    // If the user is logged in, attempt to withdraw the amount entered
    // otherwise, reset the ATM and display an error message.
    // Reads the amount from numberPadInput, validates it, and updates messages/results accordingly.
    public void processWithdraw() {

        if(bank.withdraw( amount ).equals("SUCCESS")){
            soundManager.playSuccess(); //success sound
            message = "Withdraw Successful";
            result = "Withdrawn: " + numberPadInput + "\nYour Balance is now: " + bank.getBalance();
            bank.saveAccounts(); // saves state of account.txt
        }
        else if (bank.withdraw( amount ).equals("INSUFFICIENT")) {
            soundManager.playError(); //error sound
            message = "Withdraw Failed: Insufficient Funds";
            result = "Now enter the amount\nThen press (Dep = Deposit, W/D = Withdraw)\n(Bal = Balance) to check you current balance\n(ChP = Change Password) to change your password\n(TRN = Transfer) to bank transfer\n(STM = Statement) To show and print a receipt of transactions";
        }
        else if (bank.withdraw( amount ).equals("LIMIT")) {
            soundManager.playError(); //error sound
            message = "Withdraw Failed: You have gone over your withdrawal limit";
            result = "Now enter the amount\nThen press (Dep = Deposit, W/D = Withdraw)\n(Bal = Balance) to check you current balance\n(ChP = Change Password) to change your password\n(TRN = Transfer) to bank transfer\n(STM = Statement) To show and print a receipt of transactions";
        }
        else if (bank.withdraw( amount ).equals("OVERDRAFT")) {
            soundManager.playError(); //error sound
            message = "Withdraw Failed: You have gone over your overdraft limit";
            result = "Now enter the amount\nThen press (Dep = Deposit, W/D = Withdraw)\n(Bal = Balance) to check you current balance\n(ChP = Change Password) to change your password\n(TRN = Transfer) to bank transfer\n(STM = Statement) To show and print a receipt of transactions";
        }
        amount = 0; //clear amount just in case
        update();
    }

    // Handle the Deposit button:
    // - If the user is logged in, deposit the amount entered into the bank
    // - Reads the amount from numberPadInput, validates it, and updates messages/results accordingly
    // - Otherwise, reset the ATM and display an error message
    public void processDeposit() {
        if (state.equals(STATE_LOCKED)) return; //bao
        if (state.equals(CONFIRMATION)) { //nathan
            soundManager.playError();
            return;
        }
        if (state.equals(STATE_TRANSFER_ACC)) {
            soundManager.playError();
            return;
        }
        if (state.equals(STATE_TRANSFER_SND)) {
            soundManager.playError();
            return;
        }
        if (state.equals(STATE_LOGGED_IN)) {
            int amount = parseValidAmount(numberPadInput);
            if (amount > 0) {
                soundManager.playSuccess(); //success sound
                bank.deposit( amount );
                message = "Deposit Successful";
                result = "Deposited: " + numberPadInput + "\nYour Balance is now: " + bank.getBalance();
                bank.saveAccounts(); // saves state of account.txt
            }
            else {
                soundManager.playError(); //error sound
                message = "Invalid Amount";
                result = "Now enter the amount\nThen press (Dep = Deposit, W/D = Withdraw)\n(Bal = Balance) to check you current balance\n(ChP = Change Password) to change your password\n(TRN = Transfer) to bank transfer\n(STM = Statement) To show and print a receipt of transactions";
            }
            numberPadInput = "";
        }
        else {
            soundManager.playError(); //error sound
            reset("You are not logged in");
        }
        update();
    }
    public void processTransfer() {//Toby
        if (state.equals(STATE_LOCKED)) return; //bao
        if (state.equals(STATE_TRANSFER_ACC)) {
            soundManager.playError();
            return;
        }
        if (state.equals(STATE_TRANSFER_SND)) {
            soundManager.playError();
            return;
        }
        if (state.equals(CONFIRMATION)) {
            soundManager.playError();
            return;
        }
        if (state.equals(STATE_LOGGED_IN)) {
            setState(STATE_TRANSFER_ACC);
            numberPadInput = "";

            result = "Bank Transfer \nEnter the user account number that you want to transfer";

            message = "Transfer to: " + transferTargetAcc;
            soundManager.playSuccess();
        }
        else {
            soundManager.playError();
            reset("You are not logged in");
        }
        update();
    }

    // Handle the L/O button:
    // - If the user is logged in, log out
    // - Otherwise, reset the ATM and display an error message
    public void processFinish() {
        if (state.equals(STATE_LOCKED)) {
            soundManager.playSuccess(); //success sound
            bank.logout();
            setState(STATE_GOODBYE);
            view.Goodbye(view.getWindow());
            return;
        }
        if (state.equals(CONFIRMATION)) { //nathan
            soundManager.playError();
            return;
        }
        if (state.equals(STATE_ACCOUNT_NO))
        {
            soundManager.playSuccess(); //success sound
            bank.logout();
            setState(STATE_GOODBYE);
            view.Goodbye(view.getWindow());
        }
        else if (state.equals(STATE_LOGGED_IN))
        {
            soundManager.playSuccess(); //success sound
            bank.logout();
            reset("Log out successful"); // To exit, press log out again
        }
        else if (state.equals(STATE_TRANSFER_ACC)) {
            soundManager.playClick();
            setState(STATE_LOGGED_IN);
            message = "Returned to home";
            result = "Now enter the amount\nThen press (Dep = Deposit, W/D = Withdraw)\n(Bal = Balance) to check you current balance\n(ChP = Change Password) to change your password\n(TRN = Transfer) to bank transfer\n(STM = Statement) To show and print a receipt of transactions";
        }
        else if (state.equals(NEW_ACCOUNT_NO)||state.equals(NEW_PASSWORD)||state.equals(NEW_ACCOUNT_TYPE)||state.equals(STATE_PASSWORD)) {
            soundManager.playClick();
            setState(STATE_ACCOUNT_NO);
            message = "Returned to home";
            result = "Enter your account number\nFollowed by 'Ent'\nTo create a new account, press 'New'\nWhen you are done, press (L/O = Logout)";
        }
        else {
            soundManager.playError();
            message = "Cannot logout during this action";
        }
        update();
    }

    // Handle unknown or invalid buttons for the current state:
    // - Reset the ATM and display an "Invalid Command" message
    public void processUnknownKey(String action) {
        reset("Invalid Command");
        update();
    }

    // Notify the View of changes by calling its update method
    private void update() {
        view.update(message,numberPadInput, result);
    }

    // account creation method by tony
    public void processNewAccount(){
        if (state.equals(STATE_LOCKED)) return; //bao
        if (state.equals(CONFIRMATION)) { //nathan
            soundManager.playError();
            return;
        }
        if (state.equals(STATE_TRANSFER_ACC)) {
            soundManager.playError();
            return;
        }
        if (state.equals(STATE_TRANSFER_SND)) {
            soundManager.playError();
            return;
        }
        // Checks if the user is logged in first
        // prevents account creation when the user is already logged in
        if (state.equals(STATE_LOGGED_IN) ) {
            soundManager.playError();
            message = "Please log out first before pressing 'New'";
            result = "Now enter the amount\nThen press (Dep = Deposit, W/D = Withdraw)\n(Bal = Balance) to check you current balance\n(ChP = Change Password) to change your password\n(TRN = Transfer) to bank transfer\n(STM = Statement) To show and print a receipt of transactions";
        }
        else if (state.equals(CHANGE_PASSWORD_NEW)||state.equals(CHANGE_PASSWORD_CONFIRM)) {
            soundManager.playError();
            message = "Please change password and logout first";
        }
        else {
            soundManager.playSuccess();
            setState(NEW_ACCOUNT_TYPE);
            numberPadInput = "";
            message = "Create new account";
            result = "Select account type\n 1 = Basic account\n 2 = Student account\n 3 = Prime account\n 4 = Savings account\nNotes:\nStudent accounts have a withdrawal limit of £200\nPrime accounts have an overdraft limit of -£200\nSavings accounts have an interest rate of 5% per period ";
        }
        update();
    }

    //function to change existing user's password
    public void processChangePassword(){
        if (state.equals(STATE_LOCKED)) return; //bao
        if (state.equals(CONFIRMATION)) { //nathan
            soundManager.playError();
            return;
        }
        if (state.equals(STATE_TRANSFER_ACC)) {
            soundManager.playError();
            return;
        }
        if (state.equals(STATE_TRANSFER_SND)) {
            soundManager.playError();
            return;
        }
        if (state.equals(STATE_LOGGED_IN)) {
            setState(CHANGE_PASSWORD_NEW);
            soundManager.playSuccess(); //success sound
            message = "Change Password";
            result = "Enter your new password";
        }
        else { // edit by nathan week 7, requires user to be logged in to change pass otherwise error
            soundManager.playError(); //error sound
            numberPadInput = "";
            message = "Log in first to change password";
            result = "Enter your account number\nFollowed by \"Ent\"\nTo create a new account, press \"New\"\nWhen you are done, press (L/O = Logout/Return)";
        }
        update();
    }
    public void processMiniStatement() {
        if (state.equals(STATE_LOCKED)) return;
        if (state.equals(CONFIRMATION)) { soundManager.playError(); return; }
        if (state.equals(STATE_TRANSFER_ACC)) { soundManager.playError(); return; }
        if (state.equals(STATE_TRANSFER_SND)) { soundManager.playError(); return; }

        if (state.equals(STATE_LOGGED_IN)) {
            soundManager.playClick();
            numberPadInput = "";
            message = "Bank Transactions";
            result = bank.getMiniStatement();
        } else {
            soundManager.playError();
            reset("You are not logged in");
        }
        update();
    }

    public void showHelp() {
        view.showHelpPopup();
    }
}

