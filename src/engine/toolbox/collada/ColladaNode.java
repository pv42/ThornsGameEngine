package engine.toolbox.collada;

import org.joml.Matrix4f;

import java.util.List;

class ColladaNode {
    private List<ColladaNode> children;
    private Matrix4f transformation;
    private boolean isJoint;
    private String id;
    private String name;
    private String sid;
    private ColladaVisualScene.ColladaInstanceController instanceController;

    public ColladaNode(String id, List<ColladaNode> children, Matrix4f transformation, boolean isJoint) {
        this.children = children;
        this.transformation = transformation;
        this.isJoint = isJoint;
        this.id = id;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    ColladaVisualScene.ColladaInstanceController getInstanceController() {
        return instanceController;
    }

    void setInstanceController(ColladaVisualScene.ColladaInstanceController instanceController) {
        this.instanceController = instanceController;
    }

    public Matrix4f getTransformation() {
        return transformation;
    }

    public List<ColladaNode> getChildren() {
        return children;
    }

    public String getSid() {
        return sid;
    }
}
