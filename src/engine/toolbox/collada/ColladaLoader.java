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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * Created by pv42 on 02.08.16.
 */
public class ColladaLoader {
    private static final String TAG = "COLLADA";
    private Map<String, Node> idElements = new HashMap<>();
    private Map<String, Node> symbolElements = new HashMap<>();
    private List<TexturedModel> animatedTexturedModels = new ArrayList<>();
    private Map<String, Joint> allJoints = new HashMap<>();
    private List<Node> skinsToHandle = new ArrayList<>();

    public List<TexturedModel> loadColladaModelAnimated(String filename) {
        return loadColladaModelAnimated(filename, null);
    }

    public List<TexturedModel> loadColladaModelAnimated(String filename, Matrix4f transformation) {
        try {
            filename = "res/meshs/" + filename + ".dae";
            Log.d(TAG, "loading file '" + filename + "'");
            File file = new File(filename);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document dom = builder.parse(file);
            Element root = dom.getDocumentElement();
            root.normalize();
            if (!root.getNodeName().equals("COLLADA")) Log.w(TAG, "root: " + root.getNodeName());
            NodeList mainNodes = root.getChildNodes();
            for (int i = 0; i < mainNodes.getLength(); i++) {
                switch (mainNodes.item(i).getNodeName()) {
                    case "asset":
                        asset(mainNodes.item(i));
                        break;
                    case "library_images":
                        library_images(mainNodes.item(i));
                        break;
                    case "library_materials":
                        library_materials(mainNodes.item(i));
                        break;
                    case "library_effects":
                        library_effects(mainNodes.item(i));
                        break;
                    case "library_geometries":
                        library_geometries(mainNodes.item(i));
                        break;
                    case "library_controllers":
                        library_controllers(mainNodes.item(i));
                        break;
                    case "library_visual_scenes":
                        library_visual_scene(mainNodes.item(i));
                        break;
                    case "scene":
                        break;
                    case "#text":
                        break;
                    default:
                        Log.i(TAG, "todo :" + mainNodes.item(i).getNodeName());
                }
            }
            Log.d(TAG, "loading data to VRAM");
            for (Node n : skinsToHandle) {
                animatedTexturedModels.add(readSkin(n).getAnimatedTexturedModel(transformation));
            }
            Log.i(TAG, "file '" + filename + "' loaded");
        } catch (FileNotFoundException e) {
            Log.e(TAG, "file '" + filename + "' not found");
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return animatedTexturedModels;
    }

    private void library_images(Node node) {
        //Log.d(TAG,"library:images");
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("image")) {
                putIdElement(n);
            } else if (!n.getNodeName().equals("#text")) {
                Log.i(TAG, "unkn_li:" + n.getNodeName());
            }
        }
    }

    private String readImage(Node node) {
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("init_from")) {
                return n.getTextContent().replaceFirst("file:///", "");
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_I:" + n.getNodeName());
            }
        }
        Log.e(TAG, "no init_from found");
        return null;
    }

    private void library_materials(Node node) {
        //Log.d(TAG,"library:materials");
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("material")) {
                putIdElement(n);
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_lm:" + n.getNodeName());
            }
        }
    }

    private Material readMaterial(Node node) {
        if (node.getNodeName().equals("instance_material")) {
            return readMaterial(getIdElement(readInstance_material(node)));
        }
        Material material = new Material();
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("instance_effect")) {
                material.setInstanceEffect(readEffect(getIdElement(n.getAttributes().getNamedItem("url").getNodeValue())));
            } else if (!(n.getNodeName().equals("#text") || n.getNodeName().equals("extra"))) {
                Log.w(TAG, "unkn_rm:" + n.getNodeName());
            }
        }
        if (material.getInstanceEffect() == null) Log.e(TAG, "ie == null : mat:" + node.getNodeName());
        return material;
    }

    private void library_effects(Node node) {
        //Log.d(TAG,"library:effect");
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("effect")) {
                putIdElement(n);
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_le:" + n.getNodeName());
            }
        }
    }

    private Effect readEffect(Node node) {
        Effect effect = new Effect();
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("profile_COMMON")) {
                for (Node n2 : getListFromNodeList(n.getChildNodes())) {
                    if (n2.getNodeName().equals("newparam")) {
                        readNewparam(n2, effect);
                    } else if (n2.getNodeName().equals("technique")) {
                        //todo
                    } else if (!n2.getNodeName().equals("#text")) {
                        Log.w(TAG, "unkn_pC:" + n.getNodeName());
                    }
                }
            } else if (n.getNodeName().equals("extra")) {
                //ignore extras
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_e:" + n.getNodeName());
            }
        }
        return effect;
    }

    private void readNewparam(Node node, Effect effect) {
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("surface")) {
                for (Node n2 : getListFromNodeList(n.getChildNodes())) {
                    if (n2.getNodeName().equals("init_from")) {
                        effect.setImage(getIdElement(n2.getTextContent()));
                    } else if (n2.getNodeName().equals("format")) {
                        //nothing yet
                    } else if (!n2.getNodeName().equals("#text")) {
                        Log.w(TAG, "unkn_sf:" + n2.getNodeName());
                    }
                }
            } else if (n.getNodeName().equals("sampler2D")) {
                //nothing yet
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_nP:" + n.getNodeName());
            }
        }
    }

    private void library_geometries(Node node) {
        //Log.d(TAG,"library:geometries");
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("geometry")) {
                geometry(n);
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_lg:" + n.getNodeName());
            }
        }
    }

    private void asset(Node node) {
        //Log.d(TAG,"asset");
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("created")) {
                //Log.d(TAG," created:" +n.getTextContent());
            } else if (n.getNodeName().equals("modified")) {
                //Log.d(TAG," last modified:" +n.getTextContent());
            } else if (n.getNodeName().equals("unit")) {
                String unit = n.getAttributes().getNamedItem("name").getNodeValue();
                Float value = Float.valueOf(n.getAttributes().getNamedItem("meter").getNodeValue());
                //Log.d(TAG," unit: " +value + unit);
            } else if (n.getNodeName().equals("up_axis")) {
                //Log.d(TAG," up:" +n.getTextContent());
            } else if (!n.getNodeName().equals("#text")) {
                //Log.d(TAG," unkn_a:" + n.getNodeName());
            }
        }
    }

    private void geometry(Node node) {
        putIdElement(node);
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("mesh")) {
                mesh(n);
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_g:" + n.getNodeName());
            }
        }
    }

    private void mesh(Node node) {
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("source")) {
                source(n);
            } else if (n.getNodeName().equals("vertices")) {
                putIdElement(n);
            } else if (!(n.getNodeName().equals("#text") || n.getNodeName().equals("triangles") || n.getNodeName().equals("polylist"))) {
                Log.w(TAG, "unkn_m:" + n.getNodeName());
            }
        }
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

    private Vertices readGeometry(Node node) {
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("mesh")) {
                return readMesh(n);
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_g:" + n.getNodeName());
            }
        }
        Log.e(TAG, "no mesh element found");
        return null;
    }

    private Vertices readMesh(Node node) {
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("triangles")) {
                return readTriangles(n);
            } else if (n.getNodeName().equals("polylist")) {
                return readPolylist(n);
            } else if (!(n.getNodeName().equals("#text") || n.getNodeName().equals("source") || n.getNodeName().equals("vertices"))) {
                Log.w(TAG, "unkn_m:" + n.getNodeName());
            }
        }
        Log.e(TAG, "no triangles Element found");
        return null;
    }

    private Vertices readTriangles(Node node) {
        String materialName = node.getAttributes().getNamedItem("material").getNodeValue();
        Material material = readMaterial(getSymbolElement(materialName));
        String imageFile = readImage(material.getInstanceEffect().getImage());
        Vertices vertices = null;
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("input")) {
                Node inputnode = getIdElement(n.getAttributes().getNamedItem("source").getNodeValue()); //todo semantic, offset
                String semantic = n.getAttributes().getNamedItem("semantic").getNodeValue();
                switch (semantic) {
                    case "VERTEX":
                        vertices = readVertices(inputnode, null, imageFile);
                        break;
                    case "NORMAL":
                        vertices.setNormal(readSource(inputnode).getFloatData());
                        break;
                    case "TEXCOORD":
                        vertices.setTexCoord(readSource(inputnode).getFloatData());
                        break;
                    default:
                        Log.w(TAG, "unkn_T_semantic" + semantic);
                }
            } else if (n.getNodeName().equals("p")) {
                vertices.setIndices(readInt_array(n));
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_t:" + n.getNodeName());
            }
        }
        assert vertices != null;
        return vertices;
    }

    private Vertices readPolylist(Node node) {
        //String img = readImage(readMaterial(getIdElement(node.getAttributes().getNamedItem("material").getNodeValue())).getInstanceEffect().getImage());
        String img = "white";

        float[][] norm = null;
        float[][] uv = null;
        int[] p = null;
        int[] vcount = null;
        Vertices vertices = null;
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("input")) {
                String source = n.getAttributes().getNamedItem("source").getNodeValue();
                String semantic = n.getAttributes().getNamedItem("semantic").getNodeValue();
                switch (semantic) {
                    case "VERTEX":
                        vertices = readVertices(getIdElement(source), null, null);
                        break;
                    case "NORMAL":
                        norm = readSource(getIdElement(source)).getFloatData();
                        break;
                    case "TEXCOORD":
                        uv = readSource(getIdElement(source)).getFloatData();
                        break;
                    default:
                        Log.w("unknown input type: " + semantic);
                }
            } else if (n.getNodeName().equals("vcount")) {
                vcount = readInt_array(n);
            } else if (n.getNodeName().equals("p")) {
                p = readInt_array(n);
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_v:" + n.getNodeName());
            }
        }
        if(p != null) {
            float[][] normal = new float[p.length / 3][3];
            for (int i = 0; i < p.length / 3; i++) {
                normal[i] = norm[p[3 * i + 1]];
            }
            vertices.setNormal(normal);
        } else {
            Log.w(TAG,"p is not set");
        }

        vertices.setTexCoord(uv);
        vertices.setIndices(p);
        vertices.setImageFile(img);
        return vertices;
    }

    private Vertices readVertices(Node node, int[] indices, String imageFile) {
        float[][] pos = null, normal = null, uv = null;
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("input")) {
                String source = n.getAttributes().getNamedItem("source").getNodeValue();
                String semantic = n.getAttributes().getNamedItem("semantic").getNodeValue();
                if (semantic.equals("POSITION")) {
                    pos = readSource(getIdElement(source)).getFloatData();
                } else if (semantic.equals("NORMAL")) {
                    normal = readSource(getIdElement(source)).getFloatData();
                } else if (semantic.equals("TEXCOORD")) {
                    uv = readSource(getIdElement(source)).getFloatData();
                }
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_v:" + n.getNodeName());
            }
        }
        return new Vertices(pos, normal, uv, indices, imageFile);
    }

    private Source readSource(Node node) {
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("technique_common")) {
                for (Node n2 : getListFromNodeList(n.getChildNodes())) {
                    if (n2.getNodeName().equals("accessor")) {
                        return readAccessor(n2);
                    } else if (!n2.getNodeName().equals("#text")) {
                        Log.w(TAG, "unkn_S:" + n.getNodeName());
                    }
                }
            } else if (n.getNodeName().equals("float_array") || n.getNodeName().equals("Name_array")) {
                //noting
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_S:" + n.getNodeName());
            }
        }
        Log.e(TAG, "no technique_common found");
        return null;

    }

    private Source readAccessor(Node node) {
        String source = node.getAttributes().getNamedItem("source").getNodeValue();
        int count = Integer.valueOf(node.getAttributes().getNamedItem("count").getNodeValue());
        int stride = Integer.valueOf(node.getAttributes().getNamedItem("stride").getNodeValue());
        List<Param> params = new ArrayList<>();
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("param")) {
                params.add(readParam(n));
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_A:" + n.getNodeName());
            }
        }
        String[] rawData = getIdElement(source).getTextContent().split(" ");
        String[][] data = new String[count][stride];
        for (int i = 0; i < rawData.length; i++) {
            try {
                data[i / stride][i % stride] = rawData[i];
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
                Log.w("[" + i / count + "][" + i % count + "]");
                System.exit(-1);
            }
        }
        return new Source(data, params.get(0).getDataType());
    }

    private Param readParam(Node node) {
        return new Param(node.getAttributes().getNamedItem("name").getNodeValue(), node.getAttributes().getNamedItem("type").getNodeValue());
    }

    /**
     * read a library_controllers node
     * @param node node to read
     */
    private void library_controllers(Node node) {
        //Log.d(TAG,"library:controllers");
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("controller")) {
                controller(n);
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_lc:" + n.getNodeName());
            }
        }
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
                Log.w(TAG,"toto_sk:" +  n.getNodeName());
            } else if (n.getNodeName().equals("vertex_weights")) {
                Log.w(TAG,"toto_sk:" +  n.getNodeName());
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_sk:" + n.getNodeName());
            }
        }
    }

    private ColladaSkin readSkin(Node node) {
        ColladaSkin skin = new ColladaSkin();
        skin.setVsource(readGeometry(getIdElement(node.getAttributes().getNamedItem("source").getNodeValue())));
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("joints")) {
                skin.setJoints(readJoints(n)); //store in joints global
            } else if (n.getNodeName().equals("bind_shape_matrix")) {
                skin.setBindShapeMatrix(readMatrix4f(n));
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
                    sids = Util.getList(readSource(getIdElement(n.getAttributes().getNamedItem("source").getNodeValue())).getStringData());
                } else if (n.getAttributes().getNamedItem("semantic").getNodeValue().equals("INV_BIND_MATRIX")) {
                    matrices = Util.getList(readSource(getIdElement(n.getAttributes().getNamedItem("source").getNodeValue())).getMatrix4Data());
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
                    weights = Util.getList(readSource(getIdElement(n.getAttributes().getNamedItem("source").getNodeValue())).getFloatData());
                } else {
                    Log.w("unkn_se: input::semantic" + n.getAttributes().getNamedItem("semantic"));
                }
            } else if (n.getNodeName().equals("vcount")) {
                vcount = Util.getList(readInt_array(n));
            } else if (n.getNodeName().equals("v")) {
                v = Util.getList(readInt_array(n));
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
            //Joint joint = new Joint(parent);
            Matrix4f tm = null;
            List<Node> childs = new ArrayList<>();
            for (Node n : getListFromNodeList(node.getChildNodes())) {
                if (n.getNodeName().equals("matrix")) {
                    tm = readMatrix4f(n);
                } else if (n.getNodeName().equals("rotate")) {
                    float[] rot = readFloat_array(n);
                    Log.w(TAG,"todo nJr");
                    //todo joint.rotateJoint(new Vector3f(rot[0], rot[1], rot[2]), rot[3]);
                } else if (n.getNodeName().equals("scale")) {
                    float[] rot = readFloat_array(n);
                    Log.w(TAG,"todo nJs");
                    //joint.scaleJoint(new Vector3f(rot[0], rot[1], rot[2]));
                } else if (n.getNodeName().equals("translate")) {
                    float[] rot = readFloat_array(n);
                    Log.w(TAG,"todo nJt");
                    //joint.translateJoint(new Vector3f(rot[0], rot[1], rot[2]));
                } else if (n.getNodeName().equals("node")) {
                    childs.add(n);
                } else if (!n.getNodeName().equals("#text")) {
                    Log.w(TAG, "unkn_n:" + n.getNodeName());
                }

            }
            if(tm == null) tm = new Matrix4f().identity();
            Joint joint = new Joint(name,tm ,parent);
            allJoints.put(sid, joint);
            for(Node n: childs) {
                node(n,joint);
            }
        } else if (type.equals("NODE")) {
            for (Node n : getListFromNodeList(node.getChildNodes())) {
                if (n.getNodeName().equals("instance_controller")) {
                    instance_controller(n);
                } else if (n.getNodeName().equals("matrix")) {
                    //nothing
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
                                putSymbolElement(n3);
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

    private String readInstance_material(Node node) {
        if (node == null) Log.e(TAG, "instaceMaterialNode is null");
        return node.getAttributes().getNamedItem("target").getNodeValue();
    }

    private static int[] readInt_array(Node node) {
        int[] array = new int[node.getTextContent().split(" ").length];
        int i = 0;
        for (String s : node.getTextContent().split(" ")) {
            array[i] = Integer.valueOf(s);
            i++;
        }
        return array;
    }

    private static float[] readFloat_array(Node node) {
        float[] array = new float[node.getTextContent().split(" ").length];
        int i = 0;
        for (String s : node.getTextContent().split(" ")) {
            array[i] = Float.valueOf(s);
            i++;
        }
        return array;
    }

    private static Matrix4f readMatrix4f(Node node) {
        Matrix4f matrix4f = new Matrix4f();
        float[] array = new float[16];
        int i = 0;
        for (String s : node.getTextContent().split(" ")) {
            array[i] = Float.valueOf(s);
            i++;
        }
        matrix4f.set(array);
        return matrix4f;
    }

    private List<Node> getListFromNodeList(NodeList nodeList) {
        List<Node> list = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            list.add(nodeList.item(i));
        }
        return list;
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

    private void putSymbolElement(Node node) {

        String symbol = node.getAttributes().getNamedItem("symbol").getNodeValue();
        symbolElements.put(symbol, node);
    }

    private Node getIdElement(String hashedId) {
        Node node = idElements.get(hashedId.replaceFirst("#", ""));
        if (node == null) Log.e(TAG, "id " + hashedId + " not found");
        return node;
    }

    private Node getSymbolElement(String symbol) {
        Node node = symbolElements.get(symbol.replaceFirst("#", ""));
        if (node == null) Log.e(TAG, "symbol " + symbol + " not found");
        return node;
    }
}