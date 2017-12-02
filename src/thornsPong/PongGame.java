package thornsPong;

import engine.graphics.entities.Entity;
import engine.physics.PhysicalEntity;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class PongGame {
    static final float GAME_X_SIZE = 1.5f;
    static final float GAME_Y_SIZE = 1.5f;
    static final float BALL_SIZE = 0.02f;
    private static final float REFLECT_SIZE = 0.2f;
    private static final float MOVESTEP = 0.5f;
    private static final float BALL_SPEED = 1f;
    private int score_left = 0;
    private int score_right = 0;
    private Entity leftPaddle;
    private Entity rightPaddle;
    private PhysicalEntity ball;
    public PongGame(Entity rightPaddle, Entity leftPaddle, PhysicalEntity ball) {
        this.leftPaddle = leftPaddle;
        this.rightPaddle = rightPaddle;
        this.ball = ball;
        resetBallAndPaddles();
    }

    private void resetBallAndPaddles() {
        ball.setPosition(0, 0, 0);
        ball.setVelocity(new Vector3f(100, 1, 0).normalize().mul(BALL_SPEED));
        leftPaddle.setPosition(-0.75f,0,0);
        rightPaddle.setPosition(0.75f,0,0);
    }

    protected void update(float timeStep, boolean klu, boolean kld, boolean kru, boolean krd) {
        if(klu) moveLeft(MOVESTEP * timeStep);
        if(kld) moveLeft(-MOVESTEP * timeStep);
        if(kru) moveRight(MOVESTEP * timeStep);
        if(krd) moveRight(-MOVESTEP * timeStep);
        Vector3f ballPosition = ball.getPosition();
        if(ballPosition.y > GAME_Y_SIZE/2) {
            ballPosition.y = GAME_Y_SIZE - ballPosition.y;
            ball.getVelocity().y = - ball.getVelocity().y;
        }
        if(ballPosition.y < -GAME_Y_SIZE/2 ) {
            ballPosition.y = -GAME_Y_SIZE - ballPosition.y;
            ball.getVelocity().y = - ball.getVelocity().y;
        }
        if(ballPosition.x > GAME_X_SIZE/2) {
            score_left ++;
            resetBallAndPaddles();
        }
        if(ballPosition.x < - GAME_X_SIZE/2) {
            score_right ++;
            resetBallAndPaddles();
        }
    }

    private void moveLeft(float value) {
        leftPaddle.increasePosition(0, value,0);
        if(leftPaddle.getPosition().y() < REFLECT_SIZE /2 - GAME_Y_SIZE/2)
            leftPaddle.setPositionElement(REFLECT_SIZE /2 - GAME_Y_SIZE/2, 1);
        if(leftPaddle.getPosition().y() > -REFLECT_SIZE /2 + GAME_Y_SIZE/2)
            leftPaddle.setPositionElement(- REFLECT_SIZE /2 + GAME_Y_SIZE/2, 1);
    }

    private void moveRight(float value) {
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

