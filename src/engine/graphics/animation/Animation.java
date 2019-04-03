package engine.graphics.animation;

import engine.toolbox.Matrix4fDbg;
import org.joml.Matrix4f;
import org.joml.Quaterniond;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Animation {
    private static final String TAG = "Animation";
    private List<KeyFrame> keyframes;
    private float lastKeyFrameTime = 0;

    public Animation() {
        keyframes = new ArrayList<>();
    }

    public void addKeyFrame(KeyFrame keyFrame) {
        if(keyFrame.getTimestamp() > lastKeyFrameTime) lastKeyFrameTime = keyFrame.getTimestamp();
        keyframes.add(keyFrame);
    }

    Matrix4f getMatrix(float time, String jointName) {
        float prevTS = 0;
        float nextTS = Float.POSITIVE_INFINITY;
        Matrix4f nextMatrix = null;
        Matrix4f prevMatrix = keyframes.get(0).getJointData().get(jointName);
        for (KeyFrame frame : keyframes) {
            float timestamp = frame.getTimestamp();
            if (timestamp > time && timestamp < nextTS) { //find next timestamp
                nextTS = timestamp;
                nextMatrix = frame.getJointData().get(jointName);
            }
            if (timestamp <= time && timestamp >= prevTS) { // find prev timestamp
                prevTS = timestamp;
                prevMatrix = frame.getJointData().get(jointName);
            }
        }
        if (nextTS == Float.POSITIVE_INFINITY) return prevMatrix; // past last timestamp
        float progress = (time - prevTS) / (nextTS - prevTS);//between 0,1; current interpolate
        Quaterniond prevQuat = new Quaterniond().setFromNormalized(prevMatrix);
        Quaterniond nextQuat = new Quaterniond().setFromNormalized(nextMatrix);
        Quaterniond rotation = new Quaterniond();
        prevQuat.nlerp(nextQuat, progress, rotation);
        Vector3f prevTranslation = prevMatrix.getTranslation(new Vector3f());
        Vector3f nextTranslation = nextMatrix.getTranslation(new Vector3f());
        prevTranslation.mul(progress);
        nextTranslation.mul(1f - progress);
        prevTranslation.add(nextTranslation);
        Matrix4f matrix = new Matrix4f();
        matrix.translate(prevTranslation);
        matrix.mul(rotation.get(new Matrix4f()));
        return matrix;
    }

    public float getLastKeyFrameTime() {
        return lastKeyFrameTime;
    }
}
