package engine.toolbox.collada;

import engine.toolbox.Log;
import org.w3c.dom.Node;

import java.util.Map;

import static engine.toolbox.collada.ColladaUtil.getAttribValue;
import static engine.toolbox.collada.ColladaUtil.getListFromNodeList;

/***
 * Created by pv42 on 03.08.16.
 */
public class ColladaMaterial extends ColladaPrimaryElement {
    private static final String TAG = "Collada:Material";
    private String instanceEffectId;

    public static ColladaMaterial fromNode(Node node) {
        if (node.getNodeName().equals("instance_material")) {
            Log.w(TAG,"not yet implemented"); //todo implement right
        }
        ColladaMaterial material = new ColladaMaterial();
        material.setId(getAttribValue(node,"id"));
        for (Node n : getListFromNodeList(node.getChildNodes())) {
            if (n.getNodeName().equals("instance_effect")) {
                material.setInstanceEffect(getAttribValue(n,"url").replaceFirst("#",""));
            } else if (!(n.getNodeName().equals("#text") || n.getNodeName().equals("extra"))) {
                Log.w(TAG, "unkn_rm:" + n.getNodeName());
            }
        }
        return material;
    }

    private void setInstanceEffect(String instanceEffectId) {
        this.instanceEffectId = instanceEffectId;
    }

    public ColladaEffect getInstanceEffect(Map<String,ColladaEffect> effectMap) {
        return effectMap.get(instanceEffectId);
    }



    @Override
    public String toString() {
        return "ColladaMaterial{" +
                "instanceEffectId='" + instanceEffectId + '\'' +
                ", id='" + getId() + '\'' +
                '}';
    }
}