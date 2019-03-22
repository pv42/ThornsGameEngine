package engine.graphics.terrains;

import engine.graphics.glglfwImplementation.GLLoader;
import engine.graphics.glglfwImplementation.models.GLRawModel;
import org.joml.Vector2f;
import org.joml.Vector3f;
import engine.graphics.glglfwImplementation.textures.TerrainTexture;
import engine.graphics.glglfwImplementation.textures.TerrainTexturePack;
import engine.toolbox.Log;
import engine.toolbox.Maths;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/***
 * Created by pv42 on 18.06.16.
 */
public class Terrain {
    private static final float SIZE = 800;
    private static final float MAX_HEIGHT = 40;
    private static final float MAX_PIXEL_COLOR = 0x1000000; // 0xffffff ?
    private static final int GENERATE_VERTEX_COUNT = 128;
    private float x;
    private float z;
    private GLRawModel model;
    private TerrainTexturePack texturePack;
    private TerrainTexture blendMap;
    private float [][] heights;

    public Terrain(int gridX, int gridZ, TerrainTexturePack texturePack, TerrainTexture blendMap,String heightMapFileName) {
        this.texturePack = texturePack;
        this.blendMap = blendMap;
        x = gridX * SIZE;
        z = gridZ * SIZE;
        if(heightMapFileName == null) {
            model = generateTerrain(0);
        } else {
            model = generateTerrainFromFile(heightMapFileName);
        }
    }
    public float getHeightOfTerrain(float worldX, float worldZ) {
        float terrainX = worldX - this.x;
        float terrainZ = worldZ - this.z;
        float gridSquareSize = SIZE / ((float)heights.length - 1);
        int gridX = (int) Math.floor(terrainX / gridSquareSize);
        int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
        if(gridX >= heights.length -1  || gridZ >= heights.length - 1 || gridX<0 || gridZ<0) {
            return 0;
        }
        float xCoord =(terrainX % gridSquareSize)/gridSquareSize;
        float zCoord =(terrainZ % gridSquareSize)/gridSquareSize;
        float answer;
        Vector3f d1 = new Vector3f(1,heights[gridX+1][gridZ],0);
        Vector3f d2 = new Vector3f(0,heights[gridX][gridZ+1],1);
        if(xCoord <= (1-zCoord)) {
            answer = Maths.barryCentric(new Vector3f(0,heights[gridX][gridZ],0),d1,d2,new Vector2f(xCoord,zCoord));
        } else {
            answer = Maths.barryCentric(new Vector3f(1,heights[gridX+1][gridZ+1],1),d1,d2,new Vector2f(xCoord,zCoord));
        }
        if (Float.isNaN(answer)) Log.w("NaN: x=" + worldX + ", z=" + worldZ + "");
        return answer;
    }
    private GLRawModel generateTerrain(int seed) {
        HeightGenerator generator;
        if(seed == 0) {
            generator = new HeightGenerator();
        } else {
            generator = new HeightGenerator(seed, (int) (x / SIZE), (int) (z / SIZE), GENERATE_VERTEX_COUNT);
        }
        int VERTEX_COUNT = GENERATE_VERTEX_COUNT;
        heights = new float[VERTEX_COUNT][VERTEX_COUNT];
        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];
        int vertexPointer = 0;
        for (int i = 0; i < VERTEX_COUNT; i++) {
            for (int j = 0; j < VERTEX_COUNT; j++) {
                vertices[vertexPointer * 3] = (float) j / ((float) VERTEX_COUNT - 1) * SIZE;
                heights[j][i] = generator.generateHeight(j, i);
                vertices[vertexPointer * 3 + 1] = heights[j][i];
                vertices[vertexPointer * 3 + 2] = (float) i / ((float) VERTEX_COUNT - 1) * SIZE;
                Vector3f normal = calculateNormal(j, i, generator);
                normals[vertexPointer * 3] = normal.x;
                normals[vertexPointer * 3 + 1] = normal.y;
                normals[vertexPointer * 3 + 2] = normal.z;
                textureCoords[vertexPointer * 2] = (float) j / ((float) VERTEX_COUNT - 1);
                textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) VERTEX_COUNT - 1);
                vertexPointer++;
            }
        }
        int pointer = 0;
        for (int gz = 0; gz < VERTEX_COUNT - 1; gz++) {
            for (int gx = 0; gx < VERTEX_COUNT - 1; gx++) {
                int topLeft = (gz * VERTEX_COUNT) + gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        return GLLoader.loadToVAO(vertices, textureCoords, normals, indices);
    }
    private GLRawModel generateTerrainFromFile(String heightMap){
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File("res/" + heightMap + ".png"));
        } catch (IOException e) {
            Log.e("couldn't read heightMap '" + heightMap + "'");
        }
        int VERTEX_COUNT = image.getHeight();
        heights = new float[VERTEX_COUNT][VERTEX_COUNT];
        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count*2];
        int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
        int vertexPointer = 0;
        for(int i=0;i<VERTEX_COUNT;i++){
            for(int j=0;j<VERTEX_COUNT;j++){
                vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * SIZE;
                heights[j][i] = getHeight(j,i,image);
                vertices[vertexPointer*3+1] = heights[j][i];
                vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * SIZE;
                Vector3f normal = calculateNormal(j,i,image);
                normals[vertexPointer*3] = normal.x;
                normals[vertexPointer*3+1] = normal.y;
                normals[vertexPointer*3+2] = normal.z;
                textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
                textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
                vertexPointer++;
            }
        }
        int pointer = 0;
        for(int gz=0;gz<VERTEX_COUNT-1;gz++){
            for(int gx=0;gx<VERTEX_COUNT-1;gx++){
                int topLeft = (gz*VERTEX_COUNT)+gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        return GLLoader.loadToVAO(vertices, textureCoords, normals, indices);
    }
    public float getX() {
        return x;
    }
    public float getZ() {
        return z;
    }
    public GLRawModel getModel() {
        return model;
    }
    public TerrainTexturePack getTexturePack() {
        return texturePack;
    }
    public TerrainTexture getBlendMap() {
        return blendMap;
    }
    private float getHeight( int x, int z, BufferedImage image) {
        if( x<0 || z<0 || x>=image.getHeight() || z>=image.getHeight()) return 0;
        float height = image.getRGB(x,z);
        height += MAX_PIXEL_COLOR;
        height /= MAX_PIXEL_COLOR/2f;
        height *= MAX_HEIGHT;
        return height;
    }
    private Vector3f calculateNormal(int x, int z, BufferedImage image ) {
        float heightL = getHeight(x-1 , z, image);
        float heightR = getHeight(x+1 , z, image);
        float heightD = getHeight(x, z-1 , image);
        float heightU = getHeight(x, z+1 , image);
        Vector3f normal = new Vector3f(heightL-heightR, 2f, heightD - heightU);
        normal.normalize();
        return normal;
    }
    private Vector3f calculateNormal(int x, int z, HeightGenerator generator ) {
        float heightL = generator.generateHeight(x-1, z);
        float heightR = generator.generateHeight(x+1, z);
        float heightD = generator.generateHeight(x, z-1);
        float heightU = generator.generateHeight(x, z+1);
        Vector3f normal = new Vector3f(heightL-heightR, 2f, heightD - heightU);
        normal.normalize();
        return normal;
    }
}
