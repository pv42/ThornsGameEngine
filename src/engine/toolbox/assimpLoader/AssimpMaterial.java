package engine.toolbox.assimpLoader;

import engine.EngineMaster;
import engine.graphics.materials.Material;
import engine.graphics.materials.TexturedMaterial;
import engine.graphics.textures.Texture;
import engine.toolbox.Log;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMaterialProperty;
import org.lwjgl.assimp.Assimp;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class AssimpMaterial {

    private static final String TAG = "AssimpMaterial";

    private Vector4f ambient;
    private Vector4f diffuse;
    private Vector4f diffuseColor;
    private Vector4f specular;
    private Vector4f emissive;
    private Vector4f colorReflective;
    private float materialReflectivity;
    private float shininess;
    private float refractionIndex;
    private float opacity;
    private boolean wireframe;
    private boolean twoSided;
    private String name;
    private String textureFile;

    private AssimpMaterial() {
        ambient = new Vector4f(0.1f, 0.1f, 0.1f, 1);
        diffuse = new Vector4f(1);
        specular = new Vector4f(0.5f, 0.5f, 0.5f, 1);
        emissive = new Vector4f(0, 0, 0, 1);
        colorReflective = new Vector4f(0);
        materialReflectivity = 0;
        shininess = 0;
        refractionIndex = 1;
        opacity = 1;
        wireframe = false;
        twoSided = false;
    }

    static AssimpMaterial load(AIMaterial aiMaterial) {
        int numProp = aiMaterial.mNumProperties();
        AssimpMaterial material = new AssimpMaterial();
        for (int i = 0; i < numProp; i++) {
            AIMaterialProperty property = AIMaterialProperty.create(aiMaterial.mProperties().get(i));
            String key = property.mKey().dataString();
            int len = property.mDataLength();
            ByteBuffer data = property.mData();
            int type = property.mType();
            if (type == Assimp.aiPTI_String) {
                switch (key) {
                    case Assimp.AI_MATKEY_NAME:
                        material.setName(readString(data, len));
                        break;
                    case Assimp._AI_MATKEY_TEXTURE_BASE:
                        String texbase = readString(data, len);
                        if(texbase.equals("*0")){// blender
                            texbase ="white.png";
                        }
                        material.setTextureFile(texbase);
                        break;
                    default:
                        Log.w(TAG, "unknown material property: \"" + key + "\"");
                        System.out.println(readString(data, len));
                }
            } else if (type == Assimp.aiPTI_Float) {
                //System.out.println(key + " l =" + len);
                switch (key) {
                    case Assimp.AI_MATKEY_COLOR_AMBIENT:
                        material.setAmbient(readVector1or3or4f(data, len));
                        break;
                    case Assimp.AI_MATKEY_COLOR_DIFFUSE:
                        material.setDiffuse(readVector1or3or4f(data, len));
                        break;
                    case Assimp.AI_MATKEY_COLOR_SPECULAR:
                        material.setSpecular(readVector1or3or4f(data, len));
                        break;
                    case Assimp.AI_MATKEY_COLOR_EMISSIVE:
                        material.setEmissive(readVector1or3or4f(data, len));
                        break;
                    case Assimp.AI_MATKEY_COLOR_REFLECTIVE:
                        material.setColorReflective(readVector1or3or4f(data, len));
                        break;
                    case Assimp.AI_MATKEY_SHININESS:
                        material.setShininess(readFloat(data, len));
                        break;
                    case Assimp.AI_MATKEY_REFLECTIVITY:
                        material.setMaterialReflectivity(readFloat(data, len));
                        break;
                    case Assimp.AI_MATKEY_REFRACTI:
                        material.setRefractionIndex(readFloat(data, len));
                        break;
                    case Assimp.AI_MATKEY_OPACITY:
                        material.setOpacity(readFloat(data, len));
                        break;
                    case Assimp._AI_MATKEY_UVTRANSFORM_BASE: // i have no idea what this is
                    case Assimp._AI_MATKEY_TEXBLEND_BASE:
                    case Assimp.AI_MATKEY_BUMPSCALING:
                        break;
                    default:
                        if(key.startsWith("$mat.blend.")) {
                            //Log.d(TAG, "ignoring blender property " + key);
                        } else {
                            Log.w(TAG, "unknown material float property: \"" + key + "\"");
                            System.out.println(Arrays.toString(readFloatArray(data, len)));
                        }
                }
            } else if (type == Assimp.aiPTI_Integer) {
                switch (key) {
                    case Assimp.AI_MATKEY_ENABLE_WIREFRAME:
                        material.setWireframe(readBoolean(data, len));
                        break;
                    case Assimp.AI_MATKEY_TWOSIDED:
                        material.setTwoSided(readBoolean(data, len));
                        break;
                    case Assimp.AI_MATKEY_SHADING_MODEL: // ignore all these
                    case Assimp._AI_MATKEY_UVWSRC_BASE:
                    case Assimp._AI_MATKEY_MAPPINGMODE_U_BASE:
                    case Assimp._AI_MATKEY_MAPPINGMODE_V_BASE:
                        Log.d(TAG, "ignoring property: " + key);
                        break;
                    default:
                        if(key.startsWith("$mat.blend.")) {
                            Log.d(TAG, "ignoring blender property " + key);
                        } else {
                            Log.w(TAG, "unknown material int property: \"" + key + "\" + len=" + len);
                            System.out.println(data.get());
                        }
                }
            } else {
                Log.w(TAG, "unknown material typ/property: \"" + key + "\" + len=" + len);
                System.out.println(data.getInt());
            }
        }

        return material;
    }

    private static String readString(ByteBuffer buffer, int len) {
        StringBuilder string = new StringBuilder();
        int str_len = buffer.getInt();
        if (str_len != len - 5) Log.w(TAG, "string length mismatch (" + (len - 5) + " != " + str_len + ")");
        for (int i = 4; i < len - 1; i++) { // 4 byte len, 1 byte null terminator
            string.append((char) buffer.get());
        }
        return string.toString();
    }

    private static float[] readFloatArray(ByteBuffer buffer, int len) {
        if (len % 4 != 0) Log.w(TAG, "float data is not a multiple of 4 bytes");
        float[] data = new float[len / 4];
        for (int i = 0; i < len / 4; i++) {
            data[i] = buffer.getFloat();
        }
        return data;
    }

    // reads form float
    private static Vector4f readVector4f(ByteBuffer buffer, int len) {
        float[] data = readFloatArray(buffer, len);
        Vector4f vector;
        if (len >= 16) { // 4 * 4 since a float is 4 byte the vector has 4 floats
            vector = new Vector4f(data[0], data[1], data[2], data[3]);
            if (len > 16) Log.w(TAG, "material property vector is to long (16 bytes expected, got " + len + ")");
        } else {
            Log.w(TAG, "material property vector is to short (16 bytes expected, got " + len + ")");
            vector = new Vector4f();
            if (len >= 4) vector.x = data[0];
            if (len >= 8) vector.y = data[1];
            if (len >= 12) vector.z = data[2];
        }
        return vector;
    }

    private static Vector3f readVector3f(ByteBuffer buffer, int len) {
        float[] data = readFloatArray(buffer, len);
        Vector3f vector;
        if (len >= 12) { // 4 * 4 since a float is 4 byte the vector has 4 floats
            vector = new Vector3f(data[0], data[1], data[2]);
            if (len > 12) Log.w(TAG, "material property vector is to long (12 bytes expected, got " + len + ")");
        } else {
            Log.w(TAG, "material property vector is to short (12 bytes expected, got " + len + ")");
            vector = new Vector3f();
            if (len >= 4) vector.x = data[0];
            if (len >= 8) vector.y = data[1];
        }
        return vector;
    }

    private static float readFloat(ByteBuffer buffer, int len) {
        if (len != 4) Log.w(TAG, "material property vector is to short (4 bytes expected, got " + len + ")");
        return buffer.getFloat();
    }

    // read 3f and appends 1 or reads 4f or reads float and makes it to vector4f by setting x,y,z to the value and w to 1
    private static Vector4f readVector1or3or4f(ByteBuffer buffer, int len) {
        if (len == 4) {
            float f = readFloat(buffer, len);
            return new Vector4f(f,f,f,1);
        } else if (len == 12) {
            Vector3f vector3f = readVector3f(buffer, len);
            return new Vector4f(vector3f, 1);
        } else if (len == 16) {
            return readVector4f(buffer, len);
        } else {
            Log.w(TAG, "material property vector is of wrong length expected 4, 12 or 16");
            return new Vector4f(0,0,0,1);
        }
    }

    private static boolean readBoolean(ByteBuffer buffer, int len) {
        if(len == 4) {
            int v = buffer.getInt();
            switch (v){
                case 0: return false;
                case 1: return true;
                default: // still use c boolean interpretation but throw a warning
                    Log.w(TAG, "boolean is neither 0 nor 1 but " + v);
                    return true;
            }
        } else {
            Log.w(TAG, " boolean length is " + len + ", 4 expected");
            return false;
        }
    }

    public float getOpacity() {
        return opacity;
    }

    private void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    public Vector4f getAmbient() {
        return ambient;
    }

    private void setAmbient(Vector4f ambient) {
        this.ambient = ambient;
    }

    public Vector4f getDiffuse() {
        return diffuse;
    }

    private void setDiffuse(Vector4f diffuse) {
        this.diffuse = diffuse;
    }

    public Vector4f getSpecular() {
        return specular;
    }

    private void setSpecular(Vector4f specular) {
        this.specular = specular;
    }

    private void setEmissive(Vector4f emissive) {
        this.emissive = emissive;
    }

    private void setColorReflective(Vector4f reflective) {
        this.colorReflective = reflective;
    }

    private void setMaterialReflectivity(float materialReflectivity) {
        this.materialReflectivity = materialReflectivity;
    }

    private void setShininess(float shininess) {
        this.shininess = shininess;
    }

    private void setRefractionIndex(float refractionIndex) {
        this.refractionIndex = refractionIndex;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public String getTextureFile() {
        return textureFile;
    }

    private void setTextureFile(String textureFile) {
        this.textureFile = textureFile;
    }

    public Material getMaterial() {
        Texture texture = EngineMaster.getTextureLoader().loadTexture(getTextureFile());
        TexturedMaterial tm = new TexturedMaterial(texture);
        tm.setReflectivity(materialReflectivity);
        tm.setWireframe(wireframe);
        return tm;
    }

    private void setWireframe(boolean wireframe) {
        this.wireframe = wireframe;
    }

    private void setTwoSided(boolean twoSided) {
        this.twoSided = twoSided;
    }
}
