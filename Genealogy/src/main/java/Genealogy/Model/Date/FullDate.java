package Genealogy.Model.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * FullDate class : host a date with day, month and year, inherit MyDate
 */
public class FullDate extends MyDate implements Serializable {
    /**
     * LocalDate date
     */
    private LocalDate date;
    /**
     * Class logger
     */
    public final static Logger logger = LogManager.getLogger(FullDate.class);

    /**
     * FullDate constructor : from String input, initialize date. If fails, returns ParseException
     *
     * @param input
     */
    public FullDate(String input) {
        date = LocalDate.parse(input, DATE_FORMATTER_BIG_MONTHS);
    }

    /**
     * FullDate constructor from Date input - for Geneanet converter only
     *
     * @param input
     */
    public FullDate(Date input) {
        date = input.toInstant()
                .atZone(ZoneId.of("GMT+1"))
                .toLocalDate();
    }

    /**
     * FullDate constructor from LocalDate input
     *
     * @param input
     */
    public FullDate(LocalDate input) {
        date = input;
    }

    /**
     * FullDate constructor from current instant input
     */
    public FullDate() {
        date = LocalDate.now();
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
    public LocalDate getDate() {
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
