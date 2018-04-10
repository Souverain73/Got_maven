package got.gameObjects;

import got.animations.Animation;
import got.animations.Animator;
import org.joml.Vector2f;

/**
 * Created by Souverain73 on 17.04.2017.
 */
public class AnimatedObject extends AbstractGameObject<AnimatedObject>{
    private AbstractGameObject<?> object;

    @Override protected AnimatedObject getThis() {return this;}

    public AnimatedObject(AbstractGameObject<?> object){
        this.object = object;
    }

    public Animation<?> move(Vector2f pos, long time){
        return Animator.animateVector2f(object.getPos(), pos, time, object::setPos);
    }

    public Animation<?> scale(float scale, long time){
        return Animator.animateFloat(object.getScale(), scale, time, object::setScale);
    }

    public Animation<?> resize(Vector2f dim, long time){
        return Animator.animateVector2f(object.getDim(), dim, time, object::setDim);
    }

    public AbstractGameObject<?> getObject() {
        return object;
    }
}
