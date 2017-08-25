package engine.graphics.animation;

import org.joml.Matrix4f;
import org.joml.Quaterniond;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Animation {
    private Map<Float, KeyFrame> keyframes;
    private List<String> jointIds;

    public Animation(List<String> jointIds) {
        jointIds = jointIds;
        keyframes = new HashMap<>();
    }

    public void addKeyFrame(KeyFrame keyFrame) {
        keyframes.put(keyFrame.getTimestamp(), keyFrame);
    }

    public Matrix4f getMatrix(float time, String jointName) {
        Set<Float> timestamps = keyframes.keySet();
        float prevTS = 0, nextTS = Float.POSITIVE_INFINITY;
        for(float timestamp: timestamps) {
            if(timestamp > time && timestamp < nextTS) nextTS = timestamp;
            if(timestamp < time && timestamp > prevTS) prevTS = timestamp;
        }
        float progress = (time - prevTS)/(nextTS - prevTS);//0 .. 1
        Matrix4f prevMatrix = keyframes.get(prevTS).getJointData().get(jointName);
        Matrix4f nextMatrix = keyframes.get(nextTS).getJointData().get(jointName);
        Quaterniond prevQuat = new Quaterniond().setFromNormalized(prevMatrix);
        Quaterniond nextQuat = new Quaterniond().setFromNormalized(nextMatrix);
        Quaterniond rotation = new Quaterniond();
        prevQuat.nlerp(nextQuat,progress,rotation);
        Vector3f prevTranslation = new Vector3f(prevMatrix.m30(), prevMatrix.m31(), prevMatrix.m32());
        Vector3f nextTranslation = new Vector3f(nextMatrix.m30(), nextMatrix.m31(), nextMatrix.m32());
        prevTranslation.mul(prevTranslation);
        nextTranslation.mul(1 - progress);
        prevTranslation.add(nextTranslation);
        Matrix4f matrix = new Matrix4f();
        matrix.translate(prevTranslation);
        matrix.mul(rotation.get(new Matrix4f()));
        return matrix;
    }
}
