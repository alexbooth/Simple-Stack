package com.foxo.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class BaseImage {
    private float x, y, width, height;
    private TextureRegion image;
    private Rectangle bounds;

    public BaseImage(float x, float y, float width, float height, TextureRegion image) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;

        bounds = new Rectangle(x, y, width, height);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void setX(float x) {
        this.x = x;
        bounds.setX(x);
    }

    public void setY(float y) {
        this.y = y;
        bounds.setY(y);
    }

    public float getX() {
        return x;
    }

    public float getFloatX() {
        return x;
    }

    public float getFloatY() {
        return y;
    }

    public float getY() {
        return y;
    }

    public TextureRegion getImage() {
        return image;
    }

    public void setImage(TextureRegion image) {
        this.image = image;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setPos(float x, float y) {
        this.x = x;
        this.y = y;
        bounds.setPosition(x, y);
    }
}