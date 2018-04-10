package got.graphics;

import java.nio.FloatBuffer;

import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import got.Constants;

/**
 * Basic 2D camera with orthographic projection.
 * 
 * @author Souverain73
 *
 */
public class Ortho2DCamera implements ICamera{
	//constants
	private Matrix4f projection;
	private Vector3f position;
	private AxisAngle4f rotation;
	private float scale;
	private float w;
	private float h;
	private boolean forceUpdate;
	private boolean debug;
	private FloatBuffer fbProjection;

	public Ortho2DCamera(){
		this(true);
	}
	
	public Ortho2DCamera(boolean forceUpdate){
		this(forceUpdate, false);
	}
	
	public Ortho2DCamera(boolean forceUpdate, boolean debug){
		this(0, 0, forceUpdate, debug);
	}
	
	public Ortho2DCamera(float x, float y, boolean forceUpdate, boolean debug) {
		projection = new Matrix4f();
		fbProjection = BufferUtils.createFloatBuffer(16);
		position = new Vector3f(x, y, 0);
		rotation = new AxisAngle4f(0, 0, 0, 1);
		w = 0;
		h = 0;
		scale = 0.2f;
		this.forceUpdate = forceUpdate;
		this.debug = debug;
		updateCamera();
	}
	
	@Override
	public Matrix4f getProjection() {
		return projection;
	}
	
	@Override
	public FloatBuffer getProjectionAsFloatBuffer() {
		return fbProjection;
	}

	@Override
	public void moveCamera(Vector3f diff) {
		position.x += (diff.x/scale);
		position.y += (diff.y/scale);
		if (forceUpdate) updateCamera();
	}

	@Override
	public void setPosition(Vector3f pos) {
		position.x = pos.x;
		position.y = pos.y;
		if (forceUpdate) updateCamera();
	}
	
	@Override
	public Vector3f getCameraPosition() {
		return position;
	}

	@Override
	public void rotateCamera(float degrees, Vector3f axis) {
		rotation.angle += degrees;
		if (forceUpdate) updateCamera();
	}
	
	@Override
	public void setRotation(float degrees, Vector3f axis) {
		rotation.angle = degrees;
		if (forceUpdate) updateCamera();
	}

	@Override
	public void lookAt(Vector3f target) {
		position.x = -target.x;
		position.y = -target.y;
		if (forceUpdate) updateCamera();
	}

	@Override
	public void setViewport(float left, float right, float bottom, float top) {
		
	}

	@Override
	public void windowResizeCallback(int newW, int newH) {
		w = newW;
		h = newH;
		if (forceUpdate) updateCamera();
	}
	
	@Override
	public void scale(float diff) {
		scale+=diff;
		if (scale > Constants.MAX_SCALE) scale = Constants.MAX_SCALE;
		if (scale < Constants.MIN_SCALE) scale = Constants.MIN_SCALE;
		if (forceUpdate) updateCamera();
	}

	@Override
	public void setScale(float scale) {
		this.scale = scale;
		if (scale > Constants.MAX_SCALE) scale = Constants.MAX_SCALE;
		if (scale < Constants.MIN_SCALE) scale = Constants.MIN_SCALE;
		if (forceUpdate) updateCamera();
	}
	
	@Override
	public void updateCamera(){
		if (debug) System.out.println(this.toString());
		projection.setOrtho(-w/2, w/2, h/2, -h/2, 1, -1);
		projection.scale(scale, scale, 1);
		projection.translate(position);
		fbProjection.rewind();
		projection.get(fbProjection);
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Ortho2DCamera\n");
		sb.append("Position: "+position.x+" "+position.y+"\n");
		sb.append("Rotation: "+rotation.angle+"\n");
		sb.append("Scale: "+scale+"\n");
		return sb.toString();
	}
}
