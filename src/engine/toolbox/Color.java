package engine.toolbox;

import org.lwjgl.util.vector.Vector3f;

/**
 * Created by pv42 on 15.09.2016.
 * A color saved as argb 32bit
 */
public class Color {
    private int data; // 0x00rrbbgg / 0xAArrbbgg
    /**
     * Creates a RGBA color object
     * @param A alpha component
     * @param b blue component
     * @param g green component
     * @param r red component
     */
    public Color(byte A,byte r,byte g, byte b) {
        data = A * 0x1000000 + r * 0x10000 + g * 0x100 + b;
    }

    /**
     * Creates a RGB color object with alpha 255 ( not transparent)
     * @param b blue component
     * @param g green component
     * @param r red component
     */
    public Color(byte r, byte g, byte b) {
        this(255,r,b,g);
    }

    /**
     * Creates a RGBA color object
     * @param b blue component
     * @param g green component
     * @param r red component
     */
    public Color(double r, double g, double b) {
        this(1.0,r,g,b);
    }

    /**
     * Creates a RGBA color object
     * @param A alpha component
     * @param B blue component float between 0 and 1
     * @param G green component float between 0 and 1
     * @param R red component float between 0 and 1
     * @throws IllegalArgumentException if argument is not between 0 and 1
     */
    public Color(double A, double R, double G, double B) {
        this((byte)(A * 0xff),(byte)(R * 0xff), (byte)(G * 0xff), (byte)(B * 0xff));
        if(0 > A || A > 1 || 0 > R || R > 1 || 0 > G || G > 1 || 0 > B || B > 1)
            throw new IllegalArgumentException("Value have to be betweed 0.0 and 1.0");
    }

    /**
     * Creates a color object from a rgb vector
     * @param rgb rgb, each value has to be between 0 and 1
     */
    public Color(Vector3f rgb) {
        this(rgb.x, rgb.y, rgb.z);
    }

    /**
     * Creates a color object from a 32bit int representation
     * @param data 32bit color data
     */
    public Color(int data) {
        this.data = data;
    }

    /**
     * Gets a Color vector
     * @return rgb vector of values between 0 and 1
     */
    public Vector3f getVector() {
        return new Vector3f(getR(),getG(),getB());
    }

    /**
     * gets the color data
     * @return 32bit color data
     */
    public int getColorData() {
        return data;
    }

    /**
     * creates a color from the hsl
     * @param h hue parameter of the hls format
     * @param s satuartion parameter of the hls format
     * @param l lightness parameter of the hls format
     * @return Color
     */
    public static Color fromHSL(float h, float s, float l) {
        // 0<=h<360, 0<=l,s<=1
        h = h % 360;
        float c = (1 - Math.abs(2 * l -1)) * s;
        float x = c * (1 - Math.abs((h / 60) % 2 - 1));
        float m = l - c/2;
        Vector3f rgb;
        if (h < 60) {
            rgb = new Vector3f(c,x,0);
        } else if(h < 120) {
            rgb = new Vector3f(x,c,0);
        } else if(h < 180) {
            rgb = new Vector3f(0,c,x);
        } else if(h < 240) {
            rgb = new Vector3f(0,x,c);
        }else if(h < 300) {
            rgb = new Vector3f(x,0,c);
        }else {
            rgb = new Vector3f(c,0,x);
        }
        rgb.x += m;
        rgb.y += m;
        rgb.z += m;
        return new Color(rgb);
    }

    /**
     * gets the alpha component of the 32bit color
     * @return 8bit colors alpha part
     */
    public byte getAByte() {
        return (byte)(data/0x1000000);
    }
    /**
     * gets the red component of the 32bit color
     * @return 8bit colors red part
     */
    public byte getRByte() {
        return (byte)((data/0x10000)%0x100);
    }

    /**
     * gets the green component of the 32bit color
     * @return 8bit colors green part
     */
    public byte getGByte() {
        return (byte)((data/0x100)%0x100);
    }

    /**
     * gets the blue component of the 32bit color
     * @return 8bit colors blue part
     */
    public byte getBByte() {
        return  (byte)(data%0x100);
    }

    public float getA() {
        return (float) getAByte() / 255f;
    }
    public float getR() {
        return (float) getRByte() / 255f;
    }
    public float getG() {
        return (float) getGByte() / 255f;
    }
    public float getB() {
        return (float) getBByte() / 255f;
    }
}
