package engine.graphics.models;

import engine.graphics.animation.Joint;

import java.util.List;

/***
 * Created by pv42 on 26.06.16.
 */

public class AnimatedRawModel {
    private List<Joint> joints;
    private int vaoID;
    private int vertexCount;
    public AnimatedRawModel(int vaoID, int vertexCount, List<Joint> joints) {
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