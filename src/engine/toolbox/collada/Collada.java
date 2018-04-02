package engine.toolbox.collada;

import engine.graphics.animation.Animation;
import engine.graphics.animation.Joint;
import engine.graphics.animation.KeyFrame;
import engine.graphics.models.RawModel;
import engine.graphics.models.TexturedModel;
import engine.graphics.renderEngine.Loader;
import engine.graphics.textures.ModelTexture;
import engine.toolbox.Log;
import engine.toolbox.StorageFormatUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Collada implements ICollada{
    private static final String TAG = "Collada";
    private ColladaAsset colladaAsset;
    private Map<String, ColladaImage> images;
    private Map<String, ColladaMaterial> materials;
    private Map<String, ColladaEffect> effects;
    private Map<String, ColladaGeometry> geometries;
    private Map<String, ColladaController> controllers;
    private Map<String, ColladaVisualScene> visualScenes;
    private Map<String, ColladaAnimation> animations;
    private Map<String, ColladaCamera> cameras;
    private ColladaVisualScene scene;

    public ColladaAsset getColladaAsset() {
        return colladaAsset;
    }

    public Map<String, ColladaImage> getImages() {
        return images;
    }

    public Map<String, ColladaMaterial> getMaterials() {
        return materials;
    }

    public Map<String, ColladaEffect> getEffects() {
        return effects;
    }

    public Map<String, ColladaGeometry> getGeometries() {
        return geometries;
    }

    public Map<String, ColladaController> getControllers() {
        return controllers;
    }

    public Map<String, ColladaVisualScene> getVisualScenes() {
        return visualScenes;
    }

    public Map<String, ColladaAnimation> getAnimations() {
        return animations;
    }

    public Map<String, ColladaCamera> getCameras() {
        return cameras;
    }

    public ColladaVisualScene getScene() {
        return scene;
    }

    void setAnimations(Map<String, ColladaAnimation> animations) {
        this.animations = animations;
    }

    void setColladaAsset(ColladaAsset colladaAsset) {
        this.colladaAsset = colladaAsset;
    }

    void setImages(Map<String, ColladaImage> images) {
        this.images = images;
    }

    void setMaterials(Map<String, ColladaMaterial> materials) {
        this.materials = materials;
    }

    void setEffects(Map<String, ColladaEffect> effects) {
        this.effects = effects;
    }

    void setGeometries(Map<String, ColladaGeometry> geometries) {
        this.geometries = geometries;
    }

    void setControllers(Map<String, ColladaController> controllers) {
        this.controllers = controllers;
    }

    void setVisualScenes(Map<String, ColladaVisualScene> visualScenes) {
        this.visualScenes = visualScenes;
    }

    void setScene(ColladaVisualScene scene) {
        this.scene = scene;
    }

    void setCameras(Map<String,ColladaCamera> cameras) {
        this.cameras = cameras;
    }

    public List<TexturedModel> getTexturedModels() {
        Log.d(TAG, "loading data to VRAM");
        List<TexturedModel> models = new ArrayList<>();
        for (int i = 0; i < scene.getRootNodes().size(); i++) { //for models
            String nodeId = scene.getRootNodes().get(i);
            ColladaNode cnode = scene.getNode(nodeId);
            ColladaVisualScene.ColladaInstanceController ic = cnode.getInstanceController();
            if (ic != null) {
                ColladaController controller = controllers.get(ic.getUrl());
                ColladaNode skeletonRoot = scene.getNode(ic.getSkeleton());
                Map<String, Joint> joints = controller.getJoints();
                processNode(skeletonRoot, joints, null); //apply poses to joints
                ColladaGeometry geometry = geometries.get(controller.getGeometryId());
                RawModel model = Loader.loadToVAOAnimated(
                        StorageFormatUtil.get1DArray(geometry.getPosition()),
                        StorageFormatUtil.get1DArray(geometry.getTextureCoordinates()),
                        StorageFormatUtil.get1DArray(geometry.getNormal()),
                        geometry.getIndices(),
                        controller.getWeights().getIndicesData(geometry.getPolylistIndicesBase()),
                        controller.getWeights().getWeightsData(geometry.getPolylistIndicesBase()),
                        controller.getJointList());
                String materialId = geometry.getMaterialId();
                materialId = ic.getBindMaterialId(materialId);
                ColladaEffect effect = materials.get(materialId).getInstanceEffect(effects);
                String imageFile = images.get(effect.getImage()).getSource();
                TexturedModel texturedModel = new TexturedModel(model, new ModelTexture(Loader.loadTexture(
                        imageFile.replaceFirst("file:///",""))));
                models.add(texturedModel);
            }
        }
        return models;
    }

    private void processNode(ColladaNode node, Map<String, Joint> joints, Joint parent) {
        Joint joint = joints.get(node.getSid());
        joint.setPoseTransformationMatrix(node.getTransformation());
        joint.setParent(parent);
        for (ColladaNode n : node.getChildren()) {
            processNode(n, joints, joint);
        }
    }

    public Animation getAnimation() {
        Animation animation = new Animation();
        int index = 0;

        for (float time : animations.values().iterator().next().getKeyFrameTimes()) {
            KeyFrame keyFrame = new KeyFrame(time);
            for(ColladaAnimation colladaAnimation : animations.values()) {
                keyFrame.addJointData(colladaAnimation.getTarget(), colladaAnimation.getKeyFrameMatrices()[index]);
            }
            index++;
            animation.addKeyFrame(keyFrame);
        }
        return animation;
    }
}
