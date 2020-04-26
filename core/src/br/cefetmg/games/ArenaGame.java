package br.cefetmg.games;

import br.cefetmg.games.characters.FemaleHero;
import br.cefetmg.games.graphics.SeamlessOrthogonalTiledMapRenderer;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ArenaGame extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture adventurerMale, adventurerFemale;
    private FemaleHero hero;
    private TiledMap map;
    private TiledMapRenderer tiledMapRenderer;
    private Viewport viewport;
    private OrthographicCamera camera;

    @Override
    public void create() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera = new OrthographicCamera(w, h);
        camera.translate(w / 2, h / 2);
        camera.update();
        viewport = new ExtendViewport(w, h, camera);
        batch = new SpriteBatch();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        adventurerMale = new Texture("Banner.png");
        adventurerFemale = new Texture("adventurer-female.png");
        map = LevelManager.LoadLevel("arena.tmx");
        hero = new FemaleHero(adventurerFemale, map);
        tiledMapRenderer = new SeamlessOrthogonalTiledMapRenderer(map, batch);
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    private void update(float dt) {
        handleInput();
        camera.update();
        hero.update(dt);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(Gdx.graphics.getDeltaTime());
        
        batch.setProjectionMatrix(camera.combined);
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
        batch.begin();
        hero.render(batch);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        adventurerMale.dispose();
    }
}
