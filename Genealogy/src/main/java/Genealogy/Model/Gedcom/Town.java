package Genealogy.Model.Gedcom;

import Genealogy.MapViewer.Structures.MyCoordinate;
import Genealogy.Model.Act.Act;
import Genealogy.URLConnexion.MyHttpUrlConnection;
import Genealogy.URLConnexion.Serializer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Town class : handle act places
 */
public class Town implements Serializable {
    /**
     * String name of the city
     */
    private String name;
    /**
     * String county of the city
     */
    private String county;
    /**
     * Coordinates MyCoordinate of the city
     */
    private MyCoordinate coordinates;
    /**
     * HashMap mapTownAct of list of Act per Town
     */
    private static HashMap<Town, ArrayList<Act>> mapTownAct = new HashMap<>();
    /**
     * ArrayList of Towns towns : static list of all towns
     */
    private static ArrayList<Town> towns = new ArrayList<>();
    /**
     * Arraylist of Town townsToSerialize : towns to save through serializer with complete coordinates
     */
    private static ArrayList<Town> townsToSerialize = new ArrayList<>();
    /**
     * ArrayList of string fullName towns coordinates not found
     */
    private static ArrayList<String> lostTowns = new ArrayList<>();
    /**
     * HashMap townAssociation : associations of string fullName of Towns.
     * The first town is not found, and its coordinates will be searched on the second town
     */
    private static HashMap<String, String> townAssociation = new HashMap<>();
    /**
     * Boolean saveCoordinatesTxtFile : save on serializer coordinates text file
     */
    private static boolean saveCoordinatesTxtFile = true;
    /**
     * Class logger
     */
    final static Logger logger = LogManager.getLogger(Town.class);

    /**
     * Town constructor from name and county, and add the town to towns if not present
     *
     * @param name
     * @param county
     */
    public Town(String name, String county) {
        this.name = name;
        this.county = county;
        if (!towns.contains(this)) {
            towns.add(this);
        }
    }

    /**
     * Town constructor with fullName, parsing with townRegex, and add the town to towns if not present
     *
     * @param fullName
     */
    public Town(String fullName) {
        for (String regex : Serializer.getTownRegex()) {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(fullName);
            if (m.find()) {
                name = StringUtils.trim(m.group(1));
                if (m.groupCount() > 1) {
                    county = StringUtils.trim(m.group(2));
                } else {
                    county = "";
                }
                break;
            }
        }
        if (!towns.contains(this)) {
            towns.add(this);
        }
    }

    /**
     * TownsToSave getter
     *
     * @return
     */
    public static ArrayList<Town> getTownsToSerialize() {
        return townsToSerialize;
    }

    /**
     * LostTowns getter
     *
     * @return
     */
    public static ArrayList<String> getLostTowns() {
        return lostTowns;
    }

    /**
     * TownAssociation getter
     *
     * @return
     */
    public static HashMap<String, String> getTownAssociation() {
        return townAssociation;
    }

    /**
     * townAssociation setter
     *
     * @param townAssociation
     */
    public static void setTownAssociation(HashMap<String, String> townAssociation) {
        Town.townAssociation = townAssociation;
    }

    /**
     * Name getter
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Detail getter
     *
     * @return
     */
    public String getCounty() {
        return county;
    }

    /**
     * ListOfTowns getter
     *
     * @return
     */
    public static HashMap<Town, ArrayList<Act>> getMapTownAct() {
        return mapTownAct;
    }

    /**
     * Towns getter
     *
     * @return
     */
    public static ArrayList<Town> getTowns() {
        return towns;
    }

    /**
     * Coordinates getter
     *
     * @return
     */
    public MyCoordinate getCoordinates() {
        return coordinates;
    }

    /**
     * Coordinates setter
     *
     * @param coordinates
     */
    public void setCoordinates(MyCoordinate coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * Function addLostTowns : add town to lostTowns, put the town and county into townAssociation
     *
     * @param town
     * @param county
     */
    public static void addLostTowns(String town, String county) {
        lostTowns.add(town + " (" + county + ")");
        townAssociation.put(town + " (" + county + ")", "");
    }

    /**
     * Function getFullName : return the name and county surrounded by parenthesis and trim whitespace
     *
     * @return
     */
    public String getFullName() {
        return StringUtils.trim(Objects.toString(name, "") + " " + Objects.toString(county, ""));
    }

    /**
     * Function getFullName : return the name and county surrounded by parenthesis and trim whitespace
     *
     * @return
     */
    public String getFullNameWithParenthesis() {
        if (StringUtils.isEmpty(county)) {
            return StringUtils.trim(name);
        } else {
            return StringUtils.trim(name + " (" + county + ")");
        }
    }

    /**
     * Function parseJsonArray : turns jsonObject parameter into MyCoordinate
     *
     * @param jsonObject
     * @return
     */
    public static MyCoordinate parseJsonArray(String jsonObject) {
        if (StringUtils.isBlank(jsonObject) || StringUtils.equals(jsonObject, "[]")) {
            return null;
        }
        JSONObject jsonObj = new JSONObject(jsonObject.substring(1, jsonObject.length() - 1));
        MyCoordinate point2D = new MyCoordinate(Double.valueOf((String) jsonObj.get("lat")), Double.valueOf((String) jsonObj.get("lon")));
        return point2D;
    }

    /**
     * Function equals : equals on name, then county
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Town)) return false;
        Town town = (Town) o;
        if (getName() != null ? !getName().equals(town.getName()) : town.getName() != null) return false;
        return getCounty() != null ? getCounty().equals(town.getCounty()) : town.getCounty() == null;

    }

    /**
     * Function hashCode : hash the object with name and county
     *
     * @return
     */
    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getCounty() != null ? getCounty().hashCode() : 0);
        return result;
    }

    /**
     * Function addAct : add act to mapTownAct and town to towns
     *
     * @param act
     */
    public void addAct(Act act) {
        if (this != null) {
            if (this.getName() != null) {
                if (mapTownAct.containsKey(this)) {
                    mapTownAct.get(this).add(act);
                } else {
                    ArrayList<Act> acts = new ArrayList<>();
                    acts.add(act);
                    mapTownAct.put(this, acts);
                    towns.add(this);
                }
            }
        }
    }

    /**
     * Function findTown : find thisTown in towns with getFullName , return null if not found
     *
     * @param towns
     * @param thisTown
     * @return
     */
    public static Town findTown(ArrayList<Town> towns, Town thisTown) {
        for (Town town : towns) {
            if (town.getFullName().equals(thisTown.getFullName())) {
                return town;
            }
        }
        return null;
    }

    /**
     * Function findTown : find thisTown in towns with getFullNameWithParenthesis , return null if not found
     *
     * @param towns
     * @param fullNameWithParenthesis
     * @return
     */
    public static Town findTown(ArrayList<Town> towns, String fullNameWithParenthesis) {
        for (Town town : towns) {
            if (town.getFullNameWithParenthesis().equals(fullNameWithParenthesis)) {
                return town;
            }
        }
        return null;
    }

    /**
     * Function findCoordinateFromTowns : find coordinates of the current city in towns
     *
     * @return
     */
    public MyCoordinate findCoordinateFromTowns() {
        if (coordinates != null) {
            return coordinates;
        } else {
            for (int i = 0; i < Town.getTowns().size(); i++) {
                if (towns.get(i).getFullNameWithParenthesis().equals(this.getFullNameWithParenthesis())) {
                    return towns.get(i).getCoordinates();
                }
            }
            return null;
        }
    }

    /**
     * Function findCoordinateFromTowns : find coordinate of the input city fullname in towns
     *
     * @param city
     * @return
     */
    public static MyCoordinate findCoordinateFromTowns(String city) {
        for (int i = 0; i < Town.getTowns().size(); i++) {
            if (towns.get(i).getFullNameWithParenthesis().equals(city)) {
                return towns.get(i).getCoordinates();
            }
        }
        return null;
    }

    /**
     * Function setAllCoordinatesFromFile : set town coordinates from file, handles alias
     *
     * @param townCoordinatesList
     * @throws Exception
     */
    public static void setAllCoordinatesFromFile(ArrayList<Town> townCoordinatesList) throws Exception {
        //Handle alias - cities that changed names
        HashMap<String, String> alias = Town.getTownAssociation();
        Serializer serializer = Serializer.getInstance();
        for (Town thisTown : towns) {
            Town town = findTown(townCoordinatesList, thisTown);
            if ((town != null) && (town.getCoordinates() != null)) {
                thisTown.setCoordinates(town.getCoordinates());
            }
            String aliasName = thisTown.getFullNameWithParenthesis();
            String city = thisTown.getName();
            String county = thisTown.getCounty();
            if (alias.containsKey(aliasName)) {
                aliasName = alias.get(aliasName);
                town = findTown(townCoordinatesList, aliasName);
                if ((town != null) && (town.getCoordinates() != null)) {
                    thisTown.setCoordinates(town.getCoordinates());
                }
                String[] tab = aliasName.split("\\(");
                if (tab != null && tab.length == 2) {
                    city = StringUtils.trim(tab[0]);
                    county = StringUtils.trim(tab[1].substring(0, tab[1].length() - 1));
                } else {
                    logger.error("Failed to read alias " + aliasName + " for town " + thisTown.getFullNameWithParenthesis());
                }
            }
            if (thisTown.getCoordinates() == null) {
                MyCoordinate coo;
                String coordinatesFromFile = serializer.getCoordinatesFromFile(city, county);
                if (coordinatesFromFile != null) {
                    coo = new MyCoordinate(coordinatesFromFile);
                    thisTown.setCoordinates(coo);
                }
                coo = Town.parseJsonArray(MyHttpUrlConnection.getInstance().sendGpsRequest(city, county));
                if (coo != null) {
                    thisTown.setCoordinates(coo);
                }
            }
            //post-treatment
            if (thisTown.getCoordinates() != null) {
                if (saveCoordinatesTxtFile) {
                    saveCoordinateIntoFile(city, county, thisTown.getCoordinates().getLatitude(), thisTown.getCoordinates().getLongitude());
                }
                if (!lostTowns.contains(aliasName)) {
                    townsToSerialize.add(thisTown);
                }
            }
        }
    }

    /**
     * Function setAllCoordinates : find coordinates for each town in towns from serialized file,
     * or coordinate files, or search it
     * Handles city alias
     *
     * @throws Exception
     */
    public static void setAllCoordinates() throws Exception {
        lostTowns = new ArrayList<>();
        setAllCoordinatesFromFile(Serializer.getInstance().getTowns());
    }

    /**
     * Function saveCoordinateIntoFile : save city in serializer coordinate String file
     *
     * @param city
     * @param county
     * @param latitude
     * @param longitude
     */
    private static void saveCoordinateIntoFile(String city, String county, double latitude, double longitude) {
        Serializer.getInstance().saveCity(city + "|" + county, "" + latitude, "" + longitude);
    }

    /**
     * Function sortTowns : sort towns with TownComparator (names)
     */
    public static void sortTowns() {
        java.util.Collections.sort(towns, new TownComparator());
    }

    /**
     * Function setCoordinates : for the coordinates of the city input
     *
     * @param coordinates
     * @param city
     */
    public static void setCoordinates(MyCoordinate coordinates, String city) {
        Town thisTown;
        for (Town town : towns) {
            if (town.getFullName().equals(city)) {
                thisTown = town;
                thisTown.setCoordinates(coordinates);
                return;
            }
        }
    }

    /**
     * Function getCoordinatesPrettyPrint : return a pretty print of coordinates of the city
     *
     * @return
     */
    public String getCoordinatesPrettyPrint() {
        if (coordinates != null) {
            return "(" + coordinates.getLatitude() + "," + coordinates.getLongitude() + ")";
        } else {
            return "";
        }

    }

    /**
     * Function isEmpty : check if town is empty
     *
     * @return
     */
    public boolean isEmpty() {
        return StringUtils.isBlank(name) && StringUtils.isBlank(county);
    }

    /**
     * Function toString : classic toString
     *
     * @return the final String
     */
    @Override
    public String toString() {
        String print = "Town{" +
                "name='" + name + '\'' +
                ", county='" + county + '\'';
        if (coordinates != null) {
            print += ", 'coordinates=" + getCoordinatesPrettyPrint() + '\'';
        }
        return print + '}';
    }

    /**
     * Function toStringPrettyString : toString with pretty print
     *
     * @return the final String
     */
    public String toStringPrettyString() {
        if (StringUtils.isBlank(county)) {
            return name;
        } else {
            return name + " (" + county + ")";
        }
    }
}
