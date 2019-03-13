package engine.graphics.particles;

import org.joml.Vector3f;

import engine.graphics.glglfwImplementation.display.GLFWDisplayManager;

public abstract class ParticleSystem {

    private float pps;
    private float scale;
    float lifeLength;
    private ParticleTexture texture;

    public ParticleSystem(ParticleTexture texture,float pps, float lifeLength,float scale) {
        this.texture = texture;
        this.pps = pps;
        this.lifeLength = lifeLength;
        this.scale = scale;
    }

    public void generateParticles(Vector3f systemCenter, float timeDelta){
        float particlesToCreate = pps * timeDelta;
        int count = (int) Math.floor(particlesToCreate);
        float partialParticle = particlesToCreate % 1;
        for(int i=0;i<count;i++){
            emitParticle(systemCenter);
        }
        if(Math.random() < partialParticle){
            emitParticle(systemCenter);
        }
    }
    abstract void emitParticle(Vector3f center);

    public float getPps() {
        return pps;
    }

    public float getScale() {
        return scale;
    }

    public float getLifeLength() {
        return lifeLength;
    }

    public ParticleTexture getTexture() {
        return texture;
    }
}