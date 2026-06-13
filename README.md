# Identificação:

### Emanuel Alves de Cristo e Gabriel Weirich

### *Curso: Sistemas de Informação*

---

# Proposta

Nossa proposta é desenvolver um jogo de plataforma no qual o jogador controla um grupo de aventureiros, onde cada um possui atributos e habilidades diferentes, com o objetivo de avançar pelas fases.

# Processo de desenvolvimento:

### Dia 28/05

**Emanuel**: Baixei o exemplo `java-libgdx-extended-drop-example Public` que a professora disponibilizou nos slides sobre libGDX, estudei o código, pedi para a IA Gemini me ajudar com os elementos novos e, como forma de estudo, pedi comentários em cada linha para poder entender o funcionamento do jogo. Como exercício, modifiquei o jogo para que a velocidade aumentasse a cada gota pega e, de brincadeira, também multipliquei a quantidade de gotas que caíam a cada gota coletada.

### Dia 04/06

**Emanuel**: Clonamos um projeto existente e o estamos usando como base para o novo jogo. Será usado o `a-simple-game` como referência. Lendo os slides sobre libGDX do repositório da disciplina e usando o Copilot no VS Code, perguntei quais pastas e arquivos deveria modificar para começar um projeto novo. Apaguei diretórios desnecessários e acredito que agora o projeto esteja pronto para receber modificações.

### Dia 06/06

**Emanuel**: Editei os arquivos e criei uma hierarquia de pastas. Adicionei o "molde" básico do sistema. Olhei o repositório `gamification-2025b-caua-spamton-g-spamton` que a professora recomendou e encontrei uma playlist de vídeos ensinando como fazer um clone de Super Mario Bros no libGDX, que pensei em usar como referência.

### Dia 07/06

**Emanuel**: Utilizei o Copilot para me ajudar com os imports e erros de compilação do código, depois de corrigido, fui configurar a câmera, *aspect ratios* e *viewports* do projeto.

```java
    public TelaFase1(Jogo game) {
        this.game = game;
        texture = new Texture("background.png");
        gamecamera = new OrthographicCamera();
        gameviewport = new FitViewport(800, 480, gamecamera);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        gamecamera.update();
        game.batch.setProjectionMatrix(gamecamera.combined);
        game.batch.begin();
        game.batch.draw(texture, 0, 0);
        game.batch.end();
    }
```
Esse código executa os seguintes passos:
* Define a câmera do jogo como `OrthographicCamera`. Ela converte as coordenadas do mundo do jogo para a tela do dispositivo sem aplicar perspectiva (objetos mantêm o mesmo tamanho independente da distância), usando o conceito de unidades de mundo em vez de pixels puros.

* Prepara `gameviewport` como `FitViewport`. Trava o tamanho do 'mundo virtual' em 800x480. Controlando a câmera, impedindo que a tela fique esticada, adicionando barras pretas se a janela for redimensionada.

* O Render limpa a tela de preto. Chama `gamecamera.update()` para recalcular a tela caso tenha mudado de tamanho. Depois sincroniza o `spriteBatch` com a visão da câmera, no fim desenha a textura "background" na posição (0,0) (inicio do mundo).

### Dia 08/06 - 10/06

**Emanuel**: Criei a HUD do jogo. A `Table` permite a organização e o posicionamento dos atores de maneira simples no palco (`Stage`).  
Criei o mapa inicial do jogo usando o Tiled Map Editor, peguei um *background* online no site [craftpix.net](https://craftpix.net/freebies/free-mountain-backgrounds-pixel-art/) e os *tiles* do jogo Super Mario Advance (GBA) no site [spriters-resource.com](https://www.spriters-resource.com/game_boy_advance/sma/asset/51433/).

Tive que alterar o tamanho da tela do jogo para se adequar ao mapa de 640x400, pois acredito que esse tamanho seja o mais adequado quando o jogo for exportado para o itch.io.

```java
        private TmxMapLoader maploader;
        private TiledMap map;
        private OrthogonalTiledMapRenderer renderer;


        maploader = new TmxMapLoader();
        map = maploader.load("level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);
        gamecamera.position.set(Jogo.LARGURA / 2f, Jogo.ALTURA / 2f, 0);
```

Porém, na hora de renderizar o mapa para a web, aconteceu um erro: a tela ficava totalmente escura.  
Resolvi jogar o erro do terminal no Claude.ai, e ali descobri que o formato de mapa que eu estava usando (compressão *zlib*) era incompatível com a renderização em HTML. Então, a IA me recomendou utilizar Base64 (sem compressão). Depois da troca, o mapa começou a funcionar normalmente.

### Dia 12/06
**Gabriel**: Adicionei um dos personagens usando um sprite do site [craftpix.net](https://craftpix.net/freebies/free-warrior-pixel-art-sprite-sheets/), implementei uma animação quando ele está idle depois de muitas tentativas, também implementei uma movimentação básica para poder testar.

![print da fase](/midias/fase1-10_06.png)

## Fontes

https://github.com/elc117/gamification-2025b-caua-spamton-g-spamton  
https://youtube.com/playlist?list=PLZm85UZQLd2SXQzsF-a0-pPF6IWDDdrXt&si=IkKlzOw2SrlfgOQy  
https://libgdx.com/wiki/graphics/2d/orthographic-camera  
Repositório da Disciplina  

Fundo da fase 1: https://craftpix.net/freebies/free-mountain-backgrounds-pixel-art/  
Tiles das fases: https://www.spriters-resource.com/game_boy_advance/sma/asset/51433/  
Tiled Map Editor: https://www.mapeditor.org/