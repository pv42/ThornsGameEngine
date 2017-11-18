package thornsPong;

import engine.graphics.entities.Entity;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class PongGame {
    static final float REFLECT_DISTANCE = 0.03f;
    static final float GAME_X_SIZE = 1.5f;
    static final float GAME_Y_SIZE = 1.5f;
    static final float BALL_SIZE = 0.02f;
    static final float REFLECT_SIZE = 0.2f;
    private static final float MOVESTEP = 0.5f;
    private static final float BALL_SPEED = 1f;
    private int score_left = 0;
    private int score_right = 0;
    private Vector2f ballVelocity = new Vector2f();
    private Entity leftPaddle;
    private Entity rightPaddle;
    private Entity ball;
    public PongGame(Entity rightPaddle, Entity leftPaddle, Entity ball) {
        this.leftPaddle = leftPaddle;
        this.rightPaddle = rightPaddle;
        this.ball = ball;
        resetBallAndReflects();
    }

    private void resetBallAndReflects() {
        ball.setPosition(0, 0, 0);
        ballVelocity.set(100, 1).normalize().mul(BALL_SPEED);
        leftPaddle.setPosition(-0.75f,0,0);
        rightPaddle.setPosition(0.75f,0,0);
    }


    protected void update(float timeStep, boolean klu, boolean kld, boolean kru, boolean krd) {
        if(klu) moveLeft(MOVESTEP * timeStep);
        if(kld) moveLeft(-MOVESTEP * timeStep);
        if(kru) moveRight(MOVESTEP * timeStep);
        if(krd) moveRight(-MOVESTEP * timeStep);
        Vector3f ballPosition = ball.getPosition();
        ballPosition.add(new Vector3f(ballVelocity,0).mul(timeStep));
        if(ballPosition.y > GAME_Y_SIZE/2) {
            ballPosition.y = GAME_Y_SIZE - ballPosition.y ;
            ballVelocity.y = - ballVelocity.y;
        }
        if(ballPosition.y < -GAME_Y_SIZE/2 ) {
            ballPosition.y = -GAME_Y_SIZE - ballPosition.y;
            ballVelocity.y = - ballVelocity.y;
        }
        if(ballPosition.x > GAME_X_SIZE/2 - REFLECT_DISTANCE - BALL_SIZE/2) {
            if(ballPosition.y < rightPaddle.getPosition().y + REFLECT_SIZE /2 + BALL_SIZE/2 && ballPosition.y > rightPaddle.getPosition().y - REFLECT_SIZE /2 - BALL_SIZE/2) {
                ballPosition.x = GAME_X_SIZE - 2 * REFLECT_DISTANCE - BALL_SIZE - ballPosition.x;
                ballVelocity.set(-0.3f, (ballPosition.y - rightPaddle.getPosition().y)/ REFLECT_SIZE).normalize().mul(BALL_SPEED);
            } else {
                score_left ++;
                resetBallAndReflects();
            }
        }
        if(ballPosition.x < BALL_SIZE/2 + REFLECT_DISTANCE - GAME_X_SIZE/2) {
            if(ballPosition.y < leftPaddle.getPosition().y() + REFLECT_SIZE /2 + BALL_SIZE/2 && ballPosition.y > leftPaddle.getPosition().y() - REFLECT_SIZE /2 - BALL_SIZE/2) {
                ballPosition.x = - GAME_X_SIZE  + BALL_SIZE + 2 * REFLECT_DISTANCE - ballPosition.x ;
                ballVelocity.set(0.3f, (ballPosition.y - leftPaddle.getPosition().y() )/ REFLECT_SIZE).normalize().mul(BALL_SPEED);
            } else {
                score_right ++;
                resetBallAndReflects();
            }
        }
    }

    private void moveLeft(float value) {
        leftPaddle.increasePosition(0, value,0);
        if(leftPaddle.getPosition().y() < REFLECT_SIZE /2 - GAME_Y_SIZE/2)
            leftPaddle.setPositionElement(REFLECT_SIZE /2 - GAME_Y_SIZE/2, 1);
        //ifsa(leftPaddle.getPosition().y() > -REFLECT_SIZE /2 + GAME_Y_SIZE/2)
            //leftPaddle.setPositionElement(- REFLECT_SIZE /2 + GAME_Y_SIZE/2, 1);
    }

    protected void moveRight(float value) {
        rightPaddle.increasePosition(0, value,0);
        if(rightPaddle.getPosition().y() < REFLECT_SIZE /2 - GAME_Y_SIZE/2)
            rightPaddle.setPositionElement(REFLECT_SIZE /2 - GAME_Y_SIZE/2, 1);
        if(rightPaddle.getPosition().y() > -REFLECT_SIZE /2 + GAME_Y_SIZE/2)
            rightPaddle.setPositionElement(- REFLECT_SIZE /2 + GAME_Y_SIZE/2,1);
    }

    public int getScore_left() {
        return score_left;
    }

    public int getScore_right() {
        return score_right;
    }

}

