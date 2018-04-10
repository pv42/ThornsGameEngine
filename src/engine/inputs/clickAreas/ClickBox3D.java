package engine.inputs.clickAreas;

import engine.graphics.cameras.ThreeDimensionCamera;
import engine.graphics.renderEngine.MasterRenderer;
import engine.toolbox.Conversion;
import engine.toolbox.Maths;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Created by pv42 on 20.09.2016.
 */
public class ClickBox3D implements ClickArea {
    private float xmin,ymin,zmin,xmax,ymax,zmax;
    private ThreeDimensionCamera camera;
    private Vector3f pA,pB,pC,pD,pE,pF,pG,pH, I = new Vector3f();
    public ClickBox3D(ThreeDimensionCamera camera, Vector3f min, Vector3f max) {
        this.camera = camera;
        xmin = min.x;
        ymin = min.y;
        zmin = min.z;
        xmax = max.x;
        ymax = max.y;
        zmax = max.z;
        pA = new Vector3f(xmin,ymin,zmin);
        pB = new Vector3f(xmax,ymin,zmin);
        pC = new Vector3f(xmax,ymax,zmin);
        pD = new Vector3f(xmin,ymax,zmin);
        pE = new Vector3f(xmin,ymin,zmax);
        pF = new Vector3f(xmax,ymin,zmax);
        pG = new Vector3f(xmax,ymax,zmax);
        pH = new Vector3f(xmin,ymax,zmax);
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
        for(int i = 0; i<=4; i++) {
            if(i == 0) quad = new Vector3f[] {pA,pB,pC,pD};
            if(i == 1) quad = new Vector3f[] {pA,pB,pF,pE};
            if(i == 2) quad = new Vector3f[] {pA,pE,pH,pD};
            if(i == 3) quad = new Vector3f[] {pE,pF,pG,pH};
            if(i == 4) quad = new Vector3f[] {pD,pC,pG,pH};
            if(i == 5) quad = new Vector3f[] {pB,pF,pG,pC}; //not necessary since any Point hits the box at least twice
            I = Maths.intersectLinePlane(camera.getPosition(),v,quad[0],quad[1],quad[2]);
            if(Maths.isPointInQuad(I,quad)) return true;
        }
        return false;
    }
}
