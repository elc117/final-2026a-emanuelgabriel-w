package com.badlogic.drop;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

// A interface ApplicationListener obriga a classe a ter os métodos do ciclo de vida do jogo
public class Main implements ApplicationListener {
    
    // ==========================================
    // 1. DECLARAÇÃO DE VARIÁVEIS GLOBAIS
    // ==========================================
    int score = 0;
    float baseSpeed = 2f;

    // Recursos visuais (imagens carregadas na placa de vídeo)
    Texture backgroundTexture;
    Texture bucketTexture;
    Texture dropTexture;
    Texture splashTexture;

    // Recursos de áudio
    Sound dropSound; // Para efeitos sonoros curtos (fica na RAM)
    Music music;     // Para músicas de fundo (toca por streaming)

    // Ferramentas de desenho e câmera
    SpriteBatch spriteBatch;     // Agrupa os desenhos para enviar de uma vez para a placa de vídeo
    FitViewport viewport;        // Mantém a proporção do jogo (ex: 8x5) independente da tela
    ScreenViewport screenViewport; // Mapeia 1 unidade do jogo para 1 pixel real da tela

    // Objetos do jogo (posição, tamanho e imagem)
    Sprite bucketSprite;         // O balde desenhável
    Array<Sprite> dropSprites;   // Uma lista dinâmica de pingos desenháveis

    // Ferramentas matemáticas para controles e colisões
    Vector2 touchPos;            // Guarda as coordenadas X e Y do clique/toque
    Rectangle bucketRectangle;   // Caixa de colisão invisível do balde
    Rectangle dropRectangle;     // Caixa de colisão invisível da gota em análise

    // Variáveis de controle de estado (lógica do jogo)
    float dropTimer;             // Cronômetro para saber quando criar uma nova gota
    public boolean clickedSplash; // Controla se o jogador já passou da tela inicial
    public Preloader preloader;  // Interface personalizada para carregar coisas em segundo plano


    // ==========================================
    // 2. INICIALIZAÇÃO (Ciclo de Vida)
    // ==========================================
    @Override
    public void create() {
        // Carrega os arquivos da pasta 'assets' do projeto
        backgroundTexture = new Texture("background.png");
        bucketTexture = new Texture("bucket.png");
        dropTexture = new Texture("drop.png");
        splashTexture = new Texture("splash.png");
        
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        
        // Inicializa o gerenciador de desenhos
        spriteBatch = new SpriteBatch();
        
        // Configura o mundo virtual para ter 8 unidades de largura por 5 de altura
        viewport = new FitViewport(8, 5);
        screenViewport = new ScreenViewport();

        // Configura o balde
        bucketSprite = new Sprite(bucketTexture);
        bucketSprite.setSize(1, 1); // Define que o balde ocupa 1x1 unidades no nosso mundo de 8x5

        // Inicializa as ferramentas lógicas
        touchPos = new Vector2();
        dropSprites = new Array<>();
        bucketRectangle = new Rectangle();
        dropRectangle = new Rectangle();
    }

    // ==========================================
    // 3. REDIMENSIONAMENTO DE TELA (Ciclo de Vida)
    // ==========================================
    @Override
    public void resize(int width, int height) {
        // Se a janela mudar de tamanho, avisa as câmeras para recalcularem o espaço
        // O "true" faz com que a câmera centralize o jogo na tela
        viewport.update(width, height, true);
        screenViewport.update(width, height, true);
    }

    // ==========================================
    // 4. O GAME LOOP (Executado ~60 vezes por segundo)
    // ==========================================
    @Override
    public void render() {
        // Máquina de estados simples:
        if (!clickedSplash) {
            // Se ainda não clicou, desenha a tela inicial (splash screen)
            splashRender();
        } else {
            // Se já clicou, executa o fluxo normal do jogo:
            input(); // 1. Verifica teclado/mouse
            logic(); // 2. Atualiza posições e colisões
            draw();  // 3. Desenha na tela
        }
    }

    // ==========================================
    // 5. ENTRADAS DO JOGADOR (Controles)
    // ==========================================
    private void input() {
        float speed = 4f; // Velocidade de movimento do balde
        float delta = Gdx.graphics.getDeltaTime(); // Tempo passado desde o último frame

        // Move o balde para a direita se apertar a seta direita
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            bucketSprite.translateX(speed * delta);
        } 
        // Move o balde para a esquerda se apertar a seta esquerda
        else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            bucketSprite.translateX(-speed * delta);
        }

        // Se o jogador clicar com o mouse ou tocar na tela do celular:
        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY()); // Pega o X e Y em pixels reais
            viewport.unproject(touchPos); // Converte os pixels para as unidades matemáticas (8x5)
            bucketSprite.setCenterX(touchPos.x); // Move o centro do balde para onde o dedo/mouse está
        }
    }

    // ==========================================
    // 6. REGRAS E LÓGICA DO JOGO (Movimento e Colisão)
    // ==========================================
    private void logic() {
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float bucketWidth = bucketSprite.getWidth();
        float bucketHeight = bucketSprite.getHeight();

        // Impede que o balde saia das bordas da tela (restringe o X entre 0 e a largura do mundo)
        bucketSprite.setX(MathUtils.clamp(bucketSprite.getX(), 0, worldWidth - bucketWidth));

        float delta = Gdx.graphics.getDeltaTime();
        
        // Atualiza a posição da caixa de colisão do balde para bater exatamente com a imagem dele
        bucketRectangle.set(bucketSprite.getX(), bucketSprite.getY(), bucketWidth, bucketHeight);

        // Laço de repetição invertido para percorrer todas as gotas caindo
        // (É invertido para evitar erros ao remover um item de uma lista que está sendo percorrida)
        for (int i = dropSprites.size - 1; i >= 0; i--) {
            Sprite dropSprite = dropSprites.get(i);
            float dropWidth = dropSprite.getWidth();
            float dropHeight = dropSprite.getHeight();
            float currentSpeed = baseSpeed + (score) * 0.000001f;

            // Faz a gota cair (move para baixo no eixo Y a 2 unidades por segundo)
            dropSprite.translateY(-currentSpeed * delta);
            
            // Atualiza a caixa de colisão desta gota específica
            dropRectangle.set(dropSprite.getX(), dropSprite.getY(), dropWidth, dropHeight);

            // Verifica colisão: Se a gota passou do fundo da tela, ela se perdeu
            if (dropSprite.getY() < -dropHeight) {
                dropSprites.removeIndex(i); // Remove da lista
            } 
            // Se a caixa do balde sobrepor (overlaps) a caixa da gota: Pegou a gota!
            else if (bucketRectangle.overlaps(dropRectangle)) {
                dropSprites.removeIndex(i); // Remove da lista
                dropSound.play(); // Toca o sonzinho de plop
                score++;
            }
        }

        // Cronômetro de geração de gotas
        dropTimer += delta; // Adiciona o tempo que passou no cronômetro
        if (dropTimer > 1f) { // Se passou mais de 1 segundo
            dropTimer = 0;
            createDroplet(); // Zera o cronômetro
            for(int i=1; i<score; i++)
            createDroplet(); // Cria uma nova gota
        }
    }

    // ==========================================
    // 7. DESENHAR O JOGO (Renderização Principal)
    // ==========================================
    private void draw() {
        // Limpa a tela inteira pintando-a de preto para apagar o quadro anterior
        ScreenUtils.clear(Color.BLACK);
        
        // Aplica a câmera do jogo (mundo 8x5)
        viewport.apply();
        // Avisa ao SpriteBatch qual é a perspectiva da câmera
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        
        // Abre o "lote" de desenhos
        spriteBatch.begin();

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        // 1. Desenha o fundo esticado para caber no mundo
        spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);
        
        // 2. Desenha o balde
        bucketSprite.draw(spriteBatch);

        // 3. Desenha todas as gotas que estão na lista
        for (Sprite dropSprite : dropSprites) {
            dropSprite.draw(spriteBatch);
        }

        // Fecha o lote e envia tudo de uma vez para a placa de vídeo
        spriteBatch.end();
    }

    // ==========================================
    // 8. RENDERIZAÇÃO DA TELA INICIAL (Splash)
    // ==========================================
    private void splashRender() {
        // Se tocar na tela de inicio...
        if (Gdx.input.isTouched()) {
            clickedSplash = true; // Muda o estado para sair da tela inicial

            // Carrega a música de fundo usando nossa interface em segundo plano para não travar
            preloader.preloadBundle("delayed-loading", bundle -> {
                music = Gdx.audio.newMusic(Gdx.files.internal("delayed-loading/music.mp3"));
                music.setLooping(true); // Repete a música eternamente
                music.setVolume(.5f); // Coloca em 50% de volume
                music.play(); // Dá o play
            });
        }

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        ScreenUtils.clear(Color.BLACK);

        // Primeiro, desenha o fundo do jogo usando as medidas 8x5
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();
        spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);
        spriteBatch.flush(); // Força o envio desse primeiro lote

        // Depois, muda para a câmera baseada em pixels da tela
        screenViewport.apply();
        spriteBatch.setProjectionMatrix(screenViewport.getCamera().combined);
        
        // Desenha a imagem "splash" exatamente no centro da tela real (usando matemática de centralização)
        spriteBatch.draw(
            splashTexture, 
            MathUtils.round(screenViewport.getWorldWidth() / 2f - splashTexture.getWidth() / 2f), 
            MathUtils.round(screenViewport.getWorldHeight() / 2f - splashTexture.getHeight() / 2f)
        );

        spriteBatch.end();
    }

    // ==========================================
    // 9. FUNÇÃO AUXILIAR: CRIAR GOTA
    // ==========================================
    private void createDroplet() {
        float dropWidth = 1;
        float dropHeight = 1;
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        Sprite dropSprite = new Sprite(dropTexture);
        dropSprite.setSize(dropWidth, dropHeight); // Tamanho da gota no mundo (1x1)
        
        // Coloca a gota em um eixo X aleatório (entre 0 e a borda direita da tela)
        dropSprite.setX(MathUtils.random(0f, worldWidth - dropWidth));
        
        // Posiciona a gota no topo exato da tela (eixo Y)
        dropSprite.setY(worldHeight);
        
        // Adiciona essa nova gota na lista para começar a cair
        dropSprites.add(dropSprite);
    }

    // ==========================================
    // 10. MÉTODOS DO CICLO DE VIDA (Menos usados aqui)
    // ==========================================
    
    @Override
    public void pause() {
        // Chamado quando o jogo minimiza (ex: atende uma ligação no celular)
    }

    @Override
    public void resume() {
        // Chamado quando o jogo volta do minimizado
    }

    @Override
    public void dispose() {
        // AVISO IMPORTANTE:
        // Num jogo real, é obrigatório destruir os recursos aqui para limpar a memória RAM da placa de vídeo!
        // Faltou isso neste exemplo. O correto seria ter coisas como:
        // backgroundTexture.dispose();
        // bucketTexture.dispose();
        // dropSound.dispose();
        // spriteBatch.dispose();
    }
}