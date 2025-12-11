package airhockey.core;

import airhockey.utils.Constants;
import java.awt.*;

public class Puck {
    private float x, y;
    private float velocityX, velocityY;
    private int radius;

    public Puck(float x, float y) {
        this.x = x;
        this.y = y;
        this.velocityX = 0;
        this.velocityY = 0;
        this.radius = Constants.PUCK_RADIUS;
    }

    public void update() {
        // Apply velocity
        x += velocityX;
        y += velocityY;

        // Apply friction
        velocityX *= Constants.FRICTION;
        velocityY *= Constants.FRICTION;

        // Cap maximum speed
        float speed = (float)Math.sqrt(velocityX * velocityX + velocityY * velocityY);
        if (speed > Constants.MAX_SPEED) {
            velocityX = (velocityX / speed) * Constants.MAX_SPEED;
            velocityY = (velocityY / speed) * Constants.MAX_SPEED;
        }

        // Stop if very slow
        if (Math.abs(velocityX) < 0.1f) velocityX = 0;
        if (Math.abs(velocityY) < 0.1f) velocityY = 0;
    }

    public void render(Graphics2D g2d) {
        // Draw glow effect when moving fast
        float speed = (float)Math.sqrt(velocityX * velocityX + velocityY * velocityY);

        int alphaValue = (int)(Math.min(speed, 10) * 20);
        alphaValue = Math.max(0, Math.min(255, alphaValue));

        if (speed > 2) {
            Color glowColor = new Color(255, 255, 200, alphaValue);
            g2d.setColor(glowColor);
            g2d.fillOval((int)x - radius - 8, (int)y - radius - 8,
                    radius * 2 + 16, radius * 2 + 16);
        }

        // Draw puck with gradient effect
        GradientPaint gradient = new GradientPaint(
                (int)x - radius, (int)y - radius, new Color(60, 60, 60),
                (int)x + radius, (int)y + radius, new Color(30, 30, 30)
        );
        g2d.setPaint(gradient);
        g2d.fillOval((int)x - radius, (int)y - radius,
                radius * 2, radius * 2);

        // Draw outer ring
        g2d.setColor(new Color(150, 150, 150));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawOval((int)x - radius, (int)y - radius,
                radius * 2, radius * 2);

        // Draw inner circle
        g2d.setColor(new Color(220, 220, 220));
        g2d.fillOval((int)x - radius/2, (int)y - radius/2,
                radius, radius);

        // Draw puck number
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        String num = "●";
        FontMetrics fm = g2d.getFontMetrics();
        int numWidth = fm.stringWidth(num);
        g2d.drawString(num, (int)x - numWidth/2, (int)y + 5);
    }

    public boolean isInGoal(boolean topGoal) {
        if (topGoal) {
            // Top goal
            return y - radius < Constants.TABLE_Y &&
                    Math.abs(x - (Constants.TABLE_X + Constants.TABLE_WIDTH/2)) < Constants.GOAL_WIDTH/2;
        } else {
            // Bottom goal
            return y + radius > Constants.TABLE_Y + Constants.TABLE_HEIGHT &&
                    Math.abs(x - (Constants.TABLE_X + Constants.TABLE_WIDTH/2)) < Constants.GOAL_WIDTH/2;
        }
    }

    public void reset() {
        this.x = Constants.WINDOW_WIDTH/2;
        this.y = Constants.WINDOW_HEIGHT/2;
        this.velocityX = 0;
        this.velocityY = 0;
    }

    // Getters
    public float getX() { return x; }
    public float getY() { return y; }
    public float getVelocityX() { return velocityX; }
    public float getVelocityY() { return velocityY; }
    public int getRadius() { return radius; }

    // Setters
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public void setVelocityX(float vx) { this.velocityX = vx; }
    public void setVelocityY(float vy) { this.velocityY = vy; }
}