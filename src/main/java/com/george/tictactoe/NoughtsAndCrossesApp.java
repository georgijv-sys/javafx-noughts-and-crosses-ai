package com.george.tictactoe;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import javafx.application.Application;

public class NoughtsAndCrossesApp extends Application {
    private int size = 3;
    private Button[][] buttons;
    private GameBoard gameBoard;
    private StringProperty xPlayerName = new SimpleStringProperty();
    private StringProperty yPlayerName = new SimpleStringProperty();
    private StringProperty currentPlayerName = new SimpleStringProperty();
    // the label for displaying that there was no name entered, or displaying in the bottom of the starting screen dynamically
    // it is also used for displaying the names of the players in the game
    private Label emptyLabel = new Label("No name entered ");
    private Label turnLabel = new Label();
    private int yWins =0;
    private int xWins=0;
    private int draws = 0;
    private boolean playWithAi = false;
    private AiDifficulty aiDifficulty = AiDifficulty.HARD;


    @Override
    public void start(Stage stage) {
        VBox root = new VBox();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1f2937, #111827); -fx-padding: 30;");
        root.setSpacing(10);

        // choosing the grid size
        Label firstLabel = new Label("Choose grid size:");
        firstLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        TextField sizeField = new TextField();
        sizeField.setPromptText("Default to 3");
        sizeField.setStyle("-fx-background-radius: 8; -fx-padding: 8; -fx-font-size: 14px;");

        // choosing the name for X
        Label secondLabel = new Label("X:   ");
        secondLabel.setStyle("-fx-text-fill: #60a5fa; -fx-font-size: 16px; -fx-font-weight: bold;");
        TextField xName = new TextField();
        xName.setStyle("-fx-background-radius: 8; -fx-padding: 8; -fx-font-size: 14px;");
        HBox line2 = new HBox();
        line2.setSpacing(10);
        line2.getChildren().addAll(secondLabel, xName);

        // choosing the game mode
        Label modeLabel = new Label("Choose game mode:");
        modeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        RadioButton twoPlayersButton = new RadioButton("Two players");
        twoPlayersButton.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        RadioButton aiButton = new RadioButton("Play with AI");
        aiButton.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        ToggleGroup modeGroup = new ToggleGroup();
        twoPlayersButton.setToggleGroup(modeGroup);
        aiButton.setToggleGroup(modeGroup);
        twoPlayersButton.setSelected(true);

        HBox modeLine = new HBox();
        modeLine.setSpacing(15);
        modeLine.getChildren().addAll(twoPlayersButton, aiButton);

        // choosing AI difficulty
        Label difficultyLabel = new Label("AI difficulty:");
        difficultyLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        ComboBox<AiDifficulty> difficultyBox = new ComboBox<>();
        difficultyBox.getItems().addAll(AiDifficulty.EASY, AiDifficulty.MEDIUM, AiDifficulty.HARD);
        difficultyBox.setValue(AiDifficulty.HARD);
        difficultyBox.setDisable(true);
        difficultyBox.setStyle("-fx-background-radius: 8; -fx-padding: 4; -fx-font-size: 14px;");
        HBox difficultyLine = new HBox();
        difficultyLine.setSpacing(10);
        difficultyLine.getChildren().addAll(difficultyLabel, difficultyBox);

        // choosing a name for O
        Label thirdLabel = new Label("O:   ");
        thirdLabel.setStyle("-fx-text-fill: #f87171; -fx-font-size: 16px; -fx-font-weight: bold;");
        TextField yName = new TextField();
        yName.setStyle("-fx-background-radius: 8; -fx-padding: 8; -fx-font-size: 14px;");
        HBox line3 = new HBox();
        line3.setSpacing(10);
        line3.getChildren().addAll(thirdLabel, yName);

        aiButton.setOnAction(e -> {
            yName.setText("AI");
            yName.setDisable(true);
            difficultyBox.setDisable(false);
        });

        twoPlayersButton.setOnAction(e -> {
            yName.clear();
            yName.setDisable(false);
            difficultyBox.setDisable(true);
        });

        Button startButton = new Button("Start");
        startButton.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10 20 10 20;");

        root.getChildren().addAll(firstLabel, sizeField, modeLabel, modeLine, difficultyLine, line2, line3, emptyLabel, startButton);

        // disables the start button until both names are entered
        startButton.disableProperty().bind(
                xName.textProperty().isEmpty().or(yName.textProperty().isEmpty())
        );

        //prints out a message if both names are not entered
        emptyLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> {
                            String xn = xName.getText();
                            String yn = yName.getText();
                            if (xn == null || xn.isBlank() || yn == null || yn.isBlank()) {
                                return "Enter both names ";
                            }
                            return xn + " against " + yn;
                        },
                        xName.textProperty(),
                        yName.textProperty()
                )
        );
        emptyLabel.setStyle("-fx-text-fill: #facc15; -fx-font-size: 14px; -fx-font-weight: bold;");

        turnLabel.textProperty().bind(
                Bindings.concat("Current turn: ", currentPlayerName)
        );

        // if nothing is written set to 3
        // if not a number is written still set to 3
        startButton.setOnAction((e) -> {
            try {
                if (!sizeField.getText().isBlank()) {
                    size = Integer.parseInt(sizeField.getText());
                } else {
                    size = 3;
                }
            } catch (NumberFormatException exception) {
                size = 3;
            }

            playWithAi = aiButton.isSelected();
            aiDifficulty = difficultyBox.getValue();

            xPlayerName.set(xName.getText());
            if (playWithAi) {
                yPlayerName.set("AI");
            } else {
                yPlayerName.set(yName.getText());
            }
            currentPlayerName.set(xPlayerName.get());

            buttons = new Button[size][size];
            gameBoard = new GameBoard(size);

            startMainScreen(stage);
        });

        Scene scene = new Scene(root, 420, 360);
        stage.setTitle("Nought and Crosses");
        stage.setScene(scene);
        stage.show();

    }

    public void startMainScreen(Stage stage2){
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0f172a; -fx-padding: 15;");
        GridPane gridPane = new GridPane();
        gridPane.setStyle("-fx-background-color: #334155; -fx-padding: 10; -fx-hgap: 6; -fx-vgap: 6; -fx-background-radius: 12;");
        gridPane.setMinSize(600,600);
        for(int i = 0; i < buttons.length; i++){
            for(int j = 0; j < buttons[i].length; j++){
                Button button = new Button();
                button.setStyle("-fx-background-color: #e5e7eb; -fx-background-radius: 10; -fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #111827;");
                button.setMinSize(600.0 / size, 600.0 / size);
                final int finalRow = i;
                final int finalCol = j;
                button.setOnAction(e -> handleMove(stage2, button, finalRow, finalCol));
                buttons[i][j] = button;
                gridPane.add(button, j, i);
            }
        }
        Button scoreButton = new Button("Score");
        scoreButton.setStyle("-fx-background-color: #38bdf8; -fx-text-fill: #0f172a; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 14 8 14;");
        scoreButton.setOnAction(e -> showScorePopup());
        HBox bottomHBox = new HBox();
        bottomHBox.setSpacing(20);
        bottomHBox.setStyle("-fx-background-color: #1e293b; -fx-padding: 12; -fx-background-radius: 10;");
        bottomHBox.getChildren().addAll(turnLabel, scoreButton);

        turnLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        emptyLabel.setStyle("-fx-text-fill: #cbd5e1; -fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 0 0 12 0;");
        root.setCenter(gridPane);
        root.setTop(emptyLabel);
        root.setBottom(bottomHBox);
        Scene primaryScene = new Scene(root, 650, 720);
        stage2.setScene(primaryScene);
    }

    public void startLastScreen(Stage stage2, String winner){
        Stage popupStage = new Stage();
        popupStage.setTitle("Game Over");
        VBox root = new VBox();
        root.setSpacing(10);
        root.setStyle("-fx-padding: 25; -fx-background-color: #111827;");

        Label winnerLabel = new Label();
        if (winner.equals("Draw")){
            draws++;
            winnerLabel = new Label("Draw!");
        } else {
            if (winner.equals("X")){
                xWins++;
            } else {
                yWins++;
            }
            winnerLabel = new Label(getWinnerName(winner) + " wins!");
        }
        winnerLabel.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");
        Button okButton = new Button("Ok");
        okButton.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 18 8 18;");
        okButton.setOnAction((e) -> {
            resetGameBoard();
            popupStage.close();
        });

        root.getChildren().addAll(winnerLabel, okButton);

        Scene scene = new Scene(root, 250, 120);
        popupStage.setScene(scene);
        popupStage.show();
    }

    private void resetGameBoard(){
        gameBoard.resetBoard();

        for(int i = 0; i < buttons.length; i++){
            for(int j = 0; j < buttons[i].length; j++){
                buttons[i][j].setText("");
            }
        }
        updateCurrentPlayerName();
    }

    private String getWinnerName(String winner){
        if (winner.equals("X")){
            return xPlayerName.get();
        }
        return yPlayerName.get();
    }




    private void handleMove(Stage stage, Button button, int row, int col){
        if(!button.getText().equals("")){
            return;
        }

        if (playWithAi && gameBoard.getCurrentPlayer().equals("O")) {
            return;
        }

        gameBoard.makeMove(row,col);
        button.setText(gameBoard.getCurrentPlayer());

        if (finishGameIfNeeded(stage)) {
            return;
        }

        gameBoard.switchPlayer();
        updateCurrentPlayerName();

        if (playWithAi) {
            makeAiMove(stage);
        }
    }

    private boolean finishGameIfNeeded(Stage stage) {
        String winner = gameBoard.checkWinner();

        if (!winner.equals("")){
            startLastScreen(stage, winner);
            return true;
        } else if (gameBoard.isBoardFull()) {
            startLastScreen(stage, "Draw");
            return true;
        }

        return false;
    }

    private void makeAiMove(Stage stage) {
        int[] aiMove = gameBoard.findBestMoveForCurrentPlayer(aiDifficulty);

        if (aiMove == null) {
            return;
        }

        int row = aiMove[0];
        int col = aiMove[1];

        gameBoard.makeMove(row, col);
        buttons[row][col].setText(gameBoard.getCurrentPlayer());

        if (finishGameIfNeeded(stage)) {
            return;
        }

        gameBoard.switchPlayer();
        updateCurrentPlayerName();
    }

    private void updateCurrentPlayerName() {
        if (gameBoard.getCurrentPlayer().equals("X")) {
            currentPlayerName.set(xPlayerName.get());
        } else {
            currentPlayerName.set(yPlayerName.get());
        }
    }

    private void showScorePopup(){
        Stage scoreStage = new Stage();
        scoreStage.setTitle("Score");
        VBox root = new VBox();
        root.setSpacing(10);
        root.setStyle("-fx-padding: 25; -fx-background-color: #111827;");
        Label xScoreLabel = new Label(xPlayerName.get() + " wins: " + xWins);
        xScoreLabel.setStyle("-fx-text-fill: #60a5fa; -fx-font-size: 16px; -fx-font-weight: bold;");
        Label yScoreLabel = new Label(yPlayerName.get() + " wins: " + yWins);
        yScoreLabel.setStyle("-fx-text-fill: #f87171; -fx-font-size: 16px; -fx-font-weight: bold;");
        Label drawScoreLabel = new Label("Draw: " + draws);
        drawScoreLabel.setStyle("-fx-text-fill: #facc15; -fx-font-size: 16px; -fx-font-weight: bold;");
        Button okButton = new Button("Ok");
        okButton.setStyle("-fx-background-color: #38bdf8; -fx-text-fill: #0f172a; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 18 8 18;");
        okButton.setOnAction((e) -> {
            scoreStage.close();
        });

        root.getChildren().addAll(xScoreLabel, yScoreLabel, drawScoreLabel, okButton);
        Scene scene = new Scene(root, 250, 150);
        scoreStage.setScene(scene);
        scoreStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }


}
