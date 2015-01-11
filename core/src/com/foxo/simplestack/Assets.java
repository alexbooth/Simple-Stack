package com.foxo.simplestack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.foxo.objects.Block;
import com.foxo.saving.SaveManager;


public class Assets {

    public static final Matrix4 NORMAL_PROJECTION = new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    public static final int HEIGHT = Gdx.graphics.getHeight();
    public static final int WIDTH = Gdx.graphics.getWidth();
    public static final float V_HEIGHT = 3 * (Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth());
    public static final float V_WIDTH = 3;
    public static final int MAX_LEVEL = 12;

    public static TextureRegion pause, resumeUp, resumeDown, mainMenuUp, mainMenuDown, nextLevelUp, nextLevelDown, restartUp, restartDown, replayUp;
    public static TextureRegion replayDown, cancelUp, cancelDown, okUp, okDown, backUp, backDown, playUp, playDown, rulesUp, rulesDown;
    public static TextureRegion blockTexture[], levelDown[], levelUp[], levelLock[];
    public static Texture splash, menuBackground, gameBackground, htpBackground, title;

    public static BitmapFont AlegreyaSans;
    public static Block blocks[];

    public static boolean sixteenByNine, loadComplete = false;
    public static SaveManager save;
    public static final boolean debug = true;

    public static boolean load() {
        if(Assets.debug)
            System.out.println("Loading assets");

        double time = System.currentTimeMillis();
        save = new SaveManager(false);

        loadGameButtons();
        loadBlocks();
        loadBackgrounds();
        loadFont();

        loadComplete = true;
        if(Assets.debug)
            System.out.println("Assets loaded after " + (System.currentTimeMillis() - time) / 1000 + " seconds");

        return true;
    }

    private static void loadBackgrounds() {
        title = new Texture(Gdx.files.internal("images/menu/title.png"), true);
        title.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);

        htpBackground = new Texture(Gdx.files.internal("images/menu/htpbackground.png"));
        htpBackground.setFilter(TextureFilter.Linear, TextureFilter.Linear);

        menuBackground = new Texture(Gdx.files.internal("images/menu/menubackground.png"));
        menuBackground.setFilter(TextureFilter.Linear, TextureFilter.Linear);

        gameBackground = new Texture(Gdx.files.internal("images/menu/gamebackground.png"));
        gameBackground.setFilter(TextureFilter.Linear, TextureFilter.Linear);
    }

    public static void loadFont() {
        Texture fontTexture = new Texture(Gdx.files.internal("fonts/df.png"), true);
        fontTexture.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
        AlegreyaSans = new BitmapFont(Gdx.files.internal("fonts/df.fnt"), new TextureRegion(fontTexture), false);
    }

    private static void loadGameButtons() {
        TextureAtlas theAtlas = new TextureAtlas(Gdx.files.internal("images/gamebuttons/gamebuttons.atlas"), true);

        pause = theAtlas.findRegion("pause");
        restartUp = theAtlas.findRegion("restartUp");
        restartDown = theAtlas.findRegion("restartDown");
        resumeUp = theAtlas.findRegion("resumeUp");
        resumeDown = theAtlas.findRegion("resumeDown");
        mainMenuUp = theAtlas.findRegion("mainMenuUp");
        mainMenuDown = theAtlas.findRegion("mainMenuDown");
        nextLevelUp = theAtlas.findRegion("nextLevelUp");
        nextLevelDown = theAtlas.findRegion("nextLevelDown");
        replayUp = theAtlas.findRegion("replayUp");
        replayDown = theAtlas.findRegion("replayDown");
        cancelUp = theAtlas.findRegion("cancelUp");
        cancelDown = theAtlas.findRegion("cancelDown");
        okUp = theAtlas.findRegion("okUp");
        okDown = theAtlas.findRegion("okDown");
        backUp = theAtlas.findRegion("backUp");
        backDown = theAtlas.findRegion("backDown");
        rulesUp = theAtlas.findRegion("rulesUp");
        rulesDown = theAtlas.findRegion("rulesDown");
        playUp = theAtlas.findRegion("playUp");
        playDown = theAtlas.findRegion("playDown");
    }

    private static void loadBlocks() {
        blockTexture = new TextureRegion[12];
        levelLock = new TextureRegion[12];
        levelDown = new TextureRegion[12];
        levelUp = new TextureRegion[12];
        blocks = new Block[12];

        for(int i = 0; i < 12; i++) {
            Texture texture = new Texture(Gdx.files.internal("images/blocks/block_" + i + ".png"), true);
            texture.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
            blockTexture[i] = new TextureRegion(texture);
            blockTexture[i].flip(false, true);

            texture = new Texture(Gdx.files.internal("images/levelselection/" + i + "up.png"), true);
            texture.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
            levelUp[i] = new TextureRegion(texture);
            levelUp[i].flip(false, true);

            texture = new Texture(Gdx.files.internal("images/levelselection/" + i + "down.png"), true);
            texture.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
            levelDown[i] = new TextureRegion(texture);
            levelDown[i].flip(false, true);

            if(i > 2) {
                texture = new Texture(Gdx.files.internal("images/levelselection/" + i + "lock.png"), true);
                texture.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
                levelLock[i] = new TextureRegion(texture);
                levelLock[i].flip(false, true);
            }

            if(sixteenByNine)
                blocks[i] = new Block(0, V_HEIGHT - (V_HEIGHT / 6.0f) - i * (V_WIDTH / 26.67f), 1, V_WIDTH / 13.33f, blockTexture[i], blockTexture[i], MAX_LEVEL - i);
            else
                blocks[i] = new Block(0, V_HEIGHT - (V_HEIGHT / 4.0f) - i * (V_HEIGHT / 15f), 1, V_WIDTH / 13.34f, blockTexture[i], blockTexture[i], MAX_LEVEL - i);

        }System.out.println((V_HEIGHT / 15f));
    }

    public static void loadSplash() {
        if(Assets.debug)
            System.out.println("Loading splash assets");

        if((float) WIDTH / HEIGHT > 1.555555f)
            sixteenByNine = true;
        else
            sixteenByNine = false;

        System.out.println(sixteenByNine);

        splash = new Texture(Gdx.files.internal("images/splash/splash.png"));
        splash.setFilter(TextureFilter.Linear, TextureFilter.Linear);
    }

    public static void dispose() {
        menuBackground.dispose();
        gameBackground.dispose();
        AlegreyaSans.dispose();
        splash.dispose();
        title.dispose();
    }
}