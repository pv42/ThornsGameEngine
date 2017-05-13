package shivt.guns;

import engine.graphics.models.MMTexturedModel;
import engine.graphics.models.OBJLoader;
import engine.graphics.models.RawModel;
import engine.graphics.normalMappingObjConverter.NormalMappedObjLoader;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import engine.graphics.renderEngine.Loader;
import engine.graphics.textures.ModelTexture;
import engine.toolbox.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/***
 * Created by pv42 on 01.07.16.
 */



public class GunLoader {
    private ModelTexture texture = null;
    private String[] modelNames = null;
    private MMTexturedModel model = null;
    private float rx=0,ry =0,rz =0;
    private float scale =1;
    private Vector3f nso = new Vector3f(),so =new Vector3f();
    private Vector2f sto = null,stsc = null;
    private int guiTexture = -1;
    private float reloadDelay =0,shotDelay=0, knockback =0;
    private int magazinSize = 20;
    private float projectileSpeed = 100f;
    private int normalMap = -1;
    private int specMap = -1;

    public GunLoader (String gunConfigFile) {
        try {
            FileReader fr = new FileReader("res/guns/" + gunConfigFile + ".gun");
            BufferedReader br = new BufferedReader(fr);
            String line  = br.readLine();
            while (!line.startsWith("end")) {
                String attrib = line.split(":")[0];
                String attribValue = line.split(":")[1];
                switch (attrib) {
                    case "mesh": modelNames = readArray(attribValue);
                        break;
                    case "texture": texture =  new ModelTexture(Loader.loadTexture(attribValue));
                        break;
                    case "rotation": Vector3f rot = readVec3(attribValue);
                        rx = rot.x;
                        ry = rot.y;
                        rz = rot.z;
                        break;
                    case "scale": scale = Float.valueOf(attribValue);
                        break;
                    case "noscopeoffset": nso = readVec3(attribValue);
                        break;
                    case "scopeoffset": so = readVec3(attribValue);
                        break;
                    case "scopetexture": guiTexture = Loader.loadTexture(attribValue);
                        break;
                    case "scopetexturepos": sto = readVec2(attribValue);
                        break;
                    case "scopetexturescale": stsc = readVec2(attribValue);
                        break;
                    case "reloadDelay": reloadDelay = Float.valueOf(attribValue);
                        break;
                    case "magizineSize": magazinSize = Integer.valueOf(attribValue);
                        break;
                    case "shotDelay": shotDelay = Float.valueOf(attribValue);
                        break;
                    case "knockback": knockback = Float.valueOf(attribValue);
                        break;
                    case "projectilespeed": projectileSpeed = Float.valueOf(attribValue);
                        break;
                    case "name" : break; //// TODO: 25.06.16 implement gun name!
                    case "normalMap" : //normalMap = Loader.loadTexture(attribValue); //// TODO: 16.08.16 enable feature (causes error in KSR29)
                        break;
                    case "specMap": //specMap = Loader.loadTexture(attribValue);
                        break;
                    default: Log.w("unsupported gun attrib: " + attrib);
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            Log.e("failed reading gunfile");
            e.printStackTrace();
        }
        if (modelNames == null || texture == null) Log.e("model or texture not set");
        if(normalMap != -1) {

            List<RawModel> models = new ArrayList<>();
            for(String modelName:modelNames) {
                models.add(NormalMappedObjLoader.loadOBJ(modelName));
            }
            model = new MMTexturedModel(models,texture);
            if(specMap != -1) {
                texture.setSpecularMapID(specMap);
                texture.setShineDamper(10);
                texture.setReflectivity(0.6f);
            }
        } else {
            List<RawModel> models = new ArrayList<>();
            for(String modelName:modelNames) {
                models.add(OBJLoader.loadObjModel(modelName));
            }
            model = new MMTexturedModel(models,texture);
        }
        texture.setNormalMapID(normalMap);

    }
    private static Vector3f readVec3(String data) {
        String [] datas = data.split(",");
        Vector3f v = new Vector3f(Float.valueOf(datas[0]),Float.valueOf(datas[1]),Float.valueOf(datas[2]));
        return v;
    }

    private static Vector2f readVec2(String data) {
        String [] datas = data.split(",");
        Vector2f v = new Vector2f(Float.valueOf(datas[0]),Float.valueOf(datas[1]));
        return v;
    }
    private static String[] readArray(String data) {
        String [] datas = data.split(",");
        return datas;
    }

    public ModelTexture getTexture() {
        return texture;
    }


    public MMTexturedModel getMMTmodel() {
        return model;
    }

    public float getOffsetRx() {
        return rx;
    }

    public float getOffsetRy() {
        return ry;
    }

    public float getOffsetRz() {
        return rz;
    }

    public float getScale() {
        return scale;
    }

    public Vector3f getOffsetPosition() {
        return nso;
    }

    public Vector3f getScopeOffsetPosition() {
        return so;
    }

    public Vector2f getGuiTexturePosition() {
        return sto;
    }

    public Vector2f getScopeTextureScale() {
        return stsc;
    }

    public int getGuiTexture() {
        return guiTexture;
    }

    public float getReloadDelay() {
        return reloadDelay;
    }

    public float getShotDelay() {
        return shotDelay;
    }

    public float getKnockback() {
        return knockback;
    }

    public int getMagazineSize() {
        return magazinSize;
    }

    public float getProjectileSpeed() {
        return projectileSpeed;
    }

    public int getNormalMap() {
        return normalMap;
    }

    public int getSpecMap() {
        return specMap;
    }
}
