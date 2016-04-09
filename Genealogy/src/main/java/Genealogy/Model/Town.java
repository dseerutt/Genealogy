package Genealogy.Model;

import Genealogy.Model.Act.Act;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dan on 06/04/2016.
 */
public class Town {
    private String name;
    private String detail;
    private HashMap<Town,ArrayList<Act>> listOfTown = new HashMap<Town, ArrayList<Act>>();

    public Town(String name, String detail) {
        this.name = name;
        this.detail = detail;
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
            if (listOfTown.containsKey(this)){
                listOfTown.get(this).add(act);
            } else {
                ArrayList<Act> actes = new ArrayList<Act>();
                actes.add(act);
                listOfTown.put(this,actes);
            }
        }

    }

    public String getName() {
        return name;
    }

    public String getDetail() {
        return detail;
    }

    public HashMap<Town, ArrayList<Act>> getListOfTown() {
        return listOfTown;
    }

    @Override
    public String toString() {
        return "Town{" +
                "name='" + name + '\'' +
                ", detail='" + detail + '\'' +
                '}';
    }
}
