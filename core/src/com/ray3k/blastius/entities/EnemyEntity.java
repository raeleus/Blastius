/*
 * The MIT License
 *
 * Copyright 2018 Raymond Buckley.
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

package com.ray3k.blastius.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ray3k.blastius.Core;
import com.ray3k.blastius.Entity;
import com.ray3k.blastius.SpineTwoColorEntity;
import com.ray3k.blastius.states.EditorState.EntityType;
import com.ray3k.blastius.states.EditorState.PatternType;
import com.ray3k.blastius.states.GameState;

public class EnemyEntity extends SpineTwoColorEntity {
    private final static float BORDER = 200.0f;
    private EntityType enemyType;
    private PatternType patternType;
    private int health;
    private boolean firing;
    private float bulletTime;
    private float bulletTimer;
    private static final float BULLET_SPEED = 100.0f;
    private int coins;
    private Array<TracerEntity> collisionList;

    public EnemyEntity(EntityType enemyType, PatternType patternType) {
        super(Core.DATA_PATH + "/spine/enemy.json", "normal", GameState.twoColorPolygonBatch);
        getAnimationState().getData().setDefaultMix(0.0f);
        this.enemyType = enemyType;
        this.patternType = patternType;
        
        collisionList = new  Array<TracerEntity>();
        
        setMotion(100.0f, 270.0f);
        firing = false;
        
        String skin = "";
        switch(enemyType) {
            case DIAMOND:
                skin = "diamond";
                health = 200;
                coins = 2;
                break;
            case HEPTAGON:
                skin = "heptagon";
                health = 200;
                firing = true;
                bulletTime = 2.0f;
                bulletTimer = bulletTime;
                coins = 3;
                break;
            case RECTANGLE:
                skin = "rectangle";
                health = 500;
                coins = 5;
                break;
            case SQUARE:
                skin = "square";
                health = 100;
                coins = 2;
                break;
            case STAR:
                skin = "star";
                health = 300;
                firing = true;
                bulletTime = 1.0f;
                bulletTimer = bulletTime;
                coins = 3;
                break;
            case TRIANGLE:
                skin = "triangle";
                health = 100;
                coins = 1;
                break;
            case U:
                skin = "u";
                health = 100;
                firing = true;
                bulletTime = .5f;
                bulletTimer = bulletTime;
                coins = 5;
                break;
        }
        
        health *= GameState.enemyHealthMultiplier;
        
        getSkeleton().setSkin(skin);
        
        String animation = "";
        switch(patternType) {
            case CCW:
                animation = "ccw";
                break;
            case CW:
                animation = "cw";
                break;
            case DIAMOND_CCW:
                animation = "diamond ccw";
                break;
            case DIAMOND_CW:
                animation = "diamond cw";
                break;
            case DIVE:
                animation = "dive";
                break;
            case LEFT:
                animation = "left";
                break;
            case NORMAL:
                animation = "normal";
                break;
            case RIGHT:
                animation = "right";
                break;
            case SPIRAL_CCW:
                animation = "spiral ccw";
                break;
            case SPIRAL_CW:
                animation = "spiral cw";
                break;
            case WAVE:
                animation = "wave";
                break;
        }
        getAnimationState().setAnimation(0, animation, true);
    }
    
    private static Vector2 temp1 = new Vector2();
    private static Vector2 temp2 = new Vector2();
    
    @Override
    public void actSub(float delta) {
        float x = getSkeletonBounds().getMinX() + getSkeletonBounds().getWidth() / 2.0f;
        float y = getSkeletonBounds().getMinY() + getSkeletonBounds().getHeight() / 2.0f;
        
        if (getSkeletonBounds().getMaxY() < 0.0f) {
            dispose();
        }
        
        if (firing) {
            bulletTimer -= delta;
            if (bulletTimer < 0) {
                bulletTimer = bulletTime;
                
                BulletEntity bullet = new BulletEntity(this);
                temp1.x = x;
                temp1.y = y;
                temp2.x = GameState.player.getX();
                temp2.y = GameState.player.getY();
                bullet.setMotion(BULLET_SPEED, temp2.sub(temp1).angle());
                bullet.setPosition(x, y);
                bullet.getAnimationState().setAnimation(0, "red", false);
                GameState.entityManager.addEntity(bullet);
            }
        }
        
        if (getSkeletonBounds().getMinY() + getSkeletonBounds().getHeight() / 2.0f < GameState.GAME_HEIGHT) {
            for (Entity entity : GameState.entityManager.getEntities()) {
                if (entity instanceof BulletEntity) {
                    BulletEntity bullet = (BulletEntity) entity;
                    if (bullet.getParent().getClass().equals(PlayerEntity.class)) {
                        if (getSkeletonBounds().aabbIntersectsSkeleton(bullet.getSkeletonBounds())) {
                            health -= 100;
                            getAnimationState().setAnimation(1, "hurt", false);
                            checkHealth();
                            bullet.dispose();
                            break;
                        }
                    }
                } else if (entity instanceof TracerEntity) {
                    TracerEntity tracer = (TracerEntity) entity;
                    if (!collisionList.contains(tracer, false)) {
                        if (getSkeletonBounds().aabbIntersectsSkeleton(tracer.getSkeletonBounds())) {
                            health -= 50;
                            getAnimationState().setAnimation(1, "hurt", false);
                            checkHealth();
                            collisionList.add(tracer);
                            break;
                        }
                    }
                }
            }
        }
    }
    
    private void checkHealth() {
        if (health <= 0) {
            dispose();
            
            switch (MathUtils.random(1)) {
                case 0:
                    GameState.inst().playSound("explosion 1", .5f);
                    break;
                case 1:
                    GameState.inst().playSound("explosion 3", .5f);
                    break;
            }
            
            float x = getSkeletonBounds().getMinX() + getSkeletonBounds().getWidth() / 2.0f;
            float y = getSkeletonBounds().getMinY() + getSkeletonBounds().getHeight() / 2.0f;
            
            for (int i = 0; i < coins; i++) {
                CoinEntity coin = new CoinEntity();
                coin.setPosition(x, y);
                GameState.entityManager.addEntity(coin);
            }

            if (MathUtils.randomBoolean(.02f)) {
                PowerUpEntity powerup = new PowerUpEntity();
                powerup.setPosition(x, y);
                GameState.entityManager.addEntity(powerup);
            }
        }
    }

    @Override
    public void drawSub(SpriteBatch spriteBatch, float delta) {
    }

    @Override
    public void create() {
    }

    @Override
    public void actEnd(float delta) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void collision(Entity other) {
    }

}
