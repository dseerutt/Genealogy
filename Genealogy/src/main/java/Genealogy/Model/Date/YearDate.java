package Genealogy.Model.Date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Dan on 07/04/2016.
 */
public class YearDate extends MyDate {
    public int year;

    public YearDate(String year0) {
        year = Integer.parseInt(year0);
    }

    @Override
    public String toString() {
        return "" + year;
    }

    public long getYear(){
        return year;
    }

    public Date getDate(){
        String DATE_FORMAT2 = "dd MM yyyy";
        SimpleDateFormat SDF = new SimpleDateFormat(DATE_FORMAT2, Locale.ENGLISH);
        String input = "01 01 " + year;
        try {
            return SDF.parse(input);
        } catch (ParseException e) {
            System.out.println("Impossible de créer un timestamp de YearDate " + year);
            return null;
        }
    }

    public String descriptionDate(){
        return "en " + year;
    }
}
