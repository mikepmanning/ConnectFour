package com.example.connectfour;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConnectFourController {
    public Button quitButton;
    public Button twoPlayerButton;
    @FXML
    private Label welcomeText;
    private GridPane root;

    private static final int COLUMNS = 7;
    private static final int ROWS = 6;
    private static final double TILE_SIZE = 80;

    private boolean noClickAllowed = false; //to prevent playing another piece of the same color before previous move complete.
    private boolean gameOverFlag = false;

    private char currentPlayer = 'Y';
    private final Board board = new Board();
    private final Pane discRoot = new Pane();
    private final Pane winningRoot = new Pane();
    private Ellipse playerOneCircle;
    private Ellipse playerTwoCircle;


    //@FXML
    //protected void onOnePlayerButtonClick() {
   //     welcomeText.setText("Starting One Player Game");
    //}

    @FXML
    protected void onQuitButtonClick() {
        welcomeText.setText("Quitting");
        System.exit(0);
    }


    public void switchToGameScene(ActionEvent event) {
        board.resetBoard();
        gameOverFlag = false;
        noClickAllowed = false;
        discRoot.getChildren().clear();
        winningRoot.getChildren().clear();
        root = makeGameStage();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void restartGame(ActionEvent event) throws IOException {
        System.out.println("Running restartGame");
        switchToGameScene(event);
        switchPlayerTurns(currentPlayer);

    }

    private GridPane makeGameStage() {

        GridPane pane = new GridPane();
        Pane boardWindow = new Pane();


        boardWindow.getChildren().add(discRoot);
        Shape grid = makeGrid();
        boardWindow.getChildren().add(grid);
        boardWindow.getChildren().addAll(makeColumns());
        boardWindow.getChildren().add(winningRoot);

        playerOneCircle = new Ellipse();
        playerOneCircle.setRadiusX(100);
        playerOneCircle.setRadiusY(50);
        playerOneCircle.setFill(Color.YELLOW);
        Label playerOneLabel = new Label();
        playerOneLabel.setText("Player One");
        playerOneLabel.setFont(new Font("Arial", 30));

        playerTwoCircle = new Ellipse();
        playerTwoCircle.setRadiusX(100);
        playerTwoCircle.setRadiusY(50);
        playerTwoCircle.setFill(Color.TRANSPARENT);
        Label playerTwoLabel = new Label();
        playerTwoLabel.setText("Player Two");
        playerTwoLabel.setFont(new Font("Arial", 30));



        pane.add(boardWindow, 0, 0);
        GridPane.setColumnSpan(boardWindow, 2);
        ColumnConstraints column = new ColumnConstraints();
        column.setPercentWidth(50);
        column.setHalignment(HPos.CENTER);
        pane.getColumnConstraints().addAll(column, column);

        RowConstraints row2 = new RowConstraints();
        row2.setMinHeight(150);
        row2.setValignment(VPos.CENTER);
        pane.getRowConstraints().addAll(row2, row2);

        pane.add(playerOneCircle, 0, 1);
        pane.add(playerOneLabel, 0, 1);
        pane.add(playerTwoCircle, 1, 1);
        pane.add(playerTwoLabel, 1, 1);
        //pane.setStyle("-fx-background-color: linear-gradient(to bottom right, #00FF80, #00994C)");
        pane.setStyle(" -fx-background-color: radial-gradient(focus-distance 0%, center 50% 50%, radius 55%, #00CC66, #00994C)");


        return pane;
    }

    private Shape makeGrid() {
        Shape shape = new Rectangle((COLUMNS+1) * TILE_SIZE, (ROWS+1)*TILE_SIZE);

        for (int x = 0; x < COLUMNS; x++) {
            for (int y = 0; y < ROWS; y++) {
                Circle circle = new Circle(TILE_SIZE/2);
                circle.setCenterX(TILE_SIZE/2);
                circle.setCenterY(TILE_SIZE/2);
                circle.setTranslateX(x*(TILE_SIZE+5)+TILE_SIZE/4);
                circle.setTranslateY(y*(TILE_SIZE+5)+TILE_SIZE/4);

                shape = Shape.subtract(shape, circle);
            }
        }

        Light.Distant light = new Light.Distant();
        light.setAzimuth(45.0);
        light.setElevation(30.0);

        Lighting lighting = new Lighting();
        lighting.setLight(light);
        lighting.setSurfaceScale(5.0);


        shape.setFill(Color.BLUE);
        shape.setEffect(lighting);

        return shape;
    }

    private List<Rectangle> makeColumns() {
        List<Rectangle> list = new ArrayList<>();

        for (int x = 0; x < COLUMNS; x++) {
            Rectangle rect = new Rectangle(TILE_SIZE, (ROWS+1)*TILE_SIZE);
            rect.setTranslateX(x *(TILE_SIZE+5) + TILE_SIZE/4);
            rect.setFill((Color.TRANSPARENT));

            rect.setOnMouseEntered(e -> {
                if (!gameOverFlag) {
                    rect.setFill(Color.rgb(150, 150, 150, 0.3));
                }
            });

            rect.setOnMouseExited(e -> rect.setFill(Color.TRANSPARENT));

            final int column = x;
            rect.setOnMouseClicked(e -> placeDisc(new Disc(currentPlayer == 'R'), column));

            list.add(rect);
        }

        return list;
    }

    private void placeDisc(Disc disc, int column) {

        if (noClickAllowed || gameOverFlag) {
            return;
        }

        int row = board.playPiece(column, currentPlayer);

        //visual part
        discRoot.getChildren().add(disc);
        disc.setTranslateX(column * (TILE_SIZE+5) + TILE_SIZE/4);

        final int currentRow = row;

        TranslateTransition animation = new TranslateTransition(Duration.seconds(0.5), disc);
        animation.setToY(row * (TILE_SIZE+5) + TILE_SIZE/4);
        animation.setOnFinished(e -> {
            ArrayList<Point2D> winning_points = board.checkWinner(column, currentRow, currentPlayer);
            if (winning_points.size() >= 4) {
                try {
                    gameOver(winning_points, currentPlayer);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            if (currentPlayer == 'Y') {
                currentPlayer = 'R';
            }
            else if (currentPlayer == 'R') {
                currentPlayer = 'Y';
            }
            switchPlayerTurns(currentPlayer);

            noClickAllowed = false;
        });
        noClickAllowed = true;
        animation.play();
    }

    private void switchPlayerTurns(char player) {
        if (player == 'Y') {
            playerOneCircle.setFill(Color.YELLOW);
            playerTwoCircle.setFill(Color.TRANSPARENT);
        }
        else if (player == 'R') {
            playerOneCircle.setFill(Color.TRANSPARENT);
            playerTwoCircle.setFill(Color.RED);
        }
    }

    private VBox makeGameOverPane(char winningPlayer) {
        VBox pane = new VBox();
        HBox bottomPane = new HBox();

        pane.setPrefHeight(150);
        pane.setSpacing(50);
        pane.setAlignment(Pos.CENTER);
        pane.setStyle("-fx-background-color: #979E69");


        bottomPane.setAlignment(Pos.CENTER);
        bottomPane.setSpacing(20);

        Label winningLabel = new Label();
        String winningText = "";
        if (winningPlayer == 'R') {
            winningText = "Player Two Wins!";
            //pane.setStyle("-fx-background-color: #FF0000");
            pane.setStyle("-fx-background-color: radial-gradient(center 50% 50% , radius 90% , #FF6666, #FF0000)");
        }
        else if (winningPlayer == 'Y') {
            winningText = "Player One Wins!";
            pane.setStyle("-fx-background-color: radial-gradient(center 50% 50% , radius 90% , #FFFF99, #FFFF00)");
            //pane.setStyle("-fx-background-color: #FFFF00");
        }
        winningLabel.setText(winningText);
        winningLabel.setFont(Font.font(25));

        Button playAgain = new Button("Play Again?");
        Button quitButton = new Button("Quit");

        quitButton.setOnAction(e -> System.exit(0));
        playAgain.setOnAction(e -> {
            try {
                restartGame(e);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        pane.getChildren().add(winningLabel);
        bottomPane.getChildren().add(playAgain);
        bottomPane.getChildren().add(quitButton);
        pane.getChildren().add(bottomPane);

        return pane;
    }

    private void gameOver(ArrayList<Point2D> points, char winningPlayer) throws IOException {
        winningRoot.getChildren().addAll(highlightWinningDiscs(points));
        System.out.println("Game over!");

        gameOverFlag = true;
        VBox exitPane = makeGameOverPane(winningPlayer);
        root.add(exitPane, 0, 1, 2, 1);


    }

    private List<WinningDisc> highlightWinningDiscs(ArrayList<Point2D> points) {

        List<WinningDisc> discs = new ArrayList<>();

        for (Point2D point : points){
            WinningDisc disc = new WinningDisc();
            disc.setTranslateX(point.getX()*(TILE_SIZE+5)+TILE_SIZE/4);
            disc.setTranslateY(point.getY()*(TILE_SIZE+5) + TILE_SIZE/4);
            disc.setFill((currentPlayer == 'R' ? Color.RED : Color.YELLOW));
            disc.setRadius(disc.getRadius()*1.2);

            Bloom bloom = new Bloom();
            bloom.setThreshold(0.7);
            disc.setEffect(bloom);

            Light.Distant light = new Light.Distant();
            light.setAzimuth(30.0);
            light.setElevation(90.0);

            Lighting lighting = new Lighting();
            lighting.setLight(light);
            lighting.setSurfaceScale(10.0);

            disc.setEffect(lighting);
            discs.add(disc);

            ScaleTransition animation = new ScaleTransition(Duration.seconds(1), disc);
            animation.setByX(0.2);
            animation.setByY(0.2);

            animation.play();

        }

        return discs;
    }


    private static class Disc extends Circle {
        public Disc(boolean red) {
            super(TILE_SIZE/2, red ? Color.RED : Color.YELLOW);

            setCenterX(TILE_SIZE/2);
            setCenterY(TILE_SIZE/2);
        }
    }

    private static class WinningDisc extends Circle {

        public WinningDisc() {
            super(TILE_SIZE/2);

            setCenterY(TILE_SIZE/2);
            setCenterX(TILE_SIZE/2);
            setFill(Color.SILVER);
        }

    }
}