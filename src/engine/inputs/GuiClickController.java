package engine.inputs;

import engine.inputs.InputHandler;
import engine.inputs.Clickable;
import engine.inputs.listeners.CursorListener;
import engine.inputs.listeners.InputEventListener;
import engine.toolbox.Log;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

import static engine.inputs.InputEvent.*;

/***
 * Created by pv42 on 14.09.2016.
 */
public class GuiClickController {
    private List<Clickable> clickables;
    private int mouseX, mouseY;
    private Clickable currentClickable;
    private Clickable mouseDownClickable;
    public GuiClickController() {
        clickables = new ArrayList<>();
        currentClickable = null;
        InputHandler.setCursorListener(new CursorListener() {
            @Override
            public void onMove(int x, int y) {
                mouseX = x;
                mouseY = y;
                Clickable oldClickable = currentClickable;
                updateCurrentClickable();
                if((currentClickable != oldClickable && oldClickable != null) ) oldClickable.onHoverEnd();
                if(currentClickable != null) currentClickable.onHover();
            }
        });
        InputHandler.addListener(new InputEventListener(MOUSE_EVENT,KEY_PRESS,L_MOUSE) {
            @Override
            public void onOccur(Vector2f v2f) {
                //updateCurrentClickable();
                if(currentClickable != null) currentClickable.onMouseDown();
                mouseDownClickable = currentClickable;
            }
        });
        InputHandler.addListener(new InputEventListener(MOUSE_EVENT,KEY_RELEASE,L_MOUSE) {
            @Override
            public void onOccur(Vector2f v2f) {
                //updateCurrentClickable();
                if(currentClickable != null) {
                    currentClickable.onMouseUp();
                    if(currentClickable == mouseDownClickable) currentClickable.onClick();
                }
            }
        });
    }
    private void updateCurrentClickable() {
        for (Clickable clickable: clickables) {
            if (clickable.getClickArea().isPointIn(new Vector2f(mouseX,mouseY)))  {
                currentClickable = clickable;
                return;
            }
        }
        currentClickable = null;
    }
    public void addClickable(Clickable clickable) {
        clickables.add(clickable);
    }
    public void clearClickables() {
        clickables.clear();
    }
}
