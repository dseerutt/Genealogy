package Genealogy.Model.Date;

import Genealogy.AuxMethods;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Dan on 07/04/2016.
 */
public class FullDate extends MyDate {
    private Date date;

    public FullDate(String input) throws ParseException {
            SimpleDateFormat format = new SimpleDateFormat(AuxMethods.DATE_FORMAT2, Locale.ENGLISH);
            date =  format.parse(input);
    }

    @Override
    public String toString() {
        return AuxMethods.getStringDate(date);
    }
}
