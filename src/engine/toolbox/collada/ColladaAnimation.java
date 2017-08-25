package engine.toolbox.collada;

import engine.toolbox.Log;
import engine.toolbox.Util;
import org.joml.Matrix4f;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

import static engine.toolbox.collada.ColladaUtil.getAttribValue;
import static engine.toolbox.collada.ColladaUtil.getListFromNodeList;
import static engine.toolbox.collada.ColladaUtil.readSource;

public class ColladaAnimation extends ColladaPrimaryElement {
    private static final String TAG = "Collada:animation";
    private String target;
    private float[] keyFrameTimes;
    private Matrix4f[] keyFrameMatrices;

    public ColladaAnimation(String id, String target, float[] keyFrameTimes, Matrix4f[] keyFrameMatrices) {
        super();
        super.setId(id);
        this.target = target;
        this.keyFrameTimes = keyFrameTimes;
        this.keyFrameMatrices = keyFrameMatrices;
    }

    public String getTarget() {
        return target;
    }

    public float[] getKeyFrameTimes() {
        return keyFrameTimes;
    }

    public Matrix4f[] getKeyFrameMatrices() {
        return keyFrameMatrices;
    }

    public static ColladaAnimation fromNode(Node node) {
        String id = getAttribValue(node, "id");
        Map<String ,Node> sources = new HashMap<>();
        Map<String, Node> samplers = new HashMap<>();
        Node channel = null;
        for(Node n: getListFromNodeList(node.getChildNodes())) {
            if(n.getNodeName().equals("source")) {
                sources.put(getAttribValue(n,"id"),n);
            } else if(n.getNodeName().equals("sampler")) {
                samplers.put(getAttribValue(n,"id"),n);
            } else if(n.getNodeName().equals("channel")) {
                channel = n;
            } else if(!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_ani " + n.getNodeName());
            }
        }
        Node sampler = samplers.get(getAttribValue(channel,"source").replaceFirst("#",""));
        String target = getAttribValue(channel, "target").split("/")[0];
        String input = null;
        String output = null;
        String interpolation = null;
        for(Node n: getListFromNodeList(sampler.getChildNodes())) {
            if(n.getNodeName().equals("input")) {
                String semantic = getAttribValue(n,"semantic");
                String source = getAttribValue(n,"source").replaceFirst("#","");
                switch (semantic) {
                    case "INPUT": input = source;
                        break;
                    case "OUTPUT": output = source;
                        break;
                    case "INTERPOLATION": interpolation = source;
                        break;
                    default: Log.w(TAG, "unkn_smp_i:" + semantic);
                }
            } else if(!n.getNodeName().equals("text")) {
                Log.w(TAG, "unkn_smp" + n.getNodeName());
            }
        }
        Matrix4f[] poses = readSource(sources.get(output)).getMatrix4Data();
        float[] times = Util.get1DArray(readSource(sources.get(input)).getFloatData());
        return new ColladaAnimation(id, target, times, poses);
    }
}
