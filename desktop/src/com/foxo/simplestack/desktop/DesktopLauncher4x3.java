package com.foxo.simplestack.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.foxo.simplestack.SSGame;


public class DesktopLauncher4x3 {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1024;
        config.height = 768;
        config.vSyncEnabled = true;
		new LwjglApplication(new SSGame(), config);
	}
}
