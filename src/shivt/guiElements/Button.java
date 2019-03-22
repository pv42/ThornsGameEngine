package shivt.guiElements;

import engine.graphics.Scene;
import engine.graphics.display.Window;
import engine.graphics.glglfwImplementation.text.GLGuiText;
import engine.graphics.glglfwImplementation.text.GLTTFont;
import engine.graphics.guis.GuiTexture;
import engine.inputs.Clickable;
import engine.inputs.clickAreas.RectangleClickArea;
import engine.inputs.listeners.OnMouseDownListener;
import engine.inputs.listeners.OnMouseEnterListener;
import engine.inputs.listeners.OnMouseLeaveListener;
import engine.inputs.listeners.OnMouseUpListener;
import engine.graphics.glglfwImplementation.GLLoader;
import engine.toolbox.Color;
import engine.toolbox.Maths;
import org.joml.Vector2f;

/***
 * Created by pv42 on 20.07.16.
 */
public class Button extends Clickable {
    private GuiTexture gui;
    private GLGuiText text;
    private Color textHoverColor;
    private Color textColor;
    private Color textClickColor;
    public Button(GuiTexture gui, GLGuiText text) {
        super(new RectangleClickArea(gui.getArea()));
        this.gui = gui;
        this.text = text;
        textColor = text.getColor();
        textHoverColor = textColor;
        textClickColor = textColor;
        setListeners();
    }
    public Button(Vector2f position, Vector2f scale, String text, Window window) {
        super(new RectangleClickArea(Maths.getAreaFromPositionAndScale(window, position,scale))); //todo
        this.gui = new GuiTexture(GLLoader.loadTexture("path_cs.png"),position,scale, window);
        this.text = new GLGuiText(new GLTTFont("res/fonts/arial.ttf",64),text,scale.y / 1000, new Color(0),new Vector2f(position));
        this.text.setColor(new Color(0.0,1.0,0.0));
        setTextClickColor(new Color(1.0,0.0,0.0));
        setTextHoverColor(new Color(0.0,0.0,1.0));
        textColor = this.text.getColor();
        setListeners();
    }
    private void setListeners() {
        addOnMouseDownListener(new OnMouseDownListener() {
            @Override
            public void onMouseDown() {
                textColor = text.getColor();
                text.setColor(textClickColor);
            }
        });
        addOnMouseUpListener(new OnMouseUpListener() {
            @Override
            public void onMouseUp() {
                text.setColor(textColor);
            }
        });
        addOnMouseEnterListener(new OnMouseEnterListener() {
            @Override
            public void onMouseEnter() {
                textColor = text.getColor();
                text.setColor(textHoverColor);
            }
        });
        addOnMouseLeaveListener(new OnMouseLeaveListener() {
            @Override
            public void onMouseLeave() {
                text.setColor(textColor);
            }
        });
    }
    public void setTextHoverColor(Color textHoverColor) {
        this.textHoverColor = textHoverColor;
    }
    public void setTextClickColor(Color textClickColor) {
        this.textClickColor = textClickColor;
    }
    public GuiTexture getGui() {
        return gui;
    }
    public GLGuiText getText() {
        return text;
    }
    public void processRender(Scene scene) {
        scene.addGui(gui);
        scene.addText(text);
    }
}
