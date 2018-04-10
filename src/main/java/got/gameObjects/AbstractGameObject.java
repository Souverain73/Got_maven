package got.gameObjects;

import java.util.ArrayList;
import java.util.List;

import got.Constants;
import org.joml.Vector2f;

import got.gameStates.GameState;
import got.graphics.DrawSpace;
import got.interfaces.IComposer;
import got.utils.LoaderParams;

/**
 * Base class for all GameObject.
 * Implements basic GameObject functions and extends it with Position, Dimensions, and Container functions.
 * @author Souverain73
 *
 */
public abstract class AbstractGameObject<T extends AbstractGameObject<T>> implements GameObject, IComposer<AbstractGameObject>{
	protected List<AbstractGameObject> childs;
	protected AbstractGameObject parent;
	protected Vector2f pos;
	protected float w, h;
	protected float scale;
	protected boolean visible;
	protected DrawSpace space = DrawSpace.WORLD;
	protected boolean updated, drawed, used;
	
	protected abstract T getThis();
	
//	public Vector2f getAbsolutePos() {
//		return (parent == null) ? pos : new Vector2f(parent.getAbsolutePos()).add(pos);
//	}

	public Vector2f getAbsolutePos(){
		if (parent == null){
			return pos;
		}else{
			return new Vector2f(parent.getAbsolutePos()).add(new Vector2f(pos).mul(parent.getAbsoluteScale()));
		}
	}

	public Vector2f getPos() {
		return pos;
	}

	protected AbstractGameObject() {
		//new objects are active
		visible = true;
		updated = true;
		drawed = true;
		used = true;
		pos = new Vector2f();
		scale = 1.0f;
		childs = new ArrayList<>();
	}
	
	public T setSpace(DrawSpace space){
		this.space = space;
		childs.forEach(o->o.setSpace(space));
		return getThis();
	}
	
	public DrawSpace getSpace(){
		return space;
	}
	
	@Override
	public void addChild(AbstractGameObject object) {
		if (object!=null){
			object.setParent(this);
			childs.add(object);
		}
	}

	@Override
	public AbstractGameObject getChild(int i) {
		if (i<childs.size()) return childs.get(i);
		return null;
	}

	public void removeChild(AbstractGameObject object){
		if (object!=null){
			object.setParent(null);
			childs.remove(object);
		}
	}
		
	@Override
	public void setParent(AbstractGameObject object) {
		parent = object;
		if (parent!=null)
			this.setSpace(object.space);
	}
	
	@Override
	public AbstractGameObject getParent() {
		return parent;
	}
	
	@Override
	public boolean init(LoaderParams params) {
		return false;
	}

	@Override

	public void draw(GameState state) {
		drawed = true;
		if (isVisible()){
			childs.forEach(obj->obj.draw(state));
		}
	}

	public float getW() {
		return w;
	}

	public T setW(float w) {
		this.w = w;
		return getThis();
	}

	public float getH() {
		return h;
	}

	public T setH(float h) {
		this.h = h;
		return getThis();
	}

	public float getAbsoluteScale() {
		if (parent == null){
			return scale;
		}else{
			return parent.getAbsoluteScale() * scale;
		}
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public T setDim(Vector2f dim){
		this.w = dim.x;
		this.h = dim.y;
		return getThis();
	}
	
	public Vector2f getDim(){
		return new Vector2f(this.w, this.h);
	}
	
	/**
	 * Метод устанавливает одновременно ширину и высоту объекта равную size<br>
	 * <b>(w = h = size)</b>
	 * @param size
	 */
	public T setSize(float size){
		this.w = this.h = size;
		return getThis();
	}

	public T setPos(Vector2f pos) {
		this.pos = pos;
		return getThis();
	}

	public T setPos(int x, int y){
		this.pos = new Vector2f(x, y);
		return getThis();
	}

	@Override
	public void update(GameState state) {
		updated = true;
		childs.forEach(obj->obj.update(state));
	}

	@Override
	public void finish() {
		childs.forEach(obj->obj.finish());
	}

	@Override
	public boolean isVisible(){
		return visible;
	};
	
	@Override
	public void setVisible(boolean visible){
		this.visible = visible;
	}

	@Override
	public void tick() {
		childs.forEach(obj->obj.tick());
		used = updated && drawed;
		updated = drawed = false;
	}

	protected boolean isUsed() {
		return used;
	}
}
