package engine.inputs;

import engine.inputs.clickAreas.ClickArea;
import engine.inputs.listeners.*;

import java.util.ArrayList;
import java.util.List;

/***
 * Created by pv42 on 10.09.2016.
 */
public abstract class Clickable {
    private ClickArea clickArea;
    private List<OnMouseDownListener> onMouseDownListeners;
    private List<OnMouseUpListener> onMouseUpListeners;
    private List<OnClickListener> onClickListeners;
    private List<OnMouseEnterListener> onMouseEnterListeners;
    private List<OnMouseLeaveListener> onMouseLeaveListeners;

    public Clickable(ClickArea clickArea) {
        this.clickArea = clickArea;
        onMouseDownListeners = new ArrayList<>();
        onMouseUpListeners = new ArrayList<>();
        onClickListeners = new ArrayList<>();
        onMouseEnterListeners = new ArrayList<>();
        onMouseLeaveListeners = new ArrayList<>();
    }

    public void onHover() {
        onMouseEnterListeners.forEach(OnMouseEnterListener::onMouseEnter);
    }
    public void onHoverEnd() {
        onMouseLeaveListeners.forEach(OnMouseLeaveListener::onMouseLeave);
    }
    public void onMouseDown() {
        onMouseDownListeners.forEach(OnMouseDownListener::onMouseDown);
    }
    public void onMouseUp() {
        onMouseUpListeners.forEach(OnMouseUpListener::onMouseUp);
    }
    public void onClick() {
        onClickListeners.forEach(OnClickListener::onClick);
    }

    public void addOnMouseDownListener(OnMouseDownListener onMouseDownListener) {
        onMouseDownListeners.add(onMouseDownListener);
    }
    public void addOnMouseUpListener(OnMouseUpListener onMouseUpListener) {
        onMouseUpListeners.add(onMouseUpListener);
    }
    public void addOnClickListener(OnClickListener onClickListener) {
        onClickListeners.add(onClickListener);
    }
    public void addOnMouseEnterListener(OnMouseEnterListener onMouseEnterListener) {
        onMouseEnterListeners.add(onMouseEnterListener);
    }
    public void addOnMouseLeaveListener(OnMouseLeaveListener onMouseLeaveListener) {
       onMouseLeaveListeners.add(onMouseLeaveListener);
    }

    public ClickArea getClickArea() {
        return clickArea;
    }
}
