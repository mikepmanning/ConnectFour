package com.example.connectfour;

import javafx.geometry.Point2D;

import java.util.ArrayList;

public class Board {
    private static final int ROWS = 6;
    private static final int COLUMNS = 7;
    private static final Spot[][] spot = new Spot[COLUMNS][ROWS];

    public Board() {
        for (int column=0; column<COLUMNS; column++){
            for (int row=0; row<ROWS; row++) {
                spot[column][row]= new Spot(column, row);
            }
        }
    }

    public char getSpot(int column, int row){
        return spot[column][row].getPiece();
    }

    public int getFirstAvailableSpot(int column){
        for (int r=ROWS-1; r >= 0; r-- ){
            if (spot[column][r].getPiece() == 'N') {
                return r;
            }
        }

        return -1;
    }

    public int playPiece(int column, char player){
        int r = getFirstAvailableSpot(column);
        if (r == -1)
            return -1;

        spot[column][r].setPiece(player);
        return r;
    }

    public void resetBoard() {
        for (int c=0;c<COLUMNS;c++){
            for (int r = 0;r<ROWS;r++) {
                spot[c][r].setPiece('N');
            }
        }
    }

    private ArrayList<Point2D> checkHorizontal(int column, int row, char currentPlayer){

        ArrayList<Point2D> list = new ArrayList<>();
        int minCol = Math.max(column-4, 0);
        int maxCol = Math.min(column+4, COLUMNS-1);

        int c = minCol;
        while (c <= maxCol) {
            if (getSpot(c, row) == currentPlayer) {
                Point2D point = new Point2D(c, row);
                list.add(point);
                if (list.size() >= 4){
                    return list;
                }
            }
            else {
                list.removeAll(list);
            }
            c++;
        }

        return list;
    }

    private ArrayList<Point2D> checkVertical(int column, int row, char currentPlayer) {

        ArrayList<Point2D> list = new ArrayList<>();

        int minRow = Math.max(row-4, 0);
        int maxRow = Math.min(row+4, ROWS-1);

        int r = minRow;
        while (r <= maxRow) {
            if (getSpot(column, r) == currentPlayer) {
                Point2D point = new Point2D(column, r);
                list.add(point);
                if (list.size() >= 4){
                    return list;
                }
            }
            else {
                list.removeAll(list);
            }
            r++;
        }

        return list;
    }

    //checks from upper left to lower right diagonal win
    private ArrayList<Point2D> checkForwardDiagonal(int column, int row, char currentPlayer){

        ArrayList<Point2D> list = new ArrayList<>();

        //set possible win starting point
        int minRow = row;
        int minCol = column;
        while (minRow > 0 && minCol > 0) {
            minRow--;
            minCol--;
        }

        //set possible win ending point
        int maxRow = row;
        int maxCol = column;
        while (maxRow < ROWS-1 && maxCol < COLUMNS-1){
            maxRow++;
            maxCol++;
        }

        int r = minRow;
        int c = minCol;
        while (r <= maxRow && c <= maxCol) {
            if (getSpot(c, r) == currentPlayer) {
                Point2D point = new Point2D(c, r);
                list.add(point);
                if (list.size() >= 4)
                    return list;
            }
            else {
                list.removeAll(list);
            }
            r++;
            c++;
        }

        return list;
    }

    //checks diagonal from lower left to upper right
    private ArrayList<Point2D> checkBackwardDiagonal(int column, int row, char currentPlayer){

        ArrayList<Point2D> list = new ArrayList<>();

        //get starting points
        int maxRow = row;
        int minCol = column;
        while (minCol > 0 && maxRow < ROWS-1) {
            maxRow++;
            minCol--;
        }

        //get ending points
        int minRow = row;
        int maxCol = column;
        while (maxCol < COLUMNS-1 && minRow > 0) {
            minRow--;
            maxCol++;
        }

        int r = maxRow;
        int c = minCol;
        while (r >= minRow && c <= maxCol) {
            if (getSpot(c, r) == currentPlayer) {
                Point2D point = new Point2D(c, r);
                list.add(point);
                if (list.size() >= 4){
                    return list;
                }
            }
            else {
                list.removeAll(list);
            }
            r--;
            c++;
        }

        return list;
    }

    public ArrayList<Point2D> checkWinner(int column, int row, char currentPlayer) {

        ArrayList<Point2D> list;

        list = checkHorizontal(column, row, currentPlayer);
        if (list.size() >= 4) {
            return list;
        }

        list = checkVertical(column, row, currentPlayer);
        if (list.size() >= 4) {
            return list;
        }

        list = checkForwardDiagonal(column, row, currentPlayer);
        if (list.size() >= 4) {
            return list;
        }

        list = checkBackwardDiagonal(column, row, currentPlayer);
        if (list.size() >= 4) {
            return list;
        }

        list = new ArrayList<>();
        return list;
    }

    public void showBoard() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                System.out.print(getSpot(c, r));
            }
            System.out.println();
        }
    }
}
