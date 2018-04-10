package got.animations;

/**
 * Created by Souverain73 on 10.04.2017.
 */
public class Easings {
    private enum EasingType {
        BEZIER,
        BEZIER_INTERPOLATED,
        PENNERS_EQUATIONS
    }
    private static int BEZIER_STEPS = 1000;
    private static EasingType type = EasingType.PENNERS_EQUATIONS;
    public static Easing LINEAR = (v)->v;
    public static Easing IN_OUT_CUBIC;
    public static Easing IN_CUBIC;
    public static Easing OUT_CUBIC;
    public static Easing IN_BACK;
    public static Easing OUT_BACK;
    public static Easing IN_OUT_BACK;
    public static Easing IN_OUT_SINE;
    public static Easing IN_SINE;
    public static Easing OUT_SINE;

    static{
        if (type == EasingType.BEZIER){
            IN_OUT_CUBIC = new CubicBezierEasing(0.645f, 0.045f, 0.355f, 1f, BEZIER_STEPS);
            IN_CUBIC     = new CubicBezierEasing(0.55f, 0.055f, 0.675f, 0.19f, BEZIER_STEPS);
            OUT_CUBIC    = new CubicBezierEasing(0.215f, 0.61f, 0.355f, 1f, BEZIER_STEPS);
            IN_BACK      = new CubicBezierEasing(0.6f, -0.28f, 0.735f, 0.045f, BEZIER_STEPS);
            OUT_BACK     = new CubicBezierEasing(0.175f, 0.885f, 0.32f, 1.275f, BEZIER_STEPS);
            IN_OUT_BACK  = new CubicBezierEasing(0.68f, -0.55f, 0.265f, 1.55f, BEZIER_STEPS);
            IN_OUT_SINE  = new CubicBezierEasing(0.445f, 0.05f, 0.55f, 0.95f, BEZIER_STEPS);
            IN_SINE      = new CubicBezierEasing(0.47f, 0f, 0.745f, 0.715f, BEZIER_STEPS);
            OUT_SINE     = new CubicBezierEasing(0.39f, 0.575f, 0.565f, 1f, BEZIER_STEPS);
        }else if (type == EasingType.BEZIER_INTERPOLATED){
            IN_OUT_CUBIC = new InterpolatedCubicBezierEasing(0.645f, 0.045f, 0.355f, 1f, BEZIER_STEPS);
            IN_CUBIC     = new InterpolatedCubicBezierEasing(0.55f, 0.055f, 0.675f, 0.19f, BEZIER_STEPS);
            OUT_CUBIC    = new InterpolatedCubicBezierEasing(0.215f, 0.61f, 0.355f, 1f, BEZIER_STEPS);
            IN_BACK      = new InterpolatedCubicBezierEasing(0.6f, -0.28f, 0.735f, 0.045f, BEZIER_STEPS);
            OUT_BACK     = new InterpolatedCubicBezierEasing(0.175f, 0.885f, 0.32f, 1.275f, BEZIER_STEPS);
            IN_OUT_BACK  = new InterpolatedCubicBezierEasing(0.68f, -0.55f, 0.265f, 1.55f, BEZIER_STEPS);
            IN_OUT_SINE  = new InterpolatedCubicBezierEasing(0.445f, 0.05f, 0.55f, 0.95f, BEZIER_STEPS);
            IN_SINE      = new InterpolatedCubicBezierEasing(0.47f, 0f, 0.745f, 0.715f, BEZIER_STEPS);
            OUT_SINE     = new InterpolatedCubicBezierEasing(0.39f, 0.575f, 0.565f, 1f, BEZIER_STEPS);
        }else if (type == EasingType.PENNERS_EQUATIONS){
            IN_OUT_CUBIC = t-> -2*t*t*t +3*t*t;
            IN_CUBIC     = t -> t*t*t;
            OUT_CUBIC    = t -> (t=t-1)*t*t+1;
            IN_BACK      = new CubicBezierEasing(0.6f, -0.28f, 0.735f, 0.045f, BEZIER_STEPS);
            OUT_BACK     = new CubicBezierEasing(0.175f, 0.885f, 0.32f, 1.275f, BEZIER_STEPS);
            IN_OUT_BACK  = new CubicBezierEasing(0.68f, -0.55f, 0.265f, 1.55f, BEZIER_STEPS);
            IN_OUT_SINE  = t -> -1.0f/2 * ((float)Math.cos(Math.PI*t) - 1);
            IN_SINE      = t -> -1 * (float)Math.cos(t * (Math.PI/2)) + 1;
            OUT_SINE     = t -> (float)Math.sin(t * (Math.PI/2));
        }
    }
}
