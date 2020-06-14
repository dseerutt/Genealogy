package Genealogy.Model.Act;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Proof class : handles proof data from proof file
 */
public class Proof {

    /**
     * The String proof date
     */
    private String date;

    /**
     * The String proof town
     */
    private String town;

    /**
     * The String proof people
     */
    private String people;

    /**
     * The String act type
     */
    private String typeAct;

    /**
     * Date getter
     *
     * @return
     */
    public String getDate() {
        return date;
    }

    /**
     * Proof regex for Proof file
     */
    private final static String proofRegex = "- (.* \\(.*\\)) (.*?) (.*?) (.*)";

    /**
     * Date setter
     *
     * @param date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Town getter
     *
     * @return
     */
    public String getTown() {
        return town;
    }

    /**
     * Town setter
     *
     * @param town
     */
    public void setTown(String town) {
        this.town = town;
    }

    /**
     * People getter
     *
     * @return
     */
    public String getPeople() {
        return people;
    }

    /**
     * People setter
     *
     * @param people
     */
    public void setPeople(String people) {
        this.people = people;
    }

    /**
     * TypeAct getter
     *
     * @return
     */
    public String getTypeAct() {
        return typeAct;
    }

    /**
     * TypeAct setter
     *
     * @param typeAct
     */
    public void setTypeAct(String typeAct) {
        this.typeAct = typeAct;
    }

    /**
     * ProofRegex getter
     *
     * @return
     */
    public static String getProofRegex() {
        return proofRegex;
    }

    /**
     * Proof constructor from all parameters
     *
     * @param date
     * @param town
     * @param people
     * @param typeAct
     */
    public Proof(String date, String town, String people, String typeAct) {
        this.date = date;
        this.town = town;
        this.people = people;
        this.typeAct = typeAct;
    }

    /**
     * Proof constructor from String input
     *
     * @param input
     */
    public Proof(String input) {
        Pattern pattern = Pattern.compile(proofRegex);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            this.town = matcher.group(1);
            this.date = matcher.group(2);
            this.typeAct = matcher.group(3);
            this.people = matcher.group(4);
        }
    }

    /**
     * ToString function
     *
     * @return
     */
    @Override
    public String toString() {
        return "Proof{" +
                "date='" + date + '\'' +
                ", town='" + town + '\'' +
                ", people='" + people + '\'' +
                ", typeAct='" + typeAct + '\'' +
                '}';
    }
}
