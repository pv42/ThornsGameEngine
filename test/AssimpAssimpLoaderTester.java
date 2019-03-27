import engine.EngineMaster;
import engine.graphics.Scene;
import engine.graphics.cameras.Camera;
import engine.graphics.cameras.StaticThreeDimensionCamera;
import engine.graphics.display.Window;
import engine.graphics.glglfwImplementation.GLLoader;
import engine.graphics.glglfwImplementation.MasterRenderer;
import engine.graphics.glglfwImplementation.entities.GLEntity;
import engine.graphics.glglfwImplementation.models.GLTexturedModel;
import engine.graphics.glglfwImplementation.textures.ModelTexture;
import engine.toolbox.assimpLoader.AssimpLoader;
import engine.toolbox.assimpLoader.AssimpMaterial;
import engine.toolbox.assimpLoader.AssimpMesh;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;


public class AssimpAssimpLoaderTester {
    @Test
    void testLoad() {
        AssimpLoader c = new AssimpLoader();
        c.load("C:\\Users\\pv42\\Documents\\IdeaProjects\\ThornsGameEngine\\testres\\cowboy.dae");
        for(AssimpMesh meshData: c.meshs) {
            System.out.println(meshData.toString());
            System.out.println(" nvc=" + meshData.getNormal().length);
            System.out.println(" uvvc=" + meshData.getUv().length);
        }
        for (AssimpMaterial material: c.materials) {
            System.out.println(material.getName());
            System.out.println(material.getTextureFile());
        }
    }

    @Test
    void testLoadAndDraw() {
        AssimpLoader c = new AssimpLoader();
        c.load("C:\\Users\\pv42\\Documents\\IdeaProjects\\ThornsGameEngine\\testres\\cowboy.dae");
        //engine
        Window window = EngineMaster.init();
        Camera camera = new StaticThreeDimensionCamera(new Vector3f(0,0,10), new Vector3f());
        Scene scene = new Scene();
        ModelTexture texture = new ModelTexture(GLLoader.loadTexture("diffuse.png"));
        GLTexturedModel texturedModel = new GLTexturedModel(c.meshs.get(0).createRawModel(), texture);
        GLEntity entity = new GLEntity(texturedModel, new Vector3f());
        entity.setRx(-90);
        scene.addEntity(entity);
        while (!window.isCloseRequested()) {
            MasterRenderer.render(scene, camera);
            window.update();
        }
    }
}
