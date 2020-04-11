package Genealogy.Model.Date;

import Genealogy.Model.Gedcom.AuxMethods;
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
public class FullDate extends MyDate implements Serializable {
    private Date date;
    final static Logger logger = LogManager.getLogger(FullDate.class);

    public FullDate(String input) throws ParseException {
            SimpleDateFormat format = new SimpleDateFormat(AuxMethods.DATE_FORMAT2, Locale.ENGLISH);
            date =  format.parse(input);
    }
    public FullDate(Date input){
        date = input;
    }

    public FullDate(){
        date = new Date();
    }

    public FullDate(long timestamp){
        date = new Date(timestamp);
    }

    @Override
    public String toString() {
        return AuxMethods.getStringDate(date);
    }

    public long getYear(){
        return AuxMethods.getYear(date);
    }

    public Date getDate(){
        try {
            return date;
        } catch (Exception e) {
            logger.error("Impossible de créer un timestamp de FullDate " + AuxMethods.getStringDate(date));
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FullDate)) return false;

        FullDate fullDate = (FullDate) o;

        return getDate() != null ? getDate().equals(fullDate.getDate()) : fullDate.getDate() == null;

    }

    @Override
    public int hashCode() {
        return getDate() != null ? getDate().hashCode() : 0;
    }

    public String descriptionDate(){
        int day = AuxMethods.getDay(date);
        int month = AuxMethods.getMonth(date);
        int year = AuxMethods.getYear(date);
        return "le " + day + " " + getMonthForInt(month) + " " + year;
    }
}
