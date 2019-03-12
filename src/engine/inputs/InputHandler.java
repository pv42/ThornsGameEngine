package engine.inputs;

import engine.inputs.listeners.CursorListener;
import engine.inputs.listeners.InputEventListener;
import engine.toolbox.Log;
import engine.toolbox.Settings;
import engine.toolbox.Time;
import org.lwjgl.glfw.*;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static engine.inputs.InputEvent.*;
/***
 * Created by pv42 on 04.09.2016.
 */
public class InputHandler {
    private static List<InputEvent> eventQ;
    private static Map<Integer,InputEventListener> listeners;
    private static CursorListener cursorListener = null;
    private static int mouseX,mouseY;
    private static boolean isMouseBound = false;
    public static void init(long windowID) {
        eventQ = new ArrayList<>();
        listeners = new HashMap<>();
        mouseX = 0;
        mouseY = 0;
        GLFW.glfwSetCursorPosCallback(windowID, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long l, double v, double v1) {
                if(isMouseBound) eventQ.add(new InputEvent(MOUSE_EVENT,CURSOR_MOVE,0, Time.getMilliTime(),new Vector2f((float) v-mouseX,(float) v1-mouseY )));
                else eventQ.add(new InputEvent(MOUSE_EVENT,CURSOR_MOVE,0, Time.getMilliTime(),new Vector2f((float) v,(float) v1 )));
                mouseX = (int)v;
                mouseY = (int)v1;
            }
        });
        GLFW.glfwSetMouseButtonCallback(windowID, new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                eventQ.add(new InputEvent(MOUSE_EVENT,action,button, Time.getMilliTime(),new Vector2f(mouseX,mouseY)));
            }
        });
        GLFW.glfwSetKeyCallback(windowID, new GLFWKeyCallback() {
            @Override
            public void invoke(long windowID, int key, int scancode, int action, int mods) {
                if(key != 0)  {
                    eventQ.add(new InputEvent(KEY_EVENT,action,key, Time.getMilliTime()));
                }
            }
        });
        GLFW.glfwSetScrollCallback(windowID, new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                eventQ.add(new InputEvent(MOUSE_EVENT,SCROLL,0, Time.getMilliTime(),new Vector2f((float) xoffset,(float) yoffset)));
            }
        });
    }
    static boolean hasNextEvent() {
        return !eventQ.isEmpty();
    }
    static void handleNextEvent() {
        InputEvent e = eventQ.remove(0);
        // print events
        if(Settings.SHOW_DEBUG_LOG) {
            String source = e.getEventSource() == InputEvent.MOUSE_EVENT ? "MOUSE_EVENT" : e.getEventSource() == InputEvent.KEY_EVENT ? "KEY_EVENT" : "SCROLL_EVENT";
            String type;
            switch (e.getEventType()) {
                case KEY_PRESS:
                    type = "KEY_DOWN,";
                    break;
                case KEY_RELEASE:
                    type = "KEY_UP,";
                    break;
                case KEY_REPEAT:
                    type = "KEY_RPT,";
                    break;
                case SCROLL:
                    type = "SCROLL,";
                    break;
                case CURSOR_MOVE:
                    type = "CURSOR,";
                    break;
                default:
                    type = "err";
            }
            String data;
            if (e.getEventSource() == InputEvent.MOUSE_EVENT)
                data = e.getEventData() == 0 ? "L_MOUSE" : e.getEventData() == 1 ? "R_MOUSE" : "M_MOUSE";
            else data = GLFW.glfwGetKeyName(e.getEventData(), 0);
            if (e.getEventType() == CURSOR_MOVE || e.getEventType() == SCROLL)
                data = (int) e.getMousePosition().x + "," + (int) e.getMousePosition().y;
            if (data == null) data = e.getEventData() + "";
            Log.ev("InputEvent", source + "," + type + data);
        }
        // listeners
        if(e.getEventType() == CURSOR_MOVE && cursorListener != null) {
            cursorListener.onMove((int) e.getMousePosition().x,(int) e.getMousePosition().y);
        } else {
            InputEventListener l = listeners.get(e.getDataID());
            if(l != null) {
                l.onOccur();
            }
        }

    }
    public static void addListener(InputEventListener listener) {
        // TODO: 12.09.2016 //
        listeners.put(listener.getDataID(), listener);
    }
    public static void setCursorListener(CursorListener cursorListener) {
        InputHandler.cursorListener = cursorListener;
    }

    public static void setMouseBound(boolean isMouseBound) {
        InputHandler.isMouseBound = isMouseBound;
    }
}