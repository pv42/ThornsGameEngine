package engine.toolbox;

import engine.graphics.display.DisplayManager;
import engine.graphics.display.Window;
import org.joml.*;

/***
 * Created by pv42 on 21.07.16.
 */
public class Conversion {
    private Conversion() {} //this can never be an object
    public static Vector2f pixelFromOpenGLSpace2D(Vector2f openGLSpace) {
        Vector2i size = DisplayManager.getActiveWindow().getSize();
        return new Vector2f((openGLSpace.x + 1)*.5f* size.x,(openGLSpace.y + 1)*.5f* size.y);
    }
    public static Vector3f getWorldRay(float pixelX,float pixelY,Matrix4f projectionMatrix, Matrix4f viewMatrix) {
        Vector2f normCoords = normalizedDeviceCoordsFromPixelCoods(pixelX,pixelY);
        Vector4f clipCoords = new Vector4f(normCoords.x,normCoords.y,-1,1);
        Vector4f eyeCoords = toEyeCoords(clipCoords, projectionMatrix);
        return toWorldCoords(eyeCoords, viewMatrix);
    }
    public static Vector2f normalizedDeviceCoordsFromPixelCoods(float pixelX, float pixelY) {
        //float x = (2f * pixelX) / Display.getWidth() - 1;
        //float y = (2f * pixelY) /Display.getHeight() - 1;
        return new Vector2f(); //// TODO: 12.09.2016
    }
    private static Vector4f toEyeCoords(Vector4f clipCoords,Matrix4f projectionMatrix) {

        Matrix4f invertProjection = projectionMatrix.invert(new Matrix4f());
        Vector4f eyeCoords = invertProjection.transform(clipCoords);
        return new Vector4f(eyeCoords.x, eyeCoords.y, -1 , 0);
    }
    private static Vector3f toWorldCoords(Vector4f eyeCoords,Matrix4f viewMatrix) {
        Matrix4f invertedView = viewMatrix.invert(new Matrix4f());
        Vector4f rayWorld = invertedView.transform(eyeCoords);
        Vector3f mouseRay = new Vector3f(rayWorld.x,rayWorld.y,rayWorld.z);
        mouseRay.normalize();
        return mouseRay;
    }
}