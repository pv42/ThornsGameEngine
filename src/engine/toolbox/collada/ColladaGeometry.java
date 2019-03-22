package engine.toolbox.collada;

import engine.graphics.glglfwImplementation.GLLoader;
import engine.graphics.glglfwImplementation.models.GLRawModel;
import engine.toolbox.Log;
import engine.toolbox.StorageFormatUtil;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

import static engine.toolbox.collada.ColladaUtil.*;

public class ColladaGeometry extends ColladaPrimaryElement {
    private static final String TAG = "COLLADA:geometry";
    private String id;
    private float[][] position;
    private float[][] normal;
    private float[][] textureCoordinates;
    private int[] indices;
    private int[] polylistIndicesBase;
    private String materialId;

    private ColladaGeometry(float[][] position, float[][] normal, float[][] textureCoordinates, int[] indices, String materialId) {
        this.position = position;
        this.normal = normal;
        this.textureCoordinates = textureCoordinates;
        this.indices = indices;
        this.materialId = materialId;
    }

    public GLRawModel getRawModel() {
        return GLLoader.loadToVAO(StorageFormatUtil.get1DArray(position), StorageFormatUtil.get1DArray(textureCoordinates), StorageFormatUtil.get1DArray(normal), indices);
    }

    int[] getIndices() {
        return indices;
    }

    float[][] getTextureCoordinates() {
        return textureCoordinates;
    }

    public float[][] getNormal() {
        return normal;
    }

    public float[][] getPosition() {
        return position;
    }

    String getMaterialId() {
        return materialId;
    }

    void setPosition(float[][] position) {
        this.position = position;
    }

    void setNormal(float[][] normal) {
        this.normal = normal;
    }

    private void setIndices(int[] indices) {
        this.indices = indices;
    }

    private void setTextureCoordinates(float[][] textureCoordinates) {
        this.textureCoordinates = textureCoordinates;
    }
    /**
     * loads a collada geometry from a collada node
     *
     * @param node node to load from
     * @return loaded collada geometry
     */
    static ColladaGeometry fromNode(Node node) {
        if (!node.getNodeName().equals("geometry")) throw new RuntimeException("Node given must be an asset node");
        ColladaGeometry geometry = null;
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("mesh")) {
                geometry = readMesh(n);
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_g:" + n.getNodeName());
            }
        }
        if (geometry == null) Log.e(TAG, "no mesh element found");
        else geometry.setId(getAttribValue(node, "id"));
        return geometry;
    }

    private static ColladaGeometry readMesh(Node node) {
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
        if (primitive.getNodeName().equals("polylist")) return readPolylist(primitive, vertices, sources);
        throw new RuntimeException("no primitive element for mesh found");
    }

    private static ColladaGeometry readTriangles(Node triangles, Node vertices, Map<String, Node> sources) {
        String materialName = triangles.getAttributes().getNamedItem("material").getNodeValue();
        ColladaGeometry geometry = null;
        for (Node n : getListFromNodeList(triangles.getChildNodes())) {
            if (n.getNodeName().equals("input")) {
                String semantic = n.getAttributes().getNamedItem("semantic").getNodeValue();
                if (semantic.equals("VERTEX") && !getAttribValue(vertices, "id").equals(
                        getAttribValue(n, "source").replaceFirst("#", ""))
                        ) {
                    throw new RuntimeException("vertices id and primitive source does not match");
                }
                //todo offset
                switch (semantic) {
                    case "VERTEX":
                        geometry = readVertices(vertices, materialName, sources);
                        break;
                    case "NORMAL":
                    case "TEXCOORD":
                        Log.e(TAG, "todo_readN/T");
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

    private static ColladaGeometry readVertices(Node node, String materialId, Map<String, Node> sources) {
        float[][] pos = null, normal = null, uv = null;
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("input")) {
                String source = getAttribValue(n, "source").replaceFirst("#", "");
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
        return new ColladaGeometry(pos, normal, uv, null, materialId);
    }


    private static ColladaGeometry readPolylist(Node polylist, Node vertices, Map<String, Node> sources) {
        //String img = readImage(readMaterial(getIdElement(node.getAttributes().getNamedItem("material").getNodeValue())).getInstanceEffect().getImage());
        String materialName = getAttribValue(polylist,"material");
        float[][] normalData = null;
        float[][] uvData = null;
        int[] primitive = null;
        int[] vcounts = null;
        int numberOfInputs = 0;
        ColladaGeometry geometry = null;
        for (Node n : getListFromNodeList(polylist.getChildNodes())) {
            if (n.getNodeName().equals("input")) {
                numberOfInputs++;
                String sourceId = getAttribValue(n, "source").replaceFirst("#", "");
                String semantic = getAttribValue(n, "semantic").replaceFirst("#", "");
                // n.getAttributes().getNamedItem("semantic").getNodeValue();
                switch (semantic) {
                    case "VERTEX":
                        geometry = readVertices(vertices, materialName, sources );
                        break;
                    case "NORMAL":
                        normalData = readSource(sources.get(sourceId)).getFloatData();
                        break;
                    case "TEXCOORD":
                        uvData = readSource(sources.get(sourceId)).getFloatData();
                        break;
                    default:
                        Log.w("unknown input type: " + semantic);
                }
            } else if (n.getNodeName().equals("vcount")) {
                vcounts = readIntArray(n);
            } else if (n.getNodeName().equals("p")) {
                primitive = readIntArray(n);
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_v:" + n.getNodeName());
            }
        }
        for(int vcount: vcounts) {
            if(vcount != 3) throw new RuntimeException("Only 3 vertices supported per polygon");
        }
        if (primitive != null) {
            float[][] pos = new float[primitive.length / numberOfInputs][3];
            float[][] norm = new float[primitive.length / numberOfInputs][3];
            float[][] uv = new float[primitive.length / numberOfInputs][2];
            int[] indices = new int[primitive.length / numberOfInputs];

            for (int i = 0; i < primitive.length / numberOfInputs; i++) {
                pos[i] = geometry.getPosition()[primitive[numberOfInputs * i]];
                norm[i] = normalData[primitive[numberOfInputs * i + 1]];
                uv[i] = uvData[primitive[numberOfInputs * i + 2]];
                indices[i] = i;

            }
            geometry.setPolylistIndicesBase(primitive);
            geometry.setPosition(pos);
            geometry.setNormal(norm);
            geometry.setTextureCoordinates(uv);
            geometry.setIndices(indices);
        } else {
            Log.w(TAG, "p is not set");
        }
        return geometry;
    }

    public int[] getPolylistIndicesBase() {
        return polylistIndicesBase;
    }

    private void setPolylistIndicesBase(int[] polylistIndicesBase) {
        this.polylistIndicesBase = polylistIndicesBase;
    }
}
