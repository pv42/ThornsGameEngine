package thornsPong;

import org.joml.Vector2f;

public class PongGame {
    static final int REFLECT_DISTANCE = 10;
    static final int GAME_X_SIZE = 800;
    static final int GAME_Y_SIZE = 600;
    static final int BALL_SIZE = 8;
    static final float REFLECT_SIZE = 60;
    private static final float MOVESTEP = 212;
    private static final float BALL_SPEED = 514;
    private int score_left = 0;
    private int score_right = 0;
    private Vector2f ballPosition = new Vector2f();
    private Vector2f ballVelocity = new Vector2f();
    private float left_y;
    private float right_y;
    public PongGame() {
        resetBallAndReflects();
    }

    private void resetBallAndReflects() {
        ballPosition.set(GAME_X_SIZE/2, GAME_Y_SIZE/2);
        ballVelocity.set(100, 1).normalize().mul(BALL_SPEED);
        left_y = GAME_Y_SIZE / 2;
        right_y = GAME_Y_SIZE / 2;
    }

    protected Vector2f getBallPosition() {
        return ballPosition;
    }

    protected float getLeft_y() {
        return left_y;
    }

    protected float getRight_y() {
        return right_y;
    }

    protected void update(float timeStep, boolean klu, boolean kld, boolean kru, boolean krd) {
        if(klu) moveLeft(- MOVESTEP * timeStep);
        if(kld) moveLeft(MOVESTEP * timeStep);
        if(kru) moveRight(- MOVESTEP * timeStep);
        if(krd) moveRight(MOVESTEP * timeStep);
        ballPosition.add(new Vector2f(ballVelocity).mul(timeStep));
        if(ballPosition.y > GAME_Y_SIZE - BALL_SIZE/2) {
            ballPosition.y = 2 * (GAME_Y_SIZE - BALL_SIZE/2) - ballPosition.y ;
            ballVelocity.y = - ballVelocity.y;
        }
        if(ballPosition.y < BALL_SIZE/2 ) {
            ballPosition.y = BALL_SIZE - ballPosition.y;
            ballVelocity.y = - ballVelocity.y;
        }
        if(ballPosition.x > GAME_X_SIZE - REFLECT_DISTANCE - BALL_SIZE/2) {
            if(ballPosition.y < right_y + REFLECT_SIZE /2 + BALL_SIZE/2 && ballPosition.y > right_y - REFLECT_SIZE /2 - BALL_SIZE/2) {
                ballPosition.x = 2 * (GAME_X_SIZE - REFLECT_DISTANCE - BALL_SIZE / 2) - ballPosition.x;
                ballVelocity.set(-0.3f, (ballPosition.y - right_y )/ REFLECT_SIZE).normalize().mul(BALL_SPEED);
            } else {
                score_left ++;
                resetBallAndReflects();
            }
        }
        if(ballPosition.x < BALL_SIZE/2 + REFLECT_DISTANCE) {
            if(ballPosition.y < left_y + REFLECT_SIZE /2 + BALL_SIZE/2 && ballPosition.y > left_y - REFLECT_SIZE /2 - BALL_SIZE/2) {
                ballPosition.x = BALL_SIZE + 2 * REFLECT_DISTANCE - ballPosition.x;
                ballVelocity.set(0.3f, (ballPosition.y - left_y )/ REFLECT_SIZE).normalize().mul(BALL_SPEED);
            } else {
                score_right ++;
                resetBallAndReflects();
            }
        }
    }

    protected void moveLeft(float value) {
        left_y += value;
        if(left_y < REFLECT_SIZE /2) left_y = REFLECT_SIZE /2;
        if(left_y > GAME_Y_SIZE - REFLECT_SIZE /2) left_y = GAME_Y_SIZE - REFLECT_SIZE /2;
    }

    protected void moveRight(float value) {
        right_y += value;
        if(right_y < REFLECT_SIZE /2) right_y = REFLECT_SIZE /2;
        if(right_y > GAME_Y_SIZE - REFLECT_SIZE /2) right_y = GAME_Y_SIZE - REFLECT_SIZE /2;
    }

    public int getScore_left() {
        return score_left;
    }

    public int getScore_right() {
        return score_right;
    }

}

