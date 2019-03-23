package engine.graphics;

import engine.graphics.glglfwImplementation.entities.GLEntity;
import engine.graphics.glglfwImplementation.models.GLTexturedModel;
import engine.graphics.glglfwImplementation.text.GLGuiText;
import engine.graphics.guis.GuiTexture;
import engine.graphics.lights.Light;
import engine.graphics.lines.LineModel;
import engine.graphics.terrains.Terrain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene {
    private static final String TAG = "Scene";
    //rendering objects
    private Map<List<GLTexturedModel>, List<GLEntity>> entities;
    private Map<List<GLTexturedModel>, List<GLEntity>> aniEntities;
    private List<Terrain> terrains;
    private List<LineModel> lineStripModels;
    private List<GuiTexture> guis;
    private List<GLGuiText> texts;
    private List<Light> lights;

    public Scene() {
        entities = new HashMap<>();
        aniEntities = new HashMap<>();
        terrains = new ArrayList<>();
        lineStripModels = new ArrayList<>();
        guis = new ArrayList<>();
        texts = new ArrayList<>();
        lights = new ArrayList<>();
    }

    public Map<List<GLTexturedModel>, List<GLEntity>> getEntities() {
        return entities;
    }

    public Map<List<GLTexturedModel>, List<GLEntity>> getAniEntities() {
        return aniEntities;
    }

    public List<Terrain> getTerrains() {
        return terrains;
    }

    public List<LineModel> getLineStripModels() {
        return lineStripModels;
    }

    public List<GuiTexture> getGuis() {
        return guis;
    }

    public List<GLGuiText> getTexts() {
        return texts;
    }

    public List<Light> getLights() {
        return lights;
    }

    public void addTerrain(Terrain terrain) {
        terrains.add(terrain);
    }

    public void addEntity(Entity entity) {
        if (!(entity instanceof GLEntity)) {
            throw new UnsupportedOperationException("Can't process none GL entities");
        }
        GLEntity glEntity = (GLEntity) entity;
        List<GLTexturedModel> entityModels = glEntity.getModels();
        List<GLEntity> batch = entities.get(entityModels);
        if (batch != null) {
            batch.add(glEntity);
        } else {
            List<GLEntity> newBatch = new ArrayList<>();
            newBatch.add(glEntity);
            entities.put(entityModels, newBatch);
        }
    }

    public void addAniEntity(GLEntity entity) {
        List<GLTexturedModel> entityModel = entity.getModels(); //// TODO: 10.08.16 ?
        List<GLEntity> batch = aniEntities.get(entityModel);
        if (batch != null) {
            batch.add(entity);
        } else {
            List<GLEntity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            aniEntities.put(entityModel, newBatch);
        }
    }

    public void addLine(LineModel lineStripModel) {
        lineStripModels.add(lineStripModel);
    }

    public void addGui(GuiTexture gui) {
        guis.add(gui);
    }

    public void addText(GLGuiText text) {
        texts.add(text);
    }

    public void addLight(Light light) {
        lights.add(light);
    }

    public void clear() {
        guis.clear();
        terrains.clear();
        entities.clear();
        aniEntities.clear();
        lineStripModels.clear();
        texts.clear();
        lights.clear();
    }
}
