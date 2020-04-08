package Genealogy.Model.Act;

import Genealogy.Model.Date.MyDate;
import Genealogy.Model.Person;
import Genealogy.Model.Town;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Dan on 06/04/2016.
 */
public class Union extends Act {
    protected Person partner;
    protected UnionType unionType;

    public enum UnionType {
        HETERO_MAR,
        HOMO_MAR,
        COHABITATION,
        DIVORCE;
    }

    public Union(Person person, Person person2, MyDate date, Town town, UnionType unionType0) {

        super(person, date, town);
        unionType = unionType0;
        partner = person2;
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
        String res = unionType + "{" +
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
    public String toStringPrettyPrint(String idPerson) {
        String res = unionType.toString();
        if (partner != null) {
            Person otherPerson = getOtherPerson(idPerson);
            if (otherPerson != null) {
                res += " with " + otherPerson.getFullName();
            }
        }
        if (date != null) {
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

    public static UnionType parseUnionType(String s) {
        switch (s) {
            case "NOT MARRIED":
                return UnionType.COHABITATION;
            case "F":
                return UnionType.HOMO_MAR;
            case "D":
                return UnionType.DIVORCE;
            default:
                return UnionType.HETERO_MAR;
        }
    }

    public Person getPartner() {
        return partner;
    }

    public UnionType getUnionType() {
        return unionType;
    }

    public Person getOtherPerson(Person person) {
        if (citizen.getId().equals(person.getId())) {
            return partner;
        } else if (partner.getId().equals(person.getId())) {
            return citizen;
        } else {
            return null;
        }
    }

    public Person getOtherPerson(String idPerson) {
        if (citizen.getId().equals(idPerson)) {
            return partner;
        } else if (partner.getId().equals(idPerson)) {
            return citizen;
        } else {
            return null;
        }
    }

    @Override
    public String getNecessaryResearch() {
        String result = "";
        if (date == null || !date.isFullDate()) {
            result += " Date";
        }
        if (town.isEmpty()) {
            result += " Town";
        }
        if (citizen == null) {
            result += " Partner";
        }
        if (!result.equals("")) {
            return result.substring(1);
        } else {
            return null;
        }
    }
}
