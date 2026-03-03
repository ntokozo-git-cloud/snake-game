package com.ntokozo.model;

import java.util.Random;

public class Food {
    private Point position;
    private Random random;
    private int gridSize;

    public Food() {
        this(20); // Default grid size
    }

    public Food(int gridSize) {
        this.gridSize = gridSize;
        this.random = new Random();
        generateNewPosition();
    }

    public Point getPosition() {
        return position;
    }

    public void generateNewPosition() {
        int x = random.nextInt(gridSize);
        int y = random.nextInt(gridSize);
        position = new Point(x, y);
    }

    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }

    public int getGridSize() {
        return gridSize;
    }
}