package engine.toolbox;

import engine.graphics.cameras.Camera;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

/***
 * Created by pv42 on 17.06.16.
 */
public class Maths {
    public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        Matrix4f.translate(translation,matrix,matrix);
        Matrix4f.scale(new Vector3f(scale.x,scale.y,1f), matrix,matrix);
        return matrix;
    }
    public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f roatation, float scale) {
        return createTransformationMatrix(translation,roatation.x,roatation.y,roatation.z,scale);
    }
    public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        Matrix4f.translate(translation,matrix,matrix);
        Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1,0,0),matrix,matrix );
        Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0,1,0),matrix,matrix );
        Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0,0,1),matrix,matrix );
        Matrix4f.scale(new Vector3f(scale,scale,scale), matrix,matrix);
        return matrix;
    }
    public static Matrix4f createViewMatrix(Camera camera) {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.setIdentity();
        Matrix4f.rotate((float) Math.toRadians(camera.getPitch()),new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
        Matrix4f.rotate((float) Math.toRadians(camera.getYaw()),  new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
        Matrix4f.rotate((float) Math.toRadians(camera.getRoll()), new Vector3f(0, 0, 1), viewMatrix, viewMatrix);
        Vector3f cameraPos = camera.getPosition();
        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
        return viewMatrix;
    }
    public static Matrix4f createViewMatrixNoRoll(Camera camera) {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.setIdentity();
        Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
        Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
        Vector3f cameraPos = camera.getPosition();
        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
        Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
        return viewMatrix;
    }
    public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) { //todo do not work
        float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
        float l1  = ((p2.z - p3.z) * (pos.x- p3.x) + (p3.x - p2.x) * (pos.y- p3.z)) / det;
        float l2  = ((p3.z - p1.z) * (pos.x- p3.x) + (p1.x - p3.x) * (pos.y- p3.z)) / det;
        float l3 = 1f - l1 - l2;
        return l1*p1.y + l2*p2.y + l3*p3.y;
    }
    public static float sin(float x) {
        return (float) Math.sin(Math.toRadians(x));
    }

    public static float cos(float x) {
        return (float) Math.sin(Math.toRadians(x));
    }
    public static Vector3f scaleVector(Vector3f vector, float scale) {
        vector.x *= scale;
        vector.y *= scale;
        vector.z *= scale;
        return vector;
    }
    //test collision detection(triangle / ray) {
    public static Vector3f intersectLinePlane(Vector3f linepoint, Vector3f linedirect, Vector3f point1, Vector3f point2, Vector3f point3) {
        //ebene in coordinatenform
        Vector3f p = point1;
        Vector3f u = Vector3f.sub(point1,point2,null);
        Vector3f v = Vector3f.sub(point1,point3,null);
        Vector3f n = Vector3f.cross(u,v,null);
        float a = n.x;
        float b = n.y;
        float c = n.z;
        float d = Vector3f.dot(n,p);
        float x,y,z;
        float lambda = a * linepoint.x + b* linepoint.y + c* linepoint.z;
        lambda /= (a * linedirect.x + b* linedirect.y +c* linedirect.z);
        Vector3f s = new Vector3f(
                linepoint.x  + lambda * linedirect.x,
                linepoint.y  + lambda * linedirect.y,
                linepoint.z  + lambda * linedirect.z
        );
        return s;
    }
    public static boolean isPointInTriangle(Vector3f p , Vector3f t1, Vector3f t2, Vector3f t3) {
        Vector3f v0 = Vector3f.sub(t3,t1,null);
        Vector3f v1 = Vector3f.sub(t2,t1,null);
        Vector3f v2 = Vector3f.sub(p,t1,null);
        float dot00 = Vector3f.dot(v0, v0);
        float dot01 = Vector3f.dot(v0, v1);
        float dot02 = Vector3f.dot(v0, v2);
        float dot11 = Vector3f.dot(v1, v1);
        float dot12 = Vector3f.dot(v1, v2);
        float invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
        float u = (dot11 * dot02 - dot01 * dot12) * invDenom;
        float v = (dot00 * dot12 - dot01 * dot02) * invDenom;
        return (u >= 0) && (v >= 0) && (u + v < 1);
    }
    public static boolean isPointInQuad(Vector3f p , Vector3f t1, Vector3f t2, Vector3f t3, Vector3f t4) {
        return isPointInTriangle(p,t1,t2,t3) || isPointInTriangle(p,t1,t3,t4);
    }
    public static boolean isPointInQuad(Vector3f p, Vector3f[] quad) {
        return isPointInQuad(p,quad[0],quad[1],quad[2],quad[3]);
    }
    public static Matrix4f mulMatrices(Matrix4f[] matrices) {
        Matrix4f m = new Matrix4f(matrices[0]);
        for(int i = 1; i < matrices.length; i++) {
            Matrix4f.mul(m,matrices[i],m);
        }
        return m;
    }
    public static Vector3f getPositionComponent(Matrix4f matrix4f) {
        if(Math.abs(matrix4f.m03 + matrix4f.m13 + matrix4f.m23 + Math.abs(matrix4f.m33-1)) > 0.01)
            Log.w("unusual TM:\n" + matrix4f);
        return new Vector3f(matrix4f.m30,matrix4f.m31, matrix4f.m32);
    }
    public static Vector3f getRotationComponent(Matrix4f m) {
        float sy = m.m20;
        float ry = (float) Math.asin(sy);
        float cy = (float) Math.cos(ry);
        float sz = - m.m10 / cy;
        float rz = (float) Math.asin(sz);
        float sx = - m.m21 / cy;
        float rx = (float) Math.asin(sx);
        //Log.d("rx:" + Math.toDegrees(rx));
        //Log.d("ry:" + Math.toDegrees(ry));
        //Log.d("rz:" + Math.toDegrees(rz));
        return new Vector3f(rx, ry, rz);
    }
    public static Vector3f getNormalisedPosition(Vector4f in) {
        return new Vector3f(in.x/in.w,in.y/in.w,in.z/in.w);
    }
    public static Vector4f getAreaFromPositionAndScale(Vector2f pos,Vector2f scale) {
        Vector2f min = Convertation.pixelFromOpenGLSpace2D(new Vector2f(pos.getX() - scale.getX(),pos.getY() - scale.getY()));
        Vector2f max = Convertation.pixelFromOpenGLSpace2D(new Vector2f(pos.getX() + scale.getX(),pos.getY() + scale.getY()));
        return new Vector4f(min.x,min.y,max.x,max.y);
    }
}

