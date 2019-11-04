package Genealogy.Model.Date.RepublicanDate;

//Code decompiled from external Jar
public enum Months {
    JANVIER("Janvier"),
    FEVRIER("Février"),
    MARS("Mars"),
    AVRIL("Avril"),
    MAI("Mai"),
    JUIN("Juin"),
    JUILLET("Juillet"),
    AOUT("Août"),
    SEPTEMBRE("Septembre"),
    OCTOBRE("Octobre"),
    NOVEMBRE("Novembre"),
    DECEMBRE("Décembre");

    private String month;

    public String getMonth() {
        return this.month;
    }

    private Months(String month) {
        this.month = month;
    }
}
