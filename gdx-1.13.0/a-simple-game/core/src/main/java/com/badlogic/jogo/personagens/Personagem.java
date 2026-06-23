package com.badlogic.jogo.personagens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public abstract class Personagem {
    protected float x, y;
    protected float velocidade;
    protected boolean ativo;
    protected Texture textura;
    protected Body body;
    protected World world;
    protected boolean noChao = false;
    protected boolean controlavel = false;
    
    // Constantes de movimento
    protected static final float VELOCIDADE_MOVIMENTO = 150f;
    protected static final float FORCA_PULO = 120f;
    protected static final float LARGURA_SPRITE = 96f;

    public Personagem(float x, float y, float velocidade, Texture textura) {
        this.x = x;
        this.y = y;
        this.velocidade = velocidade;
        this.textura = textura;
        this.ativo = false;
    }

    // lógica comum de movimento
    protected void atualizarMovimento(float dt) {
        if (!controlavel) return;
        float velX = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            velX = VELOCIDADE_MOVIMENTO;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            velX = -VELOCIDADE_MOVIMENTO;
        }
        
        body.setLinearVelocity(velX, body.getLinearVelocity().y);

        // Pulo
        if ((Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W) || 
            Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) && (y < 30 || noChao)) {
            body.setLinearVelocity(body.getLinearVelocity().x, FORCA_PULO);
            noChao = false;
        }

        // Atualiza posição
        x = body.getPosition().x - LARGURA_SPRITE / 2;
        y = body.getPosition().y - LARGURA_SPRITE / 2;

        // Limites da tela
        if (x < 0) {
            x = 0;
            body.setTransform(LARGURA_SPRITE / 2, body.getPosition().y, 0);
        }
        if (x + LARGURA_SPRITE > 640) {
            x = 640 - LARGURA_SPRITE;
            body.setTransform(640 - LARGURA_SPRITE / 2, body.getPosition().y, 0);
        }
    }
    
    
    public void setControlavel(boolean controlavel) {
        this.controlavel = controlavel;
    }
    
    public boolean isControlavel() {
        return controlavel;
    }

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

    public boolean caiuNoBuraco() {
        return y < -100;
    }

    public void dispose() {
        if (textura != null) {
            textura.dispose();
        }
    }

    public abstract void update(float dt);
    public abstract void render(SpriteBatch batch);
    public abstract void usarHabilidade();
    
}