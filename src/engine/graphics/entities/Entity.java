package engine.graphics.entities;

import org.joml.Vector3f;

public interface Entity {
    void setPosition(Vector3f position);
    void setPosition(float x, float y, float z);
    void setScale(float scale);
    void setRx(float rx);
    void setRy(float ry);
    void setRz(float rz);
    Vector3f getPosition();
    float getScale();
    float getRx();
    float getRy();
    float getRz();
}
