package engine.graphics.animation;

import engine.toolbox.Log;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pv42 on 27.07.16.
 */
public class Joint {

    private static final String TAG = "Joint";
    private Matrix4f inverseBindMatrix;
    private final Matrix4f localTransformationMatrix;
    private Joint parent;
    private String name;
    private List<Joint> children = new ArrayList<>();

    public Joint(String name, Matrix4f localTransformationMatrix, Joint parent) {
        this.parent = parent;
        this.name = name;
        this.localTransformationMatrix = localTransformationMatrix;
        if (hasParent()) parent.addChild(this);
    }

    public Joint getParent() {
        return parent;
    }

    public String getName() {
        return name;
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

    private void addChild(Joint joint) {
        children.add(joint);
    }

    public List<Joint> getChildren() {
        return children;
    }

    private Matrix4f getTransformationMatrix() {
        Matrix4f matrix = new Matrix4f(localTransformationMatrix);
        if (hasParent()) matrix.mul(parent.getTransformationMatrix());
        return matrix;
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
