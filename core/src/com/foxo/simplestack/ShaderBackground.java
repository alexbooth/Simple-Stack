package com.foxo.simplestack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import java.util.Random;


public class ShaderBackground {
    private ShaderProgram shader;
    private SpriteBatch batch;
    private TextureRegion fboRegion;
    private FrameBuffer fbo;
    private Mesh mesh;
    private Random r;

    private int timeOffset;
    private long timeOrigin;
    private float time;

    private float width;
    private float height;

    public ShaderBackground(SpriteBatch batch) {
        this.batch = batch;

        width = Assets.WIDTH;
        height = Assets.HEIGHT;

        r = new Random();
        timeOffset = r.nextInt(100000);
        timeOrigin = System.currentTimeMillis();

        fbo = new FrameBuffer(Pixmap.Format.RGB888, 640, 360, false);
        fboRegion = new TextureRegion(fbo.getColorBufferTexture());
        fboRegion.flip(false, true);

        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(Gdx.files.internal("shaders/background.vert").readString(), Gdx.files.internal("shaders/background.frag").readString());
        mesh = genFullViewRectangle();

        if (shader.getLog().length()!=0)
            System.out.println(shader.getLog());
    }

    public void draw() {
        time = (float) ((System.currentTimeMillis() - timeOrigin) / 1000.0d + timeOffset);
        int a = shader.getUniformLocation("time");

        fbo.begin();
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shader.begin();
        shader.setUniformf(a, time);
        shader.setUniformMatrix("u_projTrans", batch.getProjectionMatrix());
        mesh.render(shader, GL20.GL_TRIANGLES);
        shader.end();
        fbo.end();

        batch.begin();
        batch.disableBlending();
        batch.draw(fboRegion, 0, 0, width, height);
        batch.enableBlending();
        batch.draw(Assets.board, 0, height - height * (Assets.board.getHeight() / 1080f), width, height * (Assets.board.getHeight() / 1080f), 0, 0, 1920, 1080, false, true);
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

    public void dispose() {
        fboRegion = null;
        fbo.dispose();
        shader.dispose();
        mesh.dispose();
    }
}
