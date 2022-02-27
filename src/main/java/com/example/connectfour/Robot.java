package com.example.connectfour;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;

public abstract class Robot {

    private final String name;
    private final Color color;


    private static final int MIDDLE_COLUMN_ADJ = 2;

    public Robot(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    protected static final int COLUMNS = 7;

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public abstract int playColumn(Board board);


    protected static int getScore(Board board, int column) {
        int score;

        Board copy = board.getCopy();
        copy.playPiece(column, 1);

        score = copy.getPositionScore(1);

        if (isMiddleColumn(board, column)) {
            score += MIDDLE_COLUMN_ADJ;
        }

        return score;
    }

    private static boolean isMiddleColumn(Board board, int column) {
        int columns = board.getColumns();
        int mid;
        if (columns % 2 == 0){
            mid = columns/2-1;
        }
        else{
            mid = columns/2;
        }

        return (column == mid);
    }

    protected static ArrayList<Integer> minimax(Board board, int currentPlayer, int depth, int alpha, int beta, boolean maxPlayer) {

        ArrayList<Integer> bestSol = new ArrayList<>();

        if (board.getAvailableColumns().isEmpty()) {
            bestSol.add(0);
            bestSol.add(-1);
            return bestSol;
        }

        int score = board.getPositionScore(currentPlayer);
        if (score == board.getWinnerAdj()) {
            bestSol.add(score);
            bestSol.add(-1);
            return bestSol;
        }

        if (score == (-1*board.getWinnerAdj())) {
            bestSol.add(score);
            bestSol.add(-1);
            return bestSol;
        }

        if (depth == 0) {
            bestSol.add(score);
            bestSol.add(-1);
            return bestSol;
        }

        ArrayList<Integer> colToCheck = board.getAvailableColumns();
        Collections.shuffle(colToCheck); //randomizes piece placement

        if (maxPlayer) {
            int maxValue = Integer.MIN_VALUE;
            int bestCol = -1;
            for (int column : colToCheck) {
                Board copy = board.getCopy();
                copy.playPiece(column, 1);
                ArrayList<Integer> tempSol = minimax(copy, currentPlayer, depth - 1, alpha, beta, false);
                int eval = tempSol.get(0);
                if (eval > maxValue) {
                    maxValue = eval;
                    bestCol = column;
                }
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }

            bestSol.add(maxValue);
            bestSol.add(bestCol);
            return bestSol;
        }
        else {
            int minValue = Integer.MAX_VALUE;
            int bestCol = -1;
            for (int column : colToCheck) {
                Board copy = board.getCopy();
                copy.playPiece(column, 0);
                ArrayList<Integer> tempSol = minimax(copy, currentPlayer, depth - 1, alpha, beta, true);
                int eval = tempSol.get(0);
                if (eval < minValue) {
                    minValue = eval;
                    bestCol = column;
                }
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break;
                }
            }

            bestSol.add(minValue);
            bestSol.add(bestCol);
            return bestSol;
        }

    }
}
