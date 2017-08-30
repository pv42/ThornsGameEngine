package shivt.guns;

/**
 * Created by pv42 on 03.07.16.
 */

import engine.audio.AudioMaster;
import engine.audio.OggData;
import engine.audio.Source;
import engine.graphics.entities.Player;
import org.joml.Vector3f;
import engine.graphics.particles.Particle;
import engine.graphics.particles.ParticleTexture;
import engine.graphics.display.DisplayManager;
import engine.graphics.renderEngine.Loader;


/**
 * Created by pv42 on 01.07.16.
 */
public class MP5 extends Gun {
    private OggData soundBuffer;
    private Source soundSource;
    public MP5() {
        super(new GunLoader("MP5"));
        soundBuffer = AudioMaster.loadSound("KSR-29");
        soundSource = new Source();
    }
    public boolean shot(Player player) {
        if(shotCooldown > 0) return false;
        if(remainingAmmo <= 0) return false;
        if(reloadCooldown > 0) return false;
        remainingAmmo --;
        shotCooldown = getShotDelay();
        new Particle(new ParticleTexture(Loader.loadTexture("boom"),5,true,false),
                player.getEyePosition().add(getMuzzlePosition(),new Vector3f()),
                new Vector3f(getProjectileSpeed(),0,0),0,10f,0,2);
        //todo direction
        soundSource.play(soundBuffer);
        return true;
    }
    @Override
    public void update(Player player) {
        int sco = -1;
        /*if(Mouse.isButtonDown(1)) {
            sco = 1;
        }*///// TODO: 12.09.2016 m
        setScopingProgress(Math.max(Math.min(getScopingProgress() + sco / getScopingTime() * DisplayManager.getFrameTimeSeconds(),1),0));
        float sin = (float) Math.sin(Math.toRadians(player.getRy()));
        float cos = (float) Math.cos(Math.toRadians(player.getRy()));
        float scprog = getScopingProgress();
        float knockback = getShotCooldown()/getShotDelay() * getKnockback();
        float gx =  - knockback + getOffsetPosition().x * (1f-scprog) +getScopeOffsetPosition().x * scprog;
        float gz = getOffsetPosition().z * (1f-scprog) + getScopeOffsetPosition().z * scprog;
        Vector3f calcOffset = new Vector3f(gx * sin + gz * cos,getOffsetPosition().y * (1f-scprog) + getScopeOffsetPosition().y * scprog, gx * cos - gz * sin);
        setPosition(player.getPosition().add(calcOffset,new Vector3f()));

        //rot
        reloadCooldown = Math.max(reloadCooldown - DisplayManager.getFrameTimeSeconds(), 0f);
        shotCooldown = Math.max(shotCooldown - DisplayManager.getFrameTimeSeconds(), 0f);
        //setRx(player.getRx() + offsetRx);
        setRy(player.getRy() + getOffsetRy());
        //setRz(player.getRz() + offsetRz);
        //shot
        /*if(Mouse.isButtonDown(0)) {
            if(shot(mousePicker,player)) Log.i("peng");
        }
        //reload
        if(Keyboard.isKeyDown(Keyboard.KEY_R)) {
            reload();
        }*/ //todo mouss
    }
}