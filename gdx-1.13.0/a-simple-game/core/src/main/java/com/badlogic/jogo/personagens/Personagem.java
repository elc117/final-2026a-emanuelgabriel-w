package com.badlogic.jogo.personagens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Personagem {
    protected float x, y;
    protected float velocidade;
    protected boolean ativo;
    protected Texture textura;

    public Personagem(float x, float y, float velocidade, Texture textura) {
        this.x = x;
        this.y = y;
        this.velocidade = velocidade;
        this.textura = textura;
        this.ativo = false;
    }

    public void render(SpriteBatch batch) {
        batch.draw(textura, x, y);
    }

    public void setAtivo(boolean estado){
        this.ativo = estado;
    }

    public abstract void usarHabilidade();
}
