package shivt.guns;

import engine.audio.AudioMaster;
import engine.audio.OggSource;
import engine.audio.Source;
import engine.graphics.entities.Player;
import org.joml.Vector3f;
import engine.graphics.particles.Particle;
import engine.graphics.particles.ParticleTexture;
import engine.graphics.renderEngine.DisplayManager;
import engine.graphics.renderEngine.Loader;
import engine.toolbox.Maths;

/***
 * Created by pv42 on 01.07.16.
 */
public class KSR29 extends Gun {
    private static final String TAG = "KSR-29";
    private OggSource soundBuffer;
    private Source soundSource;
    public KSR29() {
        super(new GunLoader("KSR-29"));
        soundBuffer = AudioMaster.loadSound("KSR-29");
        soundSource = new Source();
    }
    public boolean shot( Player player) {
        if(shotCooldown > 0) return false;
        if(remainingAmmo <= 0) return false;
        if(reloadCooldown > 0) return false;
        remainingAmmo --;
        shotCooldown = getShotDelay();
        //todo new Particle(new ParticleTexture(Loader.loadTexture("boom"),5,true,false), Vector3f.add(player.getEyePosition(),getMuzzlePosition(),null), Maths.scaleVector(new Vector3f(0,0,1),getProjectileSpeed()),0,10f,0,2);
        soundSource.play(soundBuffer);
        return true;
    }
    @Override
    public void update(Player player) {
        int sco = -1;
        /*if(Mouse.isButtonDown(1)) {
            sco = 1;
        }*/ //todo
        setScopingProgress(Math.max(Math.min(getScopingProgress() + sco / getScopingTime() * DisplayManager.getFrameTimeSeconds(),1),0));
        float sin = (float) Math.sin(Math.toRadians(player.getRy()));
        float cos = (float) Math.cos(Math.toRadians(player.getRy()));
        float scprog = getScopingProgress();
        float knockback = getShotCooldown()/getShotDelay() * getKnockback();
        float gx =  - knockback + getOffsetPosition().x * (1f-scprog) +getScopeOffsetPosition().x * scprog;
        float gz = getOffsetPosition().z * (1f-scprog) + getScopeOffsetPosition().z * scprog;
        Vector3f calcOffset = new Vector3f(gx * sin + gz * cos,getOffsetPosition().y * (1f-scprog) + getScopeOffsetPosition().y * scprog, gx * cos - gz * sin);
        //todo setPosition(Vector3f.add(player.getPosition(),calcOffset,null));
        //rot
        reloadCooldown = Math.max(reloadCooldown - DisplayManager.getFrameTimeSeconds(), 0f);
        shotCooldown = Math.max(shotCooldown - DisplayManager.getFrameTimeSeconds(), 0f);
        //setRx(player.getRx() + offsetRx);
        setRy(player.getRy() + getOffsetRy());
        //setRz(player.getRz() + offsetRz);
        //shot
        /*if(Mouse.isButtonDown(0)) {
            if(shot(mousePicker,player)) Log.i(TAG, "peng");
        }
        //reload
        if(Keyboard.isKeyDown(Keyboard.KEY_R)) {
            reload();
        }*///// TODO: 12.09.2016 key
    }
}