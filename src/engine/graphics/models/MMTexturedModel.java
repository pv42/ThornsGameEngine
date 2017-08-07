package engine.graphics.models;

import org.joml.Vector3f;
import engine.graphics.textures.ModelTexture;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pv42 on 03.07.16.
 */
@Deprecated
public class MMTexturedModel {
    private List<RawModel> rawmodels;
    private ModelTexture texture;
    private List<Vector3f> positionOffsets;

    public MMTexturedModel(List<RawModel> rawmodels, ModelTexture texture,List<Vector3f> positionOffsets) {
        this.rawmodels = rawmodels;
        this.texture = texture;
        this.positionOffsets = positionOffsets;
    }
    public MMTexturedModel(List<RawModel> rawmodels, ModelTexture texture) {
        this.rawmodels = rawmodels;
        this.texture = texture;
        positionOffsets = new ArrayList<>();
        for(int i = 0; i < rawmodels.size(); i++) {
            positionOffsets.add(new Vector3f(0f,0f,0f));
        }
    }


    public List<RawModel> getRawModels() {
        return rawmodels;
    }

    public ModelTexture getTexture() {
        return texture;
    }

    public List<Vector3f> getPositionOffsets() {
        return positionOffsets;
    }

    public void setPositionOffset(int index,Vector3f positionOffset) {
        this.positionOffsets.set(index,positionOffset);
    }
}