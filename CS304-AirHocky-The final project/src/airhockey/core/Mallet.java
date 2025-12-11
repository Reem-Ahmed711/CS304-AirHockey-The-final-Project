package airhockey.core;

import airhockey.utils.Constants;
import java.awt.*;

public class Mallet {
    private float x, y;
    private float targetX, targetY;
    private float velocityX, velocityY;
    private int radius;
    private boolean isPlayer1;
    private boolean mouseControl;

    public Mallet(float x, float y, boolean isPlayer1) {
        this.x = x;
        this.y = y;
        this.targetX = x;
        this.targetY = y;
        this.velocityX = 0;
        this.velocityY = 0;
        this.radius = Constants.MALLET_RADIUS;
        this.isPlayer1 = isPlayer1;
        this.mouseControl = isPlayer1; // Player 1 uses mouse by default
    }

    public void update() {
        // Smooth movement towards target
        float dx = targetX - x;
        float dy = targetY - y;
        float distance = (float)Math.sqrt(dx * dx + dy * dy);

        if (distance > 1) {
            float speed = isPlayer1 ?
                    (mouseControl ? Constants.MOUSE_SENSITIVITY * distance : Constants.KEYBOARD_SPEED) :
                    Constants.AI_SPEED;

            speed = Math.min(speed, distance * 0.5f);
            velocityX = (dx / distance) * speed;
            velocityY = (dy / distance) * speed;
        } else {
            velocityX *= 0.9f;
            velocityY *= 0.9f;
        }

        // Update position
        x += velocityX;
        y += velocityY;

        // Apply boundaries
        enforceBoundaries();
    }

    private void enforceBoundaries() {
        // Left/Right boundaries
        if (x - radius < Constants.TABLE_X) {
            x = Constants.TABLE_X + radius;
            velocityX = 0;
        } else if (x + radius > Constants.TABLE_X + Constants.TABLE_WIDTH) {
            x = Constants.TABLE_X + Constants.TABLE_WIDTH - radius;
            velocityX = 0;
        }

        if (isPlayer1) {
            // Player 1
            if (y - radius < Constants.TABLE_Y + Constants.TABLE_HEIGHT / 2) {
                y = Constants.TABLE_Y + Constants.TABLE_HEIGHT / 2 + radius;
                velocityY = 0;
            }
            if (y + radius > Constants.TABLE_Y + Constants.TABLE_HEIGHT) {
                y = Constants.TABLE_Y + Constants.TABLE_HEIGHT - radius;
                velocityY = 0;
            }
        } else {
            // Player 2
            if (y - radius < Constants.TABLE_Y) {
                y = Constants.TABLE_Y + radius;
                velocityY = 0;
            }
            if (y + radius > Constants.TABLE_Y + Constants.TABLE_HEIGHT / 2) {
                y = Constants.TABLE_Y + Constants.TABLE_HEIGHT / 2 - radius;
                velocityY = 0;
            }
        }
    }

    public void render(Graphics2D g2d) {
        // Draw glow effect when moving
        if (Math.abs(velocityX) > 0.5f || Math.abs(velocityY) > 0.5f) {
            Color glowColor = isPlayer1 ?
                    new Color(255, 100, 100, 100) :
                    new Color(100, 150, 255, 100);
            g2d.setColor(glowColor);
            g2d.fillOval((int)x - radius - 5, (int)y - radius - 5,
                    radius * 2 + 10, radius * 2 + 10);
        }

        // Draw mallet
        Color malletColor = isPlayer1 ? Constants.PLAYER1_COLOR : Constants.PLAYER2_COLOR;
        g2d.setColor(malletColor);
        g2d.fillOval((int)x - radius, (int)y - radius,
                radius * 2, radius * 2);

        // Draw border
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawOval((int)x - radius, (int)y - radius,
                radius * 2, radius * 2);

        // Draw center dot
        g2d.setColor(Color.WHITE);
        g2d.fillOval((int)x - 5, (int)y - 5, 10, 10);

        // Draw player label
        g2d.setFont(Constants.getFont(14, true));
        String label = isPlayer1 ? "P1" : "P2";
        FontMetrics fm = g2d.getFontMetrics();
        int labelWidth = fm.stringWidth(label);
        g2d.drawString(label, (int)x - labelWidth/2, (int)y + 5);
    }

    // Movement methods
    public void moveTo(float x, float y) {
        this.targetX = x;
        this.targetY = y;
    }

    public void move(float dx, float dy) {
        this.targetX = this.x + dx;
        this.targetY = this.y + dy;
    }

    // Keyboard movement
    public void moveKeyboard(float dx, float dy) {
        this.targetX += dx;
        this.targetY += dy;
        this.mouseControl = false;
    }

    // Getters
    public float getX() { return x; }
    public float getY() { return y; }
    public float getVelocityX() { return velocityX; }
    public float getVelocityY() { return velocityY; }
    public int getRadius() { return radius; }
    public boolean isPlayer1() { return isPlayer1; }
    public boolean isMouseControl() { return mouseControl; }

    // Setters
    public void setX(float x) { this.x = x; this.targetX = x; }
    public void setY(float y) { this.y = y; this.targetY = y; }
    public void setMouseControl(boolean mouseControl) { this.mouseControl = mouseControl; }
}