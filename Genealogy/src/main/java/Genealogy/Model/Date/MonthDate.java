package Genealogy.Model.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * MonthDate class : gregorian date with month and year only, inherit MyDate
 */
public class MonthDate extends MyDate implements Serializable {
    /**
     * Int month
     */
    private int month;
    /**
     * Int year
     */
    private int year;
    /**
     * Class logger
     */
    public final static Logger logger = LogManager.getLogger(MonthDate.class);

    /**
     * MonthDate constructor from input String with MMM yyyy format
     *
     * @param input
     * @throws ParseException
     */
    public MonthDate(String input) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(BIG_MONTH_YEAR_FORMAT, Locale.ENGLISH);
        Date date = format.parse(input);
        month = getMonth(date) + 1;
        year = getYear(date);
    }

    /**
     * MonthDate constructor from int month and int year input
     *
     * @param month0
     * @param year0
     */
    public MonthDate(int month0, int year0) {
        month = month0;
        year = year0;
    }

    /**
     * Function equals : compare month and year
     *
     * @param object
     * @return
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof MonthDate)) return false;

        MonthDate monthDate = (MonthDate) object;

        if (month != monthDate.month) return false;
        return getYear() == monthDate.getYear();

    }

    /**
     * Function toString : return month/year
     *
     * @return
     */
    @Override
    public String toString() {
        return month + "/" + year;
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
     * Function getDate : return date from MonthDate object, return null if fails
     *
     * @return
     */
    public Date getDate() {
        SimpleDateFormat SDF = new SimpleDateFormat(DATE_FORMAT_CLASSIC, Locale.ENGLISH);
        String input = "30 " + month + " " + year;
        try {
            return SDF.parse(input);
        } catch (ParseException e) {
            logger.error("Failed to create MonthDate for " + month + " " + year);
            return null;
        }
    }

    /**
     * Function descriptionDate : return the String description of the object
     *
     * @return
     */
    public String descriptionDate() {
        return "en " + getMonthForInt(month - 1) + " " + year;
    }

    /**
     * Function hashCode : return hash year added to hash month
     *
     * @return
     */
    @Override
    public int hashCode() {
        return (new Integer(year)).hashCode() + (new Integer(month)).hashCode();
    }
}
