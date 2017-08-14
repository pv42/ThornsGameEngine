package shivt.guns;


import engine.audio.Source;
import engine.graphics.entities.Player;
import org.joml.Vector3f;
import engine.graphics.display.DisplayManager;

/**
 * Created by pv42 on 01.07.16.
 */
public class Beretta92 extends Gun{
    private int soundBuffer;
    private Source soundSource;
    public Beretta92() {
        super(new GunLoader("Beretta92"));
        //soundBuffer = AudioMaster.loadSound("Beretta92");
        //soundSource = new Source();
    }
    @Override
    public boolean shot(Player player) {
        if(super.shotCooldown > 0) return false;
        if(remainingAmmo <= 0) return false;
        if(reloadCooldown > 0) return false;
        remainingAmmo --;
        shotCooldown = getShotDelay();
        //todo new Particle(new ParticleTexture(Loader.loadTexture("boom.png"),5,true,false), Vector3f.add(player.getEyePosition(),getMuzzlePosition(),null), Maths.scaleVector(new Vector3f(1,0,0),getProjectileSpeed()),0,10f,0,2);
        soundSource.play(soundBuffer);
        return true;
    }

    @Override
    public void update(Player player) {
        int sco = -1;
        /*if(Mouse.isButtonDown(1)) {
            sco = 1;
        }*/ //// TODO: 12.09.2016 m
        setScopingProgress(Math.max(Math.min(getScopingProgress() + sco / getScopingTime() * DisplayManager.getFrameTimeSeconds(),1),0));
        float sin = (float) Math.sin(Math.toRadians(player.getRy()));
        float cos = (float) Math.cos(Math.toRadians(player.getRy()));
        float scprog = getScopingProgress();
        float knockback = getShotCooldown()/getShotDelay() * getKnockback();
        getModel().setPositionOffset(1,new Vector3f(-knockback * sin,0, -knockback * cos));
        float gx =  + getOffsetPosition().x * (1f-scprog) +getScopeOffsetPosition().x * scprog;
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
            if(shot(mousePicker,player)) Log.i("peng");
        }
        //reload
        if(Keyboard.isKeyDown(Keyboard.KEY_R)) {
            reload();
        }*/ //// TODO: 12.09.2016
    }
}