package com.foxo.buttons;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.foxo.objects.BaseImage;

public class PressableImage extends BaseImage {
	
	private TextureRegion imagePressed;

	public PressableImage(float x, float y, float width, float height, TextureRegion image, TextureRegion imagePressed) {
		super(x, y, width, height, image);
		this.imagePressed = imagePressed;
	}

	public TextureRegion getImagePressed() {
		return imagePressed;
	}

	public void setImagePressed(TextureRegion imagePressed) {
		this.imagePressed = imagePressed;
	}
	
	public void setImage(TextureRegion image, TextureRegion imagePressed) {
		this.imagePressed = imagePressed;
		setImage(image);
	}
}