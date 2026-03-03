package com.ntokozo.engine;

import com.ntokozo.Main;
import com.ntokozo.model.*;
import com.ntokozo.utils.HighScoreManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class GameEngine extends JPanel implements ActionListener {
    private Snake snake;
    private Food food;
    private List<PowerUp> powerUps;
    private int score;
    private boolean running, paused, invincible;
    private Timer timer;
    private int gridSize, cellSize, baseSpeed;
    private String difficulty;
    private Main mainFrame;
    private HighScoreManager highScoreManager;
    private long invincibleUntil, speedBoostUntil, slowMotionUntil;
    private String playerName = "Player";
    private List<Particle> particles;

    public GameEngine(int gridSize, int cellSize, String difficulty, Main mainFrame) {
        this.gridSize = gridSize;
        this.cellSize = cellSize;
        this.difficulty = difficulty;
        this.mainFrame = mainFrame;
        this.highScoreManager = new HighScoreManager();
        this.powerUps = new ArrayList<>();
        this.particles = new ArrayList<>();

        switch(difficulty) {
            case "EASY": baseSpeed = 200; break;
            case "MEDIUM": baseSpeed = 150; break;
            case "HARD": baseSpeed = 100; break;
            case "EXPERT": baseSpeed = 70; break;
            default: baseSpeed = 150;
        }

        resetGame();
        setPreferredSize(new Dimension(gridSize * cellSize, gridSize * cellSize));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) { handleKeyPress(e); }
        });
    }

    private void resetGame() {
        snake = new Snake();
        food = new Food(gridSize);
        powerUps.clear();
        particles.clear();
        score = 0;
        running = paused = invincible = false;
        invincibleUntil = speedBoostUntil = slowMotionUntil = 0;
    }

    private void handleKeyPress(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_P && running) { paused = !paused; repaint(); return; }
        if (key == KeyEvent.VK_R && !running) { resetGame(); startGame(); return; }
        if (key == KeyEvent.VK_ESCAPE) {
            if (!running) mainFrame.showMenu();
            else if (JOptionPane.showConfirmDialog(this, "Return to menu?", "Exit",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) mainFrame.showMenu();
            return;
        }

        if (!running || paused) return;

        switch (key) {
            case KeyEvent.VK_UP: case KeyEvent.VK_W: snake.setDirection(Direction.UP); break;
            case KeyEvent.VK_DOWN: case KeyEvent.VK_S: snake.setDirection(Direction.DOWN); break;
            case KeyEvent.VK_LEFT: case KeyEvent.VK_A: snake.setDirection(Direction.LEFT); break;
            case KeyEvent.VK_RIGHT: case KeyEvent.VK_D: snake.setDirection(Direction.RIGHT); break;
        }
    }

    public void startGame() {
        running = true;
        paused = false;
        if (timer != null) timer.stop();
        timer = new Timer(calculateSpeed(), this);
        timer.start();
        requestFocusInWindow();
    }

    private int calculateSpeed() {
        int speed = baseSpeed;
        long now = System.currentTimeMillis();
        if (now < speedBoostUntil) speed *= 0.7;
        if (now < slowMotionUntil) speed *= 1.5;
        return Math.max(50, speed - (score / 10));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused) {
            update();
            repaint();
            int newSpeed = calculateSpeed();
            if (timer.getDelay() != newSpeed) timer.setDelay(newSpeed);
        }
    }

    public void update() {
        snake.move();
        invincible = System.currentTimeMillis() < invincibleUntil;

        // Check food collision
        if (snake.getBody().get(0).equals(food.getPosition())) {
            snake.grow();
            score += 10;
            addParticleEffect(food.getPosition(), Color.RED);
            food.generateNewPosition();
        }

        // Check power-ups
        List<PowerUp> expired = new ArrayList<>();
        for (PowerUp p : powerUps) {
            if (snake.getBody().get(0).equals(p.getPosition())) {
                handlePowerUp(p);
                expired.add(p);
                addParticleEffect(p.getPosition(), p.getType().color);
            } else if (p.isExpired()) expired.add(p);
        }
        powerUps.removeAll(expired);

        // Spawn power-ups
        if (Math.random() < 0.01 && powerUps.size() < 3) {
            PowerUp newPowerUp = new PowerUp(gridSize);
            boolean valid = snake.getBody().stream().noneMatch(p -> p.equals(newPowerUp.getPosition()));
            if (valid) powerUps.add(newPowerUp);
        }

        particles.removeIf(Particle::isDead);
        particles.forEach(Particle::update);

        if (!invincible && (snake.checkCollision() || checkWallCollision())) gameOver();
    }

    private void handlePowerUp(PowerUp p) {
        snake.grow();
        score += p.getType().points;
        long now = System.currentTimeMillis();
        switch (p.getType()) {
            case SPEED: speedBoostUntil = now + 5000; break;
            case SLOW: slowMotionUntil = now + 3000; break;
            case INVINCIBLE: invincibleUntil = now + 3000; break;
        }
    }

    private void addParticleEffect(com.ntokozo.model.Point pos, Color color) {
        for (int i = 0; i < 10; i++)
            particles.add(new Particle(pos.getX() * cellSize + cellSize/2,
                    pos.getY() * cellSize + cellSize/2, color));
    }

    private boolean checkWallCollision() {
        if (invincible) return false;
        com.ntokozo.model.Point head = snake.getBody().get(0);
        return head.getX() < 0 || head.getX() >= gridSize || head.getY() < 0 || head.getY() >= gridSize;
    }

    private void gameOver() {
        running = false;
        if (timer != null) timer.stop();

        if (highScoreManager.isHighScore(score)) {
            String name = JOptionPane.showInputDialog(this, "New High Score! Enter your name:",
                    "High Score!", JOptionPane.INFORMATION_MESSAGE);
            if (name != null && !name.trim().isEmpty()) playerName = name;
            highScoreManager.addScore(playerName, score, difficulty);
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (running) {
            // Grid
            g.setColor(new Color(30, 30, 30));
            for (int i = 0; i <= gridSize; i++) {
                g.drawLine(i * cellSize, 0, i * cellSize, getHeight());
                g.drawLine(0, i * cellSize, getWidth(), i * cellSize);
            }

            // Power-ups
            for (PowerUp p : powerUps) {
                g.setColor(p.getType().color);
                com.ntokozo.model.Point pos = p.getPosition();
                int pulse = (int)(cellSize - 2 + Math.sin(System.currentTimeMillis() * 0.01) * 3);
                int offset = (cellSize - pulse) / 2;
                g.fillOval(pos.getX() * cellSize + offset, pos.getY() * cellSize + offset, pulse, pulse);
            }

            // Food
            g.setColor(Color.RED);
            com.ntokozo.model.Point foodPos = food.getPosition();
            g.fillOval(foodPos.getX() * cellSize + 2, foodPos.getY() * cellSize + 2, cellSize - 4, cellSize - 4);

            // Snake
            List<com.ntokozo.model.Point> body = snake.getBody();
            for (int i = 0; i < body.size(); i++) {
                com.ntokozo.model.Point p = body.get(i);
                float intensity = 1.0f - (i * 0.7f / body.size());

                if (invincible) {
                    float hue = (System.currentTimeMillis() % 1000) / 1000.0f;
                    g.setColor(Color.getHSBColor(hue, 1.0f, intensity));
                } else {
                    g.setColor(new Color(0, (int)(255 * intensity), 0));
                }

                g.fillRoundRect(p.getX() * cellSize + 2, p.getY() * cellSize + 2,
                        cellSize - 4, cellSize - 4, 8, 8);

                if (i == 0) { // Eyes
                    g.setColor(Color.WHITE);
                    g.fillOval(p.getX() * cellSize + 5, p.getY() * cellSize + 5, 5, 5);
                    g.fillOval(p.getX() * cellSize + 15, p.getY() * cellSize + 5, 5, 5);
                    g.setColor(Color.BLACK);
                    g.fillOval(p.getX() * cellSize + 6, p.getY() * cellSize + 6, 3, 3);
                    g.fillOval(p.getX() * cellSize + 16, p.getY() * cellSize + 6, 3, 3);
                }
            }

            // Particles
            particles.forEach(p -> p.draw(g2d));

            // HUD
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString("Score: " + score, 10, 20);
            g.drawString("Difficulty: " + difficulty, 10, 40);

            int y = 60;
            if (System.currentTimeMillis() < invincibleUntil) { g.setColor(Color.MAGENTA); g.drawString("INVINCIBLE", 10, y); y += 20; }
            if (System.currentTimeMillis() < speedBoostUntil) { g.setColor(Color.CYAN); g.drawString("SPEED BOOST", 10, y); y += 20; }
            if (System.currentTimeMillis() < slowMotionUntil) { g.setColor(Color.BLUE); g.drawString("SLOW MOTION", 10, y); }

            if (paused) {
                g.setColor(new Color(255,255,255,150));
                g.setFont(new Font("Arial", Font.BOLD, 50));
                g.drawString("PAUSED", 150, 250);
            }
        } else {
            // Game Over
            g.setColor(new Color(0,0,0,200));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("GAME OVER", 120, 200);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Score: " + score, 180, 270);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Press R to Restart", 160, 330);
            g.drawString("Press ESC for Menu", 160, 360);

            if (highScoreManager.isHighScore(score)) {
                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial", Font.BOLD, 25));
                g.drawString("NEW HIGH SCORE!", 140, 400);
            }
        }
    }

    private class Particle {
        double x, y, vx, vy;
        Color color;
        int life, maxLife;

        Particle(double x, double y, Color color) {
            this.x = x; this.y = y;
            this.vx = (Math.random() - 0.5) * 4;
            this.vy = (Math.random() - 0.5) * 4;
            this.color = color;
            this.maxLife = 30 + (int)(Math.random() * 30);
            this.life = maxLife;
        }

        void update() { x += vx; y += vy; vy += 0.1; life--; }
        void draw(Graphics2D g) {
            int alpha = (int)(255 * ((double)life / maxLife));
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
            g.fillOval((int)x - 2, (int)y - 2, 4, 4);
        }
        boolean isDead() { return life <= 0; }
    }
}