package Genealogy.Model.Gedcom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for Sex enum
 */
public class SexTest {

    /**
     * toString test : one letter only
     */
    @Test
    public void testToStringTest() {
        assertEquals("M", Sex.MALE.toString());
        assertEquals("F", Sex.FEMALE.toString());
        assertEquals("U", Sex.UNKNOWN.toString());
    }

    /**
     * toString test with pretty print
     */
    @Test
    public void toStringPrettyPrintTest() {
        assertEquals("man", Sex.MALE.toStringPrettyPrint());
        assertEquals("woman", Sex.FEMALE.toStringPrettyPrint());
        assertEquals("sex unknown", Sex.UNKNOWN.toStringPrettyPrint());
    }
}