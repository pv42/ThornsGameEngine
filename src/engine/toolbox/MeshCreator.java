package engine.toolbox;

import engine.graphics.models.RawModel;
import engine.graphics.renderEngine.Loader;

import static java.lang.Math.PI;

/**
 * @author pv42
 * creates simple meshes
 */
public class MeshCreator {
    /**
     * creates a mesh box parallel to the axis
     * @param lwrX boxes lower x coordinate
     * @param uprX boxes lower x coordinate
     * @param lwrY boxes lower y coordinate
     * @param uprY boxes lower y coordinate
     * @param lwrZ boxes lower z coordinate
     * @param uprZ boxes lower z coordinate
     * @return boxes raw model
     */
    public static RawModel createBox(float lwrX, float uprX, float lwrY, float uprY, float lwrZ, float uprZ) {
        float[] positions = {
                uprX, lwrY, lwrZ,
                uprX, lwrY, uprZ,
                lwrX, lwrY, uprZ,
                lwrX, lwrY, lwrZ,
                uprX, uprY, lwrZ,
                uprX, uprY, uprZ,
                lwrX, uprY, uprZ,
                lwrX, uprY, lwrZ
        };
        float[] textures = {
                0,0, 1,0,
                0,0, 0,1,
                0,0, 0,0,
                0,1, 1,0
        };
        float[] normals = {
                 0, 0, -1, 1, 0,  0,
                -1, 0,  0, 0, 0, -1,
                 1, 0,  0, 0, 0,  1,
                -1, 0,  0, 0, 0, -1
        };
        int[] indices = {
                1, 3, 0, 7, 5, 4,
                4, 1, 0, 5, 2, 1,
                2, 7, 3, 0, 7, 4,
                1, 2, 3, 7, 6, 5,
                4, 5, 1, 5, 6, 2,
                2, 6, 7, 0, 3, 7
        };
        return Loader.loadToVAO(positions, normals, textures, indices);
    }
    /**
     * creates a mesh box parallel to the axis
     * @param xSize boxes x size
     * @param ySize boxes y size
     * @param zSize boxes z size
     */
    public static RawModel createBox(float xSize, float ySize, float zSize) {
        return createBox(-xSize/2,xSize/2,-ySize/2,ySize/2,-zSize/2, zSize/2);
    }

    public static RawModel createCircle(float radius, int circleVertices) {
        float[] positions = new float[circleVertices * 3];
        for (int i = 0; i < circleVertices * 3; i += 3) {
            float alpha = (float) (i * 2*PI / circleVertices / 3);
            positions[i] = (float) Math.sin(alpha) * radius;
            positions[i+1] = (float) Math.cos(alpha) * radius;
            positions[i+2] = 0;
        }
        int[] indices = new int[3 * circleVertices - 6];
        for (int i = 0; i < circleVertices-2; i += 1){
            indices[3*i] = 0;
            indices[3*i+1] = i+2;
            indices[3*i+2] = i+1;
        }
        float[] textures = new float[circleVertices * 2];
        for (int i = 0; i < circleVertices * 2; i += 2) {
            float alpha = (float) (i * 2*PI / circleVertices / 2);
            textures[i] = (float) Math.sin(alpha) * radius;
            textures[i+1] = (float) Math.cos(alpha) * radius;
        }
        float[] normals = new float[circleVertices * 3];
        return Loader.loadToVAO(positions, textures, normals, indices);
    }
}
