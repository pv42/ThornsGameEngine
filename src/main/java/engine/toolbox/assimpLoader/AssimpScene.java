package engine.toolbox.assimpLoader;

import engine.toolbox.Log;
import org.joml.Matrix4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIAnimation;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;

import java.util.ArrayList;
import java.util.List;

public class AssimpScene {
    private static final String TAG = "AssimpScene";

    private List<AssimpMesh> meshes = new ArrayList<>();
    private List<AssimpMaterial> materials = new ArrayList<>();
    private List<AssimpAnimation> animations = new ArrayList<>();

    static float[] readBuffer3f(AIVector3D.Buffer buffer, int count) {
        float[] data = new float[count * 3];
        int i = 0;
        while (buffer.hasRemaining()) {
            AIVector3D vector3D = buffer.get();
            data[3 * i] = vector3D.x();
            data[3 * i + 1] = vector3D.y();
            data[3 * i + 2] = vector3D.z();
            i++;
        }
        if (3 * i != data.length) {
            Log.w(TAG, "buffer length mismatch (" + 3 * i + "!=" + data.length + ")");
        }
        return data;
    }

    static float[] readBuffer3fTo2f(AIVector3D.Buffer buffer, int count) {
        float[] data = new float[count * 2];
        int i = 0;
        while (buffer.hasRemaining()) {
            AIVector3D vector3D = buffer.get();
            data[2 * i] = vector3D.x();
            data[2 * i + 1] = vector3D.y();
            if (vector3D.z() != 0) Log.w(TAG, "tried reading vector3f as 2f with z not zero " + vector3D.z());
            i++;
        }
        if (2 * i != data.length) Log.w(TAG, "buffer length mismatch (" + 2 * i + "!=" + data.length + ")");
        return data;
    }

    static Matrix4f readMatrix4f(AIMatrix4x4 source) {
        Matrix4f target = new Matrix4f();
        target.m00(source.a1());
        target.m01(source.a2());
        target.m02(source.a3());
        target.m03(source.a4());
        target.m10(source.b1());
        target.m11(source.b2());
        target.m12(source.b3());
        target.m13(source.b4());
        target.m20(source.c1());
        target.m21(source.c2());
        target.m22(source.c3());
        target.m23(source.c4());
        target.m30(source.d1());
        target.m31(source.d2());
        target.m32(source.d3());
        target.m33(source.d4());
        return target;
    }

    public void load(String file) {
        Log.i(TAG, "assimp v" + Assimp.aiGetVersionMajor() + "." + Assimp.aiGetVersionMinor() + " loading " + file);
        long millis = System.currentTimeMillis();
        AIScene scene = Assimp.aiImportFile(file, 0);
        if (scene == null) {
            throw new RuntimeException("Error loading model");
        }
        loadTextures(scene);
        loadMaterials(scene);
        loadMeshs(scene);
        loadAnimations(scene);
        loadJointNodeStructure(scene);
        Log.d(TAG, "loaded " + file + " in " + (System.currentTimeMillis() - millis) + "ms");
    }

    private void loadJointNodeStructure(AIScene scene) {
        AINode rootNode = scene.mRootNode();
        processJointNode(rootNode);
    }

    private void processJointNode(AINode aiNode) {
        int numChildren = aiNode.mNumChildren();
        String nodeName = aiNode.mName().dataString();
        for (int i = 0; i < numChildren; i++) {
            AINode child = AINode.create(aiNode.mChildren().get(i));
            processJointNode(child);
            for (AssimpMesh mesh : meshes) {
                AssimpJoint aiParent = mesh.getJointByName(nodeName);
                String childrenName = child.mName().dataString();
                AssimpJoint aiChild = mesh.getJointByName(childrenName);
                if (aiChild == null || aiParent == null) {
                    continue;
                }
                aiChild.setParent(aiParent);
            }
        }
    }

    private void loadTextures(AIScene scene) {
        Log.d(TAG, "texture loading disabled since there is a assimp-lwjgl error");
    }

    private void loadAnimations(AIScene scene) {
        int count = scene.mNumAnimations();
        PointerBuffer aiAnimations = scene.mAnimations();
        Log.d(TAG, "animcount=" + count);
        for (int i = 0; i < count; i++) {
            AIAnimation animation = AIAnimation.create(aiAnimations.get(i));
            animations.add(AssimpAnimation.load(animation));
        }
    }

    private void loadMeshs(AIScene scene) {
        int numMeshes = scene.mNumMeshes();
        PointerBuffer aiMeshes = scene.mMeshes();
        for (int i = 0; i < numMeshes; i++) {
            AIMesh mesh = AIMesh.create(aiMeshes.get(i));
            meshes.add(AssimpMesh.load(mesh, materials));
        }
    }

    private void loadMaterials(AIScene scene) {
        int numMats = scene.mNumMaterials();
        PointerBuffer aiMats = scene.mMaterials();
        for (int i = 0; i < numMats; i++) {
            AIMaterial material = AIMaterial.create(aiMats.get(i));
            materials.add(AssimpMaterial.load(material));
        }
    }

    public List<AssimpMesh> getMeshes() {
        return meshes;
    }

    public List<AssimpMaterial> getMaterials() {
        return materials;
    }

    public List<AssimpAnimation> getAnimations() {
        return animations;
    }
}
