package engine.toolbox.collada;

import engine.graphics.animation.Joint;
import engine.graphics.models.RawModel;
import engine.graphics.models.TexturedModel;
import engine.graphics.renderEngine.Loader;
import engine.graphics.textures.ModelTexture;
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

    /**
     * loads a collada files models into vram
     *
     * @param filename file to load from
     * @return list of the models loaded
     */
    public List<TexturedModel> loadColladaModelAnimated(String filename) {
        return loadColladaModelAnimated(filename, null);
    }

    /**
     * loads a collada files models into vram
     *
     * @param filename       files name to load from
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
            models = loadColladaModelAnimated(root, transformation);
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
     *
     * @param root           collada root node to load from
     * @param transformation transformation to apply before loading into vram
     * @return list of the models loaded
     */
    public List<TexturedModel> loadColladaModelAnimated(Node root, Matrix4f transformation) {
        root.normalize();
        if (!root.getNodeName().equals("COLLADA")) Log.w(TAG, "root: " + root.getNodeName());
        NodeList mainNodes = root.getChildNodes();
        ColladaVisualScene scene = null;
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
        return processScene(scene);
    }

    private List<TexturedModel> processScene(ColladaVisualScene scene) {
        List<TexturedModel> models = new ArrayList<>();
        for (int i = 0; i < scene.getRootNodes().size(); i++) {
            String nodeId = scene.getRootNodes().get(i);
            ColladaNode cnode = scene.getNode(nodeId);
            ColladaVisualScene.ColladaInstanceController ic = cnode.getInstanceController();
            if (ic != null) {
                ColladaController controller = controllers.get(ic.getUrl());
                ColladaNode skeletonRoot = scene.getNode(ic.getSkeleton());
                Map<String, Joint> joints = controller.getJoints();
                processNode(skeletonRoot, joints, null); //apply poses to joints
                ColladaGeometry geometry = geometries.get(controller.getGeometryId());
                RawModel model = Loader.loadToVAOAnimated(
                        Util.get1DArray(geometry.getPosition()),
                        Util.get1DArray(geometry.getTextureCoordinates()),
                        Util.get1DArray(geometry.getNormal()),
                        geometry.getIndices(),
                        Util.get1DArrayFromListListInteger(controller.getWeights().getIndices()),
                        Util.get1DArrayFromListListFloat(controller.getWeights().getWeights()),
                        controller.getJointList());
                String materialId = geometry.getMaterialId();
                materialId = ic.getBindMaterialId(materialId);
                ColladaEffect effect = materials.get(materialId).getInstanceEffect(effects);
                String imageFile = images.get(effect.getImage()).getSource();
                TexturedModel texturedModel = new TexturedModel(model, new ModelTexture(Loader.loadTexture(
                        imageFile.replaceFirst("file:///",""))));
                models.add(texturedModel);
            }
        }
        return models;
    }

    private void processNode(ColladaNode node, Map<String, Joint> joints, Joint parent) {
        Joint joint = joints.get(node.getSid());
        joint.setPoseTransformationMatrix(node.getTranslation());
        joint.setParent(parent);
        for (ColladaNode n : node.getChildren()) {
            processNode(n, joints, joint);
        }
    }

    /**
     * reads scene to use
     *
     * @param node   scene node to read
     * @param scenes visual_scenes library
     * @return scene from the library
     */
    private ColladaVisualScene readScene(Node node, Map<String, ColladaVisualScene> scenes) {
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("instance_visual_scene")) {
                return scenes.get(getAttribValue(n, "url").replaceFirst("#", ""));
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_sc" + n.getNodeName());
            }
        }
        Log.e(TAG, "No instance_visual_scene found in scene node");
        return null;
    }

    /**
     * reads an image library
     *
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
     *
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
     *
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
     *
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
     *
     * @param node library_geometries node to read from
     * @return map of the geometries, with the geometries' ids as keys
     */
    private Map<String, ColladaGeometry> readGeometryLibrary(Node node) {
        Map<String, ColladaGeometry> geometries = new HashMap<>();
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("geometry")) {
                ColladaGeometry geometry = ColladaGeometry.fromNode(n);
                geometries.put(geometry.getId(), geometry);
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unknown_lg:" + n.getNodeName());
            }
        }
        return geometries;
    }

    /**
     * read a library_controllers node
     *
     * @param node node to read
     */
    private Map<String, ColladaController> readControllerLibrary(Node node) {
        //Log.d(TAG,"library:controllers");
        Map<String, ColladaController> controllers = new HashMap<>();
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("controller")) {
                ColladaController controller = ColladaController.fromNode(n);
                controllers.put(controller.getId(), controller);
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_lc:" + n.getNodeName());
            }
        }
        return controllers;
    }
}