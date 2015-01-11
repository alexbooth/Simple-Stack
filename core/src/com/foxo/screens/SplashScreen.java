package com.foxo.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;
import com.foxo.simplestack.Assets;


public class SplashScreen implements Screen {

    private Game game;

    private SpriteBatch batcher;
    private OrthographicCamera camera;

    private boolean initialDraw;
    private boolean loadStarted;
    private boolean transition;

    private long startTime;

    public SplashScreen(Game game) {
        this.game = game;

        batcher = new SpriteBatch();

        camera = new OrthographicCamera();
        camera.setToOrtho(true, Assets.WIDTH, Assets.HEIGHT);

        initialDraw = false;
        loadStarted = false;
        transition = false;

        Gdx.input.setCatchBackKey(true);
        if(Assets.debug) System.out.println("SplashScreen attached");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batcher.disableBlending();
        batcher.setProjectionMatrix(camera.combined);
        batcher.begin();
        batcher.draw(Assets.splash, 0, 0, Assets.WIDTH, Assets.HEIGHT, 0, 0,  Assets.splash.getWidth(), Assets.splash.getHeight(), false, true);
        batcher.end();

        if(!initialDraw) {
            startTime = TimeUtils.nanoTime();
            initialDraw = true;
        }
        else if(!loadStarted) {
            loadStarted = true;
            Assets.load();
        } else if (!transition && TimeUtils.nanoTime() - startTime >= 1250000000L) {
            if(Assets.debug) System.out.println("Starting after " + (float) (TimeUtils.nanoTime() - startTime)/1000000000L);
            transition = true;
            game.setScreen(new TransitionScreen(this, new MenuScreen(game), game, TransitionScreen.FADE_OUT_IN));
        }
    }

    @Override
    public void dispose() {
        batcher.dispose();

        if(Assets.debug)
            System.out.println("SplashScreen disposed");
    }

    @Override
    public void show() {}

    @Override
    public void resize(int width, int height) {}

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}
}