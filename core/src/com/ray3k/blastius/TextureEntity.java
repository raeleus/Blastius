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

package com.ray3k.blastius;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public abstract class TextureEntity extends Entity {
    private TextureRegion textureRegion;
    private final Vector2 scale;
    private float rotation;
    private final Vector2 offset;

    public TextureEntity() {
        scale = new Vector2();
        scale.x = 1.0f;
        scale.y = 1.0f;
        
        rotation = 0.0f;
        
        offset = new Vector2();
    }

    @Override
    public void draw(SpriteBatch spriteBatch, float delta) {
        if (textureRegion != null) {
            spriteBatch.draw(textureRegion, getX() + getXspeed() * delta, getY() + getYspeed() * delta, getOffsetX(), getOffsetY(), textureRegion.getRegionWidth(), textureRegion.getRegionHeight(), getScaleX(), getScaleY(), getRotation());
        }
        
        drawSub(spriteBatch, delta);
    }
    
    public abstract void drawSub(SpriteBatch spriteBatch, float delta);
    
    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    public void setTextureRegion(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }

    public Vector2 getScale() {
        return scale.cpy();
    }
    
    public float getScaleX() {
        return scale.x;
    }
    
    public void setScaleX(float scaleX) {
        scale.x = scaleX;
    }
    
    public float getScaleY() {
        return scale.y;
    }
    
    public void setScaleY(float scaleY) {
        scale.y = scaleY;
    }
    
    public float getRotation() {
        return rotation;
    }
    
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }
    
    public void addRotation(float rotation) {
        this.rotation += rotation;
    }
    
    public Vector2 getOffset() {
        return offset.cpy();
    }
    
    public float getOffsetX() {
        return offset.x;
    }
    
    public void setOffsetX(float x) {
        offset.x = x;
    }
    
    public float getOffsetY() {
        return offset.y;
    }
    
    public void setOffsetY(float y) {
        offset.y = y;
    }
}
