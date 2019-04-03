package engine.graphics.animation;

import engine.toolbox.Matrix4fDbg;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pv42 on 27.07.16.
 *
 * @author pv42
 */
public class Joint {
    private static final Matrix4f CORRECTION = new Matrix4f().rotate((float) Math.toRadians(-90), new Vector3f(1, 0, 0)); // blender up axis

    private static final String TAG = "Joint";
    private final String id;
    // only use in example
    public int numId;
    private Matrix4f inverseBindMatrix;
    private Matrix4f animationTransformationMatrix = new Matrix4f();
    private Joint parent;

    public Joint(int id, String nameId, Matrix4f bindTransform) {
        this.id = nameId;
        this.numId = id;
        inverseBindMatrix = bindTransform;
    }

    public Joint(String id, Matrix4f inverseBindMatrix) {
        this.id = id;
        this.inverseBindMatrix = inverseBindMatrix;
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

    public Matrix4f getTransformationMatrix() {
        Matrix4f m0 = new Matrix4f(getAbsoluteAnimationTransformMatrix());
        Matrix4f m1 = new Matrix4f(inverseBindMatrix);
        return new Matrix4f(new Matrix4f(m0).mul(new Matrix4f(m1).invert()));
    }


    private Matrix4f getAbsoluteAnimationTransformMatrix() {
        if (hasParent()) {
            return new Matrix4f(parent.getAbsoluteAnimationTransformMatrix()).mul(animationTransformationMatrix);
        } else {
            return new Matrix4f(animationTransformationMatrix);
        }
    }

    public void setAnimationTransformationMatrix(Matrix4f animationTransformationMatrix) {
        this.animationTransformationMatrix = new Matrix4f(animationTransformationMatrix);
    }
}
