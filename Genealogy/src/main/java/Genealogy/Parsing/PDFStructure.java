package Genealogy.Parsing;

import org.apache.commons.collections.list.SetUniqueList;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PDFStructure class : class that hosts String PDF sources - uses SetUniqueList to avoid duplicate and have order
 */
public class PDFStructure {
    /**
     * String SetUniqueList birth sources
     */
    private SetUniqueList birth = SetUniqueList.decorate(new ArrayList<>());
    /**
     * String SetUniqueList unions sources
     */
    private SetUniqueList unions = SetUniqueList.decorate(new ArrayList<>());
    /**
     * String SetUniqueList death sources
     */
    private SetUniqueList death = SetUniqueList.decorate(new ArrayList<>());

    /**
     * Birth getter
     *
     * @return
     */
    public SetUniqueList getBirth() {
        return birth;
    }

    /**
     * Birth setter
     *
     * @param birth
     */
    public void setBirth(SetUniqueList birth) {
        this.birth = birth;
    }

    /**
     * Unions getter
     *
     * @return
     */
    public SetUniqueList getUnions() {
        return unions;
    }

    /**
     * Unions setter
     *
     * @param unions
     */
    public void setUnions(SetUniqueList unions) {
        this.unions = unions;
    }

    /**
     * Death getter
     *
     * @return
     */
    public SetUniqueList getDeath() {
        return death;
    }

    /**
     * Death setter
     *
     * @param death
     */
    public void setDeath(SetUniqueList death) {
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
     * Function addPDFUnion : add String source path to list of unions
     *
     * @param PDFPath
     * @param unionIndex
     */
    public void addPDFUnion(String PDFPath, int unionIndex) {
        unions.add((unionIndex + 1) + ")" + PDFPath);
    }

    /**
     * Function addPDFDeath : add String source path to list of deaths
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
     * Function convertStringToSet : convert string input separated by commas to a SetUniqueList
     *
     * @param input
     * @return
     */
    public SetUniqueList convertStringToSet(String input) {
        String[] tab = input.split(", ");
        SetUniqueList result = SetUniqueList.decorate(new ArrayList<>());
        for (int i = 0; i < tab.length; i++) {
            result.add(tab[i]);
        }
        return result;
    }

}
