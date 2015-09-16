package com.foxo.background;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.foxo.simplestack.Assets;

import java.util.Random;


public class ShaderBackground {

    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private ShaderProgram shader;
    private Random r;
    private Forest[] forest = new Forest[3];
    private Hill[] hill = new Hill[3];
    private int timeOffset;
    private long timeOrigin;
    private float time;


    public ShaderBackground(ShapeRenderer shapeRenderer, OrthographicCamera camera) {
        this.shapeRenderer = shapeRenderer;
        this.camera = camera;
        r = new Random();

        timeOffset = r.nextInt(100000);
        timeOrigin = System.currentTimeMillis();

        for(int i = 0; i < 3; i++) {
            hill[i] = new Hill(i);
            forest[i] = new Forest(i);
        }

        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(Gdx.files.internal("shaders/trees.vert").readString(), Gdx.files.internal("shaders/trees.frag").readString());
        if (shader.getLog().length()!=0)
            System.out.println(shader.getLog());
    }



    public void draw(SpriteBatch batch) {
        time = (float) ((System.currentTimeMillis() - timeOrigin) / 200.0d + timeOffset);

        float hill_3_amplitude = (float) (Math.sin(time * 0.0045f) * 0.025f);
        float hill_3_phase = time * 0.0375f;

        float hill_2_amplitude = (float) (Math.sin(time * 0.004f) * 0.0375f);
        float hill_2_phase = -time * 0.035f;

        float hill_1_amplitude = (float) (Math.sin(time * 0.0035f) * 0.028f);
        float hill_1_phase = time * 0.04f;

        hill[0].update(hill_1_amplitude, hill_1_phase, 0.31f);
        hill[1].update(hill_2_amplitude, hill_2_phase, 0.25f);
        hill[2].update(hill_3_amplitude, hill_3_phase, 0.2f);

        forest[0].update(hill_1_amplitude, hill_1_phase, 0.309f);
        forest[1].update(hill_2_amplitude, hill_2_phase, 0.249f);
        forest[2].update(hill_3_amplitude, hill_3_phase, 0.199f);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(0, 0, Assets.V_WIDTH, Assets.V_HEIGHT/2,
                new Color(Color.argb8888( 90 / 255f, 200 / 255f,190 / 255f, 1)), new Color(Color.argb8888( 90 / 255f, 200 / 255f, 190 / 255f,1)),
                new Color(Color.argb8888(235 / 256f, 235 / 256f,215 / 256f, 1)), new Color(Color.argb8888( 235 / 256f, 235 / 256f,215 / 256f, 1)));
        shapeRenderer.end();

        camera.setToOrtho(false, Assets.V_WIDTH, Assets.V_HEIGHT);
        batch.setProjectionMatrix(camera.combined);
        shader.begin();
        shader.setUniformMatrix("u_projTrans", batch.getProjectionMatrix());

        for(int i = 0; i < 3; i++) {
            hill[i].render(shader);
            forest[i].render(shader);
        }

        shader.end();
        camera.setToOrtho(true, Assets.V_WIDTH, Assets.V_HEIGHT);
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.enableBlending();
        batch.draw(Assets.board, 0, Assets.V_HEIGHT - (Assets.V_HEIGHT) * (Assets.board.getHeight() / 1080f),
                    Assets.V_WIDTH, (Assets.V_HEIGHT) * (Assets.board.getHeight() / 1080f), 0, 0,  Assets.board.getWidth(), Assets.board.getHeight(), false, true);
        batch.end();
    }

    public void dispose() {
        shader.dispose();
    }
}