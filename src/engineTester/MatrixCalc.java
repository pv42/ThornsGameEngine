package engineTester;

import engine.toolbox.Maths;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/***
 * Created by pv42 on 10.08.16.
 */
public class MatrixCalc {
    public static void main(String args[]) {
        Matrix4f tm = Maths.createTransformationMatrix(new Vector3f(9, 8, 5), 0, 90, 0, 1);
        System.out.println(tm);
        Vector3f transl = new Vector3f(1,0,0);
        tm.translate(transl);
        System.out.println(tm);

        //Vector3f rot = Maths.getRotationComponent(tm);

        //System.out.println("\nx:" + Math.toDegrees(rot.x) + "\ny:" + Math.toDegrees(rot.y) + "\nz:" + Math.toDegrees(rot.z));
    }
    /*private static Matrix4f readMatrix4f(String sin) {
        Matrix4f matrix4f = new Matrix4f();
        float[] array = new float[16];
        int i = 0;
        for (String s:sin.split(" ")) {
            array[i] = Float.valueOf(s);
            i++;
        }
        matrix4f.m00 = array[0];
        matrix4f.m01 = array[1];
        matrix4f.m02 = array[2];
        matrix4f.m03 = array[3];
        matrix4f.m10 = array[4];
        matrix4f.m11 = array[5];
        matrix4f.m12 = array[6];
        matrix4f.m13 = array[7];
        matrix4f.m20 = array[8];
        matrix4f.m21 = array[9];
        matrix4f.m22 = array[10];
        matrix4f.m23 = array[11];
        matrix4f.m30 = array[12];
        matrix4f.m31 = array[13];
        matrix4f.m32 = array[14];
        matrix4f.m33 = array[15];
        return matrix4f;
    }*//*
    static Matrix4f roundMatrix(Matrix4f src) {
        Matrix4f dest = new Matrix4f();
        dest.m00 = Math.round(src.m00);
        dest.m01 = Math.round(src.m01);
        dest.m02 = Math.round(src.m02);
        dest.m03 = Math.round(src.m03);
        dest.m10 = Math.round(src.m10);
        dest.m11 = Math.round(src.m11);
        dest.m12 = Math.round(src.m12);
        dest.m13 = Math.round(src.m13);
        dest.m20 = Math.round(src.m20);
        dest.m21 = Math.round(src.m21);
        dest.m22 = Math.round(src.m22);
        dest.m23 = Math.round(src.m23);
        dest.m30 = Math.round(src.m30);
        dest.m31 = Math.round(src.m31);
        dest.m32 = Math.round(src.m32);
        dest.m33 = Math.round(src.m33);
        return dest;
    }*/

}