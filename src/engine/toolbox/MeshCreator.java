package engine.toolbox;

import engine.graphics.glglfwImplementation.GLLoader;
import engine.graphics.glglfwImplementation.models.GLRawModel;

import static java.lang.Math.PI;

/**
 * @author pv42
 * creates simple meshes
 */
public class MeshCreator {
    /**
     * creates a mesh box parallel to the axis
     *
     * @param lwrX boxes lower x coordinate
     * @param uprX boxes lower x coordinate
     * @param lwrY boxes lower y coordinate
     * @param uprY boxes lower y coordinate
     * @param lwrZ boxes lower z coordinate
     * @param uprZ boxes lower z coordinate
     * @return boxes raw model
     */
    public static GLRawModel createBox(float lwrX, float uprX, float lwrY, float uprY, float lwrZ, float uprZ) {
        float[] positions = {
                lwrX, lwrY, lwrZ,
                lwrX, lwrY, uprZ,
                lwrX, uprY, lwrZ,
                lwrX, uprY, uprZ,

                uprX, uprY, uprZ,
                uprX, uprY, lwrZ,
                uprX, lwrY, uprZ,
                uprX, lwrY, lwrZ,

                lwrX, lwrY, lwrZ,
                lwrX, lwrY, uprZ,
                uprX, lwrY, lwrZ,
                uprX, lwrY, uprZ,

                lwrX, uprY, lwrZ,
                lwrX, uprY, uprZ,
                uprX, uprY, lwrZ,
                uprX, uprY, uprZ,

                lwrX, lwrY, lwrZ,
                lwrX, uprY, lwrZ,
                uprX, lwrY, lwrZ,
                uprX, uprY, lwrZ,

                lwrX, lwrY, uprZ,
                lwrX, uprY, uprZ,
                uprX, lwrY, uprZ,
                uprX, uprY, uprZ,

        };
        float[] textures = {
                0, 0,
                0, 1,
                1, 0,
                1, 1,

                0, 0,
                0, 1,
                1, 0,
                1, 1,

                0, 0,
                0, 1,
                1, 0,
                1, 1,

                0, 0,
                0, 1,
                1, 0,
                1, 1,

                0, 0,
                0, 1,
                1, 0,
                1, 1,

                0, 0,
                0, 1,
                1, 0,
                1, 1,
        };
        float[] normals = {
                -1, 0, 0,
                -1, 0, 0,
                -1, 0, 0,
                -1, 0, 0,

                1, 0, 0,
                1, 0, 0,
                1, 0, 0,
                1, 0, 0,

                0, -1, 0,
                0, -1, 0,
                0, -1, 0,
                0, -1, 0,
                0, 1, 0,
                0, 1, 0,
                0, 1, 0,
                0, 1, 0,


                0, 0, -1,
                0, 0, -1,
                0, 0, -1,
                0, 0, -1,
                0, 0, 1,
                0, 0, 1,
                0, 0, 1,
                0, 0, 1,
        };
        int[] indices = {
                0, 1, 2,
                2, 1, 3,
                4, 6, 5,
                5, 6, 7,

                8, 10, 9,
                9, 10, 11,
                12, 13, 14,
                14, 13, 15,

                16, 17, 18,
                18, 17, 19,
                20, 22, 21,
                21, 22, 23,
        };
        return GLLoader.loadToVAO(positions, textures, normals, indices);
    }

    /**
     * creates a mesh box parallel to the axis with given size
     *
     * @param xSize boxes x size
     * @param ySize boxes y size
     * @param zSize boxes z size
     * @return created GLRawModel of a box
     */
    public static GLRawModel createBox(float xSize, float ySize, float zSize) {
        return createBox(-xSize / 2, xSize / 2, -ySize / 2, ySize / 2, -zSize / 2, zSize / 2);
    }

    public static GLRawModel createCircle(float radius, int circleVertices) {
        float[] positions = new float[circleVertices * 3];
        for (int i = 0; i < circleVertices * 3; i += 3) {
            float alpha = (float) (i * 2 * PI / circleVertices / 3);
            positions[i] = (float) Math.sin(alpha) * radius;
            positions[i + 1] = (float) Math.cos(alpha) * radius;
            positions[i + 2] = 0;
        }
        int[] indices = new int[3 * circleVertices - 6];
        for (int i = 0; i < circleVertices - 2; i += 1) {
            indices[3 * i] = 0;
            indices[3 * i + 1] = i + 2;
            indices[3 * i + 2] = i + 1;
        }
        float[] textures = new float[circleVertices * 2];
        for (int i = 0; i < circleVertices * 2; i += 2) {
            float alpha = (float) (i * 2 * PI / circleVertices / 2);
            textures[i] = (float) Math.sin(alpha) * radius;
            textures[i + 1] = (float) Math.cos(alpha) * radius;
        }
        float[] normals = new float[circleVertices * 3];
        return GLLoader.loadToVAO(positions, textures, normals, indices);
    }
}
