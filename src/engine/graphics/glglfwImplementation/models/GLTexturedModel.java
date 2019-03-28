package engine.graphics.glglfwImplementation.models;

import engine.graphics.glglfwImplementation.textures.GLModelTexture;

/***
 * Created by pv42 on 17.06.16.
 */
public class GLTexturedModel {
    private GLRawModel rawmodel;
    private GLModelTexture texture;
    private boolean isAnimated;

    public GLTexturedModel(GLRawModel rawmodel, GLModelTexture texture) {
        this(rawmodel,texture,!(rawmodel.getJoints() == null));
    }

    public GLTexturedModel(GLRawModel rawModel, GLModelTexture texture, boolean isAnimated) {
        this.rawmodel = rawModel;
        this.texture = texture;
        this.isAnimated = isAnimated;
    }

    public GLRawModel getRawModel() {
        return rawmodel;
    }

    public GLModelTexture getTexture() {
        return texture;
    }

    public boolean isAnimated() {
        return isAnimated;
    }
}
