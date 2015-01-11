package com.foxo.buttons;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class OrderedButton extends Button {
	
	private int index;

	public OrderedButton(float x, float y, float width, float height, TextureRegion buttonUp, TextureRegion buttonDown, int index) {
		super(x, y, width, height, buttonUp, buttonDown);
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
}