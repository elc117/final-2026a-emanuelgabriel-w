package com.badlogic.jogo.telas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.jogo.Jogo;
import com.badlogic.jogo.cenas.Hud;

public class TelaFase1 implements Screen{
    private Jogo game;
    private Texture texture;
    // A camera define 'o que' vemos 
    private OrthographicCamera gamecamera;
    // O viewport define 'como' vemos (tamanho da tela) 
    private Viewport gameviewport;
    private Hud hud;

    public TelaFase1(Jogo game) {
        this.game = game;
        texture = new Texture("background.png");
        gamecamera = new OrthographicCamera();
        // FitViewport mantém a proporção da tela, adicionando barras pretas se necessário
        gameviewport = new FitViewport(Jogo.LARGURA, Jogo.ALTURA, gamecamera);
        hud = new Hud(game.batch);
    }
    
    @Override
    public void show() {
        // Chamado quando essa tela se torna a tela atual 
    }

    @Override
    public void render(float delta) {
        // Limpa a tela com a cor preta
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        gamecamera.update();
        // Sincroniza o SpriteBAtch com a visao da camera antes de desenhar 
        game.batch.setProjectionMatrix(gamecamera.combined);
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
        game.batch.begin();
        // Desenha a textura na posição inicial (x=0, y=0)
        game.batch.draw(texture, 0, 0);
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        // Atualiza a area de visao se o usuário redimensionar a janela
        // 'true' re-centraliza a camera automaticamente 
        gameviewport.update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        // chamado quando o jogo muda para outra tela 
    }

    @Override
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }

}