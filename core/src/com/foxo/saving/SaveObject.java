package com.foxo.saving;

import com.badlogic.gdx.utils.IntArray;


public class SaveObject {

    private int moves;
    private int level;
    private float time;
    private boolean inProgress;
    private float bestTime;
    private int bestMoves;
    private IntArray[] array;

    public SaveObject() {}

    public SaveObject(int level) {
        this.level = level;
        bestTime = Float.MAX_VALUE;
        bestMoves = Integer.MAX_VALUE;
        inProgress = true;
        array = new IntArray[3];
        prepare();
    }

    public void saveScore(int moves, float time) {
        this.moves = moves;
        this.time = time;
    }

    public void savePositions(int stack, int size) {
        array[stack].add(size);
    }

    public IntArray[] getArray() {
        return array;
    }

    public boolean getInProgress() {
        return inProgress;
    }

    public void setBestTime(float bestTime) {
        this.bestTime = bestTime;
    }

    public void setBestMoves(int bestMoves) {
        this.bestMoves = bestMoves;
    }

    public int getBestMoves() {
        return bestMoves;
    }

    public float getBestTime() {
        return bestTime;
    }

    public void endGame() {
        inProgress = false;
    }

    public void prepare() {
        inProgress = true;

        for(int i = 0; i < 3; i++)
            array[i] = new IntArray();
    }

    public float getTime() {
        return time;
    }

    public int getMoves() {
        return moves;
    }
}