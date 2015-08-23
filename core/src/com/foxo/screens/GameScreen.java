package com.foxo.screens;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.foxo.buttons.Button;
import com.foxo.objects.Block;
import com.foxo.objects.Board;
import com.foxo.objects.GameTimer;
import com.foxo.saving.SaveObject;
import com.foxo.simplestack.Assets;
import com.foxo.simplestack.FontSize;
import com.foxo.background.ShaderBackground;


public class GameScreen implements Screen, InputProcessor {

    private Game game;

    private static final Matrix4 NORMAL_PROJECTION = Assets.NORMAL_PROJECTION;
    private static final float V_HEIGHT = Assets.V_HEIGHT;
    private static final float V_WIDTH = Assets.V_WIDTH;

    private SpriteBatch batcher = Assets.batch;
    private OrthographicCamera camera = Assets.camera;
    private ShapeRenderer shapeRenderer;
    private ShaderBackground background;

    private TweenManager tween;
    private GameTimer timer;

    private BitmapFont font;
    private ShaderProgram shader;

    private Array<Button> wonButtons, pausedButtons, popUpButtons;
    private Button pause;

    private SaveObject save;
    private Board board;
    private int level;

    private boolean popup = false;
    private String welcomeMsg, winMsg;
    private boolean renderReady = false; // TODO find some way to not use this

    private GameState state = GameState.Ready;
    private enum GameState {
        Ready, Paused, Playing, Won
    }

    public GameScreen(Game game, int level, int type) {
        this.game = game;
        this.level = level;
        init(type);
        background = new ShaderBackground(shapeRenderer, camera);

        if(Assets.debug)
            System.out.println("Level: " + level + " GameScreen attached");
    }

    public GameScreen(Game game, int level, int type, ShaderBackground background) {
        this.game = game;
        this.level = level;
        init(type);
        this.background = background;
        if(Assets.debug)
            System.out.println("Level: " + level + " GameScreen attached");
    }

    public void init(int type) {
        camera.setToOrtho(true, V_WIDTH, V_HEIGHT);

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);

        wonButtons = new Array<>();
        pausedButtons = new Array<>();
        popUpButtons = new Array<>();
        tween = new TweenManager();
        board = new Board(this, type);
        save = Assets.save.loadDataValue("level:" + level, SaveObject.class);

        if(save != null && save.getInProgress() && (save.getMoves() > 0 || save.getTime() > 1)) {
            timer = new GameTimer(save.getTime());
            board.setMoves(save.getMoves());
            welcomeMsg = "Tap to resume!";
        }
        else {
            timer = new GameTimer();
            board.saveToJSON();
            save = Assets.save.loadDataValue("level:" + level, SaveObject.class);
            welcomeMsg = "Tap to begin!";
        }

        font = Assets.AlegreyaSans;
        shader = new ShaderProgram(Gdx.files.internal("shaders/outline.vert"), Gdx.files.internal("shaders/outline.frag"));
        createButtons();
    }

    public void createButtons() {
        wonButtons.add(new Button(V_WIDTH / 16f, V_HEIGHT / 2f - V_WIDTH / 16f, V_WIDTH / 4f, V_WIDTH / 8f, Assets.replayUp, Assets.replayDown));
        wonButtons.add(new Button(V_WIDTH / 2 - V_WIDTH / 8f, V_HEIGHT / 2f - V_WIDTH / 16f, V_WIDTH / 4f, V_WIDTH / 8f, Assets.nextLevelUp, Assets.nextLevelDown));
        wonButtons.add(new Button(V_WIDTH - V_WIDTH / 3.2f, V_HEIGHT / 2f - V_WIDTH / 16f, V_WIDTH / 4f, V_WIDTH / 8f, Assets.mainMenuUp, Assets.mainMenuDown));

        pausedButtons.add(new Button(V_WIDTH / 16f, V_HEIGHT / 2f - V_WIDTH / 16f, V_WIDTH / 4f, V_WIDTH / 8f, Assets.restartUp, Assets.restartDown));
        pausedButtons.add(new Button(V_WIDTH / 2 - V_WIDTH / 8f, V_HEIGHT / 2f - V_WIDTH / 16f, V_WIDTH / 4f, V_WIDTH / 8f,Assets.resumeUp, Assets.resumeDown));
        pausedButtons.add(new Button(V_WIDTH - V_WIDTH / 3.2f, V_HEIGHT / 2f - V_WIDTH / 16f, V_WIDTH / 4f, V_WIDTH / 8f, Assets.mainMenuUp, Assets.mainMenuDown));

        pause = new Button(V_WIDTH - V_HEIGHT / 8f - 0.05f, 0.05f, V_HEIGHT / 8f, V_HEIGHT / 8f, Assets.pause, Assets.pause);

        popUpButtons.add(new Button(V_WIDTH / 2 - V_WIDTH / 4f, V_HEIGHT / 2f - V_WIDTH / 8f, V_WIDTH / 4f, V_WIDTH / 4f, Assets.okUp, Assets.okDown));
        popUpButtons.add(new Button(V_WIDTH/ 2, V_HEIGHT / 2f - V_WIDTH / 8f, V_WIDTH / 4f, V_WIDTH / 4f, Assets.cancelUp, Assets.cancelDown));
    }

    @Override
    public void render(float delta) {
        batcher.setProjectionMatrix(camera.combined);

        background.draw(batcher);

        batcher.begin();

        for(Array<Block> stack: board)
            for(Block b: stack) {
                b.draw(batcher);
                renderNumber(b);
            }

        batcher.end();



        batcher.begin();

        if(board.getHeld() != null)
            board.getHeld().draw(batcher);

        float scale = (Assets.WIDTH / V_WIDTH);

        batcher.setShader(shader);

        if(board.getHeld() != null) {
            batcher.setProjectionMatrix(NORMAL_PROJECTION);
            adjustFont(board.getHeld(), scale);
        }

        batcher.end();
        batcher.setShader(null);
        batcher.disableBlending();
        batcher.setProjectionMatrix(camera.combined);

        if (state == GameState.Ready)
            renderReady();
        else if (state == GameState.Paused)
            renderPaused();
        else if (state == GameState.Playing)
            renderPlaying();
        else if (state == GameState.Won)
            renderWon();

        tween.update(delta);
    }

    public void renderReady() {
        Gdx.graphics.getGL20().glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.4f);
        shapeRenderer.rect(0, 0, V_WIDTH, V_HEIGHT);
        shapeRenderer.end();
        Gdx.graphics.getGL20().glDisable(GL20.GL_BLEND);

        batcher.setProjectionMatrix(NORMAL_PROJECTION);
        batcher.setShader(shader);
        font.setScale(FontSize.SIZE_16);
        batcher.enableBlending();
        batcher.begin();

        font.draw(batcher, welcomeMsg, Assets.WIDTH / 2f - font.getBounds(welcomeMsg).width / 2f, Assets.HEIGHT / 2f + font.getBounds(welcomeMsg).height / 2f + 30f);

        batcher.end();
        batcher.disableBlending();
        batcher.setShader(null);
    }

    public void renderPaused() {
        batcher.enableBlending();
        batcher.begin();

        pause.draw(batcher);

        batcher.end();
        batcher.setProjectionMatrix(NORMAL_PROJECTION);
        batcher.setShader(shader);
        font.setScale(FontSize.SIZE_12);
        batcher.begin();

        if(font!= null && timer != null && batcher != null) {
            font.draw(batcher, timer.getTime(), 5, Assets.HEIGHT - 5);                              // TODO this line crashed with a null pointer exception, why???
            font.draw(batcher, "Moves: " + board.getMoves(), Assets.WIDTH / 2, Assets.HEIGHT - 5);
        }

        batcher.end();
        batcher.setShader(null);
        batcher.setProjectionMatrix(camera.combined);

        Gdx.graphics.getGL20().glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.4f);
        shapeRenderer.rect(0, 0, V_WIDTH, V_HEIGHT);
        shapeRenderer.end();
        Gdx.graphics.getGL20().glDisable(GL20.GL_BLEND);

        batcher.begin();

        for(int i = 0; i < pausedButtons.size; i++)
            pausedButtons.get(i).draw(batcher);

        batcher.end();

        if(popup)
            drawPopUp();

        batcher.disableBlending();
    }

    public void renderNumber(Block b) { // TODO something could be optimized here...
        batcher.setShader(shader);
        batcher.setProjectionMatrix(NORMAL_PROJECTION);

        float scale = (Assets.WIDTH / V_WIDTH);

        adjustFont(b, scale);

        batcher.setShader(null);
        batcher.setProjectionMatrix(camera.combined);
    }

    public void adjustFont(Block b, float scale) {
        int num = (b.getSize() - (Assets.MAX_LEVEL - level));
        font.setScale(FontSize.SIZE_8);

        if(b.getSize() == 9 || b.getSize() == 4)
            font.draw(batcher, "" +  num, b.getFloatX() * scale + b.getWidth()  * scale / 2f - font.getBounds("" + num).width / 2f,  Assets.HEIGHT - b.getFloatY() * scale - b.getHeight()  * scale /2.6f);

        else if(b.getSize() != 12 && b.getSize() != 11 && b.getSize() != 8 && b.getSize() != 6)
            font.draw(batcher, "" +  num, b.getFloatX() * scale + b.getWidth()  * scale / 2f - font.getBounds("" + num).width / 2f,  Assets.HEIGHT - b.getFloatY() * scale - b.getHeight()  * scale /2.5f);
        else
            font.draw(batcher, "" +  num, b.getFloatX() * scale + b.getWidth()  * scale / 2f - font.getBounds("" + num).width / 2f,  Assets.HEIGHT - b.getFloatY() * scale - b.getHeight()  * scale /2.2f);
    }

    public void renderPlaying() {
        timer.update();

        batcher.enableBlending();
        batcher.begin();

        pause.draw(batcher);

        batcher.end();
        batcher.setProjectionMatrix(NORMAL_PROJECTION);
        batcher.setShader(shader);
        font.setScale(FontSize.SIZE_12);
        font.setColor(Color.WHITE);
        batcher.begin();

        font.draw(batcher, timer.getTime(), 5, Assets.HEIGHT - 5);
        //font.draw(batcher, "FPS: " + Gdx.graphics.getFramesPerSecond(), Assets.WIDTH / 2, Assets.HEIGHT - 5);
        font.draw(batcher, "Moves: " + board.getMoves(), Assets.WIDTH / 2, Assets.HEIGHT - 5);

        batcher.end();
        batcher.setShader(null);
        batcher.disableBlending();
    }

    public void renderWon() {
        Gdx.graphics.getGL20().glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.4f);
        shapeRenderer.rect(0, 0, V_WIDTH, V_HEIGHT);
        shapeRenderer.end();
        Gdx.graphics.getGL20().glDisable(GL20.GL_BLEND);

        batcher.enableBlending();
        batcher.begin();

        wonButtons.get(0).draw(batcher);
        wonButtons.get(2).draw(batcher);

        if(level != Assets.MAX_LEVEL)
            wonButtons.get(1).draw(batcher);

        batcher.end();

        batcher.setProjectionMatrix(NORMAL_PROJECTION);
        batcher.setShader(shader);
        font.setScale(FontSize.SIZE_12);
        batcher.begin();

        font.draw(batcher, winMsg, Assets.WIDTH / 2 - font.getBounds(winMsg).width / 2, Assets.HEIGHT - 100);
        font.draw(batcher, timer.getTime(), Assets.WIDTH / 3 - font.getBounds(timer.getTime()).width/2, Assets.HEIGHT - 5);
        font.draw(batcher, "Moves: " + board.getMoves(), Assets.WIDTH * 2 / 3 - font.getBounds("Moves: " + board.getMoves()).width/2, Assets.HEIGHT - 5);

        batcher.end();

        batcher.setShader(null);
        batcher.disableBlending();
    }

    public void drawPopUp() {
        Gdx.graphics.getGL20().glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.4f);
        shapeRenderer.rect(0, 0, V_WIDTH, V_HEIGHT);
        shapeRenderer.end();
        Gdx.graphics.getGL20().glDisable(GL20.GL_BLEND);

        batcher.begin();

        for(Button b: popUpButtons)
            b.draw(batcher);

        batcher.end();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (board.getHeld() == null && (keycode == Keys.BACK || keycode == Keys.B)) {
            if (state == GameState.Playing) {
                state = GameState.Paused;
                board.saveToJSON();
            }
            else if (state == GameState.Paused)
                state = GameState.Playing;
            else if (state == GameState.Ready)
                game.setScreen(new TransitionScreen(this, new LevelSelectionScreen(game), game, TransitionScreen.FADE_OUT_IN));
        }

        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 touchPos = camera.unproject(new Vector3(screenX, screenY, 0));

        if(state == GameState.Playing) {
            if(!pause.isTouchDown(touchPos.x, touchPos.y))
                board.touchDown(touchPos.x, touchPos.y);
        }
        else if(state == GameState.Paused && !popup)
            for(Button b: pausedButtons)
                b.isTouchDown(touchPos.x, touchPos.y);
        else if(popup) {
            if (touchPos.y > V_HEIGHT / 2f + V_WIDTH / 16f)
                for (Button b : popUpButtons)
                    b.isTouchDown(touchPos.x, touchPos.y);
        }
        else if(state == GameState.Won)
            for(Button b: wonButtons)
                b.isTouchDown(touchPos.x, touchPos.y);

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Vector3 touchPos = camera.unproject(new Vector3(screenX, screenY, 0));

        if (state == GameState.Ready)
            state = GameState.Playing;
        else if (state == GameState.Playing) {
            if(pause.isTouchUp(touchPos.x, touchPos.y)) {
                state = GameState.Paused;
                board.saveToJSON();
            }

            if(board.touchUp(touchPos.x)) {
                boolean	recordTime = timer.getTimeFloat() < save.getBestTime();
                boolean recordMoves = board.getMoves() < save.getBestMoves();

                if(recordTime && recordMoves)
                    winMsg = "Record time and number of moves!";
                else if (recordTime)
                    winMsg = "Record time!";
                else if (recordMoves)
                    winMsg = "Record number of moves!";
                else
                    winMsg = "";

                board.handleHighScores();
                state = GameState.Won;
            }
        }
        else if(state == GameState.Paused && !popup) {      // TODO
            if(pausedButtons.get(0).isTouchUp(touchPos.x, touchPos.y))
                popup = true;
            else if(pausedButtons.get(1).isTouchUp(touchPos.x, touchPos.y))
                state = GameState.Playing;
            else if(pausedButtons.get(2).isTouchUp(touchPos.x, touchPos.y)) { // TODO is this dead code??? dispose??
                Gdx.input.setInputProcessor(null);
                game.setScreen(new TransitionScreen(this, new MenuScreen(game), game, TransitionScreen.FADE_OUT_IN));
            }
        }
        else if(popup) {
            if(popUpButtons.get(0).isTouchUp(touchPos.x, touchPos.y)) {
                board.endGame();
                game.setScreen(new GameScreen(game, level, Board.REPLAY, background));
            }
            else if (popUpButtons.get(1).isTouchUp(touchPos.x, touchPos.y))
                popup = false;
        }
        else if(state == GameState.Won) {               // TODO
            if(wonButtons.get(0).isTouchUp(touchPos.x, touchPos.y)) {
                game.setScreen(new GameScreen(game, level, Board.REPLAY, background));
            }
            else if(wonButtons.get(1).isTouchUp(touchPos.x, touchPos.y) && level != Assets.MAX_LEVEL) {
                game.setScreen(new GameScreen(game, level + 1, Board.NEXT_LEVEL, background));
            }
            else if(wonButtons.get(2).isTouchUp(touchPos.x, touchPos.y)) {
                Gdx.input.setInputProcessor(null);
                //dispose(); // TODO dispose???
                game.setScreen(new TransitionScreen(this, new MenuScreen(game), game, TransitionScreen.FADE_OUT_IN));
            }
        }

        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector3 touchPos = camera.unproject(new Vector3(screenX, screenY, 0));

        if(state == GameState.Playing)
            board.touchDragged(touchPos.x, touchPos.y);

        return true;
    }

    @Override
    public void dispose () {
        shapeRenderer.dispose();
        background.dispose();
        shader.dispose();
        font.dispose();

        Gdx.input.setInputProcessor(null);

        if(Assets.debug)
            System.out.println("Level " + level  +" GameScreen disposed");
    }

    public int getLevel() {
        return level;
    }

    public GameTimer getTimer() {
        return timer;
    }

    public TweenManager getTweenManager() {
        return tween;
    }

    @Override
    public void hide () {}

    @Override
    public void show () {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) { return true; }

    @Override
    public boolean scrolled(int amount) { return true; }

    @Override
    public boolean keyUp(int keycode) { return true; }

    @Override
    public boolean keyTyped(char character) { return true; }

    @Override
    public void resize (int width, int height) {}

    @Override
    public void pause () {}

    @Override
    public void resume () {}
}