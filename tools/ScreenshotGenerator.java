import javafx.application.Application;
import javafx.application.Platform;
import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Standalone driver that rebuilds each NoughtsAndCrossesApp screen with the exact
 * same inline styles used by the real app, then saves clean high-resolution PNGs
 * via JavaFX snapshot(). Used only to produce README screenshots; not part of the game.
 *
 * Launcher class (does not extend Application) so JavaFX can be launched from the
 * classpath without the modular "JavaFX runtime components are missing" error.
 */
public class ScreenshotGenerator {
    public static void main(String[] args) {
        Application.launch(SnapshotApp.class, args);
    }

    public static class SnapshotApp extends Application {
        private static final double SCALE = 2.0; // retina-quality output
        private final List<Shot> shots = new ArrayList<>();
        private int index = 0;
        private Stage stage;

        private static final class Shot {
            final String file;
            final Node root;
            final double w;
            final double h;
            Shot(String file, Node root, double w, double h) {
                this.file = file; this.root = root; this.w = w; this.h = h;
            }
        }

        @Override
        public void start(Stage stage) {
            this.stage = stage;
            // keep the helper window off the visible desktop while snapshotting
            stage.setX(-5000);
            stage.setY(-5000);

            // a touch taller than the app's default 360px window so the whole
            // resizable setup form (including the Start button) is fully visible
            shots.add(new Shot("setup.png", buildSetupScreen(), 420, 440));
            shots.add(new Shot("gameplay.png", buildBoardScreen(), 650, 720));
            shots.add(new Shot("game-over.png", buildGameOverPopup(), 250, 120));
            shots.add(new Shot("score.png", buildScorePopup(), 250, 150));

            new File("docs/screenshots").mkdirs();
            showNext();
        }

        private void showNext() {
            if (index >= shots.size()) {
                Platform.exit();
                return;
            }
            Shot shot = shots.get(index);
            Scene scene = new Scene((javafx.scene.Parent) shot.root, shot.w, shot.h);
            stage.setScene(scene);
            stage.show();

            // let CSS + layout + font rendering settle, then snapshot and continue
            PauseTransition pause = new PauseTransition(Duration.millis(300));
            pause.setOnFinished(e -> {
                capture(shot);
                index++;
                showNext();
            });
            pause.play();
        }

        private void capture(Shot shot) {
            SnapshotParameters params = new SnapshotParameters();
            params.setTransform(Transform.scale(SCALE, SCALE));
            int w = (int) Math.round(shot.w * SCALE);
            int h = (int) Math.round(shot.h * SCALE);
            WritableImage image = new WritableImage(w, h);
            shot.root.snapshot(params, image);

            BufferedImage buffered = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            PixelReader reader = image.getPixelReader();
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    buffered.setRGB(x, y, reader.getArgb(x, y));
                }
            }
            try {
                File out = new File("docs/screenshots/" + shot.file);
                ImageIO.write(buffered, "png", out);
                System.out.println("Saved " + out.getPath() + " (" + w + "x" + h + ")");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // ---- Screen 1: setup screen (Play-with-AI mode, Hard difficulty) ----
        private Node buildSetupScreen() {
            VBox root = new VBox();
            root.setStyle("-fx-background-color: linear-gradient(to bottom, #1f2937, #111827); -fx-padding: 30;");
            root.setSpacing(10);

            Label firstLabel = new Label("Choose grid size:");
            firstLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
            TextField sizeField = new TextField();
            sizeField.setPromptText("Default to 3");
            sizeField.setText("3");
            sizeField.setStyle("-fx-background-radius: 8; -fx-padding: 8; -fx-font-size: 14px;");

            Label modeLabel = new Label("Choose game mode:");
            modeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

            RadioButton twoPlayersButton = new RadioButton("Two players");
            twoPlayersButton.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            RadioButton aiButton = new RadioButton("Play with AI");
            aiButton.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            ToggleGroup modeGroup = new ToggleGroup();
            twoPlayersButton.setToggleGroup(modeGroup);
            aiButton.setToggleGroup(modeGroup);
            aiButton.setSelected(true);
            HBox modeLine = new HBox();
            modeLine.setSpacing(15);
            modeLine.getChildren().addAll(twoPlayersButton, aiButton);

            Label difficultyLabel = new Label("AI difficulty:");
            difficultyLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
            ComboBox<String> difficultyBox = new ComboBox<>();
            difficultyBox.getItems().addAll("Easy", "Medium", "Hard");
            difficultyBox.setValue("Hard");
            difficultyBox.setStyle("-fx-background-radius: 8; -fx-padding: 4; -fx-font-size: 14px;");
            HBox difficultyLine = new HBox();
            difficultyLine.setSpacing(10);
            difficultyLine.getChildren().addAll(difficultyLabel, difficultyBox);

            Label secondLabel = new Label("X:   ");
            secondLabel.setStyle("-fx-text-fill: #60a5fa; -fx-font-size: 16px; -fx-font-weight: bold;");
            TextField xName = new TextField("Alice");
            xName.setStyle("-fx-background-radius: 8; -fx-padding: 8; -fx-font-size: 14px;");
            HBox line2 = new HBox();
            line2.setSpacing(10);
            line2.getChildren().addAll(secondLabel, xName);

            Label thirdLabel = new Label("O:   ");
            thirdLabel.setStyle("-fx-text-fill: #f87171; -fx-font-size: 16px; -fx-font-weight: bold;");
            TextField yName = new TextField("AI");
            yName.setDisable(true);
            yName.setStyle("-fx-background-radius: 8; -fx-padding: 8; -fx-font-size: 14px;");
            HBox line3 = new HBox();
            line3.setSpacing(10);
            line3.getChildren().addAll(thirdLabel, yName);

            Label emptyLabel = new Label("Alice against AI");
            emptyLabel.setStyle("-fx-text-fill: #facc15; -fx-font-size: 14px; -fx-font-weight: bold;");

            Button startButton = new Button("Start");
            startButton.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10 20 10 20;");

            root.getChildren().addAll(firstLabel, sizeField, modeLabel, modeLine, difficultyLine, line2, line3, emptyLabel, startButton);
            return root;
        }

        // ---- Screen 2: in-progress 3x3 board ----
        private Node buildBoardScreen() {
            int size = 3;
            // a realistic mid-game position; X (Alice) to move, no winner yet
            String[][] cells = {
                {"X", "",  "O"},
                {"",  "X", "O"},
                {"",  "",  ""}
            };

            BorderPane root = new BorderPane();
            root.setStyle("-fx-background-color: #0f172a; -fx-padding: 15;");
            GridPane gridPane = new GridPane();
            gridPane.setStyle("-fx-background-color: #334155; -fx-padding: 10; -fx-hgap: 6; -fx-vgap: 6; -fx-background-radius: 12;");
            gridPane.setMinSize(600, 600);
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    Button button = new Button(cells[i][j]);
                    button.setStyle("-fx-background-color: #e5e7eb; -fx-background-radius: 10; -fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #111827;");
                    button.setMinSize(600.0 / size, 600.0 / size);
                    gridPane.add(button, j, i);
                }
            }

            Label turnLabel = new Label("Current turn: Alice");
            turnLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
            Button scoreButton = new Button("Score");
            scoreButton.setStyle("-fx-background-color: #38bdf8; -fx-text-fill: #0f172a; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 14 8 14;");
            HBox bottomHBox = new HBox();
            bottomHBox.setSpacing(20);
            bottomHBox.setStyle("-fx-background-color: #1e293b; -fx-padding: 12; -fx-background-radius: 10;");
            bottomHBox.getChildren().addAll(turnLabel, scoreButton);

            Label topLabel = new Label("Alice against AI");
            topLabel.setStyle("-fx-text-fill: #cbd5e1; -fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 0 0 12 0;");

            root.setCenter(gridPane);
            root.setTop(topLabel);
            root.setBottom(bottomHBox);
            return root;
        }

        // ---- Screen 3: game over popup ----
        private Node buildGameOverPopup() {
            VBox root = new VBox();
            root.setSpacing(10);
            root.setStyle("-fx-padding: 25; -fx-background-color: #111827;");
            Label winnerLabel = new Label("Alice wins!");
            winnerLabel.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");
            Button okButton = new Button("Ok");
            okButton.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 18 8 18;");
            root.getChildren().addAll(winnerLabel, okButton);
            return root;
        }

        // ---- Screen 4: score popup ----
        private Node buildScorePopup() {
            VBox root = new VBox();
            root.setSpacing(10);
            root.setStyle("-fx-padding: 25; -fx-background-color: #111827;");
            Label xScoreLabel = new Label("Alice wins: 2");
            xScoreLabel.setStyle("-fx-text-fill: #60a5fa; -fx-font-size: 16px; -fx-font-weight: bold;");
            Label yScoreLabel = new Label("AI wins: 1");
            yScoreLabel.setStyle("-fx-text-fill: #f87171; -fx-font-size: 16px; -fx-font-weight: bold;");
            Label drawScoreLabel = new Label("Draw: 1");
            drawScoreLabel.setStyle("-fx-text-fill: #facc15; -fx-font-size: 16px; -fx-font-weight: bold;");
            Button okButton = new Button("Ok");
            okButton.setStyle("-fx-background-color: #38bdf8; -fx-text-fill: #0f172a; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 18 8 18;");
            root.getChildren().addAll(xScoreLabel, yScoreLabel, drawScoreLabel, okButton);
            return root;
        }
    }
}
