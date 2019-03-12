package engine.graphics.animation;

import engine.toolbox.Log;
import engine.toolbox.Matrix4fDbg;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Created by pv42 on 27.07.16.
 */
/*
 TARGET MATRICES ARE
 float4x4 Torso.procT is (changes)
0.99999994 0.0 0.0 0.0
0.0 0.997907 -0.06466552 3.810999
0.0 0.06466542 0.997907 -1.6658406E-7
0.0 0.0 0.0 1.0

float4x4 Torso.mjd is (static)
0.99999994 0.0 0.0 0.0
0.0 0.997907 -0.06466552 3.210999
0.0 0.06466542 0.997907 -1.4035723E-7
0.0 0.0 0.0 1.0
 */
public class Joint {
    private static final Matrix4f CORRECTION = new Matrix4f().rotate((float) Math.toRadians(-90), new Vector3f(1, 0, 0));

    private static final String TAG = "Joint";
    private final Matrix4fDbg inverseBindMatrix;
    private Matrix4fDbg poseTransformationMatrix;
    private Matrix4fDbg animationTransformationMatrix = new Matrix4fDbg("I");
    private Joint parent;
    private final String id;

    public Joint(String id, Matrix4f inverseBindMatrix) {
        this.id = id;
        this.inverseBindMatrix = new Matrix4fDbg(inverseBindMatrix,id);
    }

    public Joint getParent() {
        return parent;
    }

    public String getId() {
        return id;
    }

    private boolean hasParent() {
        return parent != null;
    }

    public Matrix4fDbg getTransformationMatrix() {
        if(poseTransformationMatrix == null) {
            Log.w(TAG, "id:" + id);
            Matrix4fDbg matrix4fDbg =  new Matrix4fDbg("I");
            matrix4fDbg.identity();
            return matrix4fDbg;
        }
        //Matrix4fDbg mat1 = poseTransformationMatrix;
        //Matrix4fDbg mat0 = animationTransformationMatrix;
        Matrix4fDbg matrix;
        matrix = getMat0();
        matrix.mul(getMat1().invert());
        //System.out.println(getMat0().getName()+ "|" + getMat1().getName());

        return matrix;
    }

    private Matrix4fDbg getMat0() {

        if (hasParent()) {
            return new Matrix4fDbg(parent.getMat0()).mul(animationTransformationMatrix);
        } else {
            return new Matrix4fDbg(animationTransformationMatrix);
        }
    }

    private Matrix4fDbg getMat1() {

        if (hasParent()) {
            return new Matrix4fDbg(parent.getMat1()).mul(poseTransformationMatrix);
        } else {
            return new Matrix4fDbg(poseTransformationMatrix);
        }
    }

    public void setPoseTransformationMatrix(Matrix4f poseTransformationMatrix) {
        this.poseTransformationMatrix = new Matrix4fDbg(CORRECTION.mul(poseTransformationMatrix.transpose(new Matrix4f()), new Matrix4f()), id + ".pTM");
        //this.poseTransformationMatrix.debugPrint();
    }

    
    public void setAnimationTransformationMatrix(Matrix4f animationTransformationMatrix) {
        this.animationTransformationMatrix = new Matrix4fDbg(CORRECTION.mul(animationTransformationMatrix, new Matrix4f()), id + ".aTM");
        this.animationTransformationMatrix.debugPrint();
    }

    public void setParent(Joint parent) {
        this.parent = parent;
    }

}
