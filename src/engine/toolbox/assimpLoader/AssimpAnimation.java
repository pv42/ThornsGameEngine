package engine.toolbox.assimpLoader;

import engine.graphics.animation.Animation;
import engine.graphics.animation.KeyFrame;
import engine.toolbox.Log;
import engine.toolbox.Matrix4fDbg;
import javafx.util.Pair;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.assimp.AIAnimation;
import org.lwjgl.assimp.AIMeshAnim;
import org.lwjgl.assimp.AINodeAnim;
import org.lwjgl.assimp.AIQuatKey;
import org.lwjgl.assimp.AIQuaternion;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.AIVectorKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssimpAnimation {
    private static final String TAG = "AssimpAnimation";
    private final String name;
    private double duration;
    private Map<String, List<Pair<Double, Matrix4f>>> keyFrameData;

    private AssimpAnimation(String name) {
        this.name = name;
        this.keyFrameData = new HashMap<>();
    }

    static AssimpAnimation load(AIAnimation aiAnimation) {
        AssimpAnimation animation = new AssimpAnimation(aiAnimation.mName().dataString());
        animation.setDuration(aiAnimation.mDuration());
        int numChannels = aiAnimation.mNumChannels();
        for (int i = 0; i < numChannels; i++) {
            AINodeAnim nodeAnim = AINodeAnim.create(aiAnimation.mChannels().get(i));
            loadChannel(nodeAnim, i < nodeAnim.mNumScalingKeys(), animation);
        }
        return animation;
    }

    private static void loadChannel(AINodeAnim nodeAnim, boolean loadScalingKey, AssimpAnimation animation) {
        int numFrames = nodeAnim.mNumPositionKeys();
        AIVectorKey.Buffer positionKeys = nodeAnim.mPositionKeys();
        AIVectorKey.Buffer scalingKeys = nodeAnim.mScalingKeys();
        AIQuatKey.Buffer rotationKeys = nodeAnim.mRotationKeys();
        String name = nodeAnim.mNodeName().dataString();
        int pre = nodeAnim.mPreState();
        int post = nodeAnim.mPostState();
        if (pre != 0 || post != 0) Log.w(TAG, "pre or post state are not 0 for node " + name);
        for (int j = 0; j < numFrames; j++) {
            AIVectorKey positionKey = positionKeys.get(j);
            double time = positionKey.mTime();
            AIVector3D aiPosition = positionKey.mValue();

            Matrix4f matrix = new Matrix4f().translate(aiPosition.x(), aiPosition.y(), aiPosition.z());

            AIQuaternion rotation = rotationKeys.get(j).mValue();
            Quaternionf quat = new Quaternionf(rotation.x(), rotation.y(), rotation.z(), rotation.w());
            matrix.rotate(quat);
            if (loadScalingKey) {
                positionKey = scalingKeys.get(j);
                aiPosition = positionKey.mValue();
                matrix.scale(aiPosition.x(), aiPosition.y(), aiPosition.z());
            }
            animation.addData(name, time, matrix);
        }
    }

    private static Quaternionf readQuaternion(AIQuaternion quaternion) {
        return new Quaternionf(quaternion.w(), quaternion.x(), quaternion.y(), quaternion.z());
    }

    private static Vector3f readVector3f(AIVector3D vector) {
        return new Vector3f(vector.x(), vector.y(), vector.z());
    }

    public Animation getAnimation() {
        Animation animation = new Animation();
        Map<Float, KeyFrame> keyFrames = new HashMap<>();
        for (String name : keyFrameData.keySet()) {
            List<Pair<Double, Matrix4f>> pairs = keyFrameData.get(name);
            for (Pair<Double, Matrix4f> pair : pairs) {
                float time = new Float(pair.getKey());
                keyFrames.computeIfAbsent(time, aFloat -> {
                    keyFrames.put(time, new KeyFrame(aFloat));
                    return null;
                });
                keyFrames.get(time).addJointData(name, new Matrix4fDbg(pair.getValue(), name + ".anM"));
            }
        }
        keyFrames.forEach((aFloat, keyFrame) -> animation.addKeyFrame(keyFrame));
        return animation;
    }

    private void setDuration(double duration) {
        this.duration = duration;
    }

    private void addData(String jointName, double time, Matrix4f transform) {
        if (!keyFrameData.containsKey(jointName)) {
            keyFrameData.put(jointName, new ArrayList<>());
        }
        keyFrameData.get(jointName).add(new Pair<>(time, transform));
    }
}
