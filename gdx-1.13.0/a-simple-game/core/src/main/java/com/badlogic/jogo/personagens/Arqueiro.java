package com.badlogic.jogo.personagens;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;


public class Arqueiro extends Personagem {
    private Animation<TextureRegion> animacaoIdle;
    private float tempoAnimacao = 0;
    
    public Arqueiro(float x, float y, Texture texturaSpritesheet, World world) {
        super(x, y, 200, null);
        this.world = world;
        
        TextureRegion[][] tmp = TextureRegion.split(texturaSpritesheet, 128, 128);
        TextureRegion[] frames = tmp[0]; 
        
        animacaoIdle = new Animation<>(0.15f, frames);
        animacaoIdle.setPlayMode(Animation.PlayMode.LOOP);

        criarCorpoBox2D(x, y);
    }
    
    private void criarCorpoBox2D(float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x + LARGURA_SPRITE / 2, y + LARGURA_SPRITE / 2);
        bodyDef.fixedRotation = true;

        body = world.createBody(bodyDef);
        body.setUserData("arqueiro");
        body.setLinearDamping(0f);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(15, 47);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;

        fixtureDef.filter.categoryBits = 0x0002;  // Personagens
        fixtureDef.filter.maskBits = 0x0001;      // Colidem com ambiente

        body.createFixture(fixtureDef);
        shape.dispose();

    }

    @Override
    public void update(float dt) {
        tempoAnimacao += dt;
        atualizarMovimento(dt);
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
