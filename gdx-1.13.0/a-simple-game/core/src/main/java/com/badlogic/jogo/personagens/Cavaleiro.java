package com.badlogic.jogo.personagens;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;


public class Cavaleiro extends Personagem {
    private Animation<TextureRegion> animacaoIdle;
    private float tempoAnimacao = 0;
    
    public Cavaleiro(float x, float y, Texture texturaSpritesheet) {
        super(x, y, 200, null);
        
        TextureRegion[][] tmp = TextureRegion.split(texturaSpritesheet, 96, 96);
        TextureRegion[] frames = tmp[0]; // pega a primeira (e única) linha
        
        animacaoIdle = new Animation<>(0.15f, frames);
        animacaoIdle.setPlayMode(Animation.PlayMode.LOOP);
    }
    
    public void update(float dt) {
    tempoAnimacao += dt;
    // pega input do teclado
    if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
        x += velocidade * dt; 
    }
    if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
        x -= velocidade * dt; 
    }
    if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
        y += velocidade * dt; 
    }
    if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
        y -= velocidade * dt; 
    }
    //não deixa o cavaleiro sair da tela
    if (x < 0) x = 0;
    if (x + 96 > 640) x = 640 - 96;
    if (y < 0) y = 0;
    if (y + 96 > 400) y = 400 - 96;
}
    
    @Override
    public void render(SpriteBatch batch) {
        TextureRegion frameAtual = animacaoIdle.getKeyFrame(tempoAnimacao);
        batch.draw(frameAtual, x, y);
    }
    
    @Override
    public void usarHabilidade() {
 
    }
}
