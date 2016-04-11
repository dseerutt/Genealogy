package Genealogy.Model;

import Genealogy.Genealogy;
import Genealogy.Model.Act.Act;
import edu.emory.mathcs.backport.java.util.Collections;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dan on 06/04/2016.
 */
public class Town {
    private String name;
    private String detail;
    private static HashMap<Town,ArrayList<Act>> listOfTown = new HashMap<Town, ArrayList<Act>>();
    private static ArrayList<Town> towns = new ArrayList<Town>();

    public Town(String name, String detail) {
        this.name = name;
        this.detail = detail;
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

    public static class TownComparator implements Comparator<Town> {
        @Override
        public int compare(Town o1, Town o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    public static void sortTowns(){
        java.util.Collections.sort(towns, new TownComparator());
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

    @Override
    public String toString() {
        return "Town{" +
                "name='" + name + '\'' +
                ", detail='" + detail + '\'' +
                '}';
    }
}
