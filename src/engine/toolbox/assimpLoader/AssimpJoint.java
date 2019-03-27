package engine.toolbox.assimpLoader;

import org.joml.Matrix4f;
import org.lwjgl.assimp.AIBone;

import static engine.toolbox.assimpLoader.AssimpLoader.readMatrix4f;

public class AssimpJoint {
    private static final String TAG = "AssimpJoint";
    private final String name;
    private Matrix4f offsetMatrix;

    private AssimpJoint(String name) {
        this.name = name;
    }



    public static AssimpJoint load(AIBone bone) {
        AssimpJoint data = new AssimpJoint(bone.mName().dataString());
        data.setOffsetMatrix(readMatrix4f(bone.mOffsetMatrix()));
        //Log.d(TAG,"read joint " + data.getName());
        return data;
    }

    private void setOffsetMatrix(Matrix4f offsetMatrix) {
        this.offsetMatrix = offsetMatrix;
    }

    public String getName() {
        return name;
    }

    public Matrix4f getOffsetMatrix() {
        return offsetMatrix;
    }
}
