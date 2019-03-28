package engine.graphics.glglfwImplementation.textures;

import engine.graphics.textures.Texture;
import engine.toolbox.Log;

/**
 * Created by pv42 on 17.06.16.
 */

public class GLModelTexture implements Texture {

    private int textureID;
    private final int normalMapID = -1;
    private int specularMapID;

    private float shineDamper = 1;
    private float reflectivity = 0;

    private boolean hasTransparency = false;
    private boolean useFakeLightning = false;
    private boolean hasSpecularMap = false;



    private int numberOfRows = 1;

    public void setHasTransparency(boolean hasTransparency) {
        this.hasTransparency = hasTransparency;
    }

    public boolean hasTransparency() {
        return hasTransparency;
    }

    public GLModelTexture(int texture){
        this.textureID = texture;
    }

    public void setNormalMapID(int normalMapID) {
        //todo this.normalMapID = normalMapID;
        Log.w("ModelT", "nomal map not supported");
    }

    public void setSpecularMapID(int specularMapID) {
        this.specularMapID = specularMapID;
        hasSpecularMap = true;
    }

    public int getSpecularMapID() {
        return specularMapID;
    }

    public boolean hasSpecularMap() {
        return hasSpecularMap;
    }

    public int getID(){
        return textureID;
    }

    public float getShineDamper() {
        return shineDamper;
    }

    public void setShineDamper(float shineDamper) {
        this.shineDamper = shineDamper;
    }

    public float getReflectivity() {
        return reflectivity;
    }

    public void setReflectivity(float reflectivity) {
        this.reflectivity = reflectivity;
    }

    public int getNormalMapID() {
        return normalMapID;
    }

    public boolean isUseFakeLightning() {
        return useFakeLightning;
    }

    public void setUseFakeLightning(boolean useFakeLightning) {
        this.useFakeLightning = useFakeLightning;
    }
    public int getNumberOfRows() {
        return numberOfRows;
    }

    public void setNumberOfRows(int numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    public boolean isHasTransparency() {
        return hasTransparency;
    }
}