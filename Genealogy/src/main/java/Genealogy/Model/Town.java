package Genealogy.Model;

import Genealogy.URLConnexion.MyHttpURLConnexion;
import Genealogy.Model.Act.Act;
import Genealogy.URLConnexion.Serializer;
import org.json.JSONArray;
import org.json.JSONObject;

import Genealogy.MapViewer.Structures.MyCoordinate;
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

    public Town(String name, String detail) {
        this.name = name;
        this.detail = detail;
    }

    public static ArrayList<Town> getTownsToSave() {
        return townsToSave;
    }

    public static void addLostTowns(String town){
        lostTowns.add(town);
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

    public static MyCoordinate parseJsonArray(String jsonObject, String city){
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
            if (towns.get(i).getFullName().equals(this.getFullName())){
                return towns.get(i).getCoordinates();
            }
        }
        return null;
    }

    public static MyCoordinate findCoordinate(String city){
        for (int i = 0 ; i < Town.getTowns().size() ; i++){
            if (towns.get(i).getFullName().equals(city)){
                return towns.get(i).getCoordinates();
            }
        }
        return null;
    }

    public static void setCoordinates() throws Exception {
        lostTowns = new ArrayList<String>();
        //Alias villes qui ont changé de nom
        HashMap<String,String> alias = Town.getTownAssociation();

        ArrayList<Town> townsInFile = Serializer.getSerializer().getTowns();
        if ((townsInFile == null)||(townsInFile.isEmpty())){
            //Si le fichier init est vide
            for (Town thisTown : towns) {
                MyCoordinate coo = null;
                String newName = thisTown.getFullName();
                if (alias.containsKey(thisTown.getFullName())) {
                    newName = alias.get(thisTown.getFullName());
                }
                    coo = Town.parseJsonArray((new MyHttpURLConnexion()).sendAddressRequest(newName),newName);
                if (!lostTowns.contains(newName)){
                    townsToSave.add(thisTown);
                }

                if (coo != null) {
                    thisTown.setCoordinates(coo);
                }
            }
        } else {
            for (Town thisTown : towns){
                Town town = findTown(townsInFile, thisTown);
                String aliasName = thisTown.getFullName();
                if (alias.containsKey(aliasName)){
                    aliasName = alias.get(thisTown.getFullName());
                }
                if ((town != null)&&(town.getCoordinates() != null)){
                    thisTown.setCoordinates(town.getCoordinates());
                    if (!lostTowns.contains(aliasName)){
                        townsToSave.add(thisTown);
                    }
                } else {
                    //Ajouter la ville
                    MyCoordinate coo = Town.parseJsonArray((new MyHttpURLConnexion()).sendAddressRequest(aliasName),aliasName);
                    if (coo != null) {
                        thisTown.setCoordinates(coo);
                    }
                    if (!lostTowns.contains(aliasName)){
                        townsToSave.add(thisTown);
                    }
                }
            }
        }
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
