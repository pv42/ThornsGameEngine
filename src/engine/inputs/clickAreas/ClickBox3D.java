package engine.inputs.clickAreas;

import engine.graphics.cameras.Camera;
import engine.graphics.renderEngine.MasterRenderer;
import engine.toolbox.Convertation;
import engine.toolbox.Maths;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by pv42 on 20.09.2016.
 */
public class ClickBox3D extends ClickArea{
    private float xmin,ymin,zmin,xmax,ymax,zmax;
    private Camera camera;
    public ClickBox3D(Camera camera, Vector3f min, Vector3f max) {
        this.camera = camera;
        xmin = min.x;
        ymin = min.y;
        zmin = min.z;
        xmax = max.x;
        ymax = max.y;
        zmax = max.z;
    }
    @Override
    public boolean isPointIn(Vector2f point) {
        Vector3f v = Convertation.getWorldRay(point.x, point.y, MasterRenderer.getProjectionMatrix(), Maths.createViewMatrix(camera));
        //points;
        /*
             y
             ^
             |
             |  H---------G
               /|        /|
              / |       / |
             D--+------C  |
             |  |      |  |
             |  E------+--F
             | /       | /
             |/        |/
             A---------B    -----> x
            /
           /
          /
         <
        -z
        */
        Vector3f A,B,C,D,E,F,G,H, I = new Vector3f();
        A = new Vector3f(xmin,ymin,zmin);
        B = new Vector3f(xmax,ymin,zmin);
        C = new Vector3f(xmax,ymax,zmin);
        D = new Vector3f(xmin,ymax,zmin);
        E = new Vector3f(xmin,ymin,zmax);
        F = new Vector3f(xmax,ymin,zmax);
        G = new Vector3f(xmax,ymax,zmax);
        H = new Vector3f(xmin,ymax,zmax);
        Vector3f[] quad = new Vector3f[4];
        for(int i = 0; i<=4; i++) {
            if(i == 0) quad = new Vector3f[] {A,B,C,D};
            if(i == 1) quad = new Vector3f[] {A,B,F,E};
            if(i == 2) quad = new Vector3f[] {A,E,H,D};
            if(i == 3) quad = new Vector3f[] {E,F,G,H};
            if(i == 4) quad = new Vector3f[] {D,C,G,H};
            if(i == 5) quad = new Vector3f[] {B,F,G,C};
            I = Maths.intersectLinePlane(camera.getPosition(),v,quad[0],quad[1],quad[2]);
            if(Maths.isPointInQuad(I,quad)) return true;
        }
        return false;
    }
}
