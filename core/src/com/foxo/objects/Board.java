package com.foxo.objects;

import java.util.Iterator;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Expo;
import aurelienribon.tweenengine.equations.Quint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.foxo.saving.SaveObject;
import com.foxo.screens.GameScreen;
import com.foxo.simplestack.Assets;
import com.foxo.tween.BaseImageAccessor;


public class Board implements  Iterable<Array<Block>>{

    private GameScreen gameScreen;

    public static final int NEW_GAME = 0;
    public static final int REPLAY = 1;
    public static final int NEXT_LEVEL = 2;

    private Array<Array<Block>> blocks;
    private Block held;
    private int level;
    private SaveObject save;
    private int moves = 0;
    private float vWidth = 3;
    private float vHeight = 3 * (Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth());
    private float base;

    public Board(GameScreen gameScreen, int type) {
        blocks = new Array<>();
        blocks.add(new Array<Block>());
        blocks.add(new Array<Block>());
        blocks.add(new Array<Block>());
        level = gameScreen.getLevel();

        save = Assets.save.loadDataValue("level:" + level, SaveObject.class);

        this.gameScreen = gameScreen;
        
        if(Assets.sixteenByNine)
            base = vHeight / 6.0f;
        else
            base = vHeight / 4.0f;

        Tween.setWaypointsLimit(3);
        Tween.setCombinedAttributesLimit(10);

        switch(type) {
            case NEW_GAME:
                if(save != null && save.getInProgress()) {
                    moves = save.getMoves();
                    for(int i = 0; i < 3; i++) {
                        for(int j = 0; j < save.getArray()[i].size; j++) {
                            int n = save.getArray()[i].get(j);
                            if(n > 0) {
                                Assets.blocks[Assets.MAX_LEVEL - n].setLocation(i + 1);
                                Assets.blocks[Assets.MAX_LEVEL - n].setPos(i, vHeight - base - (j) * (vWidth / 26.67f));
                                blocks.get(i).add(Assets.blocks[Assets.MAX_LEVEL - n]);
                            }
                        }
                    }
                }
                else {
                    for(int i = 0; i < level; i++) {
                        Assets.blocks[i].setLocation(1);
                        Assets.blocks[i].setPos(0, vHeight - base - (i) * (vWidth / 26.67f));
                        blocks.get(0).add(Assets.blocks[i]);
                    }
                }
                break;
            case REPLAY:
                for(int i = 0; i < level; i++) {
                    Assets.blocks[i].setLocation(1);
                    blocks.get(0).add(Assets.blocks[i]);

                    Tween.to(blocks.get(0).get(i), BaseImageAccessor.POSITION_XY, 2f)
                            .target(0, vHeight - base - i * (vWidth / 26.67f))
                            .ease(Expo.INOUT)
                            .delay(i * 0.01f)
                            .start(gameScreen.getTweenManager());
                }
                break;
            case NEXT_LEVEL:
                if(save != null && save.getInProgress()) {
                    moves = save.getMoves();
                    for(int i = 0; i < 3; i++) {
                        for(int j = 0; j < save.getArray()[i].size; j++) {
                            int n = save.getArray()[i].get(j);
                            if(n > 0) {
                                Assets.blocks[Assets.MAX_LEVEL - n].setLocation(i + 1);
                                blocks.get(i).add(Assets.blocks[Assets.MAX_LEVEL - n]);
                                Tween.to(Assets.blocks[Assets.MAX_LEVEL - n], BaseImageAccessor.POSITION_XY, 0.75f)
                                        .target(i, vHeight - base - j * (vWidth / 26.67f))
                                        .ease(Expo.OUT)
                                        .start(gameScreen.getTweenManager());
                            }
                        }
                    }
                }
                else {
                    for(int i = 0; i < level - 1; i++) {
                        Assets.blocks[i].setLocation(1);
                        blocks.get(0).add(Assets.blocks[i]);

                        Tween.to(blocks.get(0).get(i), BaseImageAccessor.POSITION_X, 1.5f - i * 0.1f)
                                .target(0)
                                .ease(Expo.OUT)
                                .delay(i * 0.1f)
                                .start(gameScreen.getTweenManager());
                    }

                    Assets.blocks[level - 1].setLocation(1);
                    blocks.get(0).add(Assets.blocks[level - 1]);
                    Assets.blocks[level - 1].setPos(-1.5f, vHeight - base - (level - 1) * (vWidth / 26.67f));

                    Tween.to(blocks.get(0).get(level - 1), BaseImageAccessor.POSITION_XY, 1.25f) // TODO: crashed here a few times randomly w/ java.lang.ArrayIndexOutOfBoundsException: 0 // length=0 index=0
                            .waypoint(-0.5f, vHeight - base - (level+2) * (vWidth / 26.67f))
                            .target(0, vHeight - base - (level - 1) * (vWidth / 26.67f))
                            .ease(Back.INOUT)
                            .delay(level * 0.1f)
                            .start(gameScreen.getTweenManager());
                }
                break;
            default: break;
        }

        if(save == null)
            save = new SaveObject(level);

        held = null;
        moves = 0;
    }

    public void saveToJSON() {
        double time = System.nanoTime();

        save.saveScore(moves, gameScreen.getTimer().getTimeFloat());
        save.prepare();

        for(int i = 0; i < 3; i++)
            for(Block b: blocks.get(i))
                save.savePositions(i, b.getSize());

        Assets.save.saveDataValue("level:" + level, save);

        if(Assets.debug)  System.out.println("Save took " + (System.nanoTime() - time)/1000000000L + " seconds");
    }

    public void touchDown(float x, float y) {
        if(held == null){
            if (x < 1 && blocks.get(0).size > 0)
                held = blocks.get(0).pop();
            else if (x > 2 && blocks.get(2).size > 0)
                held = blocks.get(2).pop();
            else if (x >= 1 && x <= 2 && blocks.get(1).size > 0)
                held = blocks.get(1).pop();

            Tween.to(held, BaseImageAccessor.POSITION_XY, 0.2f)
                    .target(x - 0.5f, y - (vWidth / 26.67f))
                    .ease(Quint.OUT)
                    .start(gameScreen.getTweenManager());
        }
        for(int j = 0; j < 3; j++)
            for(int i = 0; i < blocks.get(j).size; i++)
                System.out.println((j+1) + " " + blocks.get(j).get(i).getSize());
    }

    public boolean touchUp(float x) {
        if(held != null) {
            if(x < 1 && (blocks.get(0).size == 0 || held.getSize() < blocks.get(0).peek().getSize())) {
                if(held.getLocation() != 1)
                    moves++;

                held.setLocation(1);
            }
            else if(x > 2 && (blocks.get(2).size == 0 || held.getSize() < blocks.get(2).peek().getSize())) {
                if(held.getLocation() != 3)
                    moves++;

                held.setLocation(3);
            }
            else if(x >= 1 && x <= 2 && (blocks.get(1).size == 0 || held.getSize() < blocks.get(1).peek().getSize())) {
                if(held.getLocation() != 2)
                    moves++;

                held.setLocation(2);
            }
            if(blocks.get(1).size != 0)
                System.out.println(held.getSize() + " " + held.getLocation() + " " + (held.getSize()));
            if(held.getLocation() == 1)
                blocks.get(0).add(held);
            else if(held.getLocation() == 2)
                blocks.get(1).add(held);
            else if(held.getLocation() == 3)
                blocks.get(2).add(held);

            for(int j = 0; j < 3; j++)
                for(int i = 0; i < blocks.get(j).size; i++)
                    Tween.to(blocks.get(j).get(i), BaseImageAccessor.POSITION_XY, 0.3f)
                            .target(j, vHeight - base - i * (vWidth / 26.67f))
                            .ease(Quint.OUT)
                            .start(gameScreen.getTweenManager());

            held = null;

            saveToJSON();

            if(blocks.get(2).size == level)
                return true;
        }

        return false;
    }

    public void handleHighScores() {
        if(gameScreen.getTimer().getTimeFloat() < save.getBestTime())
            save.setBestTime(gameScreen.getTimer().getTimeFloat());

        if(moves < save.getBestMoves())
            save.setBestMoves(moves);

        save.endGame();

        Assets.save.saveDataValue("level:" + level, save);
    }

    public void touchDragged(float x, float y) {
        if(held != null) {
            gameScreen.getTweenManager().killTarget(held);
            held.setPos(x - 0.5f, y - (vWidth / 26.67f));
        }
    }

    public void setMoves(int moves) {
        this.moves = moves;
    }

    public int getMoves() {
        return moves;
    }

    public Block getHeld() {
        return held;
    }

    public void endGame() {
        save.endGame();
    }

    @Override
    public Iterator<Array<Block>> iterator() {
        return blocks.iterator();
    }
}