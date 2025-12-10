package airhockey.game;

import airhockey.ai.AI;
import airhockey.core.*;
import airhockey.physics.PhysicsEngine;
import airhockey.utils.Constants;
import airhockey.audio.AudioManager;

import java.awt.event.KeyEvent;

public class GameController {
    public enum GameMode {
        SINGLE_PLAYER,
        TWO_PLAYERS
    }

    private GameMode gameMode;
    private AI ai;
    private int aiDifficulty = 2; // Medium by default
    private Constants.Theme currentTheme = Constants.Theme.MODERN;

    private AudioManager audioManager; // Audio system

    private HockeyTable table;
    private Puck puck;
    private Mallet player1;
    private Mallet player2;

    private int player1Score = 0;
    private int player2Score = 0;
    private boolean gameRunning = true;
    private boolean gamePaused = false;
    private boolean isResetting = false;

    // Power-up system
    private boolean powerUpActive = false;
    private String activePowerUp = "";
    private long powerUpEndTime = 0;
    private int powerUpRadius = 40; // Size when power-up is active

    // Keyboard controls
    private boolean p1Up, p1Down, p1Left, p1Right;
    private boolean p2Up, p2Down, p2Left, p2Right;

    // Mouse controls
    private int mouseX, mouseY;
    private boolean mousePressed = false;

    // Game states
    private int gameState = Constants.STATE_PLAYING;

    // GameRenderer (كما أضفتها سابقًا)
    private GameRenderer gameRenderer; // أضيفت هنا

    public GameController(GameMode mode, int difficulty, Constants.Theme theme) {
        this.gameMode = mode;
        this.aiDifficulty = difficulty;
        this.currentTheme = theme;
        Constants.CURRENT_THEME = theme;

        // Initialize audio system
        this.audioManager = new AudioManager();
        audioManager.setMusicEnabled(true);
        audioManager.setSoundEnabled(true);
        audioManager.playBackgroundMusic();

        initialize();
    }

    private void initialize() {
        table = new HockeyTable();
        puck = new Puck(Constants.WINDOW_WIDTH/2, Constants.WINDOW_HEIGHT/2);

        player1 = new Mallet(
                Constants.WINDOW_WIDTH/2,
                Constants.TABLE_Y + Constants.TABLE_HEIGHT - 100,
                true
        );

        player2 = new Mallet(
                Constants.WINDOW_WIDTH/2,
                Constants.TABLE_Y + 100,
                false
        );

        if (gameMode == GameMode.SINGLE_PLAYER) {
            ai = new AI(aiDifficulty);
        }

        resetGame();
    }

    public void update() {
        if (!gameRunning || gamePaused || isResetting) return;

        handleInput();

        if (gameMode == GameMode.SINGLE_PLAYER && ai != null) {
            // **!! التعديل هنا: تمرير الوقت الحالي (System.currentTimeMillis()) !!**
            ai.update(player2, puck, System.currentTimeMillis());
        }

        player1.update();
        player2.update();
        puck.update();

        checkCollisions();
        checkGoals();
        updatePowerUps();

        if (player1Score >= Constants.WIN_SCORE || player2Score >= Constants.WIN_SCORE) {
            gameRunning = false;
            gameState = Constants.STATE_GAME_OVER;
        }
    }

    // ... باقي الكلاس كما هو ...

    private void handleInput() {
        // Player 1 controls
        if (player1.isMouseControl() && mousePressed) {
            player1.moveTo(mouseX, mouseY);
        } else {
            float dx = 0, dy = 0;
            float speed = Constants.KEYBOARD_SPEED;

            if (p1Up) dy -= speed;
            if (p1Down) dy += speed;
            if (p1Left) dx -= speed;
            if (p1Right) dx += speed;

            if (dx != 0 || dy != 0) {
                player1.moveKeyboard(dx, dy);
            }
        }

        // Player 2 controls (only in two player mode)
        if (gameMode == GameMode.TWO_PLAYERS) {
            float dx2 = 0, dy2 = 0;
            float speed = Constants.KEYBOARD_SPEED;

            if (p2Up) dy2 -= speed;
            if (p2Down) dy2 += speed;
            if (p2Left) dx2 -= speed;
            if (p2Right) dx2 += speed;

            if (dx2 != 0 || dy2 != 0) {
                player2.moveKeyboard(dx2, dy2);
            }
        }
    }

    private void checkCollisions() {
        // Check puck-mallet collisions
        if (PhysicsEngine.checkCollision(puck, player1)) {
            PhysicsEngine.handleCollision(puck, player1);
            audioManager.playHit();

            // Apply power-up effects
            if (powerUpActive && activePowerUp.equals("SPEED_BOOST")) {
                puck.setVelocityX(puck.getVelocityX() * 1.5f);
                puck.setVelocityY(puck.getVelocityY() * 1.5f);
                audioManager.playPowerUp();
            }
        }

        if (PhysicsEngine.checkCollision(puck, player2)) {
            PhysicsEngine.handleCollision(puck, player2);
            audioManager.playHit();

            // Apply power-up effects
            if (powerUpActive && activePowerUp.equals("SPEED_BOOST")) {
                puck.setVelocityX(puck.getVelocityX() * 1.5f);
                puck.setVelocityY(puck.getVelocityY() * 1.5f);
                audioManager.playPowerUp();
            }
        }

        // Check wall collisions
        PhysicsEngine.handleWallCollision(puck);
    }

    private void checkGoals() {
        if (puck.isInGoal(true)) { // Top goal - Player 1 scores
            player1Score++;
            audioManager.playGoal();
            triggerGoalEffects(true);

            // Trigger animation
            if (gameRenderer != null) {
                gameRenderer.triggerGoalAnimation(true);
            }

            resetAfterGoal();
        } else if (puck.isInGoal(false)) { // Bottom goal - Player 2/AI scores
            player2Score++;
            audioManager.playGoal();
            triggerGoalEffects(false);

            // Trigger animation
            if (gameRenderer != null) {
                gameRenderer.triggerGoalAnimation(false);
            }

            resetAfterGoal();
        }
    }

    // أضف setter method:
    public void setGameRenderer(GameRenderer renderer) {
        this.gameRenderer = renderer;
    }

    private void triggerGoalEffects(boolean player1Scored) {
        // Random chance for power-up
        if (Math.random() < 0.3) { // 30% chance
            activateRandomPowerUp();
        }
    }

    private void activateRandomPowerUp() {
        String[] powerUps = {"SPEED_BOOST", "PUCK_SLOW"};
        activePowerUp = powerUps[(int)(Math.random() * powerUps.length)];
        powerUpActive = true;
        powerUpEndTime = System.currentTimeMillis() + 5000; // 5 seconds

        // Apply power-up immediately
        switch(activePowerUp) {
            case "SPEED_BOOST":
                // Applied during collision
                break;
            case "PUCK_SLOW":
                puck.setVelocityX(puck.getVelocityX() * 0.5f);
                puck.setVelocityY(puck.getVelocityY() * 0.5f);
                audioManager.playPowerUp();
                break;
        }
    }

    private void updatePowerUps() {
        if (powerUpActive && System.currentTimeMillis() > powerUpEndTime) {
            powerUpActive = false;
            activePowerUp = "";
        }
    }

    private void resetAfterGoal() {
        isResetting = true;

        new Thread(() -> {
            try {
                Thread.sleep(Constants.GOAL_RESET_DELAY);

                puck.reset();
                player1.setX(Constants.WINDOW_WIDTH/2);
                player1.setY(Constants.TABLE_Y + Constants.TABLE_HEIGHT - 100);

                player2.setX(Constants.WINDOW_WIDTH/2);
                player2.setY(Constants.TABLE_Y + 100);

                if (ai != null) {
                    ai.reset();
                }

                isResetting = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Public methods
    public void resetGame() {
        player1Score = 0;
        player2Score = 0;
        gameRunning = true;
        gamePaused = false;
        isResetting = false;
        powerUpActive = false;
        gameState = Constants.STATE_PLAYING;

        puck.reset();
        player1.setX(Constants.WINDOW_WIDTH/2);
        player1.setY(Constants.TABLE_Y + Constants.TABLE_HEIGHT - 100);

        player2.setX(Constants.WINDOW_WIDTH/2);
        player2.setY(Constants.TABLE_Y + 100);

        if (ai != null) {
            ai.reset();
        }
    }

    public void togglePause() {
        gamePaused = !gamePaused;
        gameState = gamePaused ? Constants.STATE_PAUSED : Constants.STATE_PLAYING;
    }

    public void toggleMouseControl() {
        player1.setMouseControl(!player1.isMouseControl());
    }

    // Audio controls
    public void toggleSound() {
        audioManager.setSoundEnabled(!audioManager.isSoundEnabled());
    }

    public void toggleMusic() {
        audioManager.setMusicEnabled(!audioManager.isMusicEnabled());
    }

    public void setVolume(float volume) {
        audioManager.setVolume(volume);
    }

    // Input handling
    public void mouseMoved(int x, int y) {
        mouseX = x;
        mouseY = y;
    }

    public void mousePressed(int x, int y) {
        mouseX = x;
        mouseY = y;
        mousePressed = true;
    }

    public void mouseReleased() {
        mousePressed = false;
    }

    public void keyPressed(int keyCode) {
        switch (keyCode) {
            // Player 1
            case KeyEvent.VK_W: p1Up = true; break;
            case KeyEvent.VK_S: p1Down = true; break;
            case KeyEvent.VK_A: p1Left = true; break;
            case KeyEvent.VK_D: p1Right = true; break;

            // Player 2
            case KeyEvent.VK_UP: p2Up = true; break;
            case KeyEvent.VK_DOWN: p2Down = true; break;
            case KeyEvent.VK_LEFT: p2Left = true; break;
            case KeyEvent.VK_RIGHT: p2Right = true; break;

            // Game controls
            case KeyEvent.VK_P:
            case KeyEvent.VK_SPACE: togglePause(); break;
            case KeyEvent.VK_R: resetGame(); break;
            case KeyEvent.VK_M: toggleMouseControl(); break;
            case KeyEvent.VK_ESCAPE:
                if (gameState == Constants.STATE_GAME_OVER || gameState == Constants.STATE_PAUSED) {
                    gameRunning = false;
                }
                break;
        }
    }

    public void keyReleased(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_W: p1Up = false; break;
            case KeyEvent.VK_S: p1Down = false; break;
            case KeyEvent.VK_A: p1Left = false; break;
            case KeyEvent.VK_D: p1Right = false; break;

            case KeyEvent.VK_UP: p2Up = false; break;
            case KeyEvent.VK_DOWN: p2Down = false; break;
            case KeyEvent.VK_LEFT: p2Left = false; break;
            case KeyEvent.VK_RIGHT: p2Right = false; break;
        }
    }

    // Getters
    public boolean isGameRunning() { return gameRunning; }
    public boolean isGamePaused() { return gamePaused; }
    public boolean isResetting() { return isResetting; }
    public int getPlayer1Score() { return player1Score; }
    public int getPlayer2Score() { return player2Score; }
    public Puck getPuck() { return puck; }
    public Mallet getPlayer1() { return player1; }
    public Mallet getPlayer2() { return player2; }
    public HockeyTable getTable() { return table; }
    public GameMode getGameMode() { return gameMode; }
    public boolean isMouseControl() { return player1.isMouseControl(); }
    public boolean isPowerUpActive() { return powerUpActive; }
    public String getActivePowerUp() { return activePowerUp; }
    public int getPowerUpRadius() { return powerUpRadius; }
    public int getAIDifficulty() { return aiDifficulty; }
    public String getAIDifficultyName() {
        return ai != null ? ai.getDifficultyName() : "N/A";
    }
    public int getGameState() { return gameState; }
    public AudioManager getAudioManager() { return audioManager; }

    public boolean isSoundEnabled() {
        return audioManager != null && audioManager.isSoundEnabled();
    }

    public boolean isMusicEnabled() {
        return audioManager != null && audioManager.isMusicEnabled();
    }

    public void setAIDifficulty(int difficulty) {
        this.aiDifficulty = difficulty;
        if (ai != null) {
            ai.setDifficulty(difficulty);
        }
    }

    public void setTheme(Constants.Theme theme) {
        this.currentTheme = theme;
        Constants.CURRENT_THEME = theme;
    }

    public void dispose() {
        if (audioManager != null) {
            audioManager.dispose();
        }
    }

}