package com.example.connectfour;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Random;

public class MediumRobot extends Robot {
    public MediumRobot(Color color) {
        super("Agnes", color);
    }

    @Override
    public int playColumn(Board board) {
        ArrayList<Integer> bestColumn = new ArrayList<>();

        int maxScore = Integer.MIN_VALUE;
        for (int column : board.getAvailableColumns()) {
            int score = getScore(board, column);
            System.out.println("Column: " + column + ", Score: " + score);
            if (score > maxScore) {
                maxScore = score;
                bestColumn.clear();
                bestColumn.add(column);
            }
            else if (score == maxScore) {
                bestColumn.add(column);
            }
        }

        Random rand = new Random();
        return bestColumn.get(rand.nextInt(bestColumn.size()));
    }
}
