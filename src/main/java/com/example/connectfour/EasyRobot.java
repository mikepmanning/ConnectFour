package com.example.connectfour;

import javafx.scene.paint.Color;

import java.util.Random;

public class EasyRobot extends Robot {
    public EasyRobot(Color color) {
        super("Fred", color);
    }

    @Override
    public int playColumn(Board board) {
        Random rand = new Random();
        return rand.nextInt(COLUMNS);
    }
}
