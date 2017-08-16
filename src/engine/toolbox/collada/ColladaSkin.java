package engine.toolbox.collada;

import engine.graphics.animation.Bone;
import engine.graphics.models.RawModel;
import engine.graphics.models.TexturedModel;
import engine.graphics.renderEngine.Loader;
import engine.graphics.textures.ModelTexture;
import engine.toolbox.Log;
import engine.toolbox.Settings;
import engine.toolbox.Util;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

/***
 * Created by pv42 on 04.08.16.
 */
public class ColladaSkin {
    private Matrix4f bindShapeMatrix;
    private List<Bone> bones;
    private VertexWeights vertexWeights;
    private Vertices vsource;
    public ColladaSkin() {
        bindShapeMatrix = new Matrix4f();
        bindShapeMatrix.identity();
    }

    public Vertices getVsource() {
        return vsource;
    }

    public void setVsource(Vertices vsource) {
        this.vsource = vsource;
    }

    public VertexWeights getVertexWeights() {
        return vertexWeights;
    }

    public void setVertexWeights(VertexWeights vertexWeights) {
        this.vertexWeights = vertexWeights;
    }

    public List<Bone> getBones() {
        return bones;
    }

    public void setBones(List<Bone> bones) {
        this.bones = bones;
        for (Bone bone:bones) {
            bone.setBindShapeMatrix(bindShapeMatrix);
        }
    }

    public void setBindShapeMatrix(Matrix4f bindMatrix) {
        this.bindShapeMatrix = bindMatrix;
    }

    public Matrix4f getBindMatrix() {
        return bindShapeMatrix;
    }

    public TexturedModel getAnimatedTexturedModel() {
        return getAnimatedTexturedModel(null);
    }
    public TexturedModel getAnimatedTexturedModel(Matrix4f transformation) {
        if(vsource.getPosition() == null) Log.w("pnull");
        if(vsource.getNormal() == null) Log.w("nnull");
        if(vsource.getTexCoord() == null) Log.w("vnull");
        if(transformation != null) {
            List<Vector4f> vertices = new ArrayList<>();
            List<Vector4f> normals = new ArrayList<>();
            for(float[] v : vsource.getPosition()) {
                Vector4f vec = new Vector4f(v[0],v[1],v[2],1);
                transformation.transform(vec, vec);
                vertices.add(vec);
            }
            vsource.setPosition(Util.get2DArray(vertices));
            for(float[] n : vsource.getNormal()) {
                Vector4f vec = new Vector4f(n[0],n[1],n[2],1);
                transformation.transform(vec, vec);
                normals.add(vec);
            }
            vsource.setNormal(Util.get2DArray(normals));
        }
        float[] pos = Util.get1DArray(vsource.getPosition());
        float[] uv = Util.get1DArray(vsource.getTexCoord());
        float[] norm = Util.get1DArray(vsource.getNormal());
        int[] boneInicesArray = new int[uv.length * 2];
        float[] boneWeightArray = new float[uv.length * 2];
        fillBoneData(boneInicesArray,boneWeightArray,vertexWeights.getIndices(),vertexWeights.getWeights());
        RawModel model  =  Loader.loadToVAOAnimated(pos, uv, norm, vsource.getIndices(), boneInicesArray, boneWeightArray, bones);
        ModelTexture texture = new ModelTexture(Loader.loadTexture(vsource.getImageFile()));
        return new TexturedModel(model,texture,true);
    }
    private void fillBoneData(int[] bina, float[] bwea, List<List<Integer>> bin,List<List<Float>> bwe) {
        for (int i = 0; i < bin.size(); i++) {
            for(int j = 0; j < Settings.MAX_BONES_PER_VERTEX; j++) {
                if(bin.get(i).size() > j) {
                    bina[Settings.MAX_BONES_PER_VERTEX * i + j] = bin.get(i).get(j);
                    bwea[Settings.MAX_BONES_PER_VERTEX * i + j] = bwe.get(i).get(j);
                } else {
                    bina[Settings.MAX_BONES_PER_VERTEX * i + j] = 0;
                    bwea[Settings.MAX_BONES_PER_VERTEX * i + j] = 0;
                }
            }
        }
    }
}
