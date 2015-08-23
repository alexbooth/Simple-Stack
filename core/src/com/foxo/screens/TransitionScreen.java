package com.foxo.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Pixmap;
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


public class TransitionScreen implements TweenCallback, Screen {

    public static final int SLIDE_BOTH_LEFT = 0x0;
    public static final int SLIDE_BOTH_RIGHT = 0x1;
    public static final int FADE_OUT_IN = 0x2;

    private SpriteBatch batch = new SpriteBatch(); // transition screen will need its own spritebatch
    private ShapeRenderer sr = Assets.sr;

    private FrameBuffer currentBuffer;
    private FrameBuffer nextBuffer;

    private Sprite currentScreenSprite;
    private Sprite nextScreenSprite;

    private TweenManager manager;
    private TweenableFloat alpha;
    private int tweenType;
    private Game game;
    private Screen current, next;


    public TransitionScreen(Screen current, Screen next, final Game game, int tweenType) {
        this.tweenType = tweenType;
        this.game = game;
        this.next = next;
        this.current = current;

        alpha = new TweenableFloat();
        manager = new TweenManager();

        nextBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Assets.WIDTH, Assets.HEIGHT, false);
        nextBuffer.begin();
        next.render(0);
        nextBuffer.end();
        nextScreenSprite = new Sprite(nextBuffer .getColorBufferTexture());
        nextScreenSprite.flip(false, true);

        currentBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Assets.WIDTH, Assets.HEIGHT, false);
        currentBuffer.begin();
        current.render(0);
        currentBuffer.end();
        currentScreenSprite = new Sprite(currentBuffer.getColorBufferTexture());
        currentScreenSprite.flip(false, true);

        switch(tweenType) {
            case SLIDE_BOTH_LEFT:   slideBothLeft();    break;
            case SLIDE_BOTH_RIGHT:  slideBothRight();   break;
            case FADE_OUT_IN:       fadeOutIn();        break;
        }
    }

    FrameBuffer f;
    public TransitionScreen(FrameBuffer currentScreenBuffer, Screen next, final Game game, int tweenType) {
        this.tweenType = tweenType;
        this.game = game;
        this.next = next;

        alpha = new TweenableFloat();
        manager = new TweenManager();

        nextBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Assets.WIDTH, Assets.HEIGHT, false);
        nextBuffer.begin();
        next.render(0);
        nextBuffer.end();
        nextScreenSprite = new Sprite(nextBuffer .getColorBufferTexture());
        nextScreenSprite.flip(false, true);

        f = currentScreenBuffer;
        this.currentScreenSprite = new Sprite(f.getColorBufferTexture());
        currentScreenSprite.flip(false, true);

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

        Tween.to(alpha, FloatAccessor.FLOAT, 0.3f)
                .repeatYoyo(1, 0)
                .target(1)
                .ease(Linear.INOUT)
                .setCallback(this)
                .setCallbackTriggers(TweenCallback.COMPLETE)
                .start(manager);
    }

    private void slideBothRight() {
        Tween.registerAccessor(Sprite.class, new TransitionTween());

        currentScreenSprite.setPosition(0, 0);
        nextScreenSprite.setPosition(-Assets.WIDTH, 0);

        Tween.to(nextScreenSprite, TransitionTween.X,  0.5f)
                .target(0)
                .setCallback(this)
                .setCallbackTriggers(TweenCallback.COMPLETE)
                .start(manager);

        Tween.to(currentScreenSprite, TransitionTween.X,  0.5f)
                .target(Assets.WIDTH)
                .setCallback(this)
                .setCallbackTriggers(TweenCallback.COMPLETE)
                .start(manager);
    }

    private void slideBothLeft() {
        Tween.registerAccessor(Sprite.class, new TransitionTween());

        currentScreenSprite.setPosition(0, 0);
        nextScreenSprite.setPosition(Assets.WIDTH, 0);

        Tween.to(nextScreenSprite, TransitionTween.X, 0.5f)
                .target(0)
                .setCallback(this)
                .setCallbackTriggers(TweenCallback.COMPLETE)
                .start(manager);

        Tween.to(currentScreenSprite, TransitionTween.X, 0.5f)
                .target(-Assets.WIDTH)
                .setCallback(this)
                .setCallbackTriggers(TweenCallback.COMPLETE)
                .start(manager);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        nextScreenSprite.draw(batch);
        currentScreenSprite.draw(batch);
        batch.end();

        if(tweenType == FADE_OUT_IN)
            renderFadeOutIn();

        tick(delta);
    }

    public void tick(float delta) {
        if(delta <= 0.022f)     //this help to keep tweens from looking too choppy when the cpu is busy
            manager.update(Gdx.graphics.getDeltaTime());
    }

    float lastAlpha = 0;                        // ...
    boolean alphaHitOne = false;                // Hacky fix to fade issue...
    public void renderFadeOutIn() {
        float diff =  alpha.getFloat() - lastAlpha; // ...
        lastAlpha = alpha.getFloat();          // ...
        System.out.println(alpha.getFloat());

        if(!alphaHitOne)                        // ...
            if (diff < 0)                       // ...
                alphaHitOne = true;             // ...

        Gdx.graphics.getGL20().glEnable(GL20.GL_BLEND);
        sr.begin(ShapeType.Filled);
        if(alphaHitOne) {                       // TODO fix the hacky fix?
            currentScreenSprite.setAlpha(0);    // ...
            nextScreenSprite.setAlpha(1);       // ...
        }                                       // ...

        sr.setColor(0, 0, 0, alpha.getFloat());
        sr.rect(0, 0, Assets.WIDTH, Assets.HEIGHT);
        sr.end();
        Gdx.graphics.getGL20().glDisable(GL20.GL_BLEND);
    }

    @Override
    public void dispose() {
        if(currentBuffer != null)
            currentBuffer.dispose();
        if(current != null)
            current.dispose();
        if(f != null)
            f.dispose();
        nextBuffer.dispose();
        currentScreenSprite.getTexture().dispose();
        nextScreenSprite.getTexture().dispose();
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

    @Override
    public void onEvent(int type, BaseTween<?> source) {
        game.setScreen(next);
    }
}