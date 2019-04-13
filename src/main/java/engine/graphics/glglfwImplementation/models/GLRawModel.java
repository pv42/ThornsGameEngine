package engine.graphics.glglfwImplementation.models;

import engine.graphics.animation.Joint;

import java.util.List;

/***
 * A reference to a openGL model loaded to VRAM (a vertex array object, vao), may also have joints for animation
 *
 * @author pv42
 * @see engine.graphics.glglfwImplementation.GLLoader
 */
public class GLRawModel {
    private final int vaoID;
    private final int vertexCount;
    private final List<Joint> joints;

    /**
     * creates a model without joints from a vertex array object and the number of vertices in the model
     *
     * @param vaoID       vertex array objects id
     * @param vertexCount the models number of vertices
     */
    public GLRawModel(int vaoID, int vertexCount) {
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
        this.joints = null;
    }

    /**
     * creates a model with joints (and so opportunity for animation) from a vertex array object and the number of
     * vertices in the model
     *
     * @param vaoID       vertex array objects id
     * @param vertexCount the models number of vertices
     * @param joints      a list of joints for the model
     */
    public GLRawModel(int vaoID, int vertexCount, List<Joint> joints) {
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
        this.joints = joints;
    }

    /**
     * gets the openGL vertex array object's id
     *
     * @return vao id
     */
    public final int getVaoID() {
        return vaoID;
    }

    /**
     * gets the number of vertices
     *
     * @return number of vertices
     */
    public final int getVertexCount() {
        return vertexCount;
    }

    /**
     * gets the list of joints of the model or {@code null} if the model was created without bones
     *
     * @return the list of joints or null
     */
    public List<Joint> getJoints() {
        return joints;
    }
}
