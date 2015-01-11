package com.foxo.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.foxo.buttons.Button;


public class Block extends Button {

    private int size = 1;
    private int location = 1;

    public Block(float x, float y, float width, float height, TextureRegion buttonUp, TextureRegion buttonDown, int size) {
        super(x, y, width, height, buttonUp, buttonDown);
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int i) {
        location = i;
    }
}