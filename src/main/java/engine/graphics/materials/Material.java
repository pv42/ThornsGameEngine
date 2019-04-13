package engine.graphics.materials;

public abstract class Material {
    private float reflectivity;
    private float shineDamper;
    private boolean wireframe;

    protected Material() {
        reflectivity = 0;
        shineDamper = 1;
    }

    public float getReflectivity() {
        return reflectivity;
    }

    public void setReflectivity(float reflectivity) {
        this.reflectivity = reflectivity;
    }

    public boolean isWireframe() {
        return wireframe;
    }

    public void setWireframe(boolean wireframe) {
        this.wireframe = wireframe;
    }

    public void setShineDamper(float shineDamper) {
        this.shineDamper = shineDamper;
    }

    public float getShineDamper() {
        return shineDamper;
    }
}
