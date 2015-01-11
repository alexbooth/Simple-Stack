package com.foxo.simplestack.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.foxo.simplestack.SSGame;


public class DesktopLauncher16x9 {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1280;
        config.height = 720;
		new LwjglApplication(new SSGame(), config);
	}
}
