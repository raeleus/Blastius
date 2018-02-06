/*
 * The MIT License
 *
 * Copyright 2017 Raymond Buckley.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ray3k.blastius.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.spine.utils.TwoColorPolygonBatch;
import com.ray3k.blastius.Core;
import com.ray3k.blastius.EntityManager;
import com.ray3k.blastius.InputManager;
import com.ray3k.blastius.State;
import com.ray3k.blastius.entities.EnemyEntity;
import com.ray3k.blastius.entities.PlayerEntity;
import com.ray3k.blastius.entities.PowerUpEntity;
import com.ray3k.blastius.states.EditorState.EditorEntity;

public class GameState extends State {
    private static GameState instance;
    private int score;
    private static int highscore = 0;
    private OrthographicCamera gameCamera;
    private Viewport gameViewport;
    private InputManager inputManager;
    private Skin skin;
    private Stage stage;
    private Table table;
    private Label scoreLabel;
    public static EntityManager entityManager;
    public static TextureAtlas spineAtlas;
    public static final float GAME_WIDTH = 800.0f;
    public static final float GAME_HEIGHT = 600.0f;
    public static TwoColorPolygonBatch twoColorPolygonBatch;
    private float formationTimer;
    private float formationTime;
    private static final float FORMATION_TIME_DECAY = .009f;
    public static PlayerEntity player;
    public static float enemyHealthMultiplier;
    public static final float ENEMY_HEALTH_MULTIPLIER_INCREASE = .01f;
    public static final int MIN_SCORE_INCREASE_HEALTH = 100;
    
    public static GameState inst() {
        return instance;
    }
    
    public GameState(Core core) {
        super(core);
    }
    
    @Override
    public void start() {
        instance = this;
        
        spineAtlas = Core.assetManager.get(Core.DATA_PATH + "/spine/blastius.atlas", TextureAtlas.class);
        
        score = 0;
        
        inputManager = new InputManager();
        
        gameCamera = new OrthographicCamera();
        gameViewport = new StretchViewport(GameState.GAME_WIDTH, GameState.GAME_HEIGHT, gameCamera);
        gameViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getWidth(), true);
        gameViewport.apply();
        
        gameCamera.position.set(gameCamera.viewportWidth / 2, gameCamera.viewportHeight / 2, 0);
        
        skin = Core.assetManager.get(Core.DATA_PATH + "/ui/blastius.json", Skin.class);
        stage = new Stage(new StretchViewport(GameState.GAME_WIDTH, GameState.GAME_HEIGHT));
        
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(inputManager);
        inputMultiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(inputMultiplexer);
        
        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        
        entityManager = new EntityManager();
        
        createStageElements();
        
        twoColorPolygonBatch = new TwoColorPolygonBatch(3100);
        
        player = new PlayerEntity();
        player.setPosition(150.0f, 150.0f);
        entityManager.addEntity(player);
        
        PowerUpEntity powerup = new PowerUpEntity();
        powerup.setPosition(MathUtils.random(GAME_WIDTH), MathUtils.random(GAME_HEIGHT));
        entityManager.addEntity(powerup);
        
        formationTime = 5.0f;
        formationTimer = formationTime;
        
        enemyHealthMultiplier = 1.0f;
    }
    
    private void createStageElements() {
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
        
        scoreLabel = new Label("0", skin);
        root.add(scoreLabel).expandY().padTop(25.0f).top().expandX();
    }
    
    @Override
    public void draw(SpriteBatch spriteBatch, float delta) {
        Gdx.gl.glClearColor(0.0f / 255.0f, 0.0f / 255.0f, 0.0f / 255.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        gameCamera.update();
//        spriteBatch.setProjectionMatrix(gameCamera.combined);
//        spriteBatch.begin();
//        spriteBatch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        twoColorPolygonBatch.setProjectionMatrix(gameCamera.combined);
        twoColorPolygonBatch.begin();
        twoColorPolygonBatch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        entityManager.draw(spriteBatch, delta);
        twoColorPolygonBatch.end();
//        spriteBatch.end();
//        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        stage.draw();
    }
    
    private static Json json = new Json(JsonWriter.OutputType.json);
    
    public void spawnFormation() {
        FileHandle directory = Gdx.files.local(Core.DATA_PATH + "/formations/");
        Array<FileHandle> files = new Array<FileHandle>(directory.list());
        FileHandle file = files.random();
        
        Array<EditorEntity> entities = json.fromJson(Array.class, EditorState.EditorEntity.class, file);

        for (EditorState.EditorEntity entity : entities) {
            EnemyEntity enemy = new EnemyEntity(entity.entityType, entity.patternType);
            enemy.setPosition(entity.getX(), entity.getY() + GAME_HEIGHT);
            entityManager.addEntity(enemy);
        }
    }

    @Override
    public void act(float delta) {
        entityManager.act(delta);
        
        stage.act(delta);
        
        formationTime -= FORMATION_TIME_DECAY * delta;
        
        formationTimer -= delta;
        if (formationTimer < 0) {
            spawnFormation();
            formationTimer = formationTime;
        }
        
        if (getScore() > MIN_SCORE_INCREASE_HEALTH) {
            enemyHealthMultiplier += ENEMY_HEALTH_MULTIPLIER_INCREASE * delta;
        }
        
        if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
            Core.stateManager.loadState("menu");
        }
    }

    @Override
    public void dispose() {
        if (twoColorPolygonBatch != null) {
            twoColorPolygonBatch.dispose();
        }
    }

    @Override
    public void stop() {
        stage.dispose();
    }
    
    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height, true);
        stage.getViewport().update(width, height, true);
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
        scoreLabel.setText(Integer.toString(score));
        if (score > highscore) {
            highscore = score;
        }
    }
    
    public void addScore(int score) {
        this.score += score;
        scoreLabel.setText(Integer.toString(this.score));
        if (this.score > highscore) {
            highscore = this.score;
        }
    }

    public OrthographicCamera getGameCamera() {
        return gameCamera;
    }

    public void setGameCamera(OrthographicCamera gameCamera) {
        this.gameCamera = gameCamera;
    }

    public Skin getSkin() {
        return skin;
    }

    public Stage getStage() {
        return stage;
    }
    
    public void playSound(String name) {
        playSound(name, 1.0f, 1.0f);
    }
    
    public void playSound (String name, float volume) {
        playSound(name, volume, 1.0f);
    }
    
    /**
     * 
     * @param name
     * @param volume
     * @param pitch .5 to 2. 1 is default
     */
    public void playSound(String name, float volume, float pitch) {
        Core.assetManager.get(Core.DATA_PATH + "/sfx/" + name + ".wav", Sound.class).play(volume, pitch, 0.0f);
    }
}