import engine.toolbox.Color;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ColorTester {
    @Test
    void test() {
        System.out.println(new Color(0.5, 0.7, 0.8));
        System.out.println(new Color(0.5, 0.7, 0.8).getVector().toString(new DecimalFormat()));
        float eps = 0.01f;
        assertEquals(0.5, new Color(0.5, 0.7, 0.8).getR(), eps);
        assertEquals(0.7, new Color(0.5, 0.7, 0.8).getG(), eps);
        assertEquals(0.8, new Color(0.5, 0.7, 0.8).getB(), eps);
        assertEquals(0.4, new Color(0.5, 0.7, 0.8, 0.4).getA(), eps);
        assertEquals(102, new Color(0.5, 0.7, 0.8, 0.4).getAByte());
        assertEquals((byte) 255, new Color(0.5, 0.7, 0.8, 1).getAByte());
        assertEquals(1, new Color((byte) 1, (byte) 2, (byte) 3, (byte) 4).getRByte());
        assertEquals(2, new Color((byte) 1, (byte) 2, (byte) 3, (byte) 4).getGByte());
        assertEquals(3, new Color((byte) 1, (byte) 2, (byte) 3, (byte) 4).getBByte());
        assertEquals(4, new Color((byte) 1, (byte) 2, (byte) 3, (byte) 4).getAByte());
    }
}
