package com.foxo.tween;

import com.foxo.objects.BaseImage;

import aurelienribon.tweenengine.TweenAccessor;


public class BaseImageAccessor implements TweenAccessor<BaseImage> {

    public static final int POSITION_X = 1;
    public static final int POSITION_Y = 2;
    public static final int POSITION_XY = 3;
    public static final int BOUNDS_WH = 4;

    @Override
    public int getValues(BaseImage target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case POSITION_X:
                returnValues[0] = target.getX();
                return 1;
            case POSITION_Y:
                returnValues[0] = target.getY();
                return 1;
            case POSITION_XY:
                returnValues[0] = target.getX();
                returnValues[1] = target.getY();
                return 2;
            case BOUNDS_WH:
                returnValues[0] = target.getWidth();
                returnValues[1] = target.getHeight();
                return 3;
            default: assert false; return -1;
        }
    }

    @Override
    public void setValues(BaseImage target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case POSITION_X:
                target.setX(newValues[0]);
                break;
            case POSITION_Y:
                target.setY(newValues[0]);
                break;
            case POSITION_XY:
                target.setPos(newValues[0], newValues[1]);
                break;
            case BOUNDS_WH:
                target.setWidth(newValues[0]);
                target.setHeight(newValues[1]);
                break;
            default: assert false; break;
        }
    }
}