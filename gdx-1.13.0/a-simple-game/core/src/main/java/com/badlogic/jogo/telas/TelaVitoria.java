package com.badlogic.jogo.telas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.jogo.Jogo;
import com.badlogic.jogo.telas.TelaFase1;

public class TelaVitoria implements Screen {
    private Jogo game;
    private Stage stage;
    // carrega os visuais do arquivo uiskin.json
    private Skin skin;
    private Texture backgroundMenuTexture;

    public TelaVitoria(Jogo game) {
        this.game = game;
        stage = new Stage(new FitViewport(Jogo.LARGURA, Jogo.ALTURA));
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        backgroundMenuTexture = new Texture(Gdx.files.internal("background_menu.gif"));

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        Label tituloLabel = new Label("Parabens! Finalizou a fase", skin);
        TextButton playButton = new TextButton("Jogar", skin);
        TextButton exitButton = new TextButton("Sair", skin);

        // adiciona um "escutador" ao botao para detectar cliques
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // troca para a fase 1
                game.setScreen(new TelaFase1(game));
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
        
        table.add(tituloLabel).padBottom(20).center();
        table.row();
        table.add(playButton).padBottom(20).width(200).height(50);
        table.row();
        table.add(exitButton).width(200).height(50);
        // fiz que 'stage' vai receber os cliques
        Gdx.input.setInputProcessor(stage);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(stage.getCamera().combined);
        game.batch.begin();
        game.batch.draw(backgroundMenuTexture, 0, 0, Jogo.LARGURA, Jogo.ALTURA);
        game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose(){
        stage.dispose();
        skin.dispose();
        backgroundMenuTexture.dispose();
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}