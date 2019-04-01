package engine;

import engine.graphics.cameras.ThreeDimensionCamera;
import engine.graphics.glglfwImplementation.entities.FirstPersonPlayer;

/**
   Created by pv42 on 17.06.16.
 */
public class FirstPersonCamera extends ThreeDimensionCamera {
    private static float DISTANCE_MIN = -1;
    private static float DISTANCE_MAX = 100;

    private FirstPersonPlayer player;
    private float distanceFromPlayer = -1;

    public FirstPersonCamera(FirstPersonPlayer player) {
        super();
        this.player = player;
    }

    public void move() {
        calculateZoom();
        calculatePitch();
        calculateYaw();
        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance,verticalDistance);

    }

    private void calculateYaw() {
        setYaw(180 - player.getRz());
    }


    private void calculateZoom() {
        //float zoomLevel = Mouse.getDWheel() * 0.01f; TODO
        float zoomLevel = 0;
        distanceFromPlayer -= zoomLevel;
        if(distanceFromPlayer < DISTANCE_MIN) distanceFromPlayer = DISTANCE_MIN;
        if(distanceFromPlayer > DISTANCE_MAX) distanceFromPlayer = DISTANCE_MAX;

    }
    private void calculatePitch() {
        setPitch(player.getHeightAngle());
    }
    private float calculateHorizontalDistance() {
        return (float) (distanceFromPlayer * Math.cos(Math.toRadians(getPitch())));
    }
    private float calculateVerticalDistance() {
        return (float) (distanceFromPlayer * Math.sin(Math.toRadians(getPitch())));
    }
    private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
        float theta = player.getRy();
        float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
        getPosition().x = player.getEyePosition().x - offsetX;
        getPosition().y = player.getEyePosition().y + verticalDistance;
        getPosition().z = player.getEyePosition().z - offsetZ;

    }
}
