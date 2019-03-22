package engine.graphics.glglfwImplementation.models;

import engine.graphics.glglfwImplementation.textures.ModelTexture;

/***
 * Created by pv42 on 17.06.16.
 */
public class GLTexturedModel {
    private GLRawModel rawmodel;
    private ModelTexture texture;
    private boolean isAnimated;

    public GLTexturedModel(GLRawModel rawmodel, ModelTexture texture) {
        this(rawmodel,texture,!(rawmodel.getJoints() == null));
    }

    public GLTexturedModel(GLRawModel rawModel, ModelTexture texture, boolean isAnimated) {
        this.rawmodel = rawModel;
        this.texture = texture;
        this.isAnimated = isAnimated;
    }

    public GLRawModel getRawModel() {
        return rawmodel;
    }

    public ModelTexture getTexture() {
        return texture;
    }

    public boolean isAnimated() {
        return isAnimated;
    }
}
