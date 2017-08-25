package engine.graphics.animation;

import java.util.List;

public class Animator {
    public static void applyAnimation(Animation animation, List<Joint> joints, float time) {
        for (Joint joint: joints) {
            joint.setPoseTransformationMatrix(animation.getMatrix(time, joint.getId()));
        }
    }
}
