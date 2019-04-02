package engine.graphics.materials;

import engine.graphics.textures.Texture;

public class TexturedMaterial extends Material {
    private Texture texture;

    public TexturedMaterial(Texture texture) {
        super();
        this.texture = texture;
    }

    public Texture getTexture() {
        return texture;
    }
}
