package com.example.connectfour;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class ConnectFourApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        ConnectFourController.initialGameScreen(stage);
    }

    public static void main(String[] args) {
        launch();
    }

}