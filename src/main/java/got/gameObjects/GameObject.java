package got.gameObjects;


import got.gameStates.GameState;
import got.utils.LoaderParams;

/**
 * Interface with all GameObject methods.
 * @author Souverain73
 *
 */
public interface GameObject{
	public boolean init(LoaderParams params);
	public void draw(GameState state);
	public void update(GameState state);
	public void finish();
	public boolean isVisible();
	public void setVisible(boolean visible);
	/**
	 * Метод, вызываемый каждый кадр для каждого объекта. 
	 * Используется для проверки, используется ли объект в данный момент.
	 * Нужен для корректной работы InputManager'a
	 */
	public void tick();
}
