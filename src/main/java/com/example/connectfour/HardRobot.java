package com.example.connectfour;

import javafx.scene.paint.Color;

import java.util.ArrayList;

public class HardRobot extends Robot {
    public HardRobot(Color color) {
        super("Megan", color);
    }

    @Override
    public int playColumn(Board board) {
        ArrayList<Integer> bestColumn = minimax(board, 1, 6, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        bestColumn.remove(0); //remove maxScore portion, we only want the columns

        if (bestColumn.get(0) == -1) {
            System.out.println("Issue - returning -1 for column");
            return 0;
        }

        System.out.println("Best Columns: " + bestColumn);
        return bestColumn.get(0);
    }
}
