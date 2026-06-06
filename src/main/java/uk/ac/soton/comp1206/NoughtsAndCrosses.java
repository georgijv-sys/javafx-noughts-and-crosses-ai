package uk.ac.soton.comp1206;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;


public class NoughtsAndCrosses extends Application {

    private Button[][] buttons = new Button[3][3];
    private boolean turn = true;
    private Label winlab = new Label();
    private String winner;
    private int xWin = 0;
    private int yWin = 0;
    private Label titlelab = new Label();





    @Override
    public void start(Stage stage) {
        GridPane grid = new GridPane();
        grid.setMinSize(300, 300);
        titlelab.setText("X Wins: " + xWin + " | Y Wins: " + yWin + " ");


        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                Button button = new Button();
                button.setMinSize(150, 150);
                button.setOnAction(new ButtonClickHandler(i, j));
                buttons[i][j] = button;
                grid.add(button, i, j); // here i and j specify the position of a button
            }
        }

        HBox topBar = new HBox(15);
        Button reset = new Button("Reset");
        topBar.getChildren().addAll(titlelab, reset);
        reset.setOnAction(new ResetClickHandler());


        BorderPane root = new BorderPane();
        root.setCenter(grid);
        Scene scene = new Scene(root, 600, 600);
        root.setTop(topBar);
        root.setBottom(winlab);
        stage.setScene(scene);
        stage.setTitle("Noughts and Crosses");
        stage.show();





    }
    public String checkWinner() {
        for  (int i = 0; i < 3; i++) {
            String a = buttons[i][0].getText();
            String b = buttons[i][1].getText();
            String c = buttons[i][2].getText();
            if(a.equals(b) && b.equals(c) && !a.isEmpty()) {
                winner = buttons[i][0].getText();
                return winner;
            }
        }
        for  (int i = 0; i < 3; i++) {
            String a = buttons[0][i].getText();
            String b = buttons[1][i].getText();
            String c = buttons[2][i].getText();

            if(a.equals(b) && b.equals(c) && !a.isEmpty()) {
                winner = buttons[0][i].getText();
                return winner;
            }
        }
        String center = buttons[1][1].getText();

        if (!center.isEmpty()) {
            if (center.equals(buttons[0][0].getText()) &&
                    center.equals(buttons[2][2].getText())) {
                winner = buttons[0][0].getText();
                return winner;
            }

            if (center.equals(buttons[0][2].getText()) &&
                    center.equals(buttons[2][0].getText())) {
                winner = buttons[0][2].getText();
                return winner;
            }
        }
        return "none";
    }

    private class ButtonClickHandler implements EventHandler<ActionEvent> {
        private final int i;
        private final int j;


        public ButtonClickHandler(int i, int j) {
            this.i = i;
            this.j = j;
        }

        public void handle(ActionEvent event) {
            if (turn) {
                buttons[i][j].setText("X");
                turn = false;
            } else {
                buttons[i][j].setText("O");
                turn = true;
            }

            if (checkWinner().equals("X") || checkWinner().equals("O")) {
                winlab.setText(winner + " Won");

                if (winner.equals("X")) {
                    xWin++;
                } else if (winner.equals("O")) {
                    yWin++;
                }
                titlelab.setText("X Wins: " + xWin + " | Y Wins: " + yWin + " ");
            }
        }
    }
    private class ResetClickHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            for(int i = 0; i < buttons.length; i++) {
                for(int j = 0; j < buttons[i].length; j++) {
                    buttons[i][j].setText("");
                }
            }
        }
    }


    public static void main(String[] args) {
        launch();
    }

}