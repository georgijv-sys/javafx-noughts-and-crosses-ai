package uk.ac.soton.comp1206;


public class GameBoard {

    private int size;
    private String[][] board;
    private String currentPlayer = "X";

    public GameBoard(int size){
        this.size = size;
        board = new String[size][size];
        resetBoard();
    }
    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public void resetBoard(){
        currentPlayer = "X";
        for (int row=0;row<board.length;row++){
            for (int col=0;col<board[row].length;col++){
                board[row][col] = "";
            }
        }
    }

    public void switchPlayer(){
        if(currentPlayer.equals("X")){
            currentPlayer="O";
        }
        else{
            currentPlayer="X";
        }
    }

    public void makeMove(int row, int col){
        if (board[row][col].equals("")){
            board[row][col] = currentPlayer;
        }
    }

    public int[] findBestMoveForCurrentPlayer() {
        int[] winningMove = findMoveThatWouldWin(currentPlayer);
        if (winningMove != null) {
            return winningMove;
        }

        String opponent;
        if (currentPlayer.equals("X")) {
            opponent = "O";
        } else {
            opponent = "X";
        }

        int[] blockingMove = findMoveThatWouldWin(opponent);
        if (blockingMove != null) {
            return blockingMove;
        }

        return findFirstEmptyCell();
    }

    private int[] findMoveThatWouldWin(String player) {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board[row][col].equals("")) {
                    board[row][col] = player;
                    boolean moveWins = checkWinner().equals(player);
                    board[row][col] = "";

                    if (moveWins) {
                        return new int[]{row, col};
                    }
                }
            }
        }

        return null;
    }

    private int[] findFirstEmptyCell() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board[row][col].equals("")) {
                    return new int[]{row, col};
                }
            }
        }

        return null;
    }

    public String checkWinner(){
        for (int row=0;row<size;row++){
            String first = board[row][0];
            if (!first.equals("")){
                boolean win = true;

                for(int col=1;col<size;col++){
                    if (!board[row][col].equals(first)){
                        win = false;
                        break;
                    }
                }
                if (win){
                    return first;
                }
            }
        }

        for(int col=0;col<size;col++){
            String first = board[0][col];

            if (!first.equals("")){
                boolean win = true;
                for(int row=1;row<size;row++){
                    if (!board[row][col].equals(first)){
                        win = false;
                        break;
                    }
                }
                if (win){
                    return first;
                }
            }
        }

        String firstDiag = board[0][0];
        if (!firstDiag.equals("")){
            boolean win = true;
            for(int row=1;row<size;row++){
                if (!board[row][row].equals(firstDiag)){
                    win = false;
                    break;
                }
            }
            if (win){
                return firstDiag;
            }
        }
        String firstOppositeDiagonal = board[0][size - 1];

        if (!firstOppositeDiagonal.equals("")) {
            boolean oppositeDiagonalWin = true;

            for (int i = 1; i < size; i++) {
                if (!board[i][size - 1 - i].equals(firstOppositeDiagonal)) {
                    oppositeDiagonalWin = false;
                    break;
                }
            }

            if (oppositeDiagonalWin) {
                return firstOppositeDiagonal;
            }
        }
        return "";

    }
    public boolean isBoardFull(){
        for (int row=0;row<size;row++){
            for (int col=0;col<size;col++){
                if (board[row][col].equals("")){
                    return false;
                }
            }
        }
        return true;
    }





}
