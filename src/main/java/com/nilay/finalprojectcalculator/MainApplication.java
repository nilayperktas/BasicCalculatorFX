package com.nilay.finalprojectcalculator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class  MainApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize user history system
        UserHistory.loadAllHistories();

        // Start with the login screen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Scene scene = new Scene(loader.load());

        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
