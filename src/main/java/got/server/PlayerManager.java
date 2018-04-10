package got.server;

import java.util.concurrent.ConcurrentHashMap;

import got.gameStates.GameConfigState;
import got.model.Fraction;
import got.model.Game;
import got.model.Player;
import got.utils.Utils;

public class PlayerManager {
	private static PlayerManager _instance = null;
	public ConcurrentHashMap<Integer, Player> players = new ConcurrentHashMap<>();
	private int playersCount;
	private int maxPlayers = 6;
	private Player self;
	private Player currentPlayer;

	private PlayerManager() {
		playersCount = 0;
	}

	public static PlayerManager instance() {
		if (_instance == null) {
			_instance = new PlayerManager();
		}
		return _instance;
	}
	
	public boolean isLoggedIn(int id){
		return players.contains(id);
	}
	
	
	/**
	 * LogIn player on the server
	 * @param player
	 * @return
	 */
	public int LogIn(Player player){
		//if max players already logged in return -1
		if (playersCount == maxPlayers) return -1;
		
		//get free id
		int id = 0;
		for (id = 0; id<maxPlayers; id++){
			if (!players.containsKey(id))
				break;
		}
		
		//if all id already exists return -1
		if (id == maxPlayers) return -1;
		
		playersCount++;
		players.put(id, player);
		
		return id;
	}
	
	
	
	public static Player getSelf(){
		return instance().self;
	}
	
	/**
	 * Initialize client side player
	 * @param player - main player on client
	 */
	public void initPlayer(Player player){
		register(player);
		self = player;
	}
	
	/**
	 * register player on client
	 * @param player
	 */
	public void register(Player player){
		if (self!=null && player.id == self.id) return;
		playersCount++;
		players.put(player.id, player);
	}
	
	public void registerAll(Player[] list){
		for (Player pl:list){
			register(pl);
		}
	}
	
	public Player getPlayer(int id){
		return players.get(id);
	}
	
	public boolean disconnect(int id){
		if (!players.containsKey(id)) return false;
		
		players.remove(id);
		playersCount--;
		
		return true;
	}
	
	public int getPlayersCount(){
		return playersCount;
	}
	
	public Player[] getPlayersList(){
		return (Player[]) players.values().toArray(new Player[0]);
	}
	
	public Player getPlayerByFraction(Fraction fraction){
		for (Player pl: players.values()){
			if (pl.getFraction() == fraction)
				return pl;
		}
		return null;
	}
	
	public boolean isAllPlayersReady(){
		boolean ready = true;
		for (Player pl : players.values()){
			ready = ready && pl.isReady();
		}
		return ready;
	}
	
	public void initRandomFractions(){
		Fraction [] fractions = new Fraction[getPlayersCount()];
		Fraction [] pool = Game.instance().getFractionsPool();
		
		int count = getPlayersCount();
		for (int i=0; i<count; i++){
			fractions[i] = pool[i];
		}
		
		int step = 3;
		while (step-- > 0){
			for (int i=0; i<count-1; i++){
				if (Utils.chance(50)){
					Fraction t = fractions[i];
					fractions[i] = fractions[i+1];
					fractions[i+1] = t;
				}
			}
		}
		
		initFractions(fractions);
	}
	
	/**
	 * Set player fractions
	 * where player.fraction = fractions[player.id];
	 */
	public void initFractions(Fraction[] fractions){
		for (Player pl: players.values()){
			pl.setFraction(fractions[pl.id]);
		}
	}
	
	public Fraction[] getFractions(){
		Fraction [] result = new Fraction[getPlayersCount()];
		
		for (Player pl: players.values()){
			result[pl.id] = pl.getFraction();
		}
		
		return result;
	}

	@Override
	public String toString() {
		return "PlayerManager{" +
				"players=" + players +
				'}';
	}

	public void setCurrentPlayer(Player currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}
}
