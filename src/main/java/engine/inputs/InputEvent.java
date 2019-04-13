package engine.inputs;


import org.lwjgl.glfw.GLFW;
import org.joml.Vector2f;

/**
 * Created by pv42 on 04.09.2016.
 */
public class InputEvent {
    public static final int MOUSE_EVENT = 0; //mouse btn / cursor mv / scroll
    public static final int KEY_EVENT = 1;
    public static final int KEY_PRESS = GLFW.GLFW_PRESS;
    public static final int KEY_RELEASE = GLFW.GLFW_RELEASE;
    public static final int KEY_REPEAT = GLFW.GLFW_REPEAT;
    public static final int CURSOR_MOVE = 3;
    public static final int SCROLL = 4;
    public static final int L_MOUSE = 0;
    public static final int R_MOUSE = 1;
    private final int eventSource;
    private final int eventType;
    private final int eventData;
    private final long eventTime;
    private final Vector2f mousePosition;

    public InputEvent(int eventSource, int eventType, int eventData, long eventTime,Vector2f mousePosition) {
        this.eventSource = eventSource;
        this.eventType = eventType;
        this.eventData = eventData;
        this.eventTime = eventTime;
        this.mousePosition = mousePosition;
    }
    public InputEvent(int eventSource, int eventType, int eventData, long eventTime) {
        this.eventSource = eventSource;
        this.eventType = eventType;
        this.eventData = eventData;
        this.eventTime = eventTime;
        this.mousePosition = null;
    }

    public int getEventSource() {
        return eventSource;
    }

    public int getEventType() {
        return eventType;
    }

    public int getEventData() {
        return eventData;
    }

    public long getEventTime() {
        return eventTime;
    }

    public static boolean compareData(InputEvent ie1, InputEvent ie2) {
        return ie1.getEventSource() == ie2.getEventSource() && ie1.getEventType() == ie2.getEventType() && ie1.getEventData() == ie2.getEventData();
    }
    public boolean compareData(InputEvent ie) {
        return compareData(this,ie);
    }
    public int getDataID() {
        return eventSource + 2 * eventType + 8 * eventData;
    }

    public Vector2f getMousePosition() {
        return mousePosition;
    }
}
