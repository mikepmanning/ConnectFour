package com.example.connectfour;

import javafx.scene.paint.Color;

public class Player {
    private String name;
    private int wins;
    private int losses;
    private Color color;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public boolean isPlayer() {
        return true;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }
}
