package got.gameObjects;

import java.util.*;
import java.util.stream.Collectors;

import got.gameObjects.interfaceControls.AbstractButtonObject;
import got.gameStates.PlanningPhase;
import got.graphics.TextureManager;
import got.model.*;
import got.server.PlayerManager;
import org.joml.Vector2f;
import org.joml.Vector3f;

import got.Constants;
import got.InputManager;
import got.gameStates.GameState;
import got.graphics.Effect;
import got.graphics.GraphicModule;
import got.graphics.Texture;
import got.utils.LoaderParams;
import got.utils.Utils;

/**
 * Handling all map region data, and region interactions.<br>
 * Also extends {@link AbstractButtonObject}.
 * @author Souverain73
 *
 */
public class MapPartObject extends AbstractButtonObject<MapPartObject> {


	private String textureName;

	public enum RegionType{	GROUND, SEA, PORT;}

	@Override
	protected MapPartObject getThis() {
		return this;
	}

	protected Fraction fraction = Fraction.NONE;

	private int ID = 0;

	private RegionType type;

	private String name;

	private int resourcesCount;

	private int influencePoints;
	private int buildingLevel;

	protected Texture texture;


	protected List<MapPartObject>  neighbors;

	protected int w, h;
	protected int act_x, act_y;
	protected int unit_x, unit_y;
	protected int token_x, token_y;
	protected Action action;
	protected List<UnitObject> units;
	protected boolean powerToken;

	public MapPartObject() {
		super(0);
		neighbors = new ArrayList<>();
		units = new ArrayList<>();
	}

	@Override
	public boolean init(LoaderParams params) {
		name = (String)params.get("name");
		ID = (Integer)params.get("id");
		resourcesCount = (Integer)params.get("resources");
		influencePoints = (Integer)params.get("influence");
		buildingLevel = (Integer)params.get("building");
		textureName = (String) params.get("texture");
		pos.x = (Integer)params.get("x");
		pos.y = (Integer)params.get("y");
		act_x = (Integer)params.get("action_x");
		act_y = (Integer)params.get("action_y");
		unit_x = (Integer)params.get("unit_x");
		unit_y = (Integer)params.get("unit_y");
		type = RegionType.valueOf((String)params.get("type"));
		try{
			token_x = (Integer)params.get("token_x");
			token_y = (Integer)params.get("token_y");;
		}catch(NullPointerException e){
			token_x = w/2;
			token_y = h/2;
		}
		try {
			setAction(Action.valueOf((String)params.get("action")));
		} catch (Exception e) {}
		try{
			fraction = Fraction.valueOf((String)params.get("fraction"));
		}catch(Exception e){}
		placeUnits();

		return false;
	}
	public int getID(){
		return ID;
	}
	@Override
	public void draw(GameState st) {
		if (!isVisible()) return;
		if (texture == null){
			initTexture();
		}
		GraphicModule.instance().setDrawSpace(this.space);
		if (state == State.FREE){

		}else if (state == State.HOVER){
			GraphicModule.instance().setEffect(
					new Effect().Overlay(new Vector3f(0.0f, 0.2f, 0.0f))
			);
		}else if (state == State.DOWN){
			GraphicModule.instance().setEffect(
					 new Effect().Overlay(new Vector3f(0.2f, 0.0f, 0.0f))
			);
		}

		if (state == State.DISABLED){
			GraphicModule.instance().setEffect(
					new Effect().Overlay(new Vector3f(-0.2f, -0.2f, -0.2f))
			);
		}

		Vector2f cp = getAbsolutePos();
		texture.draw(cp.x, cp.y, w, h, 0);
		GraphicModule.instance().resetEffect();
		float actImgSize = Constants.ACTION_IMAGE_SIZE;
		float halfActImgSize = Constants.ACTION_IMAGE_SIZE/2;
		float tokenImageSize = Constants.POWER_TOKEN_IMAGE_SIZE;
		if (st instanceof PlanningPhase && action != null && fraction != PlayerManager.getSelf().getFraction()) {
			//А тут значит задник.
			fraction.getBackTexture().draw(cp.x + act_x - halfActImgSize, cp.y + act_y - halfActImgSize,
					actImgSize, actImgSize);
		}else{
			if (action != null){
				action.getTexture().draw(cp.x + act_x - halfActImgSize, cp.y + act_y -halfActImgSize,
						actImgSize, actImgSize);
			}
		}

		GraphicModule.instance().setEffect(
				new Effect().Multiply(fraction.getMultiplyColor())
		);
		if (units != null){
			units.forEach(unit->unit.draw(st));
		}

		if (powerToken){
			PowerToken.getTexture().draw(cp.x + token_x, cp.y + token_y,
					tokenImageSize, tokenImageSize);
		}
		GraphicModule.instance().resetEffect();
		super.draw(st);
	}

	protected void initTexture() {
		texture = TextureManager.instance().loadTexture(textureName);
		w = texture.getWidth();
		h = texture.getHeight();
	}

	public List<MapPartObject> getNeighbors(){
		return neighbors;
	}

	public List<MapPartObject> getRegionsToMove(){
		return getRegionsToMove(this.getFraction());
	}

	public List<MapPartObject> getRegionsToMove(Fraction fraction){
		PathFinder pf = new PathFinder(fraction);
		List<MapPartObject> result = pf.getRegionsToMoveFrom(this);
		if (type == RegionType.GROUND){
			return result
					.stream()
					.filter(reg->reg.type == RegionType.GROUND)
					.collect(Collectors.toList());
		}else{
			return result
					.stream()
					.filter(reg->reg.type != RegionType.GROUND)
					.collect(Collectors.toList());
		}
	}
	public Unit[] getUnitsForHelp(Fraction helperFraction){
		return getRegionsForHelp(helperFraction).stream()
				.flatMap(reg->reg.units.stream())
				.map(uo->uo.getType()).toArray(Unit[]::new);
	}

	public List<MapPartObject> getRegionsForHelp(Fraction helperFraction){
		return getNeighbors().stream()
				//фильтруем по фракции
				.filter(region->region.getFraction() == helperFraction)
				//фильтруем по приказу
				.filter(region->region.getAction() == Action.HELP || region.getAction() == Action.HELPPLUS)
				.collect(Collectors.toList());
	}

	public int getBattlePowerForHelpers(Fraction helperFraction){
		return getRegionsForHelp(helperFraction).stream()
				//считаем силу
				.mapToInt(region->{
					int power = Arrays.stream(region.getUnits())
							.mapToInt(Unit::getDamage)
							.sum();

					if (region.getAction() == Action.HELPPLUS) power++;
					return power;
				}).sum();
	}

	public boolean canHelp(Fraction fraction){
		return getNeighbors().stream()
				.anyMatch(obj->
				obj.getFraction() == fraction
				&& (obj.getAction() == Action.HELPPLUS
				|| obj.getAction() == Action.HELP));
	}

	public String getName(){
		return name;
	}

	//Метод добавляющий соседа.
	public void addNeighbor(MapPartObject neighbor){
		if (neighbors.indexOf(neighbor)!=-1)
			//если сосед уже есть то делать ничего не надо
			return;

		//Если соседа нет, дбавляем его к соседям
		neighbors.add(neighbor);
		//Добавляем обратную связь
		neighbor.addNeighbor(this);
	}

	public void addUnit(UnitObject unit){
		if (units.size()>=4){
			System.out.println("Can't add more than 4 units for one region");
			return;
		}
		units.add(unit);
		unit.setVisible(true);
		placeUnits();
	}

	public void setUnits(Unit[] units){
		this.units.forEach(obj -> obj.finish());
		this.units.clear();

		for(Unit unit: units){
			this.units.add(new UnitObject(unit));
		}
		updateUnits();
	}

	public List<MapPartObject> getRegionsForHire(){
		List<MapPartObject> result = getNeighbors().stream().filter(obj->
				((obj.getType() == RegionType.SEA || obj.getType() == RegionType.PORT) &&
						((obj.getFraction() == PlayerManager.getSelf().getFraction())
							|| obj.getFraction() == Fraction.NONE))
				|| (obj == this)
		).collect(Collectors.toList());
		result.add(this);
		return result;
	}

	public void updateUnits(){
		placeUnits();
	}

	private void placeUnits(){
		Vector2f cp = getAbsolutePos();
		float x = cp.x+unit_x;
		float y = cp.y+unit_y;
		float angle = 0;
		float radius = Constants.UNIT_SIZE*Constants.UNIT_SCALE*0.7f;
		float step;

		if (units.size() == 0) return;

		if (units.size() == 1){
			units.get(0).setPos(new Vector2f(x,y));
		}else{
			switch(units.size()){
			case 2: angle = 0; break;
			case 3: angle = -90; break;
			case 4: angle = 45; break;
			}
			angle = (float)Math.toRadians(angle);
			step = (float)(360.0f/units.size() * Math.PI/180.0f);
			for(int i=0; i<units.size(); i++){
				units.get(i).setPos(new Vector2f((float)(x+Math.cos(angle)*radius), (float)(y+Math.sin(angle)*radius)));
				angle+=step;
			}
		}
	}


	public void setAction(Action act){
		action = act;
	}

	@Override
	public boolean ifMouseIn(Vector2f mousePos) {
		if (Utils.pointInRect(InputManager.instance().getMousePosWorld(), getAbsolutePos(), new Vector2f(w,h))){
			Vector2f worldPos = InputManager.instance().getMousePosWorld();
			Vector2f cp = getAbsolutePos();
			Vector2f modPos = new Vector2f(worldPos.x-cp.x, worldPos.y-cp.y);
			if (texture.getAlfa(modPos.x/w, modPos.y/h) != 0){
				return true;
			}
		}
		return false;
	}

	public int getHirePoints(){
		return buildingLevel;
	}

	public Unit[] getUnits() {
		return units.stream().map(obj -> obj.getType()).toArray(Unit[]::new);
	}

	public boolean addUnit(Unit unit){
		if (units.size() >= 4) return false;

		units.add(new UnitObject(unit));
		updateUnits();
		return true;
	}

	public boolean addUnits(Unit[] units){
		for (Unit unit: units){
			addUnit(unit);
		}
		updateUnits();
		return true;
	}

	public boolean removeUnit(Unit unit){
		if (units.isEmpty()) return false;

		Iterator<UnitObject> iterator = units.iterator();
		while (iterator.hasNext()) {
			UnitObject next =  iterator.next();
			if (next.getType() == unit){
				next.finish();
				iterator.remove();
				updateUnits();
				return true;
			}
		}
		return false;
	}

	public boolean removeUnits(Unit[] units){
		for (Unit unit: units){
			removeUnit(unit);
		}
		updateUnits();
		return true;
	}

	public void removeAllUnits(){
		removeUnits(getUnits());
	}

	public void killUnits() {
		setUnits(Arrays.stream(getUnits()).map(Unit::getKilled).toArray(Unit[]::new));
	}

	public void resurectUnits() {
		setUnits(Arrays.stream(getUnits()).map(Unit::getAlive).toArray(Unit[]::new));
	}


	/**
	 * Нужен для обратной совместимости, в идеале надо бы это переписать.
	 * @return - возвращает список юнитов в виде игровых объектов
	 */
	public List<UnitObject> getUnitObjects(){
		return units;
	}

	@Override
	protected void click(GameState st) {
		super.click(st);
	}

	public Action getAction() {
		return action;
	}

	public void hideUnits(){
		units.forEach(unit->unit.setVisible(false));
	}


	public void showUnits(){
		units.forEach(unit->unit.setVisible(true));
	}

	public RegionType getType() {
		return type;
	}

	public Fraction getFraction() {
		return fraction;
	}

	public void setFraction(Fraction fraction) {
		this.fraction = fraction;
	}

	public int getBuildingLevel() {
		return buildingLevel;
	}

	public int getInfluencePoints() {
		return influencePoints;
	}

	public int getResourcesCount() {
		return resourcesCount;
	}

	public int getUnitsCount(){

		return units.size();
	}

	public Player getOwnerPlayer() {
		return PlayerManager.instance().getPlayerByFraction(fraction);
	}

	@Override
	public String toString() {
		return name;
	}

	public boolean havePowerToket() {
		return powerToken;
	}

	public void placePowerToken() {
		powerToken = true;
	}

	public void removePowerToken(){
		powerToken = false;
	}

	public class PathFinder{


		Fraction playerFraction;
		HashSet<MapPartObject> visited = new HashSet<>();
		boolean start = true;
		public PathFinder(Fraction playerFraction) {
			this.playerFraction = playerFraction;
		}

		public List<MapPartObject> getRegionsToMoveFrom(MapPartObject from){
			List<MapPartObject> result = __getRegionsToMoveFrom(from);
			result.remove(from);
			return result;
		}

		private  List<MapPartObject> __getRegionsToMoveFrom(MapPartObject from){
			List<MapPartObject> result = new ArrayList<>();
			result.add(from);
			visited.add(from);

			if (!canGo(from) && !start) {
				return result;
			}
			start = false;

			for (MapPartObject region: from.getNeighbors()){
				if (!visited.contains(region)){
					result.addAll(__getRegionsToMoveFrom(region));
				}
			}

			return result;
		}

		private boolean canGo(MapPartObject from){
			return (from.getFraction() == playerFraction && from.type == RegionType.SEA);
		}
	}

	public boolean isEnabled(){
		return state != State.DISABLED;
	}
}
