package got.gameObjects;

import org.joml.Vector2f;

import got.Constants;
import got.gameStates.GameState;
import got.graphics.DrawSpace;
import got.graphics.Texture;
import got.model.Unit;

public class UnitObject extends AbstractGameObject<UnitObject>{
	@Override protected UnitObject getThis() {return this;}
	
	private Unit type;
	
	public UnitObject(Unit type) {
		this.type = type;
		this.setSpace(DrawSpace.WORLD);
		this.setSize(Constants.UNIT_SIZE);
	}

	@Override
	public void draw(GameState state) {
		if (isVisible()){
			super.draw(state);
			Vector2f cp = getAbsolutePos();
			
			type.getTexture().draw(cp.x, cp.y, getW(), getH());
		}
	}



	public void setType(Unit type){
		this.type = type;
	}

	public Unit getType() {
		return type;
	}
	
	public Texture getTexture(){
		return type.getTexture();
	}

	public int getCost(){
		return type.getCost();
	}

	public boolean isUpgradeable(){
		return !(type.getPosibleUpgrades() == null || type.getPosibleUpgrades().length==0);
	}
	
	public Unit[] getPossibleUpgrades(){
		return type.getPosibleUpgrades();
	}
}
