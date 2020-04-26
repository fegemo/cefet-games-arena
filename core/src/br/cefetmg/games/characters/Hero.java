package br.cefetmg.games.characters;

import br.cefetmg.games.graphics.MultiAnimatedSprite;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author fegemo
 */
public abstract class Hero {

    private MultiAnimatedSprite sprite;
    private Facing facing;
    private final Vector2 speed;
    private final int frameWidth, frameHeight;
    private boolean isWalking = false;
    private final TiledMap map;

    public Hero(Texture texture, int frameWidth, int frameHeight, TiledMap map) {
        facing = Facing.WEST;
        this.map = map;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        speed = new Vector2(0, 0);
        assembleSpriteAnimations(texture);
    }

    private void assembleSpriteAnimations(Texture texture) {
        TextureRegion[][] frames = TextureRegion.split(texture, 32, 32);
        Map<String, Animation<TextureRegion>> animations = new HashMap<>();
        animations.put("idle", new Animation<>(0.1f, new Array<>(getIdleFrames(frames)), Animation.PlayMode.LOOP));
        animations.put("walk", new Animation<>(0.1f, new Array<>(getWalkFrames(frames)), Animation.PlayMode.LOOP));
        animations.put("attack", new Animation<>(0.1f, new Array<>(getAttackFrames(frames)), Animation.PlayMode.NORMAL));
        animations.put("hit", new Animation<>(0.1f, new Array<>(getHitFrames(frames)), Animation.PlayMode.NORMAL));
        animations.put("die", new Animation<>(0.1f, new Array<>(getDieFrames(frames)), Animation.PlayMode.NORMAL));

        this.sprite = new MultiAnimatedSprite(animations, "idle");
    }

    /**
     * Atualiza a posição do herói e a delimita dentro da janela.
     *
     * @param dt tempo desde a última atualização, que é usado para fazer o
     * herói se deslocar uma quantidade igual independente da velocidade do
     * computador que está executando.
     */
    private void updatePosition(float dt) {
        Vector2 position = new Vector2(sprite.getX(), sprite.getY());

//        float previousX = position.x;
//        float previousY = position.y;
//        
        position.add(speed.scl(dt * 120));
        // limita as posições (x, y) dentro da janela (não podem ser menor que 
        // 0, nem maior que a largura da janela - largura do goomba)
        position.x = Math.max(0, Math.min(
                Gdx.graphics.getWidth() - frameWidth, position.x));
        position.y = Math.max(0, Math.min(
                Gdx.graphics.getHeight() - frameHeight, position.y));

        if (isPositionWalkable((int) position.x, (int) position.y)) {
            sprite.setPosition(position.x, position.y);
        }
    }

    private boolean isPositionWalkable(int x, int y) {
        boolean walkable = true;
        for (Cell cell : getCellsAt(x+frameWidth/2, y+frameHeight/2)) {
            MapProperties props = cell.getTile().getProperties();

            walkable = walkable && props.get("walkable", false, Boolean.class);
        }
        return walkable;
    }

    private Array<Cell> getCellsAt(int j, int i) {
        TiledMapTileLayer ground = (TiledMapTileLayer) map.getLayers().get(0);
        TiledMapTileLayer walls = (TiledMapTileLayer) map.getLayers().get(1);
        TiledMapTileLayer decoration = (TiledMapTileLayer) map.getLayers().get(2);

        Array<Cell> cells = new Array<>(3);
        Cell groundCell = ground.getCell(j, i);
        Cell wallCell = walls.getCell(j, i);
        Cell decorationCell = decoration.getCell(j, i);

        if (groundCell != null) {
            cells.add(groundCell);
        }
        if (wallCell != null) {
            cells.add(wallCell);
        }
        if (decorationCell != null) {
            cells.add(decorationCell);
        }

        return cells;
    }

    private void handleInput() {
        // zera a velocidade do Goomba e vai acumulando os vetores direção
        // de cada tecla que está pressionada (digamos, UP e RIGHT 
        // simultaneamente)
        speed.setZero();

        // percorre todas as direções perguntando se a respectiva tecla 
        // está pressionada e, em caso afirmativo, soma sua direção à velocidade
        for (WalkingDirection d : WalkingDirection.values()) {
            if (Gdx.input.isKeyPressed(d.getKey())) {
                isWalking = true;
                sprite.startAnimation("walk");
                if (d.facing != null && d.facing != facing) {
                    sprite.flipFrames(d.facing != facing, false);
                    facing = d.facing;
                }
                speed.add(d.vector);
            }
        }

        // caso mais de uma tecla esteja pressionada (e.g, UP e RIGHT), a 
        // velocidade resultante teria o tamanho de 1.41... (raiz de 2) e o 
        // herói se movimentaria mais rápido. Para que isso não aconteça,
        // podemos normalizar o vetor da velocidade (aí ele passa a ter o
        // tamanho = 1 e aponta na mesma direção
        speed.nor();

        if (isWalking && speed.len2() < 0.000000005) {
            isWalking = false;
            sprite.startAnimation("idle");
        }
    }

    public void update(float dt) {
        handleInput();
        // atualiza a posição do herói
        if (speed.len2() > 0.000005) {
            updatePosition(dt);
        }

        this.sprite.update(dt);

    }

    public void render(SpriteBatch batch) {
        this.sprite.draw(batch);
    }

    protected abstract TextureRegion[] getIdleFrames(TextureRegion[][] frames);

    protected abstract TextureRegion[] getWalkFrames(TextureRegion[][] frames);

    protected abstract TextureRegion[] getAttackFrames(TextureRegion[][] frames);

    protected abstract TextureRegion[] getHitFrames(TextureRegion[][] frames);

    protected abstract TextureRegion[] getDieFrames(TextureRegion[][] frames);

    enum WalkingDirection {
        DOWN(Keys.DOWN, new Vector2(0, -1), null),
        RIGHT(Keys.RIGHT, new Vector2(1, 0), Facing.WEST),
        UP(Keys.UP, new Vector2(0, 1), null),
        LEFT(Keys.LEFT, new Vector2(-1, 0), Facing.EAST);

        // código da tecla que, quando pressionada, faz o personagem se 
        // movimentar nesta direção
        private final int keyCode;
        // a direção da velocidade quando o personagem se movimentar nesta
        // direção
        private final Vector2 vector;
        private final Facing facing;

        private WalkingDirection(int keyCode, Vector2 vector, Facing facing) {
            this.keyCode = keyCode;
            this.vector = vector;
            this.facing = facing;
        }

        int getKey() {
            return keyCode;
        }

        Vector2 getVector() {
            return vector;
        }
    }
}
