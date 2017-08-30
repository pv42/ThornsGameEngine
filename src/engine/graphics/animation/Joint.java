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
    private final Matrix4f inverseBindMatrix; // absolute
    private Matrix4f poseTransformationMatrix; //relative to parent
    private Joint parent;
    private final String id;

    public Joint(String id, Matrix4f inverseBindMatrix) {
        this.id = id;
        this.inverseBindMatrix = inverseBindMatrix;
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

    /**
     * generates the matrix to use in the vertex shader for the actual animation
     * @return generated joint matrix
     */
    public Matrix4f getJointMatrix() {

        Matrix4f matrix = new Matrix4f().identity();
        matrix.mul(getTransformationMatrix());
        matrix.mul(inverseBindMatrix);
        return matrix;
    }


    private Matrix4f getTransformationMatrix() {
        if(poseTransformationMatrix == null) {
            Log.w(TAG, "id:" + id);
            return  new Matrix4f().identity();
        }
        Matrix4f matrix = new Matrix4f(poseTransformationMatrix);
        if (hasParent()) matrix.mul(parent.getTransformationMatrix());
        return matrix;
    }

    public void setPoseTransformationMatrix(Matrix4f poseTransformationMatrix) {
        this.poseTransformationMatrix = poseTransformationMatrix;
    }


    public void setParent(Joint parent) {
        this.parent = parent;
    }
}
