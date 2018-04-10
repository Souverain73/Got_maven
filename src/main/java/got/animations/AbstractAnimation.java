package got.animations;

import java.util.function.Consumer;

import static got.animations.Animator.getCurrentTime;

/**
 * Created by Souverain73 on 28.03.2017.
 */
public abstract class AbstractAnimation<T> implements Animation<T> {
    private final T startValue;
    private final T endValue;
    private final long time;
    private final Consumer<T> setter;
    private long startingTime;
    private boolean enabled;
    private boolean finished;
    private T currentValue;
    private Easing easing;
    private Runnable after;

    AbstractAnimation(T startValue, T endValue, long time, Easing easing, Consumer<T> setter){
        this.startValue = startValue;
        this.endValue = endValue;
        this.time = time;
        this.setter = setter;
        this.startingTime = getCurrentTime();
        this.enabled = true;
        this.finished = false;
        this.easing = easing;
    }

    AbstractAnimation(T startValue, T endValue, long time, Consumer<T> setter){
        this(startValue, endValue, time, Easings.IN_OUT_SINE, setter);
    }

    @Override
    public Animation setEasing(Easing easing) {
        this.easing = easing;
        return this;
    }

    @Override
    public Animation after(Runnable callback) {
        this.after = callback;
        return this;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public T getCurrentValue() {
        return currentValue;
    }

    @Override
    public void update(){
        if (!enabled) return;

        long timeLast = Animator.getCurrentTime() - startingTime;

        if (timeLast > time){
            timeLast = time;
            enabled = false;
            finished = true;
        }
        currentValue = updateValue(startValue, endValue, easing.ease((float)timeLast/time));
        setter.accept(currentValue);
        if (finished && after != null){
            after.run();
        }
    }

    protected abstract T updateValue(T from, T to, float percent);
}
