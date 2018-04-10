package got.animations;

import java.util.function.Consumer;

/**
 * Created by Souverain73 on 28.03.2017.
 */
public class FloatAnimation extends AbstractAnimation<Float> {
    FloatAnimation(Float startValue, Float endValue, long time, Consumer<Float> setter) {
        super(startValue, endValue, time, setter);
    }

    @Override
    protected Float updateValue(Float from, Float to, float percent) {
        return from + (to - from) * percent;
    }
}
