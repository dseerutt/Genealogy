package Genealogy.Model.Gedcom;

/**
 * Sex enum for persons
 */
public enum Sex {
    MALE,
    FEMALE,
    UNKNOWN;

    /**
     * Function toString : print sex
     *
     * @return
     */
    public String toString() {
        if (this != null) {
            switch (this) {
                case MALE:
                    return "M";
                case FEMALE:
                    return "F";
                case UNKNOWN:
                    return "U";
                default:
                    break;
            }
        }
        return null;
    }

    /**
     * Function toStringPrettyPrint : pretty print sex
     *
     * @return
     */
    public String toStringPrettyPrint() {
        if (this != null) {
            switch (this) {
                case MALE:
                    return "man";
                case FEMALE:
                    return "woman";
                case UNKNOWN:
                    return "sex unknown";
                default:
                    break;
            }
        }
        return null;
    }
}
