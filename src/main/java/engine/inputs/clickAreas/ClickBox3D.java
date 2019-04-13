package engine.inputs.clickAreas;

import engine.graphics.cameras.ThreeDimensionCamera;
import engine.graphics.glglfwImplementation.MasterRenderer;
import engine.toolbox.Conversion;
import engine.toolbox.Maths;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Created by pv42 on 20.09.2016.
 *
 * @author pv42
 */
public class ClickBox3D implements ClickArea {
    private ThreeDimensionCamera camera;
    private Vector3f pA, pB, pC, pD, pE, pF, pG, pH;

    public ClickBox3D(ThreeDimensionCamera camera, Vector3f min, Vector3f max) {
        this.camera = camera;
        pA = new Vector3f(min.x, min.y, min.z);
        pB = new Vector3f(max.x, min.y, min.z);
        pC = new Vector3f(max.x, max.y, min.z);
        pD = new Vector3f(min.x, max.y, min.z);
        pE = new Vector3f(min.x, min.y, max.z);
        pF = new Vector3f(max.x, min.y, max.z);
        pG = new Vector3f(max.x, max.y, max.z);
        pH = new Vector3f(min.x, max.y, max.z);
    }

    @Override
    public boolean isPointIn(Vector2f point) {
        Vector3f v = Conversion.getWorldRay(point.x, point.y, MasterRenderer.getProjectionMatrix(), camera.getViewMatrix());
        //points;
        /*
             y
             ^
             |
             |  H---------G
               /|        /|
              / |       / |
             D---------C  |
             |  |      |  |
             |  E-  -  |--F
             | /       | /
             |         |/
             A---------B    -----> x
            /
           /
          /
         <
        -z
        */


        Vector3f[] quad = new Vector3f[4];
        for (int i = 0; i <= 4; i++) {
            if (i == 0) quad = new Vector3f[]{pA, pB, pC, pD};
            if (i == 1) quad = new Vector3f[]{pA, pB, pF, pE};
            if (i == 2) quad = new Vector3f[]{pA, pE, pH, pD};
            if (i == 3) quad = new Vector3f[]{pE, pF, pG, pH};
            if (i == 4) quad = new Vector3f[]{pD, pC, pG, pH};
            if (i == 5)
                quad = new Vector3f[]{pB, pF, pG, pC}; //not necessary since any Point hits the box at least twice
            Vector3f I = Maths.intersectLinePlane(camera.getPosition(), v, quad[0], quad[1], quad[2]);
            if (Maths.isPointInQuad(I, quad)) return true;
        }
        return false;
    }
}
