package engine.toolbox.collada;

import engine.graphics.animation.Joint;
import engine.graphics.models.TexturedModel;
import engine.toolbox.Log;
import engine.toolbox.Util;
import org.joml.Matrix4f;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static engine.toolbox.collada.ColladaUtil.getAttribValue;
import static engine.toolbox.collada.ColladaUtil.getListFromNodeList;

/***
 * Created by pv42 on 02.08.16.
 */
//todo prevent loading multiple collada files
public class ColladaLoader {
    private static final String TAG = "COLLADA";
    private ColladaAsset colladaAsset = null;
    private Map<String, ColladaImage> images;
    private Map<String, Material> materials;
    private Map<String, ColladaEffect> effects;
    private Map<String, ColladaGeometry> geometries;
    private Map<String, ColladaController> controllers;
    private Map<String, ColladaVisualScene> visualScenes;
    private Map<String, Node> idElements = new HashMap<>();
    private List<TexturedModel> animatedTexturedModels = new ArrayList<>();
    private Map<String, Joint> allJoints = new HashMap<>();
    private List<Node> skinsToHandle = new ArrayList<>();
    private Map<String, String> instanceMaterials = new HashMap<>();

    /**
     * loads a collada files models into vram
     * @param filename file to load from
     * @return list of the models loaded
     */
    public List<TexturedModel> loadColladaModelAnimated(String filename) {
        return loadColladaModelAnimated(filename, null);
    }

    /**
     * loads a collada files models into vram
     * @param filename files name to load from
     * @param transformation transformation to apply before loading into vram
     * @return list of the models loaded
     */
    public List<TexturedModel> loadColladaModelAnimated(String filename, Matrix4f transformation) {
        List<TexturedModel> models = new ArrayList<>();
        try {
            filename = "res/meshs/" + filename + ".dae";
            Log.d(TAG, "loading file '" + filename + "'");
            File file = new File(filename);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document dom = builder.parse(file);
            Element root = dom.getDocumentElement();
            models =  loadColladaModelAnimated(root, transformation);
            Log.i(TAG, "file '" + filename + "' loaded");
        } catch (FileNotFoundException e) {
            Log.e(TAG, "file '" + filename + "' not found");
        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return models;
    }

    /**
     * loads a colladas models from a node
     * @param root collada root node to load from
     * @param transformation transformation to apply before loading into vram
     * @return list of the models loaded
     */
    public List<TexturedModel> loadColladaModelAnimated(Node root, Matrix4f transformation) {
        root.normalize();
        if (!root.getNodeName().equals("COLLADA")) Log.w(TAG, "root: " + root.getNodeName());
        NodeList mainNodes = root.getChildNodes();
        ColladaVisualScene scene;
        for (int i = 0; i < mainNodes.getLength(); i++) {
            switch (mainNodes.item(i).getNodeName()) {
                case "asset":
                    colladaAsset = ColladaAsset.fromNode(mainNodes.item(i));
                    break;
                case "library_images":
                    images = readImageLibrary(mainNodes.item(i));
                    break;
                case "library_materials":
                    materials = readMaterialLibrary(mainNodes.item(i));
                    break;
                case "library_effects":
                    effects = readEffectLibrary(mainNodes.item(i));
                    break;
                case "library_geometries":
                    geometries = readGeometryLibrary(mainNodes.item(i));
                    break;
                case "library_controllers":
                    controllers = readControllerLibrary(mainNodes.item(i));
                    break;
                case "library_visual_scenes":
                    visualScenes = readVisualSceneLibrary(mainNodes.item(i));
                    library_visual_scene(mainNodes.item(i));
                    break;
                case "scene":
                    scene = readScene(mainNodes.item(i), visualScenes);
                    break;
                case "#text":
                    break;
                default:
                    Log.i(TAG, "todo :" + mainNodes.item(i).getNodeName());
            }
        }
        Log.d(TAG, "loading data to VRAM");
        for (Node n : skinsToHandle) {
            animatedTexturedModels.add(readSkin(n, geometries)
                    .getAnimatedTexturedModel(transformation, materials, effects, instanceMaterials, images));
        }
        //new
        //return processScene(scene);

        return animatedTexturedModels;
    }

    private List<TexturedModel> processScene(ColladaVisualScene scene) {
        List<TexturedModel> models = new ArrayList<>();
        for(int i = 0; i < scene.getRootNodes().size(); i++) {
            String nodeId = scene.getRootNodes().get(i);
            ColladaNode cnode = scene.getNode(nodeId);
            ColladaVisualScene.ColladaInstanceController ic = cnode.getInstanceController();
            if(ic != null) {
                ColladaController controller = controllers.get(ic.getUrl());
                ColladaNode skeletonRoot = scene.getNode(ic.getSkeleton());
                Map<String,Joint> joints = controller.getJoints();
                processNode(skeletonRoot, joints, null); //apply poses to joints
                controller.

            }
        }
        return models;
    }

    private void processNode(ColladaNode node, Map<String, Joint> joints, ColladaNode parent) {
        Joint joint = joints.get(node.getId());
        joint.setPoseTransformationMatrix(node.getTranslation());
        for(ColladaNode n: node.getChildren()) {
            processNode(n, joints, node);
        }
    }

    /**
     * reads scene to use
     * @param node scene node to read
     * @param scenes visual_scenes library
     * @return scene from the library
     */
    public ColladaVisualScene readScene(Node node, Map<String, ColladaVisualScene> scenes) {
        for(Node n: getListFromNodeList(node.getChildNodes())) {
            if(n.getNodeName().equals("instance_visual_scene")) {
                return scenes.get(getAttribValue(n, "url"). replaceFirst("#",""));
            } else if(!n.getNodeName().equals("#text")) {
                Log.w(TAG,"unkn_sc" + n.getNodeName());
            }
        }
        Log.e(TAG,"No instance_visual_scene found in scene node");
        return null;
    }

    /**
     * reads an image library
     * @param node library_images node to read from
     * @return map of the images, with the images' ids as keys
     */
    private Map<String, ColladaImage> readImageLibrary(Node node) {
        Map<String, ColladaImage> images = new HashMap<>();
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("image")) {
                ColladaImage image = ColladaImage.formNode(n);
                images.put(image.getId(), image);
            } else if (!n.getNodeName().equals("#text")) {
                Log.i(TAG, "unkn_li:" + n.getNodeName());
            }
        }
        return images;
    }

    /**
     * reads a visual scene library
     * @param node library_visual_scene node to read from
     * @return map of visual scenes, with the scenes' ids as keys
     */
    private Map<String, ColladaVisualScene> readVisualSceneLibrary(Node node) {
        Map<String, ColladaVisualScene> visualScenes = new HashMap<>();
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("visual_scene")) {
                ColladaVisualScene scene = ColladaVisualScene.fromNode(n);
                visualScenes.put(scene.getId(), scene);
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_lvs:" + n.getNodeName());
            }
        }
        return visualScenes;
    }
    /**
     * reads a material library
     * @param node library_materials node to read from
     * @return map of the materials, with the materials' ids as keys
     */
    private Map<String, Material> readMaterialLibrary(Node node) {
        Map<String, Material> materials = new HashMap<>();
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("material")) {
                Material material = Material.fromNode(n);
                materials.put(material.getId(), material);
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_lm:" + n.getNodeName());
            }
        }
        return materials;
    }

    /**
     * reads a effect library
     * @param node library_effects node to read from
     * @return map of the effects, with the effects' ids as keys
     */
    private Map<String, ColladaEffect> readEffectLibrary(Node node) {
        //Log.d(TAG,"library:effect");
        Map<String, ColladaEffect> effects = new HashMap<>();
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("effect")) {
                ColladaEffect colladaEffect = ColladaEffect.fromNode(n);
                effects.put(colladaEffect.getId(), colladaEffect);
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_le:" + n.getNodeName());
            }
        }
        return effects;
    }

    /**
     * reads a geometry library
     * @param node library_geometries node to read from
     * @return map of the geometries, with the geometries' ids as keys
     */
    private Map<String, ColladaGeometry> readGeometryLibrary(Node node) {
        Map<String, ColladaGeometry> geometries = new HashMap<>();
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("geometry")) {
                ColladaGeometry geometry = ColladaGeometry.fromNode(n);
                geometries.put(geometry.getId(), geometry);
            } else if(!n.getNodeName().equals("#text")){
                Log.w(TAG, "unknown_lg:" + n.getNodeName());
            }
        }
        return geometries;
    }

    private void source(Node node) {
        putIdElement(node);
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("float_array")) {
                putIdElement(n);
            } else if (n.getNodeName().equals("Name_array")) {
                putIdElement(n);
            } else if (!(n.getNodeName().equals("#text") || n.getNodeName().equals("technique_common"))) {
                Log.w(TAG, "unkn_s:" + n.getNodeName());
            }
        }
    }

    /**
     * read a library_controllers node
     *
     * @param node node to read
     */
    private Map<String, ColladaController> readControllerLibrary(Node node) {
        //Log.d(TAG,"library:controllers");
        Map<String,ColladaController> controllers = new HashMap<>();
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("controller")) {
                ColladaController controller = ColladaController.fromNode(n);
                controllers.put(controller.getId(), controller);
                controller(n);
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_lc:" + n.getNodeName());
            }
        }
        return controllers;
    }

    /**
     * reads a controller node
     * @param node node to read
     */
    private void controller(Node node) {
        putIdElement(node);
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("skin")) {
                skin(n);
            } else if (!(n.getNodeName().equals("#text") || n.getNodeName().equals("extra"))) {
                Log.w(TAG, "unkn_c:" + n.getNodeName());
            }
        }
    }

    /**
     * reads a skin element (child element from controller)
     * @param node node to read
     */
    private void skin(Node node) {
        skinsToHandle.add(node);
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("bind_shape_matrix")) {
                //todo usally idetity but can be used
            } else if (n.getNodeName().equals("source")) {
                source(n);
            } else if (n.getNodeName().equals("joints")) {
                Log.w(TAG, "toto_sk:" + n.getNodeName());
            } else if (n.getNodeName().equals("vertex_weights")) {
                Log.w(TAG, "toto_sk:" + n.getNodeName());
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_sk:" + n.getNodeName());
            }
        }
    }

    private ColladaSkin readSkin(Node node, Map<String, ColladaGeometry> geometries) {
        ColladaSkin skin = new ColladaSkin();
        skin.setVsource(geometries.get(getAttribValue(node, "source").replaceFirst("#", "")));
        //readGeometry(getIdElement(node.getAttributes().getNamedItem("source").getNodeValue())));
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("joints")) {
                skin.setJoints(readJoints(n)); //store in joints global
            } else if (n.getNodeName().equals("bind_shape_matrix")) {
                skin.setBindShapeMatrix(ColladaUtil.readMatrix4f(n));
            } else if (n.getNodeName().equals("vertex_weights")) {
                skin.setVertexWeights(readVertex_weights(n));
            } else if (!(n.getNodeName().equals("#text") || n.getNodeName().equals("source"))) {
                Log.w(TAG, "unkn_sk:" + n.getNodeName());
            }
        }
        return skin;
    }

    private List<Joint> readJoints(Node node) {
        List<Matrix4f> matrices = null;
        List<String> sids = null;
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("input")) {
                if (n.getAttributes().getNamedItem("semantic").getNodeValue().equals("JOINT")) {
                    sids = Util.getList(ColladaUtil.readSource(getIdElement(n.getAttributes().getNamedItem("source").getNodeValue())).getStringData());
                } else if (n.getAttributes().getNamedItem("semantic").getNodeValue().equals("INV_BIND_MATRIX")) {
                    matrices = Util.getList(ColladaUtil.readSource(getIdElement(n.getAttributes().getNamedItem("source").getNodeValue())).getMatrix4Data());
                } else {
                    Log.w("unkn_s: input::semantic" + n.getAttributes().getNamedItem("semantic"));
                }
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_J:" + n.getNodeName());
            }
        }
        if (matrices == null || sids == null || sids.size() != matrices.size()) Log.e(TAG, "wrong joint data");
        List<Joint> joints = new ArrayList<>();
        for (int i = 0; i < matrices.size(); i++) {
            String sid = sids.get(i);
            allJoints.get(sid).setInverseBindMatrix(matrices.get(i));
            joints.add(allJoints.get(sid));
        }
        return joints;

    }

    private VertexWeights readVertex_weights(Node node) {
        List<Float> weights = null;
        List<Integer> vcount = null;
        List<Integer> v = null;
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("input")) {
                if (n.getAttributes().getNamedItem("semantic").getNodeValue().equals("JOINT")) {
                    //todo worth it?
                } else if (n.getAttributes().getNamedItem("semantic").getNodeValue().equals("WEIGHT")) {
                    weights = Util.getList(ColladaUtil.readSource(getIdElement(n.getAttributes().getNamedItem("source").getNodeValue())).getFloatData());
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

    private void library_visual_scene(Node node) {
        //Log.d(TAG,"library:visual_scene");
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("visual_scene")) {
                visual_scene(n);
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_lvs:" + n.getNodeName());
            }
        }
    }

    private void visual_scene(Node node) {
        putIdElement(node);
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("node")) {
                node(n, null);
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_vs:" + n.getNodeName());
            }
        }
    }

    private void node(Node node, Joint parent) {
        putIdElement(node);
        String type = node.getAttributes().getNamedItem("type") == null ? "NODE" : node.getAttributes().getNamedItem("type").getNodeValue();
        if (type.equals("JOINT")) {
            String sid = node.getAttributes().getNamedItem("sid").getNodeValue();
            String name = node.getAttributes().getNamedItem("sid").getNodeValue();
            Matrix4f tm = new Matrix4f().identity();
            List<Node> childs = new ArrayList<>();
            for (Node n : getListFromNodeList(node.getChildNodes())) {
                if (n.getNodeName().equals("matrix")) {
                    tm.mul(ColladaUtil.readMatrix4f(n));
                } else if (n.getNodeName().equals("rotate")) {
                    float[] rot = ColladaUtil.readFloatArray(n);
                    tm.rotate((float) Math.toRadians(rot[3]), rot[0], rot[1], rot[2]);
                } else if (n.getNodeName().equals("scale")) {
                    float[] sca = ColladaUtil.readFloatArray(n);
                    tm.scale(sca[0], sca[1], sca[2]);
                } else if (n.getNodeName().equals("translate")) {
                    float[] trans = ColladaUtil.readFloatArray(n);
                    tm.translate(trans[0], trans[1], trans[2]);
                } else if (n.getNodeName().equals("node")) {
                    childs.add(n);
                } else if (n.getNodeName().equals("extra")) {
                    //todo Log.d(TAG,"extra not implemented");
                } else if (!n.getNodeName().equals("#text")) {
                    Log.w(TAG, "unkn_node:" + n.getNodeName());
                }

            }
            if (tm == null) tm = new Matrix4f().identity();
            Joint joint = new Joint(name, tm, parent);
            allJoints.put(sid, joint);
            for (Node n : childs) {
                node(n, joint);
            }
        } else if (type.equals("NODE")) {
            Matrix4f matrix = new Matrix4f().identity();
            for (Node n : getListFromNodeList(node.getChildNodes())) {
                if (n.getNodeName().equals("instance_controller")) {
                    instance_controller(n);
                } else if (n.getNodeName().equals("matrix")) {
                    matrix.mul(ColladaUtil.readMatrix4f(n));
                } else if (n.getNodeName().equals("rotate")) {
                    float[] rot = ColladaUtil.readFloatArray(n);
                    matrix.rotate((float) Math.toRadians(rot[3]), rot[0], rot[1], rot[2]);
                } else if (n.getNodeName().equals("scale")) {
                    float[] sca = ColladaUtil.readFloatArray(n);
                    matrix.scale(sca[0], sca[1], sca[2]);
                } else if (n.getNodeName().equals("translate")) {
                    float[] trans = ColladaUtil.readFloatArray(n);
                    matrix.translate(trans[0], trans[1], trans[2]);
                } else if (n.getNodeName().equals("node")) {
                    node(n, null);
                } else if (!n.getNodeName().equals("#text")) {
                    Log.w(TAG, "unkn_n:" + n.getNodeName());
                }
            }
        }
    }

    private void instance_controller(Node node) {
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("bind_material")) {
                for (Node n2 : getListFromNodeList(n.getChildNodes())) {
                    if (n2.getNodeName().equals("technique_common")) {
                        for (Node n3 : getListFromNodeList(n2.getChildNodes())) {
                            if (n3.getNodeName().equals("instance_material")) {
                                instanceMaterials.put(getAttribValue(n3, "symbol"), getAttribValue(n3, "target").replaceFirst("#", ""));
                            } else if (!n3.getNodeName().equals("#text")) {
                                Log.w(TAG, "unkn_tc:" + n3.getNodeName());
                            }
                        }
                    } else if (!n2.getNodeName().equals("#text")) {
                        Log.w(TAG, "unkn_tc:" + n2.getNodeName());
                    }
                }
            } else if (!(n.getNodeName().equals("#text") || n.getNodeName().equals("skeleton") || n.getNodeName().equals("bind_material"))) {
                Log.w(TAG, "unkn_ic:" + n.getNodeName());
            }
        }
    }

    private void putIdElement(Node node) {
        Node node1 = node.getAttributes().getNamedItem("id");
        if (node1 == null) {
            Log.w(TAG, "no id found");
            return;
        }
        String id = node1.getNodeValue();
        idElements.put(id, node);
    }

    private Node getIdElement(String hashedId) {
        Node node = idElements.get(hashedId.replaceFirst("#", ""));
        if (node == null) Log.e(TAG, "id " + hashedId + " not found");
        return node;
    }

}