package Genealogy.Model.Date;

import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by Dan on 07/04/2016.
 */
public abstract class MyDate implements Serializable {

    public MyDate() {
    }

    public static Object Mydate(String input) throws ParseException {
        if (input.length() == 4){
            return new YearDate(input);
        } else if (input.length() == 8){
            return new MonthDate(input);
        } else {
            return new FullDate(input);
        }
    }

    public abstract long getYear();
    public abstract Date getDate();
    public abstract String descriptionDate();

    public boolean isFullDate(){
        return this instanceof FullDate;
    }

    public static String getMonthForInt(int num) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11 ) {
            month = months[num];
        }
        return month;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
