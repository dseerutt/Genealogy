package Genealogy.Model;

import Genealogy.MapViewer.Structures.MyCoordinate;
import Genealogy.Model.Act.Act;
import Genealogy.URLConnexion.MyHttpURLConnexion;
import Genealogy.URLConnexion.Serializer;
import org.apache.commons.codec.binary.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dan on 06/04/2016.
 */
public class Town implements Serializable{
    private String name;
    private String detail;
    private MyCoordinate coordinates;
    private static HashMap<Town,ArrayList<Act>> listOfTown = new HashMap<Town, ArrayList<Act>>();
    private static ArrayList<Town> towns = new ArrayList<Town>();
    private static ArrayList<Town> townsToSave = new ArrayList<Town>();
    private static ArrayList<String> lostTowns = new ArrayList<String>();
    private static HashMap<String,String> townAssociation;
    private ArrayList<Town> serializerTowns;
    private static boolean useFileSave = true;

    public Town(String name, String detail) {
        this.name = name;
        this.detail = detail;
    }

    public static ArrayList<Town> getTownsToSave() {
        return townsToSave;
    }

    public static void addLostTowns(String town, String county){
        lostTowns.add(town);
        townAssociation.put(town, county);
    }

    public static ArrayList<String> getLostTowns(){
        return lostTowns;
    }

    public static HashMap<String, String> getTownAssociation() {
        return townAssociation;
    }

    public static void setTownAssociation(HashMap<String, String> townAssociation) {
        Town.townAssociation = townAssociation;
    }

    public String getFullName(){
        return name + " " + detail;
    }

    public String getFullNameWithParenthesis(){
        return name + " (" + detail + ")";
    }

    public static MyCoordinate parseJsonArray(String jsonObject){
        if (StringUtils.equals(jsonObject,"[]")){
            return null;
        }
        JSONObject jsonObj = new JSONObject(jsonObject.substring(1,jsonObject.length()-1));
        MyCoordinate point2D = new MyCoordinate(Double.valueOf((String) jsonObj.get("lat")), Double.valueOf((String) jsonObj.get("lon")));
        return point2D;
    }

    public static MyCoordinate parseGoogleJsonArray(String jsonObject){
        JSONObject jsonObj = new JSONObject(jsonObject);
        if (jsonObj.get("status").equals("OK")){
            JSONArray results = (JSONArray) jsonObj.get("results");
            JSONObject resultsIndex = (JSONObject) results.get(0);
            JSONObject geometry = (JSONObject) resultsIndex.get("geometry");
            JSONObject location = (JSONObject) geometry.get("location");
            MyCoordinate point2D = new MyCoordinate((double) location.get("lat"),(double) location.get("lng"));
            return point2D;
        } else {
            return null;
        }
    }

    public static MyCoordinate parseGoogleJsonArray(String jsonObject, String city){
        if (Serializer.getSerializer().isJar()){
            String[] tab = city.toLowerCase().split(" ");
            String input = jsonObject.toLowerCase();
            for (int i = 0 ; i < tab.length ; i++){
                if (!input.contains(tab[i])){
                    return null;
                }
            }
        }
        JSONObject jsonObj = new JSONObject(jsonObject);
        if (jsonObj.get("status").equals("OK")){
            JSONArray results = (JSONArray) jsonObj.get("results");
            JSONObject resultsIndex = (JSONObject) results.get(0);
            JSONObject geometry = (JSONObject) resultsIndex.get("geometry");
            JSONObject location = (JSONObject) geometry.get("location");
            MyCoordinate point2D = new MyCoordinate((double) location.get("lat"),(double) location.get("lng"));
            return point2D;
        } else {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Town)) return false;

        Town town = (Town) o;

        if (getName() != null ? !getName().equals(town.getName()) : town.getName() != null) return false;
        return getDetail() != null ? getDetail().equals(town.getDetail()) : town.getDetail() == null;

    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getDetail() != null ? getDetail().hashCode() : 0);
        return result;
    }

    public Town(String fullname) throws Exception{
            Pattern p = Pattern.compile("(.*) \\((.*)\\)");
            Matcher m = p.matcher(fullname);
            if (m.find()){
                name = m.group(1);
                detail = m.group(2);
            }
    }

    public void addTown(Act act){
        if (this != null){
            if (this.getName() != null){
                if (listOfTown.containsKey(this)){
                    listOfTown.get(this).add(act);
                } else {
                    ArrayList<Act> actes = new ArrayList<Act>();
                    actes.add(act);
                    listOfTown.put(this,actes);
                    towns.add(this);
                }
            }
        }
    }

    public static Town findTown(ArrayList<Town> towns, Town thisTown){
        for (Town town : towns){
            if (town.getName().equals(thisTown.getName())){
                return town;
            }
        }
        return null;
    }

    public MyCoordinate findCoordinate(){
        for (int i = 0 ; i < Town.getTowns().size() ; i++){
            if (towns.get(i).getFullNameWithParenthesis().equals(this.getFullName())){
                return towns.get(i).getCoordinates();
            }
        }
        return null;
    }

    public static MyCoordinate findCoordinate(String city){
        for (int i = 0 ; i < Town.getTowns().size() ; i++){
            if (towns.get(i).getFullNameWithParenthesis().equals(city)){
                return towns.get(i).getCoordinates();
            }
        }
        return null;
    }

    public static void setCoordinates() throws Exception {
        lostTowns = new ArrayList<String>();
        //Alias villes qui ont changé de nom
        HashMap<String,String> alias = Town.getTownAssociation();
        Serializer serializer = Serializer.getSerializer();
        String separator = "|";

        ArrayList<Town> townsInFile = Serializer.getSerializer().getTowns();
        if ((townsInFile == null)||(townsInFile.isEmpty())){
            //Si le fichier init est vide
            for (Town thisTown : towns) {
                MyCoordinate coo = null;
                String newName = thisTown.getFullName();
                String city = thisTown.getName();
                String county = thisTown.getDetail();

                String coordinatesFromFile = serializer.getCoordinatesFromFile(city + separator + county);
                if (coordinatesFromFile != null){
                    coo = new MyCoordinate(coordinatesFromFile);
                    thisTown.setCoordinates(coo);
                } else {
                    if (alias.containsKey(thisTown.getFullName())) {
                    newName = alias.get(thisTown.getFullName());
                }
                    coo = Town.parseJsonArray((new MyHttpURLConnexion()).sendAddressRequest(city, county));
                    if (!lostTowns.contains(newName)){
                        townsToSave.add(thisTown);
                    }

                    if (coo != null) {
                        thisTown.setCoordinates(coo);
                        if (useFileSave){
                            saveCoordinateIntoFile(city, county, coo.getLattitude(), coo.getLongitude());
                        }
                    }
                }
            }
        } else {
            for (Town thisTown : towns){
                Town town = findTown(townsInFile, thisTown);
                String aliasName = thisTown.getFullName();
                String city = thisTown.getName();
                String county = thisTown.getDetail();
                if (alias.containsKey(aliasName)){
                    aliasName = alias.get(thisTown.getFullName());
                }
                if ((town != null)&&(town.getCoordinates() != null)){
                    thisTown.setCoordinates(town.getCoordinates());
                    if (!lostTowns.contains(aliasName)){
                        townsToSave.add(thisTown);
                    }
                } else {
                    MyCoordinate coo;
                    String coordinatesFromFile = serializer.getCoordinatesFromFile(city + separator + county);
                    if (coordinatesFromFile != null){
                        coo = new MyCoordinate(coordinatesFromFile);
                        thisTown.setCoordinates(coo);
                        break;
                    }
                    //Ajouter la ville
                    coo = Town.parseJsonArray((new MyHttpURLConnexion()).sendAddressRequest(city, county));
                    if (coo != null) {
                        thisTown.setCoordinates(coo);
                        if (useFileSave){
                            saveCoordinateIntoFile(city, county, coo.getLattitude(), coo.getLongitude());
                        }
                    }
                    if (!lostTowns.contains(aliasName)){
                        townsToSave.add(thisTown);
                    }
                }
            }
        }
    }

    private static void saveCoordinateIntoFile(String city, String county, double lattitude, double longitude) {
        Serializer.getSerializer().saveCity(city + "|" + county, "" + lattitude, "" + longitude);
    }

    public ArrayList<Town> getSerializerTowns() {
        return serializerTowns;
    }

    public void setSerializerTowns(ArrayList<Town> serializerTowns) {
        this.serializerTowns = serializerTowns;
    }

    public static class TownComparator implements Comparator<Town> {
        @Override
        public int compare(Town o1, Town o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    public static void sortTowns(){
        java.util.Collections.sort(towns, new TownComparator());
    }

    public MyCoordinate getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(MyCoordinate coordinates) {
        this.coordinates = coordinates;
    }

    public static void setCoordinates(MyCoordinate coordinates, String city) {
        Town thisTown;
        for (Town town : towns){
            if (town.getFullName().equals(city)){
                thisTown =  town;
                thisTown.setCoordinates(coordinates);
                return;
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getDetail() {
        return detail;
    }

    public static HashMap<Town, ArrayList<Act>> getListOfTown() {
        return listOfTown;
    }

    public static ArrayList<Town> getTowns() {
        return towns;
    }

    public String printCoordinates(){
        if (coordinates != null){
            return "(" + coordinates.getLattitude() + "," + coordinates.getLongitude() + ")";
        } else {
            return "";
        }

    }

    public boolean isEmpty(){
        return name == null && detail == null;
    }

    @Override
    public String toString() {
        String print = "Town{" +
                "name='" + name + '\'' +
                ", detail='" + detail + '\'';
        if (coordinates != null){
            print +=  ", 'coordinates=" + printCoordinates()  + '\'';
        }
           return print + '}';
    }
}
