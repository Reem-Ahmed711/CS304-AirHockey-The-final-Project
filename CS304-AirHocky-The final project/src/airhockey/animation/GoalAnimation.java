package airhockey.animation;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class GoalAnimation {
    private List<Particle> particles = new ArrayList<>();
    private String scorerText;
    private long startTime;
    private boolean active = false;
    private Color color;

    class Particle {
        float x, y;
        float vx, vy;
        float size;
        Color color;
        long birthTime;
        float lifeTime;

        Particle(float x, float y, Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.size = 10 + (float)Math.random() * 20;
            this.vx = (float)(Math.random() * 8 - 4);
            this.vy = (float)(Math.random() * -6 - 2);
            this.birthTime = System.currentTimeMillis();
            this.lifeTime = 1000 + (float)Math.random() * 500;
        }

        void update() {
            x += vx;
            y += vy;
            vy += 0.1f;
            size *= 0.98f;
        }

        boolean isAlive() {
            return System.currentTimeMillis() - birthTime < lifeTime;
        }
    }

    public void triggerGoal(String scorer, Color color, int centerX, int centerY) {
        this.scorerText = scorer;
        this.color = color;
        this.startTime = System.currentTimeMillis();
        this.active = true;
        this.particles.clear();

        // Create particles
        for (int i = 0; i < 50; i++) {
            particles.add(new Particle(centerX, centerY, color));
        }
    }

    public void update() {
        if (!active) return;

        // Update particles
        particles.removeIf(particle -> !particle.isAlive());
        for (Particle p : particles) {
            p.update();
        }

        // Add new particles while animation is active
        if (System.currentTimeMillis() - startTime < 2000) {
            if (particles.size() < 30 && Math.random() < 0.3) {
                particles.add(new Particle(
                        600 + (float)(Math.random() * 200 - 100),
                        350 + (float)(Math.random() * 100 - 50),
                        color
                ));
            }
        } else {
            active = false;
        }
    }

    public void render(Graphics2D g2d) {
        if (!active) return;

        long elapsed = System.currentTimeMillis() - startTime;

        // Draw particles
        for (Particle p : particles) {
            float alpha = 1.0f - ((System.currentTimeMillis() - p.birthTime) / p.lifeTime);
            g2d.setColor(new Color(p.color.getRed(), p.color.getGreen(), p.color.getBlue(), (int)(alpha * 255)));
            g2d.fillOval((int)p.x, (int)p.y, (int)p.size, (int)p.size);
        }

        // Draw goal text with animation
        if (elapsed < 2000) {
            float scale = 1.0f;
            float alpha = 1.0f;

            if (elapsed < 500) {
                // Scale in
                scale = elapsed / 500f;
            } else if (elapsed > 1500) {
                // Fade out
                alpha = 1.0f - (elapsed - 1500) / 500f;
            }

            // Draw "GOAL!" text
            g2d.setFont(new Font("Impact", Font.BOLD, 80));
            String goalText = "GOAL!";
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(goalText);

            // Text shadow
            g2d.setColor(new Color(0, 0, 0, (int)(alpha * 150)));
            g2d.drawString(goalText,
                    600 - textWidth/2 + 3,
                    300 + 3);

            // Main text with gradient
            GradientPaint gradient = new GradientPaint(
                    600 - textWidth/2, 300, color,
                    600 + textWidth/2, 300, color.brighter()
            );
            g2d.setPaint(gradient);
            g2d.drawString(goalText,
                    600 - textWidth/2,
                    300);

            // Draw scorer text
            if (elapsed > 300) {
                float scorerAlpha = Math.min(1.0f, (elapsed - 300) / 300f);
                if (elapsed > 1500) {
                    scorerAlpha = alpha;
                }

                g2d.setFont(new Font("Arial", Font.BOLD, 36));
                FontMetrics sm = g2d.getFontMetrics();
                int scorerWidth = sm.stringWidth(scorerText);

                g2d.setColor(new Color(255, 255, 255, (int)(scorerAlpha * 255)));
                g2d.drawString(scorerText,
                        600 - scorerWidth/2,
                        350);
            }
        }
    }

    public boolean isActive() {
        return active;
    }
}