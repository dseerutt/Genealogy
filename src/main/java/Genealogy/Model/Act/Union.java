package Genealogy.Model.Act;

import Genealogy.Model.Act.Enum.UnionType;
import Genealogy.Model.Date.MyDate;
import Genealogy.Model.Gedcom.Person;
import Genealogy.Model.Gedcom.Town;
import org.apache.commons.lang3.StringUtils;

/**
 * Union class : representation of a union civil act
 */
public class Union extends Act {
    /**
     * Person partner of the union
     */
    protected Person partner;
    /**
     * Type of Union with enum UnionType, divorce possible
     */
    protected UnionType unionType;

    /**
     * Union constructor
     *
     * @param person
     * @param partner
     * @param date
     * @param town
     * @param unionType0
     */
    public Union(Person person, Person partner, MyDate date, Town town, UnionType unionType0) {
        super(person, date, town);
        unionType = unionType0;
        this.partner = partner;
    }

    /**
     * Function toString : toString
     *
     * @return the final String
     */
    @Override
    public String toString() {
        String res = unionType + "{" +
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
     * Function toStringPrettyString : toString with pretty print given the person id, null if not found
     *
     * @return the final String
     */
    public String toStringPrettyPrint(String idPerson) {
        String res = unionType.toString();
        if (partner != null) {
            Person otherPerson = getOtherPerson(idPerson);
            if (otherPerson != null) {
                res += " with " + otherPerson.getFullName();
            } else {
                return null;
            }
        }
        if (date != null && date.getDate() != null) {
            res += " on " + date;
        }
        if (town != null && !StringUtils.isBlank(town.getFullName())) {
            res += " at " + town.toStringPrettyString();
        }
        if (!proofs.isEmpty()) {
            res += "(" + proofs + ")";
        }
        return res;
    }

    /**
     * Partner getter
     *
     * @return
     */
    public Person getPartner() {
        return partner;
    }

    /**
     * UnionType getter
     *
     * @return
     */
    public UnionType getUnionType() {
        return unionType;
    }

    /**
     * Function getOtherPerson : given a parameter person, returns the other person of the union, null instead
     *
     * @param person
     * @return
     */
    public Person getOtherPerson(Person person) {
        if (this.person.getId().equals(person.getId())) {
            return partner;
        } else if (partner.getId().equals(person.getId())) {
            return this.person;
        } else {
            return null;
        }
    }

    /**
     * Function getOtherPerson : given a parameter String idPerson, returns the other person of the union, null instead
     *
     * @param idPerson
     * @return
     */
    public Person getOtherPerson(String idPerson) {
        if (person.getId().equals(idPerson)) {
            return partner;
        } else if (partner.getId().equals(idPerson)) {
            return person;
        } else {
            return null;
        }
    }

    /**
     * Function getNecessaryResearch : return the fields that are null, null if there is none
     *
     * @return
     */
    @Override
    public String getNecessaryResearch() {
        String result = StringUtils.EMPTY;
        if (date == null || !date.isFullDate()) {
            result += " date";
        }
        if (town == null || town.isEmpty()) {
            result += " town";
        }
        if (person == null) {
            result += " person";
        }
        if (partner == null) {
            result += " partner";
        }
        if (unionType == null) {
            result += " unionType";
        }
        if (!result.equals(StringUtils.EMPTY)) {
            return result.substring(1);
        } else {
            return null;
        }
    }
}
