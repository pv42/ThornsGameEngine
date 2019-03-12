package engine.toolbox;


/**
 * Created by pv42 on 22.06.16.
 */
public class Time {
    public static long getMilliTime() {
        return System.currentTimeMillis(); //time in ms
    }
    public static long getNanoTime() {
        return System.nanoTime();
    }
}
