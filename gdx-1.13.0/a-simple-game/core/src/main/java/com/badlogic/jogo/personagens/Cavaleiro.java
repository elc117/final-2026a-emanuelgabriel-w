package com.badlogic.jogo.personagens;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;


public class Cavaleiro extends Personagem {
    private Animation<TextureRegion> animacaoIdle;
    private Animation<TextureRegion> animacaoAgachado;
    private Texture texturaAgachado;
    private float tempoAnimacao = 0;
    private static final float OFFSET_X = 0f; 
    private static final float OFFSET_Y = 0f;
    private static final float OFFSET_Y_AGACHADO = 16f;
    private static final float OFFSET_X_AGACHADO = -12f;
    private boolean agachado = false;
    
    public Cavaleiro(float x, float y, Texture texturaSpritesheet, Texture texturaAgachado, World world) {
        super(x, y, 200, null);
        this.world = world;
        this.texturaAgachado = texturaAgachado;

        try {
        TextureRegion[][] tmp = TextureRegion.split(texturaSpritesheet, 128, 128);
        TextureRegion[] frames = tmp[0]; 
        
        animacaoIdle = new Animation<>(0.3f, frames);
        animacaoIdle.setPlayMode(Animation.PlayMode.LOOP);

        TextureRegion[][] tmpAgachado = TextureRegion.split(texturaAgachado, 128, 128);
        TextureRegion[] framesAgachado = tmpAgachado[0];
        
        animacaoAgachado = new Animation<>(0.1f, framesAgachado);
        animacaoAgachado.setPlayMode(Animation.PlayMode.NORMAL);

        criarCorpoBox2D(x, y);
        System.out.println("Cavaleiro criado com sucesso!");
    } catch (Exception e) {
        System.out.println("Erro ao criar Cavaleiro: " + e.getMessage());
        e.printStackTrace();
    }
    }
    
    private void criarCorpoBox2D(float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x + 64, y + 64);
        bodyDef.fixedRotation = true;

        body = world.createBody(bodyDef);
        body.setUserData("cavaleiro");
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
        if (agachado) {
            body.setLinearVelocity(0, body.getLinearVelocity().y);
        }
        if (!agachado){
            atualizarMovimento(dt);
        } else{
            x = body.getPosition().x - LARGURA_SPRITE / 2;
            y = body.getPosition().y - LARGURA_SPRITE / 2;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Animation<TextureRegion> animacao = agachado ? animacaoAgachado : animacaoIdle;
        TextureRegion frameAtual = animacao.getKeyFrame(tempoAnimacao, false);
        float offsetY = agachado ? OFFSET_Y_AGACHADO : OFFSET_Y;
        float offsetX = agachado ? OFFSET_X_AGACHADO : OFFSET_X;
        batch.draw(frameAtual, x + offsetX, y + offsetY);
    }

    @Override
    public void usarHabilidade() {
        agachado = !agachado;
        while (body.getFixtureList().size > 0) {
            body.destroyFixture(body.getFixtureList().get(0));
        }
            
        PolygonShape shape = new PolygonShape();
        
        if (agachado) {
            // Hitbox quando agachado
            shape.setAsBox(20, 17);
        } else {
            // Hitbox normal
            shape.setAsBox(15, 32);
        }
        
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0f;            
        fixtureDef.restitution = 0f;
        fixtureDef.filter.categoryBits = 0x0002;
        fixtureDef.filter.maskBits = 0x0001;
        
        body.createFixture(fixtureDef);
        shape.dispose();

        if (agachado) {
            PolygonShape topShape = new PolygonShape();
            topShape.setAsBox(20, 0, new com.badlogic.gdx.math.Vector2(0, 18), 0);  // No topo
            
            FixtureDef topFixtureDef = new FixtureDef();
            topFixtureDef.shape = topShape;
            topFixtureDef.filter.categoryBits = 0x0001;  // Plataforma
            topFixtureDef.filter.maskBits = 0x0002;      // Detecta personagens
            
            body.createFixture(topFixtureDef);
            topShape.dispose();
        }
    }
    @Override
    public void dispose() {
        super.dispose();
        if (texturaAgachado != null) {
            texturaAgachado.dispose();
        }
    }
}

