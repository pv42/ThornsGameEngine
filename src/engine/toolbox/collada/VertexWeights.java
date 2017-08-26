package engine.toolbox.collada;


import java.util.ArrayList;
import java.util.List;

import static engine.toolbox.Settings.MAX_BONES_PER_VERTEX;

/**
 * Created by pv42 on 04.08.16.
 */
public class VertexWeights {
    private List<List<Float>> weights = new ArrayList<>();
    private List<List<Integer>> indices = new ArrayList<>();

    public VertexWeights(List<List<Float>> weights, List<List<Integer>> indices) {
        this.weights = weights;
        this.indices = indices;
    }

    public void applyIndicesChange(int[] i1, int[] i2) { //for polylist
        //...
    }

    float[] getWeightsData() {
        float[] weightsData = new float[weights.size() * 4];
        int i = 0;
        for(List<Float> weights_: weights) {
            for(int j = 0; j < MAX_BONES_PER_VERTEX; j++) {
                float weight;
                if(j < weights_.size()) {
                    weight = weights_.get(j);
                } else {
                    weight = 0;
                }
                weightsData[MAX_BONES_PER_VERTEX * i + j] = weight;
            }
            i ++;
        }
        return weightsData;
    }

    int[] getIndicesData() {
        int[] indicesData = new int[indices.size() * 4];
        int i = 0;
        for(List<Integer> indices_: indices) {
            for(int j = 0; j < MAX_BONES_PER_VERTEX; j++) {
                int index;
                if(j < indices_.size()) {
                    index = indices_.get(j);
                } else {
                    index = 0;
                }
                indicesData[MAX_BONES_PER_VERTEX * i + j] = index;
            }
            i++;
        }
        return indicesData;
    }
}
