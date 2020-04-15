package Genealogy.Model.Act;

import Genealogy.Model.Act.Enum.UnionType;
import Genealogy.Model.Date.FullDate;
import Genealogy.Model.Exception.ParsingException;
import Genealogy.Model.Gedcom.Genealogy;
import Genealogy.Model.Gedcom.Person;
import Genealogy.Model.Gedcom.Town;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UnionTest {

    /**
     * toStringPrettyPrint test
     * Test the pretty print of Christening
     *
     * @throws ParsingException
     * @throws ParseException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void toStringPrettyPrintTest() throws ParsingException, ParseException, NoSuchFieldException, IllegalAccessException {
        //Init
        Genealogy genealogy = new Genealogy();
        Person person = new Person(genealogy, null);
        Person partner = new Person(genealogy, null);
        Union union = new Union(person, partner, new FullDate("05 MAR 2020"), new Town("Saintes", "Charente-Maritime"), UnionType.HETERO_MAR);

        //Reflection init
        Field idField = person.getClass().getDeclaredField("id");
        Field nameField = person.getClass().getDeclaredField("name");
        Field surnameField = person.getClass().getDeclaredField("surname");
        idField.setAccessible(true);
        nameField.setAccessible(true);
        surnameField.setAccessible(true);

        //Reflection set
        idField.set(person, "1");
        idField.set(partner, "2");
        nameField.set(person, "Pierre");
        nameField.set(partner, "Marie");
        surnameField.set(person, "Jean");
        surnameField.set(partner, "Georgette");

        //Verification
        assertEquals("HETERO_MAR with Georgette Marie on 05/03/2020 at Saintes (Charente-Maritime)", union.toStringPrettyPrint("1"));
        assertEquals("HETERO_MAR with Jean Pierre on 05/03/2020 at Saintes (Charente-Maritime)", union.toStringPrettyPrint("2"));
        assertNull(union.toStringPrettyPrint("3"));
    }

    /**
     * Test getOtherPerson(Person) : getting back the other person of the union from a parameter Person, null elsewhere
     *
     * @throws ParsingException
     * @throws NoSuchFieldException
     * @throws ParseException
     * @throws IllegalAccessException
     */
    @Test
    public void getOtherPersonTestPerson() throws ParsingException, NoSuchFieldException, ParseException, IllegalAccessException {
        //Init
        Genealogy genealogy = new Genealogy();
        Person person = new Person(genealogy, null);
        Person partner = new Person(genealogy, null);
        Person otherPerson = new Person(genealogy, null);
        Union union = new Union(person, partner, new FullDate("05 MAR 2020"), new Town("Saintes", "Charente-Maritime"), UnionType.HETERO_MAR);

        //Reflection init
        Field idField = person.getClass().getDeclaredField("id");
        idField.setAccessible(true);

        //Reflection set
        idField.set(person, "1");
        idField.set(partner, "2");

        //Verification
        assertEquals(partner, union.getOtherPerson(person));
        assertEquals(person, union.getOtherPerson(partner));
        assertNull(union.getOtherPerson(otherPerson));
    }

    /**
     * Test getOtherPerson(String) : getting back the other person of the union from a parameter String, null elsewhere
     *
     * @throws ParsingException
     * @throws NoSuchFieldException
     * @throws ParseException
     * @throws IllegalAccessException
     */
    @Test
    public void testGetOtherPersonTestId() throws ParsingException, ParseException, NoSuchFieldException, IllegalAccessException {
        //Init
        Genealogy genealogy = new Genealogy();
        Person person = new Person(genealogy, null);
        Person partner = new Person(genealogy, null);
        Union union = new Union(person, partner, new FullDate("05 MAR 2020"), new Town("Saintes", "Charente-Maritime"), UnionType.HETERO_MAR);

        //Reflection init
        Field idField = person.getClass().getDeclaredField("id");
        idField.setAccessible(true);

        //Reflection set
        idField.set(person, "1");
        idField.set(partner, "2");

        //Verification
        assertEquals(partner, union.getOtherPerson("1"));
        assertEquals(person, union.getOtherPerson("2"));
        assertNull(union.getOtherPerson("3"));
    }

    /**
     * Test getNecessaryResearch : test main possibilities of Union missing elements, checking the null fields
     *
     * @throws ParsingException
     * @throws ParseException
     */
    @Test
    public void getNecessaryResearchTest() throws ParsingException, ParseException {
        //Init
        Genealogy genealogy = new Genealogy();
        Person person = new Person(genealogy, null);
        Person partner = new Person(genealogy, null);
        Union union1 = new Union(person, partner, new FullDate("05 MAR 2020"), new Town("Saintes", "Charente-Maritime"), UnionType.HETERO_MAR);
        Union union2 = new Union(null, partner, new FullDate("05 MAR 2020"), new Town("Saintes", "Charente-Maritime"), UnionType.HETERO_MAR);
        Union union3 = new Union(person, null, new FullDate("05 MAR 2020"), new Town("Saintes", "Charente-Maritime"), UnionType.HETERO_MAR);
        Union union4 = new Union(person, partner, null, new Town("Saintes", "Charente-Maritime"), UnionType.HETERO_MAR);
        Union union5 = new Union(person, partner, new FullDate("05 MAR 2020"), null, UnionType.HETERO_MAR);
        Union union6 = new Union(person, partner, new FullDate("05 MAR 2020"), new Town("Saintes", "Charente-Maritime"), null);
        Union union7 = new Union(null, null, null, null, null);

        assertNull(union1.getNecessaryResearch());
        assertEquals("person", union2.getNecessaryResearch());
        assertEquals("partner", union3.getNecessaryResearch());
        assertEquals("date", union4.getNecessaryResearch());
        assertEquals("town", union5.getNecessaryResearch());
        assertEquals("unionType", union6.getNecessaryResearch());
        assertEquals("date town person partner unionType", union7.getNecessaryResearch());
    }
}