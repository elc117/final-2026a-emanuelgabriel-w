package com.badlogic.jogo;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Jogo extends Game {
    public SpriteBatch batch;

    @override
    public void create() {
        batch = new SpriteBatch();

        this.setScreen(new MainMenuScreen(this));
    }

    @override
    public void render() {
        super.render();
    }

    @override
    public void dispose() {
        batch.dispose();
    }

}