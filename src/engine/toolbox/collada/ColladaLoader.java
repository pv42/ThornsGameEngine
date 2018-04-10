package engine.toolbox.collada;

import engine.toolbox.Log;
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

import static engine.toolbox.collada.ColladaUtil.getAttribValue;
import static engine.toolbox.collada.ColladaUtil.getListFromNodeList;

/***
 * Created by pv42 on 02.08.16.
 */
public class ColladaLoader {
    private static final String TAG = "COLLADA:Loader";

    /**
     * loads a collada files models into vram
     *
     * @param filename files name to load from
     * @return list of the models loaded
     */
    public static Collada loadCollada(String filename) {
        Collada collada = null;
        try {
            filename = "res/meshs/" + filename + ".dae";
            Log.d(TAG, "loading file '" + filename + "'");
            File file = new File(filename);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document dom = builder.parse(file);
            Element root = dom.getDocumentElement();
            collada = loadCollada(root);
            Log.i(TAG, "file '" + filename + "' loaded");
        } catch (FileNotFoundException e) {
            Log.e(TAG, "file '" + filename + "' not found");
        } catch (ParserConfigurationException | SAXException | IOException | RuntimeException e) {
            e.printStackTrace();
        }
        return collada;
    }

    /**
     * loads a colladas models from a node
     *
     * @param root collada root node to load from
     * @return list of the models loaded
     */
    private static Collada loadCollada(Node root) {
        root.normalize();
        if (!root.getNodeName().equals("COLLADA")) Log.w(TAG, "root: " + root.getNodeName());
        NodeList mainNodes = root.getChildNodes();
        Collada collada = new Collada();
        for (int i = 0; i < mainNodes.getLength(); i++) {
            switch (mainNodes.item(i).getNodeName()) {
                case "asset":
                    collada.setColladaAsset(ColladaAsset.fromNode(mainNodes.item(i)));
                    break;
                case "library_animations":
                    collada.setAnimations(readAnimationLibrary(mainNodes.item(i)));
                    break;
                case "library_cameras":
                    collada.setCameras(readCameraLibrary(mainNodes.item(i)));
                    break;
                case "library_images":
                    collada.setImages(readImageLibrary(mainNodes.item(i)));
                    break;
                case "library_materials":
                    collada.setMaterials(readMaterialLibrary(mainNodes.item(i)));
                    break;
                case "library_effects":
                    collada.setEffects(readEffectLibrary(mainNodes.item(i)));
                    break;
                case "library_geometries":
                    collada.setGeometries(readGeometryLibrary(mainNodes.item(i)));
                    break;
                case "library_controllers":
                    collada.setControllers(readControllerLibrary(mainNodes.item(i)));
                    break;
                case "library_visual_scenes":
                    collada.setVisualScenes(readVisualSceneLibrary(mainNodes.item(i)));
                    break;
                case "scene":
                    collada.setScene(readScene(mainNodes.item(i), collada.getVisualScenes()));
                    break;
                case "#text":
                    break;
                default:
                    Log.w(TAG, "unkn :" + mainNodes.item(i).getNodeName());
            }
        }
        return collada;
    }

    /**
     * reads an camera library
     *
     * @param node library_cameras node to read from
     * @return map of the cameras, with the animations' ids as keys
     */
    private static Map<String, ColladaCamera> readCameraLibrary(Node node) {
        Map<String, ColladaCamera> cameras = new HashMap<>();
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("camera")) {
                ColladaCamera camera = ColladaCamera.fromNode(n);
                cameras.put(camera.getId(), camera);
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_la:" + n.getNodeName());
            }
        }
        return cameras;
    }

    /**
     * reads an animation library
     *
     * @param node library_animations node to read from
     * @return map of the animations, with the animations' ids as keys
     */
    private static Map<String, ColladaAnimation> readAnimationLibrary(Node node) {
        Map<String, ColladaAnimation> animations = new HashMap<>();
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("animation")) {
                ColladaAnimation animation = ColladaAnimation.fromNode(n);
                animations.put(animation.getId(), animation);
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_la:" + n.getNodeName());
            }
        }
        return animations;
    }

    /**
     * reads scene to use
     *
     * @param node   scene node to read
     * @param scenes visual_scenes library
     * @return scene from the library
     */
    private static ColladaVisualScene readScene(Node node, Map<String, ColladaVisualScene> scenes) {
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("instance_visual_scene")) {
                return scenes.get(Objects.requireNonNull(getAttribValue(n, "url")).replaceFirst("#", ""));
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
    private static Map<String, ColladaImage> readImageLibrary(Node node) {
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
    private static Map<String, ColladaVisualScene> readVisualSceneLibrary(Node node) {
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
    private static Map<String, ColladaMaterial> readMaterialLibrary(Node node) {
        Map<String, ColladaMaterial> materials = new HashMap<>();
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("material")) {
                ColladaMaterial material = ColladaMaterial.fromNode(n);
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
    private static Map<String, ColladaEffect> readEffectLibrary(Node node) {
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
    private static Map<String, ColladaGeometry> readGeometryLibrary(Node node) {
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
    private static Map<String, ColladaController> readControllerLibrary(Node node) {
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