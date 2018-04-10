package got.graphics;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public interface ICamera {
	/**
	 * Gets projection matrix for current camera state.
	 * @return Matrix4f projection matrix
	 */
	public Matrix4f getProjection();
	
	public FloatBuffer getProjectionAsFloatBuffer();
	/**
	 * Move camera relative to current position.
	 * @param diff - move value.
	 */
	public void 	moveCamera(Vector3f diff);
	/**
	 * Set camera position
	 * @param pos - new camera position.
	 */
	public void 	setPosition(Vector3f pos);
	/**
	 * Gets camera position
	 * @return Vector3f camera position
	 */
	public Vector3f getCameraPosition();
	/**
	 * Rotate camera relative to current state.
	 * @param degrees
	 * @param axis
	 */
	public void 	rotateCamera(float degrees, Vector3f axis);
	/**
	 * Set absolute camera rotation.
	 * @param degrees
	 * @param axis
	 */
	public void 	setRotation(float degrees, Vector3f axis);
	/**
	 * Set camera target.
	 * @param target
	 */
	public void		lookAt(Vector3f target);
	/**
	 * Set viewport for camera.<br>
	 * Usually camera viewport equals Window viewport.
	 * @param left
	 * @param right
	 * @param bottom
	 * @param top
	 */
	public void 	setViewport(float left, float right, float bottom, float top);
	/**
	 * Handle window resize.
	 * @param newW
	 * @param newH
	 */
	public void 	windowResizeCallback(int newW, int newH);
	/**
	 * Scale camera relative to current scale.
	 * @param diff
	 */
	public void 	scale(float diff);
	/**
	 * Set absolute scale value.
	 * @param scale
	 */
	public void 	setScale(float scale);
	/**
	 * Force update camera state.<br>
	 * Usually you don't need to use it.
	 */
	public void 	updateCamera();
}
