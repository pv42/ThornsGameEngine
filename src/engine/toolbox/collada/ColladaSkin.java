package engine.toolbox.collada;

import engine.graphics.animation.Joint;
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

import static org.joml.Matrix4fc.PROPERTY_IDENTITY;

/***
 * Created by pv42 on 04.08.16.
 */
public class ColladaSkin {
    private static final String TAG = "ColladaSkin";
    private List<Joint> joints;
    private VertexWeights vertexWeights;
    private Vertices vsource;
    private Matrix4f bindShapeMatrix;
    public ColladaSkin() {

    }


    public void setVsource(Vertices vsource) {
        this.vsource = vsource;
    }

    public void setVertexWeights(VertexWeights vertexWeights) {
        this.vertexWeights = vertexWeights;
    }

    public void setJoints(List<Joint> joints) {
        this.joints = joints;
    }

    public TexturedModel getAnimatedTexturedModel() {
        return getAnimatedTexturedModel(null);
    }
    public TexturedModel getAnimatedTexturedModel(Matrix4f transformation) {
        if(vsource.getPosition() == null) Log.e(TAG,"pnull");
        if(vsource.getNormal() == null) Log.e(TAG,"nnull");
        if(vsource.getTexCoord() == null) Log.e(TAG,"vnull");
        if(transformation != null || ((bindShapeMatrix.properties() & PROPERTY_IDENTITY) != 0) ) {
            List<Vector4f> vertices = new ArrayList<>();
            List<Vector4f> normals = new ArrayList<>();
            for(float[] v : vsource.getPosition()) {
                Vector4f vec = new Vector4f(v[0],v[1],v[2],1);
                if(transformation != null) transformation.transform(vec, vec);
                if((bindShapeMatrix.properties() & PROPERTY_IDENTITY) != 0) bindShapeMatrix.transform(vec,vec);
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
        int[] boneIndicesArray = new int[uv.length * 2];
        float[] boneWeightArray = new float[uv.length * 2];
        fillBoneData(boneIndicesArray,boneWeightArray,vertexWeights.getIndices(),vertexWeights.getWeights());
        RawModel model  =  Loader.loadToVAOAnimated(pos, uv, norm, vsource.getIndices(), boneIndicesArray, boneWeightArray, joints);
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

    public Matrix4f getBindShapeMatrix() {
        return bindShapeMatrix;
    }

    public void setBindShapeMatrix(Matrix4f bindShapeMatrix) {
        this.bindShapeMatrix = bindShapeMatrix;
    }
}
