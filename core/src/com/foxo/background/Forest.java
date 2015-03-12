package com.foxo.background;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.foxo.simplestack.Assets;

import java.util.Random;


public class Forest {

    private Tree[][] tree;
    private Random r;
    private int minimumTrees = 7;
    private int maximumTrees = 13;
    private int numTrees;

    private int numGroups;


    public Forest(int index) {
        r = new Random();
        numTrees = r.nextInt(maximumTrees-minimumTrees) + minimumTrees;
        numGroups = r.nextInt(3)+3;
        tree = new Tree[numGroups][4];

        for(int j = 0; j < numGroups; j++) {
            for (int i = 0; i < r.nextInt(4) + 1; i++) {
                float off = r.nextFloat();

                if(i > 0){System.out.println(tree[j][i-1].getWidth());
                    tree[j][i] = new Tree(tree[j][i-1].getX()+tree[j][i-1].getWidth(), index);}
                else
                    tree[j][i] = new Tree(off * Assets.V_WIDTH, index);
            }
        }
    }

    public void update(float amplitude, float phase, float vertShift) {
        for(int j = 0; j < numGroups; j++) {
            for (int i = 0; i < tree[j].length; i++) {
                if (tree[j][i] != null)
                    tree[j][i].update(amplitude, phase, vertShift);
            }
        }
    }

    public void render(ShaderProgram shaderProgram) {
        for(int j = 0; j < numGroups; j++) {
            for (int i = 0; i < tree[j].length; i++) {
                if (tree[j][i] != null)
                    tree[j][i].render(shaderProgram);
            }
        }
    }
}