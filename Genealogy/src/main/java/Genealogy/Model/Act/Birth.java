package Genealogy.Model.Act;

import Genealogy.Model.Date.MyDate;
import Genealogy.Model.Gedcom.Person;
import Genealogy.Model.Gedcom.Town;
import org.apache.commons.lang3.StringUtils;

/**
 * Birth class : representation of a birth civil act
 */
public class Birth extends Act {

    /**
     * Birth constructor
     *
     * @param person
     * @param date
     * @param town
     */
    public Birth(Person person, MyDate date, Town town) {
        super(person, date, town);
    }

    /**
     * Function toString : print the Birth
     *
     * @return the final String
     */
    @Override
    public String toString() {
        String res = "Birth{" +
                "citizen=" + person.getFullName();
        if (date != null) {
            res += ", date=" + date;
        }
        if (town != null && !StringUtils.isBlank(town.getFullName())) {
            res += ", town=" + town;
        }
        if (!proofs.isEmpty()) {
            res += ", proof=" + proofs;
        }
        res += '}';
        return res;
    }

    /**
     * Function toStringPrettyString : toString with pretty print
     *
     * @return the final String
     */
    public String toStringPrettyPrint() {
        String res = "born";
        if (date != null) {
            res += " on " + date;
        }
        if (town != null && !StringUtils.isBlank(town.getFullName())) {
            res += " at " + town.toStringPrettyString();
        }
        if (!proofs.isEmpty()) {
            res += " (" + proofs + ")";
        }
        return res;
    }
}
