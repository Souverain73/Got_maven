package got.animations;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.function.Consumer;

/**
 * Created by Souverain73 on 28.03.2017.
 */
public class Vector3fAnimation extends AbstractAnimation<Vector3f> {
    Vector3fAnimation(Vector3f startValue, Vector3f endValue, long time, Consumer<Vector3f> setter) {
        super(startValue, endValue, time, setter);
    }

    @Override
    protected Vector3f updateValue(Vector3f from, Vector3f to, float percent) {
        Vector3f result = new Vector3f(
                from.x + (to.x-from.x) * percent,
                from.y + (to.y-from.y) * percent,
                from.z + (to.z-from.z) * percent
        );
        return result;
    }
}
