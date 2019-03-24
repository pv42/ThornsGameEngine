package engine.toolbox.nCollada.colladaLoader;

import engine.toolbox.nCollada.dataStructures.AnimatedModelData;
import engine.toolbox.nCollada.dataStructures.AnimationData;
import engine.toolbox.nCollada.dataStructures.MeshData;
import engine.toolbox.nCollada.dataStructures.SkeletonData;
import engine.toolbox.nCollada.dataStructures.SkinningData;
import engine.toolbox.nCollada.xmlParser.XmlNode;
import engine.toolbox.nCollada.xmlParser.XmlParser;

import java.io.File;

public class ColladaLoader {

    public static AnimatedModelData loadColladaModel(File colladaFile, int maxWeights) {
        XmlNode node = XmlParser.loadXmlFile(colladaFile);

        SkinLoader skinLoader = new SkinLoader(node.getChild("library_controllers"), maxWeights);
        SkinningData skinningData = skinLoader.extractSkinData();

        SkeletonLoader jointsLoader = new SkeletonLoader(node.getChild("library_visual_scenes"), skinningData.jointOrder);
        SkeletonData jointsData = jointsLoader.extractBoneData();

        GeometryLoader g = new GeometryLoader(node.getChild("library_geometries"), skinningData.verticesSkinData);
        MeshData meshData = g.extractModelData();

        return new AnimatedModelData(meshData, jointsData);
    }

    public static AnimationData loadColladaAnimation(File colladaFile) {
        XmlNode node = XmlParser.loadXmlFile(colladaFile);
        XmlNode animNode = node.getChild("library_animations");
        XmlNode jointsNode = node.getChild("library_visual_scenes");
        AnimationLoader loader = new AnimationLoader(animNode, jointsNode);
        AnimationData animData = loader.extractAnimation();
        return animData;
    }

}
