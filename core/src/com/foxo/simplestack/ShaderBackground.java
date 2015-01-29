package com.foxo.simplestack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.Random;


public class ShaderBackground {
    private ShaderProgram shader, shader2;
    private TextureRegion fboRegion;
    private FrameBuffer fbo;
    private Mesh mesh, mesh2;
    private Random r;
    private ShapeRenderer sr;
    private int timeOffset;
    private long timeOrigin;
    private float time;

    private float width;
    private float height;

    public ShaderBackground() {
        width = Assets.WIDTH;
        height = Assets.HEIGHT;

        r = new Random();
        timeOffset = r.nextInt(100000);
        timeOrigin = System.currentTimeMillis();
        sr = new ShapeRenderer();
        fbo = new FrameBuffer(Pixmap.Format.RGB888,(int) (width/2), (int) (height/2), false);
        fboRegion = new TextureRegion(fbo.getColorBufferTexture());
        fboRegion.flip(false, true);

        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(Gdx.files.internal("shaders/background.vert").readString(), Gdx.files.internal("shaders/background.frag").readString());
        shader2 = new ShaderProgram(Gdx.files.internal("shaders/trees.vert").readString(), Gdx.files.internal("shaders/trees.frag").readString());
        mesh = genFullViewRectangle();
        newTree = tree.clone();
        makeMesh();

        if (shader2.getLog().length()!=0)
            System.out.println(shader2.getLog());
    }

    public void draw(SpriteBatch batch) {
        time = (float) ((System.currentTimeMillis() - timeOrigin) / 1000.0d + timeOffset);
       // time =  timeOffset;
        int a = shader.getUniformLocation("time");

        fbo.begin();
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shader.begin();
        shader.setUniformf(a, time);
        shader.setUniformMatrix("u_projTrans", batch.getProjectionMatrix());
        mesh.render(shader, GL20.GL_TRIANGLES);
        shader.end();

        shader2.begin();
        shader2.setUniformMatrix("u_projTrans", batch.getProjectionMatrix());
        mesh2.render(shader2, GL20.GL_TRIANGLES, 0, 27);
        shader2.end();
        fbo.end();

        batch.begin();
        batch.enableBlending();
        batch.draw(fboRegion, 0, 0, Assets.V_WIDTH, Assets.V_HEIGHT);
        batch.draw(Assets.board, 0, Assets.V_HEIGHT - (Assets.V_HEIGHT) * (Assets.board.getHeight() / 1080f),
                Assets.V_WIDTH, (Assets.V_HEIGHT) * (Assets.board.getHeight() / 1080f), 0, 0,  Assets.board.getWidth(), Assets.board.getHeight(), false, true);
        batch.end();

        //(0.0250 * Math.sin(15.0 + time * 0.095) * Math.sin(time * 0.014) + 0.680)
        System.out.println((float) ((0.025f * Math.sin(0.33333f) * Math.sin(time)) + 0.68f) * Assets.V_HEIGHT);
              updateMesh(0,(float) ((0.025f * Math.sin(0.33333f) * Math.sin(time))+ 0.68f) * Assets.V_HEIGHT);
    }

    public static Mesh genFullViewRectangle() {
        Mesh mesh = new Mesh(true, 4, 6,
                new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));

        mesh.setVertices(new float[] { -Assets.V_WIDTH, -Assets.V_HEIGHT, 0, 0, 0,
                                        Assets.V_WIDTH, -Assets.V_HEIGHT, 0, 1, 0,
                                        Assets.V_WIDTH,  Assets.V_HEIGHT, 0, 1, 1,
                                       -Assets.V_WIDTH,  Assets.V_HEIGHT, 0, 0, 1});

        mesh.setIndices(new short[] { 0, 1, 2, 2, 3, 0 });

        return mesh;
    }

    public void makeMesh() {
        mesh2 = new Mesh(true, 11, 27, new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"));

        for(int i = 0; i < tree.length; i+=3) {
            newTree[i] += 1;
            //newTree[i+1] += 0.0250 * Math.sin(1 * 15.0 + time * 0.095) * Math.sin(time * 0.014) + 0.680;
        }

        mesh2.setVertices(newTree);

        mesh2.setIndices(new short[] { 0,  1,  2,
                                      2,  3,  1,
                                      3,  4,  5,
                                      3,  2,  7,
                                      7,  6,  2,
                                      3,  7,  5,
                                      7,  8, 10,
                                      7, 10,  5,
                                      5,  9, 10 });
    }

    float[] tree = new float[]{ 0.00f, 0.00f, 0,
                                0.05f, 0.00f, 0,
                                0.00f, 0.10f, 0,
                                0.05f, 0.10f, 0,
                                0.10f, 0.10f, 0,
                                0.05f, 0.225f,0,
                               -0.05f, 0.10f, 0,
                                0.00f, 0.225f,0,
                               -0.05f, 0.225f,0,
                                0.10f, 0.225f,0,
                                0.025f, 0.35f,0 };

    float[] newTree;

    public void updateMesh(float xOff, float yOff) {
        for(int y = 1; y < newTree.length; y+=3)
          newTree[y] = tree[y] + yOff;

        mesh2.setVertices(newTree);
    }

    public void dispose() {
        fboRegion = null;
        fbo.dispose();
        shader.dispose();
        mesh.dispose();
    }
}
