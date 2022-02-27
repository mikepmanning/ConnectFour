package com.example.connectfour;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

import static com.example.connectfour.ConnectFourController.initialGameScreen;

public class OnePlayerViewController {


    public TextField onePlayerNameField;
    public Button onePlayerBack;
    public AnchorPane onePlayerView;
    public Button hardRobotButton;
    public Button easyRobotButton;
    public Button mediumRobotButton;

    private com.example.connectfour.ConnectFourController connectFourController;

    public void injectController(ConnectFourController connectFourController){
        this.connectFourController = connectFourController;
    }

    @FXML
    public void onOnePlayerBackButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        initialGameScreen(stage);
    }

    @FXML
    public void onEasyButtonClick(ActionEvent event) {
        connectFourController.setPlayerOne (new Player(onePlayerNameField.getText(), Color.YELLOW));
        connectFourController.setRobotPlayerTwo(new EasyRobot(Color.RED));
        connectFourController.switchToGameScene(event);
    }

    @FXML
    public void onMediumButtonClick(ActionEvent event) {
        connectFourController.setPlayerOne (new Player(onePlayerNameField.getText(), Color.YELLOW));
        connectFourController.setRobotPlayerTwo(new MediumRobot(Color.RED));
        connectFourController.switchToGameScene(event);
    }

    @FXML
    public void onHardButtonClick(ActionEvent event) {
        connectFourController.setPlayerOne (new Player(onePlayerNameField.getText(), Color.YELLOW));
        connectFourController.setRobotPlayerTwo(new HardRobot(Color.RED));
        connectFourController.switchToGameScene(event);
    }

}
