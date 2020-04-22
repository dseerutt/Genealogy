package Genealogy.Model.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * FullDate class : host a date with day, month and year, inherit MyDate
 */
public class FullDate extends MyDate implements Serializable {
    /**
     * Date date
     */
    private Date date;
    /**
     * Class logger
     */
    public final static Logger logger = LogManager.getLogger(FullDate.class);

    /**
     * FullDate constructor : from String input, initialize date. If fails, returns ParseException
     *
     * @param input
     * @throws ParseException
     */
    public FullDate(String input) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_BIG_MONTHS, Locale.ENGLISH);
        date = format.parse(input);
    }

    /**
     * FullDate constructor from Date input
     *
     * @param input
     */
    public FullDate(Date input) {
        date = input;
    }

    /**
     * FullDate constructor from current instant input
     */
    public FullDate() {
        date = new Date();
    }

    /**
     * Function toString : print date with DATE_FORMAT_SLASH format
     *
     * @return
     */
    @Override
    public String toString() {
        return getStringDate(date);
    }

    /**
     * Function getYear : get year of the date
     *
     * @return
     */
    public int getYear() {
        return getYear(date);
    }

    /**
     * Function getDate : date getter
     *
     * @return
     */
    public Date getDate() {
        return date;
    }

    /**
     * Function equals : date comparison
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FullDate)) return false;

        FullDate fullDate = (FullDate) o;

        return getDate() != null ? getDate().equals(fullDate.getDate()) : fullDate.getDate() == null;

    }

    /**
     * Function hashCode : hashcode on date
     *
     * @return
     */
    @Override
    public int hashCode() {
        return getDate() != null ? getDate().hashCode() : 0;
    }

    /**
     * Function descriptionDate : return description of the date object
     *
     * @return
     */
    public String descriptionDate() {
        int day = getDay(date);
        int month = getMonth(date);
        int year = getYear(date);
        return "le " + day + " " + getMonthForInt(month) + " " + year;
    }
}
