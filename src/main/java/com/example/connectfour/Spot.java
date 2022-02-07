package com.example.connectfour;

public class Spot {
    private char piece;
    private final int column;
    private final int row;

    public Spot(int column, int row) {
        this.column = column;
        this.row = row;
        this.piece = 'N';
    }

    public char getPiece() {
        return piece;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public void setPiece(char piece) {
        this.piece = piece;
    }
}
