package com.foxo.simplestack;

import com.badlogic.gdx.Game;
import com.foxo.screens.SplashScreen;


public class SSGame extends Game {

    @Override
    public void create () {
        Assets.loadSplash();
        setScreen(new SplashScreen(this));
    }

    @Override
    public void dispose () {
        super.dispose();
        getScreen().dispose();
    }
}