package engine.toolbox.collada;

import engine.toolbox.Log;
import org.w3c.dom.Node;

import static engine.toolbox.collada.ColladaUtil.getListFromNodeList;

public class ColladaImage extends ColladaPrimaryElement{
    private static final String TAG = "COLLADA:Image";
    private String name;
    private String source;

    /**
     * loads a collada image form an image node
     * @param node node to load from
     * @return loaded image
     */
    static ColladaImage formNode(Node node) {
        if(!node.getNodeName().equals("image")) throw new IllegalArgumentException("Node given must be an image node");
        ColladaImage image = new ColladaImage();
        image.setId(node.getAttributes().getNamedItem("id").getNodeValue());
        if(node.getAttributes().getNamedItem("name") != null) {
            image.setName(node.getAttributes().getNamedItem("name").getNodeValue());
        }
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("init_from")) {
                image.setSource(n.getTextContent());//.replaceFirst("file:///", "");
            } else if (!n.getNodeName().equals("#text")) {
                Log.w(TAG, "unkn_I:" + n.getNodeName());
            }
        }
        if(image.getSource() == null) Log.e(TAG, "no init_from found");
        return image;
    }

    /**
     * gets images name
     * @return images name
     */
    public String getName() {
        return name;
    }

    /**
     * gets image source determent through the init_from tag
     * @return images source
     */
    public String getSource() {
        return source;
    }

    private void setSource(String source) {
        this.source = source;
    }

    private void setName(String name) {
        this.name = name;
    }
}