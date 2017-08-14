package engineTester;

import engine.toolbox.Color;

public class ColorTester {
    public static void main(String[] args) {
        System.out.println(new Color(0.5,0.7,0.8).getVector());
        System.out.println(new Color(0.5,0.7,0.8).getB());
    }
}
