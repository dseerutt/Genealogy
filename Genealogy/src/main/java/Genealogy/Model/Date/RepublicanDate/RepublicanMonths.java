package Genealogy.Model.Date.RepublicanDate;

/**
 * Class RepublicanMonths : French revolutionary calendar months
 * Code from Gary - Follow the code
 */
public enum RepublicanMonths {
    VENDEMIAIRE("[V|v]end[é|e]miaire", 0),
    BRUMAIRE("[B|b]rumaire", 1),
    FRIMAIRE("[F|f]rimaire", 2),
    NIVOSE("[N|n]iv[ô|o]se", 3),
    PLUVIOSE("[P|p]luvi[ô|o]se", 4),
    VENTOSE("[V|v]ent[ô|o]se", 5),
    GERMINAL("[G|g]erminal", 6),
    FLOREAL("[F|f]lor[é|e]al", 7),
    PRAIRIAL("[P|p]rairial", 8),
    MESSIDOR("[M|m]essidor", 9),
    THERMIDOR("[T|t]hermidor", 10),
    FRUCTIDOR("[F|f]ructidor", 11);

    /**
     * String month
     */
    private String month;
    /**
     * int index : number of the month
     */
    private int index;

    /**
     * Month getter
     *
     * @return
     */
    public String getMonth() {
        return this.month;
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
     * RepublicanMonths constructor from String month and int index
     *
     * @param month
     * @param index
     */
    private RepublicanMonths(String month, int index) {
        this.month = month;
        this.index = index;
    }
}
