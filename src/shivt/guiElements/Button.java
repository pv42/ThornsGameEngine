package shivt.guiElements;

import engine.graphics.fontMeshCreator.GUIText;
import engine.graphics.guis.GuiTexture;
import engine.inputs.Clickable;
import engine.inputs.clickAreas.RectangleClickArea;
import engine.inputs.listeners.OnMouseDownListener;
import engine.inputs.listeners.OnMouseEnterListener;
import engine.inputs.listeners.OnMouseLeaveListener;
import engine.inputs.listeners.OnMouseUpListener;
import engine.graphics.renderEngine.Loader;
import engine.graphics.renderEngine.MasterRenderer;
import engine.toolbox.Color;
import engine.toolbox.Maths;
import org.lwjgl.util.vector.Vector2f;

/***
 * Created by pv42 on 20.07.16.
 */
public class Button extends Clickable {
    private GuiTexture gui;
    private GUIText text;
    private Color textHoverColor;
    private Color textColor;
    private Color textClickColor;
    public Button(GuiTexture gui, GUIText text) {
        super(new RectangleClickArea(gui.getArea()));
        this.gui = gui;
        this.text = text;
        textColor = text.getColor();
        textHoverColor = textColor;
        textClickColor = textColor;
        setListeners();
    }
    public Button(Vector2f position, Vector2f scale,String text) {
        super(new RectangleClickArea(Maths.getAreaFromPositionAndScale(position,scale))); //todo
        this.gui = new GuiTexture(Loader.loadTexture("path_cs.png"),position,scale);
        this.text = new GUIText(text,scale.y * 22,Loader.loadFont("courier_df"),new Vector2f(position.x,position.y),1,false);
        this.text.setColor(new Color(0.0,1.0,0.0));
        this.text.setBorderColor(0,1,0);
        this.text.setBorderWidth(0);
        this.text.setEdge(0.03f);
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
    public GUIText getText() {
        return text;
    }
    public void processRender() {
        MasterRenderer.processGui(gui);
        MasterRenderer.processText(text);
    }
}
