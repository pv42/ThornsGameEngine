package engine.toolbox.nCollada.colladaLoader;

import engine.toolbox.Matrix4fDbg;
import engine.toolbox.nCollada.dataStructures.JointData;
import engine.toolbox.nCollada.dataStructures.SkeletonData;
import engine.toolbox.nCollada.xmlParser.XmlNode;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.List;

public class SkeletonLoader {

    private XmlNode armatureData;

    private List<String> boneOrder;

    private int jointCount = 0;

    private static final Matrix4fDbg CORRECTION = new Matrix4fDbg(new Matrix4f().rotate((float) Math.toRadians(-90), new Vector3f(1, 0, 0)), "COR");

    public SkeletonLoader(XmlNode visualSceneNode, List<String> boneOrder) {
        this.armatureData = visualSceneNode.getChild("visual_scene").getChildWithAttribute("node", "id", "Armature");
        this.boneOrder = boneOrder;
    }

    public SkeletonData extractBoneData() {
        XmlNode headNode = armatureData.getChild("node");
        JointData headJoint = loadJointData(headNode, true);

        return new SkeletonData(jointCount, headJoint);
    }

    private JointData loadJointData(XmlNode jointNode, boolean isRoot) {
        JointData joint = extractMainJointData(jointNode, isRoot);
        for (XmlNode childNode : jointNode.getChildren("node")) {
            joint.addChild(loadJointData(childNode, false));
        }
        return joint;
    }

    private JointData extractMainJointData(XmlNode jointNode, boolean isRoot) {
        String nameId = jointNode.getAttribute("id");
        int index = boneOrder.indexOf(nameId);
        String[] matrixData = jointNode.getChild("matrix").getData().split(" ");
        Matrix4fDbg matrix = new Matrix4fDbg(new Matrix4f(convertData(matrixData)), "");
        matrix.setName(nameId + ".ibm"); //ptm
        matrix.transpose();
        if (isRoot) {
            //because in Blender z is up, but in our game y is up.
            CORRECTION.mul(matrix, matrix);
        }
        jointCount++;
        for (int i = 0; i < 16; i++) {
            System.out.print(matrixData[i] + ",");
        }
        System.out.println();
        System.out.println();
        return new JointData(index, nameId, matrix);
    }

    private FloatBuffer convertData(String[] rawData) {
        float[] matrixData = new float[16];
        for (int i = 0; i < matrixData.length; i++) {
            matrixData[i] = Float.parseFloat(rawData[i]);
        }
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        buffer.put(matrixData);
        buffer.flip();
        return buffer;
    }

}
