package com.george.tictactoe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class ComputerPlayer {

    private static final int WIN_SCORE = 100_000;
    private Random random = new Random();

    public int[] chooseMove(GameBoard board, String aiPlayer, AiDifficulty difficulty) {
        List<int[]> emptyCells = board.findEmptyCells();

        if (emptyCells.isEmpty()) {
            return null;
        }

        if (difficulty == AiDifficulty.EASY) {
            return emptyCells.get(random.nextInt(emptyCells.size()));
        }

        if (difficulty == AiDifficulty.MEDIUM) {
            return chooseMediumMove(board, aiPlayer);
        }

        if (board.getSize() == 3) {
            return chooseMinimaxMove(board, aiPlayer);
        }

        return chooseHeuristicMove(board, aiPlayer);
    }

    private int[] chooseMediumMove(GameBoard board, String aiPlayer) {
        int[] winningMove = findMoveThatWouldWin(board, aiPlayer);

        if (winningMove != null) {
            return winningMove;
        }

        int[] blockingMove = findMoveThatWouldWin(board, opponent(aiPlayer));

        if (blockingMove != null) {
            return blockingMove;
        }

        int[] centerMove = findCenterMove(board);

        if (centerMove != null) {
            return centerMove;
        }

        int[] cornerMove = findCornerMove(board);

        if (cornerMove != null) {
            return cornerMove;
        }

        return board.findEmptyCells().get(0);
    }

    private int[] chooseMinimaxMove(GameBoard board, String aiPlayer) {
        int[] bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        for (int[] move : orderedMoves(board)) {
            board.makeMoveForPlayer(move[0], move[1], aiPlayer);
            int score = minimax(board, opponent(aiPlayer), aiPlayer, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            board.clearCell(move[0], move[1]);

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private int minimax(GameBoard board, String playerToMove, String aiPlayer, int depth, int alpha, int beta) {
        String winner = board.checkWinner();

        if (winner.equals(aiPlayer)) {
            return 10 - depth;
        }

        if (winner.equals(opponent(aiPlayer))) {
            return depth - 10;
        }

        if (board.isBoardFull()) {
            return 0;
        }

        if (playerToMove.equals(aiPlayer)) {
            int bestScore = Integer.MIN_VALUE;

            for (int[] move : orderedMoves(board)) {
                board.makeMoveForPlayer(move[0], move[1], playerToMove);
                bestScore = Math.max(bestScore, minimax(board, opponent(playerToMove), aiPlayer, depth + 1, alpha, beta));
                board.clearCell(move[0], move[1]);
                alpha = Math.max(alpha, bestScore);

                if (beta <= alpha) {
                    break;
                }
            }

            return bestScore;
        }

        int bestScore = Integer.MAX_VALUE;

        for (int[] move : orderedMoves(board)) {
            board.makeMoveForPlayer(move[0], move[1], playerToMove);
            bestScore = Math.min(bestScore, minimax(board, opponent(playerToMove), aiPlayer, depth + 1, alpha, beta));
            board.clearCell(move[0], move[1]);
            beta = Math.min(beta, bestScore);

            if (beta <= alpha) {
                break;
            }
        }

        return bestScore;
    }

    private int[] chooseHeuristicMove(GameBoard board, String aiPlayer) {
        int[] bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        for (int[] move : orderedMoves(board)) {
            board.makeMoveForPlayer(move[0], move[1], aiPlayer);
            int score = scoreBoard(board, aiPlayer) - strongestOpponentReply(board, aiPlayer) + positionScore(board, move);
            String winner = board.checkWinner();
            board.clearCell(move[0], move[1]);

            if (winner.equals(aiPlayer)) {
                score = WIN_SCORE;
            }

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private int strongestOpponentReply(GameBoard board, String aiPlayer) {
        String opponent = opponent(aiPlayer);
        int strongestReply = 0;

        for (int[] move : board.findEmptyCells()) {
            board.makeMoveForPlayer(move[0], move[1], opponent);

            if (board.checkWinner().equals(opponent)) {
                strongestReply = WIN_SCORE;
            } else {
                strongestReply = Math.max(strongestReply, scoreBoard(board, opponent));
            }

            board.clearCell(move[0], move[1]);

            if (strongestReply == WIN_SCORE) {
                break;
            }
        }

        return strongestReply;
    }

    private int[] findMoveThatWouldWin(GameBoard board, String player) {
        for (int[] move : board.findEmptyCells()) {
            board.makeMoveForPlayer(move[0], move[1], player);
            boolean moveWins = board.checkWinner().equals(player);
            board.clearCell(move[0], move[1]);

            if (moveWins) {
                return move;
            }
        }

        return null;
    }

    private int[] findCenterMove(GameBoard board) {
        int center = board.getSize() / 2;

        if (board.getSize() % 2 == 1 && board.getCell(center, center).equals("")) {
            return new int[]{center, center};
        }

        return null;
    }

    private int[] findCornerMove(GameBoard board) {
        int last = board.getSize() - 1;
        int[][] corners = {
                {0, 0},
                {0, last},
                {last, 0},
                {last, last}
        };

        for (int[] corner : corners) {
            if (board.getCell(corner[0], corner[1]).equals("")) {
                return corner;
            }
        }

        return null;
    }

    private List<int[]> orderedMoves(GameBoard board) {
        List<int[]> moves = new ArrayList<>(board.findEmptyCells());
        Collections.sort(moves, Comparator.comparingInt((int[] move) -> positionScore(board, move)).reversed());
        return moves;
    }

    private int scoreBoard(GameBoard board, String player) {
        int score = 0;
        int size = board.getSize();

        for (int row = 0; row < size; row++) {
            score += scoreRow(board, player, row);
        }

        for (int col = 0; col < size; col++) {
            score += scoreColumn(board, player, col);
        }

        score += scoreMainDiagonal(board, player);
        score += scoreOppositeDiagonal(board, player);
        return score;
    }

    private int scoreRow(GameBoard board, String player, int row) {
        int ownCells = 0;
        int opponentCells = 0;

        for (int col = 0; col < board.getSize(); col++) {
            String cell = board.getCell(row, col);

            if (cell.equals(player)) {
                ownCells++;
            } else if (cell.equals(opponent(player))) {
                opponentCells++;
            }
        }

        return scoreLine(ownCells, opponentCells);
    }

    private int scoreColumn(GameBoard board, String player, int col) {
        int ownCells = 0;
        int opponentCells = 0;

        for (int row = 0; row < board.getSize(); row++) {
            String cell = board.getCell(row, col);

            if (cell.equals(player)) {
                ownCells++;
            } else if (cell.equals(opponent(player))) {
                opponentCells++;
            }
        }

        return scoreLine(ownCells, opponentCells);
    }

    private int scoreMainDiagonal(GameBoard board, String player) {
        int ownCells = 0;
        int opponentCells = 0;

        for (int i = 0; i < board.getSize(); i++) {
            String cell = board.getCell(i, i);

            if (cell.equals(player)) {
                ownCells++;
            } else if (cell.equals(opponent(player))) {
                opponentCells++;
            }
        }

        return scoreLine(ownCells, opponentCells);
    }

    private int scoreOppositeDiagonal(GameBoard board, String player) {
        int ownCells = 0;
        int opponentCells = 0;
        int size = board.getSize();

        for (int i = 0; i < size; i++) {
            String cell = board.getCell(i, size - 1 - i);

            if (cell.equals(player)) {
                ownCells++;
            } else if (cell.equals(opponent(player))) {
                opponentCells++;
            }
        }

        return scoreLine(ownCells, opponentCells);
    }

    private int scoreLine(int ownCells, int opponentCells) {
        if (ownCells > 0 && opponentCells > 0) {
            return 0;
        }

        if (ownCells > 0) {
            return (int) Math.pow(10, ownCells);
        }

        if (opponentCells > 0) {
            return -(int) Math.pow(9, opponentCells);
        }

        return 1;
    }

    private int positionScore(GameBoard board, int[] move) {
        double center = (board.getSize() - 1) / 2.0;
        double distance = Math.abs(move[0] - center) + Math.abs(move[1] - center);
        boolean corner = (move[0] == 0 || move[0] == board.getSize() - 1)
                && (move[1] == 0 || move[1] == board.getSize() - 1);

        return (int) Math.round(100 - (distance * 12)) + (corner ? 8 : 0);
    }

    private String opponent(String player) {
        if (player.equals("X")) {
            return "O";
        }

        return "X";
    }
}
