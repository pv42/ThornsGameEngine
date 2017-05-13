package engine.graphics.models;

import engine.graphics.animation.Bone;

import java.util.List;

/***
 * Created by pv42 on 26.06.16.
 */

public class AnimatedRawModel {
    private List<Bone> bones;
    private int vaoID;
    private int vertexCount;
    public AnimatedRawModel(int vaoID, int vertexCount, List<Bone> bones) {
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