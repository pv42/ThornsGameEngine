package shivt.levels;

import engine.graphics.entities.Entity;
import engine.graphics.lights.Light;
import engine.graphics.fontMeshCreator.FontType;
import engine.graphics.fontMeshCreator.GUIText;
import engine.graphics.models.OBJLoader;
import engine.graphics.models.TexturedModel;
import engine.graphics.particles.ParticleMaster;
import engine.graphics.particles.ParticleSystem;
import engine.graphics.particles.ParticleSystemStream;
import engine.graphics.particles.ParticleTexture;
import engine.graphics.renderEngine.Loader;
import engine.graphics.renderEngine.MasterRenderer;
import engine.graphics.textures.ModelTexture;
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
    private List<Entity> entities;
    private List<GUIText> texts;
    private List<Vector3f> ends;
    private Light sun;
    public RenderLevel(ShivtLevel shivtLevel, FontType font) {
        lines = new ArrayList<>();
        entities = new ArrayList<>();
        ends = new ArrayList<>();
        texts = new ArrayList<>();
        ParticleTexture pt = new ParticleTexture(Loader.loadTexture("frostfire.png"),4,true,true);
        for (Route route: shivtLevel.getRoutes()) {
            int start = route.getStations()[0],
                    end = route.getStations()[1];
            ParticleSystem ps = new ParticleSystemStream(pt,30,1.7f,.5f, shivtLevel.getStations().get(start).getPosition(),new Vector3f(.01f,.01f,.01f));
            lines.add(ps);
            ends.add(shivtLevel.getStations().get(end).getPosition());
        }
        for(Station station : shivtLevel.getStations()) {
            TexturedModel texturedModel = new TexturedModel(OBJLoader.loadObjModel("spaceStation"),new ModelTexture(Loader.loadTexture("blue.png")));
            texturedModel.getTexture().setReflectivity(.1f);
            Entity e = new Entity(texturedModel,station.getPosition(),0,0,0,.5f);
            entities.add(e);
            GUIText text = new GUIText(station.getTroopsStrength()  + "T",1,font,station.getPosition(),new Vector2f(1f,-1f),1,false);
            text.setColor(new Color(.3,.3,.3));
            texts.add(text);
            MasterRenderer.loadText(text);
        }
        sun = new Light(new Vector3f(0,0,-20),new Color(1.0,1.0,1.0));
    }
    public void process() {
        for (int i = 0; i < lines.size(); i++) {
            lines.get(i).generateParticles(ends.get(i));
        }
        ParticleMaster.update();
        entities.forEach(MasterRenderer::processEntity);
        texts.forEach(MasterRenderer::processText);
        MasterRenderer.processLight(sun);
    }
}
