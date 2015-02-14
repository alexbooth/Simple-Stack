package com.foxo.background;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.foxo.simplestack.Assets;

import java.util.Random;


public class Tree {

    private  float[] pointyTreeBase;
    private float[] newTree;
    private Mesh mesh;
    private float x;
    private int index;
    private Random r;

    public Tree(float x, int index) {
        this.x = x;
        this.index = index;

        float trunkWidth = 0.03f;

        pointyTreeBase = new float[]{  0.00f,  0.00f,  0, 0, 1f,
                                  trunkWidth,  0.00f,  0, 0, 0f,
                                       0.00f,  0.10f,  0, 0, 1f,
                                  trunkWidth,  0.10f,  0, 0, 0f,
                          trunkWidth + 0.05f,  0.10f,  0, 0, 1f,
                                  trunkWidth,  0.225f, 0, 0, 0f,
                                      -0.05f,  0.10f,  0, 0, 1f,
                                       0.00f,  0.225f, 0, 0, 0f,
                                      -0.05f,  0.225f, 0, 0, 1f,
                          trunkWidth + 0.05f,  0.225f, 0, 0, 0f,
                               trunkWidth/2f, 0.35f,  0, 0, 1f};

        newTree = deform(pointyTreeBase);

        createMesh();
    }

    public float[] deform(float[] mesh) {
        r = new Random();

        float maxOffset = 0.02f;

        for (int i = 0; i < mesh.length; i += 5)
            mesh[i] += ((r.nextInt(500)-1000)/1000f) * maxOffset;

        for (int i = 11; i < mesh.length; i += 5)
            mesh[i] += ((r.nextInt(500)-1000)/1000f) * maxOffset;

        float xScale = r.nextFloat()/2f + 0.5f;
        float yScale = r.nextFloat()/2f + 0.5f;

        if(index == 0){
            xScale *= 0.60f;
            yScale *= 0.60f;
        }
        else if (index == 1) {
            xScale *= 0.80f;
            yScale *= 0.80f;
        }

        for (int i = 0; i < mesh.length; i += 5) {
            mesh[i] *= xScale;
            mesh[i+1] *= yScale;
        }

        return mesh.clone();
    }

    public void createMesh() {
        mesh = new Mesh(true, 11, 27,
                new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
                new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));

        mesh.setVertices(newTree);

        mesh.setIndices(new short[]{ 0, 1, 2,
                                     2, 3, 1,
                                     3, 4, 5,
                                     3, 2, 7,
                                     7, 6, 2,
                                     3, 7, 5,
                                     7, 8, 10,
                                     7, 10, 5,
                                     5, 9, 10 });
    }

    public void update(float amplitude, float phase, float vertShift) {
        Vector2 normal = new Vector2((float) (2.5 * Math.cos(2.5 * x + phase + 7.5f) * amplitude)* Assets.V_WIDTH, -1);
        for(int i = 0; i < newTree.length; i+=5) {
            float rotAngle = (float) (normal.angleRad()+Math.PI/2);
            newTree[i] =   (float) (pointyTreeBase[i] * Math.cos(rotAngle) - pointyTreeBase[i+1] * Math.sin(rotAngle)) + x;
            newTree[i+1] = (float) (pointyTreeBase[i] * Math.sin(rotAngle) + pointyTreeBase[i+1] * Math.cos(rotAngle)) + (float) (Math.sin(2.5 * x + phase + 7.5f) * amplitude + vertShift) * Assets.V_WIDTH;
        }

        mesh.setVertices(newTree);
    }

    public void render(ShaderProgram shader) {
        switch(index) {
            case 0: shader.setUniformf("color", 170 / 256f, 210 / 256f, 205 / 256f); break;
            case 1: shader.setUniformf("color", 150 / 256f, 190 / 256f, 185 / 256f); break;
            case 2: shader.setUniformf("color", 110 / 256f, 142 / 256f, 135 / 256f); break;
            default:shader.setUniformf("color", 1, 0, 0); break;
        }
        mesh.render(shader, GL20.GL_TRIANGLES);
    }

    public float getWidth() {
        float leftMostpoint = 4f;
        float rightMostPoint = -1f;

        for(int i = 0; i < newTree.length; i+=5) {
            if(newTree[i] < leftMostpoint)
                leftMostpoint = newTree[i];
            if(newTree[i] > rightMostPoint)
                rightMostPoint = newTree[i];
        }

        return rightMostPoint-leftMostpoint;
    }

    public float getX() {
        return x;
    }
}
