package shivt;

import engine.inputs.GuiClickController;
import engine.inputs.listeners.OnClickListener;
import engine.graphics.display.DisplayManager;
import engine.graphics.renderEngine.Loader;
import engine.toolbox.Log;
import org.joml.Vector2f;
import shivt.guiElements.Button;
import shivt.levels.Level;
import shivt.levels.RenderLevel;

/***
 * Created by pv42 on 20.07.16.
 */
public class ShivtGame {
    private static final String TAG = "ShivtGame";
    private static final int GAME_STATE_NULL = 0;
    private static final int GAME_STATE_MAIN_MENU = 1;
    private static final int GAME_STATE_LEVEL = 2;
    private int gameState;
    private int requestedGameState;
    private ShivtGameLoop gameLoop;
    private ShivtCamera camera;
    private GuiClickController guiClickController;
    public static void main(String[] argv) {
        new ShivtGame();
    }
    private ShivtGame() {
        gameState = GAME_STATE_NULL;
        requestedGameState = GAME_STATE_MAIN_MENU;
        gameLoop = new ShivtGameLoop();
        guiClickController = new GuiClickController();
        requestedGameState(GAME_STATE_MAIN_MENU);
        camera = new ShivtCamera();
        Level level = Level.readFromFile("test");
        RenderLevel renderLevel = new RenderLevel(level,Loader.loadFont("courier_df"));
        while (!DisplayManager.isCloseRequested()) {
            gameLoop.loop(renderLevel,camera);
            applyGameState();
        }
        gameLoop.finish();
    }
    private void requestedGameState(int state) {
        requestedGameState = state;
    }
    private void applyGameState() {
        if(requestedGameState == gameState) return;
        Log.i(TAG,"changing game state");
        if(requestedGameState == GAME_STATE_MAIN_MENU) mainMenu();
        if(requestedGameState == GAME_STATE_LEVEL) loadLevel(1);
        gameState = requestedGameState;
    }
    private void mainMenu() {
        Button startBtn = new Button(new Vector2f(),new Vector2f(.7f,.3f),"start");
        guiClickController.addClickable(startBtn);
        startBtn.addOnClickListener(new OnClickListener() {
            @Override
            public void onClick() {
                requestedGameState(GAME_STATE_LEVEL);
            }
        });
        gameLoop.addButton(startBtn);
    }
    private void loadLevel(int i) {
        gameLoop.removeAll();
        Button menuBtn = new Button(new Vector2f(-.8f,-.8f),new Vector2f(0.1f,0.1f),"Menu");
        gameLoop.addButton(menuBtn);
    }
}