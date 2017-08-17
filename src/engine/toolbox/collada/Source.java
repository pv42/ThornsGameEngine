package engine.toolbox.collada;


import engine.toolbox.Log;
import org.joml.Matrix4f;

/**
 * Created by pv42 on 04.08.16.
 */
public class Source {
    public static final int UNKNOWN = -1;
    public static final int STRING = 13;
    public static final int FLOAT = 14;
    public static final int MATRIX = 15;
    private String[][] data;
    private int datatype;
    public Source(String[][] data,String datatype) {
        this.data = data;
        this.datatype = getDataType(datatype);
    }
    private static int getDataType(String datatype) {
        if(datatype.toLowerCase().equals("name")) return STRING;
        if(datatype.equals("float")) return FLOAT;
        if(datatype.equals("float4x4")) return  MATRIX;
        Log.w("datatype " + datatype + " unknown");
        return UNKNOWN;
    }
    public String[][] getStringData() {
        return data;
    }
    public float[][] getFloatData() throws NumberFormatException{
        if(datatype == STRING) Log.w("try reading StringData to float");
        float[][] floats = new float[data.length][data[0].length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                floats[i][j] = Float.valueOf(data[i][j]);
            }
        }
        return floats;
    }
    public Matrix4f[] getMatrix4Data() throws NumberFormatException {
        if(datatype != MATRIX) Log.w("try reading none-Matrix to Matrix");
        if(data[0].length != 16) throw new NumberFormatException("stride must be 16 to read 4x4-matrices");
        Matrix4f[] matrices = new Matrix4f[data.length];
        float[][] floatData = getFloatData();
        for (int i = 0; i < data.length; i++) {
            matrices[i] = new Matrix4f();
            matrices[i].m00(floatData[i][0]);
            matrices[i].m01(floatData[i][1]);
            matrices[i].m02(floatData[i][2]);
            matrices[i].m03(floatData[i][3]);
            matrices[i].m10(floatData[i][4]);
            matrices[i].m11(floatData[i][5]);
            matrices[i].m12(floatData[i][6]);
            matrices[i].m13(floatData[i][7]);
            matrices[i].m20(floatData[i][8]);
            matrices[i].m21(floatData[i][9]);
            matrices[i].m22(floatData[i][10]);
            matrices[i].m23(floatData[i][11]);
            matrices[i].m30(floatData[i][12]);
            matrices[i].m31(floatData[i][13]);
            matrices[i].m32(floatData[i][14]);
            matrices[i].m33(floatData[i][15]);
        }
        return matrices;
    }
}
