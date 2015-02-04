package com.foxo.background;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.foxo.simplestack.Assets;

import java.util.Random;


public class Forest {
    private Tree[] tree;
    private Random r;
    private int minimumTrees = 7;
    private int maximumTrees = 15;
    private int numTrees;

    public Forest(int index) {
        r = new Random();
        numTrees = r.nextInt(maximumTrees-minimumTrees) + minimumTrees;
        tree = new Tree[numTrees];

        for(int i = 0; i < tree.length; i++)
            tree[i] = new Tree(r.nextFloat()* Assets.V_WIDTH, index);
    }

    public void update(float amplitude, float phase, float vertShift) {
        for(int i = 0; i < tree.length; i++)
            tree[i].update(amplitude, phase, vertShift);
    }

    public void render(ShaderProgram shaderProgram) {
        for(int i = 0; i < tree.length; i++)
            tree[i].render(shaderProgram);
    }
}