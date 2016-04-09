package Genealogy.Model.Date;

/**
 * Created by Dan on 07/04/2016.
 */
public class YearDate extends MyDate {
    public int year;

    public YearDate(String year0) {
        year = Integer.parseInt(year0);
    }

    @Override
    public String toString() {
        return "" + year;
    }
}
