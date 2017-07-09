package engine.graphics.entities;

import engine.graphics.models.MMTexturedModel;
import org.joml.Vector3f;


/**
 * Created by pv42 on 02.07.16.
 */
@Deprecated
public class MMEntity {

    private MMTexturedModel model;
    private Vector3f position;
    private float rx;
    private float ry;
    private float rz;
    private float scale;
    private float textureOffsetX = 0; //// TODO: 23.06.16 !texatlases
    private float textureOffsetY = 0;
    private int textureIndex = 0;

    public MMEntity(MMTexturedModel model, Vector3f position, float rx, float ry, float rz, float scale) {
        this.model = model;
        this.position = position;
        this.rx = rx;
        this.ry = ry;
        this.rz = rz;
        this.scale = scale;
    }
    public MMEntity(MMTexturedModel model, int textureIndex, Vector3f position, float rx, float ry, float rz, float scale) {
        this.model = model;
        this.textureIndex = textureIndex;
        this.position = position;
        this.rx = rx;
        this.ry = ry;
        this.rz = rz;
        this.scale = scale;
    }
    public float getTextureXOffset(){
        int col = textureIndex%model.getTexture().getNumberOfRows();
        return (float)col/(float)model.getTexture().getNumberOfRows();
    }

    public float getTextureYOffset(){
        int row = textureIndex/model.getTexture().getNumberOfRows();
        return (float)row/(float)model.getTexture().getNumberOfRows();
    }



    public MMTexturedModel getModel() {
        return model;
    }

    public void setModels(MMTexturedModel models) {
        this.model = models;
    }

    public float getRx() {
        return rx;
    }

    public void setRx(float rx) {
        this.rx = rx;
    }

    public float getRy() {
        return ry;
    }

    public void setRy(float ry) {
        this.ry = ry;
    }

    public float getRz() {
        return rz;
    }

    public void setRz(float rz) {
        this.rz = rz;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void increasePosition(Vector3f dpos) {
        position.add(dpos);
    }

    public void increasePosition(float dx, float dy, float dz) {
        position.x += dx;
        position.y += dy;
        position.z += dz;
    }

    public void increaseRotation(float drx, float dry, float drz) {
        rx += drx;
        ry += dry;
        rz += drz;
    }
}

