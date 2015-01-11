package com.foxo.tween;

public class TweenableFloat {
    private float x;

    public TweenableFloat() {
        x = 0;
    }

    public TweenableFloat(float x) {
        this.x = x;
    }

    public float getFloat() {
        return x;
    }

    public void setFloat(float x) {
        this.x = x;
    }
}