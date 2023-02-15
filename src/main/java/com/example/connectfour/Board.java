package com.example.connectfour;

import com.google.gson.Gson;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Board {
    private static final int ROWS = 6;
    private static final int COLUMNS = 7;
    private final Spot[][] spot = new Spot[COLUMNS][ROWS];
    private boolean winState = false;
    private int winner;

    private static final int EMPTY_PIECE = 9;

    private static final int THREE_ADJ = 10;
    private static final int TWO_ADJ = 1;
    private static final int WINNER_ADJ = 100000;

    private final ArrayList<Integer> availableColumns = new ArrayList<>();

    private long[] bitboard = new long[2];
    private int[] height = new int[COLUMNS];
    private int counter = 0;
    private final List<Integer> moves = new ArrayList<>();


    public Board() {
        for (int column=0; column<COLUMNS; column++){
            for (int row=0; row<ROWS; row++) {
                spot[column][row]= new Spot(column, row);
            }
        }
    }

    public int getWinnerAdj() {
        return WINNER_ADJ;
    }

    public int getColumns(){
        return COLUMNS;
    }

    public boolean getWinState(){
        return winState;
    }

    public int getWinner() {
        return winner;
    }

    public int getSpot(int column, int row){
        return spot[column][row].getPiece();
    }

    public void setSpot(int column, int row, int piece) {
        spot[column][row].setPiece(piece);
    }

    public int getFirstAvailableSpot(int column){
        for (int r=ROWS-1; r >= 0; r-- ){
            if (spot[column][r].getPiece() == EMPTY_PIECE) {
                return r;
            }
        }

        return -1;
    }

    public int playPiece(int column, int player){
        int r = getFirstAvailableSpot(column);
        if (r == -1)
            return -1;

        spot[column][r].setPiece(player);

        if (r == 0) {
            availableColumns.remove(Integer.valueOf(column));
        }


        long move = 1L << height[column]++;
        bitboard[player] ^= move;
        moves.add(counter++, column);

        checkWinner(column, r, player);

        return r;
    }

    public ArrayList<Integer> getAvailableColumns()
    {
        return availableColumns;
    }

    public void resetBoard() {
        for (int c=0;c<COLUMNS;c++){
            for (int r = 0;r<ROWS;r++) {
                spot[c][r].setPiece(EMPTY_PIECE);
            }
        }
        winState = false;
        int[] stream = IntStream.range(0,COLUMNS).toArray();
        availableColumns.clear();
        for (int i : stream) {
            availableColumns.add(i);
        }

        moves.clear();
        bitboard[0] = 0;
        bitboard[1] = 0;
        counter = 0;
        for (int i = 0; i < COLUMNS; i++) {
            height[i] = (ROWS+1)*i;
            System.out.println("Height[" + i + "] = " + (ROWS+1)*i);
        }
    }

    private ArrayList<Point2D> getHorizontalSpots(int column, int row){
        ArrayList<Point2D> list = new ArrayList<>();

        int minCol = Math.max(column-3, 0);
        int maxCol = Math.min(column+3, COLUMNS-1);

        int c = minCol;
        while (c<= maxCol) {
            list.add(new Point2D(c, row));
            c++;
        }

        return list;
    }

    private ArrayList<Point2D> getVerticalSpots(int column, int row) {
        ArrayList<Point2D> list = new ArrayList<>();

        int minRow = Math.max(row-3, 0);
        int maxRow = Math.min(row+3, ROWS-1);

        int r = minRow;
        while (r<= maxRow) {
            list.add(new Point2D(column, r));
            r++;
        }

        return list;
    }

    private ArrayList<Point2D> getForwardDiagonalSpots(int column, int row) {
        ArrayList<Point2D> list = new ArrayList<>();

        System.out.println("Column: " + column);
        System.out.println("Row: " + row);

        int minRow = row;
        int minCol = column;
        int count = 0;
        while (minRow > 0 && minCol > 0 && count < 3) {
            minRow--;
            minCol--;
            count++;
        }

        //set possible win ending point
        int maxRow = row;
        int maxCol = column;
        count = 0;
        while (maxRow < ROWS-1 && maxCol < COLUMNS-1 && count < 3){
            maxRow++;
            maxCol++;
            count++;
        }

        int r = minRow;
        int c = minCol;
        while (r <= maxRow && c <= maxCol) {
            list.add(new Point2D(c, r));
            r++;
            c++;
        }

        return list;
    }

    private ArrayList<Point2D> getBackwardDiagonalSpots(int column, int row) {
        ArrayList<Point2D> list = new ArrayList<>();

        int maxRow = row;
        int minCol = column;
        int count = 0;
        while (maxRow < ROWS-1 && minCol > 0 && count < 3) {
            maxRow++;
            minCol--;
            count++;
        }

        //set possible win ending point
        int minRow = row;
        int maxCol = column;
        count = 0;
        while (minRow >0 && maxCol < COLUMNS-1 && count < 3){
            minRow--;
            maxCol++;
            count++;
        }

        int r = maxRow;
        int c = minCol;
        while (r >= minRow && c <= maxCol) {
            list.add(new Point2D(c, r));
            r--;
            c++;
        }

        return list;
    }

    public ArrayList<Point2D> checkWinnerPoints(ArrayList<Point2D> list, int currentPlayer) {

        ArrayList<Point2D> winning_list = new ArrayList<>();

        for (Point2D point : list) {
            int c = (int) point.getX();
            int r = (int) point.getY();
            if (spot[c][r].getPiece() == currentPlayer) {
                winning_list.add(point);
                if (winning_list.size() >= 4) {
                    winState = true;
                    winner = currentPlayer;
                    return winning_list;
                }
            }
            else {
                winning_list.clear();
            }
        }

        //no winners, return empty list
        return new ArrayList<>();
    }
    public ArrayList<Point2D> checkWinner(int column, int row, int currentPlayer) {

        if (horizontal_win(currentPlayer)) {
            return checkWinnerPoints(getHorizontalSpots(column, row), currentPlayer);
        }

        if (vertical_win(currentPlayer)) {
            return checkWinnerPoints(getVerticalSpots(column, row), currentPlayer);
        }


        if (diagonal_win(currentPlayer)) {
            return checkWinnerPoints(getBackwardDiagonalSpots(column, row), currentPlayer);
        }

        if (back_diagonal_win(currentPlayer)) {
            return checkWinnerPoints(getForwardDiagonalSpots(column, row), currentPlayer);
        }

        return new ArrayList<>();
    }

    private boolean horizontal_win(int currentPlayer){
        long bb;
        bb = bitboard[currentPlayer] & (bitboard[currentPlayer] >> 7);
        if ((bb & (bb >> 14)) != 0) {
            return true;
        }

        return false;
    }
    private boolean vertical_win(int currentPlayer) {
        long bb;
        bb = bitboard[currentPlayer] & (bitboard[currentPlayer] >> 1);
        if ((bb & (bb >> 2)) != 0) {
            return true;
        }

        return false;
    }

    private boolean back_diagonal_win(int currentPlayer) {
        long bb;
        bb = bitboard[currentPlayer] & (bitboard[currentPlayer] >> 6);
        if ((bb & (bb >> 12)) != 0) {
            return true;
        }

        return false;
    }

    private boolean diagonal_win(int currentPlayer) {
        long bb;
        bb = bitboard[currentPlayer] & (bitboard[currentPlayer] >> 8);
        if ((bb & (bb >> 16)) != 0) {
            return true;
        }

        return false;
    }

    private int getPoints(int pp, int empty, int op) {
        int score = 0;

        if (pp == 4) {
            return WINNER_ADJ; //means player won
        }
        else if (pp == 3 && empty == 1) {
            score += THREE_ADJ;
        }
        else if (pp == 2 && empty == 2) {
            score += TWO_ADJ;
        } else if (op == 4) {
            return -1*WINNER_ADJ; //means opposing player won
        }
        else if (op == 3 && empty == 1) {
            score -= THREE_ADJ;
        }
        else if (op == 2 && empty == 2) {
            score -= TWO_ADJ;
        }

        return score;
    }

    public int getPositionScore(int player) {
        int score = 0;

        //for horizontal lines
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLUMNS-3;c++) {
                int pp = 0; //player piece
                int empty = 0; //empty piece
                int op = 0; //opposing player piece
                int[] stream = IntStream.range(0,4).toArray();
                for (int i : stream) {
                    if (spot[c+i][r].getPiece() == player) {
                        pp += 1;
                    }
                    else if (spot[c+i][r].getPiece() == EMPTY_PIECE) {
                        empty += 1;
                    }
                    else if (spot[c+i][r].getPiece() != player) {
                        op += 1;
                    }
                }

                score += getPoints(pp, empty, op);
                if (score >= WINNER_ADJ) {
                    return WINNER_ADJ;
                }
                else if (score <= WINNER_ADJ*-1) {
                    return -1*WINNER_ADJ;
                }
            }
        }

        //for vertical lines
        for (int c = 0; c < COLUMNS; c++) {
            for (int r = 0; r < ROWS-3;r++) {
                int pp = 0; //player piece
                int empty = 0; //empty piece
                int op = 0; //opposing player piece
                int[] stream = IntStream.range(0,4).toArray();
                for (int i : stream) {
                    if (spot[c][r+i].getPiece() == player) {
                        pp += 1;
                    }
                    else if (spot[c][r+i].getPiece() == EMPTY_PIECE) {
                        empty += 1;
                    }
                    else if (spot[c][r+i].getPiece() != player) {
                        op += 1;
                    }
                }

                score += getPoints(pp, empty, op);
                if (score >= WINNER_ADJ) {
                    return WINNER_ADJ;
                }
                else if (score <= WINNER_ADJ*-1) {
                    return -1*WINNER_ADJ;
                }
            }
        }

        //backward diagonals
        for (int c = 0; c < COLUMNS-3; c++) {
            for (int r = 0; r < ROWS-3;r++) {
                int pp = 0; //player piece
                int empty = 0; //empty piece
                int op = 0; //opposing player piece
                int[] stream = IntStream.range(0,4).toArray();
                for (int i : stream) {
                    if (spot[c+i][r+i].getPiece() == player) {
                        pp += 1;
                    }
                    else if (spot[c+i][r+i].getPiece() == EMPTY_PIECE) {
                        empty += 1;
                    }
                    else if (spot[c+i][r+i].getPiece() != player) {
                        op += 1;
                    }
                }

                score += getPoints(pp, empty, op);
                if (score >= WINNER_ADJ) {
                    return WINNER_ADJ;
                }
                else if (score <= WINNER_ADJ*-1) {
                    return -1*WINNER_ADJ;
                }
            }
        }

        //forward diagonals
        for (int c = 0; c < COLUMNS-3; c++) {
            for (int r = 3; r < ROWS;r++) {
                int pp = 0; //player piece
                int empty = 0; //empty piece
                int op = 0; //opposing player piece
                int[] stream = IntStream.range(0,4).toArray();
                for (int i : stream) {
                    if (spot[c+i][r-i].getPiece() == player) {
                        pp += 1;
                    }
                    else if (spot[c+i][r-i].getPiece() == EMPTY_PIECE) {
                        empty += 1;
                    }
                    else if (spot[c+i][r-i].getPiece() != player) {
                        op += 1;
                    }
                }

                score += getPoints(pp, empty, op);
                if (score >= WINNER_ADJ) {
                    return WINNER_ADJ;
                }
                else if (score <= WINNER_ADJ*-1) {
                    return -1*WINNER_ADJ;
                }
            }
        }

        return score;
    }

    public void showBoard() {
//        for (int r = 0; r < ROWS; r++) {
//            for (int c = 0; c < COLUMNS; c++) {
//                char piece = '.';
//                if (getSpot(c,r) == 0)
//                    piece = 'Y';
//                else if (getSpot(c,r) == 1)
//                    piece = 'R';
//
//                System.out.print(piece);
//            }
//            System.out.println();
//        }

        StringBuilder result = new StringBuilder();

        result.append("BitBoard 1\n");
        for (int r = 1; r <= ROWS; r++) {
            for (int i = ROWS-r; i < ((COLUMNS)*7); i +=7) {
                long mask = 1L << i;
                String R = (bitboard[0] & mask) != 0 ? "1" : ".";
                result.append(R);
            }
            result.append("\n");
        }

        result.append("BitBoard 2\n");
        for (int r = 1; r <= ROWS; r++) {
            for (int i = ROWS-r; i < ((COLUMNS)*7); i +=7) {
                long mask = 1L << i;
                String R = (bitboard[1] & mask) != 0 ? "1" : ".";
                result.append(R);
            }
            result.append("\n");
        }

        System.out.println(result);
    }

    public Board getCopy() {
        Gson gson = new Gson();
        String b = gson.toJson(this);
        return gson.fromJson(b, Board.class);
    }
}
