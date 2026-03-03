package com.ntokozo.ui;

import com.ntokozo.Main;
import com.ntokozo.utils.HighScoreManager;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class MenuPanel extends JPanel {
    private Main mainFrame;
    private HighScoreManager highScoreManager;
    private String selectedDifficulty = "MEDIUM";
    private int selectedGridSize = 20;

    public MenuPanel(Main mainFrame) {
        this.mainFrame = mainFrame;
        this.highScoreManager = new HighScoreManager();
        setPreferredSize(new Dimension(500, 600));
        setBackground(Color.BLACK);
        setLayout(new GridBagLayout());

        createMenuComponents();
    }

    private void createMenuComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        JLabel titleLabel = new JLabel("SNAKE GAME");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.GREEN);
        add(titleLabel, gbc);

        // Difficulty selector
        JPanel difficultyPanel = new JPanel();
        difficultyPanel.setBackground(Color.BLACK);
        JLabel diffLabel = new JLabel("Difficulty: ");
        diffLabel.setForeground(Color.WHITE);
        diffLabel.setFont(new Font("Arial", Font.BOLD, 18));

        String[] difficulties = {"EASY", "MEDIUM", "HARD", "EXPERT"};
        JComboBox<String> diffCombo = new JComboBox<>(difficulties);
        diffCombo.setSelectedItem("MEDIUM");
        diffCombo.addActionListener(e -> selectedDifficulty = (String) diffCombo.getSelectedItem());

        difficultyPanel.add(diffLabel);
        difficultyPanel.add(diffCombo);
        add(difficultyPanel, gbc);

        // Grid size selector
        JPanel sizePanel = new JPanel();
        sizePanel.setBackground(Color.BLACK);
        JLabel sizeLabel = new JLabel("Grid Size: ");
        sizeLabel.setForeground(Color.WHITE);
        sizeLabel.setFont(new Font("Arial", Font.BOLD, 18));

        String[] sizes = {"15x15", "20x20", "25x25", "30x30"};
        JComboBox<String> sizeCombo = new JComboBox<>(sizes);
        sizeCombo.setSelectedItem("20x20");
        sizeCombo.addActionListener(e -> {
            String size = (String) sizeCombo.getSelectedItem();
            selectedGridSize = Integer.parseInt(size.split("x")[0]);
        });

        sizePanel.add(sizeLabel);
        sizePanel.add(sizeCombo);
        add(sizePanel, gbc);

        // Start button
        JButton startButton = new JButton("START GAME");
        startButton.setFont(new Font("Arial", Font.BOLD, 24));
        startButton.setBackground(Color.GREEN);
        startButton.setForeground(Color.BLACK);
        startButton.addActionListener(e -> {
            int speed = getSpeedForDifficulty(selectedDifficulty);
            mainFrame.startGame(selectedGridSize, 25, selectedDifficulty);
        });
        add(startButton, gbc);

        // High scores button
        JButton highScoresButton = new JButton("HIGH SCORES");
        highScoresButton.setFont(new Font("Arial", Font.BOLD, 18));
        highScoresButton.addActionListener(e -> showHighScores());
        add(highScoresButton, gbc);

        // Instructions button
        JButton instructionsButton = new JButton("INSTRUCTIONS");
        instructionsButton.setFont(new Font("Arial", Font.BOLD, 18));
        instructionsButton.addActionListener(e -> showInstructions());
        add(instructionsButton, gbc);

        // Exit button
        JButton exitButton = new JButton("EXIT");
        exitButton.setFont(new Font("Arial", Font.BOLD, 18));
        exitButton.setBackground(Color.RED);
        exitButton.setForeground(Color.WHITE);
        exitButton.addActionListener(e -> System.exit(0));
        add(exitButton, gbc);
    }

    private int getSpeedForDifficulty(String difficulty) {
        switch(difficulty) {
            case "EASY": return 200;
            case "MEDIUM": return 150;
            case "HARD": return 100;
            case "EXPERT": return 70;
            default: return 150;
        }
    }

    private void showHighScores() {
        String scores = highScoreManager.getFormattedScores();
        JOptionPane.showMessageDialog(this,
                scores,
                "High Scores",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showInstructions() {
        String instructions = """
            INSTRUCTIONS:
            
            • Use ARROW KEYS or WASD to move
            • Eat red food to grow and score points
            • Special food appears randomly:
              - Gold: +50 points, slows time
              - Blue: +20 points, speeds up
              - Rainbow: +100 points, invincibility
            
            • Avoid hitting walls or yourself
            • Game speed increases with score
            • Press P to pause
            • Press R to restart when game over
            
            Good luck!
            """;

        JOptionPane.showMessageDialog(this,
                instructions,
                "Instructions",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw some snake-like decorations
        g.setColor(new Color(0, 50, 0));
        for (int i = 0; i < 10; i++) {
            int x = (int)(Math.random() * getWidth());
            int y = (int)(Math.random() * getHeight());
            g.fillOval(x, y, 10, 10);
        }
    }
}