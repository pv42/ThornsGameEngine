package engine.toolbox.collada;

import engine.toolbox.Log;
import org.joml.Matrix4f;
import org.w3c.dom.Node;

import java.util.*;

import static engine.toolbox.collada.ColladaUtil.*;

public class ColladaVisualScene extends ColladaPrimaryElement{
    private static final String TAG = "Collada:VisualScene";
    private Map<String, ColladaNode> nodes = new HashMap<>();
    private List<String> rootNodes = new ArrayList<>();

    /**
     * loads a collada visual scene from a collada node
     *
     * @param node node to load from
     * @return loaded collada visual scene
     */
    static ColladaVisualScene fromNode(Node node) {
        if (!node.getNodeName().equals("visual_scene"))
            throw new IllegalArgumentException("Node given must be a visual scene node");
        ColladaVisualScene colladaVisualScene = new ColladaVisualScene();
        colladaVisualScene.setId(ColladaUtil.getAttribValue(node, "id"));
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("node")) {
                colladaVisualScene.addRootNode(readNode(n, colladaVisualScene).getId());
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_cvs_vs " + n.getNodeName());
            }
        }
        return colladaVisualScene;
    }

    private static ColladaNode readNode(Node node, ColladaVisualScene scene) {
        String id = getAttribValue(node, "id");
        String name = getAttribValue(node, "name");
        boolean isJoint = Objects.equals(getAttribValue(node, "type"), "JOINT");
        String sid = getAttribValue(node, "sid");
        Matrix4f matrix = new Matrix4f().identity();
        List<ColladaNode> nodes = new ArrayList<>();
        ColladaInstanceController instanceController = null;
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("node")) {
                nodes.add(readNode(n,scene));
            } else if (n.getNodeName().equals("matrix")) {
                matrix.mul(readMatrix4f(n));
            } else if (n.getNodeName().equals("scale")) {
                float[] data = readFloatArray(n);
                matrix.scale(data[0], data[1], data[2]);
            } else if (n.getNodeName().equals("rotate")) {
                float[] data = readFloatArray(n);
                matrix.rotate(data[3], data[0], data[1], data[2]);
            } else if (n.getNodeName().equals("translate")) {
                float[] data = readFloatArray(n);
                matrix.translate(data[0], data[1], data[2]);
            } else {
                if (n.getNodeName().equals("instance_controller")) {
                    instanceController = readInstanceController(n);
                } else if (n.getNodeName().equals("extra")) {
                    // ignore extra nodes
                } else if (!n.getNodeName().equals("#text")) {
                    Log.w(TAG, "unkn_cvs_n " + n.getNodeName());
                }
            }
        }
        ColladaNode colladaNode = new ColladaNode(id, nodes, matrix, isJoint);
        colladaNode.setName(name);
        colladaNode.setSid(sid);
        colladaNode.setInstanceController(instanceController);
        scene.addNode(colladaNode);
        return colladaNode;
    }

    private static ColladaInstanceController readInstanceController(Node node) {
        String url = getAttribValue(node, "url").replaceFirst("#", "");
        String skeleton = null;
        Map<String, String> materials = new HashMap<>();
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("skeleton")) {
                skeleton = n.getTextContent().replaceFirst("#", "");
            } else if (n.getNodeName().equals("bind_material")) {
                for (Node n2 : getListFromNodeList(n.getChildNodes())) {
                    if (n2.getNodeName().equals("technique_common")) {
                        for (Node n3 : getListFromNodeList(n2.getChildNodes())) {
                            if (n3.getNodeName().equals("instance_material")) {
                                materials.put(getAttribValue(n3, "symbol"), getAttribValue(n3, "target").replaceFirst("#", ""));
                            } else if (!n3.getNodeName().equals("#text")) {
                                Log.w(TAG, "unkn_ic:" + n3.getNodeName());
                            }
                        }
                    } else if(!n2.getNodeName().equals("#text")){
                        Log.w(TAG, "unkn_ic2:" + n2.getNodeName());
                    }
                }
            } else if(!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_ric " + n.getNodeName());
            }
        }
        return new ColladaInstanceController(url, materials, skeleton);
    }

    ColladaNode getNode(String id) {
        return nodes.get(id);
    }


    List<String> getRootNodes() {
        return rootNodes;
    }

    private void addNode(ColladaNode node) {
        nodes.put(node.getId(), node);
    }

    private void addRootNode(String rootNode) {
        this.rootNodes.add(rootNode);
    }

    static class ColladaInstanceController {
        private String url;
        private Map<String, String> bindMaterials;
        private String skeleton;

        ColladaInstanceController(String url, Map<String, String> bindMaterials, String skeleton) {
            this.url = url;
            this.bindMaterials = bindMaterials;
            this.skeleton = skeleton;
        }

        public String getUrl() {
            return url;
        }

        public String getBindMaterialId(String id) {
            return bindMaterials.get(id);
        }

        public String getSkeleton() {
            return skeleton;
        }
    }
}
