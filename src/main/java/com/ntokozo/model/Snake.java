package com.ntokozo.model;

import java.util.*;

public class Snake {
    private List<Point> body;
    private Direction direction;
    private boolean growing;

    public Snake() {
        body = new ArrayList<>();
        // Start with 3 segments
        body.add(new Point(5, 5));
        body.add(new Point(4, 5));
        body.add(new Point(3, 5));
        direction = Direction.RIGHT;
        growing = false;
    }

    public List<Point> getBody() {
        return body;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction newDirection) {
        // Prevent 180-degree turns
        if ((direction == Direction.UP && newDirection != Direction.DOWN) ||
                (direction == Direction.DOWN && newDirection != Direction.UP) ||
                (direction == Direction.LEFT && newDirection != Direction.RIGHT) ||
                (direction == Direction.RIGHT && newDirection != Direction.LEFT)) {
            direction = newDirection;
        }
    }

    public void move() {
        if (body.isEmpty()) return;

        Point head = body.get(0);
        Point newHead = new Point(head.getX(), head.getY());

        switch (direction) {
            case UP: newHead.setY(newHead.getY() - 1); break;
            case DOWN: newHead.setY(newHead.getY() + 1); break;
            case LEFT: newHead.setX(newHead.getX() - 1); break;
            case RIGHT: newHead.setX(newHead.getX() + 1); break;
        }

        body.add(0, newHead);

        if (!growing) {
            body.remove(body.size() - 1);
        } else {
            growing = false;
        }
    }

    public void grow() {
        growing = true;
    }

    public boolean checkCollision() {
        if (body.isEmpty()) return false;

        Point head = body.get(0);
        for (int i = 1; i < body.size(); i++) {
            if (head.equals(body.get(i))) {
                return true;
            }
        }
        return false;
    }
}