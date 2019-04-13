package engine.graphics.glglfwImplementation.models;

import engine.graphics.materials.Material;

/***
 * Created by pv42 on 17.06.16.
 */
public class GLMaterializedModel {
    private final GLRawModel rawModel;
    private Material material;
    private boolean isAnimated;

    public GLMaterializedModel(GLRawModel rawModel, Material material) {
        this(rawModel, material, !(rawModel.getJoints() == null));
    }

    public GLMaterializedModel(GLRawModel rawModel, Material material, boolean isAnimated) {
        this.rawModel = rawModel;
        this.material = material;
        this.isAnimated = isAnimated;
    }

    public GLRawModel getRawModel() {
        return rawModel;
    }

    public Material getMaterial() {
        return material;
    }

    public boolean isAnimated() {
        return isAnimated;
    }
}
