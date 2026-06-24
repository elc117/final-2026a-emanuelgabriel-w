package com.badlogic.jogo.personagens;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;


public class Mago extends Personagem {
    private Animation<TextureRegion> animacaoIdle;
    private float tempoAnimacao = 0;
    private static final float OFFSET_X = -21f; 
    private static final float OFFSET_Y = 0f;
    private boolean puloPendente = true;
    
    public Mago(float x, float y, Texture texturaSpritesheet, World world) {
        super(x, y, 200, null);
        this.world = world;
        
        TextureRegion[][] tmp = TextureRegion.split(texturaSpritesheet, 128, 128);
        TextureRegion[] frames = tmp[0]; 
        
        animacaoIdle = new Animation<>(0.25f, frames);
        animacaoIdle.setPlayMode(Animation.PlayMode.LOOP);

        criarCorpoBox2D(x, y);
    }
    
    private void criarCorpoBox2D(float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x + 64, y + 64);
        bodyDef.fixedRotation = true;

        body = world.createBody(bodyDef);
        body.setUserData("mago");
        body.setLinearDamping(0f);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(15, 32);

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
    protected void atualizarMovimento(float dt) {
        // Atualiza posição
        x = body.getPosition().x - LARGURA_SPRITE / 2;
        y = body.getPosition().y - LARGURA_SPRITE / 2;

        if (!controlavel) return;
        
        float velX = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            velX = VELOCIDADE_MOVIMENTO;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            velX = -VELOCIDADE_MOVIMENTO;
        }
        
        body.setLinearVelocity(velX, body.getLinearVelocity().y);

        // Pulo duplo: verifica antes do pulo normal
        if ((Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W) || 
            Gdx.input.isKeyJustPressed(Input.Keys.SPACE))) {
            if (noChao) {
                // Primeiro pulo
                body.setLinearVelocity(body.getLinearVelocity().x, FORCA_PULO);
                noChao = false;
            } else if (puloPendente) {
                // Segundo pulo
                body.setLinearVelocity(body.getLinearVelocity().x, 140f);
                puloPendente = false;
            }
        }
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
        batch.draw(frameAtual, x + OFFSET_X, y + OFFSET_Y);
    }

    @Override
    public void usarHabilidade() {
    }

    public void resetarPuloPendente() {
        puloPendente = true;
    }
}
