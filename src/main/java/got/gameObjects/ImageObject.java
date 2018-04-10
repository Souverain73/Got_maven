package got.gameObjects;

import org.joml.Vector2f;

import got.gameStates.GameState;
import got.graphics.GraphicModule;
import got.graphics.Texture;
import got.graphics.TextureManager;

/**
 * Implements simple image.
 * @author КизиловМЮ
 *
 */
public class ImageObject extends AbstractGameObject<ImageObject>{
	private Texture tex;

	@Override
	protected ImageObject getThis() {
		return this;
	}
	
	public ImageObject(Texture tex, int w, int h) {
		super();
		this.w = w;
		this.h = h;
		this.tex = tex;
	}
	
	public ImageObject(String textureName, int w, int h){
		this(TextureManager.instance().loadTexture(textureName),
				w, h);
	}
		
	@Override
	public void draw(GameState state) {
		if (!isVisible()) return;
		GraphicModule.instance().setDrawSpace(this.space);
		Vector2f cp = getAbsolutePos();
		float scale = getAbsoluteScale();
		tex.draw(cp.x, cp.y, w*scale, h*scale);
		super.draw(state);
	}
	
	public void setTexture(Texture texture){
		this.tex = texture;
	}
}
