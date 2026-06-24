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

![print da fase](/midias/fase1-10_06.png)

### Dia 12/06
**Gabriel**: Adicionei um dos personagens usando um sprite do site [craftpix.net](https://craftpix.net/freebies/free-warrior-pixel-art-sprite-sheets/), implementei uma animação quando ele está idle depois de muitas tentativas, também implementei uma movimentação básica para poder testar.

### Dia 13/06
**Gabriel**: Troquei a movimentação básica por uma melhor com física utilizando a biblioteca Box2D, adicionei colisão no chão, a parte mais trabalhosa foi fazer funcionar a detecção do personagem com o chão pra fazer ele pular.

**Emanuel**: Adicionei o menu inicial do jogo, ele funciona de maneira semelhante ao `Hud.java`. Foram adicionado os botões de "Jogar" e "Sair" estilizados pelo arquivo `uiskin.json`. O sistema de botões usa reatividade para atualizar a tela quando um botão for clicado, ela funciona por conta do `setInputProcessor(stage)`.
```java
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new TelaFase1(game));
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
        ...
        Gdx.input.setInputProcessor(stage);
```
![print do menu](/midias/telaMenu-20_06.png)

### Dia 14/06
**Gabriel**: Adicionei o projeto na jam criada pela professora no itch.io: https://gabuz52.itch.io/advancetogether

Decidimos que o nome do jogo será Advance Together.

### Dia 17/06
**Emanuel**: Implementei a física sobre os objetos do mapa, sendo essa, provavelmente, a parte mais complexa do projeto.
Criei os métodos `criarColisoesDoMapa` e `criarEspinhosDoMapa`. Eles leem as camadas que eu criei no Tiled (ground e spikes) e instanciam a física usando o Box2D.
* **BodyDef**: Define onde o objeto nasce e o seu tipo. Em ambos os casos, está como `StaticBody`, o que significa que ele não é afetado pela gravidade e nem se mexe.
* **Shape**: Pega a forma geométrica do mapa, usa `setAsBox` para o chão (que é um retângulo) e `getTransformedVertices` para os espinhos, pois sua colisão é poligonal.
* **Fixture**: É o que conecta a forma ao corpo do objeto e dá as suas propriedades físicas. No chão, é aplicado o atrito (`fdef.friction = 0.4f`). Nos espinhos, é aplicado `fdef.isSensor = true`, o que não os deixa "sólidos" quando o personagem toca neles; apenas identifica o contato, o que pode ser útil quando formos implementar o dano.  
O que mais me deixou confuso foram os cálculos matemáticos envolvendo os retângulos do chão. Então, enviei os códigos para o Gemini e perguntei por que é necessário aplicar essa lógica ao chão e não aos espinhos.
```java
    bdef.position.set(rect.x + rect.width / 2, rect.y + rect.height / 2);

    ...

    PolygonShape shape = new PolygonShape();
    shape.setAsBox(rect.width / 2, rect.height / 2);

```
Então, eu entendi que o Tiled salva a coordenada do retângulo no canto inferior esquerdo do bloco, mas, como o Box2D precisa fazer os cálculos matemáticos para o jogo, a posição do corpo não pode ser nas quinas; ele precisa do centro do bloco. É por isso que é passada a metade da largura e a metade da altura do bloco. O método `setAsBox` usa esses valores divididos para construir a colisão, expandindo-a de dentro para fora.
Já os espinhos usam a função `getTransformedVertices()`, esse método já dá as coordenadas de cada ponta do triângulo da forma que elas já estão no mundo, por isso não é necessário essa matemática.
![print da fase atualizada](/midias/fase1-20_06.png)

### Dia 20/06
**Emanuel**: Criei a tela de Game Over do jogo. Ela funciona da mesma maneira que a tela de menu, mas é acionada no código, por enquanto, apenas quando o jogador cai no buraco. 
```java
    if (cavaleiro.caiuNoBuraco()){
        game.setScreen(new TelaGameOver(game));
    }
```  
Adicionei a lógica de cair no buraco na classe do personagem, que é acionada quando ele ultrapassa os limites da tela do mapa.
```java
    public boolean caiuNoBuraco(){
        return y < -100;
    }
```
![print da tela de Game Over](/midias/telaGameOver-20_06.png)  
Também removi o atrito, pois percebi que, dessa maneira, era possível "grudar" o personagem na parede.

### Dia 22/06
**Gabriel**: Adicionei os outros 2 personagens que faltavam, eles estavam colidindo e deslizando entre si, então configurei um filtro pra remover essa colisão, ainda tem alguns problemas de hitbox desalinhada e atualização da textura com a hitbox que vou arrumar amanhã, também programei pro jogador poder mudar o personagem que está utilizando apertando a tecla C.

### Dia 23/06
**Gabriel**: Mudei o sprite do cavaleiro pra combinar mais com os outro 2, adicionei a habilidade de fazer o cavaleiro agachar apertando "E", nesse estado os outros personagem podem usar ele de plataforma, também deixei ele imune aos espinhos porque sua armadura o protege. Adicionei a habilidade de pulo duplo para a maga, essa foi bem tranquila de adicionar.

## Como rodar no VScode
cd gdx-1.13.0/a-simple-game  
./gradlew html:dist  
cd html/build/dist  
python -m http.server  

## Fontes

https://github.com/elc117/gamification-2025b-caua-spamton-g-spamton  
https://youtube.com/playlist?list=PLZm85UZQLd2SXQzsF-a0-pPF6IWDDdrXt&si=IkKlzOw2SrlfgOQy  
https://libgdx.com/wiki/graphics/2d/orthographic-camera  
Repositório da Disciplina  

Fundo da fase 1: https://craftpix.net/freebies/free-mountain-backgrounds-pixel-art/  
Tiles das fases: https://www.spriters-resource.com/game_boy_advance/sma/asset/51433/  
Tiled Map Editor: https://www.mapeditor.org/
