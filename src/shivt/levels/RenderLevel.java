package shivt.levels;

import engine.EngineMaster;
import engine.graphics.Scene;
import engine.graphics.glglfwImplementation.entities.GLEntity;
import engine.graphics.glglfwImplementation.models.GLMaterializedModel;
import engine.graphics.glglfwImplementation.text.GLGuiText;
import engine.graphics.glglfwImplementation.text.GLTTFont;
import engine.graphics.lights.Light;
import engine.graphics.materials.TexturedMaterial;
import engine.toolbox.OBJLoader;
import engine.graphics.particles.ParticleMaster;
import engine.graphics.particles.ParticleSystem;
import engine.graphics.particles.ParticleSystemStream;
import engine.graphics.particles.ParticleTexture;
import engine.graphics.glglfwImplementation.textures.GLModelTexture;
import engine.toolbox.Color;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/***
 * Created by pv42 on 18.09.2016.
 */
public class RenderLevel {
    private List<ParticleSystem> lines;
    private List<GLGuiText> texts;
    private List<Vector3f> ends;

    public RenderLevel(ShivtLevel level, GLTTFont font, Scene scene) {
        lines = new ArrayList<>();
        List<GLEntity> entities = new ArrayList<>();
        ends = new ArrayList<>();
        texts = new ArrayList<>();
        ParticleTexture pt = EngineMaster.getTextureLoader().loadParticleTexture("frostfire.png",4,true,true);
        for (Route route: level.getRoutes()) {
            int start = route.getStations()[0];
            int end = route.getStations()[1];
            ParticleSystem ps = new ParticleSystemStream(pt,30,1.7f,.5f,level.getStations().get(start).getPosition(),new Vector3f(.01f,.01f,.01f));
            lines.add(ps);
            ends.add(level.getStations().get(end).getPosition());
        }
        for(Station station : level.getStations()) {
            GLMaterializedModel texturedModel = new GLMaterializedModel(OBJLoader.loadObjModel("spaceStation"),
                    new TexturedMaterial(EngineMaster.getTextureLoader().loadTexture("blue.png")));
            texturedModel.getMaterial().setReflectivity(.1f);
            GLEntity e = new GLEntity(texturedModel,station.getPosition());
            e.setScale(0.5f);
            entities.add(e);
            GLGuiText text = new GLGuiText(font, station.getTroopsStrength()  + "T",0.0005f,new Color(.3,.3,.3),new Vector2f(station.getPosition().x(), station.getPosition().y()));
            texts.add(text);
            scene.addText(text);
        }
        Light sun = new Light(new Vector3f(0, 0, -20), new Color(1.0, 1.0, 1.0));
        entities.forEach(scene::addEntity);
        scene.addLight(sun);
    }
    public void process(float timeDelta) {
        for (int i = 0; i < lines.size(); i++) {
            lines.get(i).generateParticles(ends.get(i), timeDelta);
        }
        ParticleMaster.update(timeDelta);
    }
}