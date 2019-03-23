package engine.graphics.glglfwImplementation.entities;import engine.graphics.animation.Animation;import engine.graphics.Entity;import engine.graphics.glglfwImplementation.models.GLTexturedModel;import org.joml.Vector3f;import java.util.ArrayList;import java.util.List;/** Created by pv42 on 19.06.16. recreated bc lost data on 23.06.16 */public class GLEntity implements Entity {    private List<GLTexturedModel> models;    private Vector3f position;    private Vector3f rotation;    private float scale;    private float textureOffsetX = 0; //// TODO: 23.06.16 !texatlases    private float textureOffsetY = 0;    private int textureIndex = 0;    private List<Animation> animations;    /**     * creates an GLEntity     * @param model TexturedModel to use     * @param position models position     */    public GLEntity(GLTexturedModel model, Vector3f position) {        models = new ArrayList<>();        this.models.add(model);        this.position = new Vector3f(position);        this.rotation = new Vector3f();        this.scale = 1;    }    /**     * creates an GLEntity     * @param models TexturedModels to use     * @param position models position     */    public GLEntity(List<GLTexturedModel> models, Vector3f position) {        this.models =  models;        this.position = new Vector3f(position);        this.rotation = new Vector3f();        this.scale = 1;    }    /**     * creates an GLEntity     * @param model GLTexturedModel to use     * @param textureIndex texture to use id texture is indexed     * @param position models position     */    public GLEntity(GLTexturedModel model, int textureIndex, Vector3f position) {        models = new ArrayList<>();        this.models.add( model);        this.textureIndex = textureIndex;        this.position = new Vector3f(position);        this.rotation = new Vector3f();        this.scale = 1;    }    public GLEntity(GLTexturedModel model, Vector3f position, int rx, int ry, int rz, int i3) {        this(model, position);        this.rotation.x = rx;        this.rotation.y = ry;        this.rotation.z = rz;    }    public float getTextureXOffset(int i){        int col = textureIndex%models.get(i).getTexture().getNumberOfRows();        return (float)col/(float)models.get(i).getTexture().getNumberOfRows();    }    public float getTextureYOffset(int i){        int row = textureIndex/models.get(i).getTexture().getNumberOfRows();        return (float)row/(float)models.get(i).getTexture().getNumberOfRows();    }    public float getTextureXOffset() {        return getTextureXOffset(0);    }    public float getTextureYOffset() {        return getTextureYOffset(0);    }    public List<GLTexturedModel> getModels() {        return models;    }    /**     * return entity's euler x rotation     * @return the entity's euler x rotation     */    public float getRx() {        return rotation.x();    }    public void setRx(float rx) {        this.rotation.x = rx;    }    /**     * return entity's euler y rotation     * @return the entity's euler y rotation     */    public float getRy() {        return rotation.y;    }    public void setRy(float ry) {        this.rotation.y = ry;    }    public float getRz() {        return rotation.z;    }    public void setRz(float rz) {        this.rotation.z = rz;    }    /**     * returns the entity's scale     * @return the entity's scale     */    public float getScale() {        return scale;    }    public void setScale(float scale) {        this.scale = scale;    }    public Vector3f getPosition() {        return position;    }    public void setPosition(Vector3f position) {        this.position = position;    }    public void setPosition(float x, float y, float z) {        position.x = x;        position.y = y;        position.z = z;    }    public void increasePosition(float dx, float dy, float dz) {        position.x += dx;        position.y += dy;        position.z += dz;    }    public void setPositionElement(float value, int index) {        switch (index) {            case 0: position.x = value;                break;            case 1: position.y = value;                break;            case 2: position.z = value;                break;            default: throw new IndexOutOfBoundsException("index has to be between 0 and 2");        }    }    public void increaseRotation(float drx, float dry, float drz) {        rotation.x += drx;        rotation.y += dry;        rotation.z += drz;    }    public void increaseRotation(Vector3f dr) {        rotation.add(dr);    }    /**     * scales the entity relative to its current scale     * @param factor factor to scale the entity     */    public void scale(float factor) {        scale *= factor;    }}