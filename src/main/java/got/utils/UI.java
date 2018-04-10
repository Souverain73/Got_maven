package got.utils;

import com.esotericsoftware.minlog.Log;
import got.GameClient;
import got.model.Player;

import javax.swing.JOptionPane;

import static got.translation.Translator.tt;

public class UI {
	
	public static String getString(String title, String label, String def){
		String input = (String)JOptionPane.showInputDialog(null, label, title, 
				JOptionPane.QUESTION_MESSAGE, null, null ,def);
		if (input != null) input = input.trim();
		return input;
	}
	
	public static void serverMessage(String message){
		System.out.println("[Server]:"+message);
	}
	
	public static void systemMessage(String message){
		System.out.println("[System]:"+message);
	}

	public static void logAction(String format, Object... args){
		format = tt(format);
		Log.info("[Action]:" + String.format(format, args));
	}

	public static void logSystem(String message){
		Log.info("[System]:" + message);
	}

	public static void tooltipWait(Player player){
		GameClient.instance().setTooltipText("common.waitForPlayer", player.getNickname());
	}
}
