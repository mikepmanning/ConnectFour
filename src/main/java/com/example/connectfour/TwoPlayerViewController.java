package com.example.connectfour;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

import static com.example.connectfour.ConnectFourController.initialGameScreen;

public class TwoPlayerViewController {

    public TextField onePlayerNameField;
    public TextField twoPlayerNameField;
    private com.example.connectfour.ConnectFourController connectFourController;

    public void injectController(ConnectFourController connectFourController){
        this.connectFourController = connectFourController;
    }

    @FXML
    public void onTwoPlayerBackButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        initialGameScreen(stage);
    }

    @FXML
    public void onTwoPlayerSubmitButtonClick(ActionEvent event) {
        connectFourController.setPlayerOne (new Player(onePlayerNameField.getText(), Color.YELLOW));
        connectFourController.setPlayerTwo (new Player(twoPlayerNameField.getText(), Color.RED));
        connectFourController.switchToGameScene(event);
    }
}
