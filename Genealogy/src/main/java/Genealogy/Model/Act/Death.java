package Genealogy.Model.Act;

import Genealogy.Model.Date.MyDate;
import Genealogy.Model.Person;
import Genealogy.Model.Town;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Dan on 06/04/2016.
 */
public class Death extends Act {

    public Death(Person person, MyDate date, Town town) {
        super(person, date, town);
        if (town != null) {
            town.addTown(this);
        }
    }

    /**
     * Function toString : toString
     *
     * @return the final String
     */
    @Override
    public String toString() {
        String res = "Death{" +
                "citizen=" + citizen.getFullName();
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
        String res = "died";
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
