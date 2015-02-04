package com.foxo.simplestack.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.foxo.simplestack.SSGame;


public class DesktopLauncher16x9 {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1920;
        config.height = 1080;
        config.fullscreen = true;
        config.vSyncEnabled = true;
		new LwjglApplication(new SSGame(), config);
	}
}
