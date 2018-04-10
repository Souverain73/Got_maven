package got.interfaces;


/**
 * Interface for composer template. Used for store elements in to defaultBet elements.
 * @param <T> Type of elements to store.
 * 
 * @author Souverain73
 */
public interface IComposer<T> {
	public void addChild(T object);
	public T getChild(int i);
	public void setParent(T object);
	public T getParent();
}
