package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

import got.model.ChangeAction;
import got.model.Game;
import got.model.Player;
import got.network.Packages;
import got.network.Packages.ConnectionError;
import got.network.Packages.InitPlayer;
import got.network.Packages.LogIn;
import got.network.Packages.PlayerConnected;
import got.network.Packages.PlayersList;
import got.network.Packages.Ready;
import got.network.Packages.PlayerReady;
import got.server.GameServer;
import got.server.GameServer.PlayerConnection;
import got.server.PlayerManager;
import got.server.serverStates.base.ServerState;
import got.utils.Timers;
import got.vesterosCards.Deck;
import got.vesterosCards.VesterosCards;

public class NetworkRoomState implements ServerState {
	private String name = "NetworkRoomState";
	private Server server;
	private Thread timerThread;
	private StateMachine stm;
	
	@Override
	public void recieve(Connection c, Object pkg) {
		PlayerConnection connection = ((PlayerConnection)c);
		Player player = connection.player;
		//if player logged in.
		if (pkg instanceof LogIn){
			Player pl = new Player();
			pl.setNickname(((LogIn)pkg).nickname);
			pl.id = PlayerManager.instance().LogIn(pl);
			if (pl.id == -1){
				connection.sendTCP(new ConnectionError(ConnectionError.LobbyIsFull));
				return;
			}
			
			connection.player = pl;
			
			//send response to player, and init player object;
			InitPlayer response = new InitPlayer();
			response.player = pl;
			connection.sendTCP(response);
			
			//send players list to new connected player
			connection.sendTCP(new PlayersList(PlayerManager.instance().getPlayersList()));
			
			//notify all players about new player
			PlayerConnected pc = new PlayerConnected();
			pc.player = response.player;
			server.sendToAllExceptTCP(connection.getID(), pc);
			Log.debug("Player:"+pl.getNickname()+" connected");
		}
		
		if (pkg instanceof Ready){
			Ready msg = ((Ready)pkg);
			player.setReady(msg.ready);
			server.sendToAllTCP(new PlayerReady(connection.player.id, msg.ready));
			
			if (PlayerManager.instance().isAllPlayersReady()){
				//Start game start countdown
				Timers.getCounter(1000,
					(time)->{
						server.sendToAllTCP(new Packages.ServerMessage(String.format("Game start in %d seconds", time/1000)));
					}, ()->{
							PlayerManager.instance().initRandomFractions();
							Game.instance().initDefaultTracks();

							GameServer.getServer().sendToAllTCP(new Packages.SetFractions(PlayerManager.instance().getFractions()));

							Game.instance().initVesterosDeck(0, new Deck(){{
//								addCard(VesterosCards.getCardByName("CollectUnits"));
//								addCard(VesterosCards.getCardByName("CollectUnits"));
//								addCard(VesterosCards.getCardByName("CollectUnits"));
//								addCard(VesterosCards.getCardByName("SummerTime1"));
//								addCard(VesterosCards.getCardByName("WinterTime1"));
								addCard(VesterosCards.getCardByName("ThroneOfSwords"));
								addCard(VesterosCards.getCardByName("ThroneOfSwords"));
//								addCard(VesterosCards.getCardByName("SuplyCard"));
//								addCard(VesterosCards.getCardByName("SuplyCard"));
//								addCard(VesterosCards.getCardByName("SuplyCard"));
							}});

							Game.instance().initVesterosDeck(1, new Deck(){{
								addCard(VesterosCards.getCardByName("WinterTime2"));
								addCard(VesterosCards.getCardByName("SummerTime2"));
								addCard(VesterosCards.getCardByName("BlackWings"));
								addCard(VesterosCards.getCardByName("BlackWings"));
//								addCard(VesterosCards.getCardByName("BattleOfKings"));
//								addCard(VesterosCards.getCardByName("BattleOfKings"));
//								addCard(VesterosCards.getCardByName("BattleOfKings"));
//								addCard(VesterosCards.getCardByName("GameOfThrones"));
//								addCard(VesterosCards.getCardByName("GameOfThrones"));
//								addCard(VesterosCards.getCardByName("GameOfThrones"));
							}});

							Game.instance().initVesterosDeck(2, new Deck(){{
//								addCard(VesterosCards.getCardByName("Wildlings"));
//								addCard(VesterosCards.getCardByName("Wildlings"));
//								addCard(VesterosCards.getCardByName("Wildlings"));
//								addCard(VesterosCards.getCardByName("AutumnRains"));
//								addCard(VesterosCards.getCardByName("FeastForCrows"));
//								addCard(VesterosCards.getCardByName("WebOfLie"));
//								addCard(VesterosCards.getCardByName("SeaOfStorms"));
								addCard(VesterosCards.getCardByName("PutToSword"));
								addCard(VesterosCards.getCardByName("PutToSword"));
//								addCard(VesterosCards.getCardByName("StormOfSwords"));
							}});

							GameServer.getServer().sendToAllTCP(new Packages.SetTrack(Game.THRONE_TRACK, Game.instance().getTrack(Game.THRONE_TRACK).getData()));
							GameServer.getServer().sendToAllTCP(new Packages.SetTrack(Game.SWORD_TRACK, Game.instance().getTrack(Game.SWORD_TRACK).getData()));
							GameServer.getServer().sendToAllTCP(new Packages.SetTrack(Game.CROWN_TRACK, Game.instance().getTrack(Game.CROWN_TRACK).getData()));

							connection.sendTCP(new Packages.ServerMessage("Start!!"));
							stm.changeState(new MainState(), ChangeAction.SET);
				}).start(false);
			}
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter(StateMachine stm) {
		this.stm = stm;
		server = GameServer.getServer();
	}

	@Override
	public void exit() {

	}

	@Override
	public int getID() {
		return 0;
	}

}
