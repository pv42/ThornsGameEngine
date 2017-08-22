package engine.toolbox.collada;

import engine.toolbox.Log;
import org.joml.Matrix4f;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class ColladaUtil {
    private static final String TAG = "COLLADA:Util";

    private ColladaUtil(){}

    static List<Node> getListFromNodeList(NodeList nodeList) {
        List<Node> list = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            list.add(nodeList.item(i));
        }
        return list;
    }

    static String getAttribValue(Node node, String key) {
        return node.getAttributes().getNamedItem(key).getNodeValue();
    }

    static int[] readIntArray(Node node) {
        int[] array = new int[node.getTextContent().split(" ").length];
        int i = 0;
        for (String s : node.getTextContent().split(" ")) {
            array[i] = Integer.valueOf(s);
            i++;
        }
        return array;
    }

    static float[] readFloatArray(Node node) {
        float[] array = new float[node.getTextContent().split(" ").length];
        int i = 0;
        for (String s : node.getTextContent().split(" ")) {
            array[i] = Float.valueOf(s);
            i++;
        }
        return array;
    }

    static Matrix4f readMatrix4f(Node node) {
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

    static Source readSource(Node node) {
        assert node==null;
        List<Node> dataSources = new ArrayList<>();// e.g. float_arrays
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("technique_common")) {
                for (Node n2 : getListFromNodeList(n.getChildNodes())) {
                    if (n2.getNodeName().equals("accessor")) {
                        return readAccessor(n2,dataSources); //todo !!
                    } else if (!n2.getNodeName().equals("#text")) {
                        Log.w(TAG, "unkn_S:" + n.getNodeName());
                    }
                }
            } else if (n.getNodeName().equals("float_array") || n.getNodeName().equals("Name_array")) {
                dataSources.add(n);
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_S:" + n.getNodeName());
            }
        }
        Log.e(TAG, "no technique_common found");
        return null;

    }

    private static Source readAccessor(Node node,List<Node> dataSources) {
        String source = node.getAttributes().getNamedItem("source").getNodeValue().replaceFirst("#","");
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
        Node dataSource = null;
        for (Node n: dataSources) {
            if(n.getAttributes().getNamedItem("id").getNodeValue().equals(source)) dataSource = n;
            //todo not so stupid loops
        }
        String[] rawData = dataSource.getTextContent().split(" ");
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

    private static Param readParam(Node node) {
        return new Param(node.getAttributes().getNamedItem("name").getNodeValue(), node.getAttributes().getNamedItem("type").getNodeValue());
    }
}
