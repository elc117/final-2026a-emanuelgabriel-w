package com.badlogic.jogo.personagens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;

public abstract class Personagem {
    protected float x, y;
    protected float velocidade;
    protected boolean ativo;
    protected Texture textura;
    protected Body body;
    protected World world;
    protected boolean noChao = false;

    public Personagem(float x, float y, float velocidade, Texture textura) {
        this.x = x;
        this.y = y;
        this.velocidade = velocidade;
        this.textura = textura;
        this.ativo = false;
    }
    public abstract void update(float dt);
    
    public abstract void render(SpriteBatch batch);
    
    public abstract void usarHabilidade();

    public void setAtivo(boolean estado) {
        this.ativo = estado;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public void noChao() {
        noChao = true;
    }

    public void deixouChao() {
        noChao = false;
    }

    public boolean estaNochao() {
        return noChao;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public boolean caiuNoBuraco(){
        return y < -100;
    }

    public void dispose() {
        if (textura != null) {
            textura.dispose();
        }
    }
}