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
public abstract class Act {
    protected Person citizen;
    protected MyDate date;
    protected Town town;
    protected static int minimumYear = 2016;

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
}
