package com.badlogic.jogo;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.jogo.telas.TelaFase1;
import com.badlogic.jogo.cenas.Hud;

// Herda de 'Game' (libGDX) para permitir multiplas telas
public class Jogo extends Game {
    public static final int LARGURA = 800;
    public static final int ALTURA = 480; 
    //enva imagens para a placa de video
    //compartilha o mesmo batch entre as telas para economizar memoria
    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        //define TelaFase1 como tela inicial do jogo
        this.setScreen(new TelaFase1(this));
    }

    @Override
    public void render() {
        //Diz ao jogo para executar o render() da tela atual
        super.render();
    }

    @Override
    public void dispose() {
        //Libera memoria da placa de video quando o jogo fecha
        batch.dispose();
    }

}