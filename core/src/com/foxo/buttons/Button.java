package com.foxo.buttons;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public class Button extends PressableImage
{
	private boolean isPressed = false;

	public Button(float x, float y, float width, float height, TextureRegion buttonUp, TextureRegion buttonDown) {
		super(x, y, width, height, buttonUp, buttonDown);
	}

	public void draw(SpriteBatch batcher) {
		if (isPressed && getImagePressed() != null) 
			batcher.draw(getImagePressed(), getX(), getY(), getWidth(), getHeight());
		else 
			batcher.draw(getImage(), getX(), getY(), getWidth(), getHeight());
	}

	public boolean isTouchDown(float screenX, float screenY) {
		if (getBounds().contains(screenX, screenY)) {
			isPressed = true;
			return true;
		}

		return false;
	}

	public boolean isTouchUp(float screenX, float screenY) {
		if (getBounds().contains(screenX, screenY) && isPressed) {
			isPressed = false;
			return true;
		}

		isPressed = false;
		return false;
	}
}