package Genealogy.Model;

import Genealogy.Maps.MyHttpURLConnection;
import Genealogy.Model.Act.Act;
import Genealogy.Serializer;
import javafx.geometry.Point2D;
import org.json.JSONArray;
import org.json.JSONObject;

import Genealogy.MyCoordinate;
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

    public Town(String name, String detail) {
        this.name = name;
        this.detail = detail;
    }

    public String getFullName(){
        return name + " " + detail;
    }

    public static MyCoordinate parseJsonArray(String jsonObject, String city){
        String[] tab = city.toLowerCase().split(" ");
        String input = jsonObject.toLowerCase();
        for (int i = 0 ; i < tab.length ; i++){
            if (!input.contains(tab[i])){
                return null;
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

    public static void setCoordinates(){
        //Alias villes qui ont changé de nom
        HashMap<String,String> alias = new HashMap<String,String>();
        alias.put("Brienon l'archevêque Yonne","Brienon sur Armançon Yonne");
        alias.put("Châtillon sur Loing Loiret","Châtillon Coligny Loiret");
        alias.put("Paris, 17e Paris","Paris 17th arrondissement");
        alias.put("Brisée Verdière Mauritius","Brisee Verdiere Mauritius");
        alias.put("Centre de Flacq Mauritius","Central Flacq Mauritius");


        ArrayList<Town> townsInFile = Serializer.readTowns();
        if ((townsInFile == null)||(townsInFile.isEmpty())){
            //Si le fichier init est vide
            for (Town thisTown : towns) {
                MyCoordinate coo = null;
                String newName = thisTown.getFullName();
                if (alias.containsKey(thisTown.getFullName())) {
                    newName = alias.get(thisTown.getFullName());
                }
                    coo = Town.parseJsonArray((new MyHttpURLConnection()).sendAddressRequest(newName),newName);

                if (coo != null) {
                    thisTown.setCoordinates(coo);
                }
            }
        } else {
            for (Town thisTown : towns){
                Town town = findTown(townsInFile, thisTown);
                String aliasName = thisTown.getFullName();
                if (alias.containsKey(thisTown.getFullName())){
                    aliasName = alias.get(thisTown.getFullName());
                }
                if ((town != null)&&(town.getCoordinates() != null)){
                    thisTown.setCoordinates(town.getCoordinates());
                } else {
                    MyCoordinate coo = Town.parseJsonArray((new MyHttpURLConnection()).sendAddressRequest(aliasName),aliasName);
                    if (coo != null) {
                        thisTown.setCoordinates(coo);
                    }
                }
            }
        }
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
