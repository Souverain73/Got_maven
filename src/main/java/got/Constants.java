package got;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Constants {
	public static final String DEFAULT_CONFIG_FILE = "client.cfg";

//GAME MECHANICS
	public static int MAX_PLAYERS = 6;
	public static int MAX_MONEY = 21;

//SCREEN
	public static int SCREEN_WIDTH = 1280;
	public static int SCREEN_HEIGHT = 720;

//WINDOW
	public static int WINDOW_WIDTH = 320;
	public static int WINDOW_HEIGHT= 180;
	
//CAMERA
	public static float MAX_SCALE = 10f;
	public static float MIN_SCALE = 0f;
	
//ACTION SELECTOR
	public static float ACTION_SELECTOR_IMAGE_SCALE = 0.5f;
	public static float ACTION_SELECTOR_RADIUS = 200;
	
//ACTION
	public static float ACTION_IMAGE_SIZE = 100;

//UNITS
	public static float UNIT_SIZE = 50;
	public static float UNIT_SCALE = 1;
	public static float UNIT_STEP = 3;

//POWER TOKEN
	public static float POWER_TOKEN_IMAGE_SIZE = 50;

	public static String DEFAULT_HOST = "localhost";
	public static String NICKNAME = "_rnd";

	public static void read(String fileName){
		try{
			List<String> lines = Files.readAllLines(Paths.get(fileName));
			Pattern keyValuePattern = Pattern.compile("(.*)=(.*)");
			for (String line : lines){
				Matcher match = keyValuePattern.matcher(line);
				if (match.find()){
					parseParam(match.group(1), match.group(2));
				}
			}
		}catch (IOException e){
			System.out.println("Can't read config file");
		}
	}

	private static void parseParam(String name, String value) {
		switch (name){
			case "MAX_PLAYERS": MAX_PLAYERS = Integer.valueOf(value); break;
			case "MAX_MONEY": MAX_MONEY = Integer.valueOf(value); break;
			case "SCREEN_WIDTH": SCREEN_WIDTH = Integer.valueOf(value); break;
			case "SCREEN_HEIGHT": SCREEN_HEIGHT = Integer.valueOf(value); break;
			case "WINDOW_WIDTH": WINDOW_WIDTH = Integer.valueOf(value); break;
			case "WINDOW_HEIGHT": WINDOW_HEIGHT = Integer.valueOf(value); break;
			case "MAX_SCALE": MAX_SCALE = Float.valueOf(value); break;
			case "MIN_SCALE": MIN_SCALE = Float.valueOf(value); break;
			case "ACTION_SELECTOR_IMAGE_SCALE": ACTION_SELECTOR_IMAGE_SCALE = Float.valueOf(value); break;
			case "ACTION_SELECTOR_RADIUS": ACTION_SELECTOR_RADIUS = Float.valueOf(value); break;
			case "ACTION_IMAGE_SIZE": ACTION_IMAGE_SIZE = Float.valueOf(value); break;
			case "UNIT_SIZE": UNIT_SIZE = Float.valueOf(value); break;
			case "UNIT_SCALE": UNIT_SCALE = Float.valueOf(value); break;
			case "UNIT_STEP": UNIT_STEP = Float.valueOf(value); break;
			case "POWER_TOKEN_IMAGE_SIZE": POWER_TOKEN_IMAGE_SIZE = Float.valueOf(value); break;
			case "DEFAULT_HOST": DEFAULT_HOST = value; break;
			case "NICKNAME": NICKNAME = value; break;
			default:
				System.out.println("Can't find parameter " + name);
		}
	}
}
