package Genealogy.Model.Act.Enum;

/**
 * Enumeration class for Union type
 */
public enum UnionType {
    HETERO_MAR,
    HOMO_MAR,
    COHABITATION,
    DIVORCE;

    /**
     * Function parseUnionType : turns String input into UnionType , default is HETERO_MAR
     *
     * @param input
     * @return
     */
    public static UnionType parseUnionType(String input) {
        switch (input) {
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
}
