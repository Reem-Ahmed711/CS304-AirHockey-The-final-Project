package airhockey.game;

import airhockey.core.*;
import airhockey.utils.Constants;
import airhockey.animation.GoalAnimation;
import javax.swing.*;
import java.awt.*;

public class GameRenderer extends JPanel {
    private GameController gameController;
    private GoalAnimation goalAnimation;
    private int fps = 0;
    private long lastFpsTime = 0;
    private int frameCount = 0;

    // Animation variables
    private float titleGlow = 0f;
    private boolean increasing = true;
    private float powerUpGlow = 0f;

    public GameRenderer(GameController controller) {
        this.gameController = controller;
        this.goalAnimation = new GoalAnimation();
        setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        setBackground(Constants.BACKGROUND_COLOR);
        setDoubleBuffered(true);
        setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        // Update animations
        updateAnimations();

        // Render game
        renderGame(g2d);

        // Render UI
        renderUI(g2d);

        // Render goal animation
        goalAnimation.render(g2d);

        updateFPS();

        renderFPS(g2d);

        // Render pause screen
        if (gameController.isGamePaused()) {
            renderPauseScreen(g2d);
        }

        // Render game over screen
        if (!gameController.isGameRunning()) {
            renderGameOverScreen(g2d);
        }
    }

    private void updateAnimations() {
        if (increasing) {
            titleGlow += 0.02f;
            if (titleGlow >= 1.0f) {
                titleGlow = 1.0f;
                increasing = false;
            }
        } else {
            titleGlow -= 0.02f;
            if (titleGlow <= 0.3f) {
                titleGlow = 0.3f;
                increasing = true;
            }
        }

        powerUpGlow = (float)(0.5f + 0.5f * Math.sin(System.currentTimeMillis() * 0.005f));

        goalAnimation.update();
    }

    private void renderGame(Graphics2D g2d) {
        gameController.getTable().render(g2d);
        gameController.getPuck().render(g2d);
        gameController.getPlayer1().render(g2d);
        gameController.getPlayer2().render(g2d);
    }

    private void renderUI(Graphics2D g2d) {
        renderPlayerScores(g2d);
        renderGameInfo(g2d);
        renderWinCondition(g2d);

        if (gameController.isPowerUpActive()) {
            renderPowerUpIndicator(g2d);
        }
        renderControlsHint(g2d);
    }

    private void renderPlayerScores(Graphics2D g2d) {
        renderScore(g2d,
                "PLAYER 1",
                gameController.getPlayer1Score(),
                Constants.PLAYER1_COLOR,
                50,
                70,
                false);

        // Player 2/AI score (right side) - MOVED DOWN from y=40 to y=70
        String player2Name = gameController.getGameMode() == GameController.GameMode.SINGLE_PLAYER ?
                "AI" : "PLAYER 2";
        Color player2Color = gameController.getGameMode() == GameController.GameMode.SINGLE_PLAYER ?
                new Color(100, 220, 100) : Constants.PLAYER2_COLOR;

        renderScore(g2d,
                player2Name,
                gameController.getPlayer2Score(),
                player2Color,
                Constants.WINDOW_WIDTH - 150,
                70, // Changed from 40 to 70
                true);

        g2d.setFont(new Font("Impact", Font.BOLD, 24));
        g2d.setColor(new Color(255, 255, 255, (int)(titleGlow * 200)));
        g2d.drawString("VS", Constants.WINDOW_WIDTH/2 - 15, 90);
    }

    private void renderScore(Graphics2D g2d, String name, int score, Color color, int x, int y, boolean rightAlign) {
        // Player name
        g2d.setFont(new Font("Calisto MT", Font.BOLD, 16));
        g2d.setColor(new Color(255, 255, 255, 220));

        FontMetrics nameFm = g2d.getFontMetrics();
        int nameWidth = nameFm.stringWidth(name);
        int nameX = rightAlign ? x + 100 - nameWidth : x;
        // Name shadow
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.drawString(name, nameX + 2, y + 2);
        // Name text
        g2d.setColor(Color.WHITE);
        g2d.drawString(name, nameX, y);
        // Score background with glow
        int scoreY = y + 30;
        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.fillRoundRect(x - 10, scoreY - 20, 120, 60, 15, 15);
        // Score border with animation
        g2d.setColor(new Color(255, 255, 255, (int)(titleGlow * 100)));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x - 10, scoreY - 20, 120, 60, 15, 15);
        // Score
        g2d.setFont(new Font("Impact", Font.BOLD, 36));
        String scoreStr = String.valueOf(score);
        FontMetrics scoreFm = g2d.getFontMetrics();
        int scoreWidth = scoreFm.stringWidth(scoreStr);
        int scoreX = rightAlign ? x + 100 - scoreWidth : x;
        // Score shadow
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.drawString(scoreStr, scoreX + 2, scoreY + 30 + 2);
        // Main score with gradient
        GradientPaint scoreGradient = new GradientPaint(
                scoreX, scoreY + 10, color,
                scoreX, scoreY + 30, color.brighter()
        );
        g2d.setPaint(scoreGradient);
        g2d.drawString(scoreStr, scoreX, scoreY + 30);
        // Inner glow effect
        g2d.setColor(new Color(255, 255, 255, 30));
        g2d.fillRoundRect(x - 5, scoreY - 15, 110, 50, 10, 10);
    }
    private void renderWinCondition(Graphics2D g2d) {
        // Draw win condition text - MOVED UP from y=WINDOW_HEIGHT-35 to y=140
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.setColor(new Color(255, 255, 200, (int)(titleGlow * 255)));
        String winText = "First to " + Constants.WIN_SCORE + " wins!";
        FontMetrics winFm = g2d.getFontMetrics();
        int winWidth = winFm.stringWidth(winText);
        // Position above the table (approximately)
        g2d.drawString(winText, (Constants.WINDOW_WIDTH - winWidth)/2, 140);
    }
    private void renderPowerUpIndicator(Graphics2D g2d) {
        int centerX = Constants.WINDOW_WIDTH / 2;
        int y = 160;
        // Background with glow
        g2d.setColor(new Color(255, 215, 0, (int)(powerUpGlow * 100)));
        g2d.fillRoundRect(centerX - 120, y - 15, 240, 40, 15, 15);
        // Border with animation
        g2d.setColor(new Color(255, 215, 0, (int)(powerUpGlow * 200)));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(centerX - 120, y - 15, 240, 40, 15, 15);
        // Power-up text
        g2d.setFont(new Font("Calisto MT", Font.BOLD, 14));
        g2d.setColor(Color.WHITE);
        String powerUpText = "POWER UP: " + gameController.getActivePowerUp();
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(powerUpText);
        g2d.drawString(powerUpText, centerX - textWidth/2, y + 5);
        // Timer bar
        g2d.setColor(new Color(0, 255, 0, 150));
        g2d.fillRoundRect(centerX - 90, y + 12, 180, 6, 3, 3);
    }

    private void renderGameInfo(Graphics2D g2d) {

        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(0, 0, 0, 180),
                0, 40, new Color(40, 40, 60, 180)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, Constants.WINDOW_WIDTH, 40);
        // Game mode and controls info - MOVED DOWN inside the taller bar
        g2d.setFont(new Font("Calisto MT", Font.BOLD, 14));
        g2d.setColor(Color.WHITE);
        String mode = gameController.getGameMode() == GameController.GameMode.SINGLE_PLAYER ?
                "SINGLE PLAYER" : "TWO PLAYERS";
        String difficulty = "";
        if (gameController.getGameMode() == GameController.GameMode.SINGLE_PLAYER) {
            difficulty = " | AI: " + gameController.getAIDifficultyName();
        }
        String control = gameController.isMouseControl() ? "MOUSE" : "KEYBOARD";
        String info = mode + difficulty + " | " + control;
        if (gameController.isGamePaused()) {
            info += " | PAUSED";
        }
        FontMetrics infoFm = g2d.getFontMetrics();
        int infoWidth = infoFm.stringWidth(info);
        g2d.drawString(info, (Constants.WINDOW_WIDTH - infoWidth)/2, 25); // Changed from 20 to 25
    }
    private void renderPauseScreen(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        // Paused text
        g2d.setFont(new Font("Impact", Font.BOLD, 60));
        String pauseText = "GAME PAUSED";
        FontMetrics pm = g2d.getFontMetrics();
        int pauseWidth = pm.stringWidth(pauseText);
        for (int i = 5; i > 0; i--) {
            g2d.setColor(new Color(255, 215, 0, 30 + i * 20));
            g2d.drawString(pauseText,
                    (Constants.WINDOW_WIDTH - pauseWidth)/2 + i,
                    Constants.WINDOW_HEIGHT/2 + i);
        }
        // Main text with gradient
        GradientPaint textGradient = new GradientPaint(
                (Constants.WINDOW_WIDTH - pauseWidth)/2, Constants.WINDOW_HEIGHT/2 - 40,
                Color.YELLOW,
                (Constants.WINDOW_WIDTH - pauseWidth)/2, Constants.WINDOW_HEIGHT/2 + 40,
                new Color(255, 140, 0)
        );
        g2d.setPaint(textGradient);
        g2d.drawString(pauseText,
                (Constants.WINDOW_WIDTH - pauseWidth)/2,
                Constants.WINDOW_HEIGHT/2);

        // Instruction with fade effect - MOVED UP slightly
        float instructionAlpha = (float)(0.7f + 0.3f * Math.sin(System.currentTimeMillis() * 0.002f));
        g2d.setFont(new Font("Calisto MT", Font.BOLD, 22));
        g2d.setColor(new Color(255, 255, 255, (int)(instructionAlpha * 255)));
        String instruction = "Press P or SPACE to continue";
        FontMetrics im = g2d.getFontMetrics();
        int instWidth = im.stringWidth(instruction);
        g2d.drawString(instruction,
                (Constants.WINDOW_WIDTH - instWidth)/2,
                Constants.WINDOW_HEIGHT/2 + 60);
    }

    private void renderGameOverScreen(Graphics2D g2d) {
        // Semi-transparent overlay with gradient
        GradientPaint overlayGradient = new GradientPaint(
                0, 0, new Color(0, 0, 0, 220),
                0, Constants.WINDOW_HEIGHT, new Color(30, 30, 60, 220)
        );
        g2d.setPaint(overlayGradient);
        g2d.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        // Winner text
        g2d.setFont(new Font("Impact", Font.BOLD, 56));

        String winnerText;
        Color winnerColor;
        if (gameController.getPlayer1Score() >= Constants.WIN_SCORE) {
            winnerText = "PLAYER 1 WINS!";
            winnerColor = Constants.PLAYER1_COLOR;
        } else {
            if (gameController.getGameMode() == GameController.GameMode.SINGLE_PLAYER) {
                winnerText = "AI WINS!";
                winnerColor = new Color(100, 200, 100);
            } else {
                winnerText = "PLAYER 2 WINS!";
                winnerColor = Constants.PLAYER2_COLOR;
            }
        }
        FontMetrics wm = g2d.getFontMetrics();
        int winnerWidth = wm.stringWidth(winnerText);
        // Text shadow with multiple layers for 3D effect
        for (int i = 5; i > 0; i--) {
            g2d.setColor(new Color(0, 0, 0, 30 * i));
            g2d.drawString(winnerText,
                    (Constants.WINDOW_WIDTH - winnerWidth)/2 + i*2,
                    Constants.WINDOW_HEIGHT/2 - 60 + i*2);
        }
        // Main winner text with gradient
        GradientPaint winnerGradient = new GradientPaint(
                (Constants.WINDOW_WIDTH - winnerWidth)/2, Constants.WINDOW_HEIGHT/2 - 80,
                winnerColor,
                (Constants.WINDOW_WIDTH - winnerWidth)/2, Constants.WINDOW_HEIGHT/2 - 20,
                winnerColor.brighter()
        );
        g2d.setPaint(winnerGradient);
        g2d.drawString(winnerText,
                (Constants.WINDOW_WIDTH - winnerWidth)/2,
                Constants.WINDOW_HEIGHT/2 - 60);

        // Score text
        g2d.setFont(new Font("Calisto MT", Font.BOLD, 32));
        g2d.setColor(new Color(255, 255, 255, 220));

        String scoreText = "Final Score: " + gameController.getPlayer1Score() +
                " - " + gameController.getPlayer2Score();
        FontMetrics sm = g2d.getFontMetrics();
        int scoreWidth = sm.stringWidth(scoreText);
        // Score background
        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.fillRoundRect(
                (Constants.WINDOW_WIDTH - scoreWidth)/2 - 20,
                Constants.WINDOW_HEIGHT/2 - 10,
                scoreWidth + 40,
                50,
                10,
                10
        );
        // Score text
        g2d.drawString(scoreText,
                (Constants.WINDOW_WIDTH - scoreWidth)/2,
                Constants.WINDOW_HEIGHT/2 + 15);
        // Options with blinking effect
        float blinkAlpha = (float)(0.5f + 0.5f * Math.sin(System.currentTimeMillis() * 0.003f));
        g2d.setFont(new Font("Calisto MT", Font.BOLD, 22));
        g2d.setColor(new Color(200, 200, 255, (int)(blinkAlpha * 255)));
        String option1 = "Press R to play again";
        String option2 = "Press ESC for main menu";

        FontMetrics om = g2d.getFontMetrics();
        int opt1Width = om.stringWidth(option1);
        int opt2Width = om.stringWidth(option2);

        g2d.drawString(option1,
                (Constants.WINDOW_WIDTH - opt1Width)/2,
                Constants.WINDOW_HEIGHT/2 + 70);
        g2d.drawString(option2,
                (Constants.WINDOW_WIDTH - opt2Width)/2,
                Constants.WINDOW_HEIGHT/2 + 100);
    }
    private void renderControlsHint(Graphics2D g2d) {
        g2d.setFont(new Font("Calisto MT", Font.PLAIN, 12));
        g2d.setColor(new Color(200, 200, 255, 180));
        String controls = "ESC: Menu | P: Pause | R: Restart | M: Toggle";
        FontMetrics controlsFm = g2d.getFontMetrics();
        int controlsWidth = controlsFm.stringWidth(controls);
        g2d.drawString(controls, (Constants.WINDOW_WIDTH - controlsWidth)/2, Constants.WINDOW_HEIGHT - 10);
    }
    private void renderFPS(Graphics2D g2d) {
        g2d.setFont(new Font("Calisto MT", Font.PLAIN, 11));
        g2d.setColor(new Color(0, 255, 0, 180));
        g2d.drawString("FPS: " + fps, Constants.WINDOW_WIDTH - 60, 30);
    }
    private void updateFPS() {
        frameCount++;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFpsTime >= 1000) {
            fps = frameCount;
            frameCount = 0;
            lastFpsTime = currentTime;
        }
    }
    // Method to trigger goal animation
    public void triggerGoalAnimation(boolean player1Scored) {
        String scorer;
        Color color;
        if (player1Scored) {
            scorer = "Player 1 Scores!";
            color = Constants.PLAYER1_COLOR;
        } else {
            if (gameController.getGameMode() == GameController.GameMode.SINGLE_PLAYER) {
                scorer = "AI Scores!";
                color = new Color(100, 220, 100);
            } else {
                scorer = "Player 2 Scores!";
                color = Constants.PLAYER2_COLOR;
            }
        }

        goalAnimation.triggerGoal(scorer, color, Constants.WINDOW_WIDTH/2, Constants.WINDOW_HEIGHT/2);
    }
}