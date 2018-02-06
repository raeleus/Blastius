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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
    private static final Vector2 temp = new Vector2();
    private final Vector2 position;
    private final Vector2 speed;
    private boolean destroyed;
    private final Vector2 gravity;
    private int depth;
    private final Rectangle collisionBox;
    private final Vector2 collisionBoxPosition;
    private boolean checkingCollisions;
    private boolean persistent;

    public Entity() {
        position = new Vector2();
        speed = new Vector2();
        gravity = new Vector2();
        depth = 0;
        destroyed = false;
        collisionBox = new Rectangle();
        collisionBoxPosition = new Vector2();
        collisionBoxPosition.x = 0;
        collisionBoxPosition.y = 0;
        checkingCollisions = false;
        persistent = false;
    }
    
    public abstract void create();
    
    public abstract void act(float delta);
    
    public abstract void actEnd(float delta);
    
    public abstract void draw(SpriteBatch spriteBatch, float delta);
    
    public abstract void destroy();
    
    public abstract void collision(Entity other);
    
    public void dispose() {
        if (!destroyed) {
            destroyed = true;
            destroy();
        }
    }

    public Vector2 getPosition() {
        return position.cpy();
    }
    
    public float getX() {
        return position.x;
    }
    
    public float getY() {
        return position.y;
    }
    
    public void setPosition(Vector2 position) {
        this.position.set(position);
    }
    
    public void setPosition(float x, float y) {
        setX(x);
        setY(y);
    }
    
    public void setX(float x) {
        this.position.x = x;
    }
    
    public void setY(float y) {
        this.position.y = y;
    }
    
    public void addX(float x) {
        this.position.x += x;
    }
    
    public void addY(float y) {
        this.position.y += y;
    }

    public float getSpeed() {
        return speed.len();
    }
    
    public float getXspeed() {
        return speed.x;
    }
    
    public float getYspeed() {
        return speed.y;
    }
    
    public void setSpeed(Vector2 speed) {
        this.speed.set(speed);
    }
    
    public void setXspeed(float x) {
        this.speed.x = x;
    }
    
    public void setYspeed(float y) {
        this.speed.y = y;
    }
    
    public void addXspeed(float x) {
        this.speed.x += x;
    }
    
    public void addYspeed(float y) {
        this.speed.y += y;
    }
    
    public void setMotion(float speed, float direction) {
        this.speed.set(speed, 0);
        this.speed.rotate(direction);
    }
    
    public void addMotion(float speed, float direction) {
        temp.set(speed, 0);
        temp.rotate(direction);
        this.speed.add(temp);
    }
    
    public float getDirection() {
        return this.speed.angle();
    }

    public boolean isDestroyed() {
        return destroyed;
    }
    
    public void setGravityX(float gravityX) {
        gravity.x = gravityX;
    }
    
    public void setGravityY(float gravityY) {
        gravity.y = gravityY;
    }
    
    public void setGravity(float speed, float direction) {
        gravity.set(speed, 0);
        gravity.rotate(direction);
    }
    
    public float getGravityX() {
        return gravity.x;
    }
    
    public float getGravityY() {
        return gravity.y;
    }
    
    public Vector2 getGravity() {
        return gravity.cpy();
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public Rectangle getCollisionBox() {
        return collisionBox;
    }

    public boolean isCheckingCollisions() {
        return checkingCollisions;
    }

    public void setCheckingCollisions(boolean checkingCollisions) {
        this.checkingCollisions = checkingCollisions;
    }

    public void setCollisionBoxX(float collisionBoxX) {
        collisionBoxPosition.x = collisionBoxX;
    }
    
    public void setCollisionBoxY(float collisionBoxY) {
        collisionBoxPosition.y = collisionBoxY;
    }
    
    public float getCollisionBoxX() {
        return collisionBoxPosition.x;
    }
    
    public float getCollisionBoxY() {
        return collisionBoxPosition.y;
    }
    
    public Core getCore() {
        return Core.instance;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }
}
