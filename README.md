# Tic Tac Toe JavaFX

A JavaFX Noughts and Crosses game with configurable board sizes, player names, score tracking, and AI difficulty levels.

## Features

- JavaFX desktop UI.
- Human-vs-human and human-vs-AI modes.
- Easy, Medium, and Hard AI difficulties.
- Hard AI uses minimax with alpha-beta pruning on the classic 3x3 board.
- Larger boards use heuristic scoring so the AI stays responsive.
- Maven-based build and test workflow.

## Run

```bash
mvn javafx:run
```

## Test

```bash
mvn test
```

## Project Structure

```text
src/main/java/com/george/tictactoe/
  AiDifficulty.java
  ComputerPlayer.java
  GameBoard.java
  NoughtsAndCrossesApp.java

src/test/java/com/george/tictactoe/
  ComputerPlayerTest.java
```

## Technical Notes

The UI keeps the original project design and JavaFX layout. The main improvement is inside the AI:

- Easy chooses a random legal move.
- Medium looks for immediate wins, blocks immediate losses, then chooses strong board positions.
- Hard uses minimax with alpha-beta pruning for perfect 3x3 play.
- For larger boards, Hard switches to heuristic scoring because full minimax becomes too expensive.

The README is intentionally short and practical, following the style of professional repositories: explain what the project does, show how to run it, and highlight the engineering decisions that matter.
