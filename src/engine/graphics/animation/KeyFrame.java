package engine.graphics.animation;

import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

public class KeyFrame {

    private Map<String, Matrix4f> jointData;
    private float timestamp;

    public KeyFrame(float timeStamp) {
        this.jointData = new HashMap<>();
    }

    public void addJointData(String name, Matrix4f data) {
        jointData.put(name,data);
    }

    public Map<String, Matrix4f> getJointData() {
        return jointData;
    }

    public float getTimestamp() {
        return timestamp;
    }
}
