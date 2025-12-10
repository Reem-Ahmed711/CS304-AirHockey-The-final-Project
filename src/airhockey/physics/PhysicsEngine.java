package airhockey.physics;

import airhockey.core.Mallet;
import airhockey.core.Puck;
import airhockey.utils.Constants;

public class PhysicsEngine {

    public static boolean checkCollision(Puck puck, Mallet mallet) {
        float dx = puck.getX() - mallet.getX();
        float dy = puck.getY() - mallet.getY();
        float distance = (float)Math.sqrt(dx * dx + dy * dy);
        return distance < (puck.getRadius() + mallet.getRadius());
    }

    public static void handleCollision(Puck puck, Mallet mallet) {
        float dx = puck.getX() - mallet.getX();
        float dy = puck.getY() - mallet.getY();
        float distance = (float)Math.sqrt(dx * dx + dy * dy);

        if (distance == 0) return;

        // Normalize collision vector
        float nx = dx / distance;
        float ny = dy / distance;

        // Calculate relative velocity
        float relVelX = puck.getVelocityX() - mallet.getVelocityX();
        float relVelY = puck.getVelocityY() - mallet.getVelocityY();

        float speedAlongNormal = relVelX * nx + relVelY * ny;

        // Only resolve if objects are moving towards each other
        if (speedAlongNormal > 0) return;

        // Calculate impulse
        float impulse = -speedAlongNormal * 1.4f;

        // Apply impulse to puck
        puck.setVelocityX(puck.getVelocityX() + nx * impulse);
        puck.setVelocityY(puck.getVelocityY() + ny * impulse);

        // Add some of mallet's momentum for realistic physics
        puck.setVelocityX(puck.getVelocityX() + mallet.getVelocityX() * 0.6f);
        puck.setVelocityY(puck.getVelocityY() + mallet.getVelocityY() * 0.6f);

        // Separate objects to prevent sticking
        float overlap = (puck.getRadius() + mallet.getRadius()) - distance;
        if (overlap > 0) {
            puck.setX(puck.getX() + nx * overlap * 1.2f);
            puck.setY(puck.getY() + ny * overlap * 1.2f);
        }
    }

    public static void handleWallCollision(Puck puck) {
        float x = puck.getX();
        float y = puck.getY();
        int radius = puck.getRadius();

        // Left wall
        if (x - radius < Constants.TABLE_X) {
            puck.setX(Constants.TABLE_X + radius);
            puck.setVelocityX(Math.abs(puck.getVelocityX()) * 0.9f);
        }
        // Right wall
        else if (x + radius > Constants.TABLE_X + Constants.TABLE_WIDTH) {
            puck.setX(Constants.TABLE_X + Constants.TABLE_WIDTH - radius);
            puck.setVelocityX(-Math.abs(puck.getVelocityX()) * 0.9f);
        }

        // Top wall (outside goal)
        if (y - radius < Constants.TABLE_Y) {
            if (Math.abs(x - (Constants.TABLE_X + Constants.TABLE_WIDTH/2)) > Constants.GOAL_WIDTH/2) {
                puck.setY(Constants.TABLE_Y + radius);
                puck.setVelocityY(Math.abs(puck.getVelocityY()) * 0.9f);
            }
        }
        // Bottom wall (outside goal)
        else if (y + radius > Constants.TABLE_Y + Constants.TABLE_HEIGHT) {
            if (Math.abs(x - (Constants.TABLE_X + Constants.TABLE_WIDTH/2)) > Constants.GOAL_WIDTH/2) {
                puck.setY(Constants.TABLE_Y + Constants.TABLE_HEIGHT - radius);
                puck.setVelocityY(-Math.abs(puck.getVelocityY()) * 0.9f);
            }
        }
    }
}