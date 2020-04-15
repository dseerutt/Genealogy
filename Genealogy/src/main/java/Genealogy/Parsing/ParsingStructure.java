package Genealogy.Parsing;

import org.apache.commons.lang3.StringUtils;

/**
 * ParsingStructure class to represent a line in the gedcom file
 */
public class ParsingStructure {
    /**
     * Id number of the line
     */
    private int number;
    /**
     * String name of the field
     */
    private String fieldName;
    /**
     * String value of the field
     */
    private String fieldValue;

    /**
     * ParsingStructure constructor
     *
     * @param number
     * @param fieldName
     * @param fieldValue
     */
    public ParsingStructure(int number, String fieldName, String fieldValue) {
        this.number = number;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /**
     * Function toStringComplete : to String complete
     *
     * @return
     */
    public String toStringComplete() {
        return "ParsingStructure{" +
                "number=" + number +
                ", id='" + fieldName + '\'' +
                ", text='" + fieldValue + '\'' +
                '}';
    }

    /**
     * Function toString with no displaying of empty elements
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder().append(number);
        if (fieldName.equals("CONT")) {
            result = result.append(" ").append(fieldName).append(" ");
            if (!StringUtils.isBlank(fieldValue)) {
                result = result.append(fieldValue);
            }
        } else {
            if (!StringUtils.isBlank(fieldName)) {
                result = result.append(" ").append(fieldName);
            }
            if (!StringUtils.isBlank(fieldValue)) {
                result = result.append(" ").append(fieldValue);
            }
        }
        return result.toString();
    }

    /**
     * Number getter
     *
     * @return
     */
    public int getNumber() {
        return number;
    }

    /**
     * FieldName getter
     *
     * @return
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * FieldValue getter
     *
     * @return
     */
    public String getFieldValue() {
        return fieldValue;
    }
}
