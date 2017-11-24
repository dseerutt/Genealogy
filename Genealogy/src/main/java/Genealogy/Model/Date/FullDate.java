package Genealogy.Model.Date;

import Genealogy.AuxMethods;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Dan on 07/04/2016.
 */
public class FullDate extends MyDate {
    private Date date;
    final static Logger logger = Logger.getLogger(FullDate.class);

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

    public String descriptionDate(){
        int day = AuxMethods.getDay(date);
        int month = AuxMethods.getMonth(date);
        int year = AuxMethods.getYear(date);
        return "le " + day + " " + getMonthForInt(month) + " " + year;
    }
}
