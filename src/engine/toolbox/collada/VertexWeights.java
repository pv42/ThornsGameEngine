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

    float[] getWeightsData(int[] primitive) {
        List<List<Float>> pWeights;
        if(primitive != null) {
            pWeights = new ArrayList<>();
            for (int i = 0; i < primitive.length / 4; i++) {
                Integer pi = primitive[i * 4];
                pWeights.add(weights.get(pi));

            }
        } else {
            pWeights = weights;
        }
        float[] weightsData = new float[pWeights.size() * 4];
        int i = 0;
        for(List<Float> weights_: pWeights) {
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

    int[] getIndicesData(int[] primitive) {
        List<List<Integer>> pIndices;
        if(primitive != null) {
            pIndices = new ArrayList<>();
            for (int i = 0; i < primitive.length / 4; i++) {
                Integer pi = primitive[i * 4];
                pIndices.add(indices.get(pi));

            }
        } else {
            pIndices = indices;
        }

        int[] indicesData = new int[pIndices.size() * 4];
        int i = 0;
        for(List<Integer> indices_: pIndices) {
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
