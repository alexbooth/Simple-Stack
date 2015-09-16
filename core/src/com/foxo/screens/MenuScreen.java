package com.foxo.screens;

import java.util.ArrayList;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Back;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.foxo.buttons.Button;
import com.foxo.simplestack.Assets;
import com.foxo.tween.BaseImageAccessor;


public class MenuScreen implements Screen,  InputProcessor {

    private Game game;

    private static final String version = "v1.10";

    private SpriteBatch batch = Assets.batch;
    private OrthographicCamera camera = Assets.camera;

    private ArrayList<Button> buttons;
    private TweenManager manager;
    private BitmapFont font;
    private float width, height;

    public MenuScreen (Game game) {
        this.game = game;
        this.width = Assets.V_WIDTH;
        this.height = Assets.V_HEIGHT;

        manager = new TweenManager();
        buttons = new ArrayList<>();

        font = new BitmapFont();
        font.setColor(1, 1, 1, 1);

        buttons.add(new Button(0.25f, height, width / 2.5f, width / 6f, Assets.playUp,  Assets.playDown));
        buttons.add(new Button(0.35f + width / 2.5f, height, width / 2.5f, width / 6f, Assets.rulesUp,  Assets.rulesDown));

        createTweens();

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
        batch.setShader(null);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.setToOrtho(true, width, height);
        batch.setProjectionMatrix(camera.combined);
        batch.disableBlending();

        batch.begin();
        batch.draw(Assets.menuBackground, 0, 0, width, height, 0, 0, Assets.menuBackground.getWidth(), Assets.menuBackground.getHeight(), false, true);
        batch.end();

        batch.enableBlending();

        batch.begin();
        batch.draw(Assets.title, 0, 0, width, height, 0, 0, Assets.title.getWidth(), Assets.title.getHeight(), false, true);

        for(Button b : buttons)
            b.draw(batch);

        batch.end();
        batch.setProjectionMatrix(Assets.NORMAL_PROJECTION);
        renderCorners();
        tick(delta);
    }

    public void renderCorners() {
        batch.begin();
        font.draw(batch, version, Assets.WIDTH - font.getBounds(version).width - 5, font.getBounds(version).height + 5);
        batch.draw(Assets.corner, Assets.WIDTH - Assets.corner.getRegionWidth(), Assets.HEIGHT - Assets.corner.getRegionHeight());
        batch.draw(Assets.corner, Assets.corner.getRegionWidth(), Assets.HEIGHT - Assets.corner.getRegionHeight(), -Assets.corner.getRegionWidth(), Assets.corner.getRegionHeight());
        batch.draw(Assets.corner, Assets.corner.getRegionWidth(), Assets.corner.getRegionHeight(), -Assets.corner.getRegionWidth(), -Assets.corner.getRegionHeight());
        batch.draw(Assets.corner, Assets.WIDTH - Assets.corner.getRegionWidth(), Assets.corner.getRegionHeight(), Assets.corner.getRegionWidth(), -Assets.corner.getRegionHeight());
        batch.end();
    }

    public void tick(float delta) {
        if(delta <= 0.022f)
            manager.update(delta);
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Keys.BACK || keycode == Keys.B) {
            Assets.dispose();
            dispose();
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
        Gdx.input.setInputProcessor(null);

        if(Assets.debug)
            System.out.println("MenuScreen disposed");
    }

    @Override
    public void show () {
        Gdx.input.setInputProcessor(this);
        Gdx.input.setCatchBackKey(true);
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