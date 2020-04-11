package Genealogy.Model.Act.Enum;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test of enum UnionType
 */
public class UnionTypeTest {

    /**
     * Test of parseUnionType : check if the objects are properly parsed
     */
    @Test
    public void parseUnionTypeTest() {
        //init
        UnionType unionTypeHetero = UnionType.parseUnionType("");
        UnionType unionTypeHomo = UnionType.parseUnionType("F");
        UnionType unionTypeCohab = UnionType.parseUnionType("NOT MARRIED");
        UnionType unionTypeDivorce = UnionType.parseUnionType("D");

        //Verification
        assertEquals(UnionType.HETERO_MAR, unionTypeHetero);
        assertEquals(UnionType.HOMO_MAR, unionTypeHomo);
        assertEquals(UnionType.COHABITATION, unionTypeCohab);
        assertEquals(UnionType.DIVORCE, unionTypeDivorce);
    }
}