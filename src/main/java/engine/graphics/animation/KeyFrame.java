package engine.graphics.animation;

import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

/**
 * a keyframe is a pose of joints at a specific timestamp, the joint transformations are stored in a hash map mapped by
 * their name
 *
 * @author pv42
 * @see Animation
 */
public class KeyFrame {

    private final Map<String, Matrix4f> jointData;
    private final float timestamp;

    /**
     * creates a keyframe at a given timestamp without any joint transformations
     *
     * @param timeStamp timestamp of the keyframe, used to interpolate between keyframes in animations
     */
    public KeyFrame(float timeStamp) {
        this.jointData = new HashMap<>();
        this.timestamp = timeStamp;
    }

    /**
     * add a pair of joint-name and transformation matrix to the keyframe, the transformation contains the information
     * about the joints pose at the keyframes timestamp
     *
     * @param name joints name
     * @param data joints transformation matrix
     */
    public void addJointData(String name, Matrix4f data) {
        jointData.put(name, data);
    }

    /**
     * gets all the joint data from the keyframe as a map with joint-names as keys and transformations as values
     *
     * @return joint data
     */
    Map<String, Matrix4f> getJointData() {
        return jointData;
    }

    /**
     * gets the keyframes timestamp
     *
     * @return timestamp
     */
    public float getTimestamp() {
        return timestamp;
    }


}
