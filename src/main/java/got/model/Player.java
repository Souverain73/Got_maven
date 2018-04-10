package got.model;

import got.Constants;
import got.gameObjects.MapPartObject;
import got.houseCards.Deck;

/**
 * Class contains all player data
 * @author Souverain73
 *
 */

public class Player {
		
	public int id;
	private Fraction fraction;
	private int specials;
	private int money;
	private int resouces;
	private String nickname;
	private boolean ready;
	private transient Deck deck;
	
	public Player() {
		nickname = "dumb";
		fraction = null;
		specials = 3;
		money = 5;
		ready = false;
		deck = null;
	}

	public int getSpecials() {
		return specials;
	}

	public void setSpecials(int specials) {
		this.specials = specials;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public int getResouces() {
		return resouces;
	}

	public void setResouces(int resouces) {
		this.resouces = resouces;
	}

	public Fraction getFraction() {
		return fraction;
	}

	public void setFraction(Fraction fraction) {
		this.fraction = fraction;
		//При смене фракции игроку надо дать новую колоду карт домов.
		//И для отладки дать ник в зависимости от фракции
		this.nickname = fraction.toString();
		this.deck = new Deck(this);
	}
	
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Deck getDeck() {
		return deck;
	}

	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}

	public void placePowerTokenAtRegion(MapPartObject region){
		if (money == 0) {
			throw new IllegalStateException("Player try place power token, but player have no power tokens");
		}
		region.placePowerToken();
		money--;
	}

	public void addMoney(int money){
		this.money+=money;
		if (this.money>Constants.MAX_MONEY){
			this.money = Constants.MAX_MONEY;
		}else if(this.money < 0){
			this.money = 0;
		}
	}

	@Override
	public String toString() {
		return String.format("Player [fraction=%s, specials=%s, money=%s, resouces=%s, ready=%s]", fraction, specials,
				money, resouces, ready);
	}

}
