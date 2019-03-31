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
import engine.graphics.lights.Light;
import engine.toolbox.Color;
import engine.toolbox.Log;
import engine.toolbox.assimpLoader.AssimpAnimation;
import engine.toolbox.assimpLoader.AssimpScene;
import engine.toolbox.assimpLoader.AssimpMaterial;
import engine.toolbox.assimpLoader.AssimpMesh;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;


class AssimpLoaderTester {
    @Test
    void testLoad() {
        AssimpScene c = new AssimpScene();
        c.load("C:\\Users\\pv42\\Documents\\IdeaProjects\\ThornsGameEngine\\testres\\cowboy.dae");
        for (AssimpMesh meshData : c.getMeshs()) {
            System.out.println(meshData.toString());
            System.out.println(" nvc=" + meshData.getNormal().length);
            System.out.println(" uvvc=" + meshData.getUv().length);
        }
        for (AssimpMaterial material : c.getMaterials()) {
            System.out.println(material.getName());
            System.out.println(material.getTextureFile());
        }
        for (AssimpAnimation animation: c.getAnimations()) {
            System.out.print("animation");
        }
    }

    @Test
    void testLoadAndDraw() {
        AssimpScene c = new AssimpScene();
        c.load("C:\\Users\\pv42\\Documents\\IdeaProjects\\ThornsGameEngine\\testres\\cowboy.dae");
        //engine
        EngineMaster.init();
        Window window = EngineMaster.getDisplayManager().createWindow();
        Camera camera = new StaticThreeDimensionCamera(new Vector3f(0, 0, 10), new Vector3f());
        Scene scene = new Scene();
        GLMaterializedModel texturedModel = c.getMeshs().get(0).createMaterializedModel();
        GLEntity entity = new GLEntity(texturedModel, new Vector3f());
        entity.setRx(-90);
        entity.setPosition(0,-5,0);
        scene.addEntity(entity);
        scene.addLight(new Light(new Vector3f(0,0,10), new Color(1,1,1)));
        Animation animation = c.getAnimations().get(0).getAnimation();
        float aniTime = 0;
        while (!window.isCloseRequested()) {
            Animator.applyAnimation(animation, texturedModel.getRawModel().getJoints(), aniTime);
            MasterRenderer.render(scene, camera);
            window.update();
            aniTime += window.getLastFrameTime();
        }
        EngineMaster.finish();
    }

    @Test
    void testLoadAndDrawLara() {
        AssimpScene c = new AssimpScene();
        c.load("C:\\Users\\pv42\\Documents\\IdeaProjects\\ThornsGameEngine\\res\\meshs\\Lara_Croft.dae");
        //engine
        EngineMaster.init();
        Window window = EngineMaster.getDisplayManager().createWindow();
        Camera camera = new StaticThreeDimensionCamera(new Vector3f(0, 0, 10), new Vector3f());
        Scene scene = new Scene();
        scene.addLight(new Light(new Vector3f(0, 0, 10), new Color(1, 1, 1)));
        Log.i("number of meshes:" + c.getMeshs().size());
        for (AssimpMesh mesh : c.getMeshs()) {
            GLMaterializedModel texturedModel = mesh.createMaterializedModel();
            GLEntity entity = new GLEntity(texturedModel, new Vector3f(0,-5,0));
            entity.setScale(3);
            entity.setRx(-90);
            scene.addEntity(entity);
        }
        while (!window.isCloseRequested()) {
            MasterRenderer.render(scene, camera);
            window.update();
        }
        EngineMaster.finish();
    }
}
