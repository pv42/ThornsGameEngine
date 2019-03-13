package engine.graphics.display;


import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryUtil;

import static engine.toolbox.Settings.HEIGHT;
import static engine.toolbox.Settings.LIMIT_FPS;
import static engine.toolbox.Settings.WIDTH;



public class GLFWWindow {


    private long id;


    private GLFWWindow(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void destroy() {
        GLFW.glfwDestroyWindow(id);
    }

    public void makeCurrentContext() {
        GLFW.glfwMakeContextCurrent(id);
    }

    public void show() {
        GLFW.glfwShowWindow(id);
    }

    public boolean isCloseRequested() {
        return GLFW.glfwWindowShouldClose(id);
    }

    public Vector2i getSize() {
        int[] h = new int[1],w = new int[1];
        GLFW.glfwGetWindowSize(id,h,w);
        return new Vector2i(w[0],h[0]);
    }
    public void setSize(int width, int height) {
        GLFW.glfwSetWindowSize(id, width, height);
    }
    public void setTitle(String title) {
        GLFW.glfwSetWindowTitle(id, title);
    }
}
