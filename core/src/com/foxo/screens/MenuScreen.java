package com.foxo.screens;

import java.util.ArrayList;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Back;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.foxo.buttons.Button;
import com.foxo.simplestack.Assets;
import com.foxo.tween.BaseImageAccessor;


public class MenuScreen extends CustomScreen implements  InputProcessor {

    private Game game;

    private SpriteBatch batcher;
    private OrthographicCamera camera;

    private ArrayList<Button> buttons;
    private TweenManager manager;
    private float width, height;

    public MenuScreen (Game game) {
        this.game = game;
        this.width = Assets.V_WIDTH;
        this.height = Assets.V_HEIGHT;

        batcher = new SpriteBatch();

        camera = new OrthographicCamera();
        camera.setToOrtho(true, width, height);

        manager = new TweenManager();
        buttons = new ArrayList<>();

        buttons.add(new Button(0.25f, height, width / 2.5f, width / 6f, Assets.playUp,  Assets.playDown));
        buttons.add(new Button(0.35f + width / 2.5f, height, width / 2.5f, width / 6f, Assets.rulesUp,  Assets.rulesDown));

        createTweens();

        Gdx.input.setCatchBackKey(true);

        if(Assets.debug)
            System.out.println("MenuScreen attached");
    }

    public void createTweens() {
        Tween.registerAccessor(Button.class, new BaseImageAccessor());

        Tween.to(buttons.get(0), BaseImageAccessor.POSITION_Y, 0.75f)
                .target(height - width / 6f - 0.2f)
                .ease(Back.OUT)
                .start(manager);

        Tween.to(buttons.get(1), BaseImageAccessor.POSITION_Y, 0.75f)
                .target(height - width / 6f - 0.2f)
                .ease(Back.OUT)
                .delay(0.05f)
                .start(manager);
    }

    @Override
    public void render (float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batcher.setProjectionMatrix(camera.combined);
        batcher.disableBlending();

        batcher.begin();
        batcher.draw(Assets.menuBackground, 0, 0, width, height, 0, 0,  Assets.menuBackground.getWidth(), Assets.menuBackground.getHeight(), false, true);
        batcher.end();

        batcher.enableBlending();

        batcher.begin();
        batcher.draw(Assets.title, 0, 0, width, height, 0, 0, Assets.title.getWidth(), Assets.title.getHeight(), false, true);

        for(Button b : buttons)
            b.draw(batcher);

        batcher.end();

        if(delta <= 0.022f)
            manager.update(delta);
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Keys.BACK || keycode == Keys.B) {
            Assets.dispose();
            Gdx.app.exit();
        }

        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 touchPos = camera.unproject(new Vector3(screenX, screenY, 0));

        for(Button b : buttons)
            b.isTouchDown(touchPos.x, touchPos.y);

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Vector3 touchPos = camera.unproject(new Vector3(screenX, screenY, 0));

        if(buttons.get(0).isTouchUp(touchPos.x, touchPos.y))
            game.setScreen(new TransitionScreen(this, new LevelSelectionScreen(game), game, TransitionScreen.FADE_OUT_IN));
        else if(buttons.get(1).isTouchUp(touchPos.x, touchPos.y)) {
            Gdx.input.setInputProcessor(null);
            game.setScreen(new TransitionScreen(this, new HowToPlayScreen(game), game, TransitionScreen.SLIDE_BOTH_LEFT));
        }

        return true;
    }

    @Override
    public void dispose () {
        batcher.dispose();
        Gdx.input.setInputProcessor(null);

        if(Assets.debug)
            System.out.println("MenuScreen disposed");
    }

    @Override
    public void show () {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) { return true; }

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
    public void hide () {}

    @Override
    public void pause () {}

    @Override
    public void resume () {}
}