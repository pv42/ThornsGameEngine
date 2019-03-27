package engineTester.graphicTest;

import engine.EngineMaster;
import engine.graphics.Entity;
import engine.graphics.Scene;
import engine.graphics.animation.Animation;
import engine.graphics.animation.Animator;
import engine.graphics.animation.Joint;
import engine.graphics.animation.KeyFrame;
import engine.graphics.cameras.Camera;
import engine.graphics.cameras.StaticThreeDimensionCamera;
import engine.graphics.glglfwImplementation.GLLoader;
import engine.graphics.glglfwImplementation.MasterRenderer;
import engine.graphics.glglfwImplementation.display.GLFWWindow;
import engine.graphics.glglfwImplementation.entities.GLEntity;
import engine.graphics.glglfwImplementation.models.GLRawModel;
import engine.graphics.glglfwImplementation.models.GLTexturedModel;
import engine.graphics.glglfwImplementation.textures.ModelTexture;
import engine.graphics.lights.Light;
import engine.inputs.InputHandler;
import engine.toolbox.Color;
import engine.toolbox.Log;
import engine.toolbox.Matrix4fDbg;
import engine.toolbox.collada.Collada;
import engine.toolbox.collada.ColladaLoader;
import engine.toolbox.nCollada.dataStructures.AnimatedModelData;
import engine.toolbox.nCollada.dataStructures.AnimationData;
import engine.toolbox.nCollada.dataStructures.JointData;
import engine.toolbox.nCollada.dataStructures.JointTransformData;
import engine.toolbox.nCollada.dataStructures.KeyFrameData;
import engine.toolbox.nCollada.dataStructures.MeshData;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class AnimationTest {
    private static Joint createJoints(JointData data) {
        Joint joint = new Joint(data.index, data.nameId, data.bindLocalTransform);
        for (JointData child : data.children) {
            joint.addChild(createJoints(child));
        }
        return joint;
    }

    private static List<Joint> listJoints(Joint head) {
        List<Joint> jointList = new ArrayList<>();
        HashMap<Integer, Joint> jointMap = new HashMap<>();
        jointMap.put(0, head);
        int max = buildJointMap(head, -1, jointMap);
        for (int i = 0; i <= max; i++) {
            jointList.add(jointMap.get(i));
        }
        return jointList;
    }

    private static int buildJointMap(Joint head, Integer max, Map<Integer, Joint> joints) {
        for (Joint joint : head.children) {
            joints.put(joint.numId, joint);
            max = max > joint.numId ? max : joint.numId;
            max = buildJointMap(joint, max, joints);
        }
        return max;
    }

    @Test
    void testAnimation() {
        GLFWWindow window = EngineMaster.init();
        Collada collada = ColladaLoader.loadCollada("cowboy");
        GLTexturedModel cowboyModel = collada.getTexturedModels().get(0);
        Animation animation = collada.getAnimation();
        GLEntity entity = new GLEntity(cowboyModel, new Vector3f(0, 0f, -10), -90, 0, 0, 1);
        Camera camera = new StaticThreeDimensionCamera(new Vector3f(0, 0, 10), new Vector3f());
        InputHandler.init(window.getId());
        Scene scene = new Scene();
        scene.addLight(new Light(new Vector3f(), new Color(1.0, 1.0, 1.0)));
        scene.addEntity(entity);
        int count = 0;
        float animationTime = 0;
        while (!window.isCloseRequested() && count < 1000) {
            MasterRenderer.render(scene, camera);
            animationTime += window.getLastFrameTime() * 0.2;
            animationTime %= 1;
            Animator.applyAnimation(animation, cowboyModel.getRawModel().getJoints(), animationTime);

            window.update();
            count++;
        }
        EngineMaster.finish();
        assertEquals(0, Log.getErrorNumber());
        //TODO fix warnings
        assertEquals(0, Log.getWarningNumber());
    }

    // other test
    @Test
    void testNAnimation() {
        String filename = "cowboy";
        AnimationData animationData = engine.toolbox.nCollada.colladaLoader.ColladaLoader.loadColladaAnimation(new File("res/meshs/" + filename + ".dae"));
        AnimatedModelData modelData = engine.toolbox.nCollada.colladaLoader.ColladaLoader.loadColladaModel(new File("res/meshs/" + filename + ".dae"), 50);
        GLFWWindow window = EngineMaster.init();
        MasterRenderer.enableCulling();
        MeshData md = modelData.getMeshData();
        Joint head = createJoints(modelData.getJointsData().headJoint);
        assert md.getVertexCount() == md.getIndices().length;
        Log.i("" + md.getVertices().length, "" + md.getVertexWeights().length);
        assert md.getVertices().length / 3 == md.getVertexWeights().length / 3;
        assert md.getVertexWeights().length == md.getJointIds().length;
        GLRawModel model = GLLoader.loadToVAOAnimated(md.getVertices(), md.getTextureCoords(), md.getNormals(), md.getIndices(), md.getJointIds(), md.getVertexWeights(), listJoints(head));
        GLTexturedModel texturedModel = new GLTexturedModel(model, new ModelTexture(GLLoader.loadTexture("diffuse.png", false)));
        Entity entity = new GLEntity(texturedModel, new Vector3f(0,-5,0));
        Scene scene = new Scene();
        scene.addEntity(entity);
        scene.addLight(new Light(new Vector3f(0,0,5), new Color(1,1,1)));
        Camera camera = new StaticThreeDimensionCamera(new Vector3f(0, 0, 10), new Vector3f());
        int count = 0;
        float animationTime = 0;
        Animation animation = new Animation();
        for (KeyFrameData keyFrameData : animationData.keyFrames) {
            KeyFrame kf = new KeyFrame(keyFrameData.time);
            for(JointTransformData jt: keyFrameData.jointTransforms) {
                kf.addJointData(jt.jointNameId, jt.jointLocalTransform);
            }
            animation.addKeyFrame(kf);
        }
        Animator.applyAnimation(animation, texturedModel.getRawModel().getJoints(), animationTime);
        while (!window.isCloseRequested() && count < 1000) {
            MasterRenderer.render(scene, camera);
            animationTime += window.getLastFrameTime() * 0.2;
            animationTime %= animation.getLastKeyFrameTime();
            Animator.applyAnimation(animation, texturedModel.getRawModel().getJoints(), animationTime);

            window.update();
            count++;
        }
        EngineMaster.finish();

    }
}
