package engine.graphics.animation;

import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/***
 * Created by pv42 on 27.07.16.
 */
public class Joint {

    private Matrix4f localInverseBindMatrix;
    private Matrix4f inverseBindMatrix;
    private final Matrix4f localTransformationMatrix; // current state
    private Matrix4f transformationMatrix;
    private Joint parent;
    private String name;
    private List<Joint> children = new ArrayList<>();

    public Joint(String name, Matrix4f localTransformationMatrix, Joint parent) {
        this.parent = parent;
        this.name = name;
        this.localTransformationMatrix = localTransformationMatrix;
        if(hasParent()) parent.addChild(this);
    }

    public Matrix4f getInverseBindMatrix() {

        return inverseBindMatrix;
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
        this.localInverseBindMatrix = new Matrix4f(inverseBindMatrix);
    }

    public Matrix4f getJointMatrix() {
        if (transformationMatrix == null) {
            if (!hasParent()) {
                calcualte(new Matrix4f().identity(), new Matrix4f().identity());
            } else {
                parent.getJointMatrix();
            }
        }
        Matrix4f matrix = new Matrix4f(localInverseBindMatrix);
        matrix.mul(transformationMatrix);
        //matrix.mul(bindMatrix);
       //System.out.println("IBM:" + inverseBindMatrix + "TM:" + localTransformationMatrix + "BM:" + bindMatrix);
        return matrix;
    }


    public void addChild(Joint joint) {
        children.add(joint);
    }

    public List<Joint> getChildren() {
        return children;
    }

    private void calcualte(Matrix4f parentBindTransform, Matrix4f parentTransf) {
        //inverseBindMatrix = new Matrix4f();
        //inverseBindMatrix.mul(parentBindTransform).mul(localInverseBindMatrix);
        transformationMatrix = new Matrix4f();
        transformationMatrix.mul(localTransformationMatrix).mul(parentTransf);
        //bindMatrix = new Matrix4f().invert(bindTransform);
        for (Joint child : children) {
            child.calcualte(inverseBindMatrix,transformationMatrix);
        }
    }

}
