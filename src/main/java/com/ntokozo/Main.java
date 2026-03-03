package com.ntokozo;

import com.ntokozo.engine.GameEngine;
import com.ntokozo.ui.MenuPanel;
import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public Main() {
        setTitle("Snake Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add menu panel
        MenuPanel menuPanel = new MenuPanel(this);
        mainPanel.add(menuPanel, "MENU");

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
    }

    public void showMenu() {
        cardLayout.show(mainPanel, "MENU");
    }

    public void startGame(int gridSize, int cellSize, String difficulty) {
        GameEngine game = new GameEngine(gridSize, cellSize, difficulty, this);
        mainPanel.add(game, "GAME");
        cardLayout.show(mainPanel, "GAME");
        game.startGame();
        game.requestFocusInWindow();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }
}