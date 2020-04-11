package Genealogy.Parsing;

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
     * Classic function toString
     *
     * @return
     */
    @Override
    public String toString() {
        return "ParsingStructure{" +
                "number=" + number +
                ", id='" + fieldName + '\'' +
                ", text='" + fieldValue + '\'' +
                '}';
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
