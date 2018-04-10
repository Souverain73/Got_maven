package got.gameStates;

import got.server.serverStates.base.ServerState;
import got.vesterosCards.states.BattleOfKings;
import got.vesterosCards.states.CollectInfluence;
import got.vesterosCards.states.CollectSuply;
import got.vesterosCards.states.CollectUnits;
import got.wildlings.states.HordeLoose;
import got.wildlings.states.HordeVictory;
import got.wildlings.states.RidersLoose;
import got.wildlings.states.WildlingsAttack;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *	All states that can be changed through network must have an ID 
 *
 */
public class StateID {
	public static final int MAIN_STATE = 16;
	public static final int GAME_CONFIG_STATE = 17;
	public static final int PLANNING_PHASE = 1;
	public static final int FIRE_PHASE = 3;
	public static final int MOVE_PHASE = 4;
	public static final int POWER_PHASE = 5;
	public static final int VESTEROS_PHASE = 6;
	public static final int BATTLE_PHASE = 7;
	public static final int HELP_PHASE = 8;
	public static final int BATTLE_RESULT_PHASE = 9;
	public static final int SELECT_HOUSE_CARD_PHASE = 10;
	public static final int PLAY_VESTEROS_STATE = 18;
	public static final int VESTEROS_COLLECT_UNITS = 19;
	public static final int VESTEROS_COLLECT_INFLUENCE = 20;
	public static final int WESTEROS_COLLECT_SUPLY = 21;
	public static final int AUCTION_STATE = 22;
	public static final int WESTEROS_BATTLE_OF_KINGS = 23;
	public static final int CHANGE_TRACK = 24;
	public static final int WILDLINGS_ATTACK = 25;
	public static final int WILDLINGS_HORDE_VICTORY = 26;
	public static final int WILDLINGS_HORDE_LOOSE = 27;
	public static final int WILDLINGS_RIDERS_LOOSE = 28;

    public static GameState getGameStateByID(int id){
		switch(id){
			case MAIN_STATE: return new MainState();
			case PLANNING_PHASE: return new PlanningPhase();
			case FIRE_PHASE: return new FirePhase();
			case MOVE_PHASE: return new MovePhase();
			case POWER_PHASE: return new PowerPhase();
			case VESTEROS_PHASE: return new VesterosPhase();
			case BATTLE_PHASE: return null;
			case HELP_PHASE: return new HelpPhase();
			case BATTLE_RESULT_PHASE: return new BattleResultState();
			case SELECT_HOUSE_CARD_PHASE: return new SelectHouseCardPhase();
			case GAME_CONFIG_STATE: return new GameConfigState();
			case PLAY_VESTEROS_STATE: return new PlayVesterosCard();
			case VESTEROS_COLLECT_UNITS: return new CollectUnits.ClientState();
			case VESTEROS_COLLECT_INFLUENCE: return new CollectInfluence.ClientState();
			case WESTEROS_COLLECT_SUPLY: return new CollectSuply.ClientState();
			case AUCTION_STATE: return new AuctionGameState();
			case WESTEROS_BATTLE_OF_KINGS: return new BattleOfKings.ClientState();
			case CHANGE_TRACK: return new ChangeTrackState();
			case WILDLINGS_ATTACK: return new WildlingsAttack.ClientState();
			case WILDLINGS_HORDE_LOOSE: return new HordeLoose.ClientState();
			case WILDLINGS_HORDE_VICTORY: return new HordeVictory.ClientState();
			case WILDLINGS_RIDERS_LOOSE: return new RidersLoose.ClientState();
		}
		return null;
	}
	
	public static ServerState getServerStateByID(int id){
		throw new NotImplementedException();
	}
}
