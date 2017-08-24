package engine.graphics.animation;

import engine.toolbox.Log;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by pv42 on 27.07.16.
 */
public class Joint {

    private static final String TAG = "Joint";
    private Matrix4f inverseBindMatrix; // absolute
    private Matrix4f poseTransformationMatrix; //relative to parent
    private Joint parent;
    private String id;

    public Joint(String id, Matrix4f poseTransformationMatrix, Joint parent) {
        this.parent = parent;
        this.id = id;
        this.poseTransformationMatrix = poseTransformationMatrix;
    }

    public Joint getParent() {
        return parent;
    }

    public String getId() {
        return id;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public void setInverseBindMatrix(Matrix4f inverseBindMatrix) {
        this.inverseBindMatrix = new Matrix4f(inverseBindMatrix);
    }

    public Matrix4f getJointMatrix() {

        Matrix4f matrix = new Matrix4f().identity();
        matrix.mul(getTransformationMatrix());
        matrix.mul(inverseBindMatrix);
        return matrix;
    }


    private Matrix4f getTransformationMatrix() {
        Matrix4f matrix = new Matrix4f(poseTransformationMatrix);
        if (hasParent()) matrix.mul(parent.getTransformationMatrix());
        return matrix;
    }

    public void setPoseTransformationMatrix(Matrix4f poseTransformationMatrix) {
        this.poseTransformationMatrix = poseTransformationMatrix;
    }

    /**
     * applies a animation transformationMatrix
     * @param matrix matrix to apply
     */
    public void applyAnimation(Matrix4f matrix) {
        //todo
        Log.w(TAG,"todo");
    }
}
