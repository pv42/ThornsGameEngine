package engine.toolbox.collada;

import engine.graphics.models.RawModel;
import engine.graphics.models.TexturedModel;
import engine.graphics.renderEngine.Loader;
import engine.graphics.textures.ModelTexture;
import engine.toolbox.Util;

/**
 * Created by pv42 on 02.08.16.
 */
public class Vertices {
    private float[][] position;
    private float[][] normal;
    private float[][] texCoord;
    private int[] indices;
    private String imageFile;

    public Vertices(float[][] position, float[][] normal, float[][] texCoord,int[] indices,String imageFile) {
        this.position = position;
        this.normal = normal;
        this.texCoord = texCoord;
        this.indices = indices;
        this.imageFile = imageFile;
    }


    public RawModel getRawModel() {

        return Loader.loadToVAO(Util.get1DArray(position),Util.get1DArray(texCoord),Util.get1DArray(normal),indices);
    }
    public TexturedModel getTexturedModel() {
        return new TexturedModel(getRawModel(),new ModelTexture(Loader.loadTexture(imageFile)));
    }

    public String getImageFile() {
        return imageFile;
    }

    public int[] getIndices() {
        return indices;
    }

    public float[][] getTexCoord() {
        return texCoord;
    }

    public float[][] getNormal() {
        return normal;
    }

    public float[][] getPosition() {
        return position;
    }

    public void setPosition(float[][] position) {
        this.position = position;
    }

    public void setNormal(float[][] normal) {
        this.normal = normal;
    }

    public void setTexCoord(float[][] texCoord) {
        this.texCoord = texCoord;
    }

    public void setIndices(int[] indices) {
        this.indices = indices;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }
}
