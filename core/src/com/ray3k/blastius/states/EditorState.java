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
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ray3k.blastius.Core;
import com.ray3k.blastius.MapScrollPane;
import com.ray3k.blastius.State;

public class EditorState extends State {
    private Stage stage;
    private static Skin skin;
    private static Table root;
    private static Group group;
    private Array<EditorEntity> entities;
    boolean dragging = false;
    EditorEntity dragTarget;
    private String fileName;

    public EditorState(Core core) {
        super(core);
    }
    
    @Override
    public void start() {
        fileName = "";
        entities = new Array<EditorEntity>();
        
        skin = Core.assetManager.get(Core.DATA_PATH + "/ui/blastius.json", Skin.class);
        stage = new Stage(new ScreenViewport());
        
        Gdx.input.setInputProcessor(stage);
        
        createMenu();
    }
    
    private void createMenu() {
        final ButtonGroup entityButtonGroup = new ButtonGroup<TextButton>();
        final ButtonGroup patternButtonGroup = new ButtonGroup<TextButton>();
        root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
        
        Table table = new Table();
        table.setName("map");
        table.setTransform(true);
        MapScrollPane scrollPane = new MapScrollPane(table, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setOverscroll(false, false);
        
        root.add(scrollPane).grow().maxWidth(800.0f).maxHeight(600.0f);
        stage.setScrollFocus(scrollPane);
        
        Stack stack = new Stack();
        table.add(stack).size(800.0f, 600.0f);
        
        Image image = new Image(skin.getTiledDrawable("grid"));
        image.setScaling(Scaling.stretch);
        stack.add(image);

        group = new Group();
        stack.add(group);
        
        stack.addListener(new ClickListener(0) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!dragging) {
                    EntityType entityType = (EntityType) entityButtonGroup.getChecked().getUserObject();
                    PatternType patternType = (PatternType) patternButtonGroup.getChecked().getUserObject();
                    EditorEntity entity = new EditorEntity(entityType, patternType);
                    entity.setPosition(x, y);
                    entities.add(entity);
                    group.addActor(entity);
                } else {
                    dragging = false;
                }
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y,
                    int pointer, int button) {
                dragTarget = null;
                for (int i = entities.size - 1; i >= 0; i--) {
                    EditorEntity entity = entities.get(i);
                    if (x > entity.getX() - entity.getWidth() / 2.0f && x < entity.getX() + entity.getWidth() / 2.0f && y > entity.getY() - entity.getHeight() / 2.0f && y < entity.getY() + entity.getHeight() / 2.0f) {
                        dragTarget = entity;
                        break;
                    }
                }
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y,
                    int pointer) {
                super.touchDragged(event, x, y, pointer);
                dragging = true;
                
                if (dragTarget != null) {
                    x = MathUtils.clamp(x, 0.0f, 800.0f);
                    y = MathUtils.clamp(y, 0.0f, 600.0f);
                    dragTarget.setX(x);
                    dragTarget.setY(y);
                }
            }
        });
        
        stack.addListener(new ClickListener(1) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                for (int i = entities.size - 1; i >= 0; i--) {
                    EditorEntity entity = entities.get(i);
                    if (x > entity.getX() - entity.getWidth() / 2.0f && x < entity.getX() + entity.getWidth() / 2.0f && y > entity.getY() - entity.getHeight() / 2.0f && y < entity.getY() + entity.getHeight() / 2.0f) {
                        entities.removeIndex(i);
                        group.removeActor(entity);
                        break;
                    }
                }
            }
            
        });
                
        table = new Table(skin);
        table.setName("controls");
        table.setBackground("tools");
        root.add(table).growY().pad(20.0f);
        
        table.defaults().grow().pad(10.0f);
        Table subTable = new Table(skin);
        subTable.setName("entities");
        subTable.setBackground("entities");
        table.add(subTable);
        
        HorizontalGroup hgroup = new HorizontalGroup();
        hgroup.wrap();
        hgroup.grow();
        hgroup.center();
        subTable.add(hgroup).grow().pad(10.0f);
        
        ImageButton imageButton = new ImageButton(skin, "diamond");
        imageButton.setUserObject(EntityType.DIAMOND);
        entityButtonGroup.add(imageButton);
        hgroup.addActor(imageButton);
        
        imageButton = new ImageButton(skin, "heptagon");
        imageButton.setUserObject(EntityType.HEPTAGON);
        entityButtonGroup.add(imageButton);
        hgroup.addActor(imageButton);
        
        imageButton = new ImageButton(skin, "rectangle");
        imageButton.setUserObject(EntityType.RECTANGLE);
        entityButtonGroup.add(imageButton);
        hgroup.addActor(imageButton);
        
        imageButton = new ImageButton(skin, "square");
        imageButton.setUserObject(EntityType.SQUARE);
        entityButtonGroup.add(imageButton);
        hgroup.addActor(imageButton);
        
        imageButton = new ImageButton(skin, "star");
        imageButton.setUserObject(EntityType.STAR);
        entityButtonGroup.add(imageButton);
        hgroup.addActor(imageButton);
        
        imageButton = new ImageButton(skin, "triangle");
        imageButton.setUserObject(EntityType.TRIANGLE);
        entityButtonGroup.add(imageButton);
        hgroup.addActor(imageButton);
        
        imageButton = new ImageButton(skin, "u");
        imageButton.setUserObject(EntityType.U);
        entityButtonGroup.add(imageButton);
        hgroup.addActor(imageButton);
        
        table.row();
        subTable = new Table(skin);
        subTable.setName("patterns");
        subTable.setBackground("patterns");
        table.add(subTable);
        
        hgroup = new HorizontalGroup();
        hgroup.wrap();
        hgroup.grow();
        hgroup.center();
        subTable.add(hgroup).grow().pad(10.0f);
        
        imageButton = new ImageButton(skin);
        imageButton.setUserObject(PatternType.NORMAL);
        patternButtonGroup.add(imageButton);
        hgroup.addActor(imageButton);
        
        imageButton = new ImageButton(skin, "left");
        imageButton.setUserObject(PatternType.LEFT);
        patternButtonGroup.add(imageButton);
        hgroup.addActor(imageButton);
        
        imageButton = new ImageButton(skin, "right");
        imageButton.setUserObject(PatternType.RIGHT);
        patternButtonGroup.add(imageButton);
        hgroup.addActor(imageButton);
        
        imageButton = new ImageButton(skin, "ccw");
        imageButton.setUserObject(PatternType.CCW);
        patternButtonGroup.add(imageButton);
        hgroup.addActor(imageButton);
        
        imageButton = new ImageButton(skin, "cw");
        imageButton.setUserObject(PatternType.CW);
        patternButtonGroup.add(imageButton);
        hgroup.addActor(imageButton);
        
        imageButton = new ImageButton(skin, "spiral-ccw");
        imageButton.setUserObject(PatternType.SPIRAL_CCW);
        patternButtonGroup.add(imageButton);
        hgroup.addActor(imageButton);
        
        imageButton = new ImageButton(skin, "spiral-cw");
        imageButton.setUserObject(PatternType.SPIRAL_CW);
        patternButtonGroup.add(imageButton);
        hgroup.addActor(imageButton);
        
        imageButton = new ImageButton(skin, "diamond-ccw");
        imageButton.setUserObject(PatternType.DIAMOND_CCW);
        patternButtonGroup.add(imageButton);
        hgroup.addActor(imageButton);
        
        imageButton = new ImageButton(skin, "diamond-cw");
        imageButton.setUserObject(PatternType.DIAMOND_CW);
        patternButtonGroup.add(imageButton);
        hgroup.addActor(imageButton);
        
        imageButton = new ImageButton(skin, "dive");
        imageButton.setUserObject(PatternType.DIVE);
        patternButtonGroup.add(imageButton);
        hgroup.addActor(imageButton);
        
        imageButton = new ImageButton(skin, "wave");
        imageButton.setUserObject(PatternType.WAVE);
        patternButtonGroup.add(imageButton);
        hgroup.addActor(imageButton);
        
        table.row();
        subTable = new Table(skin);
        subTable.setName("buttons");
        subTable.setBackground("buttons");
        table.add(subTable);
        
        subTable.defaults().space(10.0f);
        TextButton textButton = new TextButton("Clear All", skin);
        subTable.add(textButton).padLeft(20.0f).padRight(20.0f).colspan(3).padTop(10.0f);
        
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fileName = "";
                entities.clear();
                group.clearChildren();
            }
        });
        
        subTable.row();
        subTable.defaults().padBottom(10.0f);
        textButton = new TextButton("Save", skin);
        subTable.add(textButton).padLeft(10.0f);
        
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                showSaveDialog();
            }
        });
        
        textButton = new TextButton("Load", skin);
        subTable.add(textButton);
        
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                showLoadDialog();
            }
        });
        
        textButton = new TextButton("Quit", skin);
        subTable.add(textButton).padRight(10.0f);
        
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Core.stateManager.loadState("menu");
            }
        });
    }
    
    @Override
    public void draw(SpriteBatch spriteBatch, float delta) {
        Gdx.gl.glClearColor(0 / 255.0f, 0 / 255.0f, 0 / 255.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void act(float delta) {
        stage.act(delta);
    }

    @Override
    public void dispose() {
        
    }

    @Override
    public void stop() {
        stage.dispose();
    }
    
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    
    public void showSaveDialog() {
        Dialog dialog = new Dialog("Save Formation", skin) {
            @Override
            protected void result(Object object) {
                if ((Boolean) object) {
                    TextField textField = (TextField) findActor("text");
                    save(textField.getText());
                    fileName = textField.getText();
                }
            }
        };
        
        dialog.getTitleLabel().setAlignment(Align.center);
        
        TextField textField = new TextField(fileName, skin);
        textField.setName("text");
        dialog.getContentTable().add(textField).growX().pad(30.0f);
        
        dialog.button("OK", true).button("Cancel", false);
        dialog.key(Keys.ENTER, true).key(Keys.ESCAPE, false);
        
        dialog.show(stage);
        
        stage.setKeyboardFocus(textField);
        if (textField.getText().length() > 0) {
            textField.setSelection(0, textField.getText().length());
        }
        
        dialog.setSize(300.0f, 200.0f);
        dialog.setPosition(Gdx.graphics.getWidth() / 2.0f, Gdx.graphics.getHeight() / 2.0f, Align.center);
    }
    
    public void save(String name) {
        Json json = new Json(JsonWriter.OutputType.json);
        FileHandle file = Gdx.files.local(Core.DATA_PATH + "/formations/" + name + ".json");
        file.writeString(json.prettyPrint(json.toJson(entities, Array.class, EditorEntity.class)), false);
    }
    
    public void showLoadDialog() {
        Dialog dialog = new Dialog("Load Formation", skin) {
            @Override
            protected void result(Object object) {
                if ((Boolean) object) {
                    List<String> list = (List) findActor("list");
                    load(list.getSelected());
                    fileName = list.getSelected();
                }
            }
        };
        
        dialog.getTitleLabel().setAlignment(Align.center);
        
        List<String> list = new List(skin);
        list.setName("list");
        FileHandle fileHandle = Gdx.files.local(Core.DATA_PATH + "/formations/");
        Array<String> items = new Array<String>();
        for (FileHandle file : fileHandle.list()) {
            items.add(file.nameWithoutExtension());
        }
        list.setItems(items);
        ScrollPane scrollPane = new ScrollPane(list, skin, "list");
        scrollPane.setFadeScrollBars(false);
        dialog.getContentTable().add(scrollPane).growX().pad(30.0f);
        
        dialog.button("OK", true).button("Cancel", false);
        dialog.key(Keys.ENTER, true).key(Keys.ESCAPE, false);
        
        dialog.show(stage);
        stage.setScrollFocus(scrollPane);
        
        dialog.setSize(400.0f, 500.0f);
        dialog.setPosition(Gdx.graphics.getWidth() / 2.0f, Gdx.graphics.getHeight() / 2.0f, Align.center);
    }
    
    public void load(String name) {
        Json json = new Json(JsonWriter.OutputType.json);
        FileHandle file = Gdx.files.local(Core.DATA_PATH + "/formations/" + name + ".json");
        entities = json.fromJson(Array.class, EditorEntity.class, file);

        group.clearChildren();
        for (EditorEntity entity : entities) {
            group.addActor(entity);
        }
    }
    
    public static enum EntityType {
        DIAMOND, HEPTAGON, RECTANGLE, SQUARE, STAR, TRIANGLE, U
    }
    
    public static enum PatternType {
        CCW, CW, DIAMOND_CCW, DIAMOND_CW, DIVE, LEFT, RIGHT, NORMAL, SPIRAL_CCW, SPIRAL_CW, WAVE
    }
    
    public static class EditorEntity extends Actor implements Json.Serializable {
        public EntityType entityType;
        public PatternType patternType;
        
        public EditorEntity() {
            setTouchable(Touchable.disabled);
        }
        
        public EditorEntity(EntityType entityType, PatternType patternType) {
            this();
            
            this.entityType = entityType;
            this.patternType = patternType;
            
            switch (entityType) {
                case DIAMOND:
                    setWidth(skin.getDrawable("enemy-diamond").getMinWidth());
                    setHeight(skin.getDrawable("enemy-diamond").getMinHeight());
                    break;
                case HEPTAGON:
                    setWidth(skin.getDrawable("enemy-heptagon").getMinWidth());
                    setHeight(skin.getDrawable("enemy-heptagon").getMinHeight());
                    break;
                case RECTANGLE:
                    setWidth(skin.getDrawable("enemy-rectangle").getMinWidth());
                    setHeight(skin.getDrawable("enemy-rectangle").getMinHeight());
                    break;
                case SQUARE:
                    setWidth(skin.getDrawable("enemy-square").getMinWidth());
                    setHeight(skin.getDrawable("enemy-square").getMinHeight());
                    break;
                case STAR:
                    setWidth(skin.getDrawable("enemy-star").getMinWidth());
                    setHeight(skin.getDrawable("enemy-star").getMinHeight());
                    break;
                case TRIANGLE:
                    setWidth(skin.getDrawable("enemy-triangle").getMinWidth());
                    setHeight(skin.getDrawable("enemy-triangle").getMinHeight());
                    break;
                case U:
                    setWidth(skin.getDrawable("enemy-u").getMinWidth());
                    setHeight(skin.getDrawable("enemy-u").getMinHeight());
                    break;
            }
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            Drawable entityDrawable = null;
            
            switch (entityType) {
                case DIAMOND:
                    entityDrawable = skin.getDrawable("enemy-diamond");
                    setWidth(entityDrawable.getMinWidth());
                    break;
                case HEPTAGON:
                    entityDrawable = skin.getDrawable("enemy-heptagon");
                    break;
                case RECTANGLE:
                    entityDrawable = skin.getDrawable("enemy-rectangle");
                    break;
                case SQUARE:
                    entityDrawable = skin.getDrawable("enemy-square");
                    break;
                case STAR:
                    entityDrawable = skin.getDrawable("enemy-star");
                    break;
                case TRIANGLE:
                    entityDrawable = skin.getDrawable("enemy-triangle");
                    break;
                case U:
                    entityDrawable = skin.getDrawable("enemy-u");
                    break;
            }
            
            Drawable patternDrawable = null;
            
            switch (patternType) {
                case CCW:
                    patternDrawable = skin.getDrawable("icon-ccw");
                    break;
                case CW:
                    patternDrawable = skin.getDrawable("icon-cw");
                    break;
                case DIAMOND_CCW:
                    patternDrawable = skin.getDrawable("icon-diamond-ccw");
                    break;
                case DIAMOND_CW:
                    patternDrawable = skin.getDrawable("icon-diamond-cw");
                    break;
                case DIVE:
                    patternDrawable = skin.getDrawable("icon-dive");
                    break;
                case LEFT:
                    patternDrawable = skin.getDrawable("icon-left");
                    break;
                case NORMAL:
                    patternDrawable = skin.getDrawable("icon-normal");
                    break;
                case RIGHT:
                    patternDrawable = skin.getDrawable("icon-right");
                    break;
                case SPIRAL_CCW:
                    patternDrawable = skin.getDrawable("icon-spiral-ccw");
                    break;
                case SPIRAL_CW:
                    patternDrawable = skin.getDrawable("icon-spiral-cw");
                    break;
                case WAVE:
                    patternDrawable = skin.getDrawable("icon-wave");
                    break;
            }
            
            if (entityDrawable != null) {
                batch.setColor(Color.WHITE);
                entityDrawable.draw(batch, getX() - getWidth() / 2.0f, getY() - getHeight() / 2.0f, getWidth(), getHeight());
            }
            
            if (patternDrawable != null) {
                batch.setColor(Color.CYAN);
                patternDrawable.draw(batch, getX() - patternDrawable.getMinWidth() / 2.0f, getY() - patternDrawable.getMinHeight() / 2.0f, patternDrawable.getMinWidth(), patternDrawable.getMinHeight());
            }
        }

        @Override
        public void write(Json json) {
            json.writeValue("entity", entityType, EntityType.class);
            json.writeValue("pattern", patternType, PatternType.class);
            json.writeValue("x", getX());
            json.writeValue("y", getY());
        }

        @Override
        public void read(Json json, JsonValue jsonData) {
            entityType = EntityType.valueOf(jsonData.getString("entity"));
            patternType = PatternType.valueOf(jsonData.getString("pattern"));
            setX(jsonData.getFloat("x"));
            setY(jsonData.getFloat("y"));
            
            if (Core.stateManager.getLoadedStateName().equals("editor")) {
                switch (entityType) {
                    case DIAMOND:
                        setWidth(skin.getDrawable("enemy-diamond").getMinWidth());
                        setHeight(skin.getDrawable("enemy-diamond").getMinHeight());
                        break;
                    case HEPTAGON:
                        setWidth(skin.getDrawable("enemy-heptagon").getMinWidth());
                        setHeight(skin.getDrawable("enemy-heptagon").getMinHeight());
                        break;
                    case RECTANGLE:
                        setWidth(skin.getDrawable("enemy-rectangle").getMinWidth());
                        setHeight(skin.getDrawable("enemy-rectangle").getMinHeight());
                        break;
                    case SQUARE:
                        setWidth(skin.getDrawable("enemy-square").getMinWidth());
                        setHeight(skin.getDrawable("enemy-square").getMinHeight());
                        break;
                    case STAR:
                        setWidth(skin.getDrawable("enemy-star").getMinWidth());
                        setHeight(skin.getDrawable("enemy-star").getMinHeight());
                        break;
                    case TRIANGLE:
                        setWidth(skin.getDrawable("enemy-triangle").getMinWidth());
                        setHeight(skin.getDrawable("enemy-triangle").getMinHeight());
                        break;
                    case U:
                        setWidth(skin.getDrawable("enemy-u").getMinWidth());
                        setHeight(skin.getDrawable("enemy-u").getMinHeight());
                        break;
                }
            }
        }
    }
}