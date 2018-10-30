package Genealogy.Model.Date;

import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Dan on 07/04/2016.
 */
public class YearDate extends MyDate {
    public int year;
    final static Logger logger = Logger.getLogger(YearDate.class);

    public YearDate(String year0) {
        year = Integer.parseInt(year0);
    }

    public YearDate(int year0) {
        year = year0;
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
        String input = "31 12 " + year;
        try {
            return SDF.parse(input);
        } catch (ParseException e) {
            logger.error("Impossible de créer un timestamp de YearDate " + year);
            return null;
        }
    }

    public String descriptionDate(){
        return "en " + year;
    }
}
