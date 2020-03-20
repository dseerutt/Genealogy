package Genealogy.Model.Date;

import Genealogy.AuxMethods;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Dan on 07/04/2016.
 */
public class MonthDate extends MyDate implements Serializable {
    private int month;
    private int year;
    final static Logger logger = LogManager.getLogger(MonthDate.class);

    public MonthDate(String input) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(AuxMethods.DATE_FORMAT4, Locale.ENGLISH);
        Date date =  format.parse(input);
        month = AuxMethods.getMonth(date) + 1;
        year = AuxMethods.getYear(date);
    }

    public MonthDate(int month0, int year0){
        month = month0;
        year = year0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MonthDate)) return false;

        MonthDate monthDate = (MonthDate) o;

        if (month != monthDate.month) return false;
        return getYear() == monthDate.getYear();

    }

    @Override
    public String toString() {
        return month + "/" + year;
    }

    public long getYear(){
        return year;
    }

    public Date getDate(){
        String DATE_FORMAT2 = "dd MM yyyy";
        SimpleDateFormat SDF = new SimpleDateFormat(DATE_FORMAT2, Locale.ENGLISH);
        String input = "30 " + month + " " + year;
        try {
            return SDF.parse(input);
        } catch (ParseException e) {
            logger.error("Impossible de créer un timestamp de MonthDate " + month + " " + year);
            return null;
        }
    }

    public String descriptionDate(){
        return "en " + getMonthForInt(month-1) + " " + year;
    }

    @Override
    public int hashCode() {
        return (new Integer(year)).hashCode() + (new Integer(month)).hashCode();
    }
}
