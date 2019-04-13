package engine.graphics.terrains;

import java.util.Random;

import static java.lang.Math.PI;

/***
 * Created by pv42 on 20.06.16.
 */
public class HeightGenerator {
    private static final float AMPLITUDE = 20f;
    private static final float OCTAVES = 3;
    private static final float ROUGHNESS = 0.3f;
    private final Random random = new Random();
    private int seed ;
    private int gridX=0,gridZ=0;
    private int vertexCount=0;
    public HeightGenerator() {
        seed = random.nextInt(1000000000);
    }
    public HeightGenerator(int seed, int gridX, int gridZ, int vertexCount) {
        this.seed = seed;
        this.gridX = gridX;
        this.gridZ = gridZ;
        this.vertexCount = vertexCount;
    }
    public float generateHeight(int x ,int z) {
        x += gridX * vertexCount;
        z += gridZ * vertexCount;
        float total = 0;
        float freq = (float)Math.pow(2, OCTAVES);
        for (int i = 0; i < OCTAVES; i++) {
            freq /= 2;
            float amp = (float) Math.pow(ROUGHNESS, i) * AMPLITUDE;
            total += getInterpolatedNoise(x / freq, z / freq) * amp;
        }
        return total;
    }
    private float getSmoothNoise(int x, int z){
        float corners = (noise(x-1, z-1) + noise(x-1, z+1)
                + noise(x+1, z-1)+ noise(x+1, z+1)) / 16f;
        float sides = (noise(x, z-1) + noise(x, z+1)
                + noise(x-1, z)+ noise(x+1, z)) / 8f;
        float center = noise(x,z) / 4f ;
        return corners + sides + center;
    }
    private float noise(int x, int z) {
        random.setSeed(x * 53412 + z * 29118 + seed);
        return  random.nextFloat() * 2f - 1f;
    }
    private float interpolate(float a, float b, float blend) {
        double theta = blend * PI;
        float f = (float)(1f - Math.cos(theta)) * 0.5f;
        return  a * (1f -f) + b * f;
    }
    private float getInterpolatedNoise(float x, float z) {
        int intX = (int) x;
        int intZ = (int) z;
        float fracX = x - intX;
        float fracZ = z - intZ;
        float v1 = getSmoothNoise(intX,intZ);
        float v2 = getSmoothNoise(intX + 1,intZ);
        float v3 = getSmoothNoise(intX,intZ + 1);
        float v4 = getSmoothNoise(intX + 1,intZ + 1);
        float i1 = interpolate(v1,v2,fracX);
        float i2 = interpolate(v3,v4,fracX);
        return interpolate(i1,i2,fracZ);


    }
}
