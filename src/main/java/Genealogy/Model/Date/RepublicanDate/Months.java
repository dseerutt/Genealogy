package Genealogy.Model.Date.RepublicanDate;

/**
 * Months enum for French months - code from Gary - Follow the code
 */
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

    /**
     * String month
     */
    private String month;

    /**
     * Month getter
     *
     * @return
     */
    public String getMonth() {
        return this.month;
    }

    /**
     * Month constructor from String month input
     *
     * @param month
     */
    private Months(String month) {
        this.month = month;
    }
}
