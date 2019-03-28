package engine.graphics.glglfwImplementation.models;

import engine.graphics.materials.Material;

/***
 * Created by pv42 on 17.06.16.
 */
public class GLMaterializedModel {
    private GLRawModel rawmodel;
    private Material material;
    private boolean isAnimated;

    public GLMaterializedModel(GLRawModel rawmodel, Material material) {
        this(rawmodel, material, !(rawmodel.getJoints() == null));
    }

    public GLMaterializedModel(GLRawModel rawModel, Material material, boolean isAnimated) {
        this.rawmodel = rawModel;
        this.material = material;
        this.isAnimated = isAnimated;
    }

    public GLRawModel getRawModel() {
        return rawmodel;
    }

    public Material getMaterial() {
        return material;
    }

    public boolean isAnimated() {
        return isAnimated;
    }
}
