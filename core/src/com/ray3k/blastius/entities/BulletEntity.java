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
import com.ray3k.blastius.Core;
import com.ray3k.blastius.Entity;
import com.ray3k.blastius.SpineTwoColorEntity;
import com.ray3k.blastius.states.GameState;

public class BulletEntity extends SpineTwoColorEntity {
    private Entity parent;
    private static final float BORDER = 50.0f;
    
    public BulletEntity(Entity parent) {
        super(Core.DATA_PATH + "/spine/blast.json", "blue", GameState.twoColorPolygonBatch);
        this.parent = parent;
        getAnimationState().getCurrent(0).setLoop(false);
        setDepth(10);
    }
    
    @Override
    public void actSub(float delta) {
        if (getX() + BORDER < 0.0f || getX() - BORDER > GameState.GAME_WIDTH || getY() + BORDER < 0.0f || getY() - BORDER > GameState.GAME_HEIGHT) {
            dispose();
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

    public Entity getParent() {
        return parent;
    }

    public void setParent(Entity parent) {
        this.parent = parent;
    }
    
}
