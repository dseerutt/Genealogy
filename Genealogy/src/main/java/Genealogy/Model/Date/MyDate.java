package Genealogy.Model.Date;

import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

/**
 * MyDate class : host date data and abstract methods for FullDate, MonthDate, YearDate
 */
public abstract class MyDate implements Serializable {

    /**
     * String date format classic, space separated
     */
    public static final String DATE_FORMAT_CLASSIC = "dd MM yyyy";
    /**
     * String date format with long months, space separated
     */
    public static final String DATE_FORMAT_BIG_MONTHS = "dd MMM yyyy";
    /**
     * String date format classic, slash separated
     */
    public static final String DATE_FORMAT_SLASH = "dd/MM/yyyy";
    /**
     * String big month and year date format, space separated
     */
    public static final String BIG_MONTH_YEAR_FORMAT = "MMM yyyy";

    /**
     * Function getStringDate : from a Date date, return a string date
     *
     * @param date
     * @return
     */
    public static String getStringDate(Date date) {
        if (date == null) {
            return "null";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_SLASH, Locale.ENGLISH);
        return simpleDateFormat.format(date);
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
    public abstract Date getDate();

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

    /**
     * Function getMonth - get month from date
     *
     * @param date
     * @return
     */
    public static int getMonth(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate().getMonthValue();
    }

    /**
     * Function getYear - get year from date
     *
     * @param date
     * @return
     */
    public static int getYear(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate().getYear();
    }

    /**
     * Function getDay - get day of month from date
     *
     * @param date
     * @return
     */
    public static int getDay(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate().getDayOfMonth();
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
