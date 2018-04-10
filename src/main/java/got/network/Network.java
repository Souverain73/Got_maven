package got.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

import got.gameObjects.battleDeck.BattleOverrides;
import got.houseCards.HouseCard;
import got.model.Action;
import got.model.ChangeAction;

import java.util.ArrayList;

public class Network {
	public static int portTCP = 54555;
	public static int portUDP = 54777;
	
	public static void register(EndPoint endpoint){
		Kryo kryo = endpoint.getKryo();
		Packages.register(endpoint);
		kryo.register(got.model.Player.class);
		kryo.register(got.model.Player[].class);
		
		kryo.register(got.model.Fraction.class);
		kryo.register(got.model.Fraction[].class);
		
		kryo.register(got.model.Unit.class);
		kryo.register(got.model.Unit[].class);

		kryo.register(Action.class);
		kryo.register(Action[].class);

		kryo.register(BattleOverrides.class);

		kryo.register(ChangeAction.class);

		kryo.register(int[].class);
	}
	
	
}
