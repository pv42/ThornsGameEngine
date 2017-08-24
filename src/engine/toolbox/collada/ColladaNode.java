package engine.toolbox.collada;

import org.joml.Matrix4f;

import java.util.List;

class ColladaNode {
    private List<ColladaNode> children;
    private Matrix4f translation;
    private boolean isJoint;
    private String id;
    private String name;
    private String sid;
    private ColladaVisualScene.ColladaInstanceController instanceController;

    public ColladaNode(String id, List<ColladaNode> children, Matrix4f translation, boolean isJoint) {
        this.children = children;
        this.translation = translation;
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

    public Matrix4f getTranslation() {
        return translation;
    }

    public List<ColladaNode> getChildren() {
        return children;
    }
}
