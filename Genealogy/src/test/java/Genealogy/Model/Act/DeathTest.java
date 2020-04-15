package Genealogy.Model.Act;

import Genealogy.Model.Date.FullDate;
import Genealogy.Model.Exception.ParsingException;
import Genealogy.Model.Gedcom.Genealogy;
import Genealogy.Model.Gedcom.Person;
import Genealogy.Model.Gedcom.Town;
import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for Death
 */
public class DeathTest {

    /**
     * toStringPrettyPrint test
     * Test the pretty print of Death
     *
     * @throws ParsingException
     * @throws ParseException
     */
    @Test
    public void toStringPrettyPrintTest() throws ParsingException, ParseException {
        Genealogy genealogy = new Genealogy();
        Person person = new Person(genealogy, null);
        Death death = new Death(person, new FullDate("05 MAR 2020"), new Town("Saintes", "Charente-Maritime"));
        assertEquals("died on 05/03/2020 at Saintes (Charente-Maritime)", death.toStringPrettyPrint());
    }
}