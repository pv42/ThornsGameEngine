package engine.toolbox;

import engine.graphics.models.RawModel;
import engine.graphics.renderEngine.Loader;

public class MeshCreator {
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
                0, 0, -1, 1, 0, 0, -1, 0, 0, 0, 0, -1, 1, 0, 0, 0, 0, 1, -1, 0, 0, 0, 0, -1,
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

    public static RawModel createBox(float xSize, float ySize, float zSize) {
        return createBox(-xSize/2,xSize/2,-ySize/2,ySize/2,-zSize/2, zSize/2);
    }
}
