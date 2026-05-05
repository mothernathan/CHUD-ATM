package com.atmbanksimulator;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.ColumnConstraints;

//dex edited

class View {

    int H = 700;
    int W = 680;

    Controller controller;

    private Label laMsg;
    private TextField tfInput;
    private TextArea taResult;
    private ScrollPane scrollPane;

    private GridPane grid;
    private TilePane buttonPane;

    private Stage window;

    soundManager soundManager;
    UIModel uiModel;


    public void Welcome(Stage window) {

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setId("Layout");

        // ⚡ Load the image
        Image logo = new Image(getClass().getResourceAsStream("/com/atmbanksimulator/CHUD.png"));
        ImageView logoView = new ImageView(logo);
        logoView.setFitWidth(150); // optional: resize width
        logoView.setPreserveRatio(true); // keep aspect ratio

        // Welcome text
        Label welcome = new Label("Welcome to CHUD ATM");
        welcome.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        // Continue button
        Button Continuebutton = new Button("Continue");
        Continuebutton.setPrefWidth(150);
        Continuebutton.setOnAction(e -> {
            this.start(window);
            uiModel.initialise();
            soundManager.playClick(); //click sound
        });

        // Add image, label, button to layout
        layout.getChildren().addAll(logoView, welcome, Continuebutton);

        Scene scene = new Scene(layout, 700, 680);

        // Apply CSS
        scene.getStylesheets().add(
                getClass().getResource("atm.css").toExternalForm()
        );

        Image icon = new Image(getClass().getResourceAsStream("/com/atmbanksimulator/CHUD.png"));
        window.getIcons().add(icon);

        window.setScene(scene);
        window.setTitle("ATM");
        window.show();
        soundManager.playIntro();
    }

    public Stage getWindow() {
        return this.window;
    }

    public void Goodbye(Stage window) {

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setId("Layout");

        // ⚡ Load the image
        Image goodbye_img = new Image(getClass().getResourceAsStream("/com/atmbanksimulator/goodbye.png"));
        ImageView goodbyeView = new ImageView(goodbye_img);
        goodbyeView.setFitWidth(150); // optional: resize width
        goodbyeView.setPreserveRatio(true); // keep aspect ratio

        Label goodbye = new Label("Thank you for using CHUD ATM");
        goodbye.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button exitButton = new Button("Exit");
        exitButton.setPrefWidth(120);
        exitButton.setOnAction(e -> window.close());

        layout.getChildren().addAll(goodbyeView, goodbye, exitButton);

        Scene scene = new Scene(layout, 700, 680);

        scene.getStylesheets().add(
                getClass().getResource("atm.css").toExternalForm()
        );
        window.setScene(scene);
    }

    public void start(Stage window) {

        this.window = window;
        Image icon = new Image(getClass().getResourceAsStream("/com/atmbanksimulator/CHUD.png"));
        window.getIcons().add(icon);

        grid = new GridPane();
        grid.setId("Layout");
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(15);
        grid.setHgap(15);
        grid.setStyle("-fx-padding: 20;");

        // Top message label spanning 6 columns
        laMsg = new Label("Welcome to Bank ATM");
        laMsg.setStyle("-fx-font-size:18px; -fx-font-weight:bold;");
        grid.add(laMsg, 0, 0, 7, 1);

        // Input field spanning 6 columns
        tfInput = new TextField();
        tfInput.setEditable(false);
        tfInput.setPrefHeight(40);
        tfInput.setStyle("-fx-font-size:16px;");
        grid.add(tfInput, 0, 1, 7, 1);

        // Result TextArea inside ScrollPane spanning 6 columns
        taResult = new TextArea();
        taResult.setEditable(false);
        taResult.setPrefHeight(120);

        scrollPane = new ScrollPane();
        scrollPane.setContent(taResult);
        scrollPane.setFitToWidth(true);
        grid.add(scrollPane, 0, 2, 7, 1);

        // Button Grid
        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(10);
        buttonGrid.setVgap(10);
        buttonGrid.setAlignment(Pos.CENTER);

        // allows the button grid to have spaces in between the number pad
        ColumnConstraints gap = new ColumnConstraints(60); // 30px gap
        buttonGrid.getColumnConstraints().add(new ColumnConstraints()); // col 0
        buttonGrid.getColumnConstraints().add(gap);                     // col 1 (spacer)
        buttonGrid.getColumnConstraints().add(new ColumnConstraints()); // col 2
        buttonGrid.getColumnConstraints().add(new ColumnConstraints()); // col 3
        buttonGrid.getColumnConstraints().add(new ColumnConstraints()); // col 4
        buttonGrid.getColumnConstraints().add(gap);                     // col 5 (spacer)
        buttonGrid.getColumnConstraints().add(new ColumnConstraints()); // col 6

        // Define all button labels
        String[][] buttonTexts = {
                {"TRN", "", "7", "8", "9", "", "\uD83D\uDD0A"},
                {"Dep", "", "4", "5", "6", "", "ChP"},
                {"W/D", "", "1", "2", "3", "", "New"},
                {"Bal", "", "", "0", "", "", "L/O"},
                {"Stm", "", "CLR", "", "Ent", "", "?"}
        };



        // Add buttons to the GridPane
        for (int r = 0; r < buttonTexts.length; r++) {
            for (int c = 0; c < buttonTexts[r].length; c++) {
                String text = buttonTexts[r][c];
                if (!text.isEmpty()) {
                    Button btn = new Button(text);
                    btn.setPrefSize(70, 70);
                    btn.setStyle("-fx-font-size:16px; -fx-font-weight:bold;");
                    btn.setOnAction(this::buttonClicked);
                    buttonGrid.add(btn, c, r);
                }
            }
        }

        // Add buttonGrid to main grid
        grid.add(buttonGrid, 0, 3, 7, 1);

        Scene scene = new Scene(grid, W, H);
        scene.getStylesheets().add(
                getClass().getResource("atm.css").toExternalForm()
        );

        window.setScene(scene);
        window.setTitle("Central Hub for Unified Deposits ATM");
        window.show();

        //allows CSS on message box, result box and input box
        laMsg.setId("msgLabel");
        tfInput.setId("inputField");
        taResult.setId("resultArea");

        laMsg.setMaxWidth(Double.MAX_VALUE);
        laMsg.setAlignment(Pos.CENTER);
    }

    private void buttonClicked(ActionEvent event) {

        Button b = (Button) event.getSource();
        String text = b.getText();

        System.out.println("View::buttonClicked: label = " + text);

        controller.process(text);
    }

    public void update(String msg, String tfInputMsg, String taResultMsg) {

        laMsg.setText(msg);
        tfInput.setText(tfInputMsg);
        taResult.setText(taResultMsg);

    }

    // guide window
    private void showHelp(Stage owner) {
        Stage popup = new Stage();
        popup.initOwner(owner);
        popup.setTitle("Button Guide");
        popup.getIcons().add(new Image(getClass().getResourceAsStream("/com/atmbanksimulator/CHUD.png")));

        //guide text
        String guide =
                "TRN  — Transfer (Must be logged in)\n" +
                "Dep  — Deposit (Must be logged in)\n" +
                "W/D  — Withdraw (Must be logged in)\n" +
                "Bal  — Check Balance (Must be logged in)\n" +
                "Stm  — Show and Print Transactions (Must be logged in)\n" +
                "ChP  — Change Password (Must be logged in)\n" +
                "New  — New Account\n" +
                "L/O  — Log Out/Return\n" +
                "CLR  — Clear Input\n" +
                "Ent  — Enter/Confirm\n" +
                "🔊   — Toggle Mute";

        Label info = new Label(guide);
        info.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 14px; -fx-padding: 20px;");

        Button close = new Button("Close");
        close.setOnAction(e -> popup.close());

        VBox layout = new VBox(10, info, close);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20px;");

        popup.setScene(new Scene(layout));
        popup.setResizable(false);
        popup.show();
    }

    // guide window wrapper
    public void showHelpPopup() {
        showHelp(window);
    }
}