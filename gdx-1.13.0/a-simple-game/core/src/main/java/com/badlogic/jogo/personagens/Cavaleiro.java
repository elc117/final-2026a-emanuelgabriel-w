package com.badlogic.jogo.personagens;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;


public class Cavaleiro extends Personagem {
    private Animation<TextureRegion> animacaoIdle;
    private float tempoAnimacao = 0;
    private static final float VELOCIDADE_MOVIMENTO = 150f;
    private static final float FORCA_PULO = 120f;
    private static final float LARGURA_SPRITE = 96f;
    
    public Cavaleiro(float x, float y, Texture texturaSpritesheet, World world) {
        super(x, y, 200, null);
        this.world = world;
        
        TextureRegion[][] tmp = TextureRegion.split(texturaSpritesheet, 96, 96);
        TextureRegion[] frames = tmp[0]; // pega a primeira (e única) linha
        
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
        body.setUserData("cavaleiro");
        body.setLinearDamping(0f);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(15, 47);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    @Override
    public void update(float dt) {
        tempoAnimacao += dt;
        
        // Movimento horizontal
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

        // Atualiza posição visual baseada no body
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

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion frameAtual = animacaoIdle.getKeyFrame(tempoAnimacao);
        batch.draw(frameAtual, x, y);
    }

    @Override
    public void usarHabilidade() {
    }
}

