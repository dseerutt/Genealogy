package Genealogy.Model.Act;

import Genealogy.AuxMethods;
import Genealogy.Model.Date.MyDate;
import Genealogy.Model.Person;
import Genealogy.Model.Town;

import java.util.*;

/**
 * Created by Dan on 06/04/2016.
 */
public abstract class Act {
    protected Person citizen;
    protected MyDate date;
    protected Town town;
    protected static int minimumYear = Calendar.getInstance().get(Calendar.YEAR);
    protected List<String> proofs = new ArrayList<>();
    public enum TypeActe {
        Birth, Mariage, Death
    }

    public Act(Person citizen, MyDate date, Town town) {
        this.citizen = citizen;
        this.date = date;
        this.town = town;
        if (date != null){
            int yearDate = AuxMethods.getYear(date.getDate());
            if (yearDate < minimumYear){
                minimumYear = yearDate;
            }
        }
    }

    public String getNecessaryResearch(){
        String result = "";
        if (date == null || !date.isFullDate()){
            result += " Date";
        }
        if (town.isEmpty()){
            result += " Town";
        }
        if (!result.equals("")){
            return result.substring(1);
        } else {
            return null;
        }
    }

    public Person getCitizen() {
        return citizen;
    }

    public MyDate getDate() {
        return date;
    }

    public Town getTown() {
        return town;
    }

    public static int getMinimumYear() {
        return minimumYear;
    }

    public void addProof(String proof){
        proofs.add(proof);
    }

    public List<String> getProofs(){
        return proofs;
    }
}
