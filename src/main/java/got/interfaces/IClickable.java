package got.interfaces;

import org.joml.Vector2f;

import got.graphics.DrawSpace;



/**
 * Interface for clickable objects.
 * Used in {@link got.InputManager} to handle collisions and priority between clickable objects;
 * @author Souverain73
 *
 */
public interface IClickable {
	public int getPriority();
	public boolean ifMouseIn(Vector2f mousePos);
	public void setMouseIn(boolean mouseIn);
	public boolean isActive();
	public DrawSpace getSpace();
}
