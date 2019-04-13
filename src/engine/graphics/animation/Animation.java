package engine.graphics.animation;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * A animation of a raw model, based on KeyFrames and joints, joint matrices are calculated by interpolation between
 * timed keyframes
 *
 * @author pv42
 * @see KeyFrame
 */
public class Animation {
    private final List<KeyFrame> keyframes;
    private float lastKeyFrameTime = 0;

    /**
     * creates an Animation with no bones or keyframes
     */
    public Animation() {
        keyframes = new ArrayList<>();
    }

    /**
     * adds a keyframe, which hold a timestamp and joint-matrices
     *
     * @param keyFrame keyframe to add
     */
    public void addKeyFrame(KeyFrame keyFrame) {
        if (keyFrame.getTimestamp() > lastKeyFrameTime) lastKeyFrameTime = keyFrame.getTimestamp();
        keyframes.add(keyFrame);
    }

    /**
     * gets a animation transformation at a given time for a given joint identified by its name, the matrix is
     * calculated by interpolating between the closest keyframe before and after the given time
     *
     * @param time      timestamp at which the the animation currently is
     * @param jointName joints name of the joint to get the matrix for
     * @return joint animation transform at the given time
     */
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
        Quaternionf prevQuaternion = new Quaternionf().setFromNormalized(prevMatrix);
        Quaternionf nextQuaternion = new Quaternionf().setFromNormalized(nextMatrix);
        Quaternionf rotation = new Quaternionf();
        prevQuaternion.nlerp(nextQuaternion, progress, rotation);
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

    /**
     * gets the timestamp of the last keyframe in the animation, this is usually the length of the animation, however
     * if the first keyframes timestamp is not zero, this is not the case
     *
     * @return last keyframe's timestamp
     */
    public float getLastKeyFrameTime() {
        return lastKeyFrameTime;
    }
}
