package engine.graphics.lines;

import engine.toolbox.Color;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/***
 * Created by pv42 on 12.08.16.
 */
public class LineModel {
    private int vaoID;
    private Color color;
    private int pointCount;
    private Vector3f position = new Vector3f();
    private Vector3f rotation = new Vector3f();
    private float scale = 1;
    private Matrix4f transformation;

    public LineModel(int vaoID, Color color, int pointCount) {
        this.vaoID = vaoID;
        this.color = color;
        this.pointCount = pointCount;
        transformation = new Matrix4f();
        transformation.identity();
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public int getVaoID() {
        return vaoID;
    }

    public Color getColor() {
        return color;
    }


    public int getPointCount() {
        return pointCount;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Matrix4f getTransformation() {
        return transformation;
    }

    public void setTransformation(Matrix4f transformation) {
        this.transformation = transformation;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
