package Genealogy.Model.Act;

import Genealogy.Model.Act.Enum.UnionType;
import Genealogy.Model.Date.FullDate;
import Genealogy.Model.Exception.ParsingException;
import Genealogy.Model.Gedcom.Person;
import Genealogy.Model.Gedcom.Town;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test abstract class of Act
 */
public class ActTest {

    /**
     * Test of getNecessaryResearch : test main possibilities of Act missing elements, checking the null fields
     *
     * @throws ParsingException
     * @throws ParseException
     */
    @Test
    public void getNecessaryResearchTest() throws ParsingException, ParseException {
        //Init
        Person person = new Person(null);
        Act birth1 = new Birth(person, new FullDate("05 MAR 2020"), new Town("Saintes", "Charente-Maritime"));
        Act birth2 = new Birth(null, new FullDate("05 MAR 2020"), new Town("Saintes", "Charente-Maritime"));
        Act birth3 = new Birth(person, null, new Town("Saintes", "Charente-Maritime"));
        Act birth4 = new Birth(person, new FullDate("05 MAR 2020"), null);
        Act birth5 = new Birth(null, null, null);

        //Verification
        assertNull(birth1.getNecessaryResearch());
        assertEquals("person", birth2.getNecessaryResearch());
        assertEquals("date", birth3.getNecessaryResearch());
        assertEquals("town", birth4.getNecessaryResearch());
        assertEquals("person date town", birth5.getNecessaryResearch());
    }

    /**
     * Test of minimumYear within Act constructor : record the mininum year of all Acts
     *
     * @throws ParsingException
     * @throws ParseException
     */
    @Test
    public void minimumYearTest() throws ParsingException, ParseException {
        //init
        Act.minimumYear = LocalDate.now().getYear();
        Person person1 = new Person(null);
        Person person2 = new Person(null);
        Person person3 = new Person(null);
        Person person4 = new Person(null);
        Person person5 = new Person(null);

        //Verification
        assertEquals(LocalDate.now().getYear(), Act.getMinimumYear());
        Act birth1 = new Birth(person1, new FullDate("05 MAR 2019"), new Town("Saintes", "Charente-Maritime"));
        assertEquals(2019, Act.getMinimumYear());
        Act death2 = new Death(person2, new FullDate("05 MAR 2022"), new Town("Saintes", "Charente-Maritime"));
        assertEquals(2019, Act.getMinimumYear());
        Act birth3 = new Birth(person3, new FullDate("05 MAR 2015"), new Town("Saintes", "Charente-Maritime"));
        assertEquals(2015, Act.getMinimumYear());
        Act birth4 = new Union(person4, person3, new FullDate("05 MAR 2017"), new Town("Saintes", "Charente-Maritime"), UnionType.HETERO_MAR);
        assertEquals(2015, Act.getMinimumYear());
        Act birth5 = new Christening(person5, new FullDate("05 MAR 2014"), new Town("Saintes", "Charente-Maritime"));
        assertEquals(2014, Act.getMinimumYear());

    }
}