package com.nilay.finalprojectcalculator;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.*;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class LoginController {

    private static Map<String, String> users = new HashMap<>();
    private static String currentUser = null;
    private static final String DATA_FILE = "users.dat";

    // Instance variables for FXML elements
    @FXML private StackPane rootPane;
    @FXML private VBox loginPanel, registerPanel;
    @FXML private TextField loginUsername, registerUsername;
    @FXML private PasswordField loginPassword, registerPassword, confirmPassword;
    @FXML private Label loginError, registerError;

    @FXML
    private void initialize() {
        loadUsers();
        UserHistory.loadAllHistories(); // Load all user histories
        showLogin();
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = loginUsername.getText().trim();
        String password = loginPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            loginError.setText("Please enter username and password");
            return;
        }

        if (authenticateUser(username, password)) {
            currentUser = username;

            try {
                // Load the calculator view
                FXMLLoader loader = new FXMLLoader(getClass().getResource("calculator-view.fxml"));
                Parent calculatorRoot = loader.load();

                // Get the current window
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                // Set new scene
                Scene calculatorScene = new Scene(calculatorRoot, 600, 400);
                stage.setScene(calculatorScene);
                stage.setTitle("Calculator - " + username);
                stage.centerOnScreen();

            } catch (IOException e) {
                e.printStackTrace();
                loginError.setText("Error loading calculator view");
            }
        } else {
            loginError.setText("Invalid username or password");
        }
    }

    @FXML
    private void handleRegister() {
        String username = registerUsername.getText().trim();
        String password = registerPassword.getText();
        String confirm = confirmPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            registerError.setText("Please enter username and password");
            return;
        }

        if (!password.equals(confirm)) {
            registerError.setText("Passwords don't match");
            return;
        }

        if (password.length() < 6) {
            registerError.setText("Password must be at least 6 characters");
            return;
        }

        if (registerUser(username, password)) {
            // Initialize empty history for new user
            UserHistory.updateHistoryForUser(username, "");

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Registration successful! You can now login.");
            alert.showAndWait();
            clearFields();
            showLogin();
        } else {
            registerError.setText("Username already exists");
        }
    }

    @FXML
    private void showLogin() {
        loginPanel.setVisible(true);
        registerPanel.setVisible(false);
        clearErrors();
    }

    @FXML
    private void showRegister() {
        loginPanel.setVisible(false);
        registerPanel.setVisible(true);
        clearErrors();
    }

    private void clearFields() {
        loginUsername.clear();
        loginPassword.clear();
        registerUsername.clear();
        registerPassword.clear();
        confirmPassword.clear();
    }

    private void clearErrors() {
        loginError.setText("");
        registerError.setText("");
    }

    // Authentication methods
    private boolean authenticateUser(String username, String password) {
        String storedHash = users.get(username);
        return storedHash != null && storedHash.equals(hashPassword(password));
    }

    private boolean registerUser(String username, String password) {
        if (users.containsKey(username)) {
            return false;
        }
        users.put(username, hashPassword(password));
        saveUsers();
        return true;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return password;
        }
    }

    @SuppressWarnings("unchecked")
    private void loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            users = (Map<String, String>) ois.readObject();
        } catch (Exception e) {
            users = new HashMap<>();
        }
    }

    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Static methods for external access
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static String getCurrentUser() {
        return currentUser;
    }

    public static void logoutUser() {
        currentUser = null;
    }
}