import engine.EngineMaster;
import engine.graphics.Scene;
import engine.graphics.cameras.Camera;
import engine.graphics.cameras.StaticThreeDimensionCamera;
import engine.graphics.display.Window;
import engine.graphics.glglfwImplementation.MasterRenderer;
import engine.graphics.glglfwImplementation.entities.GLEntity;
import engine.graphics.glglfwImplementation.models.GLMaterializedModel;
import engine.graphics.glglfwImplementation.textures.GLModelTexture;
import engine.graphics.lights.Light;
import engine.graphics.materials.TexturedMaterial;
import engine.toolbox.Color;
import engine.toolbox.assimpLoader.AssimpLoader;
import engine.toolbox.assimpLoader.AssimpMaterial;
import engine.toolbox.assimpLoader.AssimpMesh;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;


class AssimpLoaderTester {
    @Test
    void testLoad() {
        AssimpLoader c = new AssimpLoader();
        c.load("C:\\Users\\pv42\\Documents\\IdeaProjects\\ThornsGameEngine\\testres\\cowboy.dae");
        for (AssimpMesh meshData : c.meshs) {
            System.out.println(meshData.toString());
            System.out.println(" nvc=" + meshData.getNormal().length);
            System.out.println(" uvvc=" + meshData.getUv().length);
        }
        for (AssimpMaterial material : c.materials) {
            System.out.println(material.getName());
            System.out.println(material.getTextureFile());
        }
    }

    @Test
    void testLoadAndDraw() {
        AssimpLoader c = new AssimpLoader();
        c.load("C:\\Users\\pv42\\Documents\\IdeaProjects\\ThornsGameEngine\\testres\\cowboy.dae");
        //engine
        EngineMaster.init();
        Window window = EngineMaster.getDisplayManager().createWindow();
        Camera camera = new StaticThreeDimensionCamera(new Vector3f(0, 0, 10), new Vector3f());
        Scene scene = new Scene();
        GLModelTexture texture = (GLModelTexture) EngineMaster.getTextureLoader().loadTexture("diffuse.png");
        GLMaterializedModel texturedModel = new GLMaterializedModel(c.meshs.get(0).createRawModel(), new TexturedMaterial(texture));
        GLEntity entity = new GLEntity(texturedModel, new Vector3f());
        entity.setRx(-90);
        scene.addEntity(entity);
        while (!window.isCloseRequested()) {
            MasterRenderer.render(scene, camera);
            window.update();
        }
    }

    @Test
    void testLoadAndDrawLara() {
        AssimpLoader c = new AssimpLoader();
        c.load("C:\\Users\\pv42\\Documents\\IdeaProjects\\ThornsGameEngine\\res\\meshs\\Lara_Croft.dae");
        //engine
        EngineMaster.init();
        Window window = EngineMaster.getDisplayManager().createWindow();
        Camera camera = new StaticThreeDimensionCamera(new Vector3f(0, 0, 10), new Vector3f());
        Scene scene = new Scene();
        scene.addLight(new Light(new Vector3f(0, 0, 10), new Color(1, 1, 1)));
        for (AssimpMesh mesh : c.meshs) {
            GLModelTexture texture = (GLModelTexture) EngineMaster.getTextureLoader().loadTexture("diffuse.png");
            GLMaterializedModel texturedModel = new GLMaterializedModel(mesh.createRawModel(), new TexturedMaterial(texture));
            GLEntity entity = new GLEntity(texturedModel, new Vector3f());
            entity.setRx(-90);
            scene.addEntity(entity);
        }
        while (!window.isCloseRequested()) {
            MasterRenderer.render(scene, camera);
            window.update();
        }
    }
}
