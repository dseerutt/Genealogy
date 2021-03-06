package Genealogy.URLConnexion.Geneanet;

import Genealogy.Model.Exception.RepublicanDateOutOfRangeException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * GeneanetConverter test class
 */
public class GeneanetConverterTest {

    /**
     * Test parseRepublicanDate : test conversion from republican calendar to gregorian calendar
     *
     * @throws Exception
     */
    @Test
    public void parseRepublicanDateTest() throws Exception {
        //init
        GeneanetConverter converter = GeneanetConverter.getInstance();

        //verifications
        assertEquals("22/09/1792", converter.parseRepublicanDate("1 vendémiaire an I").toString());
        assertEquals("06/10/1793", converter.parseRepublicanDate("15 vendémiaire an II").toString());
        assertEquals("29/01/1795", converter.parseRepublicanDate("10 pluviose an III").toString());
        assertEquals("18/08/1799", converter.parseRepublicanDate("1 fructidor an VII").toString());
        assertEquals("09/09/1805", converter.parseRepublicanDate("22 fructidor an XIII").toString());
        try {
            converter.parseRepublicanDate("22 fructidor an XV");
            fail("Fail out of range test");
        } catch (RepublicanDateOutOfRangeException e) {
            assertEquals("Date 22 fructidor an XV out of range", e.getMessage());
        }
    }

}