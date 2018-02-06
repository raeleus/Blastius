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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.ray3k.blastius.Core;
import com.ray3k.blastius.Entity;
import com.ray3k.blastius.Maths;
import com.ray3k.blastius.SpineTwoColorEntity;
import com.ray3k.blastius.states.GameState;

public class PlayerEntity extends SpineTwoColorEntity {
    private static final float MOVE_SPEED = 300.0f;
    private static final float BORDER = 25.0f;
    private static final float BULLET_DELAY = .3f;
    private static final float BULLET_SPEED = 400.0f;
    private float bulletTimer;
    private int powerLevel;

    public PlayerEntity() {
        super(Core.DATA_PATH + "/spine/player.json", "normal", GameState.twoColorPolygonBatch);
        getAnimationState().getCurrent(0).setLoop(false);
        
        getAnimationState().addListener(new AnimationState.AnimationStateAdapter() {
            @Override
            public void complete(AnimationState.TrackEntry entry) {
                if (entry.getAnimation().getName().equals("die")) {
                    PlayerEntity.this.dispose();
                    GameState.entityManager.addEntity(new GameOverTimerEntity(2.0f));
                }
            }
        });
        bulletTimer = BULLET_DELAY;
        powerLevel = 1;
        setDepth(-10);
    }

    @Override
    public void actSub(float delta) {
        if (!getAnimationState().getCurrent(0).getAnimation().getName().equals("die")) {
            if (Gdx.input.isKeyPressed(Keys.LEFT)) {
                setMotion(MOVE_SPEED, 180.0f);

                if (!getAnimationState().getCurrent(0).getAnimation().getName().equals("left")) {
                    getAnimationState().setAnimation(0, "left", false);
                }
            } else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
                setMotion(MOVE_SPEED, 0.0f);

                if (!getAnimationState().getCurrent(0).getAnimation().getName().equals("right")) {
                    getAnimationState().setAnimation(0, "right", false);
                }
            } else {
                setMotion(0.0f, 0.0f);

                if (!getAnimationState().getCurrent(0).getAnimation().getName().equals("normal")) {
                    getAnimationState().setAnimation(0, "normal", false);
                }
            }

            if (Gdx.input.isKeyPressed(Keys.UP)) {
                if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.RIGHT)) {
                    setMotion(MOVE_SPEED, Maths.approach360(getDirection(), 90.0f, 45.0f));
                } else {
                    setMotion(MOVE_SPEED, 90.0f);
                }
            } else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
                if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.RIGHT)) {
                    setMotion(MOVE_SPEED, Maths.approach360(getDirection(), 270.0f, 45.0f));
                } else {
                    setMotion(MOVE_SPEED, 270.0f);
                }
            }

            if (getX() + getXspeed() * delta  - BORDER < 0) {
                setX(BORDER);
                setXspeed(0.0f);
            } else if (getX() + getXspeed() * delta  + BORDER > GameState.GAME_WIDTH) {
                setX(GameState.GAME_WIDTH - BORDER);
                setXspeed(0.0f);
            }

            if (getY() + getYspeed() * delta  - BORDER < 0) {
                setY(BORDER);
                setYspeed(0.0f);
            } else if (getY() + getYspeed() * delta  + BORDER > GameState.GAME_HEIGHT) {
                setY(GameState.GAME_HEIGHT - BORDER);
                setYspeed(0.0f);
            }

            bulletTimer -= delta;

            if (bulletTimer <= 0.0f && Gdx.input.isKeyPressed(Keys.SPACE)) {
                bulletTimer = BULLET_DELAY;

                fire();
            }

            for (Entity entity : GameState.entityManager.getEntities()) {
                if (entity instanceof BulletEntity) {
                    BulletEntity bullet = (BulletEntity) entity;
                    if (!bullet.getParent().equals(this)) {
                        if (getSkeletonBounds().aabbIntersectsSkeleton(bullet.getSkeletonBounds())) {
                            bullet.dispose();
                            if (!getAnimationState().getCurrent(0).getAnimation().getName().equals("die")) {
                                GameState.inst().playSound("explosion 2", .5f);
                                setMotion(0.0f, 0.0f);
                                getAnimationState().setAnimation(0, "die", false);
                            }
                        }
                    }
                } else if (entity instanceof EnemyEntity) {
                    EnemyEntity enemy = (EnemyEntity) entity;
                    if (getSkeletonBounds().aabbIntersectsSkeleton(enemy.getSkeletonBounds())) {
                        enemy.dispose();
                        if (!getAnimationState().getCurrent(0).getAnimation().getName().equals("die")) {
                            GameState.inst().playSound("explosion 2", .5f);
                            setMotion(0.0f, 0.0f);
                            getAnimationState().setAnimation(0, "die", false);
                        }
                    }
                } else if (entity instanceof PowerUpEntity) {
                    
                    PowerUpEntity powerup = (PowerUpEntity) entity;
                    if (getSkeletonBounds().aabbIntersectsSkeleton(powerup.getSkeletonBounds())) {
                        GameState.inst().playSound("powerup", .5f);
                        powerup.dispose();
                        if (powerLevel < 7) {
                            powerLevel++;
                        } else {
                            GameState.inst().addScore(100);
                        }
                    }
                } else if (entity instanceof CoinEntity) {
                    
                    CoinEntity coin = (CoinEntity) entity;
                    if (getSkeletonBounds().aabbIntersectsSkeleton(coin.getSkeletonBounds())) {
                        GameState.inst().playSound("coin", .5f);
                        coin.dispose();
                        GameState.inst().addScore(10);
                    }
                }
            }
        }
    }
    
    private void fire() {
        GameState.inst().playSound("bullet 1", .05f, MathUtils.random(.9f, 1.2f));
        
        switch (powerLevel) {
            case 1: 
                BulletEntity bullet = new BulletEntity(this);
                bullet.setMotion(BULLET_SPEED, 90.0f);
                bullet.setPosition(getX(), getY());
                GameState.entityManager.addEntity(bullet);
                break;
            case 2:
                bullet = new BulletEntity(this);
                bullet.setMotion(BULLET_SPEED, 90.0f);
                bullet.setPosition(getX() - 10, getY());
                GameState.entityManager.addEntity(bullet);
                
                bullet = new BulletEntity(this);
                bullet.setMotion(BULLET_SPEED, 90.0f);
                bullet.setPosition(getX() + 10, getY());
                GameState.entityManager.addEntity(bullet);
                break;
            case 3:
                bullet = new BulletEntity(this);
                bullet.setMotion(BULLET_SPEED, 75.0f);
                bullet.setPosition(getX(), getY());
                GameState.entityManager.addEntity(bullet);
                
                bullet = new BulletEntity(this);
                bullet.setMotion(BULLET_SPEED, 90.0f);
                bullet.setPosition(getX() - 10, getY());
                GameState.entityManager.addEntity(bullet);
                
                bullet = new BulletEntity(this);
                bullet.setMotion(BULLET_SPEED, 90.0f);
                bullet.setPosition(getX() + 10, getY());
                GameState.entityManager.addEntity(bullet);
                
                bullet = new BulletEntity(this);
                bullet.setMotion(BULLET_SPEED, 105.0f);
                bullet.setPosition(getX(), getY());
                GameState.entityManager.addEntity(bullet);
                break;
            case 4:
                bullet = new BulletEntity(this);
                bullet.setMotion(BULLET_SPEED, 65.0f);
                bullet.setPosition(getX(), getY());
                GameState.entityManager.addEntity(bullet);
                
                bullet = new BulletEntity(this);
                bullet.setMotion(BULLET_SPEED, 270.0f);
                bullet.setPosition(getX(), getY());
                GameState.entityManager.addEntity(bullet);
                
                bullet = new BulletEntity(this);
                bullet.setMotion(BULLET_SPEED, 115.0f);
                bullet.setPosition(getX(), getY());
                GameState.entityManager.addEntity(bullet);
                
                TracerEntity tracer = new TracerEntity(this);
                tracer.setPosition(getX() - 10, getY());
                GameState.entityManager.addEntity(tracer);
                
                tracer = new TracerEntity(this);
                tracer.setPosition(getX() + 10, getY());
                GameState.entityManager.addEntity(tracer);
                break;
            case 5:
                bullet = new BulletEntity(this);
                bullet.setMotion(BULLET_SPEED, 65.0f);
                bullet.setPosition(getX(), getY());
                GameState.entityManager.addEntity(bullet);
                
                bullet = new BulletEntity(this);
                bullet.setMotion(BULLET_SPEED, 270.0f);
                bullet.setPosition(getX(), getY());
                GameState.entityManager.addEntity(bullet);
                
                bullet = new BulletEntity(this);
                bullet.setMotion(BULLET_SPEED, 115.0f);
                bullet.setPosition(getX(), getY());
                GameState.entityManager.addEntity(bullet);
                
                bullet = new BulletEntity(this);
                bullet.setMotion(BULLET_SPEED, 10.0f);
                bullet.setPosition(getX(), getY());
                GameState.entityManager.addEntity(bullet);
                
                bullet = new BulletEntity(this);
                bullet.setMotion(BULLET_SPEED, 170.0f);
                bullet.setPosition(getX(), getY());
                GameState.entityManager.addEntity(bullet);
                
                tracer = new TracerEntity(this);
                tracer.setPosition(getX() - 10, getY());
                GameState.entityManager.addEntity(tracer);
                
                tracer = new TracerEntity(this);
                tracer.setPosition(getX() + 10, getY());
                GameState.entityManager.addEntity(tracer);
                break;
            case 6:
                bullet = new BulletEntity(this);
                bullet.setMotion(BULLET_SPEED, 55.0f);
                bullet.setPosition(getX(), getY());
                GameState.entityManager.addEntity(bullet);
                
                bullet = new BulletEntity(this);
                bullet.setMotion(BULLET_SPEED, 270.0f);
                bullet.setPosition(getX() - 10, getY());
                GameState.entityManager.addEntity(bullet);
                
                bullet = new BulletEntity(this);
                bullet.setMotion(BULLET_SPEED, 270.0f);
                bullet.setPosition(getX() + 10, getY());
                GameState.entityManager.addEntity(bullet);
                
                bullet = new BulletEntity(this);
                bullet.setMotion(BULLET_SPEED, 125.0f);
                bullet.setPosition(getX(), getY());
                GameState.entityManager.addEntity(bullet);
                
                bullet = new BulletEntity(this);
                bullet.setMotion(BULLET_SPEED, 10.0f);
                bullet.setPosition(getX(), getY());
                GameState.entityManager.addEntity(bullet);
                
                bullet = new BulletEntity(this);
                bullet.setMotion(BULLET_SPEED, 170.0f);
                bullet.setPosition(getX(), getY());
                GameState.entityManager.addEntity(bullet);
                
                bullet = new BulletEntity(this);
                bullet.setMotion(BULLET_SPEED, 90.0f);
                bullet.setPosition(getX() - 20, getY() - 10);
                GameState.entityManager.addEntity(bullet);
                
                bullet = new BulletEntity(this);
                bullet.setMotion(BULLET_SPEED, 90.0f);
                bullet.setPosition(getX() - 20, getY() + 10);
                GameState.entityManager.addEntity(bullet);
                
                bullet = new BulletEntity(this);
                bullet.setMotion(BULLET_SPEED, 90.0f);
                bullet.setPosition(getX() + 20, getY() - 10);
                GameState.entityManager.addEntity(bullet);
                
                bullet = new BulletEntity(this);
                bullet.setMotion(BULLET_SPEED, 90.0f);
                bullet.setPosition(getX() + 20, getY() + 10);
                GameState.entityManager.addEntity(bullet);
                
                tracer = new TracerEntity(this);
                tracer.setPosition(getX() - 10, getY());
                GameState.entityManager.addEntity(tracer);
                
                tracer = new TracerEntity(this);
                tracer.setPosition(getX() + 10, getY());
                GameState.entityManager.addEntity(tracer);
                break;
            case 7:
                bullet = new BulletEntity(this);
                bullet.setMotion(BULLET_SPEED, 55.0f);
                bullet.setPosition(getX(), getY());
                GameState.entityManager.addEntity(bullet);
                
                bullet = new BulletEntity(this);
                bullet.setMotion(BULLET_SPEED, 270.0f);
                bullet.setPosition(getX() - 10, getY());
                GameState.entityManager.addEntity(bullet);
                
                bullet = new BulletEntity(this);
                bullet.setMotion(BULLET_SPEED, 270.0f);
                bullet.setPosition(getX() + 10, getY());
                GameState.entityManager.addEntity(bullet);
                
                bullet = new BulletEntity(this);
                bullet.setMotion(BULLET_SPEED, 125.0f);
                bullet.setPosition(getX(), getY());
                GameState.entityManager.addEntity(bullet);
                
                bullet = new BulletEntity(this);
                bullet.setMotion(BULLET_SPEED, 10.0f);
                bullet.setPosition(getX(), getY());
                GameState.entityManager.addEntity(bullet);
                
                bullet = new BulletEntity(this);
                bullet.setMotion(BULLET_SPEED, 170.0f);
                bullet.setPosition(getX(), getY());
                GameState.entityManager.addEntity(bullet);
                
                bullet = new BulletEntity(this);
                bullet.setMotion(BULLET_SPEED, 90.0f);
                bullet.setPosition(getX() - 20, getY() - 10);
                GameState.entityManager.addEntity(bullet);
                
                bullet = new BulletEntity(this);
                bullet.setMotion(BULLET_SPEED, 90.0f);
                bullet.setPosition(getX() - 20, getY() + 10);
                GameState.entityManager.addEntity(bullet);
                
                bullet = new BulletEntity(this);
                bullet.setMotion(BULLET_SPEED, 90.0f);
                bullet.setPosition(getX() + 20, getY() - 10);
                GameState.entityManager.addEntity(bullet);
                
                bullet = new BulletEntity(this);
                bullet.setMotion(BULLET_SPEED, 90.0f);
                bullet.setPosition(getX() + 20, getY() + 10);
                GameState.entityManager.addEntity(bullet);
                
                tracer = new TracerEntity(this);
                tracer.setPosition(getX() - 10, getY());
                GameState.entityManager.addEntity(tracer);
                
                tracer = new TracerEntity(this);
                tracer.setPosition(getX() + 10, getY());
                GameState.entityManager.addEntity(tracer);
                
                tracer = new TracerEntity(this);
                tracer.setPosition(getX() - 40, getY());
                GameState.entityManager.addEntity(tracer);
                
                tracer = new TracerEntity(this);
                tracer.setPosition(getX() + 40, getY());
                GameState.entityManager.addEntity(tracer);
                break;
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
