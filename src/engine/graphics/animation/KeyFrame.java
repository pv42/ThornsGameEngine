package engine.graphics.animation;

import engine.toolbox.Matrix4fDbg;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

public class KeyFrame {

    private Map<String, Matrix4fDbg> jointData;
    private float timestamp;

    public KeyFrame(float timeStamp) {
        this.jointData = new HashMap<>();
        this.timestamp = timeStamp;
    }

    public void addJointData(String name, Matrix4fDbg data) {
        jointData.put(name,data);
    }

    public Map<String, Matrix4fDbg> getJointData() {
        return jointData;
    }

    public float getTimestamp() {
        return timestamp;
    }


}
