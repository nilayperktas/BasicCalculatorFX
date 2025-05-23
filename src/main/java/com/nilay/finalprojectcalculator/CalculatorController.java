package com.nilay.finalprojectcalculator; //declares the package that declares the java class belongs to

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class CalculatorController {

    @FXML private Label displayLabel;
    @FXML private TextField historyField;
    @FXML private Label userLabel;

    // Original variables
    private String currentInput = "";
    private String firstNumber = "";
    private String operator = "";
    private boolean startNewInput = true;
    private String currentUsername;

    // NEW: Simple variables for precedence
    private String pendingNumber = "";    // For storing first number
    private String pendingOperator = "";  // For storing operator

    @FXML
    public void initialize() {
        displayLabel.setText("0");

        currentUsername = LoginController.getCurrentUser(); //this implies there is LoginController class managing user login

        if (userLabel != null && currentUsername != null) {
            userLabel.setText(" User: " + currentUsername ); //display the username
        }

        if (currentUsername != null) {
            historyField.setText(UserHistory.getHistoryForUser(currentUsername)); //load history
        } else {
            historyField.setText("");
        }

        javafx.application.Platform.runLater(() -> { //listen keyboard
            //ensure that the code inside is executed on the JavaFX Application Thread after the scene has been initialized

            Scene scene = displayLabel.getScene();
            if (scene != null) {
                scene.addEventFilter( KeyEvent.KEY_PRESSED, this::handleKeyPress );
            }
        });
    }

    @FXML
    private void handleNumberClick(ActionEvent event) {
        if (startNewInput) {
            displayLabel.setText("");
            startNewInput = false;
        }
        String digit = ((Button) event.getSource()).getText();
        displayLabel.setText(displayLabel.getText() + digit);
        currentInput = displayLabel.getText();
}

    private void handleKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();

        if ((code.isDigitKey() || (code.isKeypadKey() && code != KeyCode.MULTIPLY &&
                code != KeyCode.DIVIDE && code != KeyCode.ADD &&
                code != KeyCode.SUBTRACT && code != KeyCode.ENTER)) &&
                !event.isControlDown() && !event.isAltDown()) {

            String digit;
            if (code.isDigitKey()) {
                digit = code.getName();
            } else {
                digit = code.getName().replace("NUMPAD ", "");
            }

            if (startNewInput) {
                displayLabel.setText("");
                startNewInput = false;
            }
            displayLabel.setText(displayLabel.getText() + digit);
            currentInput = displayLabel.getText();

            event.consume();
        }
        else if (code == KeyCode.PLUS || code == KeyCode.ADD ||
                code == KeyCode.SUBTRACT || code == KeyCode.MINUS ||
                code == KeyCode.MULTIPLY || code == KeyCode.ASTERISK ||
                code == KeyCode.DIVIDE || code == KeyCode.SLASH) {

            String op;
            if (code == KeyCode.PLUS || code == KeyCode.ADD) op = "+";
            else if (code == KeyCode.SUBTRACT || code == KeyCode.MINUS) op = "-";
            else if (code == KeyCode.MULTIPLY || code == KeyCode.ASTERISK) op = "*";
            else op = "/";

            if (!currentInput.isEmpty()) {
                handleOperatorClick(op);
            }

            event.consume();
        }
        else if (code == KeyCode.EQUALS || code == KeyCode.ENTER) {
            handleFinalCalculation();
            event.consume();
        }
        else if (code == KeyCode.PERIOD || code == KeyCode.DECIMAL) {
            if (startNewInput) {
                displayLabel.setText("0.");
                startNewInput = false;
            } else if (!displayLabel.getText().contains(".")) {
                displayLabel.setText(displayLabel.getText() + ".");
            }
            currentInput = displayLabel.getText();

            event.consume();
        }
        else if (code == KeyCode.BACK_SPACE) {
            String currentDisplay = displayLabel.getText();

            if (!currentDisplay.isEmpty() && !currentDisplay.equals("0")) {
                if (currentDisplay.length() > 1) {
                    displayLabel.setText(currentDisplay.substring(0, currentDisplay.length() - 1));
                } else {
                    displayLabel.setText("0");
                    startNewInput = true;
                }
                currentInput = displayLabel.getText().equals("0") ? "" : displayLabel.getText();
            }

            event.consume();
        }
        else if (code == KeyCode.ESCAPE) {
            handleClearClick();
            event.consume();
        }
    }

    @FXML
    private void handleOperatorClick(ActionEvent event) {
        String op = ((Button) event.getSource()).getText();
        handleOperatorClick(op);
    }

    // NEW: Simplified operator handling with precedence
    private void handleOperatorClick(String op) {
        if (currentInput.isEmpty()) return;

        // If we have a pending calculation, do it first
        if (!firstNumber.isEmpty() && !operator.isEmpty()) {

            // If current operator has higher precedence, calculate immediately
            if (hasHigherPrecedence(operator, op)) {
                calculateResult();
                operator = op;
                firstNumber = displayLabel.getText();
            }
            // If pending operator has higher or equal precedence
            else {
                // If we have a pending low precedence operation, store it
                if (!pendingNumber.isEmpty() && !pendingOperator.isEmpty()) {
                    // Calculate the pending operation
                    String temp = firstNumber;
                    firstNumber = pendingNumber;
                    String tempOp = operator;
                    operator = pendingOperator;
                    calculateResult();
                    firstNumber = displayLabel.getText();
                    operator = tempOp;
                    calculateResult();
                }

                // Store current state if it's low precedence
                if (op.equals("+") || op.equals("-")) {
                    calculateResult();
                    pendingNumber = displayLabel.getText();
                    pendingOperator = op;
                    firstNumber = "";
                    operator = "";
                } else {
                    calculateResult();
                    operator = op;
                    firstNumber = displayLabel.getText();
                }
            }
        } else {
            // First operation
            firstNumber = currentInput;
            operator = op;
        }

        startNewInput = true;
        currentInput = "";
    }

    @FXML
    private void handleEqualsClick(ActionEvent event) {
        handleFinalCalculation();
    }

    // NEW: Handle final calculation with pending operations
    private void handleFinalCalculation() {
        if (!firstNumber.isEmpty() && !operator.isEmpty() && !currentInput.isEmpty()) {
            calculateResult();

            // If we have a pending operation, do it now
            if (!pendingNumber.isEmpty() && !pendingOperator.isEmpty()) {
                String temp = displayLabel.getText();
                firstNumber = pendingNumber;
                operator = pendingOperator;
                currentInput = temp;
                calculateResult();

                // Clear pending operation
                pendingNumber = "";
                pendingOperator = "";
            }

            operator = "";
            firstNumber = displayLabel.getText();
            startNewInput = true;
            currentInput = "";
        }
    }

    @FXML
    private void handleClearClick(ActionEvent event) {
        handleClearClick();
    }

    private void handleClearClick() {
        displayLabel.setText("0");
        currentInput = "";
        firstNumber = "";
        operator = "";
        // NEW: Clear pending operations
        pendingNumber = "";
        pendingOperator = "";
        startNewInput = true;
    }

    // NEW: Simple precedence check
    private boolean hasHigherPrecedence(String op1, String op2) {
        return getPrecedence(op1) > getPrecedence(op2);
    }

    // NEW: Get operator precedence (higher number = higher precedence)
    private int getPrecedence(String op) {
        if (op.equals("*") || op.equals("×") || op.equals("/") || op.equals("÷")) {
            return 2;
        } else if (op.equals("+") || op.equals("-")) {
            return 1;
        }
        return 0;
    }

    @FXML
    private void handleDeleteClick(ActionEvent event) {
        String currentDisplay = displayLabel.getText();

        if (!currentDisplay.isEmpty() && !currentDisplay.equals("0")) {
            if (currentDisplay.length() > 1) {
                displayLabel.setText(currentDisplay.substring(0, currentDisplay.length() - 1));
            } else {
                displayLabel.setText("0");
                startNewInput = true;
            }
            currentInput = displayLabel.getText().equals("0") ? "" : displayLabel.getText();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            if (currentUsername != null) {
                UserHistory.updateHistoryForUser(currentUsername, historyField.getText());
            }

            LoginController.logoutUser();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent loginRoot = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene loginScene = new Scene(loginRoot);
            stage.setScene(loginScene);
            stage.setTitle("Login");
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDecimalClick(ActionEvent event) {
        if (startNewInput) {
            displayLabel.setText("0.");
            startNewInput = false;
        } else if (!displayLabel.getText().contains(".")) {
            displayLabel.setText(displayLabel.getText() + ".");
        }
        currentInput = displayLabel.getText();
    }

    @FXML
    private void handleSignClick(ActionEvent event) {
        String current = displayLabel.getText();
        if (!current.equals("0") && !current.isEmpty()) {
            if (current.startsWith("-")) {
                displayLabel.setText(current.substring(1));
            } else {
                displayLabel.setText("-" + current);
            }
            currentInput = displayLabel.getText();
        }
    }

    // Original calculateResult method (unchanged)
    private void calculateResult() {
        try {
            double num1 = Double.parseDouble(firstNumber);
            double num2 = Double.parseDouble(currentInput);
            double result = 0;

            switch (operator) {
                case "+":
                    result = num1 + num2;
                    break;
                case "-":
                    result = num1 - num2;
                    break;
                case "*":
                case "×":
                    result = num1 * num2;
                    break;
                case "/":
                case "÷":
                    if (num2 == 0) {
                        displayLabel.setText("Error: Division by zero");
                        updateHistory("Division by zero error");
                        startNewInput = true;
                        currentInput = "";
                        firstNumber = "";
                        operator = "";
                        return;
                    }
                    result = num1 / num2;
                    break;
            }

            if (result == (long) result) {
                displayLabel.setText(String.format("%d", (long) result));
            } else {
                displayLabel.setText(String.format("%.8f", result).replaceAll("0*$", "").replaceAll("\\.$", ""));
            }

            currentInput = displayLabel.getText();
            updateHistory(String.format("%s %s %s = %s",
                    firstNumber, operator, num2, currentInput));

        } catch (NumberFormatException e) {
            displayLabel.setText("Error");
            updateHistory("Invalid calculation");
            startNewInput = true;
            currentInput = "";
            firstNumber = "";
            operator = "";
        }
    }

    private void updateHistory(String entry) {
        String currentHistory = historyField.getText();
        historyField.setText(currentHistory + entry + " | ");

        historyField.positionCaret(historyField.getText().length());

        if (currentUsername != null) {
            UserHistory.updateHistoryForUser(currentUsername, historyField.getText());
        }
    }

    @FXML
    private void handleClearHistory(ActionEvent event) {
        historyField.setText("");

        if (currentUsername != null) {
            UserHistory.updateHistoryForUser(currentUsername, "");
        }
    }
}