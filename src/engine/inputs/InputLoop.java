package engine.inputs;

/***
 * Created by pv42 on 06.09.2016.
 */

public class InputLoop {
    private static final long SLEEP_TIME = 15;
    private static boolean finish = false;
    public static void init(long windowID) {
        InputHandler.init(windowID);
    }
    public static void run () {
        while (!finish) {
            loop();
        }
    }
    private static void loop() {
        boolean noNextEvent;
        while ((noNextEvent = !InputHandler.hasNextEvent()) && !finish) {
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(!noNextEvent) loopHandle();
    }
    private static void loopHandle() {
        while(InputHandler.hasNextEvent() ) InputHandler.handleNextEvent();
    }
    public static void finish() {
        finish = true;
    }
}
