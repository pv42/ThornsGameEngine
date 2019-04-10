package engine.graphics;

import engine.graphics.glglfwImplementation.entities.GLEntity;
import engine.graphics.glglfwImplementation.guis.GuiTexture;
import engine.graphics.glglfwImplementation.lines.LineModel;
import engine.graphics.glglfwImplementation.models.GLMaterializedModel;
import engine.graphics.lights.PointLight;
import engine.graphics.terrains.Terrain;
import engine.graphics.text.GuiText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene {
    private static final String TAG = "Scene";
    //rendering objects
    private Map<List<GLMaterializedModel>, List<GLEntity>> entities;
    private List<Terrain> terrains;
    private List<LineModel> lineStripModels;
    private List<GuiTexture> guis;
    private List<GuiText> texts;
    private List<PointLight> lights;

    public Scene() {
        entities = new HashMap<>();
        terrains = new ArrayList<>();
        lineStripModels = new ArrayList<>();
        guis = new ArrayList<>();
        texts = new ArrayList<>();
        lights = new ArrayList<>();
    }

    public Map<List<GLMaterializedModel>, List<GLEntity>> getEntities() {
        return entities;
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

    public List<GuiText> getTexts() {
        return texts;
    }

    public List<PointLight> getLights() {
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
        List<GLMaterializedModel> entityModels = glEntity.getModels();
        List<GLEntity> batch = entities.get(entityModels);
        if (batch != null) {
            batch.add(glEntity);
        } else {
            List<GLEntity> newBatch = new ArrayList<>();
            newBatch.add(glEntity);
            entities.put(entityModels, newBatch);
        }
    }

    public void addLine(LineModel lineStripModel) {
        lineStripModels.add(lineStripModel);
    }

    public void addGui(GuiTexture gui) {
        guis.add(gui);
    }

    public void addText(GuiText text) {
        texts.add(text);
    }

    public void addLight(PointLight light) {
        lights.add(light);
    }

    public void clear() {
        guis.clear();
        terrains.clear();
        entities.clear();
        lineStripModels.clear();
        texts.clear();
        lights.clear();
    }
}
