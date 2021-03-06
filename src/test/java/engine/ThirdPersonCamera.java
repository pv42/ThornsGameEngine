package engine;

import engine.graphics.cameras.ThreeDimensionCamera;
import engine.inputs.InputHandler;
import engine.inputs.listeners.InputEventListener;
import org.joml.Vector2f;

import static engine.inputs.InputEvent.MOUSE_EVENT;
import static engine.inputs.InputEvent.SCROLL;

/**
 * Created by pv42 on 17.06.16.
 *
 * @author pv42
 */
public class ThirdPersonCamera extends ThreeDimensionCamera {
    private static float DISTANCE_MIN = 0;
    private static float DISTANCE_MAX = 100;
    private Player player;
    private float distanceFromPlayer = 80;
    private float angleAroundPlayer = 0;

    public ThirdPersonCamera(Player player) {
        super();
        this.player = player;
        InputHandler.addListener(new InputEventListener(MOUSE_EVENT, SCROLL, 0) {
            @Override
            public void onOccur(Vector2f v2f) {
                distanceFromPlayer -= v2f.y * 3f;
            }
        });
    }

    public void move() {
        calculateZoom();
        calculatePitch();
        calculateAngleToPlayer();
        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCamaraPosition(horizontalDistance, verticalDistance);
        setYaw(180 - (angleAroundPlayer + player.getRz()));
    }

    private void calculateZoom() {
        float zoomLevel = 0;
        distanceFromPlayer -= zoomLevel;
        if (distanceFromPlayer < DISTANCE_MIN) distanceFromPlayer = DISTANCE_MIN;
        if (distanceFromPlayer > DISTANCE_MAX) distanceFromPlayer = DISTANCE_MAX;

    }

    private void calculatePitch() {
        // // TODO: 13.09.2016 cP
        /*if(Mouse.isButtonDown(1)) {
            float pitchChange = Mouse.getDY() * 0.1f;
            setPitch(getPitch() - pitchChange);
        }*/
    }

    private void calculateAngleToPlayer() {
        //// TODO: 13.09.2016  cAtP
        /*if(Mouse.isButtonDown(0)) {
            float angelChange = Mouse.getDX() * 0.3f;
            angleAroundPlayer -= angelChange;
        }*/
    }

    private float calculateHorizontalDistance() {
        return (float) (distanceFromPlayer * Math.cos(Math.toRadians(getPitch())));
    }

    private float calculateVerticalDistance() {
        return (float) (distanceFromPlayer * Math.sin(Math.toRadians(getPitch())));
    }

    private void calculateCamaraPosition(float horizontalDistance, float verticalDistance) {
        float theta = angleAroundPlayer + player.getRz();
        float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
        getPosition().x = player.getEyePosition().x - offsetX;
        getPosition().y = player.getEyePosition().y + verticalDistance;
        getPosition().z = player.getEyePosition().z - offsetZ;
    }
}
