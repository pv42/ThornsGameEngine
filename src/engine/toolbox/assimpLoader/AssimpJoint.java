package engine.toolbox.assimpLoader;

import org.joml.Matrix4f;
import org.lwjgl.assimp.AIBone;
import org.lwjgl.assimp.AIVertexWeight;

import java.util.ArrayList;
import java.util.List;

import static engine.toolbox.assimpLoader.AssimpScene.readMatrix4f;

public class AssimpJoint {
    private static final String TAG = "AssimpJoint";
    private final String name;
    private Matrix4f offsetMatrix;
    private List<Integer> weightIndices;
    private List<Float> weightValues;

    private AssimpJoint(String name) {
        this.name = name;
        weightIndices = new ArrayList<>();
        weightValues = new ArrayList<>();
    }


    public static AssimpJoint load(AIBone bone) {
        AssimpJoint data = new AssimpJoint(bone.mName().dataString());
        data.setOffsetMatrix(readMatrix4f(bone.mOffsetMatrix()));
        data.getOffsetMatrix().invert();
        data.getOffsetMatrix().transpose();
        //data.setOffsetMatrix(data.getOffsetMatrix().transpose());
        System.out.println(data.name + ".ibm\n" + data.getOffsetMatrix());
        for (int i = 0; i < bone.mNumWeights(); i++) {
            AIVertexWeight vertexWeight = bone.mWeights().get(i);
            data.addWeight(vertexWeight.mVertexId(), vertexWeight.mWeight());
        }
        //Log.d(TAG,"read joint " + data.getName());
        return data;
    }

    private void addWeight(int index, float weightValue) {
        weightIndices.add(index);
        weightValues.add(weightValue);
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

    List<Integer> getWeightsIndices() {
        return weightIndices;
    }

    List<Float> getWeightValues() {
        return weightValues;
    }
}
