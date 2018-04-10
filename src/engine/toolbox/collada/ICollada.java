package engine.toolbox.collada;

import java.util.Map;

public interface ICollada {
    ColladaAsset getColladaAsset();
    Map<String, ColladaImage> getImages();
    Map<String, ColladaMaterial> getMaterials();
    Map<String, ColladaEffect> getEffects();
    Map<String, ColladaGeometry> getGeometries();
    Map<String, ColladaController> getControllers();
    Map<String, ColladaVisualScene> getVisualScenes();
    Map<String, ColladaAnimation> getAnimations();
    Map<String, ColladaCamera> getCameras();
    ColladaVisualScene getScene();
}
