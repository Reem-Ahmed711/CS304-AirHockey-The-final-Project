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

        float nx = dx / distance;
        float ny = dy / distance;

        float relVelX = puck.getVelocityX() - mallet.getVelocityX();
        float relVelY = puck.getVelocityY() - mallet.getVelocityY();

        float speedAlongNormal = relVelX * nx + relVelY * ny;

        if (speedAlongNormal > 0) return;

        float impulse = -speedAlongNormal * 1.4f;

        puck.setVelocityX(puck.getVelocityX() + nx * impulse);
        puck.setVelocityY(puck.getVelocityY() + ny * impulse);

        puck.setVelocityX(puck.getVelocityX() + mallet.getVelocityX() * 0.6f);
        puck.setVelocityY(puck.getVelocityY() + mallet.getVelocityY() * 0.6f);

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

        if (x - radius < Constants.TABLE_X) {
            puck.setX(Constants.TABLE_X + radius);
            puck.setVelocityX(Math.abs(puck.getVelocityX()) * 0.9f);
        }
        else if (x + radius > Constants.TABLE_X + Constants.TABLE_WIDTH) {
            puck.setX(Constants.TABLE_X + Constants.TABLE_WIDTH - radius);
            puck.setVelocityX(-Math.abs(puck.getVelocityX()) * 0.9f);
        }

        if (y - radius < Constants.TABLE_Y) {
            if (Math.abs(x - (Constants.TABLE_X + Constants.TABLE_WIDTH/2)) > Constants.GOAL_WIDTH/2) {
                puck.setY(Constants.TABLE_Y + radius);
                puck.setVelocityY(Math.abs(puck.getVelocityY()) * 0.9f);
            }
        }
        else if (y + radius > Constants.TABLE_Y + Constants.TABLE_HEIGHT) {
            if (Math.abs(x - (Constants.TABLE_X + Constants.TABLE_WIDTH/2)) > Constants.GOAL_WIDTH/2) {
                puck.setY(Constants.TABLE_Y + Constants.TABLE_HEIGHT - radius);
                puck.setVelocityY(-Math.abs(puck.getVelocityY()) * 0.9f);
            }
        }
    }
}