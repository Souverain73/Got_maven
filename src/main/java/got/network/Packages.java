package got.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import got.gameObjects.battleDeck.BattleOverrides;
import got.model.*;

import java.util.Arrays;

public class Packages {
	private Packages() {
	}

	/**
	 * Method register all network packages. Must be used for server and client/
	 * 
	 * @param endpoint
	 */
	public static void register(EndPoint endpoint) {
		Kryo kryo = endpoint.getKryo();
		kryo.register(ServerMessage.class);
		kryo.register(LogIn.class);
		kryo.register(PlayersList.class);
		kryo.register(InitPlayer.class);
		kryo.register(ConnectionError.class);
		kryo.register(PlayerConnected.class);
		kryo.register(PlayerDisconnected.class);
		kryo.register(SetFractions.class);
		kryo.register(SetUnits.class);
		kryo.register(SetTrack.class);
		kryo.register(PlayerSetAction.class);
		kryo.register(PlayerReady.class);
		kryo.register(PlayerTurn.class);
		kryo.register(PlayerAct.class);
		kryo.register(PlayerSelectRegion.class);
		kryo.register(PlayerMove.class);
		kryo.register(OpenCard.class);
		kryo.register(PlayerSelectItem.class);
		kryo.register(PlayerBets.class);
		kryo.register(ChangeState.class);
		kryo.register(SetAction.class);
		kryo.register(Ready.class);
		kryo.register(Act.class);
		kryo.register(SelectRegion.class);
		kryo.register(Move.class);
		kryo.register(PlayerChangeUnits.class);
		kryo.register(ChangeUnits.class);
		kryo.register(CollectInfluence.class);
		kryo.register(PlayerCollectInfluence.class);
		kryo.register(SelectItem.class);
		kryo.register(Bet.class);
		kryo.register(BetsList.class);
		kryo.register(ResolvePosition.class);
		kryo.register(PlayerResolvePosition.class);
		kryo.register(GetGlobalState.class);
		kryo.register(ForceReadyAt.class);
		kryo.register(SetGlobalState.class);
		kryo.register(Attack.class);
		kryo.register(PlayerAttack.class);
		kryo.register(InitBattle.class);
		kryo.register(Help.class);
		kryo.register(PlayerHelp.class);
		kryo.register(PlayerDamage.class);
		kryo.register(GetBattleResult.class);
		kryo.register(BattleResult.class);
		kryo.register(StateReady.class);
		kryo.register(KillAllUnitsAtRegion.class);
		kryo.register(PlayerKillAllUnitsAtRegion.class);
		kryo.register(MoveAttackerToAttackRegion.class);
		kryo.register(LooserReady.class);
		kryo.register(PlacePowerToken.class);
		kryo.register(PlayerPlacePowerToken.class);
		kryo.register(SelectHouseCard.class);
		kryo.register(PlayerSelectHouseCard.class);
		kryo.register(WaitForModal.class);
		kryo.register(ResumeModal.class);
		kryo.register(PlayerResumeModal.class);
		kryo.register(Confirm.class);
		kryo.register(Cancel.class);
		kryo.register(PlayerConfirm.class);
		kryo.register(PlayerCancel.class);
		kryo.register(SetOverrides.class);
		kryo.register(PlayerSetOverrides.class);
		kryo.register(RemoveHouseCard.class);
		kryo.register(PlayerRemoveHouseCard.class);
		kryo.register(KillUnit.class);
		kryo.register(PlayerKillUnit.class);
		kryo.register(EndBattle.class);
		kryo.register(ChangeRegionFraction.class);
		kryo.register(ChangeSuply.class);
		kryo.register(PlayerChangeSuply.class);
		kryo.register(SetRestrictedActions.class);
		kryo.register(AuctionResult.class);
		kryo.register(WildlingsData.class);
	}

	public static class NetPackage {
		private NetPackage() {

		}
	}

	public static class BroadcastPackage extends NetPackage {
		private BroadcastPackage() {

		}
	}

	public static class ClientServerPackage extends NetPackage {
		private ClientServerPackage() {

		}
	}

	public static class ServerClientPackage extends NetPackage {
		private ServerClientPackage() {

		}
	}

	/**
	 * Если пакет должен передаваться в модальные состояния, он должен реализовывать этот интерфейс
	 */
	public interface ModalAcceptedPackage{

	}

	
	/**
	 * Запрос от клиента на подключение.
	 */
	public static class LogIn extends ClientServerPackage {
		public String nickname;

		public LogIn Nickname(String nickname) {
			this.nickname = nickname;
			return this;
		}
	}
	
	
	
	/**
	 *	Сообщение о ошибке подключения к сетевому лоби.
	 *	Используется для сообщения клиенту о занятости лобби или иных причинах, 
	 *	по которым подключение не возможно.
	 */
	public static class ConnectionError extends ServerClientPackage {
		public final static int LobbyIsFull = 1; 
		public int errorCode;
		public ConnectionError() {
		}
		public ConnectionError(int code){
			this.errorCode = code;
		}
	}
	
	/**
	 *	Передает клиенту список игроков.
	 *	Используется для получения информации о составе сетевого лоби для вновь присоединившихся игроков.
	 */
	public static class PlayersList extends ServerClientPackage {
		public Player [] players;
		public PlayersList() {
			players = new Player[6];
		}
		public PlayersList(Player[] list){
			players = list;
		}
	}

	
	/**
	 *	Пакет, представляющий собой сообщение от сервера клиенту.
	 *	Используется для передачи различных системных сообщений
	 */
	public static class ServerMessage extends ServerClientPackage {
		public String message;
		public ServerMessage(){};
		public ServerMessage(String message){
			this.message = message;
		}
	}

	/**
	 * Инициализирует модель игрока на клиенте
	 */
	public static class InitPlayer extends ServerClientPackage {
		public Player player;
	}

	/**
	 * Сообщает всем игрокам о подключении игрока
	 */
	public static class PlayerConnected extends BroadcastPackage {
		public Player player;
	}

	/**
	 * Сообзает всем игрокам о отключении игрока
	 */
	public static class PlayerDisconnected extends BroadcastPackage {
		public Player player;
		
		public PlayerDisconnected(){
			
		}
		
		public PlayerDisconnected(Player player) {
			this.player = player;
		}
	}

	/**
	 *	Пакет для передачи списка фракций. <br>
	 *	Порядок фракций в массиве определяет принадлежность игроков к фракциям.<br>
	 *	players[id].fraction = fractions[id];<br>
	 *	Используется для инициализации распределения игроков по фракциям в начале игры.
	 */
	public static class SetFractions extends BroadcastPackage {
		public Fraction[] fractions;
		public SetFractions() {}
		public SetFractions(Fraction[] fractions) {
			this.fractions = fractions;
		}
	}
	
	/**
	 * устанавливает набор юнитов на определенной територии.
	 */
	public static class SetUnits extends BroadcastPackage {
		int region;
		int units[];

		public SetUnits() {
			units = new int[4];
		}
	}

	/**
	 * устанавливает положение игроков на треках.
	 */
	public static class SetTrack extends BroadcastPackage {
		public int track;
		public Fraction [] data;

		public SetTrack() {
		}

		public SetTrack(int track, Fraction[] data) {
			this.track = track;
			this.data = data;
		}
	}

	/**
	 * устанавливает действие для региона.
	 */
	public static class PlayerSetAction extends BroadcastPackage {
		public int region;
		public Action action;
		public PlayerSetAction() {}
		public PlayerSetAction(int region, Action action) {
			this.region = region;
			this.action = action;
		}
		
		@Override
		public String toString() {
			return String.format("PlayerSetAction [region=%s, action=%s]", region, action);
		}
		
	}

	/**
	 * сообщает всем игрокам о готовности игрока player.
	 */
	public static class PlayerReady extends BroadcastPackage {
		public int playerID;
		public boolean ready;
		public PlayerReady() {
		}
		
		public PlayerReady(int playerID, boolean ready) {
			this.playerID = playerID;
			this.ready = ready;
		}
		
		@Override
		public String toString() {
			return String.format("PlayerReady [playerID=%s, ready=%s]", playerID, ready);
		}	
	}

	/**
	 * сообщает всем игрокам, что сейчас ход игрока player
	 */
	public static class PlayerTurn extends BroadcastPackage {
		public int playerID;

		public PlayerTurn() {}

		public PlayerTurn(int playerID) {
			this.playerID = playerID;
		}

		@Override
		public String toString() {
			return String.format("PlayerTurn [playerID=%s]", playerID);
		}
	}

	/**
	 * сообщает всем игрокам, что текущий игрок играет приказ с терриротии from
	 * на территорию to
	 */
	public static class PlayerAct extends BroadcastPackage {
		public int from;
		public int to;

		public PlayerAct() {}

		public PlayerAct(int from, int to) {
			this.from = from;
			this.to = to;
		}

		@Override
		public String toString() {
			return String.format("PlayerAct [from=%s, to=%s]", from, to);
		}
	}

	/**
	 * сообщеат всем игрокам, что текущий игрок выбрал регион region
	 */
	public static class PlayerSelectRegion extends BroadcastPackage {
		int region;

		public PlayerSelectRegion() {
		}
	}

	/**
	 * сообщает всем игрокам, что текущий игрок перемещает с терриротии from на
	 * территорию to юнитов units
	 */
	public static class PlayerMove extends BroadcastPackage {
		public int player;
		public int from;
		public int to;
		public Unit [] units;

		public PlayerMove() {}

		public PlayerMove(int player, int from, int to, Unit[] units) {
			this.player = player;
			this.from = from;
			this.to = to;
			this.units = units;
		}
	}

	/**
	 * сообщает игрокам, что открыта карта card из колоды number.
	 */
	public static class OpenCard extends BroadcastPackage {
		public int number;
		public int card;

		public OpenCard() {
		}

		public OpenCard(int number, int card) {
			this.number = number;
			this.card = card;
		}
	}

	/**
	 * сообщает игрокам, что была выбрана позиция select.
	 */
	public static class PlayerSelectItem extends BroadcastPackage {
		public int select;

		public PlayerSelectItem() {
		}

		public PlayerSelectItem(int select) {
			this.select = select;
		}
	}

	/**
	 * сообщает игрокам, какие ставки были сделаны.
	 */
	public static class PlayerBets extends BroadcastPackage {
		int bets[];

		public PlayerBets() {
			bets = new int[7];
		}
	}

	/**
	 * Передает информацию о смене фазы игры.
	 */
	public static class ChangeState extends BroadcastPackage {
		public int state;
		public ChangeAction action;

		public ChangeState() {}
		public ChangeState(int state, ChangeAction action) {
			this.state = state;
			this.action = action;
		}

		@Override
		public String toString() {
			return "ChangeState{" +
					"state=" + state +
					'}';
		}
	}

	/**
	 * устанавливает действие для региона.
	 */
	public static class SetAction extends ClientServerPackage {
		public int region;
		public Action action;
		public SetAction() {}
		public SetAction(int region, Action action) {
			this.region = region;
			this.action = action;
		}
		
	}

	/**
	 * Устанавливает готовность клиента к завершению фазы или хода.
	 */
	public static class Ready extends ClientServerPackage {
		public boolean ready;
		
		public Ready() {
		}
		public Ready(boolean ready){
			this.ready = ready;
		}
		
		@Override
		public String toString() {
			return String.format("Ready [ready=%s]", ready);
		}
	}

	/**
	 * Сообщает серверу, что клиент готов к смене состояния.
	 */

	public static class StateReady extends ClientServerPackage {
	}

	/**
	 * сообщает серверу, что игрок играет приказ с территории from на территорию
	 * to
	 */
	public static class Act extends ClientServerPackage {
		public int from;
		public int to;

		public Act() {}

		public Act(int from, int to) {
			this.from = from;
			this.to = to;
		}
		
	}

	/**
	 * сообщает серверу, что игрок выбрал регион region
	 */
	public static class SelectRegion extends ClientServerPackage {
		int region;

		public SelectRegion() {
		}
	}

	/**
	 * сообщает серверу, что игрок перемещает с территории from на территорию to
	 * юнитов units
	 */
	public static class Move extends ClientServerPackage {
		public int from;
		public int to;
		public Unit[] units;

		public Move() {	}

		public Move(int from, int to, Unit[] units) {
			this.from = from;
			this.to = to;
			this.units = units;
		}
	}

	/**
	 * сообщает серверу, что игрок набирает войска в регионе и передает новый
	 * набор юнитов.
	 */
	public static class ChangeUnits extends ClientServerPackage {
		public int region;
		public Unit[] units;

		public ChangeUnits() {
			units = new Unit[4];
		}

		public ChangeUnits(int region, Unit[] units) {
			this.region = region;
			this.units = units;
		}

		@Override
		public String toString() {
			return String.format("ChangeUnits [region=%s, units=%s]", region, Arrays.toString(units));
		}
	}

	/**
	 * Сообщает клиентам, что игрок изменил состав юнитов в регионе.
	 */
	public static class PlayerChangeUnits extends ServerClientPackage {
		public int player;
		public int region;
		public Unit[] units;

		PlayerChangeUnits(){};

		public PlayerChangeUnits(int player, int region, Unit[] units) {
			this.player = player;
			this.region = region;
			this.units = units;
		}
	}

	/**
	 * сообщает серверу, что игрок собирает очки влияния с региона.
	 */
	public static class CollectInfluence extends ClientServerPackage {
		public int region;
		public int count;

		public CollectInfluence() {
		}

		public CollectInfluence(int region) {
			this.region = region;
		}

		public CollectInfluence(int region, int count) {
			this.region = region;
			this.count = count;
		}
	}

	public static class PlayerCollectInfluence extends BroadcastPackage {
		public int region;
		public int count;
		public int player;

		public PlayerCollectInfluence() {
		}

		public PlayerCollectInfluence(int region, int count, int player) {
			this.region = region;
			this.count = count;
			this.player = player;
		}
	}

	/**
	* 
	*/
	public static class SelectItem extends ClientServerPackage {
		public int select;

		public SelectItem() {
		}

		public SelectItem(int select) {
			this.select = select;
		}
	}

	/**
	 * сообщает серверу, что игрок ставит value очков влияния.
	 */
	public static class Bet extends ClientServerPackage {
		public int value;

		public Bet() {
		}

		public Bet(int value) {
			this.value = value;
		}
	}

	public static class BetsList extends BroadcastPackage {
		public int[] bets;

		public BetsList() {
		}

		public BetsList(int[] bets) {
			this.bets = bets;
		}
	}

	/**
	 * запрашивает у сервера информацию о текущем состоянии. Используется для
	 * восстановления при сбоях.
	 */
	public static class GetGlobalState extends ClientServerPackage {
		public GetGlobalState() {
		}
	}

	/**
	 * сообщает клиенту что через seconds секунд, готовность игрока будет
	 * проставлена автоматически.
	 */
	public static class ForceReadyAt extends ServerClientPackage {
		public int seconds;

		public ForceReadyAt(int seconds) {
			this.seconds = seconds;
		}

		public ForceReadyAt() {
		}
	}

	/**
	 * передает всю информацию о текущем состоянии игры. Используется для
	 * восстановления разорванных игровых сессий и синхронизации после сбоя.
	 */
	public static class SetGlobalState extends ServerClientPackage {
		int stateData;

		public SetGlobalState() {
		}
	}


	public static class Attack extends ClientServerPackage {
		public int from;
		public int to;
		public int attackerId;
		public int defenderId;

		public Attack(){}

		public Attack(int from, int to, int attackerId, int defenderId) {
			this.from = from;
			this.to = to;
			this.attackerId = attackerId;
			this.defenderId = defenderId;
		}
	}


	public static class PlayerAttack extends ServerClientPackage {
		public int palyer;
		public int from;
		public int to;

		public PlayerAttack(){}

		public PlayerAttack(int palyer, int from, int to){
			this.palyer = palyer;
			this.from = from;
			this.to = to;
		}
	}

	public static class InitBattle extends ServerClientPackage{
		public int from;
		public int to;

		public InitBattle(){

		}

		public InitBattle(int from, int to) {
			this.from = from;
			this.to = to;
		}
	}

	/**
	 * Сообщает серверу о характеристики игроков по результатам боя
	 */
	public static class PlayerDamage extends ClientServerPackage{
		public int attackerDamage;
		public int defenderDamage;

		public PlayerDamage() {
		}

		public PlayerDamage(int attackerDamage, int defenderDamage) {
			this.attackerDamage = attackerDamage;
			this.defenderDamage = defenderDamage;
		}

		@Override
		public String toString() {
			return "PlayerDamage{" +
					"attackerDamage=" + attackerDamage +
					", defenderDamage=" + defenderDamage +
					'}';
		}
	}

	/**
	 * Запрос информации о результатах боя от клиентов
	 */
	public static class GetBattleResult extends ServerClientPackage{
	}

	/**
	 * Сообщает игрокам результаты боя.
	 */
	public static class BattleResult extends ServerClientPackage {
		public int winnerID;
		public int looserID;
		public int winnerRegionID;
		public int looserRegionID;
		public int killUnits;

		public BattleResult(int winnerID, int looserID, int winnerRegionID, int looserRegionID, int killUnits) {
			this.winnerID = winnerID;
			this.looserID = looserID;
			this.winnerRegionID = winnerRegionID;
			this.looserRegionID = looserRegionID;
			this.killUnits = killUnits;
		}

		public BattleResult() {
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			BattleResult that = (BattleResult) o;

			if (winnerID != that.winnerID) return false;
			if (looserID != that.looserID) return false;
			return killUnits == that.killUnits;

		}
	}

	/**
	 * Пакет сообщает серверу, что проигравший игрок закончил убийство юнитов и отступление.
	 */
	public static class LooserReady extends ClientServerPackage{

	}

	public static class Help extends ClientServerPackage{
		public static final int SIDE_NONE = 0;
		public static final int SIDE_ATTACKER = 1;
		public static final int SIDE_DEFENDER = 2;
		public int side;

		public Help(){};

		public Help(int side){
			this.side = side;
		}
	}

	public static class PlayerHelp extends ServerClientPackage{
		public static final int SIDE_NONE = Help.SIDE_NONE;
		public static final int SIDE_ATTACKER = Help.SIDE_ATTACKER;
		public static final int SIDE_DEFENDER = Help.SIDE_DEFENDER;

		public int player;
		public int side;

		public PlayerHelp(){}

		public PlayerHelp(int player, int side){
			this.player = player;
			this.side = side;
		}
	}

	public static class KillAllUnitsAtRegion extends ClientServerPackage{
		public int regionID;

		public KillAllUnitsAtRegion(int regionID) {
			this.regionID = regionID;
		}

		public KillAllUnitsAtRegion() {
		}
	}

	public static class PlayerKillAllUnitsAtRegion extends BroadcastPackage{
		public int player;
		public int regionID;

		public PlayerKillAllUnitsAtRegion(int player, int regionID) {
			this.player = player;
			this.regionID = regionID;
		}

		public PlayerKillAllUnitsAtRegion() {
		}
	}

	public static class MoveAttackerToAttackRegion extends BroadcastPackage{
	}

	public static class PlacePowerToken extends ClientServerPackage{
		public int regionId;

		public PlacePowerToken(int regionId) {
			this.regionId = regionId;
		}

		public PlacePowerToken() {
		}
	}

	public static class PlayerPlacePowerToken extends BroadcastPackage{
		public int playerId;
		public int regionId;

		public PlayerPlacePowerToken(int playerId, int regionId) {
			this.playerId = playerId;
			this.regionId = regionId;
		}

		public PlayerPlacePowerToken() {
		}
	}

	public static class SelectHouseCard extends ClientServerPackage{
		public int card;

		public SelectHouseCard(int card) {
			this.card = card;
		}

		public SelectHouseCard() {
		}
	}

	public static class PlayerSelectHouseCard extends BroadcastPackage{
		public int player;
		public int card;

		public PlayerSelectHouseCard(int player, int card) {
			this.player = player;
			this.card = card;
		}

		public PlayerSelectHouseCard() {
		}
	}

	public static class WaitForModal extends ClientServerPackage{
	}

	public static class ResumeModal extends ClientServerPackage {
	}

	public static class PlayerResumeModal extends BroadcastPackage implements ModalAcceptedPackage{
		public int player;

		public PlayerResumeModal(int player) {
			this.player = player;
		}

		public PlayerResumeModal() {
		}
	}

	public static class Confirm extends ClientServerPackage{
	}

	public static class PlayerConfirm extends BroadcastPackage implements ModalAcceptedPackage{
		public int player;

		public PlayerConfirm(int player) {
			this.player = player;
		}

		public PlayerConfirm() {
		}
	}

	public static class Cancel extends ClientServerPackage{
	}

	public static class PlayerCancel extends BroadcastPackage implements ModalAcceptedPackage{
		int player;
		public PlayerCancel() {
		}

		public PlayerCancel(int player) {
			this.player = player;
		}
	}


	public static class SetOverrides extends ClientServerPackage {
		public BattleOverrides overrides;

		public SetOverrides() {
		}

		public SetOverrides(BattleOverrides overrides) {
			this.overrides = overrides;
		}
	}

	public static class PlayerSetOverrides extends BroadcastPackage {
		public BattleOverrides overrides;
		public int player;

		public PlayerSetOverrides(BattleOverrides overrides, int player) {
			this.overrides = overrides;
			this.player = player;
		}

		public PlayerSetOverrides() {
		}
	}

	public static class RemoveHouseCard extends ClientServerPackage{
		public int target;
		public int houseCardID;

		public RemoveHouseCard(int target, int houseCardID) {
			this.target = target;
			this.houseCardID = houseCardID;
		}

		public RemoveHouseCard() {
		}
	}

	public static class PlayerRemoveHouseCard extends BroadcastPackage {
		public int source;
		public int target;
		public int houseCardID;

		public PlayerRemoveHouseCard(int source, int target, int houseCardID) {
			this.source = source;
			this.target = target;
			this.houseCardID = houseCardID;
		}

		public PlayerRemoveHouseCard() {
		}
	}

	public static class KillUnit extends ClientServerPackage{
		public int region;
		public Unit unit;

		public KillUnit(int region, Unit unit) {
			this.region = region;
			this.unit = unit;
		}

		public KillUnit() {
		}
	}

	public static class PlayerKillUnit extends BroadcastPackage{
		public int player;
		public int region;
		public Unit unit;

		public PlayerKillUnit(int player, int region, Unit unit) {
			this.player = player;
			this.region = region;
			this.unit = unit;
		}

		public PlayerKillUnit() {
		}
	}

    public static class EndBattle extends BroadcastPackage{

    }

    public static class ChangeRegionFraction extends BroadcastPackage{
		public int region;
		public Fraction fraction;

		public ChangeRegionFraction() {
		}

		public ChangeRegionFraction(int region, Fraction fraction) {
			this.region = region;
			this.fraction = fraction;
		}

		@Override
		public String toString() {
			return "ChangeRegionFraction{" +
					"region=" + region +
					", fraction=" + fraction +
					'}';
		}
	}

    public static class PlayerChangeSuply extends BroadcastPackage{
		public Fraction fraction;
		public int level;

		public PlayerChangeSuply() {
		}

		public PlayerChangeSuply(Fraction fraction, int level) {
			this.fraction = fraction;
			this.level = level;
		}
	}

	public static class ChangeSuply extends ClientServerPackage{
		public int level;

		public ChangeSuply() {
		}

		public ChangeSuply(int level) {
			this.level = level;
		}
	}

	public static class SetRestrictedActions extends BroadcastPackage{
		public Action[] actions;

		public SetRestrictedActions() {
		}

		public SetRestrictedActions(Action[] actions) {
			this.actions = actions;
		}
	}

	public static class ResolvePosition extends ClientServerPackage{
		public int position;
		public Fraction fraction;

		public ResolvePosition() {
		}

		public ResolvePosition(int position, Fraction fraction) {
			this.position = position;
			this.fraction = fraction;
		}
	}

	public static class PlayerResolvePosition extends BroadcastPackage{
		public int player;
		public int position;
		public Fraction fraction;

		public PlayerResolvePosition() {
		}

		public PlayerResolvePosition(int player, int position, Fraction fraction) {
			this.player = player;
			this.position = position;
			this.fraction = fraction;
		}
	}

    public static class AuctionResult extends ClientServerPackage {
		public Fraction[] result;

		public AuctionResult() {
		}

		public AuctionResult(Fraction[] result) {
			this.result = result;
		}
	}

	public static class WildlingsData extends BroadcastPackage {
		public Fraction actor;
		public boolean victory;
		public int card;
		public int maxBet;

		public WildlingsData() {
		}

		public WildlingsData(int card, Fraction actor, boolean victory, int maxBet) {
			this.actor = actor;
			this.victory = victory;
			this.card = card;
			this.maxBet = maxBet;
		}

		@Override
		public String toString() {
			return "WildlingsData{" +
					"actor=" + actor +
					", victory=" + victory +
					", card=" + card +
					", maxBet=" + maxBet +
					'}';
		}
	}
}