package br.cefetmg.games.characters;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;

/**
 *
 * @author fegemo
 */
public class FemaleHero extends Hero {

    public FemaleHero(Texture texture, TiledMap map) {
        super(texture, 32, 32, map);
    }
    
    @Override
    protected TextureRegion[] getIdleFrames(TextureRegion[][] frames) {
        return new TextureRegion[] { frames[0][0], frames[0][1], frames[0][2], frames[0][3], frames[0][4], frames[0][5] };
    }

    @Override
    protected TextureRegion[] getWalkFrames(TextureRegion[][] frames) {
        return new TextureRegion[] { frames[1][0], frames[1][1], frames[1][2], frames[1][3], frames[1][4], frames[1][5], frames[1][6], frames[1][7] };
    }

    @Override
    protected TextureRegion[] getAttackFrames(TextureRegion[][] frames) {
        return new TextureRegion[] { frames[2][0], frames[2][1], frames[2][2], frames[2][3], frames[2][4], frames[2][5], frames[2][6], frames[2][7], frames[2][8] };
    }

    @Override
    protected TextureRegion[] getHitFrames(TextureRegion[][] frames) {
        return new TextureRegion[] { frames[3][0], frames[3][3], frames[3][1], frames[3][2] };
    }

    @Override
    protected TextureRegion[] getDieFrames(TextureRegion[][] frames) {
        return new TextureRegion[] { frames[4][0], frames[4][1], frames[4][2], frames[4][3], frames[4][4], frames[4][5], frames[4][6] };
    }
    
}
