package com.foxo.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.foxo.buttons.Button;
import com.foxo.objects.Board;
import com.foxo.objects.GameTimer;
import com.foxo.saving.SaveObject;
import com.foxo.simplestack.Assets;
import com.foxo.simplestack.FontSize;
import com.foxo.tween.BaseImageAccessor;
import com.foxo.tween.FloatAccessor;
import com.foxo.tween.TweenableFloat;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Quad;


public class LevelSelectionScreen implements Screen, InputProcessor {

    private Game game;

    private static final Matrix4 NORMAL_PROJECTION = Assets.NORMAL_PROJECTION;
    private static final float V_HEIGHT = Assets.V_HEIGHT;
    private static final float V_WIDTH = Assets.V_WIDTH;

    private SpriteBatch batch = Assets.batch;
    private OrthographicCamera camera = Assets.camera;

    private TweenableFloat gipy;
    private TweenManager tween;

    private BitmapFont font;
    private ShaderProgram shader;

    private Button beginButton, minusButton, plusButton;
    private boolean inProg[];
    private String bestTimes[], bestMoves[];
    private float blockTo[][], blockFrom[][];
    private int lockAboveIndex = 2, page = 3;


    public LevelSelectionScreen(Game game) {
        this.game = game;

        camera.setToOrtho(true, V_WIDTH, V_HEIGHT);

        font = Assets.AlegreyaSans;
        font.setScale(FontSize.SIZE_12);
        shader = new ShaderProgram(Gdx.files.internal("shaders/outline.vert"), Gdx.files.internal("shaders/outline.frag"));

        beginButton = new Button(0.1f,  V_HEIGHT - 0.6f, 1.2f, 0.5f, Assets.playUp,  Assets.playDown);
        plusButton  = new Button(V_WIDTH - 0.36f, 1.27f, 0.3f, 0.3f, Assets.plusUp, Assets.plusDown);
        minusButton = new Button(1.4f, 1.27f, 0.3f, 0.3f, Assets.minusUp, Assets.minusDown);

        tween = new TweenManager();
        gipy = new TweenableFloat(-font.getBounds("Game in progress!").height - 50);

        blockTo = new float[Assets.MAX_LEVEL][2];
        blockFrom = new float[Assets.MAX_LEVEL][2];

        for(int i = 0; i < Assets.MAX_LEVEL; i++) {
            Assets.blocks[i].setWidth(0.9f);
            Assets.blocks[i].setHeight(0.2f);
            System.out.println("Current width" + Assets.blocks[i].getHeight());
            blockTo[i][0] = V_WIDTH * 0.72f - Assets.blocks[i].getWidth() / 2f;
            blockTo[i][1] = V_HEIGHT - (V_HEIGHT / 3f) - i * (V_WIDTH / 30f);
            //blockTo[i][1] = V_HEIGHT - (V_HEIGHT / 3.4f) - i * (V_WIDTH / 26.67f);
            blockFrom[i][0] = V_WIDTH;
            blockFrom[i][1] = V_HEIGHT;


            if(i < 3)
                Assets.blocks[i].setPos(blockTo[i][0], blockTo[i][1]);
            else
                Assets.blocks[i].setPos(blockFrom[i][0], blockFrom[i][1]);
        }

        loadScores();
        tweenGIP();

        if(Assets.debug)
            System.out.println("LevelSelectionScreen attached");
    }

    public void loadScores() {
        bestTimes = new String[Assets.MAX_LEVEL];
        bestMoves = new String[Assets.MAX_LEVEL];
        inProg = new boolean[Assets.MAX_LEVEL];

        for(int i = 1; i < Assets.MAX_LEVEL + 1; i++) {
            SaveObject save = Assets.save.loadDataValue("level:" + i, SaveObject.class);
            inProg[i - 1] = false;
            bestMoves[i - 1]  = "n/a";
            bestTimes[i - 1] = "n/a";

            if(save != null) {
                if(save.getBestTime() != Float.MAX_VALUE) {
                    bestTimes[i - 1] = GameTimer.prettyTime(save.getBestTime());

                    if (i > 2)
                        lockAboveIndex = i;
                }

                if(save.getBestMoves() != Integer.MAX_VALUE)
                    bestMoves[i - 1] = "" + save.getBestMoves();

                if(save.getInProgress() && (save.getMoves() > 0 || save.getTime() > 1))
                    inProg[i - 1] = true;
            }
        }
    }

    @Override
    public void render (float delta) {
        batch.enableBlending();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(Assets.menuBackground, 0, 0, V_WIDTH, V_HEIGHT, 0, 0, Assets.menuBackground.getWidth(), Assets.menuBackground.getHeight(), false, false);

        if(page > lockAboveIndex + 1)
            batch.setColor(190 / 255f, 190 / 255f, 190 / 255f, 1);

        for(int i = 0; i < Assets.MAX_LEVEL; i++)
           Assets.blocks[i].draw(batch);

        beginButton.draw(batch);
        batch.setColor(1, 1, 1, 1);

        minusButton.draw(batch);
        plusButton.draw(batch);

        batch.setProjectionMatrix(NORMAL_PROJECTION);
        batch.setShader(shader);

        font.setScale(FontSize.SIZE_13);
        font.draw(batch, "Best time: " + bestTimes[page - 1], Assets.WIDTH / 30f,
                Gdx.graphics.getHeight() + font.getBounds("Best time: " + bestTimes[page - 1]).height  - Assets.WIDTH / 5f);
        font.draw(batch, "Best moves: " + bestMoves[page - 1],  Assets.WIDTH / 30f,
                Gdx.graphics.getHeight() + font.getBounds("Best time: " + bestMoves[page - 1]).height  - Assets.WIDTH / 3.5f);

        font.draw(batch, "Game in progress!", Assets.WIDTH / 14f,  Assets.HEIGHT - gipy.getFloat());

        font.setScale(FontSize.SIZE_14);
        font.draw(batch, "Level: " + page, Assets.WIDTH * 0.72f - font.getBounds("Level: " + page).width / 2f, Assets.HEIGHT / 6f + font.getBounds("Level: " + page).height / 2f - 5);

        batch.setShader(null);
        batch.end();
        renderCorners();
        tween.update(delta);
    }

    public void renderCorners() {
        batch.begin();
        batch.draw(Assets.corner, Assets.WIDTH - Assets.corner.getRegionWidth(), Assets.HEIGHT - Assets.corner.getRegionHeight());
        batch.draw(Assets.corner, Assets.corner.getRegionWidth(), Assets.HEIGHT - Assets.corner.getRegionHeight(), -Assets.corner.getRegionWidth(), Assets.corner.getRegionHeight());
        batch.draw(Assets.corner, Assets.corner.getRegionWidth(), Assets.corner.getRegionHeight(), -Assets.corner.getRegionWidth(), -Assets.corner.getRegionHeight());
        batch.draw(Assets.corner, Assets.WIDTH - Assets.corner.getRegionWidth(), Assets.corner.getRegionHeight(), Assets.corner.getRegionWidth(), -Assets.corner.getRegionHeight());
        batch.end();
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Keys.BACK || keycode == Keys.B)
            game.setScreen(new TransitionScreen(this, new MenuScreen(game), game, TransitionScreen.FADE_OUT_IN));
        return false;
    }

    public void tweenGIP() {
        font.setScale(FontSize.SIZE_12);

        if(inProg[page - 1]) {
            Tween.to(gipy, FloatAccessor.FLOAT, 0.3f)
                    .target(0)
                    .ease(Quad.INOUT)
                    .start(tween);
        }
        else {
            Tween.to(gipy, FloatAccessor.FLOAT, 0.3f)
                    .target(-font.getBounds("Game in progress!").height - 50)
                    .ease(Quad.INOUT)
                    .start(tween);
        }
    }

    @Override
    public void dispose () {
        shader.dispose();
        font.dispose();
        Gdx.input.setInputProcessor(null);

        if(Assets.debug)
            System.out.println("LevelSelectionScreen disposed");
    }

    @Override
    public void show () {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 touchPos = camera.unproject(new Vector3(screenX, screenY, 0));
        if(page <= lockAboveIndex + 1)
            beginButton.isTouchDown(touchPos.x, touchPos.y);

        if(minusButton.isTouchDown(touchPos.x, touchPos.y) && page > 1) {
            page--;
            Tween.to(Assets.blocks[page], BaseImageAccessor.POSITION_XY, 0.3f)
                    .target(blockFrom[page][0], blockFrom[page][1])
                    .ease(Quad.INOUT)
                    .start(tween);
            tweenGIP();
        }
        else if(plusButton.isTouchDown(touchPos.x, touchPos.y) && page < 12) {
            page++;
            Tween.to(Assets.blocks[page - 1], BaseImageAccessor.POSITION_XY, 0.3f)
                    .target(blockTo[page - 1][0], blockTo[page - 1][1])
                    .ease(Quad.INOUT)
                    .start(tween);
            tweenGIP();
        }

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Vector3 touchPos = camera.unproject(new Vector3(screenX, screenY, 0));
        if(beginButton.isTouchUp(touchPos.x, touchPos.y) && page <= lockAboveIndex + 1) {
            Gdx.input.setInputProcessor(null);
            game.setScreen(new TransitionScreen(screenShotandDispose(), new GameScreen(game, page, Board.NEW_GAME), game, TransitionScreen.FADE_OUT_IN));
        }
        minusButton.isTouchUp(touchPos.x, touchPos.y);
        plusButton.isTouchUp(touchPos.x, touchPos.y);

        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
       return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        return true;
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void hide () {}

    @Override
    public void pause () {}

    @Override
    public void resume () {}

    public FrameBuffer screenShotandDispose() {      // TODO: keep an eye on this, may start memory leak
            FrameBuffer currentBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Assets.WIDTH, Assets.HEIGHT, false);
            currentBuffer.begin();
            render(0);
            currentBuffer.end();
            dispose();
            return currentBuffer;
    }
}