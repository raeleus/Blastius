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
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonBounds;
import com.esotericsoftware.spine.SkeletonData;

public abstract class SpineEntity extends Entity {
    private Skeleton skeleton;
    private AnimationState animationState;
    private SkeletonBounds skeletonBounds;
    
    public SpineEntity() {
        super();
    }
    
    public SpineEntity(String skeletonDataPath, String animation) {
        super();
        setSkeletonData(skeletonDataPath, animation);
    }
    
    public void setSkeletonData(String skeletonDataPath, String animation) {
        SkeletonData skeletonData = Core.assetManager.get(skeletonDataPath, SkeletonData.class);
        skeleton = new Skeleton(skeletonData);
        AnimationStateData animationStateData = new AnimationStateData(skeletonData);
        animationStateData.setDefaultMix(.25f);
        animationState = new AnimationState(animationStateData);
        animationState.setAnimation(0, animation, true);
        
        skeletonBounds = new SkeletonBounds();
        skeletonBounds.update(skeleton, true);
    }

    @Override
    public void act(float delta) {
        if (skeleton != null) {
            skeleton.setPosition(getX(), getY());
            animationState.update(delta);
            skeleton.updateWorldTransform();
            animationState.apply(skeleton);
            skeletonBounds.update(skeleton, true);
        }
        
        actSub(delta);
    }
    
    public abstract void actSub(float delta);

    @Override
    public void draw(SpriteBatch spriteBatch, float delta) {
        if (skeleton != null) {
            Core.skeletonRenderer.draw(spriteBatch, skeleton);
        }
        
        drawSub(spriteBatch, delta);
    }
    
    public abstract void drawSub(SpriteBatch spriteBatch, float delta);

    public Skeleton getSkeleton() {
        return skeleton;
    }

    public AnimationState getAnimationState() {
        return animationState;
    }

    public SkeletonBounds getSkeletonBounds() {
        return skeletonBounds;
    }
}
