package got.animations;

import org.joml.Vector2f;

import java.util.function.Consumer;

/**
 * Created by Souverain73 on 28.03.2017.
 */
public class Vector2fAnimation extends AbstractAnimation<Vector2f> {

    Vector2fAnimation(Vector2f startValue, Vector2f endValue, long time, Consumer<Vector2f> setter) {
        super(startValue, endValue, time, setter);
    }

    @Override
    protected Vector2f updateValue(Vector2f from, Vector2f to, float percent) {
        Vector2f result = new Vector2f(
                from.x + (to.x-from.x) * percent,
                from.y + (to.y-from.y) * percent
        );
        return result;
    }
}
