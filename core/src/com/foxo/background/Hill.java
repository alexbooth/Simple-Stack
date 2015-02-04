package com.foxo.background;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.foxo.simplestack.Assets;


public class Hill {

    public static final int numSegments = 50;
    private Mesh mesh;
    private int index;
    private float[] verts;


    public Hill (int index) {
        this.index = index;

        createHillArray();
        createMesh();
    }

    public void createHillArray() {
        verts = new float[6 * (numSegments + 1)];

        for(int i = 0; i < verts.length; i+=6) {
            verts[i] = (Assets.V_WIDTH / numSegments) * (i / 6f);
            verts[i + 3] = verts[i];
        }
    }

    public void createMesh() {
       mesh = new Mesh(true, verts.length / 3, verts.length / 3, new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"));

       mesh.setVertices(verts);

       short[] indices = new short[verts.length / 3];
       for(int i = 0; i < indices.length; i++)
            indices[i] = (short) i;

        mesh.setIndices(indices);
    }

    public void update(float amplitude, float phase, float vertShift) {
        for(int i = 1; i < verts.length; i+=6)
            verts[i] = (float) (Math.sin(2.5 * verts[i - 1] + phase + 7.5f) * amplitude + vertShift) * Assets.V_WIDTH;


        mesh.setVertices(verts);
    }

    public void render(ShaderProgram shader) {
        switch(index) {
            case 0: shader.setUniformf("color", 170 / 256f, 210 / 256f, 205 / 256f); break;
            case 1: shader.setUniformf("color", 150 / 256f, 190 / 256f, 185 / 256f); break;
            case 2: shader.setUniformf("color", 110 / 256f, 142 / 256f, 135 / 256f); break;
            default:shader.setUniformf("color", 1, 0, 0); break;
        }
        mesh.render(shader, GL20.GL_TRIANGLE_STRIP);
    }
}