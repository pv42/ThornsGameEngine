package engine.toolbox;

import java.io.*;
import java.util.Date;

import static engine.toolbox.Settings.LOG_PATH;

/***
 * Created by pv42 on 24.06.16.
 * Log class does any logging output
 * by default the output is System.out/System.err
 */
public class Log {
    private static PrintStream out = System.out;
    private static PrintStream err = System.err;
    private static int errorNumber = 0;
    private static int warningNumber = 0;

    /**
     * sets the log output to a file rather than the STDOUT/STDERR
     */
    public static void connectLogFile() {
        try {
            File file = new File(LOG_PATH);
            if (!file.canWrite()) //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            out = new PrintStream(new FileOutputStream(file));
            err = out;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * logs a debug message if enabled in the settings
     * @param text message to log
     */
    public static void d(String text) {
        if (Settings.SHOW_DEBUG_LOG) out.println(new Date().toString() + " DBUG:" + text);
    }

    /**
     * logs a debug message if enabled in the settings
     * @param tag tag
     * @param text message to log
     */
    public static void d(String tag, Object text) {
        if (Settings.SHOW_DEBUG_LOG) out.println(new Date().toString() + " DBUG:" + tag + " " + text.toString());
    }

    /**
     * logs an events message if enabled in the settings
     * @param text message to log
     */
    public static void ev(String text) {
        if (Settings.SHOW_EVENT_LOG) out.println(new Date().toString() + " EVNT:" + text);
    }

    /**
     * logs an event message if enabled in the settings
     * @param tag tag...
     * @param text message to log
     */
    public static void ev(String tag, String text) {
        if (Settings.SHOW_EVENT_LOG) out.println(new Date().toString() + " EVNT:" + tag + " " + text);
    }

    /**
     * logs an information
     * @param text message to log
     */
    public static void i(String text) {
        out.println(new Date().toString() + " INFO:" + text);
    }

    /**
     * logs an infomarion
     * @param tag ---
     * @param text message to log
     */
    public static void i(String tag, String text) {
        out.println(new Date().toString() + " INFO:" + tag + " " + text);
    }

    /**
     * logs a warning
     * @param text message to log
     */
    public static void w(String text) {
        warningNumber++;
        out.println(new Date().toString() + " WARN:" + text);
    }

    /**
     * logs a warning
     * @param tag ...
     * @param text message to log
     */
    public static void w(String tag, String text) {
        warningNumber++;
        out.println(new Date().toString() + " WARN:" + tag + " " + text);
    }

    /**
     * logs an error
     * @param text message to log
     */
    public static void e(String text) {
        errorNumber++;
        err.println(new Date().toString() + " ERR :" + text);
    }

    /**
     * logs an error
     * @param tag tag
     * @param text message to log
     */
    public static void e(String tag, String text) {
        errorNumber++;
        err.println(new Date().toString() + " ERR :" + tag + " " + text);
    }

    /**
     * returns the number of errors logged
     * @return number of errors
     */
    public static int getErrorNumber() {
        return errorNumber;
    }

    /**
     * returns the number of warnings logged
     * @return number of warnings
     */
    public static int getWarningNumber() {
        return warningNumber;
    }
}
