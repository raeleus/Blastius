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
import com.ray3k.blastius.Core;
import com.ray3k.blastius.Entity;
import com.ray3k.blastius.SpineTwoColorEntity;
import com.ray3k.blastius.states.GameState;

public class PowerUpEntity extends SpineTwoColorEntity {
    private int bounces;
    public static final int MAX_BOUNCES = 3;

    public PowerUpEntity() {
        super(Core.DATA_PATH + "/spine/powerup.json", "animation", GameState.twoColorPolygonBatch);
        setMotion(200.0f, MathUtils.random(360.0f));
    }
    
    @Override
    public void actSub(float delta) {
        if (bounces < MAX_BOUNCES) {
            if (getX() < 0) {
                setX(0.0f);
                setXspeed(-getXspeed());
                bounces++;
            } else if (getX() > GameState.GAME_WIDTH) {
                setX(GameState.GAME_WIDTH);
                setXspeed(-getXspeed());
                bounces++;
            }

            if (getY() < 0) {
                setY(0.0f);
                setYspeed(-getYspeed());
                bounces++;
            } else if (getY() > GameState.GAME_HEIGHT) {
                setY(GameState.GAME_HEIGHT);
                setYspeed(-getYspeed());
                bounces++;
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
