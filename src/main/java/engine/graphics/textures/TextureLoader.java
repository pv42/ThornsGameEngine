package engine.graphics.textures;

import engine.graphics.display.Window;
import engine.graphics.glglfwImplementation.guis.GuiTexture;
import engine.graphics.glglfwImplementation.textures.TerrainTexture;
import engine.graphics.particles.ParticleTexture;
import org.joml.Vector2f;

public interface TextureLoader {
    Texture loadTexture(String file);

    Texture loadTexture(String file, boolean flip);

    int loadCubeMapTexture(String nameStart, String extension);

    GuiTexture loadGuiTexture(String name, Vector2f position, Vector2f scale, Window window);

    TerrainTexture loadTerrainTexture(String file);

    ParticleTexture loadParticleTexture(String file, boolean flip, int numberOfRows, boolean isAdditive, boolean randomizeAtlas);

    ParticleTexture loadParticleTexture(String file, int numberOfRows, boolean isAdditive, boolean randomizeAtlas);

    void cleanUp();
}
