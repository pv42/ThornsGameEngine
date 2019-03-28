package engine.toolbox.assimpLoader;

import engine.graphics.glglfwImplementation.GLLoader;
import engine.graphics.glglfwImplementation.models.GLRawModel;
import engine.toolbox.Log;
import org.lwjgl.assimp.AIBone;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIVector3D;

import java.util.ArrayList;
import java.util.List;

import static engine.toolbox.assimpLoader.AssimpLoader.readBuffer3f;
import static engine.toolbox.assimpLoader.AssimpLoader.readBuffer3fTo2f;

public class AssimpMesh {
    private static final String TAG = "AssimpMesh";
    private final float[] pos;
    private final int[] indices;
    private String name;
    private float[] normal;
    private float[] uv;
    private List<AssimpJoint> joints;
    private int vcount;

    private AssimpMesh(String name, float[] pos, int vcount, int[] indices) {
        this.name = name;
        this.pos = pos;
        this.vcount = vcount;
        this.indices = indices;
    }

    public static AssimpMesh load(AIMesh mesh) {
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
        return data;
    }

    private static int[] readIndices(AIMesh mesh) {
        int fcount = mesh.mNumFaces();
        int[] indices = new int[fcount * 3];
        Log.d(TAG, fcount + " faces");
        for(int i = 0; i < fcount; i++) {
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

    public GLRawModel createRawModel() {
        GLRawModel model = null;
        if(joints.size() > 0) {
            //model = GLLoader.loadToVAOAnimated(pos,uv,normal,  ...)
            model = GLLoader.loadToVAO(pos, uv,normal, indices);
        } else {
            model = GLLoader.loadToVAO(pos, uv,normal, indices);
        }
        //todo cases
        return model;
    }



    @Override
    public String toString() {
        return "Mesh " + name + " with " + vcount + " vertices";
    }
}
