package com.foxo.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.foxo.buttons.Button;
import com.foxo.simplestack.Assets;
import com.foxo.simplestack.FontSize;


public class HowToPlayScreen implements Screen, InputProcessor {

    private Game game;

    private static final Matrix4 normalProjection = Assets.NORMAL_PROJECTION;
    private static final String line1 = "1. Move the stack to the rightmost base";
    private static final String line2 = "2. Rocks can only be put on larger rocks";

    private SpriteBatch batch;
    private OrthographicCamera camera;

    private BitmapFont font;
    private ShaderProgram shader;

    private Button backButton;

    public HowToPlayScreen(Game game) {
        this.game = game;

        batch = Assets.batch;
        camera = Assets.camera;

        font = Assets.AlegreyaSans;
        font.setScale(FontSize.SIZE_12);
        shader = new ShaderProgram(Gdx.files.internal("shaders/outline.vert"), Gdx.files.internal("shaders/outline.frag"));

        backButton = new Button(Assets.WIDTH / 20, Assets.HEIGHT - Assets.WIDTH / 20 - Assets.WIDTH / 10, Assets.WIDTH / 10, Assets.WIDTH / 10, Assets.backUp, Assets.backDown);

        if(Assets.debug)
            System.out.println("HowToPlayScreen attached");
    }

    @Override
    public void render(float delta) {
        camera.setToOrtho(true, Assets.WIDTH, Assets.HEIGHT);
        batch.setProjectionMatrix(camera.combined);
        batch.disableBlending();
        batch.setShader(null);

        batch.begin();
        batch.draw(Assets.htpBackground, 0, 0, Assets.WIDTH, Assets.HEIGHT, 0, 0, Assets.htpBackground.getWidth(), Assets.htpBackground.getHeight(), false, true);
        batch.end();

        batch.enableBlending();
        batch.begin();
        backButton.draw(batch);
        batch.end();

        batch.setShader(shader);
        batch.setProjectionMatrix(normalProjection);

        batch.begin();
        font.draw(batch, line2, Assets.WIDTH / 2 - font.getBounds(line2).width / 2, Assets.HEIGHT / 3 + font.getBounds(line2).height * 2);
        font.draw(batch, line1, Assets.WIDTH / 2 - font.getBounds(line1).width / 2, Assets.HEIGHT / 1.5f + font.getBounds(line1).height * 2);
        batch.end();
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Keys.BACK || keycode == Keys.B)
            game.setScreen(new TransitionScreen(this, new MenuScreen(game), game, TransitionScreen.SLIDE_BOTH_RIGHT));

        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        backButton.isTouchDown(screenX, screenY);

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(backButton.isTouchUp(screenX, screenY)) {
            Gdx.input.setInputProcessor(null);
            game.setScreen(new TransitionScreen(this, new MenuScreen(game), game, TransitionScreen.SLIDE_BOTH_RIGHT));
        }

        return true;
    }

    @Override
    public void dispose () {
        shader.dispose();
        font.dispose();
        Gdx.input.setInputProcessor(null);

        if(Assets.debug)
            System.out.println("HowToPlayScreen disposed");
    }

    @Override
    public void show () {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public boolean keyUp(int keycode) { return true; }

    @Override
    public boolean keyTyped(char character) { return true; }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) { return true; }

    @Override
    public boolean mouseMoved(int screenX, int screenY) { return true; }

    @Override
    public boolean scrolled(int amount) { return true; }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}
}