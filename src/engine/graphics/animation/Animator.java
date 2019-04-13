package engine.graphics.animation;

import java.util.List;

/**
 * the Animator is used to applies a {@link Animation} to a list of bones (typically from a raw model) at a given
 * timestamp
 *
 * @author pv42
 * @see Animation
 * @see engine.graphics.glglfwImplementation.models.GLRawModel
 */
public class Animator {
    /**
     * applies a {@link Animation} to a list of bones (typically from a raw model) at a given timestamp
     *
     * @param animation animation to apply
     * @param joints    list of joints to apply the animation to
     * @param time      timestamp of the animation to apply
     */
    public static void applyAnimation(Animation animation, List<Joint> joints, float time) {
        for (Joint joint : joints) {
            joint.setAnimationTransformationMatrix(animation.getMatrix(time, joint.getId()));
        }
    }
}
