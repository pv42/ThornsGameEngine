package engine.toolbox.collada;

/***
 * Created by pv42 on 03.08.16.
 */
public class Material {
    private Effect instanceEffect = null;

    public void setInstanceEffect(Effect instanceEffect) {
        this.instanceEffect = instanceEffect;
    }

    public Effect getInstanceEffect() {
        return instanceEffect;
    }
}
