package com.example.connectfour;

public class Spot {
    private int piece;
    private final int column;
    private final int row;

    public Spot(int column, int row) {
        this.column = column;
        this.row = row;
        this.piece = 9;
    }

    public int getPiece() {
        return piece;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public void setPiece(int piece) {
        this.piece = piece;
    }
}
