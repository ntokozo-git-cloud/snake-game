package com.ntokozo.utils;

import javax.sound.sampled.*;
import java.io.*;
import java.net.URL;

public class SoundManager {
    private Clip eatSound;
    private Clip gameOverSound;
    private Clip powerUpSound;
    private Clip backgroundMusic;
    private boolean soundEnabled = true;

    public SoundManager() {
        try {
            // You'll need to add sound files to your resources
            // eatSound = loadSound("/sounds/eat.wav");
            // gameOverSound = loadSound("/sounds/gameover.wav");
            // powerUpSound = loadSound("/sounds/powerup.wav");
            // backgroundMusic = loadSound("/sounds/background.wav");
        } catch (Exception e) {
            System.out.println("Sound not available: " + e.getMessage());
            soundEnabled = false;
        }
    }

    private Clip loadSound(String path) throws Exception {
        URL soundURL = getClass().getResource(path);
        if (soundURL == null) return null;

        AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
        Clip clip = AudioSystem.getClip();
        clip.open(audioIn);
        return clip;
    }

    public void playEat() {
        if (soundEnabled && eatSound != null) {
            new Thread(() -> {
                eatSound.setFramePosition(0);
                eatSound.start();
            }).start();
        }
    }

    public void playGameOver() {
        if (soundEnabled && gameOverSound != null) {
            gameOverSound.setFramePosition(0);
            gameOverSound.start();
        }
    }

    public void playPowerUp() {
        if (soundEnabled && powerUpSound != null) {
            powerUpSound.setFramePosition(0);
            powerUpSound.start();
        }
    }

    public void startBackgroundMusic() {
        if (soundEnabled && backgroundMusic != null) {
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
    }
}