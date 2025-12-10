package airhockey.core;

import airhockey.utils.Constants;
import java.awt.*;

public class HockeyTable {
    private float goalGlow = 0f;
    private boolean glowIncreasing = true;
    private float linePulse = 0f;

    public void update() {
        // Animate goal glow
        if (glowIncreasing) {
            goalGlow += 0.02f;
            if (goalGlow >= 1.0f) {
                goalGlow = 1.0f;
                glowIncreasing = false;
            }
        } else {
            goalGlow -= 0.02f;
            if (goalGlow <= 0.3f) {
                goalGlow = 0.3f;
                glowIncreasing = true;
            }
        }

        // Animate line pulse
        linePulse = 0.5f + 0.5f * (float)Math.sin(System.currentTimeMillis() * 0.001);
    }

    public void render(Graphics2D g2d) {
        // Draw table shadow
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRect(Constants.TABLE_X + 8, Constants.TABLE_Y + 8,
                Constants.TABLE_WIDTH, Constants.TABLE_HEIGHT);

        // Draw table surface with animated gradient
        GradientPaint tableGradient = new GradientPaint(
                Constants.TABLE_X, Constants.TABLE_Y,
                Constants.getTableColor().brighter(),
                Constants.TABLE_X, Constants.TABLE_Y + Constants.TABLE_HEIGHT,
                Constants.getTableColor().darker()
        );
        g2d.setPaint(tableGradient);
        g2d.fillRect(Constants.TABLE_X, Constants.TABLE_Y,
                Constants.TABLE_WIDTH, Constants.TABLE_HEIGHT);

        // Draw inner glow effect
        g2d.setColor(new Color(255, 255, 255, 20));
        g2d.fillRect(Constants.TABLE_X + 5, Constants.TABLE_Y + 5,
                Constants.TABLE_WIDTH - 10, Constants.TABLE_HEIGHT - 10);

        // Draw table border with 3D effect
        g2d.setColor(new Color(220, 220, 240));
        g2d.setStroke(new BasicStroke(8));
        g2d.drawRect(Constants.TABLE_X, Constants.TABLE_Y,
                Constants.TABLE_WIDTH, Constants.TABLE_HEIGHT);

        // Draw inner border with pulse effect
        g2d.setColor(new Color(180, 180, 200, (int)(150 + linePulse * 105)));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(Constants.TABLE_X + 10, Constants.TABLE_Y + 10,
                Constants.TABLE_WIDTH - 20, Constants.TABLE_HEIGHT - 20);

        // Draw center line with glow
        int centerY = Constants.TABLE_Y + Constants.TABLE_HEIGHT / 2;
        g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10, new float[]{15, 10}, 0));
        g2d.setColor(new Color(255, 255, 255, (int)(150 + linePulse * 105)));
        g2d.drawLine(Constants.TABLE_X + 20, centerY,
                Constants.TABLE_X + Constants.TABLE_WIDTH - 20, centerY);

        // Draw center circle with animation
        g2d.setStroke(new BasicStroke(3 + linePulse));
        int circleSize = 120;
        g2d.drawOval(
                Constants.WINDOW_WIDTH/2 - circleSize/2,
                centerY - circleSize/2,
                circleSize, circleSize
        );

        // Draw corner circles with glow
        int cornerRadius = 40;
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.drawOval(Constants.TABLE_X - cornerRadius/2, Constants.TABLE_Y - cornerRadius/2,
                cornerRadius, cornerRadius);
        g2d.drawOval(Constants.TABLE_X + Constants.TABLE_WIDTH - cornerRadius/2,
                Constants.TABLE_Y - cornerRadius/2, cornerRadius, cornerRadius);
        g2d.drawOval(Constants.TABLE_X - cornerRadius/2,
                Constants.TABLE_Y + Constants.TABLE_HEIGHT - cornerRadius/2,
                cornerRadius, cornerRadius);
        g2d.drawOval(Constants.TABLE_X + Constants.TABLE_WIDTH - cornerRadius/2,
                Constants.TABLE_Y + Constants.TABLE_HEIGHT - cornerRadius/2,
                cornerRadius, cornerRadius);

        // Draw goals with animated glow
        int glowAlpha = 100 + (int)(goalGlow * 155);
        g2d.setColor(new Color(255, 215, 0, glowAlpha));

        // Top goal glow
        g2d.fillRect(Constants.WINDOW_WIDTH/2 - Constants.GOAL_WIDTH/2 - 15,
                Constants.TABLE_Y - 10,
                Constants.GOAL_WIDTH + 30, 20);

        // Bottom goal glow
        g2d.fillRect(Constants.WINDOW_WIDTH/2 - Constants.GOAL_WIDTH/2 - 15,
                Constants.TABLE_Y + Constants.TABLE_HEIGHT - 10,
                Constants.GOAL_WIDTH + 30, 20);

        // Draw goal lines with highlight
        g2d.setColor(Constants.GOAL_COLOR);
        g2d.setStroke(new BasicStroke(6));

        // Top goal
        g2d.drawLine(
                Constants.WINDOW_WIDTH/2 - Constants.GOAL_WIDTH/2,
                Constants.TABLE_Y,
                Constants.WINDOW_WIDTH/2 + Constants.GOAL_WIDTH/2,
                Constants.TABLE_Y
        );

        // Bottom goal
        g2d.drawLine(
                Constants.WINDOW_WIDTH/2 - Constants.GOAL_WIDTH/2,
                Constants.TABLE_Y + Constants.TABLE_HEIGHT,
                Constants.WINDOW_WIDTH/2 + Constants.GOAL_WIDTH/2,
                Constants.TABLE_Y + Constants.TABLE_HEIGHT
        );

        // Draw goal areas with pulse
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(new Color(255, 255, 255, 50 + (int)(goalGlow * 50)));

        // Top goal area
        g2d.drawRect(Constants.WINDOW_WIDTH/2 - Constants.GOAL_WIDTH/2 - 20,
                Constants.TABLE_Y,
                Constants.GOAL_WIDTH + 40,
                80);

        // Bottom goal area
        g2d.drawRect(Constants.WINDOW_WIDTH/2 - Constants.GOAL_WIDTH/2 - 20,
                Constants.TABLE_Y + Constants.TABLE_HEIGHT - 80,
                Constants.GOAL_WIDTH + 40,
                80);

        // Draw decorative elements
        drawDecorativeElements(g2d);
    }

    private void drawDecorativeElements(Graphics2D g2d) {
        // Draw sparkle effects around table
        long time = System.currentTimeMillis();
        g2d.setColor(new Color(255, 255, 255, 100));

        for (int i = 0; i < 8; i++) {
            float angle = (float)(time * 0.001 + i * Math.PI / 4);
            float radius = 450;
            int x = (int)(Constants.WINDOW_WIDTH/2 + Math.cos(angle) * radius);
            int y = (int)(Constants.TABLE_Y + Constants.TABLE_HEIGHT/2 + Math.sin(angle) * radius * 0.6);

            float sparkleSize = 3 + (float)Math.sin(time * 0.002 + i) * 2;
            g2d.fillOval(x, y, (int)sparkleSize, (int)sparkleSize);
        }
    }
}