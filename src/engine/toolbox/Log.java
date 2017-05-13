package engine.toolbox;

import java.io.*;
import java.util.Date;

/***
 * Created by pv42 on 24.06.16.
 */
public class Log {
    private static PrintStream out = System.out;
    private static PrintStream err = System.err;
    public static void connectLogFile() {
        try {
            File file = new File("log/log.txt");
            if(!file.canWrite()) file.createNewFile();
            out = new PrintStream(new FileOutputStream(file));
            err = out;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void d(String text) {
        if(Settings.SHOW_DEBUG_LOG) out.println(new Date().toString() + " DBUG:" + text);
    }
    public static void d(String tag,String text) {
        if(Settings.SHOW_DEBUG_LOG) out.println(new Date().toString() + " DBUG:"+ tag + " " + text);
    }
    public static void i(String text) {
        out.println(new Date().toString() + " INFO:" + text);
    }
    public static void i(String tag,String text) {
        out.println(new Date().toString() + " INFO:"+ tag + " " + text);
    }
    public static void w(String text) {
        out.println(new Date().toString() + " WARN:" + text);
    }
    public static void w(String tag,String text) {
        out.println(new Date().toString() + " WARN:" + tag + " " + text);
    }
    public static void e(String text) {
        err.println(new Date().toString() + " ERR :" + text);
    }
    public static void e(String tag,String text) {
        err.println(new Date().toString() + " ERR :"+ tag + " " + text);
    }
}
