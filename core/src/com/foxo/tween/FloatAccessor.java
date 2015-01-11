package com.foxo.tween;

import aurelienribon.tweenengine.TweenAccessor;

public class FloatAccessor implements TweenAccessor<TweenableFloat> {

    public static final int FLOAT = 1;

    @Override
    public int getValues(TweenableFloat target, int tweenType, float[] returnValues) {
        returnValues[0] = target.getFloat();
        return 1;
    }

    @Override
    public void setValues(TweenableFloat target, int tweenType, float[] newValues) {
        target.setFloat(newValues[0]);
    }
}