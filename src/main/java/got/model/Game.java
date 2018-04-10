package got.model;

import got.network.Packages;
import got.server.PlayerManager;
import got.vesterosCards.Deck;
import got.vesterosCards.VesterosCard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Souverain73 This class handles all Game data like tracks, provision,
 *         etc.
 */

public class Game {
	private static Game _instance = null;
	private int turn;
	private Deck[] vesterosDecks;
	private Fraction[] fractionsPool;
	private Track[] tracks;
	private SuplyTrack suplyTrack;

	public static final int THRONE_TRACK = 0;
	public static final int SWORD_TRACK = 1;
	public static final int CROWN_TRACK = 2;

	private Game() {
		tracks = new Track[3];
		tracks[THRONE_TRACK] = new Track(THRONE_TRACK, "THRONE", null);
		tracks[CROWN_TRACK] = new Track(CROWN_TRACK, "CROWN", null);
		tracks[SWORD_TRACK] = new Track(SWORD_TRACK, "SWORD", null);
		vesterosDecks = new Deck[3];
		suplyTrack = new SuplyTrack();
	}

	public static Game instance() {
		if (_instance == null) {
			_instance = new Game();
		}
		return _instance;
	}


	public void initDefaultTracks(){
		Fraction[] data = PlayerManager.instance().getFractions();
		getTrack(THRONE_TRACK).setData(data);
		getTrack(CROWN_TRACK).setData(data);
		getTrack(SWORD_TRACK).setData(data);
		suplyTrack.addFractions(data);
	}

	public Track getTrack(int id){
		if (id < 0 || id > 2) return null;
		return tracks[id];
	}

	public void setTrackData(int id, Fraction[] data){
		getTrack(id).setData(data);
	}

	public SuplyTrack getSuplyTrack() {
		return suplyTrack;
	}

	public void setFractionsPool(Fraction[] fractionsPool) {
		if (fractionsPool.length < Fraction.values().length){
			List<Fraction> fp = new ArrayList<>();
			fp.addAll(Arrays.asList(fractionsPool));
			for (Fraction f : Fraction.values()){
				if (!fp.contains(f))
					fp.add(f);
			}
			fractionsPool = fp.toArray(new Fraction[0]);
		}
		this.fractionsPool = fractionsPool;
	}

	public Fraction[] getFractionsPool() {
		if (fractionsPool == null){
			return Fraction.values();
		}

		return fractionsPool;
	}

	public void initVesterosDeck(int number, Deck deck){
		deck.shuffle();
		vesterosDecks[number] = deck;
	}

	public Deck getVesterosDeck(int number){
		return vesterosDecks[number];
	}

	public int getTurn(){
		return turn;
	}

	public void nextTurn(){
		turn += 1;
	}
}
