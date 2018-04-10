package got.model;

import java.util.Arrays;
import java.util.function.Predicate;

import got.graphics.Texture;
import got.graphics.TextureManager;

public enum Unit {
	KNIGHT(2, 2, false, "knight.png"),
	SIEGE(2, 4, false, "siege.png"),
	SHIP(1, 1, false, "ship.png"),
	SOLDIER(1, 1, false, "soldier.png", KNIGHT, SIEGE),
	WEAK_KNIGHT(10, 0, true, "weak_knight.png"),
	WEAK_SHIP(10, 0, true, "weak_ship.png"),
	WEAK_SOLDIER(10, 0, true, "weak_soldier.png");
	
	private static final String TEXTURE_BASE = "units/";
	private final int cost;
	private final int damage;
	private final String textureName;
	private Texture texture = null;
	private final Unit[] upgrades;
	private boolean isWeak;
	
	private Unit(int cost, int damage, boolean isWeak, String textureName, Unit ...upgrades){
		this.cost = cost;
		this.damage = damage;
		this.textureName = textureName;
		this.isWeak = isWeak;
		this.upgrades = upgrades;
	}
	
	public int getCost(){
		return cost;
	}

	public int getDamage() {
		return damage;
	}

	public Texture getTexture() {
		if (texture == null){
			this.texture = TextureManager.instance().loadTexture(TEXTURE_BASE+textureName);
		}
		return texture;
	}

	public boolean isWeak() {
		return isWeak;
	}

	public Unit getKilled() {
		switch (this) {
			case KNIGHT:
				return WEAK_KNIGHT;
			case SOLDIER:
				return WEAK_SOLDIER;
			case SHIP:
				return WEAK_SHIP;
			case SIEGE:
				return null;
			default:
				return this;
		}
	}

	public Unit getAlive(){
		switch (this){
			case WEAK_KNIGHT:
				return KNIGHT;
			case WEAK_SOLDIER:
				return SOLDIER;
			case WEAK_SHIP:
				return SHIP;
			default:
				return this;
		}
	}

	public Unit[] getPosibleUpgrades(){
		return upgrades;
	}
	
	public static Unit[] getUnitsByCondition(Predicate<Unit> condition){
		return Arrays.stream(Unit.values()).filter(condition).toArray(Unit[]::new);
	}
}
