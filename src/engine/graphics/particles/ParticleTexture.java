package engine.graphics.particles;

/**
   Created by pv42 on 22.06.16.
 */
public class ParticleTexture {
    private int texID;
    private int numberOfRows;
    private boolean isAdditive;
    private boolean randomizeAtlas;

    public ParticleTexture(int texID, int numberOfRows,boolean isAdditive,boolean randomizeAtlas) {
        this.texID = texID;
        this.numberOfRows = numberOfRows;
        this.isAdditive = isAdditive;
        this.randomizeAtlas = randomizeAtlas;
    }

    public int getTextureID() {
        return texID;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public boolean isAdditive() {
        return isAdditive;
    }

    public boolean isRandomizeAtlas() {
        return randomizeAtlas;
    }
}
