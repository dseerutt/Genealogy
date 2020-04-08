package Genealogy.Model;

import Genealogy.Model.Act.Birth;
import Genealogy.Model.Date.MyDate;
import org.apache.commons.lang3.StringUtils;

/**
 * Christening class based on Birth model with String godfather and godMother
 */
public class Christening extends Birth {
    /**
     * The godparents string
     */
    private String godParents;
    /**
     * The name of the church or equivalent where the christening was carried out
     */
    private String place;

    /**
     * Basic Christening constructor based on Birth parameters
     *
     * @param person
     * @param date
     * @param town
     */
    public Christening(Person person, MyDate date, Town town) {
        super(person, date, town);
    }

    /**
     * Christening Constructor with godparents list of Strings
     *
     * @param person
     * @param date
     * @param town
     * @param godParentsList
     * @param place
     */
    public Christening(Person person, MyDate date, Town town, String godParentsList, String place) {
        super(person, date, town);
        this.godParents = godParentsList;
        this.place = place;
    }

    /**
     * Getter of godparents
     *
     * @return
     */
    public String getGodParents() {
        return godParents;
    }

    /**
     * Setter of godparents
     *
     * @return
     */
    public void setGodParents(String godParents) {
        this.godParents = godParents;
    }

    /**
     * Getter of place
     *
     * @return
     */
    public String getPlace() {
        return place;
    }

    /**
     * Setter of place
     *
     * @param place
     */
    public void setPlace(String place) {
        this.place = place;
    }

    /**
     * Function toString : toString
     *
     * @return the final String
     */
    @Override
    public String toString() {
        String res = "Christening{" +
                "citizen=" + citizen.getFullName();
        if (date != null) {
            res += ", date=" + date;
        }
        if (StringUtils.isNotBlank(place)) {
            res += " , place='" + place + "'";
        }
        if (town != null && !StringUtils.isBlank(town.getFullName())) {
            res += ", town=" + town;
        }
        if (StringUtils.isNotBlank(godParents)) {
            res += " , godparents= '" + godParents + "'";
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
        String res = "christened";
        if (date != null) {
            res += " on " + date;
        }
        if (StringUtils.isNotBlank(place)) {
            res += " at the place of '" + place + "'";
        }
        if (town != null && !StringUtils.isBlank(town.getFullName())) {
            res += " at " + town.toStringPrettyString();
        }
        if (StringUtils.isNotBlank(godParents)) {
            res += " with godparents='" + godParents + "'";
        }
        if (!proofs.isEmpty()) {
            res += " (" + proofs + ")";
        }
        return res;
    }
}
