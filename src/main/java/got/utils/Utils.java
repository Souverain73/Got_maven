package got.utils;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import got.gameObjects.interfaceControls.ImageButton;
import got.graphics.DrawSpace;
import got.vesterosCards.VesterosCard;
import org.joml.Vector2f;


public class Utils {
	private static Random rand = new Random();
	
	
	/**
	 * Reads file into String;
	 * @param path - path to file
	 * @return all file data as encoded string.
	 * @throws IOException - if can't read file
	 */
	static public String readFile(String path) throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
	    return new String(encoded);
	}
	
	/**
	 * Check if point in rectangle
	 * @param point - point
	 * @param rectPos - top left corner of rectangle
	 * @param rectDim - width and height of rectangle
	 */
	static public boolean pointInRect(Vector2f point, Vector2f rectPos, Vector2f rectDim) {
		return point.x >= rectPos.x && point.x < rectPos.x + rectDim.x && point.y >= rectPos.y && point.y < rectPos.y + rectDim.y;
	}
	
	
	/**
	 * Calculate distance between two points
	 * @param p1 - point 1
	 * @param p2 - point 2
	 * @return distance between p1 and p2
	 */
	static public float distance(Vector2f p1, Vector2f p2){
		return (float)Math.sqrt((p1.x-p2.x)*(p1.x-p2.x)+(p1.y-p2.y)*(p1.y-p2.y));
	}
	
	static public boolean chance(int chance){
		return rand.nextInt()%100<chance;
	}

	static public int limitInt(int value, int min, int max){
		if (value < min) value = min;
		if (value > max) value = max;

		return value;
	}

	static public float limitFloat(float value, float min, float max){
		if (value < min) value = min;
		if (value > max) value = max;

		return value;
	}

	static public ImageButton getReadyButton(Object param){
		return new ImageButton("buttons/ready.png", 1070, 610, 200, 100, param).setSpace(DrawSpace.SCREEN);
	}

	public static List shuffle(List input){
		int size = input.size();
		List result  = new ArrayList<>(size);
		for (int i=size; i>0; i--){
			int num = ThreadLocalRandom.current().nextInt(i);
			result.add(input.get(num));
			input.remove(num);
		}
		return result;
	}
}
