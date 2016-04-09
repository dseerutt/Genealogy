package Genealogy.Model.Date;

import Genealogy.AuxMethods;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Dan on 07/04/2016.
 */
public class MonthDate extends MyDate {
    private int month;
    private int year;

    public MonthDate(String input) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(AuxMethods.DATE_FORMAT4, Locale.ENGLISH);
        Date date =  format.parse(input);
        month = AuxMethods.getMonth(date) + 1;
        year = AuxMethods.getYear(date);
    }

    @Override
    public String toString() {
        return month + "/" + year;
    }
}
