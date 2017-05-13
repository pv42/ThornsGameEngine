package engine.toolbox;


/**
 * Created by pv42 on 22.06.16.
 */
public class Timer {
    public static long getTime() {
        return System.currentTimeMillis(); //time in ms
    }
    public static long getNanoTime() {
        return System.nanoTime();
    }
}
