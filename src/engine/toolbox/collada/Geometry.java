package engine.toolbox.collada;

import engine.graphics.models.RawModel;
import engine.graphics.models.TexturedModel;
import engine.graphics.renderEngine.Loader;
import engine.graphics.textures.ModelTexture;
import engine.toolbox.Log;
import engine.toolbox.Util;
import org.joml.Matrix4f;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static engine.toolbox.collada.ColladaUtil.*;

public class Geometry {
    private static final String TAG = "COLLADA:geometry";


    private float[][] position;
    private float[][] normal;
    private float[][] textureCoordinates;
    private int[] indices;
    private String materialId;

    public Geometry(float[][] position, float[][] normal, float[][] textureCoordinates, int[] indices, String materialId) {
        this.position = position;
        this.normal = normal;
        this.textureCoordinates = textureCoordinates;
        this.indices = indices;
        this.materialId = materialId;
    }


    public RawModel getRawModel() {
        return Loader.loadToVAO(Util.get1DArray(position), Util.get1DArray(textureCoordinates), Util.get1DArray(normal), indices);
    }


    public TexturedModel getTexturedModel(Map<String, Material> materials, Map<String, Effect> effects) {
        String imageFile = getImageFile(materials,effects);
        return new TexturedModel(getRawModel(), new ModelTexture(Loader.loadTexture(imageFile)));
    }


    public String getImageFile(Map<String,Material> materials, Map<String, Effect> effects) {
        System.out.println(materials);
        System.out.println(materials.get(materialId));
        return materials.get(materialId).getInstanceEffect(effects).getImage().replaceFirst("file:///","");

    }

    public int[] getIndices() {
        return indices;
    }

    public float[][] getTextureCoordinates() {
        return textureCoordinates;
    }

    public float[][] getNormal() {
        return normal;
    }

    public float[][] getPosition() {
        return position;
    }

    public void setPosition(float[][] position) {
        this.position = position;
    }

    public void setNormal(float[][] normal) {
        this.normal = normal;
    }

    public void setTextureCoordinates(float[][] textureCoordinates) {
        this.textureCoordinates = textureCoordinates;
    }

    public void setIndices(int[] indices) {
        this.indices = indices;
    }


    public static Geometry fromNode(Node node) {
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

    private static Geometry readMesh(Node node) {
        Map<String, Node> sources = new HashMap<>();
        Node primitive = null;
        Node vertices = null;
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("source")) {
                sources.put(n.getAttributes().getNamedItem("id").getNodeValue(), n);
            } else if (n.getNodeName().equals("triangles")) {
                primitive = n;
            } else if (n.getNodeName().equals("polylist")) {
                primitive = n;
            } else if (n.getNodeName().equals("vertices")) {
                vertices = n;
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_mesh:" + n.getNodeName());
            }
        }
        if (primitive == null || vertices == null)
            throw new RuntimeException("Either no primitive or vertices element found");
        if (primitive.getNodeName().equals("triangles")) return readTriangles(primitive, vertices, sources);
        //todo if(primitive.getNodeName().equals("polylist")) return readPolylist(primitive);
        Log.e(TAG, "no primitive element found");
        return null;
    }

    private static Geometry readTriangles(Node triangles, Node vertices, Map<String, Node> sources) {
        String materialName = triangles.getAttributes().getNamedItem("material").getNodeValue();
        Geometry geometry = null;
        for (Node n : getListFromNodeList(triangles.getChildNodes())) {
            if (n.getNodeName().equals("input")) {
                String semantic = n.getAttributes().getNamedItem("semantic").getNodeValue();
                if (semantic.equals("VERTEX") && !vertices.getAttributes().getNamedItem("id").getNodeValue().equals(
                        n.getAttributes().getNamedItem("source").getNodeValue())) {
                    throw new RuntimeException("vertices id and primitive source does not match");
                }
                //todo offset
                switch (semantic) {
                    case "VERTEX":
                        geometry = readVertices(vertices, materialName, sources);
                        break;
                    case "NORMAL":
                    case "TEXCOORD":
                        Log.e(TAG,"todo_readT");
                        break;
                    default:
                        Log.w(TAG, "unkn_T_semantic" + semantic);
                }
            } else if (n.getNodeName().equals("p")) {
                geometry.setIndices(readIntArray(n));
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_t:" + n.getNodeName());
            }
        }
        if (geometry == null) throw new RuntimeException("no vertex element defined");
        return geometry;
    }

    private static Geometry readVertices(Node node, String imageFile, Map<String, Node> sources) {
        float[][] pos = null, normal = null, uv = null;
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("input")) {
                String source = n.getAttributes().getNamedItem("source").getNodeValue();
                String semantic = n.getAttributes().getNamedItem("semantic").getNodeValue();
                if (semantic.equals("POSITION")) {
                    pos = readSource(sources.get(source)).getFloatData();
                } else if (semantic.equals("NORMAL")) {
                    normal = readSource(sources.get(source)).getFloatData();
                } else if (semantic.equals("TEXCOORD")) {
                    uv = readSource(sources.get(source)).getFloatData();
                }
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_vert:" + n.getNodeName());
            }
        }
        return new Geometry(pos, normal, uv, null, imageFile);
    }

    /*private Vertices readPolylist(Node node) {
        //String img = readImage(readMaterial(getIdElement(node.getAttributes().getNamedItem("material").getNodeValue())).getInstanceEffect().getImage());
        String img = "white.png";
        float[][] normalData = null;
        float[][] uvData = null;
        int[] primitive = null;
        int[] vcount = null;
        int numberOfInputs = 0;
        Vertices vertices = null;
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("input")) {
                numberOfInputs++;
                String source = n.getAttributes().getNamedItem("source").getNodeValue();
                String semantic = n.getAttributes().getNamedItem("semantic").getNodeValue();
                switch (semantic) {
                    case "VERTEX":
                        vertices = readVertices(getIdElement(source), null, null);
                        break;
                    case "NORMAL":
                        normalData = readSource(getIdElement(source)).getFloatData();
                        break;
                    case "TEXCOORD":
                        uvData = readSource(getIdElement(source)).getFloatData();
                        break;
                    default:
                        Log.w("unknown input type: " + semantic);
                }
            } else if (n.getNodeName().equals("vcount")) {
                vcount = readInt_array(n);
            } else if (n.getNodeName().equals("p")) {
                primitive = readInt_array(n);
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_v:" + n.getNodeName());
            }
        }
        if(primitive != null) {
            float[][] pos  = new float[primitive.length / numberOfInputs][3];
            float[][] norm = new float[primitive.length / numberOfInputs][3];
            float[][] uv   = new float[primitive.length / numberOfInputs][2];
            int[] indices = new int[primitive.length / numberOfInputs];
            for (int i = 0; i < primitive.length / numberOfInputs; i++) {
                pos[i] = vertices.getPosition()[primitive[numberOfInputs * i]];
                norm[i] = normalData[primitive[numberOfInputs * i + 1]];
                uv[i]  = uvData[primitive[numberOfInputs* i + 2]];
                indices[i] = i;
            }
            vertices.setPosition(pos);
            vertices.setNormal(norm);
            vertices.setTexCoord(uv);
            vertices.setIndices(indices);
        } else {
            Log.w(TAG, "p is not set");
        }
        return vertices;
    }*/
}
