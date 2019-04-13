package engine.graphics.particles;

import org.joml.Vector3f;

/**
 * Created by pv42 on 23.06.16.
 */
public class ParticleSystemStream extends ParticleSystem {
    private Vector3f spray;
    private Vector3f startPos,endPos;
    public ParticleSystemStream(ParticleTexture texture, float pps, float lifeLength, float scale, Vector3f startPos, Vector3f spray) {
        super(texture, pps, lifeLength, scale);
        this.startPos = startPos;
        this.spray = spray;
    }

    @Override
    void emitParticle(Vector3f target) {
        Vector3f ray = target.sub(startPos,new Vector3f());
        ray.x /= lifeLength;
        ray.y /= lifeLength;
        ray.z /= lifeLength;
        Vector3f sprayVector = new Vector3f((float) Math.random() * spray.x,(float) Math.random() * spray.y,(float) Math.random() * spray.z);
        Vector3f currentStartPos = startPos.add(sprayVector,new Vector3f());
        new Particle(getTexture(),currentStartPos,ray,0,getLifeLength(),0,getScale());
    }
    public void emitParticle(Vector3f source,Vector3f target) { //todo public
        Vector3f ray = target.sub(startPos,new Vector3f());
        ray.x /= lifeLength;
        ray.y /= lifeLength;
        ray.z /= lifeLength;
        Vector3f sprayVector = new Vector3f((float) Math.random() * spray.x,(float) Math.random() * spray.y,(float) Math.random() * spray.z);
        Vector3f currentStartPos = startPos.add(sprayVector,new Vector3f());
        new Particle(getTexture(),currentStartPos,ray,0,getLifeLength(),0,getScale());
    }
}
