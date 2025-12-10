package airhockey.ai;

import airhockey.core.Mallet;
import airhockey.core.Puck;
import airhockey.utils.Constants;

public class AI {
    private float lastPuckX, lastPuckY;
    private float puckVelocityX, puckVelocityY;
    private int difficulty; // 1=easy, 2=medium, 3=hard, 4=expert
    private long reactionDelay;
    private float predictionAccuracy;
    private float errorRange;
    private int strategyMode = 0; // 0=defensive, 1=neutral, 2=offensive
    private long lastStrategyChange = 0;

    public AI(int difficulty) {
        this.difficulty = difficulty;
        this.lastPuckX = 0;
        this.lastPuckY = 0;
        this.puckVelocityX = 0;
        this.puckVelocityY = 0;

        // Set difficulty parameters
        setDifficultyParameters(difficulty);
    }

    private void setDifficultyParameters(int diff) {
        switch(diff) {
            case 1: // Easy
                reactionDelay = 300;
                predictionAccuracy = 0.5f;
                errorRange = 80f;
                break;
            case 2: // Medium
                reactionDelay = 180;
                predictionAccuracy = 0.7f;
                errorRange = 50f;
                break;
            case 3: // Hard
                reactionDelay = 100;
                predictionAccuracy = 0.85f;
                errorRange = 25f;
                break;
            case 4: // Expert
                reactionDelay = 50;
                predictionAccuracy = 0.95f;
                errorRange = 10f;
                break;
            default:
                reactionDelay = 180;
                predictionAccuracy = 0.7f;
                errorRange = 50f;
        }
    }

    public void update(Mallet aiMallet, Puck puck, Mallet playerMallet) {
        // Calculate puck velocity
        puckVelocityX = puck.getX() - lastPuckX;
        puckVelocityY = puck.getY() - lastPuckY;
        lastPuckX = puck.getX();
        lastPuckY = puck.getY();

        // Update strategy occasionally
        updateStrategy(puck, playerMallet);

        float targetX, targetY;
        float puckSpeed = (float)Math.sqrt(puckVelocityX * puckVelocityX + puckVelocityY * puckVelocityY);

        // Different AI behaviors based on difficulty
        switch (difficulty) {
            case 1: // Easy - Random movements
                targetX = puck.getX() + (float)(Math.random() * errorRange * 2 - errorRange);
                targetY = puck.getY() + (float)(Math.random() * errorRange - errorRange/2);
                break;

            case 2: // Medium - Basic following
                targetX = linearPrediction(puck, 0.7f);
                targetY = strategicPositioning(puck, aiMallet, true);
                break;

            case 3: // Hard - Advanced prediction
                targetX = predictWithBounces(puck, 1.0f);
                targetY = strategicPositioning(puck, aiMallet, false);
                // Add some anticipation
                if (puckSpeed > 8 && puck.getY() < Constants.TABLE_Y + Constants.TABLE_HEIGHT/3) {
                    targetX = predictGoalShot(puck, playerMallet);
                }
                break;

            case 4: // Expert - Smart strategy
                targetX = expertPrediction(puck, playerMallet);
                targetY = expertPositioning(puck, aiMallet, playerMallet);
                break;

            default:
                targetX = puck.getX();
                targetY = puck.getY();
        }

        // Add reaction delay based on difficulty
        float delayFactor = (float)reactionDelay / 1000f;
        targetX += puckVelocityX * delayFactor * predictionAccuracy;
        targetY += puckVelocityY * delayFactor * predictionAccuracy;

        // Add random error based on difficulty
        if (difficulty < 4) { // Expert has minimal error
            targetX += (float)(Math.random() * errorRange * 2 - errorRange);
            targetY += (float)(Math.random() * errorRange - errorRange/2);
        }

        // Constrain to AI's half with strategy consideration
        float minY = Constants.TABLE_Y + aiMallet.getRadius() + 30;
        float maxY = Constants.TABLE_Y + Constants.TABLE_HEIGHT/2 - aiMallet.getRadius() - 30;

        if (strategyMode == 2) { // Offensive
            maxY = Math.min(maxY, Constants.TABLE_Y + Constants.TABLE_HEIGHT/2 - 80);
        } else if (strategyMode == 0) { // Defensive
            minY = Math.max(minY, Constants.TABLE_Y + 100);
        }

        targetX = Math.max(Constants.TABLE_X + aiMallet.getRadius() + 10,
                Math.min(targetX, Constants.TABLE_X + Constants.TABLE_WIDTH - aiMallet.getRadius() - 10));
        targetY = Math.max(minY, Math.min(targetY, maxY));

        // Move towards target
        moveTowardsTarget(aiMallet, targetX, targetY, predictionAccuracy);
    }

    private void updateStrategy(Puck puck, Mallet playerMallet) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastStrategyChange > 5000) { // Change strategy every 5 seconds
            float puckY = puck.getY();
            float centerY = Constants.TABLE_Y + Constants.TABLE_HEIGHT/2;

            if (puckY < centerY - 100) {
                // Puck in AI's zone - defensive
                strategyMode = 0;
            } else if (puckY > centerY + 100) {
                // Puck in player's zone - offensive
                strategyMode = 2;
            } else {
                // Puck in neutral zone
                strategyMode = 1;
            }

            lastStrategyChange = currentTime;
        }
    }

    private float linearPrediction(Puck puck, float timeAhead) {
        return puck.getX() + puckVelocityX * timeAhead;
    }

    private float predictWithBounces(Puck puck, float timeAhead) {
        float predictedX = puck.getX();
        float predictedVelX = puckVelocityX;
        float steps = 8;
        float stepTime = timeAhead / steps;

        for (int i = 0; i < steps; i++) {
            predictedX += predictedVelX * stepTime;

            // Wall bounce prediction
            if (predictedX < Constants.TABLE_X + puck.getRadius()) {
                predictedX = Constants.TABLE_X + puck.getRadius();
                predictedVelX = -predictedVelX * 0.9f;
            } else if (predictedX > Constants.TABLE_X + Constants.TABLE_WIDTH - puck.getRadius()) {
                predictedX = Constants.TABLE_X + Constants.TABLE_WIDTH - puck.getRadius();
                predictedVelX = -predictedVelX * 0.9f;
            }
        }

        return predictedX;
    }

    private float predictGoalShot(Puck puck, Mallet playerMallet) {
        // Try to predict where player might shoot
        float playerX = playerMallet.getX();
        float centerX = Constants.TABLE_X + Constants.TABLE_WIDTH/2;

        // If player is on right side, protect right side of goal
        if (playerX > centerX + 50) {
            return Constants.TABLE_X + Constants.TABLE_WIDTH/2 + Constants.GOAL_WIDTH/3;
        }
        // If player is on left side, protect left side
        else if (playerX < centerX - 50) {
            return Constants.TABLE_X + Constants.TABLE_WIDTH/2 - Constants.GOAL_WIDTH/3;
        }
        // Center position
        else {
            return Constants.TABLE_X + Constants.TABLE_WIDTH/2;
        }
    }

    private float expertPrediction(Puck puck, Mallet playerMallet) {
        // Expert prediction with player analysis
        float puckSpeed = (float)Math.sqrt(puckVelocityX * puckVelocityX + puckVelocityY * puckVelocityY);

        if (puckSpeed < 3) {
            // Slow puck, move to intercept
            return puck.getX();
        }

        // Predict multiple bounces
        float predictedX = puck.getX();
        float predictedY = puck.getY();
        float predVelX = puckVelocityX;
        float predVelY = puckVelocityY;

        for (int i = 0; i < 6; i++) {
            predictedX += predVelX;
            predictedY += predVelY;

            // Wall collisions
            if (predictedX < Constants.TABLE_X + puck.getRadius() ||
                    predictedX > Constants.TABLE_X + Constants.TABLE_WIDTH - puck.getRadius()) {
                predVelX = -predVelX * 0.92f;
            }

            // Check if puck will reach AI's zone
            if (predictedY < Constants.TABLE_Y + Constants.TABLE_HEIGHT/3) {
                // Adjust based on player position
                float playerX = playerMallet.getX();
                if (Math.abs(predictedX - playerX) < 120) {
                    // Player might intercept, aim for gap
                    if (playerX > Constants.WINDOW_WIDTH/2) {
                        return Math.max(Constants.TABLE_X + 80, predictedX - 100);
                    } else {
                        return Math.min(Constants.TABLE_X + Constants.TABLE_WIDTH - 80, predictedX + 100);
                    }
                }
                break;
            }

            // Apply friction
            predVelX *= 0.98f;
            predVelY *= 0.98f;
        }

        return predictedX;
    }

    private float strategicPositioning(Puck puck, Mallet aiMallet, boolean simple) {
        float centerY = Constants.TABLE_Y + Constants.TABLE_HEIGHT/2;

        if (simple) {
            // Basic positioning
            if (puck.getY() < centerY - 50) {
                return centerY - 80; // Defensive
            } else {
                return centerY - 120; // Neutral
            }
        } else {
            // Advanced positioning
            float puckSpeed = (float)Math.sqrt(puckVelocityX * puckVelocityX + puckVelocityY * puckVelocityY);

            if (puckSpeed > 12 && puckVelocityY < -2) {
                // Fast puck coming toward goal
                return centerY - 60; // Very defensive
            } else if (puck.getY() > centerY + 80) {
                // Puck in player's zone
                return centerY - 140; // Offensive
            } else {
                return centerY - 100; // Neutral
            }
        }
    }

    private float expertPositioning(Puck puck, Mallet aiMallet, Mallet playerMallet) {
        float centerY = Constants.TABLE_Y + Constants.TABLE_HEIGHT/2;
        float puckSpeed = (float)Math.sqrt(puckVelocityX * puckVelocityX + puckVelocityY * puckVelocityY);

        switch (strategyMode) {
            case 0: // Defensive
                if (puckSpeed > 10 && puckVelocityY < -3) {
                    return centerY - 70; // Protect goal
                } else {
                    return centerY - 90; // Normal defense
                }

            case 1: // Neutral
                if (puck.getY() < centerY) {
                    return centerY - 100;
                } else {
                    return centerY - 120;
                }

            case 2: // Offensive
                // Try to intercept passes
                float playerX = playerMallet.getX();
                float puckX = puck.getX();

                if (Math.abs(playerX - puckX) > 150) {
                    // Player far from puck, move to center
                    return centerY - 130;
                } else {
                    // Player near puck, prepare for shot
                    return centerY - 150;
                }

            default:
                return centerY - 100;
        }
    }

    private void moveTowardsTarget(Mallet mallet, float targetX, float targetY, float accuracy) {
        float currentX = mallet.getX();
        float currentY = mallet.getY();
        float dx = targetX - currentX;
        float dy = targetY - currentY;
        float distance = (float)Math.sqrt(dx * dx + dy * dy);

        if (distance > 2) {
            // Expert AI has smoother movement
            float speed = difficulty == 4 ?
                    Constants.AI_SPEED * 1.2f * accuracy :
                    Constants.AI_SPEED * accuracy;

            // Smooth acceleration
            speed = Math.min(speed, distance * 0.6f);

            mallet.moveTo(currentX + (dx/distance) * speed,
                    currentY + (dy/distance) * speed);
        }
    }

    public void reset() {
        lastPuckX = 0;
        lastPuckY = 0;
        puckVelocityX = 0;
        puckVelocityY = 0;
        strategyMode = 1;
        lastStrategyChange = 0;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
        setDifficultyParameters(difficulty);
    }

    public int getDifficulty() {
        return difficulty;
    }

    public String getDifficultyName() {
        switch(difficulty) {
            case 1: return "EASY";
            case 2: return "MEDIUM";
            case 3: return "HARD";
            case 4: return "EXPERT";
            default: return "MEDIUM";
        }
    }
}

