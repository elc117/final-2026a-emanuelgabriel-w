public class MainMenu implements Screen {
    private Jogo game;
    private Stage stage;
    private Skin skin;
    private Texture backgroundMenuTexture;

    public MainMenu(Jogo game) {
        this.game = game;
        stage = new Stage(new FitViewport(Jogo.LARGURA, Jogo.ALTURA));
        skin = new Skin(Gdx.files.internal(""));
        texture backgroundMenuTexture = new Texture(Gdx.files.internal("background_menu.png"));

        Table table = new Table();
        Table.setFillParent(true);
        stage.addActor(table);

        TextButton playButton = new TextButton("Jogar", skin);
        TextButton exitButton = new TextButton("Sair", skin);

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

        table.add(playButton).padBottom(20).width(200).height(50);
        table.row();
        table.add(exitButton).width(200).height(50);

    }
}