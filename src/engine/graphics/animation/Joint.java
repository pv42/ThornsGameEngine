package engine.graphics.animation;

import engine.toolbox.Log;
import engine.toolbox.Matrix4fDbg;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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
    private static final Matrix4fDbg CORRECTION = new Matrix4fDbg(new Matrix4f().rotate((float) Math.toRadians(-90), new Vector3f(1, 0, 0)),"COR"); // blender up axis

    private static final String TAG = "Joint";
    private final Matrix4fDbg relativeInverseBindMatrix;
    private /*final*/ Matrix4fDbg relativePoseTransformationMatrix;
    private Matrix4fDbg absoluteInverseBindMatrix;
    private final String id;
    private Matrix4fDbg animationTransformationMatrix = new Matrix4fDbg("I");
    private Joint parent;

    // only use in example
    public int numId;
    public List<Joint> children = new ArrayList<>();
    public Joint(int id, String nameId, Matrix4fDbg bindTransform) {
        this.id = nameId;
        this.numId = id;
        relativeInverseBindMatrix = bindTransform;
    }
    public void addChild(Joint joint) {
        joint.setParent(this);
        children.add(joint);
    }
    //exampe use end


    public Joint(String id, Matrix4fDbg inverseBindMatrix) {
        this.id = id;
        this.relativeInverseBindMatrix = inverseBindMatrix;
        System.out.println("IBM " + id + "\n" + inverseBindMatrix.toString(new DecimalFormat()));
    }

    public Joint getParent() {
        return parent;
    }

    public void setParent(Joint parent) {
        this.parent = parent;
    }

    public String getId() {
        return id;
    }

    private boolean hasParent() {
        return parent != null;
    }

    public Matrix4fDbg getTransformationMatrix() {
        Matrix4fDbg m0 = new Matrix4fDbg(getAbsoluteAnimationTransformMatrix());
        Matrix4fDbg m1 = getAbsoluteInverseBindMatrix();
        return new Matrix4fDbg(new Matrix4fDbg(m0).mul(new Matrix4fDbg(m1).invert()));
    }

    private Matrix4fDbg getAbsoluteInverseBindMatrix() {
        if (absoluteInverseBindMatrix == null) {
            if (hasParent()) {
                absoluteInverseBindMatrix = new Matrix4fDbg(parent.getAbsoluteInverseBindMatrix()).mul(relativeInverseBindMatrix);
            } else {
                absoluteInverseBindMatrix = new Matrix4fDbg(relativeInverseBindMatrix);
            }
        }
        return absoluteInverseBindMatrix;
    }

    private Matrix4fDbg getAbsoluteAnimationTransformMatrix() {
        if (hasParent()) {
            return new Matrix4fDbg(parent.getAbsoluteAnimationTransformMatrix()).mul(animationTransformationMatrix);
        } else {
            return new Matrix4fDbg(animationTransformationMatrix);
        }
    }

    public void setPoseTransformationMatrix(Matrix4f poseTransformationMatrix) {
        this.relativePoseTransformationMatrix = new Matrix4fDbg(poseTransformationMatrix, id + ".pTM");
        this.relativePoseTransformationMatrix.debugPrint();
    }

    public void setAnimationTransformationMatrix(Matrix4fDbg animationTransformationMatrix) {
        this.animationTransformationMatrix = new Matrix4fDbg(animationTransformationMatrix);
    }
}
