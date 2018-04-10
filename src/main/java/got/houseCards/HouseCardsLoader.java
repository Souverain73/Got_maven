package got.houseCards;

import got.graphics.TextureManager;
import got.model.Fraction;
import got.utils.LoaderParams;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.*;

/**
 * Created by Souverain73 on 11.01.2017.
 */
public class HouseCardsLoader {
    private static final String defaultClassName = "got.houseCards.CommonHouseCard";
    private static final String defaultTextureName = "houseCard.png";
    private static final String HOUSE_CARDS_DATA_FILE_PATH = "data/houseCards.xml";
    private static final String TEXTURE_BASE = "House Cards/";

    private static HouseCardsLoader _instance;

    public static HouseCardsLoader instance(){
        if (_instance == null){
            _instance = new HouseCardsLoader();
            _instance.load(HOUSE_CARDS_DATA_FILE_PATH);
        }
        return _instance;
    }

    private HouseCardsLoader(){
    }


    private EnumMap<Fraction, List<HouseCard>> cardsByFraction;
    private List<HouseCard> allCards;
    private Map<Integer, HouseCard> allCardsIdMap;

    public void load(String fileName){
        cardsByFraction = new EnumMap<>(Fraction.class);
        allCards = new ArrayList<>();
        allCardsIdMap = new HashMap<>();
        try{
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fileName);
            Node root = doc.getDocumentElement();
            NodeList nodes = root.getChildNodes();
            for (int i=0; i<nodes.getLength(); i++){
                Node node = nodes.item(i);
                if (node.getNodeName().equals("house")){
                    parseHouse(node);
                }
            }
        }catch(Exception e){
            System.out.println("Can't read map file: "+fileName);
        }
    }

    private List<HouseCard> getCardsForFractionInternal(Fraction fraction){
        List<HouseCard> res = cardsByFraction.get(fraction);
        if (res == null){
            res = new ArrayList<>();
            cardsByFraction.put(fraction, res);
        }
        return res;
    }

    public List<HouseCard> getCardsForFraction(Fraction fraction){
        List<HouseCard> res = new ArrayList<>();
        if (cardsByFraction.get(fraction) == null){
            return res;
        }
        res.addAll(cardsByFraction.get(fraction));
        return res;
    }

    private void parseHouse(Node houseNode){
        Fraction currentFraction = Fraction.valueOf(attribValue(houseNode, "fraction"));
        NodeList cards = houseNode.getChildNodes();
        for (int i = 0; i < cards.getLength(); i++) {
            Node card = cards.item(i);
            if (card.getNodeType() == Node.TEXT_NODE) continue;

            String className = attribValue(card, "class");
            className = className == null ? defaultClassName : className;

            LoaderParams lp = new LoaderParams();
            lp.put("power", Integer.valueOf(valueOrDefault(attribValue(card, "power"), "0")));
            lp.put("swords", Integer.valueOf(valueOrDefault(attribValue(card, "swords"), "0")));
            lp.put("towers", Integer.valueOf(valueOrDefault(attribValue(card, "tower"), "0")));
            String title = attribValue(card, "title");
            if (title == null){
                throw new IllegalArgumentException("Card title can't be null");
            }
            lp.put("title", title);

            String textureName = null;
            NodeList childs = card.getChildNodes();
            for (int j = 0; j < childs.getLength(); j++) {
                Node child = childs.item(j);
                if (child.getNodeType() == Node.TEXT_NODE) continue;

                if (child.getNodeName().equals("texture")){
                    textureName = attribValue(child, "filename");
                }
            }

            HouseCard cardObject = null;
            textureName = textureName == null ? defaultTextureName : textureName;
            lp.put("texture", TEXTURE_BASE + textureName);
            try {
                Class<?> cardClass = Class.forName(className);
                cardObject = (HouseCard)cardClass.newInstance();
                cardObject.init(lp);
                allCards.add(cardObject);
                allCardsIdMap.put(cardObject.getID(), cardObject);
                getCardsForFractionInternal(currentFraction).add(cardObject);
            } catch (ClassNotFoundException e) {
                System.out.println("Error, can't load ["+ className + "] class");
            } catch (InstantiationException e) {
                System.out.println("Error, can't create _instance of  ["+ className + "] class");
            } catch (IllegalAccessException e) {
                System.out.println("Error, can't access constructor of ["+ className + "] class");
            }

        }
    }

    public HouseCard getCardById(int id){
        return allCardsIdMap.get(id);
    }

    private String attribValue(Node node, String attributeName){
        Node attrib = node.getAttributes().getNamedItem(attributeName);
        if (attrib == null) return null;
        else return attrib.getNodeValue();
    }

    private String valueOrDefault(String value, String def){
        return value == null ? def : value;
    }

    public static void main(String[] args) {
        HouseCardsLoader loader = new HouseCardsLoader();
        loader.load(HOUSE_CARDS_DATA_FILE_PATH);
    }

    public HouseCard getCardByTitle(String title){
        for(HouseCard card : allCards){
            if(title.equals(card.getTitle())){
                return card;
            }
        }
        return null;
    }
}
