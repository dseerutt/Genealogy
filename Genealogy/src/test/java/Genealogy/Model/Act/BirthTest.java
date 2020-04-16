package Genealogy.Model.Act;

import Genealogy.Model.Date.FullDate;
import Genealogy.Model.Exception.ParsingException;
import Genealogy.Model.Gedcom.Person;
import Genealogy.Model.Gedcom.Town;
import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for Birth
 */
public class BirthTest {

    /**
     * toStringPrettyPrint test
     * Test the pretty print of Birth
     *
     * @throws ParsingException
     * @throws ParseException
     */
    @Test
    public void toStringPrettyPrintTest() throws ParsingException, ParseException {
        Person person = new Person(null);
        Birth birth = new Birth(person, new FullDate("05 MAR 2020"), new Town("Saintes", "Charente-Maritime"));
        assertEquals("born on 05/03/2020 at Saintes (Charente-Maritime)", birth.toStringPrettyPrint());
    }
}