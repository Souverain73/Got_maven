package got.gameStates;

import java.io.IOException;
import java.util.Random;

import got.Constants;
import org.joml.Vector2f;

import com.esotericsoftware.kryonet.Connection;

import got.GameClient;
import got.gameObjects.GameObject;
import got.gameObjects.interfaceControls.ImageButton;
import got.gameObjects.NetPlayersPanel;
import got.graphics.DrawSpace;
import got.interfaces.IClickListener;
import got.model.Game;
import got.model.Player;
import got.network.Packages;
import got.network.Packages.ConnectionError;
import got.network.Packages.PlayerConnected;
import got.network.Packages.PlayerDisconnected;
import got.network.Packages.PlayerReady;
import got.network.Packages.PlayersList;
import got.network.Packages.Ready;
import got.network.Packages.SetFractions;
import got.server.PlayerManager;
import got.utils.UI;

public class NetworkRoomState extends AbstractGameState{
	private static String name = "NetworkRoomState";
	NetPlayersPanel npp;
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter(StateMachine stm) {
		System.out.println("Entering "+name);
		npp = new NetPlayersPanel();
		npp.setPos(new Vector2f(1080, 0));
		addObject(npp);
		ImageButton btn = new ImageButton("buttons/ready.png", 1080, 670, 80, 40, null)
			.setSpace(DrawSpace.SCREEN)
			.setCallback((sender, param)->{
				GameClient.instance().send(new Ready(!PlayerManager.getSelf().isReady()));
			});
		addObject(btn);
		btn = new ImageButton("buttons/exit.png", 1180, 670, 80, 40, null)
				.setSpace(DrawSpace.SCREEN)
				.setCallback((sender, param)->{
					GameClient.instance().disconnect();
					GameClient.instance().getStateMachine().setState(new MenuState());
				});
		addObject(btn);
		
		String host = UI.getString("Enter host", "host", Constants.DEFAULT_HOST);

		if (host == null) return;

		try{
			GameClient.instance().connect(host);
		}catch(IOException e){
			System.out.println("Can't connect to host:"+host);
			GameClient.instance().getStateMachine().setState(new MenuState());
			return;
		}

		String nickname = Constants.NICKNAME;
		if (nickname.equals("_rnd")){
			nickname = String.format("%010d", (new Random()).nextLong()%10000);
		}

		GameClient.instance().send(new Packages.LogIn().Nickname(nickname));
		System.out.println("Connection successful");
	}
	
	
	@Override
	public void recieve(Connection connection, Object pkg) {
		if (pkg instanceof ConnectionError){
			String message = "Unknown connection error";
			switch (((ConnectionError) pkg).errorCode){
				case ConnectionError.LobbyIsFull: message = "Lobby is full"; break;
			}
			UI.systemMessage(message);
			GameClient.instance().getStateMachine().removeState();
		}
		if (pkg instanceof PlayerConnected){
			PlayerConnected msg = (PlayerConnected) pkg;
			Player player = msg.player;
			UI.systemMessage(("Player " + player.getNickname() + " connected"));
			PlayerManager.instance().register(player);
			npp.addPlayer(player);
		}

		if (pkg instanceof PlayersList) {
			PlayersList list = (PlayersList) pkg;
			PlayerManager.instance().registerAll(list.players);
			npp.addPlayers(
					PlayerManager.instance().getPlayersList()
			);
		}

		if (pkg instanceof PlayerDisconnected) {
			PlayerDisconnected msg = ((PlayerDisconnected) pkg);
			Player player = msg.player;
			PlayerManager.instance().disconnect(player.id);
			npp.removePlayer(player.id);
		}

		if (pkg instanceof PlayerReady) {
			PlayerReady msg = (PlayerReady) pkg;
			PlayerManager.instance().getPlayer(msg.playerID).setReady(msg.ready);
			npp.setPlayerReady(msg.playerID, msg.ready);
		}
		
		if (pkg instanceof SetFractions){
			SetFractions msg = ((SetFractions)pkg);
			PlayerManager.instance().initFractions(msg.fractions);
		}

		if (pkg instanceof Packages.SetTrack) {
			Packages.SetTrack msg = (Packages.SetTrack) pkg;
			Game.instance().getTrack(msg.track).setData(msg.data);
		}
	}
}
