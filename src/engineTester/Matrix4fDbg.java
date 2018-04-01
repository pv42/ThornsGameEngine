package engineTester;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import java.text.DecimalFormat;

public class Matrix4fDbg extends Matrix4f {
    private String name;

    public Matrix4fDbg(String name) {
        super();
        this.name = name;
    }

    public Matrix4fDbg(Matrix4fc mat, String name) {
        super(mat);
        this.name = name;
    }

    public Matrix4fDbg(Matrix4fDbg matrix) {
        this(matrix, matrix.name);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Matrix4fDbg mul(Matrix4fDbg right) {
        this.name = this.name + " * " + right.name;
        mul(right, this);
        return this;
    }

    public Matrix4fDbg transpose() {
        transpose(this);
        name = "(" + name + "^T)";
        return this;
    }

    public Matrix4fDbg invert() {
        invert(this);
        name = "((" + name + ")^-1)";
        return this;
    }

    public void debugPrint() {
        System.out.println("float4x4 " + name + " is");
        System.out.println(toString(new DecimalFormat("0.0000")));
    }
}