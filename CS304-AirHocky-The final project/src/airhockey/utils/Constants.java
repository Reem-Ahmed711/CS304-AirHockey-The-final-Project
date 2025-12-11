package airhockey.utils;

import java.awt.*;

public class Constants {
    // Window
    public static final int WINDOW_WIDTH = 1200;
    public static final int WINDOW_HEIGHT = 700;

    // Table
    public static final int TABLE_WIDTH = 800;
    public static final int TABLE_HEIGHT = 400;
    public static final int TABLE_X = (WINDOW_WIDTH - TABLE_WIDTH) / 2;
    public static final int TABLE_Y = (WINDOW_HEIGHT - TABLE_HEIGHT) / 2;

    // Objects
    public static final int PUCK_RADIUS = 16;
    public static final int MALLET_RADIUS = 30;

    // Goal
    public static final int GOAL_WIDTH = 180;
    public static final int GOAL_DEPTH = 10;

    // Physics
    public static final float MAX_SPEED = 25.0f;
    public static final float FRICTION = 0.98f;
    public static final float WALL_BOUNCE = 0.85f;

    // Controls
    public static final float MOUSE_SENSITIVITY = 0.25f;
    public static final float KEYBOARD_SPEED = 10.0f;
    public static final float AI_SPEED = 8.0f;

    // Game
    public static final int WIN_SCORE = 7;
    public static final int GOAL_RESET_DELAY = 1500;
    // Game states
    public static final int STATE_MENU = 0;
    public static final int STATE_PLAYING = 1;
    public static final int STATE_PAUSED = 2;
    public static final int STATE_GAME_OVER = 3;
    public static final int STATE_SETTINGS = 4;

    // Colors - Modern Theme
    public static final Color BACKGROUND_COLOR = new Color(20, 25, 40);
    public static final Color TABLE_COLOR = new Color(0, 120, 60);
    public static final Color TABLE_BORDER = new Color(15, 15, 88);
    public static final Color GOAL_COLOR = new Color(255, 215, 0, 200);
    public static final Color PUCK_COLOR = new Color(40, 40, 40);
    public static final Color PLAYER1_COLOR = new Color(220, 60, 60);     // Red
    public static final Color PLAYER2_COLOR = new Color(60, 140, 255);    // Blue
    public static final Color AI_COLOR = new Color(100, 220, 100);        // Green for AI

    // UI Colors
    public static final Color UI_BACKGROUND = new Color(30, 35, 50, 200);
    public static final Color UI_TEXT = new Color(8, 8, 84);
    public static final Color UI_ACCENT = new Color(255, 195, 0);

    // Font Colors
    public static final Color FONT_COLOR_WHITE = new Color(49, 49, 132);
    public static final Color FONT_COLOR_YELLOW = new Color(255, 215, 0);
    public static final Color FONT_COLOR_SHADOW = new Color(0, 0, 0, 180);

    // Theme options
    public enum Theme {
        CLASSIC,
        MODERN,
        DARK,
        ICE
    }

    public static Theme CURRENT_THEME = Theme.MODERN;

    public static Color getPlayer2Color(boolean isAI) {
        return isAI ? AI_COLOR : PLAYER2_COLOR;
    }

    public static Color getTableColor() {
        switch(CURRENT_THEME) {
            case CLASSIC:
                return new Color(0, 100, 40);
            case MODERN:
                return new Color(0, 120, 60);
            case DARK:
                return new Color(0, 80, 30);
            case ICE:
                return new Color(200, 230, 255);
            default:
                return TABLE_COLOR;
        }
    }

    // Font methods
    public static Font getFont(int size, boolean bold) {
        int style = bold ? Font.BOLD : Font.PLAIN;
        String fontName = bold ? "Segoe UI Black" : "Segoe UI";
        try {
            return new Font(fontName, style, size);
        } catch (Exception e) {
            // Fallback to Arial if font not available
            return new Font(bold ? "Arial Black" : "Arial", style, size);
        }
    }

    public static Font getImpactFont(int size) {
        try {
            return new Font("Impact", Font.BOLD, size);
        } catch (Exception e) {
            return new Font("Arial Black", Font.BOLD, size);
        }
    }
}