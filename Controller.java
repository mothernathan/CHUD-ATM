package com.atmbanksimulator;

// ===== ⚡ Controller (Nerves) =====

// The Controller receives user actions from the View and delegates the appropriate tasks to the UIModel.
// Its main job is to decide what to do based on the user input.
public class Controller {

    UIModel UIModel; // Reference to the UIModel (part of the MVC setup)
    soundManager soundManager; //nathan: references the sound manager class file

    // The process method is called by the View in response to user interface events.
    // It uses a switch statement to determine which UIModel method should be called,
    // and delegates the task accordingly.
    void process( String action ) {
        switch (action) {
            case "1" : case "2" : case "3" : case "4" : case "5" :
            case "6" : case "7" : case "8" : case "9" : case "0" :
                UIModel.processNumber(action);
                soundManager.playClick(); //click sound
                break;
            case "CLR":
                UIModel.processClear();
                soundManager.playClick(); //click sound
                break;
            case "Ent":
                UIModel.processEnter();
                break;
            case "W/D":
                UIModel.withdrawConfirm();
                break;
            case "Dep":
                UIModel.processDeposit();
                break;
            case "Bal":
                UIModel.processBalance();
                break;
            case "L/O":
                UIModel.processFinish();
                break;
            case "New":
                UIModel.processNewAccount();
                break;
            case "ChP":
                UIModel.processChangePassword();
                break;
            case "TRN":
                UIModel.processTransfer();
                break;
            case "Stm":
                UIModel.processMiniStatement();
                break;
            case "\uD83D\uDD0A":
                soundManager.toggleMute();
                break;
            case "?":
                UIModel.showHelp();
                break;
            default:
                UIModel.processUnknownKey(action);
                break;
        }
    }

}


