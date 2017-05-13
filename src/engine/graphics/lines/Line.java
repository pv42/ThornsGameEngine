package engine.graphics.lines;

import engine.toolbox.Color;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by pv42 on 12.08.16.
 */
public class Line {
    private Vector3f point1;
    private Vector3f point2;
    private Color color;

    public Line(Vector3f point1, Vector3f point2) {
        this.point1 = point1;
        this.point2 = point2;
    }

    public Vector3f getPoint1() {
        return point1;
    }

    public void setPoints1(Vector3f point1) {
        this.point1 = point1;
    }

    public Vector3f getPoint2() {
        return point2;
    }

    public void setPoints2(Vector3f point2) {
        this.point2 = point2;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

}
