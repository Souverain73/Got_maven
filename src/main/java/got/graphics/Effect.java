package got.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Class for store visualization effects<br>
 * <p>
 * 		<b>overlay (Vector3f)</b> - Color overlay.<br>
 * 		<b>multiply (Vector3f)</b> - Color multiply. <b> DON'T IMPLEMENTED </b><br>
 * 		<b>transform (Matrix4f)</b> - Vertex transformation after projection.<b> DON'T IMPLEMENTED </b><br>
 * </p>
 * @author Souverain73
 *
 */
public class Effect {
	public  Vector3f overlay;
	public  Vector3f multiply;
	public  Matrix4f transform;
	
	public Effect() {
	}
	
	public Effect(Vector3f overlay, Vector3f colorMultiply, Matrix4f transform) {
		this.overlay = overlay;
		this.multiply = colorMultiply;
		this.transform = transform;
	}

	public Effect Overlay(Vector3f overlay) {
		this.overlay = overlay;
		return this;
	}

	public Effect Multiply(Vector3f multiply) {
		this.multiply = multiply;
		return this;
	}

	public Effect Transform(Matrix4f transform) {
		this.transform = transform;
		return this;
	}
	
	
}
