package engine.toolbox;

import org.lwjgl.util.vector.Vector3f;

/***
 * Created by pv42 on 15.09.2016.
 */
public class Color {
    private int data; // 0x00rrbbgg / 0xAArrbbgg
    public Color(byte A,byte r,byte b, byte g) {
        data = A * 0x1000000 + r * 0x10000 + b * 0x100 + g;
    }
    public Color(byte r, byte b, byte g) {
        data = r * 0x10000 + b * 0x100 + g;
    }
    public Color(double r, double b, double g) {
        data = (int)(r * 0x10000 * 0xff) + (int)(b * 0x100 * 0xff) + (int)(g * 0xff);
    }
    public Color(double A, double r, double b, double g) {
        data = (int)(A * 0x1000000 * 0xff) + (int)(r * 0x10000 * 0xff) + (int)(b * 0x100 * 0xff) + (int)(g * 0xff);
    }
    public Color(Vector3f rgb) {
        data = (int)(rgb.x * 0x10000 + rgb.y * 0x100 + rgb.z);
    }
    public Color(int data) {
        this.data = data;
    }
    public Vector3f getVector() {
        return new Vector3f(getR(),getG(),getB());
    }
    public int getData() {
        return data;
    }
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
    public int getIntR() {
        return data/0x10000;
    }
    public int getIntG() {
        return (data/0x100)%0x100;
    }
    public int getIntB() {
        return  data%0x100;
    }
    public float getR() {
        return (float) getIntR() / 255f;
    }
    public float getG() {
        return (float) getIntG() / 255f;
    }
    public float getB() {
        return (float) getIntB() / 255f;
    }
}
