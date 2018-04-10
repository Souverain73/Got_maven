package got.animations;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


/**
 * Created by Souverain73 on 28.03.2017.
 */
public class Animator {
    private static Animator _instance;
    List<Animation> animations = new ArrayList<>();
    private long lastUpdateTime;


    private static Animator instance(){
        if (_instance == null){
            _instance = new Animator();
        }
        return _instance;
    }

    public static Animation animateInt(int from, int to, long time, Consumer<Integer> setter){
        Animation result = new IntAnimation(from, to, time, setter);
        instance().animations.add(result);
        return result;
    }

    public static Animation animateFloat(float from, float to, long time, Consumer<Float> setter){
        Animation result = new FloatAnimation(from, to, time, setter);
        instance().animations.add(result);
        return  result;
    }

    public static Animation animateVector2f(Vector2f from,Vector2f to, long time, Consumer<Vector2f> setter){
        Animation result = new Vector2fAnimation(from, to, time, setter);
        instance().animations.add(result);
        return  result;
    }

    public static Animation animateVector3f(Vector3f from, Vector3f to, long time, Consumer<Vector3f> setter){
        Animation result = new Vector3fAnimation(from, to, time, setter);
        instance().animations.add(result);
        return  result;
    }

    public static void update(){
        instance().lastUpdateTime = System.nanoTime() / 1000000;
        instance().animations.forEach(a->a.update());
        //remove finished animations
        for (int i = 0; i < instance().animations.size(); i++) {
            if (instance().animations.get(i).isFinished()){
                instance().animations.remove(i);
            }
        }
    }

    static long getCurrentTime(){
        return instance().lastUpdateTime;
    }
}
