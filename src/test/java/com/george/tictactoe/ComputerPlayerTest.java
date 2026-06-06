package com.george.tictactoe;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class ComputerPlayerTest {

    @Test
    void hardAiTakesImmediateWin() {
        GameBoard board = new GameBoard(3);
        ComputerPlayer computerPlayer = new ComputerPlayer();

        board.makeMoveForPlayer(0, 0, "O");
        board.makeMoveForPlayer(0, 1, "O");
        board.makeMoveForPlayer(1, 0, "X");

        int[] move = computerPlayer.chooseMove(board, "O", AiDifficulty.HARD);

        assertArrayEquals(new int[]{0, 2}, move);
    }

    @Test
    void hardAiBlocksImmediateLoss() {
        GameBoard board = new GameBoard(3);
        ComputerPlayer computerPlayer = new ComputerPlayer();

        board.makeMoveForPlayer(2, 0, "X");
        board.makeMoveForPlayer(2, 1, "X");
        board.makeMoveForPlayer(0, 0, "O");

        int[] move = computerPlayer.chooseMove(board, "O", AiDifficulty.HARD);

        assertArrayEquals(new int[]{2, 2}, move);
    }

    @Test
    void hardAiStartsInTheCenterOnClassicBoard() {
        GameBoard board = new GameBoard(3);
        ComputerPlayer computerPlayer = new ComputerPlayer();

        int[] move = computerPlayer.chooseMove(board, "O", AiDifficulty.HARD);

        assertArrayEquals(new int[]{1, 1}, move);
    }
}
