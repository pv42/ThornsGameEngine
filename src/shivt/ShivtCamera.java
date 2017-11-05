package shivt;

import engine.graphics.cameras.ThreeDimensionCamera;
import engine.inputs.InputHandler;
import engine.inputs.listeners.InputEventListener;
import org.lwjgl.glfw.GLFW;
import org.joml.Vector3f;

import static engine.inputs.InputEvent.KEY_EVENT;
import static engine.inputs.InputEvent.KEY_PRESS;
import static engine.inputs.InputEvent.KEY_REPEAT;

/***
 * Created by pv42 on 18.09.2016.
 */
public class ShivtCamera extends ThreeDimensionCamera {
    public ShivtCamera() {
        setPosition(new Vector3f(0, 12.5f, 0));
        setYaw(180);
        InputHandler.addListener(new InputEventListener(KEY_EVENT, KEY_REPEAT, GLFW.GLFW_KEY_UP) {
            @Override
            public void onOccur() {
                Vector3f pos = getPosition();
                pos.z += 1;
                setPosition(pos);
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT, KEY_REPEAT, GLFW.GLFW_KEY_DOWN) {
            @Override
            public void onOccur() {

                Vector3f pos = getPosition();
                pos.z -= 1;
                setPosition(pos);
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT, KEY_REPEAT, GLFW.GLFW_KEY_LEFT) {
            @Override
            public void onOccur() {

                Vector3f pos = getPosition();
                pos.x += 1;
                setPosition(pos);
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT, KEY_REPEAT, GLFW.GLFW_KEY_RIGHT) {
            @Override
            public void onOccur() {

                Vector3f pos = getPosition();
                pos.x -= 1;
                setPosition(pos);
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT, KEY_REPEAT, GLFW.GLFW_KEY_PAGE_UP) {
            @Override
            public void onOccur() {

                Vector3f pos = getPosition();
                pos.y += 1;
                setPosition(pos);
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT, KEY_REPEAT, GLFW.GLFW_KEY_PAGE_DOWN) {
            @Override
            public void onOccur() {
                Vector3f pos = getPosition();
                pos.y -= 1;
                setPosition(pos);
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT, KEY_PRESS, GLFW.GLFW_KEY_UP) {
            @Override
            public void onOccur() {
                Vector3f pos = getPosition();
                pos.z += 1;
                setPosition(pos);
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT, KEY_PRESS, GLFW.GLFW_KEY_DOWN) {
            @Override
            public void onOccur() {

                Vector3f pos = getPosition();
                pos.z -= 1;
                setPosition(pos);
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT, KEY_PRESS, GLFW.GLFW_KEY_LEFT) {
            @Override
            public void onOccur() {

                Vector3f pos = getPosition();
                pos.x += 1;
                setPosition(pos);
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT, KEY_PRESS, GLFW.GLFW_KEY_RIGHT) {
            @Override
            public void onOccur() {

                Vector3f pos = getPosition();
                pos.x -= 1;
                setPosition(pos);
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT, KEY_PRESS, GLFW.GLFW_KEY_PAGE_UP) {
            @Override
            public void onOccur() {

                Vector3f pos = getPosition();
                pos.y += 1;
                setPosition(pos);
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT, KEY_PRESS, GLFW.GLFW_KEY_PAGE_DOWN) {
            @Override
            public void onOccur() {
                Vector3f pos = getPosition();
                pos.y -= 1;
                setPosition(pos);
            }
        });
    }

    @Override
    public void move() {
    }
}
