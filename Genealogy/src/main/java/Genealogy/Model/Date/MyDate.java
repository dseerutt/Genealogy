package Genealogy.Model.Date;

import javax.swing.plaf.FontUIResource;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by Dan on 07/04/2016.
 */
public class MyDate {

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
}
