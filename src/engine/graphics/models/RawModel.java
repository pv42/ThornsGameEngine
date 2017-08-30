package engine.graphics.models;

import engine.graphics.animation.Joint;

import java.util.List;

/***
 * Created by pv42 on 16.06.16.
 */
public class RawModel {
    private int vaoID;
    private int vertexCount;
    private List<Joint> joints;

    public RawModel(int vaoID, int vertexCount) {
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
        this.joints = null;
    }
    public RawModel(int vaoID, int vertexCount, List<Joint> joints) {
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
        this.joints = joints;
    }

    public int getVaoID() {
        return vaoID;
    }

    public int getVertexCount() {
        return vertexCount;
    }
    public List<Joint> getJoints() {
        return joints;
    }
}
