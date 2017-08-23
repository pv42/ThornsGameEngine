package engine.toolbox.collada;


import engine.toolbox.Log;
import org.w3c.dom.Node;

import static engine.toolbox.collada.ColladaUtil.getAttribValue;
import static engine.toolbox.collada.ColladaUtil.getListFromNodeList;

/**
 * Created by pv42 on 03.08.16.
 */
public class ColladaEffect {
    private static final String TAG = "COLLADA:ColladaEffect";
    private String id;
    private String imageId;

    /**
     * loads a collada effect form an effect node
     * @param node node to load from
     * @return loaded effect
     */
    public static ColladaEffect fromNode(Node node) {
        if(node.getNodeName().equals("effect"));
        ColladaEffect colladaEffect = new ColladaEffect();
        colladaEffect.setId(getAttribValue(node,"id"));
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("profile_COMMON")) {
                for (Node n2 : getListFromNodeList(n.getChildNodes())) {
                    if (n2.getNodeName().equals("newparam")) {
                        readNewparam(n2, colladaEffect);
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
        return colladaEffect;
    }

    private static void readNewparam(Node node, ColladaEffect colladaEffect) {
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("surface")) {
                for (Node n2 : getListFromNodeList(n.getChildNodes())) {
                    if (n2.getNodeName().equals("init_from")) {
                        colladaEffect.setImage(n2.getTextContent());
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

    /**
     * gets the effect image
     * @return effects image
     */
    public String getImage() {
        return imageId;
    }

    private void setImage(String imageId) {
        this.imageId = imageId;
    }

    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }
}
