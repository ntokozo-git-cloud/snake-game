package com.ntokozo.model;

import java.awt.Color;

public class PowerUp extends Food {
    public enum PowerUpType {
        NORMAL(Color.RED, 10, "Normal"),
        GOLD(Color.YELLOW, 50, "Gold"),
        SPEED(Color.CYAN, 20, "Speed"),
        INVINCIBLE(Color.MAGENTA, 100, "Invincible"),
        SLOW(Color.BLUE, 30, "Slow");

        public final Color color;
        public final int points;
        public final String name;

        PowerUpType(Color color, int points, String name) {
            this.color = color;
            this.points = points;
            this.name = name;
        }
    }

    private PowerUpType type;
    private long spawnTime;
    private static final long POWERUP_DURATION = 5000; // 5 seconds

    public PowerUp() {
        super(); // Calls Food() constructor with default grid size
        generateRandomType();
        this.spawnTime = System.currentTimeMillis();
    }

    public PowerUp(int gridSize) {
        super(gridSize); // Calls Food(int) constructor with specified grid size
        generateRandomType();
        this.spawnTime = System.currentTimeMillis();
    }

    private void generateRandomType() {
        PowerUpType[] types = PowerUpType.values();
        int random = (int)(Math.random() * 20); // 5% chance for special

        if (random < 10) {
            type = PowerUpType.NORMAL;
        } else if (random < 15) {
            type = PowerUpType.GOLD;
        } else if (random < 18) {
            type = PowerUpType.SPEED;
        } else if (random < 19) {
            type = PowerUpType.SLOW;
        } else {
            type = PowerUpType.INVINCIBLE;
        }
    }

    public PowerUpType getType() {
        return type;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - spawnTime > POWERUP_DURATION;
    }

    @Override
    public void generateNewPosition() {
        super.generateNewPosition();
        generateRandomType();
        this.spawnTime = System.currentTimeMillis();
    }
}