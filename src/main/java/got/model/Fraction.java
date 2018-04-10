package got.model;

import got.graphics.Texture;
import got.graphics.TextureManager;
import org.joml.Vector3f;

public enum Fraction {
//	STARK	(new Vector3f(3, 3, 3), "StarkBack.png", "StarkCover.png"),
//	BARATHEON(new Vector3f(5,5,0), "BaratheonBack.png", "BaratheonCover.png"),
//	LANISTER (new Vector3f(5, 1, 1), "LanisterBack.png", "LanisterCover.png"),
//	GREYJOY	(new Vector3f(0.6f, 0.6f, 0.6f), "GreyjoyBack.png", "GreyjoyCover.png"),
//	TYRELL	(new Vector3f(1, 4, 1), "TyrellBack.png", "TyrellCover.png"),
//	MARTEL	(new Vector3f(5, 2.5f, 1), "MartelBack.png", "MartelCover.png"),
//	NONE	(new Vector3f(1, 1, 1), null, null),
//	NEUTRAL_LORD(new Vector3f(1, 1, 1), null, null);

//Baratheon vs Tyrell

	BARATHEON(new Vector3f(5,5,0), "BaratheonBack.png", "BaratheonCover.png"),
	TYRELL	(new Vector3f(1, 4, 1), "TyrellBack.png", "TyrellCover.png"),
	STARK	(new Vector3f(3, 3, 3), "StarkBack.png", "StarkCover.png"),
	LANISTER (new Vector3f(5, 1, 1), "LanisterBack.png", "LanisterCover.png"),
	GREYJOY	(new Vector3f(0.6f, 0.6f, 0.6f), "GreyjoyBack.png", "GreyjoyCover.png"),
	MARTEL	(new Vector3f(5, 2.5f, 1), "MartelBack.png", "MartelCover.png"),
	NONE	(new Vector3f(1, 1, 1), null, null),
	NEUTRAL_LORD(new Vector3f(1, 1, 1), null, null),
	UNKNOWN(new Vector3f(1,1,1), "unknown.png", null);


	private final Vector3f multiplyColor;
	private final String backTextureName;
	private final String coverTextureName;
	private Texture backTexture = null;
	private Texture coverTexture = null;
	private static final String TEXTURE_BASE = "fractions/";

	Fraction(Vector3f multiply, String backTextureName, String coverTextureName){
		this.multiplyColor = multiply;
		this.backTextureName = backTextureName;
		this.coverTextureName = coverTextureName;
	}

	public Texture getBackTexture(){
		if (backTexture == null){
			backTexture = TextureManager.instance().loadTexture(TEXTURE_BASE + backTextureName);
		}
		return backTexture;
	}

	public Texture getCoverTexture(){
		if (coverTexture == null){
			coverTexture = TextureManager.instance().loadTexture(TEXTURE_BASE + coverTextureName);
		}
		return coverTexture;
	}

	public Vector3f getMultiplyColor(){
		return multiplyColor;
	}
}	
