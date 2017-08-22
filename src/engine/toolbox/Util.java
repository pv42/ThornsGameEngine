package engine.toolbox;

import engine.graphics.animation.Joint;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by pv42 on 04.08.16.
 */
public class Util {
    public static float[] get1DArray(float[][] in) {
        float[] out = new float[in.length * in[0].length];
        for( int i = 0; i < in.length * in[0].length; i++) {
            out[i] = in[i/in[0].length][i%in[0].length];
        }
        return out;
    }
    public static List<String> getList(String[][] in) {
        List<String> out = new ArrayList<>();
        for (int i = 0; i < in.length; i++) {
            for (int j = 0; j < in[0].length; j++) {
                out.add(in[i][j]);
            }
        }
        return out;
    }
    public static List<Matrix4f> getList(Matrix4f[] in) {
        List<Matrix4f> out = new ArrayList<>();
        for (int i = 0; i < in.length; i++) {
            out.add(in[i]);
        }
        return out;
    }
    public static List<Float> getList(float[][] in) {
        List<Float> out = new ArrayList<>();
        for (int i = 0; i < in.length; i++) {
            for (int j = 0; j < in[0].length; j++) {
                out.add(in[i][j]);
            }
        }
        return out;
    }
    public static List<Integer> getList(int[][] in) {
        List<Integer> out = new ArrayList<>();
        for (int i = 0; i < in.length; i++) {
            for (int j = 0; j < in[0].length; j++) {
                out.add(in[i][j]);
            }
        }
        return out;
    }
    public static List<Integer> getList(int[] in) {
        List<Integer> out = new ArrayList<>();
        for (int i = 0; i < in.length; i++) {
            out.add(in[i]);
        }
        return out;
    }
    public static List<Joint> getList(Joint[] in) {
        List<Joint> out = new ArrayList<>();
        for (int i = 0; i < in.length; i++) {
            out.add(in[i]);
        }
        return out;
    }
    public static List<Joint> getList(Map<String,Joint> in) {
        List<Joint> out = new ArrayList<>(in.values());
        return out;
    }
    public static float[][] get2DArray(List<Vector4f> in) {
        float out[][] = new float[in.size()][3];
        for (int i = 0; i < in.size(); i++) {
            Vector4f vec = in.get(i);
            out[i][0] = vec.x;
            out[i][1] = vec.y;
            out[i][2] = vec.z;
        }
        return out;
    }
    public static float[] get1DArray(List<Vector3f> in) {
        float out[] = new float[in.size() * 3];
        for(int i = 0; i < in.size(); i++) {
            out[3 * i] = in.get(i).x;
            out[3 * i + 1] = in.get(i).y;
            out[3 * i + 2] = in.get(i).z;
        }
        return out;
    }
    public static float[] get1DArray(Vector3f[] in) {
        float out[] = new float[in.length * 3];
        for(int i = 0; i < in.length; i++) {
            out[3 * i] = in[i].x;
            out[3 * i + 1] = in[i].y;
            out[3 * i + 2] = in[i].z;
        }
        return out;
    }
    public static Vector4f getVector4f(Vector3f in) {
        return new Vector4f(in.x,in.y,in.z,1);
    }
    public static Vector2f getVector2f(Vector4f in) {
        return new Vector2f(in.x/in.w,in.y/in.w);
    }

}
