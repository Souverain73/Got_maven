package got.gameObjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilderFactory;

import got.model.Action;
import got.model.Fraction;
import org.w3c.dom.*;

import got.gameStates.GameState;
import got.model.Unit;
import got.utils.LoaderParams;


/**
 * Game object represents game map.
 * Used for load and store map regions.
 * @author Souverain73
 *
 */
public class GameMapObject extends AbstractGameObject<GameMapObject>{
	@Override protected GameMapObject getThis() {
		return this;
	}


	private static final String TEXTURE_BASE = "map/";
	private HashMap<String, MapPartObject> map;
	private static int counter = 0;

	@Deprecated
	private static GameMapObject _instance;

	@Deprecated
	public static GameMapObject instance(){
		return _instance;
	}

	private boolean debug;
	
	public GameMapObject(){
		map = new HashMap<>();
		_instance = this;
	}

	public void Debug(){
		debug = true;
	}
	
	@Override
	public void finish() {
		map.values().forEach(obj->obj.finish());
	}

	private void addRegion(MapPartObject mapPart){
		if (!map.containsKey(mapPart.getName()))
			map.put(mapPart.getName(), mapPart);
	}
	
	@Override
	public boolean init(LoaderParams params) {
		String fileName = (String)params.get("filename");
		readMapFromFile(fileName);
		return false;
	}

	@Override
	public void draw(GameState st) {
		map.values().forEach(part->part.draw(st));
	}

	@Override
	public void update(GameState st) {
		map.values().forEach(part->part.update(st));
	}
	
	@Override
	public void tick() {
		map.values().forEach(part->part.tick());
		super.tick();
	}
	
	private void readMapFromFile(String fileName){
		try{
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fileName);
			Node root = doc.getDocumentElement();
			NodeList nodes = root.getChildNodes();
			for (int i=0; i<nodes.getLength(); i++){
				Node node = nodes.item(i);
				if (node.getNodeName().equals("regions")){
					parseRegions(node);
				}else if (node.getNodeName().equals("neighbors")){
					parseNeighbors(node);
				}
			}
		}catch(Exception e){
			System.out.println("Can't read map file: "+fileName);
		}
	}

	private void parseRegions(Node root){
		try{
			counter = 0;
			NodeList regions = root.getChildNodes();
			for(int i=0; i<regions.getLength(); i++){
				if (regions.item(i).getNodeType() == Node.TEXT_NODE) continue;
				Node region = regions.item(i);
				LoaderParams params = new LoaderParams();

				MapPartObject mapPart;
				if (debug){
					mapPart = new DebugMapPart();
				}else{
					mapPart = new MapPartObject();
				}
				//атрибуты
				params.put("name", attribValue(region, "name"));
				params.put("id", counter++);
				params.put("influence", Integer.valueOf(valueOrDefault(attribValue(region, "influence"),"0")));
				params.put("resources", Integer.valueOf(valueOrDefault(attribValue(region, "resources"),"0")));
				params.put("building", Integer.valueOf(valueOrDefault(attribValue(region, "building"),"0")));
						
				params.put("x", Integer.valueOf(attribValue(region, "x")));
				params.put("y", Integer.valueOf(attribValue(region, "y")));
				params.put("type", attribValue(region, "type"));			
				params.put("fraction", valueOrDefault(attribValue(region, "fraction"), "NONE"));
				
				//параметры
				NodeList regionParams = region.getChildNodes();
				for(int j=0; j<regionParams.getLength(); j++){
					if (regionParams.item(j).getNodeType() == Node.TEXT_NODE) continue;
					Node paramNode = regionParams.item(j);
					if (paramNode.getNodeName().equals("texture")){
						String texName = attribValue(paramNode, "filename");
						params.put("texture", TEXTURE_BASE + texName);
					}
					if (paramNode.getNodeName().equals("unitpos")){
						params.put("unit_x", Integer.valueOf(attribValue(paramNode,"x")));
						params.put("unit_y", Integer.valueOf(attribValue(paramNode,"y")));
					}
					if (paramNode.getNodeName().equals("unit")){
						String sType = attribValue(paramNode, "type");
						mapPart.addUnit(new UnitObject(Unit.valueOf(sType)));
					}
					if (paramNode.getNodeName().equals("action")){
						params.put("action", attribValue(paramNode, "type"));
					}
					if (paramNode.getNodeName().equals("actionpos")){
						params.put("action_x", Integer.valueOf(attribValue(paramNode,"x")));
						params.put("action_y", Integer.valueOf(attribValue(paramNode,"y")));
					}
					if (paramNode.getNodeName().equals("tokenpos")){
						params.put("token_x", Integer.valueOf(attribValue(paramNode,"x")));
						params.put("token_y", Integer.valueOf(attribValue(paramNode,"y")));
					}
				}

				try {
					params.put("w", Integer.valueOf(attribValue(region, "w")));
					params.put("h", Integer.valueOf(attribValue(region, "h")));
				}catch(NumberFormatException e){

				}

				mapPart.init(params);
				if (debug){
					mapPart.setAction(Action.MONEY);
					mapPart.setUnits(new Unit[]{Unit.KNIGHT, Unit.SOLDIER, Unit.SIEGE, Unit.SHIP});
					mapPart.setFraction(Fraction.LANISTER);
					mapPart.placePowerToken();
				}
				addRegion(mapPart);
			}
		}catch(Exception e){
			System.out.println("Error while reading regions\n Exception");
			e.printStackTrace();
		}
	}
	
	private void parseNeighbors(Node root){
		try{
		NodeList regions = root.getChildNodes();
		for(int i=0; i<regions.getLength(); i++){
			if (regions.item(i).getNodeType() == Node.TEXT_NODE) continue;
			MapPartObject region = map.get(attribValue(regions.item(i), "name"));
			NodeList neighbors = regions.item(i).getChildNodes();
			for (int j=0; j<neighbors.getLength(); j++){
				if (neighbors.item(j).getNodeType() == Node.TEXT_NODE) continue;
				String regionName = attribValue(neighbors.item(j), "name");
				MapPartObject regionToAdd = map.get(regionName);
				if (regionToAdd == null){
					System.out.println("Ошибка при чтении соседей. Не найден регион");
					System.out.println("regionName = " + regionName);
					continue;
				}
				region.addNeighbor(regionToAdd);
			}
		}}catch(Exception e){
			System.out.println("Error while reading neighbors");
		}
	}
	
	private String attribValue(Node node, String attributeName){
		Node attrib = node.getAttributes().getNamedItem(attributeName);
		if (attrib == null) return null; 
		else return attrib.getNodeValue();
	}
	
	private String valueOrDefault(String value, String def){
		if (value == null) return def;
		else return value;
	}

	public List<MapPartObject> getRegions(){
		List<MapPartObject> regions = new ArrayList<>(map.values().size());
		regions.addAll(map.values());
		return regions;
	}

	public MapPartObject getRegionByName(String name){
		Optional<MapPartObject> result = map.values().stream().filter(m->m.getName().equals(name)).findFirst();
		if (result.isPresent()){
			return result.get();
		}else{
			return null;
		}
	}

	public void enableAllRegions(){
		map.values().forEach(obj->obj.setEnabled(true));
	}
	
	public void disableAllRegions(){
		map.values().forEach(obj->obj.setEnabled(false));
	}
	
	public void enableByCondition(Predicate<MapPartObject> condition){
		map.values().forEach((region)->{
			if (condition.test(region)){
				region.setEnabled(true);
			}
		});
	}
	
	public void disableByCondition(Predicate<MapPartObject> condition){
		map.values().forEach((region)->{
			if (condition.test(region)){
				region.setEnabled(false);
			}
		});
	}
	
	public int setEnabledByCondition(Predicate<MapPartObject> condition){
		int result[] = new int[1];
		result[0] = 0;

		map.values().forEach(region -> {
			if (condition.test(region)) {
				region.setEnabled(true);
				result[0]++;
			}else{
				region.setEnabled(false);
			}
		});

		return result[0];
	}
	
	public List<MapPartObject> getEnabledRegions(){
		return map.values().stream().filter(MapPartObject::isEnabled).collect(Collectors.toList());
	}

	public int[] getArmySizesForFraction(Fraction fraction){
		ArrayList<Integer> armySizes = new ArrayList<>();
		map.values().forEach(obj->{
			if (obj.getFraction() == fraction){
				armySizes.add(obj.getUnitsCount());
			}
		});

		int result[] = new int[armySizes.size()];

		int i=0;
		for (Integer col: armySizes){
			result[i++] = col;
		}
		return result;
	}
	
	//TODO implement HASH or something else
	public MapPartObject getRegionByID(int id){
		for(MapPartObject region: map.values()){
			if (region.getID() == id) return region;
		}
		return null;
	}

	public void forEachRegion(Consumer<? super MapPartObject> func){
		map.values().forEach(func);
	}

	public int getSuply(Fraction f) {
		return map.values().stream().filter(r->
			r.getFraction() == f
		).mapToInt(MapPartObject::getResourcesCount).sum();
	}
}
