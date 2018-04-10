package got.animations;

import java.util.function.Consumer;

/**
 * Created by Souverain73 on 28.03.2017.
 */
public class IntAnimation extends AbstractAnimation<Integer> {
    IntAnimation(Integer startValue, Integer endValue, long time, Consumer<Integer> setter) {
        super(startValue, endValue, time, setter);
    }

    @Override
    protected Integer updateValue(Integer from, Integer to, float percent) {
        return from + Math.round((to - from) * percent);
    }
}
