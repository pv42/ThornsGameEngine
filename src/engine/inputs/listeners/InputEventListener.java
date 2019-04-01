package engine.inputs.listeners;

import org.joml.Vector2f;

/**
 * Created by pv42 on 06.09.2016.
 */
public abstract class InputEventListener {
    private int eventSource;
    private int eventType;
    private int eventData;

    public InputEventListener(int eventSource, int evnetType, int eventData) {
        this.eventSource = eventSource;
        this.eventType = evnetType;
        this.eventData = eventData;
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

    public int getDataID() {
        return eventSource + 2 * eventType + 8 * eventData;
    }

    public abstract void onOccur(Vector2f posData);
}
