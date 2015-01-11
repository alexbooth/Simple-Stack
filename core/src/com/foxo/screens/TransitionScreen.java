package com.foxo.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.foxo.simplestack.Assets;
import com.foxo.tween.FloatAccessor;
import com.foxo.tween.TransitionTween;
import com.foxo.tween.TweenableFloat;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;


public class TransitionScreen implements Screen {

    public static final int SLIDE_BOTH_LEFT = 1;
    public static final int SLIDE_BOTH_RIGHT = 2;
    public static final int FADE_OUT_IN = 3;

    private SpriteBatch batcher;
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private Screen current;
    private FrameBuffer currentBuffer;
    private FrameBuffer nextBuffer;
    private Sprite currentScreenSprite;
    private Sprite nextScreenSprite;

    private TweenManager manager;
    private TweenCallback backgroundAnimationTweenComplete;
    private TweenableFloat alpha;
    private int tweenType;

    public TransitionScreen(final Screen current, final Screen next, final Game game, final int tweenType) {
        this.current = current;
        this.tweenType = tweenType;

        batcher = new SpriteBatch();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Assets.WIDTH, Assets.HEIGHT);

        manager = new TweenManager();
        shapeRenderer = new ShapeRenderer();
        alpha = new TweenableFloat();

        backgroundAnimationTweenComplete = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                alpha = new TweenableFloat();
                game.setScreen(next);
            }
        };

        nextBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Assets.WIDTH, Assets.HEIGHT, false);

        nextBuffer.begin();
        next.render(Gdx.graphics.getDeltaTime());
        nextBuffer.end();

        nextScreenSprite = new Sprite(nextBuffer.getColorBufferTexture());
        nextScreenSprite.flip(false, true);

        currentBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Assets.WIDTH, Assets.HEIGHT, false);
        currentBuffer.begin();
        current.render(Gdx.graphics.getDeltaTime());
        currentBuffer.end();

        currentScreenSprite = new Sprite(currentBuffer.getColorBufferTexture());
        currentScreenSprite.flip(false, true);

        switch(tweenType) {
            case SLIDE_BOTH_LEFT:
                tweenToHTP();
                break;
            case SLIDE_BOTH_RIGHT:
                tweenToMenu();
                break;
            case FADE_OUT_IN:
                fadeOutIn();
                break;
            default:
                break;
        }

        if(Assets.debug)
            System.out.println("TransitionScreen attached");
    }

    private void fadeOutIn() {
        currentScreenSprite.setPosition(0, 0);
        nextScreenSprite.setPosition(0, 0);
        nextScreenSprite.setAlpha(0);

        Tween.registerAccessor(TweenableFloat.class, new FloatAccessor());

        Tween.to(alpha, FloatAccessor.FLOAT, 0.4f)
                .repeatYoyo(1, 0)
                .target(1)
                .ease(Linear.INOUT)
                .setCallback(backgroundAnimationTweenComplete)
                .setCallbackTriggers(TweenCallback.COMPLETE)
                .start(manager);
    }

    private void tweenToMenu() {
        Tween.registerAccessor(Sprite.class, new TransitionTween());

        currentScreenSprite.setPosition(0, 0);
        nextScreenSprite.setPosition(-Assets.WIDTH, 0);

        Tween.to(nextScreenSprite, TransitionTween.X,  0.5f)
                .target(0)
                .setCallback(backgroundAnimationTweenComplete)
                .setCallbackTriggers(TweenCallback.COMPLETE)
                .start(manager);

        Tween.to(currentScreenSprite, TransitionTween.X,  0.5f)
                .target(Assets.WIDTH)
                .setCallback(backgroundAnimationTweenComplete)
                .setCallbackTriggers(TweenCallback.COMPLETE)
                .start(manager);
    }

    private void tweenToHTP() {
        Tween.registerAccessor(Sprite.class, new TransitionTween());

        currentScreenSprite.setPosition(0, 0);
        nextScreenSprite.setPosition(Assets.WIDTH, 0);

        Tween.to(nextScreenSprite, TransitionTween.X, 0.5f)
                .target(0)
                .setCallback(backgroundAnimationTweenComplete)
                .setCallbackTriggers(TweenCallback.COMPLETE)
                .start(manager);

        Tween.to(currentScreenSprite, TransitionTween.X, 0.5f)
                .target(-Assets.WIDTH)
                .setCallback(backgroundAnimationTweenComplete)
                .setCallbackTriggers(TweenCallback.COMPLETE)
                .start(manager);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batcher.setProjectionMatrix(camera.combined);

        if(tweenType ==  FADE_OUT_IN) {
            batcher.begin();
            nextScreenSprite.draw(batcher);
            currentScreenSprite.draw(batcher);
            batcher.end();

            Gdx.graphics.getGL20().glEnable(GL20.GL_BLEND);
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeType.Filled);

            if(alpha.getFloat() >= 0.975f) {
                currentScreenSprite.setPosition(-Assets.WIDTH, 0);
                nextScreenSprite.setAlpha(1);
                shapeRenderer.setColor(0, 0, 0, 1);
            } else
                shapeRenderer.setColor(0, 0, 0, alpha.getFloat());

            shapeRenderer.rect(0, 0, Assets.WIDTH, Assets.HEIGHT);
            shapeRenderer.end();
            Gdx.graphics.getGL20().glDisable(GL20.GL_BLEND);
        }
        else {
            batcher.begin();
            currentScreenSprite.draw(batcher);
            nextScreenSprite.draw(batcher);
            batcher.end();
        }

        if(delta <= 0.022f)
            manager.update(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        currentBuffer.dispose();
        nextBuffer.dispose();
        current.dispose();

        if(Assets.debug)
            System.out.println("TransitionScreen disposed");
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void show() {}

    @Override
    public void resume() {}

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}
}