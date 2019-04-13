package engine.toolbox.assimpLoader;

import engine.graphics.animation.Joint;
import engine.graphics.glglfwImplementation.GLLoader;
import engine.graphics.glglfwImplementation.models.GLMaterializedModel;
import engine.graphics.glglfwImplementation.models.GLRawModel;
import engine.graphics.materials.Material;
import engine.toolbox.Log;
import engine.toolbox.Matrix4fDbg;
import org.joml.Matrix4f;
import org.lwjgl.assimp.AIBone;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIVector3D;

import java.util.ArrayList;
import java.util.List;

import static engine.toolbox.assimpLoader.AssimpScene.readBuffer3f;
import static engine.toolbox.assimpLoader.AssimpScene.readBuffer3fTo2f;

public class AssimpMesh {
    private static final String TAG = "AssimpMesh";
    private final float[] pos;
    private final int[] indices;
    private String name;
    private float[] normal;
    private float[] uv;
    private List<AssimpJoint> joints;
    private int vcount;
    private AssimpMaterial assimpMaterial;
    private int[] weightIndices;
    private float[] weightValues;

    private AssimpMesh(String name, float[] pos, int vcount, int[] indices) {
        this.name = name;
        this.pos = pos;
        this.vcount = vcount;
        this.indices = indices;
    }

    public static AssimpMesh load(AIMesh mesh, List<AssimpMaterial> materials) {
        Log.d(TAG, "loading mesh " + mesh.mName().dataString());
        float[] v = readVertices(mesh);
        float[] normals = readNormals(mesh);
        float[] texCoords = readTextureCoordinates(mesh);
        int[] indices = readIndices(mesh);
        List<AssimpJoint> joints = readJoints(mesh);
        AssimpMesh data = new AssimpMesh(mesh.mName().dataString(), v, mesh.mNumVertices(), indices); // new this(...)
        data.setJoints(joints);
        data.setNormal(normals);
        data.setUv(texCoords);
        try {
            data.setMaterial(materials.get(mesh.mMaterialIndex()));
        } catch (Exception ex) {
            Log.e(TAG, "could not set material");
        }
        data.createWeights(3);
        return data;
    }

    private static int[] readIndices(AIMesh mesh) {
        int fcount = mesh.mNumFaces();
        int indicesLenght = 0;
        for (int i = 0; i < fcount; i++) {
            AIFace face = mesh.mFaces().get(i);
            for (int j = 0; j < face.mNumIndices(); j++) {
                indicesLenght++;
            }
        }
        int[] indices = new int[indicesLenght];
        Log.d(TAG, fcount + " faces");
        for (int i = 0; i < fcount; i++) {
            AIFace face = mesh.mFaces().get(i);
            for (int j = 0; j < face.mNumIndices(); j++) {
                indices[i * 3 + j] = face.mIndices().get(j);
            }
        }
        return indices;
    }

    private static List<AssimpJoint> readJoints(AIMesh mesh) {
        int count = mesh.mNumBones();
        List<AssimpJoint> joints = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            AIBone bone = AIBone.create(mesh.mBones().get(i));
            joints.add(AssimpJoint.load(bone));
        }
        return joints;
    }

    private static float[] readTextureCoordinates(AIMesh mesh) {
        int count = mesh.mNumVertices();
        AIVector3D.Buffer buffer = mesh.mTextureCoords(0);
        if (buffer == null) {
            Log.d(TAG, "no uv data");
            return new float[0]; // no normal data
        }
        return readBuffer3fTo2f(buffer, count);
    }

    private static float[] readNormals(AIMesh mesh) {
        int vcount = mesh.mNumVertices();
        AIVector3D.Buffer buffer = mesh.mNormals();
        if (buffer == null) {
            Log.d(TAG, "no normal data");
            return new float[0]; // no normal data
        }
        return readBuffer3f(buffer, vcount);
    }

    private static float[] readVertices(AIMesh mesh) {
        int vcount = mesh.mNumVertices();
        AIVector3D.Buffer buffer = mesh.mVertices();
        return readBuffer3f(buffer, vcount);
    }

    private void setMaterial(AssimpMaterial assimpMaterial) {
        this.assimpMaterial = assimpMaterial;
    }

    private void createWeights(int maxWeightsPerVertex) {
        weightIndices = new int[vcount * maxWeightsPerVertex];
        weightValues = new float[vcount * maxWeightsPerVertex];
        for (int jointIndex = 0; jointIndex < joints.size(); jointIndex++) {
            AssimpJoint joint = joints.get(jointIndex);
            for (int i = 0; i < joint.getWeightsIndices().size(); i++) {
                int vertexIndex = joint.getWeightsIndices().get(i);
                if(vertexIndex >= vcount) {
                    Log.e(TAG, "vertex weight index is out of range");
                    continue;
                }
                float weight = joint.getWeightValues().get(i);
                for (int pos = vertexIndex * maxWeightsPerVertex; pos < (vertexIndex + 1) * maxWeightsPerVertex; pos++) {
                    if (weight > weightValues[pos]) {
                        moveWeightsInArrays(pos, (vertexIndex + 1) * maxWeightsPerVertex - 1);
                        weightIndices[pos] = jointIndex;
                        weightValues[pos] = weight;
                        break;
                    }
                }
            }
        }
    }

    private void moveWeightsInArrays(int pos, int limit) {
        if (pos >= limit || weightValues[pos] == 0) return;
        moveWeightsInArrays(pos + 1, limit);
        weightIndices[pos + 1] = weightIndices[pos];
        weightValues[pos + 1] = weightValues[pos];
    }

    public float[] getPos() {
        return pos;
    }

    public float[] getNormal() {
        return normal;
    }

    private void setNormal(float[] normal) {
        this.normal = normal;
    }

    public float[] getUv() {
        return uv;
    }

    private void setUv(float[] uv) {
        this.uv = uv;
    }

    public List<AssimpJoint> getJoints() {
        return joints;
    }

    private void setJoints(List<AssimpJoint> joints) {
        this.joints = joints;
    }

    public AssimpJoint getJointByName(String name) {
        for (AssimpJoint joint : joints) {
            if (joint.getName().equals(name)) return joint;
        }
        return null;
    }

    public GLRawModel createRawModel(boolean animation) {
        Log.d(TAG, "loading mesh raw model:" + name);
        GLRawModel model;
        if (joints.size() > 0 && animation) {
            model = GLLoader.loadToVAOAnimated(pos, uv, normal, indices, weightIndices, weightValues, getEngineJoints());
        } else {
            model = GLLoader.loadToVAO(pos, uv, normal, indices);
        }
        return model;
    }

    private List<Joint> getEngineJoints() {
        List<Joint> jointList = new ArrayList<>();
        for (AssimpJoint assimpJoint : joints) {
            Joint joint = new Joint(assimpJoint.getName(), new Matrix4f(assimpJoint.getOffsetMatrix()));
            AssimpJoint parentAssimpJoint = assimpJoint.getParent();
            if(parentAssimpJoint != null) {
                String parentName = parentAssimpJoint.getName();
                Joint parentJoint = null;
                for (int i = 0; i < jointList.size(); i++) {
                    if(jointList.get(i).getId().equals(parentName)) parentJoint = jointList.get(i);
                }
                joint.setParent(parentJoint);
            }
            jointList.add(joint);
        }
        return jointList;
    }

    public GLMaterializedModel createMaterializedModel(boolean animation) {
        GLRawModel model = createRawModel(animation);
        Material material = assimpMaterial.getMaterial();
        return new GLMaterializedModel(model, material);
    }

    public GLMaterializedModel createMaterializedModel() {
        return createMaterializedModel(true);
    }


    @Override
    public String toString() {
        return "Mesh " + name + " with " + vcount + " vertices";
    }
}
