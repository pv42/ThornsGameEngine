package engine.graphics.models;

import engine.graphics.animation.Joint;
import org.joml.Vector2f;
import org.joml.Vector3f;
import engine.graphics.renderEngine.Loader;
import engine.toolbox.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pv42 on 17.06.16.
 */
//TODO download improoved fx! (tut16)
public class OBJLoader {
    public static RawModel loadObjModel(String filename) {
        FileReader fr = null;
         try {
             fr = new FileReader(new File("res/meshs/" + filename + ".obj"));
         } catch (IOException e ){
             Log.e("couldn't read OBJ '" + filename + "'");
             e.printStackTrace();
         }
        BufferedReader reader = new BufferedReader(fr);
        String line;
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> uvs = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        float[] verticesArray = null;
        float[] normalsArray = null;
        float[] textureArray = null;
        int[] indicesArray = null;
        int i = 0;
        try {
            while (true) {
                i++;
                line = reader.readLine();
                String[] currentLine = line.split(" ");
                if(line.startsWith("v ")) {
                    Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]),Float.parseFloat(currentLine[2]),Float.parseFloat(currentLine[3]));
                    vertices.add(vertex);
                } else if(line.startsWith("vt ")) {
                    Vector2f uv = new Vector2f(Float.parseFloat(currentLine[1]),Float.parseFloat(currentLine[2]));
                    uvs.add(uv);
                } else if(line.startsWith("vn ")) {
                    Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]),Float.parseFloat(currentLine[2]),Float.parseFloat(currentLine[3]));
                    normals.add(normal);
                } else if(line.startsWith("f ")) {
                    textureArray = new float[vertices.size() * 2];
                    normalsArray = new float[vertices.size() * 3];
                    break;
                }

            }
            while (line != null) {
                i++;
                if(!line.startsWith("f ")) {
                    line = reader.readLine();
                    continue;
                }
                String[] currentLine = line.split(" ");
                String[] vertex1 = currentLine[1].split("/");
                String[] vertex2 = currentLine[2].split("/");
                String[] vertex3 = currentLine[3].split("/");
                processVertx(vertex1,indices,uvs,normals,textureArray,normalsArray);
                processVertx(vertex2,indices,uvs,normals,textureArray,normalsArray);
                processVertx(vertex3,indices,uvs,normals,textureArray,normalsArray);
                line = reader.readLine();
            }
            reader.close();
        }catch (Exception e) {
            Log.e("couldn't read OBJ '" + filename + "' (line:" + i + ")");
            e.printStackTrace();
        }
        verticesArray = new float[vertices.size() * 3];
        indicesArray = new int[indices.size()];
        int pointer = 0;
        for(Vector3f vertex: vertices) {
            verticesArray[pointer++] = vertex.x();
            verticesArray[pointer++] = vertex.y();
            verticesArray[pointer++] = vertex.z();
        }
        pointer = 0;
        for(int index : indices) {
            indicesArray[pointer++] = index;
        }
        return Loader.loadToVAO(verticesArray,textureArray,normalsArray,indicesArray);
    }
    @Deprecated
    public static RawModel loadObjModelAnimated(String filename, List<Joint> joints) {
        FileReader fr = null;
        try {
            fr = new FileReader(new File("res/meshs/" + filename + ".obj"));
        } catch (IOException e ){
            Log.e("couldn't read OBJ '" + filename + "'");
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(fr);
        String line;
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> uvs = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        List<Vector2f> boneIndices = new ArrayList<>();
        List<Vector2f> boneWeights = new ArrayList<>();
        float[] verticesArray = null;
        float[] normalsArray = null;
        float[] textureArray = null;
        int[] boneIndicesArray = null;
        float[] boneWeightArray = null;
        int[] indicesArray = null;
        int i = 0;
        try {
            while (true) {
                i++;
                line = reader.readLine();
                String[] currentLine = line.split(" ");
                if(line.startsWith("v ")) {
                    Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]),Float.parseFloat(currentLine[2]),Float.parseFloat(currentLine[3]));
                    vertices.add(vertex);
                } else if(line.startsWith("vt ")) {
                    Vector2f uv = new Vector2f(Float.parseFloat(currentLine[1]),Float.parseFloat(currentLine[2]));
                    uvs.add(uv);
                } else if(line.startsWith("vn ")) {
                    Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]),Float.parseFloat(currentLine[2]),Float.parseFloat(currentLine[3]));
                    normals.add(normal);
                } else if(line.startsWith("bw ")) {
                    Vector2f boneWeight = new Vector2f(Float.parseFloat(currentLine[1]),Float.parseFloat(currentLine[2]));
                    boneWeights.add(boneWeight);
                } else if(line.startsWith("bi ")) {
                    Vector2f boneIndice = new Vector2f(Float.parseFloat(currentLine[1]),Float.parseFloat(currentLine[2]));
                    boneIndices.add(boneIndice);
                } else if(line.startsWith("f ")) {
                    textureArray = new float[vertices.size() * 2];
                    normalsArray = new float[vertices.size() * 3];
                    boneIndicesArray = new int[vertices.size() * 2];
                    boneWeightArray = new float[vertices.size() * 2];
                    break;
                }
            }
            while (line != null) {
                i++;
                if(!line.startsWith("f ")) {
                    line = reader.readLine();
                    continue;
                }
                String[] currentLine = line.split(" ");
                String[] vertex1 = currentLine[1].split("/");
                String[] vertex2 = currentLine[2].split("/");
                String[] vertex3 = currentLine[3].split("/");
                processVertxAnimated(vertex1,indices,uvs,normals,boneIndices,boneWeights,textureArray,normalsArray,boneIndicesArray,boneWeightArray);
                processVertxAnimated(vertex2,indices,uvs,normals,boneIndices,boneWeights,textureArray,normalsArray,boneIndicesArray,boneWeightArray);
                processVertxAnimated(vertex3,indices,uvs,normals,boneIndices,boneWeights,textureArray,normalsArray,boneIndicesArray,boneWeightArray);
                line = reader.readLine();
            }
            reader.close();
        }catch (Exception e) {
            Log.e("couldn't read OBJ '" + filename + "' (line:" + i + ")");
            e.printStackTrace();
        }
        verticesArray = new float[vertices.size() * 3];
        indicesArray = new int[indices.size()];
        int pointer = 0;
        for(Vector3f vertex: vertices) {
            verticesArray[pointer++] = vertex.x();
            verticesArray[pointer++] = vertex.y();
            verticesArray[pointer++] = vertex.z();
        }
        pointer = 0;
        for(int index : indices) {
            indicesArray[pointer++] = index;
        }
        return Loader.loadToVAOAnimated(verticesArray,textureArray,normalsArray,indicesArray,boneIndicesArray,boneWeightArray, joints);
    }
    private static void processVertx(String[] vertxData, List<Integer> indices, List<Vector2f> textures, List<Vector3f> normals, float[] textureArray,float[] normalsArray) {
        int currentVertexPointer = Integer.parseInt(vertxData[0]) - 1;
        indices.add(currentVertexPointer);
        Vector2f currentTexture = textures.get(Integer.parseInt(vertxData[1]) - 1);
        textureArray[currentVertexPointer * 2] = currentTexture.x();
        textureArray[currentVertexPointer * 2 + 1] = currentTexture.y();
        if(vertxData.length == 3) {
            Vector3f currentNormal = normals.get(Integer.parseInt(vertxData[2]) - 1);
            normalsArray[currentVertexPointer * 3] = currentNormal.x();
            normalsArray[currentVertexPointer * 3 + 1] = currentNormal.y();
            normalsArray[currentVertexPointer * 3 + 2] = currentNormal.z();
        } else {
            // no normal data in obj
        }
    }
    private static void processVertxAnimated(String[] vertxData, List<Integer> indices, List<Vector2f> textures, List<Vector3f> normals,List<Vector2f> boneIndices, List<Vector2f> boneWeights,
                                             float[] textureArray,float[] normalsArray, int[] boneIndicesArray, float[] boneWeightArray) {
        int currentVertexPointer = Integer.parseInt(vertxData[0]) - 1;
        indices.add(currentVertexPointer);
        Vector2f currentTexture = textures.get(Integer.parseInt(vertxData[1]) - 1);
        textureArray[currentVertexPointer * 2] = currentTexture.x();
        textureArray[currentVertexPointer * 2 + 1] = currentTexture.y();
        boneIndicesArray[currentVertexPointer * 2] = (int) boneIndices.get(currentVertexPointer).x;
        boneIndicesArray[currentVertexPointer * 2 + 1] = (int) boneIndices.get(currentVertexPointer).y;
        boneWeightArray[currentVertexPointer * 2] = boneWeights.get(currentVertexPointer).x;
        boneWeightArray[currentVertexPointer * 2 + 1] = boneIndices.get(currentVertexPointer).y;
        if(vertxData.length == 3) {
            Vector3f currentNormal = normals.get(Integer.parseInt(vertxData[2]) - 1);
            normalsArray[currentVertexPointer * 3] = currentNormal.x();
            normalsArray[currentVertexPointer * 3 + 1] = currentNormal.y();
            normalsArray[currentVertexPointer * 3 + 2] = currentNormal.z();
        } else {
            // no normal data in obj
        }
    }
}
