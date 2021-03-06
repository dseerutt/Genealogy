package Genealogy.Model.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.time.LocalDate;

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
     */
    public MonthDate(String input) {
        LocalDate date = LocalDate.parse("1 " + input, DATE_FORMATTER_BIG_MONTHS);
        month = date.getMonthValue();
        year = date.getYear();
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
    public LocalDate getDate() {
        String input = year + "-" + getPaddedMonth() + "-" + "28";
        return LocalDate.parse(input);
    }

    /**
     * Function getPaddedMonth : return string month with a 0 padded if the month is on one digit
     *
     * @return
     */
    public String getPaddedMonth() {
        String monthPadded = StringUtils.EMPTY + month;
        if (monthPadded.length() == 1) {
            return "0" + monthPadded;
        } else {
            return monthPadded;
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
