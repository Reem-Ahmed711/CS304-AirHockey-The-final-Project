package airhockey.ai;

import airhockey.core.Mallet;
import airhockey.core.Puck;
import airhockey.utils.Constants;

public class AI {
    private float lastPuckX, lastPuckY;
    private float puckVelocityX, puckVelocityY;
    private int difficulty;

    private long reactionDelay;
    private float aiSpeed;
    private long lastDecisionTime = 0;

    private float predictionAccuracy;
    private float errorRange;

    private int strategyMode = 1;
    private long lastStrategyChange = 0;

    private float cachedTargetX, cachedTargetY;
    private float lastTargetX, lastTargetY;

    public AI(int difficulty) {
        setDifficulty(difficulty);
        this.lastPuckX = 0;
        this.lastPuckY = 0;
        this.puckVelocityX = 0;
        this.puckVelocityY = 0;
        this.cachedTargetX = 0;
        this.cachedTargetY = 0;
    }


    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
        switch (difficulty) {
            case 1: // EASY
                reactionDelay = 250;
                predictionAccuracy = 0.7f;
                errorRange = 80f;
                aiSpeed = 6f;
                break;
            case 2: // MEDIUM
                reactionDelay = 150;
                predictionAccuracy = 0.85f;
                errorRange = 50f;
                aiSpeed = 8f;
                break;
            case 3: // HARD
                reactionDelay = 80;
                predictionAccuracy = 0.95f;
                errorRange = 20f;
                aiSpeed = 10f;
                break;
            case 4: // EXPERT
                reactionDelay = 30;
                predictionAccuracy = 1f;
                errorRange = 5f;
                aiSpeed = 12f;
                break;
            default:
                reactionDelay = 150;
                predictionAccuracy = 0.85f;
                errorRange = 50f;
                aiSpeed = 8f;
        }
    }


    public void reset() {
        this.lastPuckX = 0;
        this.lastPuckY = 0;
        this.puckVelocityX = 0;
        this.puckVelocityY = 0;
        this.cachedTargetX = 0;
        this.cachedTargetY = 0;
        this.lastDecisionTime = 0;
        this.strategyMode = 1;
        this.lastStrategyChange = 0;
    }


    public String getDifficultyName() {
        switch (this.difficulty) {
            case 1: return "EASY";
            case 2: return "MEDIUM";
            case 3: return "HARD";
            case 4: return "EXPERT";
            default: return "UNKNOWN";
        }
    }

    public void update(Mallet mallet, Puck puck, long currentTime) {
        float dx = puck.getX() - lastPuckX;
        float dy = puck.getY() - lastPuckY;

        puckVelocityX = dx;
        puckVelocityY = dy;

        lastPuckX = puck.getX();
        lastPuckY = puck.getY();

        if (currentTime - lastDecisionTime > reactionDelay) {
            lastDecisionTime = currentTime;

            float predictedX = puck.getX() + puckVelocityX * predictionAccuracy;
            float predictedY = puck.getY() + puckVelocityY * predictionAccuracy;

            float tableXRight = Constants.TABLE_X + Constants.TABLE_WIDTH;
            float tableYTop = Constants.TABLE_Y;
            float tableYMid = Constants.TABLE_Y + Constants.TABLE_HEIGHT / 2;

            predictedX = clamp(predictedX, Constants.TABLE_X + Constants.MALLET_RADIUS, tableXRight - Constants.MALLET_RADIUS);

            predictedY = clamp(predictedY,
                    tableYTop + Constants.MALLET_RADIUS,
                    tableYMid - Constants.MALLET_RADIUS);

            cachedTargetX = predictedX + randomOffset();
            cachedTargetY = predictedY + randomOffset();

            if (currentTime - lastStrategyChange > 10000) {
                strategyMode = strategyMode == 1 ? 2 : 1;
                lastStrategyChange = currentTime;
            }
        }

        mallet.moveTo(cachedTargetX, cachedTargetY);

        lastTargetX = cachedTargetX;
        lastTargetY = cachedTargetY;
    }

    private float randomOffset() {
        return (float)((Math.random() - 0.5) * 2 * errorRange);
    }

    private float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }
}