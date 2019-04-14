package engine.toolbox;

import engine.EngineMaster;
import engine.graphics.Scene;
import engine.graphics.animation.Animation;
import engine.graphics.animation.Animator;
import engine.graphics.cameras.Camera;
import engine.graphics.cameras.StaticThreeDimensionCamera;
import engine.graphics.display.Window;
import engine.graphics.glglfwImplementation.MasterRenderer;
import engine.graphics.glglfwImplementation.entities.GLEntity;
import engine.graphics.glglfwImplementation.models.GLMaterializedModel;
import engine.graphics.lights.PointLight;
import engine.toolbox.assimpLoader.AssimpAnimation;
import engine.toolbox.assimpLoader.AssimpMaterial;
import engine.toolbox.assimpLoader.AssimpMesh;
import engine.toolbox.assimpLoader.AssimpScene;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;


class AssimpLoaderTester {
    @Test
    void testLoad() {
        AssimpScene c = new AssimpScene();
        c.load("/home/pv42/IdeaProjects/ThornsGameEngine/testres/cowboy.dae");
        //c.load("C:\\Users\\pv42\\Documents\\IdeaProjects\\ThornsGameEngine\\testres\\cowboy.dae");
        for (AssimpMesh meshData : c.getMeshes()) {
            System.out.println(meshData.toString());
            System.out.println(" nvc=" + meshData.getNormal().length);
            System.out.println(" uvvc=" + meshData.getUv().length);
        }
        for (AssimpMaterial material : c.getMaterials()) {
            System.out.println(material.getName());
            System.out.println(material.getTextureFile());
        }
        for (AssimpAnimation animation : c.getAnimations()) {
            System.out.print("animation");
        }
    }

    @Test
    void testLoadAndDraw() {
        AssimpScene c = new AssimpScene();
        c.load("/home/pv42/IdeaProjects/ThornsGameEngine/testres/cowboy.dae");
        //c.load("C:\\Users\\pv42\\Documents\\IdeaProjects\\ThornsGameEngine\\testres\\cowboy.dae");
        //engine
        EngineMaster.init();
        Window window = EngineMaster.getDisplayManager().createWindow();
        Camera camera = new StaticThreeDimensionCamera(new Vector3f(0, 0, 10), new Vector3f());
        Scene scene = new Scene();
        GLMaterializedModel texturedModel = c.getMeshes().get(0).createMaterializedModel(true);
        GLEntity entity = new GLEntity(texturedModel, new Vector3f());
        entity.setRx(-90);
        entity.setPosition(0, -5, 0);
        scene.addEntity(entity);
        scene.addLight(new PointLight(new Vector3f(0, 0, 10), new Color(1, 1, 1)));
        Animation animation = c.getAnimations().get(0).getAnimation();
        float aniTime = 0;
        while (!window.isCloseRequested() && aniTime < 5) {
            if (texturedModel.getRawModel().getJoints() != null)
                Animator.applyAnimation(animation, texturedModel.getRawModel().getJoints(), aniTime % animation.getLastKeyFrameTime());
            MasterRenderer.render(scene, camera);
            window.update();
            aniTime += window.getLastFrameTime();
        }
        EngineMaster.finish();
    }

    @Test
    void testAndDrawBlender() {
        if (0 == 0) return;
        // todo fix blender
        AssimpScene c = new AssimpScene();
        c.load("C:\\Users\\pv42\\Documents\\IdeaProjects\\ThornsGameEngine\\testres\\bmw27_cpu.blend");
        //engine
        EngineMaster.init();
        Window window = EngineMaster.getDisplayManager().createWindow();
        Camera camera = new StaticThreeDimensionCamera(new Vector3f(0, 0, 10), new Vector3f());
        Scene scene = new Scene();
        scene.addLight(new PointLight(new Vector3f(0, 0, 10), new Color(1, 1, 1)));
        Log.i("number of meshes:" + c.getMeshes().size());
        for (AssimpMesh mesh : c.getMeshes()) {
            GLMaterializedModel texturedModel = mesh.createMaterializedModel(false);
            GLEntity entity = new GLEntity(texturedModel, new Vector3f(0, -5, 0));
            entity.setScale(3);
            entity.setRx(-90);
            scene.addEntity(entity);
        }
        int i = 0;
        while (!window.isCloseRequested() && i < 1000) {
            MasterRenderer.render(scene, camera);
            window.update();
            i++;
        }
        EngineMaster.finish();
    }

    @Test
    void testLoadAndDrawLara() {
        AssimpScene c = new AssimpScene();
        c.load("/home/pv42/IdeaProjects/ThornsGameEngine/res/meshs/Lara_Croft.dae");
        //c.load("C:\\Users\\pv42\\Documents\\IdeaProjects\\ThornsGameEngine\\res\\meshs\\Lara_Croft.dae");
        //engine
        EngineMaster.init();
        Window window = EngineMaster.getDisplayManager().createWindow();
        Camera camera = new StaticThreeDimensionCamera(new Vector3f(0, 0, 10), new Vector3f());
        Scene scene = new Scene();
        scene.addLight(new PointLight(new Vector3f(0, 0, 10), new Color(1, 1, 1)));
        Log.i("number of meshes:" + c.getMeshes().size());
        for (AssimpMesh mesh : c.getMeshes()) {
            GLMaterializedModel texturedModel = mesh.createMaterializedModel(false);
            GLEntity entity = new GLEntity(texturedModel, new Vector3f(0, -5, 0));
            entity.setScale(3);
            entity.setRx(-90);
            scene.addEntity(entity);
        }
        int i = 0;
        while (!window.isCloseRequested() && i < 300) {
            MasterRenderer.render(scene, camera);
            window.update();
            i++;
        }
        EngineMaster.finish();
    }
}
