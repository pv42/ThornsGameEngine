package engine.graphics.models;

import engine.graphics.animation.Bone;

import java.util.List;

/***
 * Created by pv42 on 16.06.16.
 */
public class RawModel {
    private int vaoID;
    private int vertexCount;
    private List<Bone> bones;

    public RawModel(int vaoID, int vertexCount) {
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
        this.bones = null;
    }
    public RawModel(int vaoID, int vertexCount, List<Bone> bones) {
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
        this.bones = bones;
    }

    public int getVaoID() {
        return vaoID;
    }

    public int getVertexCount() {
        return vertexCount;
    }
    public List<Bone> getBones() {
        return bones;
    }
}
