package engine.toolbox.collada;

import java.util.ArrayList;
import java.util.List;

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

    public List<List<Float>> getWeights() {
        return weights;
    }

    public List<List<Integer>> getIndices() {
        return indices;
    }
}
