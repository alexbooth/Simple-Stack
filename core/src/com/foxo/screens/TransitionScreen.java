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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.foxo.simplestack.Assets;
import com.foxo.tween.FloatAccessor;
import com.foxo.tween.TransitionTween;
import com.foxo.tween.TweenableFloat;
import com.badlogic.gdx.graphics.GL20;


public class TransitionScreen implements Screen {

    public static final int SLIDE_BOTH_LEFT = 0;
    public static final int SLIDE_BOTH_RIGHT = 1;
    public static final int FADE_OUT_IN = 2;

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    private Sprite currentScreenSprite;
    private Sprite nextScreenSprite;

    private TweenManager manager;
    private TweenCallback transitionTweenComplete;
    private TweenableFloat alpha;
    private int tweenType;

    public TransitionScreen(CustomScreen current, final CustomScreen next, final Game game, int tweenType) {
        this.tweenType = tweenType;

        batch = new SpriteBatch();
        alpha = new TweenableFloat();
        manager = new TweenManager();
        shapeRenderer = new ShapeRenderer();

        currentScreenSprite = new Sprite(current.getBuffer());
        nextScreenSprite = new Sprite(next.getBuffer());

        currentScreenSprite.flip(false, true);
        nextScreenSprite.flip(false, true);

        transitionTweenComplete = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                game.setScreen(next);
            }
        };

        switch(tweenType) {
            case SLIDE_BOTH_LEFT:   slideBothLeft();    break;
            case SLIDE_BOTH_RIGHT:  slideBothRight();   break;
            case FADE_OUT_IN:       fadeOutIn();        break;
        }
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
                .setCallback(transitionTweenComplete)
                .setCallbackTriggers(TweenCallback.COMPLETE)
                .start(manager);
    }

    private void slideBothRight() {
        Tween.registerAccessor(Sprite.class, new TransitionTween());

        currentScreenSprite.setPosition(0, 0);
        nextScreenSprite.setPosition(-Assets.WIDTH, 0);

        Tween.to(nextScreenSprite, TransitionTween.X,  0.5f)
                .target(0)
                .setCallback(transitionTweenComplete)
                .setCallbackTriggers(TweenCallback.COMPLETE)
                .start(manager);

        Tween.to(currentScreenSprite, TransitionTween.X,  0.5f)
                .target(Assets.WIDTH)
                .setCallback(transitionTweenComplete)
                .setCallbackTriggers(TweenCallback.COMPLETE)
                .start(manager);
    }

    private void slideBothLeft() {
        Tween.registerAccessor(Sprite.class, new TransitionTween());

        currentScreenSprite.setPosition(0, 0);
        nextScreenSprite.setPosition(Assets.WIDTH, 0);

        Tween.to(nextScreenSprite, TransitionTween.X, 0.5f)
                .target(0)
                .setCallback(transitionTweenComplete)
                .setCallbackTriggers(TweenCallback.COMPLETE)
                .start(manager);

        Tween.to(currentScreenSprite, TransitionTween.X, 0.5f)
                .target(-Assets.WIDTH)
                .setCallback(transitionTweenComplete)
                .setCallbackTriggers(TweenCallback.COMPLETE)
                .start(manager);
    }

    @Override
    public void render(float delta) {
        batch.begin();
        nextScreenSprite.draw(batch);
        currentScreenSprite.draw(batch);
        batch.end();

        if(tweenType == FADE_OUT_IN)
            renderFadeOutIn();

        if(delta <= 0.022f)     //this keeps tweens from looking too choppy during cpu interrupts
            manager.update(Gdx.graphics.getDeltaTime());
    }

    public void renderFadeOutIn() {
        if(tweenType ==  FADE_OUT_IN) {
            Gdx.graphics.getGL20().glEnable(GL20.GL_BLEND);
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
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
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