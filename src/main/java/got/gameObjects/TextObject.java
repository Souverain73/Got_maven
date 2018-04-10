package got.gameObjects;

import got.graphics.text.Font;
import got.graphics.text.FontTrueType;
import org.joml.Vector2f;

import got.gameStates.GameState;
import got.graphics.GraphicModule;
import got.graphics.text.Text;

public class TextObject extends AbstractGameObject<TextObject>{
	Font font;
	Text text;
	String currentText = "";
	
	@Override
	protected TextObject getThis() {
		return this;
	}

	
	public TextObject(Font font, String message) {
		this.font = font;
		text = Text.newInstance(currentText, font);
		setText(message);
	}

	public TextObject(String message){
		this(new FontTrueType("BKANT", 16), message);
	}
	
	@Override
	public void update(GameState state) {
		super.update(state);
	}
	
	@Override
	public void draw(GameState state) {
		super.draw(state);
		Vector2f cp = getAbsolutePos();
		GraphicModule.instance().setDrawSpace(getSpace());
		text.draw(cp.x, cp.y, 1, 1);
	}
	
	public void setText(String newText){
		if (!currentText.equals(newText))
			text.changeText(newText);
		this.w = font.getStringWidth(newText);
		this.h = font.getSize();
	}
}
