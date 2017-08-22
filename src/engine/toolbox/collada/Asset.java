package engine.toolbox.collada;

import engine.toolbox.Log;

import org.w3c.dom.Node;
import java.util.HashMap;
import java.util.Map;

import static engine.toolbox.collada.ColladaUtil.getListFromNodeList;

/**
 * A COLLADA files assets similar to meta information
 */
public class Asset {
    private static final String TAG = "COLLADA:Asset";
    private Map<String,String> attributes = new HashMap<>();

    /**
     * creates assets from a collada files asset node
     * @param node asset node to read from
     * @throws IllegalArgumentException if the node given isn't an asset node
     * @return asset read from the node
     */
    public static Asset fromNode(Node node) {
        if(!node.getNodeName().equals("asset")) throw new IllegalArgumentException("Node given must be an asset node");
        Asset asset = new Asset();
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("created")) {
                asset.setAttribute("created", n.getTextContent());
            } else if (n.getNodeName().equals("modified")) {
                asset.setAttribute("modified", n.getTextContent());
            } else if (n.getNodeName().equals("unit")) {
                String unit = n.getAttributes().getNamedItem("name").getNodeValue();
                Float value = Float.valueOf(n.getAttributes().getNamedItem("meter").getNodeValue());
                asset.setAttribute("unitName", unit);
                asset.setAttribute("unitValue", value.toString());
            } else if (n.getNodeName().equals("up_axis")) {
                asset.setAttribute("up_axis", n.getTextContent());
            } else if (n.getNodeName().equals("contributor")) {
                asset.setAttribute("contributor", "<read failed>"); //todo
            } else if (!n.getNodeName().equals("#text")) {
                Log.d(TAG, " unkn_a:" + n.getNodeName());
            }
        }
        return asset;
    }

    /**
     * sets a attribute
     * @param name attributes name
     * @param value attributes value
     */
    public void setAttribute(String name, String value) {
        attributes.put(name, value);
    }

    /**
     * checks if a specific attribute is set
     * @param name name of the attribute to check
     * @return true if the attribute is set, false otherwise
     */
    public boolean hasAttribute(String name) {
        return attributes.containsKey(name);
    }

    /**
     * gets a attributes value
     * @param name attributes name
     * @return attributes value
     */
    public String getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public String toString() {
        return "AssetAttributes:" + attributes;
    }
}
