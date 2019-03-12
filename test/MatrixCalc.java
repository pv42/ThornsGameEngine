import engine.toolbox.Maths;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

/***
 * Created by pv42 on 10.08.16.
 */
public class MatrixCalc {
    public static void main(String args[]) {
        Matrix4f tm1 = loadMatrix("0 -1 -0.000153 0 -0.001205 0.000153 -0.999999 0 0.999999 0 -0.001205 0 0 0 0 1"); //tm
        Matrix4f tm2 = loadMatrix("1 0 0 2.13821 0 1 0 0.000000 0 0 1 0 0 0 0 1");
        Matrix4f tm3 = loadMatrix("0.997776 -0.000094 -0.066659 0.104977 0.000094 1 -0.000003 -0.000035 0.066659 -0.000003 0.997776 -0.006802 0 0 0 1");
        Matrix4f ibm1 =  loadMatrix("0 -0.001205 0.999999 0 -1 0.000153 0 0 -0.000153 -0.999999 -0.001205 0 0 0 0 1"); //ibm
        Matrix4f ibm2 = loadMatrix("0 -0.001205 0.999999 -2.13821 -1 0.000153 0 -0.000000 -0.000153 -0.999999 -0.001205 0 0 0 0 1");
        Matrix4f ibm3 = loadMatrix("-0.000105 -0.067861 0.997695 -2.23774 -1 0.000156 -0.000094 0.000248 -0.000149 -0.997695 -0.067861 0.156315 0 0 0 1");
        //
        Matrix4f m1 = new Matrix4f().set(tm1).mul(ibm1);
        //m2.transpose();
        Matrix4f m2 = new Matrix4f().set(ibm2).mul(tm2).mul(tm1);
        Matrix4f m3 = new Matrix4f().set(tm2).mul(tm1).mul(ibm2);
        Matrix4f m4 = new Matrix4f().set(tm1).mul(ibm2).mul(tm2);
        Matrix4f m5 = new Matrix4f().set(tm3).mul(tm2).mul(tm1).mul(ibm3);

        System.out.println("\n1 " + calcIdentitarian(m1) + "\n" + ts(m1) + "2:" + calcIdentitarian(m2) +"\n" + ts(m2) +
                "3:" + calcIdentitarian(m3) + "\n" + m3 + "4:"+ calcIdentitarian(m4) + "\n" + m4 +
                "5:" + calcIdentitarian(m5) + "\n" + m5
        );
        //Vector3f rot = Maths.getRotationComponent(tm);

        //System.out.println("\nx:" + Math.toDegrees(rot.x) + "\ny:" + Math.toDegrees(rot.y) + "\nz:" + Math.toDegrees(rot.z));
    }
    private static Matrix4f loadMatrix(String data) {
        String[] split = data.split(" ");
        float[] values = new float[split.length];
        int i = 0;
        for(String v : split) {
            values[i] = Float.valueOf(v);
            i++;
        }
        return new Matrix4f().set(values);
    }
    private static String ts(Matrix4f matrix4f) {
        return matrix4f.toString(new DecimalFormat());
    }
    private static float calcIdentitarian(Matrix4f m) {
        m = new Matrix4f(m).sub(new Matrix4f().identity());
        float fl;
        fl  = Math.abs(m.m00() + m.m10()) + Math.abs(m.m20() + m.m30());
        fl += Math.abs(m.m01() + m.m11()) + Math.abs(m.m21() + m.m31());
        fl += Math.abs(m.m02() + m.m12()) + Math.abs(m.m22() + m.m32());
        fl += Math.abs(m.m03() + m.m13()) + Math.abs(m.m23() + m.m33());
        return fl;
    }
}