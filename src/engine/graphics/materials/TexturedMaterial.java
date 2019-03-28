package engine.graphics.materials;

import engine.graphics.textures.Texture;

public abstract class TexturedMaterial implements Material {
    private Texture texture;

    public TexturedMaterial(Texture texture) {
        this.texture = texture;
    }
}
