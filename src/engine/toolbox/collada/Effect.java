package engine.toolbox.collada;


import engine.toolbox.Log;
import org.w3c.dom.Node;

import static engine.toolbox.collada.ColladaUtil.getAttribValue;
import static engine.toolbox.collada.ColladaUtil.getListFromNodeList;

/**
 * Created by pv42 on 03.08.16.
 */
public class Effect {
    private static final String TAG = "COLLADA:Effect";
    private String id;
    private String imageId;

    public static Effect fromNode(Node node) {
        Effect effect = new Effect();
        effect.setId(getAttribValue(node,"id"));
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
    private static void readNewparam(Node node, Effect effect) {
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("surface")) {
                for (Node n2 : getListFromNodeList(n.getChildNodes())) {
                    if (n2.getNodeName().equals("init_from")) {
                        effect.setImage(n2.getTextContent());
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

    public String getImage() {
        return imageId;
    }

    public void setImage(String imageId) {
        this.imageId = imageId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
