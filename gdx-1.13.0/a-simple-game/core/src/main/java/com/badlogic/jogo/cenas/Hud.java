package com.badlogic.jogo.cenas;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.jogo.Jogo;

public class Hud {
    public Stage stage;  
    private Viewport viewport;

    Label faseLabel;

    public Hud(SpriteBatch sb){
        viewport = new FitViewport(Jogo.LARGURA, Jogo.ALTURA, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        faseLabel = new Label("Fase 1", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        table.add(faseLabel).expandX().padTop(10);

        stage.addActor(table);
    }
}