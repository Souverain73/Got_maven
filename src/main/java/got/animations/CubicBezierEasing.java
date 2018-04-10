package got.animations;

/**
 * Created by Souverain73 on 10.04.2017.
 */
public class CubicBezierEasing implements Easing {
    protected int steps;
    protected float[] values;

    CubicBezierEasing(float x2, float y2, float x3, float y3, int steps){
        this.steps = steps;
        prepare(x2, y2, x3, y3);
    }

    private void prepare(float x2, float y2, float x3, float y3){
        values = new float[steps+1];
        float step = 1.0f / steps;
        for (float t=0; Float.compare(t, 1) <= 0; t+=step){
            float s = (1-t);
            float y = (3*(s*s)*t*y2) + 3*s*(t*t)*y3 + (t*t*t);
            float x = (3*(s*s)*t*x2) + 3*s*(t*t)*x3 + (t*t*t);

            values[(int)(x*steps)] = y;
            t += step;
        }
        values[steps] = 1;
        interpolate();
    }

    private void interpolate(){
        int start = 0;
        int end = 1;
        while (start != steps){
            while (values[end] == 0 && end < steps){
                end++;
            }
            for (int i=start+1; i<end; i++){
                values[i] = values[start] + (values[end] - values[start]) * (i-start)/(end-start);
            }
            start = end;
            end++;
        }
    }

    protected float calc(float t){
        return values[(int) (t*steps)];
    }

    @Override
    public float ease(float percent) {
        return calc(percent);
    }
}
