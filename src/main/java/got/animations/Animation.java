package got.animations;

/**
 * Created by Souverain73 on 28.03.2017.
 */
public interface Animation<T> {
    boolean isFinished();
    T getCurrentValue();
    void update();
    Animation setEasing(Easing easing);
    Animation after(Runnable callback);
}
