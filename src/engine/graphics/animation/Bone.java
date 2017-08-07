package engine.graphics.animation;

import engine.graphics.lines.Line;
import engine.graphics.lines.LineModel;
import engine.graphics.renderEngine.Loader;
import engine.toolbox.Color;
import engine.toolbox.Maths;
import engine.toolbox.Settings;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/***
 * Created by pv42 on 27.07.16.
 */
public class Bone {
    private Matrix4f jointMatrix;
    private Matrix4f inverseJointMatrix;
    private Matrix4f inverseBindMatrix;
    private Matrix4f transfomationMatrix;
    private Matrix4f bindShapeMatrix = null;
    private Bone parent = null;
    private String name;
    private LineModel line;

    public Bone(Bone parent) {
        jointMatrix = new Matrix4f();
        jointMatrix.identity();
        inverseJointMatrix = new Matrix4f();
        inverseJointMatrix.identity();
        inverseBindMatrix = new Matrix4f();
        inverseBindMatrix.identity();
        bindShapeMatrix = new Matrix4f();
        bindShapeMatrix.identity();
        transfomationMatrix = new Matrix4f();
        transfomationMatrix.identity();
        this.parent = parent;
        if (Settings.SHOW_SKELETON_BONES) {
            line = Loader.loadToVAO(new Line(new Vector3f(), new Vector3f(1, 0, 0)));
            line.setColor(Color.fromHSL(15 * getNumOfParents(), 1, .5f));
        }
    }

    public void setBindShapeMatrix(Matrix4f bindShapeMatrix) {
        this.bindShapeMatrix = bindShapeMatrix;
    }

    public void setInverseBindMatrix(Matrix4f inverseBindMatrix) {
        this.inverseBindMatrix = inverseBindMatrix;
    }

    public void setJointMatrix(Matrix4f jointMatrix) {
        this.jointMatrix = jointMatrix;
        jointMatrix.invert(inverseJointMatrix);

    }

    public void rotateJoint(Vector3f axis, float angle) {
        Vector3f v = Maths.getPositionComponent(jointMatrix);
        Vector3f mv = new Vector3f();
        v.x = -mv.x;
        v.y = -mv.y;
        v.z = -mv.z;
        //Matrix4f.translate(mv,jointMatrix,jointMatrix);
        jointMatrix.rotate((float) Math.toRadians(angle), axis);
        //Matrix4f.translate(v,jointMatrix,jointMatrix);
        Maths.getPositionComponent(jointMatrix);
    }

    public void scaleJoint(Vector3f scale) {
        //todo Matrix4f.scale(scale,jointMatrix,jointMatrix);
    }

    public void translateJoint(Vector3f translation) {
        jointMatrix.translate(translation);

    }

    public void rotate(Vector3f axis, float angle) {
        Vector3f v = Maths.getPositionComponent(jointMatrix);
        Vector3f mv = new Vector3f();
        v.x = -mv.x;
        v.y = -mv.y;
        v.z = -mv.z;
        //Matrix4f.translate(mv,jointMatrix,jointMatrix);
        transfomationMatrix.rotate((float) Math.toRadians(angle), axis);
        //Matrix4f.translate(v,jointMatrix,jointMatrix);
    }

    public void scale(Vector3f scale) {
        //todo Matrix4f.scale(scale,transfomationMatrix,transfomationMatrix);
    }

    public void scale(float scale) {
        //todo Matrix4f.scale(new Vector3f(scale,scale,scale),transfomationMatrix,transfomationMatrix);
    }

    public void translate(Vector3f translation) {
        transfomationMatrix.translate(translation);
    }


    public Matrix4f getTransformationMatrix() {
        //todo Matrix4f transformation = Matrix4f.mul(getTransfomation(), getWorldMatrix(), null);
        /*if(Settings.SHOW_SKELETON_BONES) line.setTransformation(transformation );
        return Maths.mulMatrices(new Matrix4f[]{
                bindShapeMatrix,
                inverseBindMatrix,
                transformation
        });*/
        return null;
    }

    public Matrix4f getWorldMatrix() {
        if (parent == null) return jointMatrix;
        //todo !! mulMatrices
        return Maths.mulMatrices(new Matrix4f[]{parent.getWorldMatrix(), jointMatrix});
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameTree() {
        if (parent == null) return name;
        return parent.getNameTree() + "->" + name;
    }

    public Bone getParent() {
        return parent;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public int getNumOfParents() {
        if (parent == null) return 0;
        return parent.getNumOfParents() + 1;
    }

    public Matrix4f getTransfomation() {
        if (parent == null) return transfomationMatrix;
        Matrix4f m = new Matrix4f();
        parent.getTransformationMatrix().mul(m, transfomationMatrix);
        return m;
    }

    public LineModel getLine() {
        return line;
    }
}
