package Genealogy.Model.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * YearDate class : date with year only, inherit Mydate
 */
public class YearDate extends MyDate implements Serializable {
    /**
     * Int year
     */
    public int year;
    /**
     * class logger
     */
    final static Logger logger = LogManager.getLogger(YearDate.class);

    /**
     * YearDate constructor from string year
     *
     * @param year0
     */
    public YearDate(String year0) {
        year = Integer.parseInt(year0);
    }

    /**
     * YearDate constructor from int year
     *
     * @param year0
     */
    public YearDate(int year0) {
        year = year0;
    }

    /**
     * Function toString : classic toString with year only
     *
     * @return
     */
    @Override
    public String toString() {
        return "" + year;
    }

    /**
     * Year getter
     *
     * @return
     */
    public int getYear() {
        return year;
    }

    /**
     * Function getDate : from the current YearDate, return a Date, return null if fails
     *
     * @return
     */
    public Date getDate() {
        SimpleDateFormat SDF = new SimpleDateFormat(DATE_FORMAT_BIG_MONTHS, Locale.ENGLISH);
        String input = "31 DEC " + year;
        try {
            return SDF.parse(input);
        } catch (ParseException e) {
            logger.error("Failed to create YearDate with " + year);
            return null;
        }
    }

    /**
     * Function equals : compare year
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof YearDate)) return false;

        YearDate yearDate = (YearDate) o;

        return getYear() == yearDate.getYear();

    }

    /**
     * Function descriptionDate : return the string for the description of the date
     *
     * @return
     */
    public String descriptionDate() {
        return "en " + year;
    }

    /**
     * Function hashCode : compare YearDate with hash of year
     *
     * @return
     */
    @Override
    public int hashCode() {
        return Long.hashCode(getYear());
    }
}
