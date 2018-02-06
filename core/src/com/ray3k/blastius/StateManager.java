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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ObjectMap;

public class StateManager {
    private final ObjectMap<String, State> states;
    private State loadedState;
    private Core core;
    
    public StateManager(Core core) {
        this.core = core;
        states = new ObjectMap<String, State>();
    }
    
    public void addState(String name, State state) {
        states.put(name, state);
    }
    
    public void draw(SpriteBatch spriteBatch, float delta) {
        if (loadedState != null) {
            loadedState.draw(spriteBatch, delta);
        }
    }
    
    public void act(float delta) {
        if (loadedState != null) {
            loadedState.act(delta);
        }
    }
    
    public void dispose() {
        for (State state : states.values()) {
            state.dispose();
        }
    }
    
    public void loadState(String name) {
        unloadState();
        
        if (name != null) {
            loadedState = states.get(name);
            if (loadedState == null) {
                Gdx.app.error(StateManager.class.getName(), "State does not exist: " + name);
            }
            loadedState.start();
        }
    }
    
    public void unloadState() {
        if (loadedState != null) {
            loadedState.stop();
            loadedState = null;
        }
    }
    
    public void removeState(State state) {
        removeState(states.findKey(state, true));
    }
    
    public void removeState(String name) {
        states.remove(name);
    }
    
    public State getState(String name) {
        return states.get(name);
    }

    public State getLoadedState() {
        return loadedState;
    }
    
    public String getLoadedStateName() {
        return states.findKey(loadedState, false);
    }

    void resize(int width, int height) {
        if (loadedState != null) {
            loadedState.resize(width, height);
        }
    }
}
