package engine.graphics;

import engine.EngineMaster;
import engine.graphics.cameras.StaticThreeDimensionCamera;
import engine.graphics.display.Window;
import engine.graphics.glglfwImplementation.MasterRenderer;
import engine.graphics.glglfwImplementation.entities.GLEntity;
import engine.graphics.glglfwImplementation.models.GLMaterializedModel;
import engine.graphics.glglfwImplementation.models.GLRawModel;
import engine.graphics.glglfwImplementation.text.GLGuiText;
import engine.graphics.glglfwImplementation.text.GLTTFont;
import engine.graphics.lights.PointLight;
import engine.graphics.materials.Material;
import engine.graphics.materials.TexturedMaterial;
import engine.graphics.text.GuiText;
import engine.graphics.textures.Texture;
import engine.toolbox.Color;
import engine.toolbox.Log;
import engine.toolbox.MeshCreator;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PointLightTests {
    @Test
    void testReflectivity() {
        Log.clearNumbers();
        EngineMaster.init();
        Window window = EngineMaster.getDisplayManager().createWindow();
        Scene scene = new Scene();
        GLRawModel rawModel = MeshCreator.createBox(1, 1, 1);
        Texture texture = EngineMaster.getTextureLoader().loadTexture("test2.png");
        Material material = new TexturedMaterial(texture);
        GLMaterializedModel model = new GLMaterializedModel(rawModel, material, false);
        Entity entity0 = new GLEntity(model, new Vector3f(-1, 0, 0));
        Entity entity1 = new GLEntity(model, new Vector3f(1, 0, 0));
        Entity entity2 = new GLEntity(model, new Vector3f(0, 0, -6));
        entity2.setScale(10);
        scene.addEntity(entity0);
        scene.addEntity(entity1);
        scene.addEntity(entity2);
        PointLight light = new PointLight(new Vector3f(0, 0, 3), new Color(1, 1, 1));
        scene.addLight(light);
        StaticThreeDimensionCamera camera = new StaticThreeDimensionCamera(new Vector3f(0, 0, 1), new Vector3f());
        GLTTFont font = EngineMaster.getFontFactory().loadSystemFont("bahnschrift", 64);
        GuiText text = new GLGuiText(font, "reflec.: 0", 0.00005f, new Color(1, 1, 1), new Vector2f(-1, .9f));
        scene.addText(text);
        float reflec = 0;
        int reflecd = 1;
        int i = 0;
        while (!window.isCloseRequested() && i < 300) {
            reflec += 0.002f * reflecd;
            if (Math.abs(reflec) > .5f) reflecd *= -1;
            text.setString(String.format("reflec.: %.2f", (reflec + .5f)));
            material.setReflectivity(reflec + .5f);
            MasterRenderer.render(scene, camera);
            window.update();
            camera.setPosition(new Vector3f(0, 0, 3));
            entity0.setRy(entity0.getRy() + 0.3f);
            entity1.setRx(entity1.getRx() + 1);
            i++;
        }
        EngineMaster.finish();
        assertEquals(0, Log.getWarningNumber());
        assertEquals(0, Log.getErrorNumber());
    }

    @Test
    void testShineDamper() {
        Log.clearNumbers();
        EngineMaster.init();
        Window window = EngineMaster.getDisplayManager().createWindow();
        Scene scene = new Scene();
        GLRawModel rawModel = MeshCreator.createBox(1, 1, 1);
        Texture texture = EngineMaster.getTextureLoader().loadTexture("test2.png");
        Material material = new TexturedMaterial(texture);
        GLMaterializedModel model = new GLMaterializedModel(rawModel, material, false);
        Entity entity0 = new GLEntity(model, new Vector3f(-1, 0, 0));
        Entity entity1 = new GLEntity(model, new Vector3f(1, 0, 0));
        Entity entity2 = new GLEntity(model, new Vector3f(0, 0, -6));
        entity2.setScale(10);
        scene.addEntity(entity0);
        scene.addEntity(entity1);
        scene.addEntity(entity2);
        PointLight light = new PointLight(new Vector3f(0, 0, 3), new Color(1, 1, 1));
        scene.addLight(light);
        StaticThreeDimensionCamera camera = new StaticThreeDimensionCamera(new Vector3f(0, 0, 1), new Vector3f());
        GLTTFont font = EngineMaster.getFontFactory().loadSystemFont("bahnschrift", 64);
        GuiText text = new GLGuiText(font, "shineDamper.: 0", 0.00005f, new Color(1, 1, 1), new Vector2f(-1, .9f));
        scene.addText(text);
        float sd = 0;
        int sdd = 1;
        int i = 0;
        material.setReflectivity(.5f);
        while (!window.isCloseRequested() && i < 300) {
            sd += 0.008f * sdd;
            if (Math.abs(sd) > 1f) sdd *= -1;
            float sdv = (sd + 1f) * (sd + 1f) * (sd + 1f) * 10 + 1f;
            text.setString(String.format("shineDamper.: %.2f", sdv));
            material.setShineDamper(sdv);
            MasterRenderer.render(scene, camera);
            window.update();
            camera.setPosition(new Vector3f(0, 0, 3));
            entity0.setRy(entity0.getRy() + 0.3f);
            entity1.setRx(entity1.getRx() + 1);
            i++;
        }
        EngineMaster.finish();
        assertEquals(0, Log.getWarningNumber());
        assertEquals(0, Log.getErrorNumber());
    }

    @Test
    void testLightColor() {
        Log.clearNumbers();
        EngineMaster.init();
        Window window = EngineMaster.getDisplayManager().createWindow();
        Scene scene = new Scene();
        GLRawModel rawModel = MeshCreator.createBox(1, 1, 1);
        Texture texture = EngineMaster.getTextureLoader().loadTexture("test2.png");
        Material material = new TexturedMaterial(texture);
        GLMaterializedModel model = new GLMaterializedModel(rawModel, material, false);
        Entity entity0 = new GLEntity(model, new Vector3f(-1, 0, 0));
        Entity entity1 = new GLEntity(model, new Vector3f(1, 0, 0));
        Entity entity2 = new GLEntity(model, new Vector3f(0, 0, -6));
        entity2.setScale(10);
        scene.addEntity(entity0);
        scene.addEntity(entity1);
        scene.addEntity(entity2);
        PointLight light = new PointLight(new Vector3f(0, 0, 3), new Color(1, 1, 1));
        scene.addLight(light);
        StaticThreeDimensionCamera camera = new StaticThreeDimensionCamera(new Vector3f(0, 0, 1), new Vector3f());
        GLTTFont font = EngineMaster.getFontFactory().loadSystemFont("cour", 64);
        GuiText text = new GLGuiText(font, "shineDamper.: 0", 0.00005f, new Color(1, 1, 1), new Vector2f(-1, .9f));
        scene.addText(text);
        float h = 0;
        float s = 0;
        int sd = 1;
        int i = 0;
        material.setReflectivity(.1f);
        material.setShineDamper(2);
        while (!window.isCloseRequested() && i < 300) {
            h += 2;
            s += .015f * sd;
            if (Math.abs(s) > .48f) sd *= -1;
            light.setColor(Color.fromHSL(h, s + .5f, .5f));

            text.setString(String.format("color.: %.2f %.2f %.2f hs %d %.2f", light.getColor().getR(), light.getColor().getG(), light.getColor().getB(), (int) h % 360, s + .5f));

            MasterRenderer.render(scene, camera);
            window.update();
            camera.setPosition(new Vector3f(0, 0, 3));
            entity0.setRy(entity0.getRy() + 0.3f);
            entity1.setRx(entity1.getRx() + 1);
            i++;
        }
        EngineMaster.finish();
        assertEquals(0, Log.getWarningNumber());
        assertEquals(0, Log.getErrorNumber());
    }

    @Test
    void testLightPosition() {
        Log.clearNumbers();
        EngineMaster.init();
        Window window = EngineMaster.getDisplayManager().createWindow();
        Scene scene = new Scene();
        GLRawModel rawModel = MeshCreator.createBox(1, 1, 1);
        Texture texture = EngineMaster.getTextureLoader().loadTexture("test2.png");
        Material material = new TexturedMaterial(texture);
        GLMaterializedModel model = new GLMaterializedModel(rawModel, material, false);
        Entity entity0 = new GLEntity(model, new Vector3f(-1, 0, 0));
        Entity entity1 = new GLEntity(model, new Vector3f(1, 0, 0));
        Entity entity2 = new GLEntity(model, new Vector3f(0, 0, -6));
        entity2.setScale(10);
        scene.addEntity(entity0);
        scene.addEntity(entity1);
        scene.addEntity(entity2);
        PointLight light = new PointLight(new Vector3f(0, 0, 3), new Color(1, 1, 1));
        scene.addLight(light);
        StaticThreeDimensionCamera camera = new StaticThreeDimensionCamera(new Vector3f(0, 0, 1), new Vector3f());
        GLTTFont font = EngineMaster.getFontFactory().loadSystemFont("cour", 64);
        GuiText text = new GLGuiText(font, "shineDamper.: 0", 0.00005f, new Color(1, 1, 1), new Vector2f(-1, .9f));
        scene.addText(text);
        float z = 0;
        int zd = 1;
        float x = 0;
        int xd = 1;
        int i = 0;
        material.setReflectivity(.2f);
        material.setShineDamper(2);
        while (!window.isCloseRequested() && i < 300) {
            z += .07f * zd;
            x += .15f * xd;
            if (Math.abs(z) > 7f) zd *= -1;
            if (Math.abs(x) > 7f) xd *= -1;
            light.setPosition(new Vector3f(x, 0, 5 + z));
            text.setString(String.format("x=%.0f y=0 z=%.0f", x, z + 5));
            MasterRenderer.render(scene, camera);
            window.update();
            camera.setPosition(new Vector3f(0, 0, 3));
            entity0.setRy(entity0.getRy() + 0.3f);
            entity1.setRx(entity1.getRx() + 1);
            i++;
        }
        EngineMaster.finish();
        assertEquals(0, Log.getWarningNumber());
        assertEquals(0, Log.getErrorNumber());
    }

}
