package engine.toolbox;

import org.joml.Matrix3x2fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Matrix4x3fc;

import java.util.ArrayList;
import java.util.List;

public class Matrix4fDebug extends Matrix4f {
    private List<Matrix4f> mulChain;
    private String name;
    public Matrix4fDebug(Matrix4f matrix, String name) {
        super(matrix);
        this.name = name;
        mulChain = new ArrayList<>();
        mulChain.add(this);
    }
    public Matrix4fDebug mul(Matrix4fDebug right) {
        return mul(right, this);
    }

    public Matrix4fDebug mul(Matrix4fDebug right, Matrix4fDebug dest) {
        Matrix4f dest4f = new Matrix4f();
        super.mul(right, dest4f);
        dest = new Matrix4fDebug(dest4f,this.name + "x" + right.name);
        dest.mulChain.clear();
        dest.mulChain.add(this);
        dest.mulChain.add(right);
        return dest;
    }

}
