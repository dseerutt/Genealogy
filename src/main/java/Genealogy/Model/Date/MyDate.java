package Genealogy.Model.Date;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

/**
 * MyDate class : host date data and abstract methods for FullDate, MonthDate, YearDate
 */
public abstract class MyDate implements Serializable {
    /**
     * LocalDate Big Months formatter case not sensitive with one digit dat=y
     */
    public static final DateTimeFormatter DATE_FORMATTER_BIG_MONTHS = (new DateTimeFormatterBuilder()).parseCaseInsensitive().appendPattern("d MMM yyyy").toFormatter().withLocale(Locale.ENGLISH);
    /**
     * LocalDate classic formatter
     */
    public static final DateTimeFormatter DATE_FORMATTER_CLASSIC = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Function getStringDate : from a LocalDate date, return a string date
     *
     * @param date
     * @return
     */
    public static String getStringDate(LocalDate date) {
        if (date != null) {
            return date.format(DATE_FORMATTER_CLASSIC);
        } else {
            return StringUtils.EMPTY;
        }
    }

    /**
     * Function MyDate : initialize YearDate/MonthDate/FullDate from String input length and data
     *
     * @param input
     * @return
     * @throws ParseException if the parsing fails
     */
    public static Object MyDate(String input) throws ParseException {
        if (input.length() == 4) {
            return new YearDate(input);
        } else if (input.length() == 8) {
            return new MonthDate(input);
        } else {
            return new FullDate(input);
        }
    }

    /**
     * Abstract function getYear : return the int year of the date
     *
     * @return
     */
    public abstract int getYear();

    /**
     * Abstract function getDate : return the date converted from the date of the object
     *
     * @return
     */
    public abstract LocalDate getDate();

    /**
     * Abstract function descriptionDate : return the description of the object
     *
     * @return
     */
    public abstract String descriptionDate();

    /**
     * Function isFullDate : check if the object is FullDate, is the date is fully determined
     *
     * @return
     */
    public boolean isFullDate() {
        return this instanceof FullDate;
    }

    public static int getMonth(LocalDate date) {
        return date.getMonthValue();
    }

    /**
     * Function getYear - get year from date
     *
     * @param date
     * @return
     */
    public static int getYear(LocalDate date) {
        return date.getYear();
    }


    public static int getDay(LocalDate date) {
        return date.getDayOfMonth();
    }

    /**
     * Function getMonthForInt : get String month from int
     *
     * @param num
     * @return
     */
    public static String getMonthForInt(int num) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11) {
            month = months[num];
        }
        return month;
    }

    /**
     * Function hashCode : call mother class hashcode
     *
     * @return
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
