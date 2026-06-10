package com.badlogic.jogo.telas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.jogo.Jogo;
import com.badlogic.jogo.cenas.Hud;

public class TelaFase1 implements Screen{
    private Jogo game;
    // A camera define 'o que' vemos 
    private OrthographicCamera gamecamera;
    // O viewport define 'como' vemos (tamanho da tela) 
    private Viewport gameViewport;
    private Hud hud;

    // Ferramentas do TiledMap
    private TmxMapLoader maploader; // Carrega mapa
    private TiledMap map; // Guarda os dados
    private OrthogonalTiledMapRenderer renderer; // Pinta o mapa na tela


    public TelaFase1(Jogo game) {
        this.game = game;
        gamecamera = new OrthographicCamera();
        // FitViewport mantém a proporção da tela, adicionando barras pretas se necessário
        gameViewport = new FitViewport(Jogo.LARGURA, Jogo.ALTURA, gamecamera);
        hud = new Hud(game.batch);

        // carrega o mapa
        maploader = new TmxMapLoader();
        map = maploader.load("level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);
        // centraliza a camera no meio do mundo
        gamecamera.position.set(Jogo.LARGURA / 2f, Jogo.ALTURA / 2f, 0);
    }

    @Override
    public void show() {
        // Chamado quando essa tela se torna a tela atual 
    }

    // metodo para separar a logica (matematica/movimento) do desenho (render)
    public void update(float dt){
        gamecamera.update();
        renderer.setView(gamecamera);
    }

    @Override
    public void render(float delta) {
        update(delta);
        // Limpa a tela com a cor preta
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // desenha mapa
        renderer.render();

        // desenha o hud por cima
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Atualiza a area de visao se o usuário redimensionar a janela
        // 'true' re-centraliza a camera automaticamente 
        gameViewport.update(width, height, true);
        hud.stage.getViewport().update(width, height, true);
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
        // limpeza de memoria
        map.dispose();
        renderer.dispose();
        hud.stage.dispose();
    }

}