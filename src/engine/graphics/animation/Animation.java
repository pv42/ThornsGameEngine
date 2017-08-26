package engine.graphics.animation;

import engine.toolbox.Log;
import org.joml.Matrix4f;
import org.joml.Quaterniond;
import org.joml.Vector3f;

import java.util.*;

public class Animation {
    private static final String TAG = "Animation";
    private List<KeyFrame> keyframes;

    public Animation() {
        keyframes = new ArrayList<>();
    }

    public void addKeyFrame(KeyFrame keyFrame) {
        keyframes.add(keyFrame);
    }

    Matrix4f getMatrix(float time, String jointName) {
        float prevTS = 0, nextTS = Float.POSITIVE_INFINITY;
        Matrix4f nextMatrix = null;
        Matrix4f prevMatrix = keyframes.get(0).getJointData().get(jointName);
        for(KeyFrame frame: keyframes) {
            float timestamp = frame.getTimestamp();
            if(timestamp > time && timestamp < nextTS) {
                nextTS = timestamp;
                nextMatrix = frame.getJointData().get(jointName);

            }
            if(timestamp < time && timestamp > prevTS) {
                prevTS = timestamp;
                prevMatrix = frame.getJointData().get(jointName);
            }
        }
        if(nextTS == Float.POSITIVE_INFINITY) return prevMatrix;
        float progress = (time - prevTS)/(nextTS - prevTS);//0 .. 1
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
