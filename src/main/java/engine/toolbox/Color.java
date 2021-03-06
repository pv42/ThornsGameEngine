package engine.toolbox;

import org.joml.Vector3f;

/**
 * Created by pv42 on 15.09.2016.
 * A color saved as argb 32bit
 */
public class Color {
    private int data; // 0x00rrbbgg / 0xAArrbbgg

    /**
     * Creates a RGBA color object
     *
     * @param a alpha component
     * @param b blue component
     * @param g green component
     * @param r red component
     */
    public Color(byte r, byte g, byte b, byte a) {
        data = (getUnsignedFromByte(a) << 24) | (getUnsignedFromByte(r) << 16) | (getUnsignedFromByte(g) << 8) |
                getUnsignedFromByte(b);
    }

    /**
     * Creates a RGB color object with alpha 255 ( not transparent)
     *
     * @param b blue component
     * @param g green component
     * @param r red component
     */
    public Color(byte r, byte g, byte b) {
        this(r, g, b, (byte) 255);
    }

    /**
     * Creates a RGBA color object
     *
     * @param B blue component
     * @param G green component
     * @param R red component
     */
    public Color(double R, double G, double B) {
        this(R, G, B, 1.0);
    }

    /**
     * Creates a RGBA color object
     *
     * @param A alpha component
     * @param B blue component float between 0 and 1
     * @param G green component float between 0 and 1
     * @param R red component float between 0 and 1
     * @throws IllegalArgumentException if argument is not between 0 and 1
     */
    public Color(double R, double G, double B, double A) {
        this((byte) (R * 255.0), (byte) (G * 255.0), (byte) (B * 255.0), (byte) (A * 255.0));
        if (0 > A || A > 1 || 0 > R || R > 1 || 0 > G || G > 1 || 0 > B || B > 1)
            throw new IllegalArgumentException("Value have to be betweed 0.0 and 1.0");
    }

    /**
     * Creates a color object from a rgb vector
     *
     * @param rgb rgb, each value has to be between 0 and 1
     */
    public Color(Vector3f rgb) {
        this(rgb.x, rgb.y, rgb.z);
    }

    /**
     * Creates a color object from a 32bit int representation
     *
     * @param data 32bit color data
     */
    public Color(int data) {
        this.data = data;
    }

    /**
     * creates a color from the hsl
     *
     * @param hue        hue parameter of the hls format
     * @param saturation saturation parameter of the hls format
     * @param lightness  lightness parameter of the hls format
     * @return Color
     */
    public static Color fromHSL(float hue, float saturation, float lightness) {
        // 0<=hue<360, 0<=lightness,saturation<=1
        hue = hue % 360;
        float c = (1 - Math.abs(2 * lightness - 1)) * saturation;
        float d = c * (1 - Math.abs((hue / 60) % 2 - 1));
        float m = lightness - c / 2;
        Vector3f rgb;
        if (hue < 60) {
            rgb = new Vector3f(c, d, 0);
        } else if (hue < 120) {
            rgb = new Vector3f(d, c, 0);
        } else if (hue < 180) {
            rgb = new Vector3f(0, c, d);
        } else if (hue < 240) {
            rgb = new Vector3f(0, d, c);
        } else if (hue < 300) {
            rgb = new Vector3f(d, 0, c);
        } else {
            rgb = new Vector3f(c, 0, d);
        }
        rgb.x += m;
        rgb.y += m;
        rgb.z += m;
        return new Color(rgb);
    }

    private static int getUnsignedFromByte(byte b) {
        return b & 0xff;
    }

    /**
     * Gets a Color vector
     *
     * @return rgb vector of values between 0 and 1
     */
    public Vector3f getVector() {
        return new Vector3f(getR(), getG(), getB());
    }

    /**
     * gets the color data
     *
     * @return 32bit color data
     */
    public int getColorData() {
        return data;
    }

    /**
     * gets the alpha component of the 32bit color
     *
     * @return 8bit colors alpha part
     */
    public byte getAByte() {
        return (byte) (data >> 24);
    }

    /**
     * gets the red component of the 32bit color, note that a value greater then 127 is negative since java bytes are unsigned
     *
     * @return 8bit colors red part
     */
    public byte getRByte() {
        return (byte) ((data >> 16) % 0x100);
    }

    /**
     * gets the green component of the 32bit color, note that a value greater then 127 is negative since java bytes are unsigned
     *
     * @return 8bit colors green part
     */
    public byte getGByte() {
        return (byte) ((data >> 8) % 0x100);
    }

    /**
     * gets the blue component of the 32bit color, note that a value greater then 127 is negative since java bytes are unsigned
     *
     * @return 8bit colors blue part
     */
    public byte getBByte() {
        return (byte) (data % 0x100);
    }

    public float getA() {
        return (float) getUnsignedFromByte(getAByte()) / 255f;
    }

    public float getR() {
        return (float) getUnsignedFromByte(getRByte()) / 255f;
    }

    public float getG() {
        return (float) getUnsignedFromByte(getGByte()) / 255f;
    }

    public float getB() {
        return (float) getUnsignedFromByte(getBByte()) / 255f;
    }

    /**
     * Compares this color to the specified object.  The result is {@code
     * true} if and only if the argument is not {@code null} and is a {@code
     * Color} object that represents the same r,g,b,a values as this
     * object.
     *
     * @param obj The object to compare this {@code Color} against
     * @return {@code true} if the given object represents a {@code Color}
     * equivalent to this color, {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Color) {
            Color anotherColor = (Color) obj;
            return this.data == anotherColor.data;
        }
        return false;
    }

    @Override
    public String toString() {
        return "RGBA(" + getUnsignedFromByte(getRByte()) + "," + getUnsignedFromByte(getGByte()) + "," +
                getUnsignedFromByte(getBByte()) + "," + getUnsignedFromByte(getAByte()) + ")";
    }

    public void set(Color color) {
        this.data = color.data;
    }
}
