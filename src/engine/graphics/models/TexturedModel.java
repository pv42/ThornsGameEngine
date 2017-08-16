package engine.graphics.models;

import engine.graphics.textures.ModelTexture;

/***
 * Created by pv42 on 17.06.16.
 */
public class TexturedModel {
    private RawModel rawmodel;
    private ModelTexture texture;
    private boolean isAnimated;

    public TexturedModel(RawModel rawmodel, ModelTexture texture) {
        this(rawmodel,texture,false);
    }

    public TexturedModel(RawModel rawModel, ModelTexture texture, boolean isAnimated) {
        this.rawmodel = rawModel;
        this.texture = texture;
        this.isAnimated = isAnimated;
    }

    public RawModel getRawModel() {
        return rawmodel;
    }

    public ModelTexture getTexture() {
        return texture;
    }

    public boolean isAnimated() {
        return isAnimated;
    }
}
