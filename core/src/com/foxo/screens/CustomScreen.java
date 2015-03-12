package com.foxo.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.foxo.simplestack.Assets;

public class CustomScreen implements Screen {
    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    public Texture getBuffer() {
        FrameBuffer buffer = new FrameBuffer(Pixmap.Format.RGBA8888, Assets.WIDTH, Assets.HEIGHT, false);
        buffer.begin();
        render(0);
        buffer.end();
        return buffer.getColorBufferTexture();
    }
}
