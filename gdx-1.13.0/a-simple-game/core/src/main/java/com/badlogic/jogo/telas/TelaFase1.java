package com.badlogic.jogo.telas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.jogo.Jogo;
import com.badlogic.jogo.cenas.Hud;
import com.badlogic.jogo.personagens.Arqueiro;
import com.badlogic.jogo.personagens.Cavaleiro;
import com.badlogic.jogo.personagens.Mago;
import com.badlogic.jogo.personagens.Personagem;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;



public class TelaFase1 implements Screen{
    private Jogo game;
    // A camera define 'o que' vemos 
    private OrthographicCamera gamecamera;
    // O viewport define 'como' vemos (tamanho da tela) 
    private Viewport gameViewport;
    private Hud hud;
    // personagens
    private Texture texturaCavaleiro;
    private Texture texturaAgachado;    
    private Texture texturaArqueiro;
    private Texture texturaMago;
    private Array<Personagem> personagens;
    private int indiceAtivo = 0;
    // Ferramentas do TiledMap
    private TmxMapLoader maploader; // Carrega mapa
    private TiledMap map; // Guarda os dados
    private OrthogonalTiledMapRenderer renderer; // Pinta o mapa na tela
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private boolean passouDeFase = false;

    // metodo para ler a camada "ground" do level1, transformando cada retangulo desenhado
    // no Tiled em um corpo fisico
    private void criarColisoesDoMapa(){
        // pega os objetos desenhados da camada "ground"
        com.badlogic.gdx.maps.MapObjects objetos = map.getLayers().get("ground").getObjects();

        // percorre cada retangulo de colisao desenhado
        for (com.badlogic.gdx.maps.objects.RectangleMapObject obj :
                objetos.getByType(com.badlogic.gdx.maps.objects.RectangleMapObject.class)) {
                    
            // pega as coordenadas e o tamanho do retangulo em pixels.
            com.badlogic.gdx.math.Rectangle rect = obj.getRectangle();
            
            // define que o corpo é estatico
            BodyDef bdef = new BodyDef();
            bdef.type = BodyDef.BodyType.StaticBody;

            // Box2d posiciona o corpo pelo centro, entao soma metade da largura/altura 
            // para achar o centro do retangulo
            bdef.position.set(rect.x + rect.width / 2, rect.y + rect.height / 2);
            
            // cria o corpo dentro do mundo
            Body body = world.createBody(bdef);
            // marca o corpo como "chao"
            body.setUserData("chao");
            
            // define o formato do corpo como um retangulo
            // usa metade da largura/altura porque setAsBox espara "raio" nao o total
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(rect.width / 2, rect.height / 2);

            // a fixture conecta o formato (shape) ao corpo (body)
            FixtureDef fdef = new FixtureDef();
            fdef.shape = shape;
            fdef.friction = 0.4f;

            body.createFixture(fdef);
            shape.dispose();
        }
    }

    private void criarEspinhosDoMapa() {
        com.badlogic.gdx.maps.MapObjects objetos = map.getLayers().get("spikes").getObjects();

        // percorre os espinhos que são poligonais
        for (com.badlogic.gdx.maps.objects.PolygonMapObject obj : 
            objetos.getByType(com.badlogic.gdx.maps.objects.PolygonMapObject.class)) {

                com.badlogic.gdx.math.Polygon polygon = obj.getPolygon();

                BodyDef bdef = new BodyDef();
                bdef.type = BodyDef.BodyType.StaticBody;
                // a posiçao do retangulo no Tiled já vem com x,y absolutos
                bdef.position.set(0, 0);

                Body body = world.createBody(bdef);
                // marca esse corpo como "spike" - serva depois para dectar se o personagem tocou em algo que
                // causa dano
                body.setUserData("spike");

                PolygonShape shape = new PolygonShape();
                // getTransformedVertices() retorna os vértices já ajustados
                float[] verts = polygon.getTransformedVertices();
                shape.set(verts);

                FixtureDef fdef = new FixtureDef();
                fdef.shape = shape;
                // nao empurra o personagem, apenas detecta quando algo o toca
                fdef.isSensor = true;

                body.createFixture(fdef);
                shape.dispose();
            }
    }

    private void criarPortaDoMapa() {
        MapObjects objetos = map.getLayers().get("door").getObjects();

        for (com.badlogic.gdx.maps.objects.RectangleMapObject obj :
             objetos.getByType(com.badlogic.gdx.maps.objects.RectangleMapObject.class)) {

                com.badlogic.gdx.math.Rectangle rect = obj.getRectangle();

                BodyDef bdef = new BodyDef();
                bdef.type = BodyDef.BodyType.StaticBody;
                bdef.position.set(rect.x + rect.width / 2, rect.y + rect.height / 2);
                
                Body body = world.createBody(bdef);
                body.setUserData("porta");

                PolygonShape shape = new PolygonShape();
                shape.setAsBox(rect.width / 2, rect.height / 2);

                FixtureDef fdef = new FixtureDef();
                fdef.shape = shape;
                fdef.isSensor = true;

                body.createFixture(fdef);
                shape.dispose();
        }
    }

    public TelaFase1(Jogo game) {
        this.game = game;
        gamecamera = new OrthographicCamera();
        world = new World(new Vector2(0, -200f), true);
        debugRenderer = new Box2DDebugRenderer();
         
        // Cria a array com os 3 personagens
        personagens = new Array<>();
        
        texturaCavaleiro = new Texture("cavaleiro.png");
        texturaAgachado = new Texture("agachado.png");
        personagens.add(new Cavaleiro(100, 40, texturaCavaleiro, texturaAgachado, world));
        
        texturaArqueiro = new Texture("arqueiro.png");
        personagens.add(new Arqueiro(70, 40, texturaArqueiro, world));
        
        texturaMago = new Texture("mago.png");
        personagens.add(new Mago(40, 40, texturaMago, world));

        // configura o contato entre os personagens e o chão
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Body bodyA = contact.getFixtureA().getBody();
                Body bodyB = contact.getFixtureB().getBody();
                
                // Verifica qual personagem tocou em algo
                for (Personagem p : personagens) {
                    if (p.getBody() == bodyA || p.getBody() == bodyB) {
                        Object userDataOutro = (p.getBody() == bodyA) ? 
                            bodyB.getUserData() : bodyA.getUserData();
                        
                        if ("chao".equals(userDataOutro)) {
                            p.noChao();
                        }
                        if ("cavaleiro".equals(userDataOutro)) {
                            p.noChao();
                        }
                        if ("spike".equals(userDataOutro)) {
                            // Cavaleiro é imune aos espinhos
                            if (!(p instanceof Cavaleiro)) {
                                game.setScreen(new TelaGameOver(game));
                            }
                        }
                    }
                }
            }
            
            @Override
            public void endContact(Contact contact) {
                Body bodyA = contact.getFixtureA().getBody();
                Body bodyB = contact.getFixtureB().getBody();
                Object userDataA = bodyA.getUserData();
                Object userDataB = bodyB.getUserData();
                
                // Verifica qual personagem saiu do contato
                for (Personagem p : personagens) {
                    if (p.getBody() == bodyA || p.getBody() == bodyB) {
                        Object userDataOutro = (p.getBody() == bodyA) ? 
                            bodyB.getUserData() : bodyA.getUserData();
                        
                        if ("chao".equals(userDataOutro)) {
                            p.deixouChao();
                        }
                    }
                }
                
                // Detecta porta (só o ativo passa de fase)
                Personagem ativo = personagens.get(indiceAtivo);
                if ((ativo.getBody() == bodyA && "porta".equals(userDataB)) ||
                    (ativo.getBody() == bodyB && "porta".equals(userDataA))) {
                    passouDeFase = true;
                }
            }
        
            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {}
            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {}
        });
        personagens.get(0).setControlavel(true);
        
        // FitViewport mantém a proporção da tela, adicionando barras pretas se necessário
        gameViewport = new FitViewport(Jogo.LARGURA, Jogo.ALTURA, gamecamera);
        hud = new Hud(game.batch);

        // carrega o mapa
        maploader = new TmxMapLoader();
        map = maploader.load("level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);

        criarColisoesDoMapa();
        criarEspinhosDoMapa();
        criarPortaDoMapa();
        // centraliza a camera no meio do mundo
        gamecamera.position.set(Jogo.LARGURA / 2f, Jogo.ALTURA / 2f, 0);
    }

    @Override
    public void show() {
        // Chamado quando essa tela se torna a tela atual 
    }

    // metodo para separar a logica (matematica/movimento) do desenho (render)
    public void update(float dt){
        world.step(dt, 8, 3);
        gamecamera.update();
        renderer.setView(gamecamera);
        
        for (Personagem p : personagens) {
            p.update(dt);
        }

        // Verifica se quer trocar de personagem (C)
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
        // Desativa o anterior
        personagens.get(indiceAtivo).setControlavel(false);
        
        // Muda para o próximo
        indiceAtivo = (indiceAtivo + 1) % personagens.size;
        
        // Ativa o novo
        personagens.get(indiceAtivo).setControlavel(true);
        System.out.println("Personagem ativo: " + indiceAtivo);
    }

        Personagem ativo = personagens.get(indiceAtivo);
        if (ativo.caiuNoBuraco()){
            game.setScreen(new TelaGameOver(game));
        }
        if (passouDeFase) {
            game.setScreen(new TelaVitoria(game));
        }
        if (ativo instanceof Cavaleiro) {
            Cavaleiro cav = (Cavaleiro) ativo;
            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                cav.usarHabilidade();
            }
        }
    }

    @Override
    public void render(float delta) {
        update(delta);
        // Limpa a tela com a cor preta
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // desenha mapa
        renderer.render();

        game.batch.setProjectionMatrix(gamecamera.combined);
        game.batch.begin();
        // desenha os personagens
        for (Personagem p : personagens) {
            p.render(game.batch);
        }
        game.batch.end();

        debugRenderer.render(world, gamecamera.combined);
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
        texturaCavaleiro.dispose();
        texturaAgachado.dispose();
        texturaArqueiro.dispose();
        texturaMago.dispose();
        world.dispose(); 
        for (Personagem p : personagens) {
            p.dispose();
        }
        debugRenderer.dispose();
    }

}