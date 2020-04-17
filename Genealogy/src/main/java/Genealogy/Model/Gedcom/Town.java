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
     * String detail of the city
     */
    private String detail;
    /**
     * Coordinates MyCoordinate of the city
     */
    private MyCoordinate coordinates;
    private static HashMap<Town, ArrayList<Act>> listOfTown = new HashMap<>();
    private static ArrayList<Town> towns = new ArrayList<>();
    private static ArrayList<Town> townsToSave = new ArrayList<>();
    private static ArrayList<String> lostTowns = new ArrayList<>();
    private static HashMap<String, String> townAssociation;
    private ArrayList<Town> serializerTowns;
    private static boolean useFileSave = true;

    /**
     * Town constructor from name and detail
     *
     * @param name
     * @param detail
     */
    public Town(String name, String detail) {
        this.name = name;
        this.detail = detail;
    }

    /**
     * Town constructor with fullname, parsing with townRegex
     *
     * @param fullname
     */
    public Town(String fullname) {
        for (String regex : Serializer.getTownRegex()) {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(fullname);
            if (m.find()) {
                name = StringUtils.trim(m.group(1));
                if (m.groupCount() > 1) {
                    detail = StringUtils.trim(m.group(2));
                } else {
                    detail = "";
                }
                break;
            }
        }
    }

    /**
     * TownsToSave getter
     *
     * @return
     */
    public static ArrayList<Town> getTownsToSave() {
        return townsToSave;
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
     * SerializerTowns getter
     *
     * @return
     */
    public ArrayList<Town> getSerializerTowns() {
        return serializerTowns;
    }

    /**
     * SerializerTowns setter
     *
     * @param serializerTowns
     */
    public void setSerializerTowns(ArrayList<Town> serializerTowns) {
        this.serializerTowns = serializerTowns;
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
    public String getDetail() {
        return detail;
    }

    /**
     * ListOfTowns getter
     *
     * @return
     */
    public static HashMap<Town, ArrayList<Act>> getListOfTown() {
        return listOfTown;
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
        townAssociation.put(town, county);
    }

    /**
     * Function getFullName : return the name and detail surrounded by parenthesis without whitespace
     *
     * @return
     */
    public String getFullName() {
        return StringUtils.deleteWhitespace(Objects.toString(name, "") + " " + Objects.toString(detail, ""));
    }

    /**
     * Function getFullName : return the name and detail surrounded by parenthesis without whitespace
     *
     * @return
     */
    public String getFullNameWithParenthesis() {
        return name + " (" + detail + ")";
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
     * Function equals : equals on name, then detail
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
        return getDetail() != null ? getDetail().equals(town.getDetail()) : town.getDetail() == null;

    }

    /**
     * Function hashCode : hash the object with name and detail
     *
     * @return
     */
    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getDetail() != null ? getDetail().hashCode() : 0);
        return result;
    }

    /**
     * Function addTown : add town to towns and listOfTown
     *
     * @param act
     */
    public void addTown(Act act) {
        if (this != null) {
            if (this.getName() != null) {
                if (listOfTown.containsKey(this)) {
                    listOfTown.get(this).add(act);
                } else {
                    ArrayList<Act> actes = new ArrayList<Act>();
                    actes.add(act);
                    listOfTown.put(this, actes);
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
        Serializer serializer = Serializer.getSerializer();
        String separator = "|";
        //If the serializer file is not empty
        for (Town thisTown : towns) {
            Town town = findTown(townsInFile, thisTown);
            String aliasName = thisTown.getFullName();
            String city = thisTown.getName();
            String county = thisTown.getDetail();
            if (alias.containsKey(aliasName)) {
                aliasName = alias.get(thisTown.getFullName());
            }
            if ((town != null) && (town.getCoordinates() != null)) {
                thisTown.setCoordinates(town.getCoordinates());
                if (!lostTowns.contains(aliasName)) {
                    townsToSave.add(thisTown);
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
                    if (useFileSave) {
                        saveCoordinateIntoFile(city, county, coo.getLattitude(), coo.getLongitude());
                    }
                }
                if (!lostTowns.contains(aliasName)) {
                    townsToSave.add(thisTown);
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
        Serializer serializer = Serializer.getSerializer();
        String separator = "|";
        //if the serializer file is empty
        for (Town thisTown : towns) {
            MyCoordinate coo = null;
            String newName = thisTown.getFullName();
            String city = thisTown.getName();
            String county = thisTown.getDetail();

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
                    townsToSave.add(thisTown);
                }

                if (coo != null) {
                    thisTown.setCoordinates(coo);
                    if (useFileSave) {
                        saveCoordinateIntoFile(city, county, coo.getLattitude(), coo.getLongitude());
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
        ArrayList<Town> townsInFile = Serializer.getSerializer().getTowns();
        if ((townsInFile == null) || (townsInFile.isEmpty())) {
            setAllCoordinatesFromSerializer();
        } else {
            setAllCoordinatesFromFile(townsInFile);
        }
    }

    /**
     * Function saveCoordinateIntoFile : save city in serializer
     *
     * @param city
     * @param county
     * @param latitude
     * @param longitude
     */
    private static void saveCoordinateIntoFile(String city, String county, double latitude, double longitude) {
        Serializer.getSerializer().saveCity(city + "|" + county, "" + latitude, "" + longitude);
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
            return "(" + coordinates.getLattitude() + "," + coordinates.getLongitude() + ")";
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
        return StringUtils.isBlank(name) && StringUtils.isBlank(detail);
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
                ", detail='" + detail + '\'';
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
        if (StringUtils.isBlank(detail)) {
            return name;
        } else {
            return name + " (" + detail + ")";
        }
    }
}
