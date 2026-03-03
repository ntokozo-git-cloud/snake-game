package com.ntokozo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SnakeTest {
    private Snake snake;

    @BeforeEach
    void setUp() {
        snake = new Snake();
    }

    @Test
    void testSnakeInitialization() {
        assertNotNull(snake.getBody());
        assertEquals(3, snake.getBody().size());
        assertEquals(Direction.RIGHT, snake.getDirection());
    }

    @Test
    void testSnakeMoveRight() {
        Point head = snake.getBody().get(0);
        Point oldHead = new Point(head.getX(), head.getY());

        snake.move();

        Point newHead = snake.getBody().get(0);
        assertEquals(oldHead.getX() + 1, newHead.getX());
        assertEquals(oldHead.getY(), newHead.getY());
        assertEquals(3, snake.getBody().size()); // Size unchanged when not growing
    }

    @Test
    void testSnakeMoveUp() {
        snake.setDirection(Direction.UP);
        Point head = snake.getBody().get(0);

        snake.move();

        Point newHead = snake.getBody().get(0);
        assertEquals(head.getX(), newHead.getX());
        assertEquals(head.getY() - 1, newHead.getY());
    }

    @Test
    void testSnakeGrow() {
        int initialSize = snake.getBody().size();

        snake.grow();
        snake.move();

        assertEquals(initialSize + 1, snake.getBody().size());
    }

    @Test
    void testNo180DegreeTurn() {
        snake.setDirection(Direction.RIGHT);
        snake.setDirection(Direction.LEFT); // Should be ignored (180° turn)
        assertEquals(Direction.RIGHT, snake.getDirection());

        snake.setDirection(Direction.UP);
        snake.setDirection(Direction.DOWN); // Should be ignored (180° turn)
        assertEquals(Direction.UP, snake.getDirection());

        snake.setDirection(Direction.RIGHT); // Should work (90° turn)
        assertEquals(Direction.RIGHT, snake.getDirection());
    }

    @Test
    void testSelfCollision() {
        // Make snake long enough to collide with itself
        for (int i = 0; i < 5; i++) {
            snake.grow();
            snake.move();
        }

        // Turn back on itself
        snake.setDirection(Direction.DOWN);
        snake.move();
        snake.setDirection(Direction.LEFT);
        snake.move();
        snake.setDirection(Direction.UP);
        snake.move();

        assertTrue(snake.checkCollision());
    }

    @Test
    void testNoCollisionInitially() {
        assertFalse(snake.checkCollision());
    }
}