package engine.toolbox.collada;

import org.w3c.dom.Node;

import static engine.toolbox.collada.ColladaUtil.getAttribValue;

public class ColladaCamera extends ColladaPrimaryElement {

    private ColladaCamera(String id) {
        super();
        setId(id);
    }

    static ColladaCamera fromNode(Node node) {
        String id = getAttribValue(node,"id");
        return new ColladaCamera(id);
    }
}
