package shivt.guns;

import engine.graphics.entities.MMEntity;
import engine.graphics.entities.Player;
import engine.graphics.guis.GuiTexture;
import engine.toolbox.Log;
import org.lwjgl.util.vector.Vector3f;

/**
  Created by pv42 on 24.06.16.
 */
public abstract class Gun extends MMEntity {
    private static final String TAG = "GUN";
    private float reloadDelay; //s
    private float magizineSize;
    float remainingAmmo = 3;
    private float shotDelay;
    float shotCooldown;
    float reloadCooldown;
    private float knockback;
    private GuiTexture scope = null;
    private Vector3f offsetPosition;
    private float offsetRx, offsetRy, offsetRz;
    private Vector3f scopeOffsetPosition;
    private Vector3f muzzlePosition = new Vector3f(0,.5f,0); //todo !
    private float projectileSpeed = 300; //// TODO: 25.06.16 !
    private float scopingTime = 0.3f;
    private float scopingProgress = 0; //0-1
    //// TODO: 02.07.16 change multimeshsupport replace with animation
    public Gun(GunLoader g) {
        super(g.getMMTmodel(), new Vector3f(0,0,0), 0, 0, 0, g.getScale());
        offsetRx = g.getOffsetRx();
        offsetRy = g.getOffsetRy();
        offsetRz = g.getOffsetRz();
        if(g.getGuiTexture() != -1) this.scope = new GuiTexture(g.getGuiTexture(),g.getGuiTexturePosition(), g.getScopeTextureScale()); //todo
        this.offsetPosition  = g.getOffsetPosition();
        this.scopeOffsetPosition = g.getScopeOffsetPosition();
        this.magizineSize = g.getMagazineSize();
        this.shotDelay = g.getShotDelay();
        this.reloadDelay = g.getReloadDelay();
        this.knockback = g.getKnockback();
        this.projectileSpeed = g.getProjectileSpeed();
    }

    public Vector3f getOffsetPosition() {
        return offsetPosition;
    }

    public float getOffsetRx() {
        return offsetRx;
    }

    public float getOffsetRy() {
        return offsetRy;
    }

    public float getOffsetRz() {
        return offsetRz;
    }

    public Vector3f getScopeOffsetPosition() {
        return scopeOffsetPosition;
    }

    public void setScopingProgress(float scopingProgress) {
        this.scopingProgress = scopingProgress;
    }

    public float getScopingTime() {
        return scopingTime;
    }

    public float getScopingProgress() {
        return scopingProgress;
    }

    public GuiTexture getScope() {
        return scope;
    }

    public abstract  boolean shot(Player player);

    public void reload() {
        if(reloadCooldown > 0) return;
        if(remainingAmmo == getMagizineSize()) return;
        Log.i(TAG, "reloading");
        remainingAmmo = getMagizineSize();
        reloadCooldown = getReloadDelay();
    }

    public abstract void update(Player player);

    public float getKnockback() {
        return knockback;
    }

    public float getReloadCooldown() {
        return reloadCooldown;
    }

    public float getMagizineSize() {
        return magizineSize;
    }

    public float getReloadDelay() {
        return reloadDelay;
    }

    public float getShotDelay() {
        return shotDelay;
    }

    public float getShotCooldown() {
        return shotCooldown;
    }

    public float getProjectileSpeed() {
        return projectileSpeed;
    }

    public Vector3f getMuzzlePosition() {
        return muzzlePosition;
    }
}