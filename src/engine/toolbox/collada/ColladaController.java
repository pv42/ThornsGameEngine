package engine.toolbox.collada;

import engine.graphics.animation.Joint;
import engine.toolbox.Log;
import engine.toolbox.Util;
import org.joml.Matrix4f;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static engine.toolbox.collada.ColladaUtil.getAttribValue;
import static engine.toolbox.collada.ColladaUtil.getListFromNodeList;

public class ColladaController {
    private static final String TAG = "Collada:Controller";
    private Matrix4f bindShapeMatrix;
    private Map<String, Joint> bindPoses;
    private VertexWeights weights; //todo

    public static ColladaController fromNode(Node node) {
        if(!node.getNodeName().equals("controller")) throw new IllegalArgumentException("Node given must be a controller node");
        Node controlElement = null;
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("skin")) {
                controlElement = n;
            } else if (!(n.getNodeName().equals("#text") || n.getNodeName().equals("extra"))) { // todo extra
                Log.w(TAG, "unkn_c:" + n.getNodeName());
            }
        }
        ColladaController controller = readSkin(controlElement);
        return controller;
    }

    private static ColladaController readSkin(Node node) {
        //skinsToHandle.add(node);
        Map<String, Node> sources = new HashMap<>();
        Node bindShapeMatrixNode = null;
        Node jointsNode = null;
        Node vertexWeightNode = null;
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("bind_shape_matrix")) {
                bindShapeMatrixNode = n;
            } else if (n.getNodeName().equals("source")) {
                sources.put(getAttribValue(n,"id"), n);
            } else if (n.getNodeName().equals("joints")) {
                jointsNode = n;
            } else if (n.getNodeName().equals("vertex_weights")) {
                vertexWeightNode = n;
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_sk:" + n.getNodeName());
            }
        }
        ColladaController controller = new ColladaController();
        if(bindShapeMatrixNode != null) {
            controller.setBindShapeMatrix(ColladaUtil.readMatrix4f(bindShapeMatrixNode));
        } else {
            controller.setBindShapeMatrix(new Matrix4f().identity());
        }
        controller.setBindPoses(readJoints(jointsNode, sources));
        controller.setWeights(readVertexWeights(vertexWeightNode, sources));
        return controller;
    }

    private static Map<String, Joint> readJoints(Node node, Map<String, Node> sources) {
        List<Matrix4f> matrices = null;
        List<String> jointIds = null;
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("input")) {
                if (getAttribValue(n,"semantic").equals("JOINT")) {
                    jointIds = Util.getList(ColladaUtil.readSource(sources.get(getAttribValue(n,"source").replaceFirst("#",""))).getStringData());
                } else if (n.getAttributes().getNamedItem("semantic").getNodeValue().equals("INV_BIND_MATRIX")) {
                    matrices = Util.getList(ColladaUtil.readSource(sources.get(getAttribValue(n,"source").replaceFirst("#",""))).getMatrix4Data());
                } else {
                    Log.w("unkn_s: input::semantic" + n.getAttributes().getNamedItem("semantic"));
                }
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_J:" + n.getNodeName());
            }
        }
        if (matrices == null || jointIds == null || jointIds.size() != matrices.size()) Log.e(TAG, "wrong joint data");
        Map<String,Joint> joints = new HashMap<>();
        for (int i = 0; i < matrices.size(); i++) {
            Joint joint = new Joint(jointIds.get(i),matrices.get(i), null);
            joints.put(joint.getId(), joint);
        }
        return joints;

    }

    private static VertexWeights readVertexWeights(Node node, Map<String, Node> sources) {
        List<Float> weights = null;
        List<Integer> vcount = null;
        List<Integer> v = null;
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("input")) {
                if (n.getAttributes().getNamedItem("semantic").getNodeValue().equals("JOINT")) {
                    //todo worth it?
                } else if (n.getAttributes().getNamedItem("semantic").getNodeValue().equals("WEIGHT")) {
                    weights = Util.getList(ColladaUtil.readSource(sources.get(getAttribValue(n,"source").replaceFirst("#",""))).getFloatData());
                    //todo weights = Util.getList(ColladaUtil.readSource(getIdElement(n.getAttributes().getNamedItem("source").getNodeValue())).getFloatData());
                } else {
                    Log.w("unkn_se: input::semantic" + n.getAttributes().getNamedItem("semantic"));
                }
            } else if (n.getNodeName().equals("vcount")) {
                vcount = Util.getList(ColladaUtil.readIntArray(n));
            } else if (n.getNodeName().equals("v")) {
                v = Util.getList(ColladaUtil.readIntArray(n));
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_vw:" + n.getNodeName());
            }
        }
        if (weights == null || v == null || vcount == null) Log.e("missing vertex_bones data");
        List<List<Float>> finalWeights = new ArrayList<>();
        List<List<Integer>> finalIndices = new ArrayList<>();
        int k = 0;
        for (int i = 0; i < vcount.size(); i++) {
            List<Float> currentWeights = new ArrayList<>();
            List<Integer> currentIndices = new ArrayList<>();
            for (int j = 0; j < vcount.get(i); j++) {
                currentIndices.add(v.get(k));
                currentWeights.add(weights.get(v.get(k + 1)));
                k += 2;
            }
            finalWeights.add(currentWeights);
            finalIndices.add(currentIndices);
        }
        //Log.d(TAG,finalWeights.toString();
        //Log.d(TAG,finalIndices.toString());)
        return new VertexWeights(finalWeights, finalIndices);
    }

    private void setBindShapeMatrix(Matrix4f bindShapeMatrix) {
        this.bindShapeMatrix = bindShapeMatrix;
    }

    private void setBindPoses(Map<String, Joint> bindPoses) {
        this.bindPoses = bindPoses;
    }

    private void setWeights(VertexWeights weights) {
        this.weights = weights;
    }
}
