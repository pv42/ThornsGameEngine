package thornsPong;

import engine.graphics.Entity;
import engine.graphics.glglfwImplementation.entities.GLEntity;
import engine.physics.PhysicalEntity;
import engine.physics.PhysicsEngine;
import org.joml.Vector3f;

public class PongGame {
    static final float GAME_X_SIZE = 1.4f;
    static final float GAME_Y_SIZE = 1.5f;
    static final float BALL_SIZE = 0.02f;
    private static final float REFLECT_SIZE = 0.2f;
    private static final float MOVESTEP = 0.5f;
    private static final float BALL_SPEED = 1f;
    private int leftScore = 0;
    private int rightScore = 0;
    private Entity leftPaddle;
    private Entity rightPaddle;
    private PhysicalEntity ball;
    private boolean paused = false;
    public PongGame(GLEntity rightPaddle, GLEntity leftPaddle, PhysicalEntity ball) {
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

    protected void update(float timeStep, boolean klu, boolean kld, boolean kru, boolean krd, boolean flipPaused) {
        if(flipPaused) paused = !paused;
        if(paused) return;
        PhysicsEngine.performStep(timeStep);
        if(klu) moveLeft(MOVESTEP * timeStep);
        if(kld) moveLeft(-MOVESTEP * timeStep);
        if(kru) moveRight(MOVESTEP * timeStep);
        if(krd) moveRight(-MOVESTEP * timeStep);
        Vector3f ballPosition = ball.getPosition();
        if(ballPosition.x > GAME_X_SIZE/2) {
            leftScore++;
            resetBallAndPaddles();
        }
        if(ballPosition.x < - GAME_X_SIZE/2) {
            rightScore++;
            resetBallAndPaddles();
        }
    }

    void flipPaused() {
        paused = !paused;
    }

    private void moveLeft(float value) {
        movePaddle(leftPaddle, value);
    }

    private void moveRight(float value) {
        movePaddle(rightPaddle, value);
    }

    private void movePaddle(Entity paddle, float value) {
        paddle.increasePosition(0, value,0);
        if(paddle.getPosition().y() < REFLECT_SIZE /2 - GAME_Y_SIZE/2)
            paddle.setPositionElement(REFLECT_SIZE /2 - GAME_Y_SIZE/2, 1);
        if(paddle.getPosition().y() > -REFLECT_SIZE /2 + GAME_Y_SIZE/2)
            paddle.setPositionElement(- REFLECT_SIZE /2 + GAME_Y_SIZE/2,1);
    }

    public int getLeftScore() {
        return leftScore;
    }

    public int getRightScore() {
        return rightScore;
    }

}

