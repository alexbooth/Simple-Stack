package com.foxo.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.TimeUtils;
import com.foxo.objects.Board;
import com.foxo.simplestack.Assets;


public class SplashScreen implements Screen {

    private Game game;

    private SpriteBatch batch = Assets.batch;
    private OrthographicCamera camera = Assets.camera;

    private boolean initialDraw = false;
    private boolean loadStarted = false;
    private boolean transition = false;

    private long startTime;

    public SplashScreen(Game game) {
        this.game = game;
        if(Assets.debug) System.out.println("SplashScreen attached");
    }

    @Override
    public void render(float delta) {
        camera.setToOrtho(true, Assets.WIDTH, Assets.HEIGHT);
        batch.setProjectionMatrix(camera.combined);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.disableBlending();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(Assets.splash, 0, 0, Assets.WIDTH, Assets.HEIGHT, 0, 0, Assets.splash.getWidth(), Assets.splash.getHeight(), false, true);
        batch.end();

        tick();
    }

    public void tick() {
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

            //game.setScreen(new TransitionScreen(this, new GameScreen(game, 3, Board.NEW_GAME), game, TransitionScreen.FADE_OUT_IN));
            game.setScreen(new TransitionScreen(this, new MenuScreen(game), game, TransitionScreen.FADE_OUT_IN));
        }
    }

    @Override
    public void dispose() {
        if(Assets.debug)
            System.out.println("SplashScreen disposed");
    }

    @Override
    public void show() {
        Gdx.input.setCatchBackKey(true);
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}
}