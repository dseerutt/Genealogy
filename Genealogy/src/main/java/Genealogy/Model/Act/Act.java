package Genealogy.Model.Act;

import Genealogy.Model.Date.MyDate;
import Genealogy.Model.Gedcom.Person;
import Genealogy.Model.Gedcom.Town;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class Act : representation of a civil act
 */
public abstract class Act {
    /**
     * The Person linked to the act
     */
    protected Person person;
    /**
     * The date MyDate of the act
     */
    protected MyDate date;
    /**
     * The Town of the act
     */
    protected Town town;
    /**
     * The minimum int year of all acts
     */
    protected static int minimumYear = LocalDate.now().getYear();
    /**
     * The list of String file proofs
     */
    protected List<String> proofs = new ArrayList<>();

    /**
     * Act Constructor : sets minimum year of act for whole program,
     * links the acts to the towns
     *
     * @param person the person to be linked to
     * @param date   the date of the act
     * @param town   the Town of the act
     */
    public Act(Person person, MyDate date, Town town) {
        this.person = person;
        this.date = date;
        this.town = town;
        if (date != null && date.getDate() != null) {
            int yearDate = MyDate.getYear(date.getDate());
            if (yearDate < minimumYear) {
                minimumYear = yearDate;
            }
        }
        if (town != null) {
            town.addAct(this);
        }
    }

    /**
     * Function getNecessaryResearch : return in a string the null fields or not FullDate dates
     *
     * @return
     */
    public String getNecessaryResearch() {
        String result = StringUtils.EMPTY;
        if (person == null) {
            result += " person";
        }
        if (date == null || !date.isFullDate()) {
            result += " date";
        }
        if (town == null || town.isEmpty()) {
            result += " town";
        }
        if (!result.equals(StringUtils.EMPTY)) {
            return result.substring(1);
        } else {
            return null;
        }
    }

    /**
     * Getter of person
     *
     * @return
     */
    public Person getPerson() {
        return person;
    }

    /**
     * Getter of date
     *
     * @return
     */
    public MyDate getDate() {
        return date;
    }

    /**
     * Getter of town
     *
     * @return
     */
    public Town getTown() {
        return town;
    }

    /**
     * Getter of minimumYear
     *
     * @return
     */
    public static int getMinimumYear() {
        return minimumYear;
    }

    /**
     * Function addProof : add a String proof to the list proofs
     *
     * @param proof
     */
    public void addProof(String proof) {
        proofs.add(proof);
    }

    /**
     * Getter of getProofs
     *
     * @return
     */
    public List<String> getProofs() {
        return proofs;
    }
}
