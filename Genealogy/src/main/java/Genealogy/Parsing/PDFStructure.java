package Genealogy.Parsing;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PDFStructure class : class that hosts String PDF sources
 */
public class PDFStructure {
    /**
     * String set birth sources
     */
    private Set<String> birth = new HashSet<>();
    /**
     * String set unions sources
     */
    private Set<String> unions = new HashSet<>();
    /**
     * String set death sources
     */
    private Set<String> death = new HashSet<>();

    /**
     * Birth getter
     *
     * @return
     */
    public Set<String> getBirth() {
        return birth;
    }

    /**
     * Birth setter
     *
     * @param birth
     */
    public void setBirth(HashSet<String> birth) {
        this.birth = birth;
    }

    /**
     * Unions getter
     *
     * @return
     */
    public Set<String> getUnions() {
        return unions;
    }

    /**
     * Unions setter
     *
     * @param unions
     */
    public void setUnions(HashSet<String> unions) {
        this.unions = unions;
    }

    /**
     * Death getter
     *
     * @return
     */
    public Set<String> getDeath() {
        return death;
    }

    /**
     * Death setter
     *
     * @param death
     */
    public void setDeath(HashSet<String> death) {
        this.death = death;
    }

    /**
     * Function addPDFBirth : add String source path to set of births
     *
     * @param PDFPath
     */
    public void addPDFBirth(String PDFPath) {
        birth.add(PDFPath);
    }

    /**
     * Function addPDFUnion : add String source path to set of unions
     *
     * @param PDFPath
     */
    public void addPDFUnion(String PDFPath) {
        unions.add(PDFPath);
    }

    /**
     * Function addPDFDeath : add String source path to set of deaths
     *
     * @param PDFPath
     */
    public void addPDFDeath(String PDFPath) {
        death.add(PDFPath);
    }

    /**
     * PDFStructure Constructor : parse string input
     *
     * @param input
     * @return
     */
    public PDFStructure(String input) {
        if (StringUtils.isNotBlank(input)) {
            String PDFRegex = "¤PDF¤\\{birth=\\[(.*)\\], unions=\\[(.*)\\], death=\\[(.*)\\]\\}¤PDF¤";
            Pattern pattern = Pattern.compile(PDFRegex, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(input);
            if (matcher.find() && matcher.groupCount() == 3) {
                String birthPDFList = matcher.group(1);
                String marriagePDFList = matcher.group(2);
                String deathPDFList = matcher.group(3);
                if (StringUtils.isNotBlank(birthPDFList)) {
                    setBirth(convertStringToSet(birthPDFList));
                }
                if (StringUtils.isNotBlank(marriagePDFList)) {
                    setUnions(convertStringToSet(marriagePDFList));
                }
                if (StringUtils.isNotBlank(deathPDFList)) {
                    setDeath(convertStringToSet(deathPDFList));
                }
            }
        }
    }

    /**
     * Function isEmpty : test if the structure is empty, all the set inside empty
     *
     * @return
     */
    public boolean isEmpty() {
        return (birth.isEmpty() && unions.isEmpty() && death.isEmpty());
    }

    /**
     * Function toString : classic print of the object
     *
     * @return
     */
    @Override
    public String toString() {
        return "¤PDF¤{" +
                "birth=" + birth +
                ", unions=" + unions +
                ", death=" + death +
                "}¤PDF¤";
    }

    /**
     * Function convertStringToSet : convert string input separated by commas to a set
     *
     * @param input
     * @return
     */
    public HashSet<String> convertStringToSet(String input) {
        String[] tab = input.split(", ");
        HashSet<String> result = new HashSet<>();
        for (int i = 0; i < tab.length; i++) {
            result.add(tab[i]);
        }
        return result;
    }

}
