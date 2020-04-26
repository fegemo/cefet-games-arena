package br.cefetmg.games.graphics;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.Map;
import net.dermetfan.gdx.graphics.g2d.AnimatedSprite;

/**
 *
 * @author fegemo <coutinho@decom.cefetmg.br>
 */
public class MultiAnimatedSprite extends AnimatedSprite {

    public final Map<String, Animation<TextureRegion>> animations;

    public MultiAnimatedSprite(Map<String, Animation<TextureRegion>> animations,
            String initialAnimationName) {
        super(animations.get(initialAnimationName));
        this.animations = animations;
        super.setAutoUpdate(false);
    }

    public void startAnimation(String animationName) {
        if (!animations.containsKey(animationName)) {
            throw new IllegalArgumentException(
                    "Pediu-se para iniciar uma animação com o nome + '"
                    + animationName + "', mas esta MultiAnimatedSprite"
                    + "não possui uma animação com esse nome");
        }

        if (animations.get(animationName) == super.getAnimation()) {
            return;
        }

        // redefine o tempo decorrente da animação anterior (se existir)
        // para 0 para poder iniciar a nova do início
        super.setTime(0);

        // define qual é a animação
        super.setAnimation(animations.get(animationName));

        // começa a animação
        super.play();
    }

    public void stopAnimation() {
        super.stop();
    }

    @Override
    public void flipFrames(float startTime, float endTime, boolean flipX, boolean flipY, boolean set) {
        for (Animation animation : animations.values()) {
            endTime = Math.min(endTime, animation.getAnimationDuration());
            for (float t = startTime; t < endTime; t += animation.getFrameDuration()) {
                TextureRegion frame = (TextureRegion) animation.getKeyFrame(t);
                frame.flip(set ? flipX && !frame.isFlipX() : flipX, set ? flipY && !frame.isFlipY() : flipY);
            }
        }
    }

    @Override
    public void flipFrames(float startTime, float endTime, boolean flipX, boolean flipY) {
        flipFrames(startTime, endTime, flipX, flipY, false);
    }

    @Override
    public void flipFrames(boolean flipX, boolean flipY, boolean set) {
        for (String name : animations.keySet()) {
            Animation<TextureRegion> animation = animations.get(name);
            TextureRegion[] frames = animation.getKeyFrames();
            for (TextureRegion frame : frames) {
                if (set) {
                    frame.flip(flipX && !frame.isFlipX(), flipY && !frame.isFlipY());
                } else {
                    frame.flip(flipX, flipY);
                }
            }            
        }
    }

    @Override
    public void flipFrames(boolean flipX, boolean flipY) {
        flipFrames(flipX, flipY, false);
    }

}
