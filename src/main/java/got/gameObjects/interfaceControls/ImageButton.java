package got.gameObjects.interfaceControls;

import org.joml.Vector2f;
import org.joml.Vector3f;

import got.gameStates.GameState;
import got.graphics.GraphicModule;
import got.graphics.Texture;
import got.graphics.TextureManager;
import got.utils.Utils;

/**
 * Extends {@link AbstractButtonObject} with image.<br>
 * For usage look {@link AbstractButtonObject}.
 *
 */
public class ImageButton extends AbstractButtonObject<ImageButton> {
	protected Texture texture;
	protected Object param;
	
	@Override
	protected ImageButton getThis() {
		return this;
	}

	
	private ImageButton(int x, int y, int w, int h, Object param){
		super(2);
		pos.x = x;
		pos.y = y;
		this.w = w;
		this.h = h;
		this.param = param;
	}
	
	public ImageButton(Texture tex, int x, int y, float scale, Object param){
		this(x,y, (int)(tex.getWidth()*scale), (int)(tex.getHeight()*scale), param);
		texture = tex;
	}
	
	
	public ImageButton(String textName, int x, int y, int w, int h, Object param) {
		this(x,y,w,h,param);
		texture = TextureManager.instance().loadTexture(textName);
	}

	public ImageButton(Texture tex, int x, int y, int w, int h, Object param) {
		this(x,y,w,h,param);
		texture = tex;
	}
	
	@Override
	public void draw(GameState st) {
		GraphicModule.instance().setDrawSpace(space);
		if (!isVisible()) return;
		if (state == State.FREE){
			
		}else if (state == State.HOVER){
			setOverlay(new Vector3f(0.0f, 0.5f, 0.0f));
		}else if (state == State.DOWN){
			setOverlay(new Vector3f(0.5f, 0.0f, 0.0f));
		}else if (state == State.DISABLED){
			setOverlay(new Vector3f(-0.5f, -0.5f, -0.5f));
		}
		
		texture.draw(getAbsolutePos().x, getAbsolutePos().y, w* getAbsoluteScale(), h* getAbsoluteScale());
		super.draw(st);
		
		GraphicModule.instance().resetEffect();
	}
	//IClickable
	@Override
	public boolean ifMouseIn(Vector2f mousePos) {
		if (Utils.pointInRect(mousePos,
				getAbsolutePos(),
				new Vector2f(w* getAbsoluteScale(), h* getAbsoluteScale()))
		){
			if (texture.getAlfa((mousePos.x - getAbsolutePos().x)/(w*getAbsoluteScale()), (mousePos.y - getAbsolutePos().y)/(h*getAbsoluteScale()))!=0) return true;
		}
		return false;
	}

	private void setOverlay(Vector3f overlay){
		GraphicModule.instance().getEffect().Overlay(overlay);
	}

	@Override
	protected void click(GameState st) {
		if (callback != null){
			callback.accept(this, param);
		}
	}

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}
}