package engine.graphics.particles;

import engine.graphics.cameras.Camera;
import engine.graphics.cameras.ThreeDimensionCamera;
import org.joml.Matrix4f;

import java.util.*;

/**
   Created by pv42 on 22.06.16.
 */
public class ParticleMaster {
    private static Map<ParticleTexture,List<Particle>> particles = new HashMap<>();
    private static ParticleRenderer renderer;
    public static void init( Matrix4f projectionMatrix) {
        renderer = new ParticleRenderer(projectionMatrix);
    }
    public static void update() {
        Iterator<Map.Entry<ParticleTexture, List<Particle>>> mapIterator = particles.entrySet().iterator();
        while (mapIterator.hasNext()) {
            List<Particle> list = mapIterator.next().getValue();
            Iterator<Particle> iterator = list.iterator();
            while (iterator.hasNext()) {
                Particle p = iterator.next();
                boolean stillAlive = p.update();
                if(!stillAlive) {
                    iterator.remove();
                    if(list.isEmpty()) {
                        mapIterator.remove();
                    }
                }
            }
        }
    }
    public static void renderParticles(Camera camera) {
        renderer.render(particles,camera);
    }
    public static void addParticle(Particle particle) {
        List<Particle> batch = particles.get(particle.getTexture());
        if(batch == null) {
            batch = new ArrayList<>();
            particles.put(particle.getTexture(),batch);
        }
        batch.add(particle);
    }
    public static void cleanUp() {
        renderer.cleanUp();
    }
}
