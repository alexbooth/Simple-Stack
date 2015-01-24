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
        shader2 = new ShaderProgram(Gdx.files.internal("shaders/default.vert").readString(), Gdx.files.internal("shaders/default.frag").readString());
        mesh = genFullViewRectangle();
        mesh2 = createMesh();

        if (shader.getLog().length()!=0)
            System.out.println(shader.getLog());
    }

    public void draw(SpriteBatch batch) {
        time = (float) ((System.currentTimeMillis() - timeOrigin) / 1000.0d + timeOffset);
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
        mesh2.render(shader2, GL20.GL_TRIANGLES, 0, 3);
        shader2.end();

        fbo.end();

        batch.begin();
        batch.enableBlending();
        batch.draw(fboRegion, 0, 0, Assets.V_WIDTH, Assets.V_HEIGHT);
        batch.draw(Assets.board, 0, Assets.V_HEIGHT - (Assets.V_HEIGHT) * (Assets.board.getHeight() / 1080f),
                Assets.V_WIDTH, (Assets.V_HEIGHT) * (Assets.board.getHeight() / 1080f), 0, 0,  Assets.board.getWidth(), Assets.board.getHeight(), false, true);
        batch.end();
    }

    public static Mesh genFullViewRectangle() {
        Mesh mesh = new Mesh(true, 4, 6, new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));

        mesh.setVertices(new float[] { -Assets.V_WIDTH, -Assets.V_HEIGHT, 0, 0, 0,
                                        Assets.V_WIDTH, -Assets.V_HEIGHT, 0, 1, 0,
                                        Assets.V_WIDTH,  Assets.V_HEIGHT, 0, 1, 1,
                                       -Assets.V_WIDTH,  Assets.V_HEIGHT, 0, 0, 1});

        mesh.setIndices(new short[] { 0, 1, 2, 2, 3, 0 });

        return mesh;
    }

    public Mesh createMesh() {
        if (mesh == null) {
            mesh = new Mesh(true, 3, 3,
                    new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
                    new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, "a_color"),
                    new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));

            mesh.setVertices(new float[] { -Assets.V_WIDTH, -0.5f, 0, Color.toFloatBits(255, 0, 0, 255), 0,    0.5f,
                    Assets.V_WIDTH, -0.5f, 0, Color.toFloatBits(0, 255, 0, 255), 0.5f, 0.5f,
                                            0,     0.5f, 0, Color.toFloatBits(0, 0, 255, 255), 0.5f, 0 });

            mesh.setIndices(new short[] { 0, 1, 2 });
        }
        return mesh;
    }


    public void dispose() {
        fboRegion = null;
        fbo.dispose();
        shader.dispose();
        mesh.dispose();
    }
}
