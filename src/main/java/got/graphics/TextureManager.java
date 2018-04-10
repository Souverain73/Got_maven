package got.graphics;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class TextureManager {
	private static TextureManager _instance = null;
	private static final String textureBase = "data/textures/";
	private HashMap<String, Texture> textures;
	
	private TextureManager(){
		textures = new HashMap<String, Texture>();
		loadTexture("dummy.png");
	}
	
	public static TextureManager instance(){
		if (_instance == null){
			_instance = new TextureManager();
		}
		return _instance;
	}
	
	public Texture loadTexture(String fileName){
		Texture res = textures.get(fileName);
		if (res == null){
			try {
				res = new Texture(textureBase + fileName);
				textures.put(fileName, res);
			}catch(FileNotFoundException e){
				System.out.println("Can't load texture " + fileName + " replaced by dummy.");
				Texture dummy;
				if ((dummy = textures.get("dummy.png")) != null){
					return dummy;
				}else{
					System.out.println("Can't load dummy.png");
					System.exit(0);
				}
			}
		}
		return res;
	}
	
	public Texture getTextureByFileName(String fileName){
		return textures.get(fileName);
	}
	
	public void deleteTexture(Texture texture){
		Iterator<Entry<String, Texture>> it = textures.entrySet().iterator();
		String res = null;
		while(it.hasNext()){
			Entry<String, Texture> item = it.next();
			if(item.getValue().equals(texture)){
				res = item.getKey();
				break;
			}
		}
		if (res!=null){
			textures.remove(res);
			texture.delete();
		}
	}
	
	public void deleteTexture(String fileName){
		Texture res = textures.get(fileName);
		if (res != null) textures.remove(fileName);
		res.delete();
	}
}
