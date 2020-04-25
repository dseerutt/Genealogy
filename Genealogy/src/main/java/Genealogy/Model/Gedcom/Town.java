package Genealogy.Model.Gedcom;

import Genealogy.MapViewer.Structures.MyCoordinate;
import Genealogy.Model.Act.Act;
import Genealogy.URLConnexion.MyHttpUrlConnection;
import Genealogy.URLConnexion.Serializer;
import org.apache.commons.lang3.StringUtils;
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
        lostTowns.add(town);
        townAssociation.put(town + " " + county, "");
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
        if (StringUtils.equals(jsonObject, "[]")) {
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
     * Function findTown : find thisTown in towns, return null instead
     *
     * @param towns
     * @param thisTown
     * @return
     */
    public static Town findTown(ArrayList<Town> towns, Town thisTown) {
        for (Town town : towns) {
            if (town.getName().equals(thisTown.getName())) {
                return town;
            }
        }
        return null;
    }

    /**
     * Function findCoordinateFromTowns : find coordinate of the current city in towns
     *
     * @return
     */
    public MyCoordinate findCoordinateFromTowns() {
        for (int i = 0; i < Town.getTowns().size(); i++) {
            if (towns.get(i).getFullNameWithParenthesis().equals(this.getFullName())) {
                return towns.get(i).getCoordinates();
            }
        }
        return null;
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
     * Function setAllCoordinatesFromFile : set town coordinates from file, or search it
     *
     * @param townsInFile
     * @throws Exception
     */
    public static void setAllCoordinatesFromFile(ArrayList<Town> townsInFile) throws Exception {
        lostTowns = new ArrayList<>();
        //Handle alias - cities that changed names
        HashMap<String, String> alias = Town.getTownAssociation();
        Serializer serializer = Serializer.getInstance();
        String separator = "|";
        //If the serializer file is not empty
        for (Town thisTown : towns) {
            Town town = findTown(townsInFile, thisTown);
            String aliasName = thisTown.getFullName();
            String city = thisTown.getName();
            String county = thisTown.getCounty();
            if (alias.containsKey(aliasName)) {
                aliasName = alias.get(thisTown.getFullName());
            }
            if ((town != null) && (town.getCoordinates() != null)) {
                thisTown.setCoordinates(town.getCoordinates());
                if (!lostTowns.contains(aliasName)) {
                    townsToSerialize.add(thisTown);
                }
            } else {
                MyCoordinate coo;
                String coordinatesFromFile = serializer.getCoordinatesFromFile(city + separator + county);
                if (coordinatesFromFile != null) {
                    coo = new MyCoordinate(coordinatesFromFile);
                    thisTown.setCoordinates(coo);
                    break;
                }
                //Add the town
                coo = Town.parseJsonArray((new MyHttpUrlConnection()).sendGpsRequest(city, county));
                if (coo != null) {
                    thisTown.setCoordinates(coo);
                    if (saveCoordinatesTxtFile) {
                        saveCoordinateIntoFile(city, county, coo.getLatitude(), coo.getLongitude());
                    }
                }
                if (!lostTowns.contains(aliasName)) {
                    townsToSerialize.add(thisTown);
                }
            }
        }
    }

    /**
     * Function setAllCoordinatesFromFile : set town coordinates from Serializer, or search it
     *
     * @throws Exception
     */
    public static void setAllCoordinatesFromSerializer() throws Exception {
        lostTowns = new ArrayList<>();
        //Handle alias - cities that changed names
        HashMap<String, String> alias = Town.getTownAssociation();
        Serializer serializer = Serializer.getInstance();
        String separator = "|";
        //if the serializer file is empty
        for (Town thisTown : towns) {
            MyCoordinate coo = null;
            String newName = thisTown.getFullName();
            String city = thisTown.getName();
            String county = thisTown.getCounty();

            String coordinatesFromFile = serializer.getCoordinatesFromFile(city + separator + county);
            if (coordinatesFromFile != null) {
                coo = new MyCoordinate(coordinatesFromFile);
                thisTown.setCoordinates(coo);
            } else {
                if (alias.containsKey(thisTown.getFullName())) {
                    newName = alias.get(thisTown.getFullName());
                }
                coo = Town.parseJsonArray((new MyHttpUrlConnection()).sendGpsRequest(city, county));
                if (!lostTowns.contains(newName)) {
                    townsToSerialize.add(thisTown);
                }

                if (coo != null) {
                    thisTown.setCoordinates(coo);
                    if (saveCoordinatesTxtFile) {
                        saveCoordinateIntoFile(city, county, coo.getLatitude(), coo.getLongitude());
                    }
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
        ArrayList<Town> townsInFile = Serializer.getInstance().getTowns();
        if ((townsInFile == null) || (townsInFile.isEmpty())) {
            setAllCoordinatesFromSerializer();
        } else {
            setAllCoordinatesFromFile(townsInFile);
        }
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
