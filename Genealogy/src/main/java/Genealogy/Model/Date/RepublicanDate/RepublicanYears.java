package Genealogy.Model.Date.RepublicanDate;

//Code decompiled from external Jar
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

    private String year;
    private int index;

    public String getYear() {
        return this.year;
    }

    public int getIndex() {
        return this.index;
    }

    private RepublicanYears(String year, int index) {
        this.year = year;
        this.index = index;
    }
}
