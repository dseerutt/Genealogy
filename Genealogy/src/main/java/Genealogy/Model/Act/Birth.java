package Genealogy.Model.Act;

import Genealogy.AuxMethods;
import Genealogy.Model.Date.MyDate;
import Genealogy.Model.Person;
import Genealogy.Model.Town;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Dan on 06/04/2016.
 */
public class Birth extends Act {

    public Birth(Person person, MyDate date, Town town) {
        super(person,date,town);
        if (town != null){
            town.addTown(this);
        }
    }

    @Override
    public String toString() {
        return "Birth{" +
                "citizen=" + citizen.getFullName() +
                ", date=" + date +
                ", town=" + town +
                ", proof=" + proofs +
                '}';
    }
}
