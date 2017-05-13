package engine.graphics.particles;

import org.lwjgl.util.vector.Vector3f;

/**
 * Created by pv42 on 23.06.16.
 */
public class ParticleSystem1 extends ParticleSystem{


    private float speed;
    private float gravityComplient;
    private ParticleTexture texture;

    public ParticleSystem1(ParticleTexture texture, float pps, float speed, float gravityComplient, float lifeLength, float scale) {
        super(texture,pps,lifeLength,scale);
        this.texture = texture;

        this.speed = speed;
        this.gravityComplient = gravityComplient;

    }

    @Override
    void emitParticle(Vector3f center){
        float dirX = (float) Math.random() * 2f - 1f;
        float dirZ = (float) Math.random() * 2f - 1f;
        Vector3f velocity = new Vector3f(dirX/1.2f, 1, dirZ/1.2f);
        velocity.normalise();
        velocity.scale(speed);
        new Particle(texture,new Vector3f(center), velocity, gravityComplient, lifeLength, 0, getScale());
    }



}
