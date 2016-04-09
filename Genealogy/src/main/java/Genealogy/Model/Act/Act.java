package Genealogy.Model.Act;

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

    public Act(Person citizen, MyDate date, Town town) {
        this.citizen = citizen;
        this.date = date;
        this.town = town;
    }
}
