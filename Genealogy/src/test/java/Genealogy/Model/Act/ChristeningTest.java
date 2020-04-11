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
 * Test class for Christening
 */
public class ChristeningTest {

    /**
     * toStringPrettyPrint test
     * Test the pretty print of Christening
     *
     * @throws ParsingException
     * @throws ParseException
     */
    @Test
    public void toStringPrettyPrintTest() throws ParsingException, ParseException {
        Genealogy genealogy = new Genealogy();
        Person person = new Person(genealogy, null, -1, -1);
        Christening christening = new Christening(person, new FullDate("05 MAR 2020"), new Town("Saintes", "Charente-Maritime"), "Super Parrain, Super Marraine", "Cathédrale de Reims");
        assertEquals("christened on 05/03/2020 at the place of 'Cathédrale de Reims' at Saintes (Charente-Maritime) with godparents='Super Parrain, Super Marraine'", christening.toStringPrettyPrint());
    }
}