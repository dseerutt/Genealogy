package Genealogy.Model.Date.RepublicanDate;

/**
 * Enum RepublicanYears : Enum to represent the republican years
 * Code from Gary - Follow the code
 */
public enum RepublicanYears {
    I("I", 1),
    II("II", 2),
    III("III", 3),
    IV("IV", 4),
    V("V", 5),
    VI("VI", 6),
    VII("VII", 7),
    VIII("VIII", 8),
    IX("IX", 9),
    X("X", 10),
    XI("XI", 11),
    XII("XII", 12),
    XIII("XIII", 13),
    XIV("XIV", 14);

    /**
     * String republican year
     */
    private String year;
    /**
     * Int republican index year
     */
    private int index;

    /**
     * Year getter
     *
     * @return
     */
    public String getYear() {
        return this.year;
    }

    /**
     * Index getter
     *
     * @return
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * RepublicanYears constructor
     *
     * @param year
     * @param index
     */
    private RepublicanYears(String year, int index) {
        this.year = year;
        this.index = index;
    }
}
