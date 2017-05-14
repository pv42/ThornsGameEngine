package shivt.levels;

import org.lwjgl.util.vector.Vector3f;

/**
 * Created by pv42 on 15.09.2016.
 */
public class Station {
    public static final byte OWNER_NEUTRAL = 0;
    public static final byte OWNER_ALLIED = 1;
    public static final byte OWNER_ENEMY = 2;
    private final Vector3f position;
    private int owner;
    private int troopsStrength;

    public Station(Vector3f position, int owner, int troopsStrength) {
        this.position = position;
        this.owner = owner;
        this.troopsStrength = troopsStrength;
    }

    public Vector3f getPosition() {
        return position;
    }

    public int getOwner() {
        return owner;
    }

    public int getTroopsStrength() {
        return troopsStrength;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public void setTroopsStrength(int troopsStrength) {
        this.troopsStrength = troopsStrength;
    }

    @Override
    public String toString() {
        return "Station{" +
                "position=" + position +
                ", owner=" + owner +
                ", troopsStrength=" + troopsStrength +
                '}';
    }
}
