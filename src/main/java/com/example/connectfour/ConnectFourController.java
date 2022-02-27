package com.example.connectfour;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
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
import java.util.Random;

public class ConnectFourController {
    public Button quitButton;
    public Button twoPlayerButton;
    public Button onePlayerButton;
    @FXML
    private Label welcomeText;
    private GridPane root;

    private static final int COLUMNS = 7;
    private static final int ROWS = 6;
    private static final double TILE_SIZE = 80;
    private static final int PLAYER_ONE_TURN = 0;
    private static final int PLAYER_TWO_TURN = 1;

    private boolean noClickAllowed = false; //to prevent playing another piece of the same color before previous move complete.
    private boolean gameOverFlag = false;
    private boolean onePlayerGame = false;

    protected Player playerOne;
    protected Player playerTwo;
    protected Robot robotPlayerOne;
    protected Robot robotPlayerTwo;

    private int currentPlayer;
    private final Board board = new Board();
    private final Pane discRoot = new Pane();
    private final Pane winningRoot = new Pane();
    private Ellipse playerOneCircle;
    private Ellipse playerTwoCircle;


    public void setPlayerOne(Player player) {
        this.playerOne = player;
    }

    public void setPlayerTwo(Player player) {
        this.playerTwo = player;
    }

    public void setRobotPlayerOne(Robot robot){
        this.robotPlayerOne = robot;
    }

    public void setRobotPlayerTwo(Robot robot){
        this.robotPlayerTwo = robot;
    }

    @FXML
    public static void initialGameScreen(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(ConnectFourApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Welcome to Connect Four");
        stage.setScene(scene);
        stage.getIcons().add(new Image(String.valueOf(ConnectFourApplication.class.getResource("images/Icon.jpg"))));
        stage.setWidth(200);
        stage.setHeight(400);

        stage.show();
    }

    @FXML
    protected void onQuitButtonClick() {
        welcomeText.setText("Quitting");
        System.exit(0);
    }

    @FXML
    public void switchToGameScene(ActionEvent event) {
        board.resetBoard();
        Random rand = new Random();
        gameOverFlag = false;
        noClickAllowed = false;
        discRoot.getChildren().clear();
        winningRoot.getChildren().clear();
        root = makeGameStage();
        currentPlayer = rand.nextInt(2);
        switchPlayerTurns(currentPlayer);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setWidth((COLUMNS+1)*TILE_SIZE);
        stage.setHeight((ROWS+1)*TILE_SIZE + 200);
        stage.show();
    }

    @FXML
    public void switchToOnePlayerScene(ActionEvent event) throws  IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ConnectFourApplication.class.getResource("oneplayer-view.fxml"));
        Parent root = fxmlLoader.load();

        OnePlayerViewController onePlayerViewController = fxmlLoader.getController();
        onePlayerViewController.injectController(this);

        onePlayerGame = true;

        Scene scene = new Scene(root, 300, 400);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Player Information");
        stage.setScene(scene);
        stage.getIcons().add(new Image(String.valueOf(ConnectFourApplication.class.getResource("images/Icon.jpg"))));
        stage.setWidth(300);
        stage.setHeight(300);


        stage.show();
    }

    @FXML
    public void switchToTwoPlayerScene(ActionEvent event) throws  IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ConnectFourApplication.class.getResource("twoplayer-view.fxml"));
        Parent root = fxmlLoader.load();

        TwoPlayerViewController twoPlayerViewController = fxmlLoader.getController();
        twoPlayerViewController.injectController(this);

        onePlayerGame = false;

        Scene scene = new Scene(root, 300, 400);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Player Information");
        stage.setScene(scene);
        stage.getIcons().add(new Image(String.valueOf(ConnectFourApplication.class.getResource("images/Icon.jpg"))));
        stage.setWidth(650);
        stage.setHeight(300);


        stage.show();
    }

    public void restartGame(ActionEvent event) throws IOException {
        System.out.println("Running restartGame");
        switchToGameScene(event);
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
        playerOneLabel.setText(playerOne.getName());
        playerOneLabel.setFont(new Font("Arial", 30));

        playerTwoCircle = new Ellipse();
        playerTwoCircle.setRadiusX(100);
        playerTwoCircle.setRadiusY(50);
        playerTwoCircle.setFill(Color.TRANSPARENT);
        Label playerTwoLabel = new Label();
        if (onePlayerGame) {
            playerTwoLabel.setText(robotPlayerTwo.getName());
        }
        else {
            playerTwoLabel.setText(playerTwo.getName());
        }
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

//        Button scoreButton = new Button("Get Position Score");
//        scoreButton.setOnAction(e -> System.out.println("Position score: " + board.getPositionScore(1)));
//        pane.add(scoreButton, 0, 2);


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
            rect.setOnMouseClicked(e -> placeDisc(new Disc(getDiscColor(currentPlayer)), column));

            list.add(rect);
        }

        return list;
    }

    private Color getDiscColor(int player)
    {
        if (player == PLAYER_TWO_TURN) {
            if (onePlayerGame) {
                return robotPlayerTwo.getColor();
            }
            else
            {
                return playerTwo.getColor();
            }
        }

        return playerOne.getColor();
    }

    private void placeDisc(Disc disc, int column) {

        if (noClickAllowed || gameOverFlag || !board.getAvailableColumns().contains(column)) {
            return;
        }

        int row = board.playPiece(column, currentPlayer);
        board.showBoard();

        //visual part
        discRoot.getChildren().add(disc);
        disc.setTranslateX(column * (TILE_SIZE+5) + TILE_SIZE/4);

        final int currentRow = row;

        TranslateTransition animation = new TranslateTransition(Duration.seconds(0.5), disc);
        animation.setToY(row * (TILE_SIZE+5) + TILE_SIZE/4);
        animation.setOnFinished(e -> {
            ArrayList<Point2D> winning_points = board.checkWinner(column, currentRow, currentPlayer);
            if (!winning_points.isEmpty()) {
                try {
                    gameOver(winning_points, currentPlayer);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            else if (board.getAvailableColumns().isEmpty()) {
                System.out.println("Should be game over Tied");
                VBox exitPane = makeGameOverPane(3);
                root.add(exitPane, 0, 1, 2, 1);
            }
            else {

                if (currentPlayer == PLAYER_ONE_TURN) {
                    currentPlayer = PLAYER_TWO_TURN;
                } else if (currentPlayer == PLAYER_TWO_TURN) {
                    currentPlayer = PLAYER_ONE_TURN;
                }
                noClickAllowed = false;
                switchPlayerTurns(currentPlayer);
            }
        });
        noClickAllowed = true;
        animation.play();
    }

    private void doRobotPlay() {
        int column = robotPlayerTwo.playColumn(board);
        System.out.println("Robot playing to Column " + column);
        placeDisc(new Disc(robotPlayerTwo.getColor()), column);
    }


    private void switchPlayerTurns(int player) {
        if (player == PLAYER_ONE_TURN) {
            playerOneCircle.setFill(playerOne.getColor());
            playerTwoCircle.setFill(Color.TRANSPARENT);
        }
        else if (player == PLAYER_TWO_TURN) {
            playerOneCircle.setFill(Color.TRANSPARENT);
            if (onePlayerGame) {
                playerTwoCircle.setFill(robotPlayerTwo.getColor());
                System.out.println("Robot turn");
                if (!board.getWinState()) {
                    doRobotPlay();
                }
            }
            else {
                playerTwoCircle.setFill(playerTwo.getColor());
            }
        }
    }

    private VBox makeGameOverPane(int winningPlayer) {
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
        if (winningPlayer == PLAYER_TWO_TURN) {
            if (onePlayerGame) {
                winningText = robotPlayerTwo.getName() + " Wins!";
            }
            else {
                winningText = playerTwo.getName() + " Wins!";
            }
            //pane.setStyle("-fx-background-color: #FF0000");
            pane.setStyle("-fx-background-color: radial-gradient(center 50% 50% , radius 90% , #FF6666, #FF0000)");
        }
        else if (winningPlayer == PLAYER_ONE_TURN) {
            winningText = playerOne.getName() + " Wins!";
            pane.setStyle("-fx-background-color: radial-gradient(center 50% 50% , radius 90% , #FFFF99, #FFFF00)");
            //pane.setStyle("-fx-background-color: #FFFF00");
        }
        else if (winningPlayer == 3) { // board filled with no winners
            winningText = "Tie Game!";
            pane.setStyle(" -fx-background-color: radial-gradient(focus-distance 0%, center 50% 50%, radius 55%, #00CC66, #00994C)");
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

    private void gameOver(ArrayList<Point2D> points, int winningPlayer) throws IOException {
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
            disc.setFill(getDiscColor(currentPlayer));
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
        public Disc(Color color) {
            super(TILE_SIZE/2, color);

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