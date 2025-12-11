package airhockey.introduction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Introduction {
    private static Timer animationTimer;
    private static float alpha = 0f;
    private static int slideIndex = 0;
    private static List<Star> stars = new ArrayList<>();

    static class Star {
        float x, y;
        float size;
        float speed;
        float brightness;

        Star(float x, float y) {
            this.x = x;
            this.y = y;
            this.size = 1 + (float)Math.random() * 3;
            this.speed = 0.5f + (float)Math.random() * 2;
            this.brightness = 0.3f + (float)Math.random() * 0.7f;
        }

        void update() {
            y += speed;
            if (y > 700) {
                y = -20;
                x = (float)Math.random() * 1200;
            }

            brightness = 0.3f + 0.7f * (float)(0.5f + 0.5f * Math.sin(System.currentTimeMillis() * 0.001 + x));
        }
    }

    private static String[] slides = {
            " WELCOME TO AIR HOCKEY 2D",
            "A classic arcade game brought to life",
            "Experience realistic physics and smooth controls",
            "Challenge AI opponents or play with friends",
            "Get ready for intense hockey action!",
            "PRESS SPACE TO BEGIN"
    };

    public static void show() {
        // Initialize stars
        for (int i = 0; i < 100; i++) {
            stars.add(new Star((float)Math.random() * 1200,
                    (float)Math.random() * 700));
        }

        JFrame introFrame = new JFrame("Air Hockey 2D - Introduction");
        introFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        introFrame.setResizable(false);
        introFrame.setSize(1200, 700);
        introFrame.setLocationRelativeTo(null);

        JPanel introPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw space background
                GradientPaint spaceGradient = new GradientPaint(
                        0, 0, new Color(10, 10, 30),
                        0, getHeight(), new Color(5, 5, 20)
                );
                g2d.setPaint(spaceGradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Draw stars
                for (Star star : stars) {
                    g2d.setColor(new Color(255, 255, 255, (int)(star.brightness * 255)));
                    g2d.fillOval((int)star.x, (int)star.y, (int)star.size, (int)star.size);
                }

                // Draw title with glow effect
                g2d.setFont(new Font("Impact", Font.BOLD, 72));
                String title = " AIR HOCKEY 2D ";
                FontMetrics fm = g2d.getFontMetrics();
                int titleWidth = fm.stringWidth(title);

                for (int i = 10; i > 0; i--) {
                    g2d.setColor(new Color(0, 100, 255, 10 * i));
                    g2d.drawString(title,
                            (getWidth() - titleWidth)/2 + i,
                            150 + i);
                }

                // Main title
                GradientPaint titleGradient = new GradientPaint(
                        (getWidth() - titleWidth)/2, 140, new Color(30, 144, 255),
                        (getWidth() - titleWidth)/2, 160, new Color(100, 200, 255)
                );
                g2d.setPaint(titleGradient);
                g2d.drawString(title,
                        (getWidth() - titleWidth)/2,
                        150);

                // Draw current slide with fade effect
                if (slideIndex < slides.length) {
                    g2d.setColor(new Color(255, 255, 255, (int)(255 * alpha)));
                    g2d.setFont(new Font("Arial", Font.BOLD, 36));

                    String slideText = slides[slideIndex];
                    FontMetrics sm = g2d.getFontMetrics();
                    int slideWidth = sm.stringWidth(slideText);

                    // Text shadow
                    g2d.setColor(new Color(0, 0, 0, (int)(255 * alpha * 0.5)));
                    g2d.drawString(slideText,
                            (getWidth() - slideWidth)/2 + 2,
                            getHeight()/2 + 2);

                    // Main text
                    g2d.setColor(new Color(255, 255, 255, (int)(255 * alpha)));
                    g2d.drawString(slideText,
                            (getWidth() - slideWidth)/2,
                            getHeight()/2);
                }

                // Draw animated progress dots
                g2d.setColor(Color.WHITE);
                for (int i = 0; i < slides.length; i++) {
                    float dotAlpha = 0.3f;
                    if (i == slideIndex) {
                        // Animate current dot
                        float pulse = 0.5f + 0.5f * (float)Math.sin(System.currentTimeMillis() * 0.003);
                        dotAlpha = 0.3f + 0.7f * pulse;
                        g2d.setColor(new Color(30, 144, 255, (int)(dotAlpha * 255)));
                        g2d.fillOval(550 + i * 30, 600, 15, 15);
                    }
                    g2d.setColor(new Color(255, 255, 255, (int)(dotAlpha * 255)));
                    g2d.drawOval(550 + i * 30, 600, 15, 15);
                }

                // Draw instructions with blink effect
                float blink = 0.5f + 0.5f * (float)Math.sin(System.currentTimeMillis() * 0.002);
                g2d.setFont(new Font("Arial", Font.PLAIN, 16));
                g2d.setColor(new Color(200, 200, 255, (int)(blink * 255)));
                g2d.drawString("Use SPACE to continue or ESC to skip", 450, 650);
            }
        };

        // Update stars animation
        Timer starTimer = new Timer(30, e -> {
            for (Star star : stars) {
                star.update();
            }
            introPanel.repaint();
        });
        starTimer.start();

        introPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    slideIndex++;
                    if (slideIndex >= slides.length) {
                        starTimer.stop();
                        animationTimer.stop();
                        introFrame.dispose();
                        startGame();
                    } else {
                        alpha = 0f;
                        animationTimer.start();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    starTimer.stop();
                    animationTimer.stop();
                    introFrame.dispose();
                    startGame();
                }
            }
        });

        introFrame.add(introPanel);
        introFrame.setVisible(true);
        introPanel.requestFocusInWindow();

        // Start fade animation
        animationTimer = new Timer(30, e -> {
            alpha += 0.03f;
            if (alpha >= 1.0f) {
                alpha = 1.0f;
                animationTimer.stop();
            }
            introPanel.repaint();
        });
        animationTimer.start();
    }

    private static void startGame() {
        SwingUtilities.invokeLater(() -> {
            airhockey.game.GameEngine engine = new airhockey.game.GameEngine();
            engine.start();
        });
    }
}