package engine.toolbox.assimpLoader;

import org.lwjgl.assimp.AIAnimation;

public class AssimpAnimation {
    private final String name;
    static AssimpAnimation load(AIAnimation aiAnimation) {
        AssimpAnimation animation = new AssimpAnimation(aiAnimation.mName().dataString());
        return animation;
    }

    private AssimpAnimation(String name) {
        this.name = name;
    }
}
