package com.ntokozo.utils;

import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HighScoreManager {
    private static final String HIGHSCORE_FILE = "highscores.txt";
    private List<HighScore> highScores;
    private DateTimeFormatter formatter;

    public HighScoreManager() {
        highScores = new ArrayList<>();
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        loadHighScores();
    }

    public void addScore(String playerName, int score, String difficulty) {
        HighScore newScore = new HighScore(playerName, score, difficulty, LocalDateTime.now());
        highScores.add(newScore);

        // Sort by score (highest first)
        highScores.sort((a, b) -> b.score - a.score);

        // Keep only top 10
        if (highScores.size() > 10) {
            highScores = new ArrayList<>(highScores.subList(0, 10));
        }

        saveHighScores();
    }

    public boolean isHighScore(int score) {
        if (highScores.size() < 10) return true;
        return score > highScores.get(highScores.size() - 1).score;
    }

    public String getFormattedScores() {
        if (highScores.isEmpty()) {
            return "No high scores yet!";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("🏆 HIGH SCORES 🏆\n\n");

        for (int i = 0; i < highScores.size(); i++) {
            HighScore hs = highScores.get(i);
            String medal = i == 0 ? "🥇 " : i == 1 ? "🥈 " : i == 2 ? "🥉 " : "   ";
            sb.append(String.format("%s%d. %s: %d (%s) - %s\n",
                    medal, i + 1, hs.playerName, hs.score, hs.difficulty,
                    hs.dateTime.format(formatter)));
        }

        return sb.toString();
    }

    private void loadHighScores() {
        File file = new File(HIGHSCORE_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 4) {
                    HighScore hs = new HighScore(
                            parts[0],
                            Integer.parseInt(parts[1]),
                            parts[2],
                            LocalDateTime.parse(parts[3], formatter)
                    );
                    highScores.add(hs);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveHighScores() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGHSCORE_FILE))) {
            for (HighScore hs : highScores) {
                writer.write(String.format("%s|%d|%s|%s\n",
                        hs.playerName, hs.score, hs.difficulty,
                        hs.dateTime.format(formatter)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class HighScore {
        String playerName;
        int score;
        String difficulty;
        LocalDateTime dateTime;

        HighScore(String playerName, int score, String difficulty, LocalDateTime dateTime) {
            this.playerName = playerName;
            this.score = score;
            this.difficulty = difficulty;
            this.dateTime = dateTime;
        }
    }
}