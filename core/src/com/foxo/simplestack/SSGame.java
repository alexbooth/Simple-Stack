package com.foxo.simplestack;

import com.badlogic.gdx.Game;
import com.foxo.objects.Board;
import com.foxo.screens.GameScreen;
import com.foxo.screens.SplashScreen;


public class SSGame extends Game {

    @Override
    public void create () {
        Assets.loadSplash();
        setScreen(new SplashScreen(this));
        //setScreen(new GameScreen(this, 3, Board.NEW_GAME));
    }

    @Override
    public void dispose () {
        super.dispose();
        getScreen().dispose();
    }
}