import engine.toolbox.Color;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class ColorTester {
    private static float eps = 0.01f;
    @Test
    void test() {
        System.out.println(new Color(0.5, 0.7, 0.8));
        System.out.println(new Color(0.5, 0.7, 0.8).getVector().toString(new DecimalFormat()));
        assertEquals(0.5, new Color(0.5, 0.7, 0.8).getR(), eps);
        assertEquals(0.7, new Color(0.5, 0.7, 0.8).getG(), eps);
        assertEquals(0.8, new Color(0.5, 0.7, 0.8).getB(), eps);
    }

    @Test
    void fromHSL() {
        Color c = Color.fromHSL(1,0,0);
        assertEquals(c, new Color((byte)0,(byte)0,(byte)0,(byte)255));
        c = Color.fromHSL(0,0,1);
        assertEquals(c, new Color((byte)255,(byte)255,(byte)255,(byte)255));
        c = Color.fromHSL(0,1,0.5f);
        assertEquals(c, new Color((byte)255,(byte)0,(byte)0,(byte)255));
        c = Color.fromHSL(0,1,0.5f);
        assertEquals(c, new Color((byte)255,(byte)0,(byte)0,(byte)255));
        c = Color.fromHSL(60,1,0.5f);
        assertEquals(c, new Color((byte)255,(byte)255,(byte)0,(byte)255));
        c = Color.fromHSL(240,1,0.5f);
        assertEquals(c, new Color((byte)0,(byte)0,(byte)255,(byte)255));
        System.out.println(c);
    }

    @Test
    void getVector() {
    }

    @Test
    void getColorData() {
        Color c = new Color((byte)254,(byte)252,(byte)10,(byte)251);
        assertEquals(c.getColorData(), (-5 * 256 * 256 * 256) + (254 * 256 * 256) + (252 * 256) + 10);
    }

    @Test
    void getAByte() {
        assertEquals(102, new Color(0.5, 0.7, 0.8, 0.4).getAByte());
        assertEquals(4, new Color((byte) 1, (byte) 2, (byte) 3, (byte) 4).getAByte());
        assertEquals((byte) 255, new Color(0.5, 0.7, 0.8, 1).getAByte());
    }

    @Test
    void getRByte() {
        assertEquals(1, new Color((byte) 1, (byte) 2, (byte) 3, (byte) 4).getRByte());
    }

    @Test
    void getGByte() {
        assertEquals(2, new Color((byte) 1, (byte) 2, (byte) 3, (byte) 4).getGByte());
    }

    @Test
    void getBByte() {
        assertEquals(3, new Color((byte) 1, (byte) 2, (byte) 3, (byte) 4).getBByte());
    }

    @Test
    void getA() {
        assertEquals(0.4, new Color(0.5, 0.7, 0.8, 0.4).getA(), eps);
    }

    @Test
    void getR() {
        assertEquals(0.5, new Color(0.5, 0.7, 0.8, 0.4).getR(), eps);
    }

    @Test
    void getG() {
        assertEquals(0.7, new Color(0.5, 0.7, 0.8, 0.4).getG(), eps);
    }

    @Test
    void getB() {
        assertEquals(0.8, new Color(0.5, 0.7, 0.8, 0.4).getB(), eps);
    }

    @Test
    void equals1() {
        Color c0 = new Color((byte)118,(byte)0,(byte)255,(byte)200);
        Color c1 = new Color((byte)118,(byte)0,(byte)255,(byte)200);
        Color c2 = new Color((byte)118,(byte)0,(byte)255,(byte)199);
        Color c3 = new Color((byte)118,(byte)0,(byte)254,(byte)200);
        Color c4 = new Color((byte)118,(byte)0,(byte)0,(byte)200);
        assertEquals(c0, c1);
        assertNotEquals(c0, c2);
        assertNotEquals(c0, c3);
        assertNotEquals(c0, c4);
    }

    @Test
    void set() {
        Color c = new Color(0.1,0.4,0.6);
        Color d = new Color(1,0,0);
        c.set(d);
        assertEquals(c,d);
    }
}
