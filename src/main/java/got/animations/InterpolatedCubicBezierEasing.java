package got.animations;

/**
 * Created by Souverain73 on 10.04.2017.
 */
public class InterpolatedCubicBezierEasing extends CubicBezierEasing {
    InterpolatedCubicBezierEasing(float x2, float y2, float x3, float y3, int steps) {
        super(x2, y2, x3, y3, steps);
    }

    @Override
    protected float calc(float t) {
        int step = (int) (t*steps);
        if (step == steps) return 1;
        float percent = t*steps - step;

        return values[step] + (values[step+1] - values[step]) * percent;
    }
}
