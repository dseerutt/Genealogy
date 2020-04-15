package Genealogy.Model.Gedcom;

import Genealogy.Model.Exception.ParsingException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * PersonNameComparator test class
 */
public class PersonNameComparatorTest {

    /**
     * compareTest test class : test sorting of Person ArrayList
     *
     * @throws ParsingException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void compareTest() throws ParsingException, NoSuchFieldException, IllegalAccessException {
        //Init
        Genealogy genealogy = new Genealogy();
        Person person1 = new Person(genealogy, null);
        Person person2 = new Person(genealogy, null);
        Person person3 = new Person(genealogy, null);
        ArrayList<Person> persons = new ArrayList<>();
        persons.add(person1);
        persons.add(person2);
        persons.add(person3);

        //Reflection init
        Field nameField = person1.getClass().getDeclaredField("name");
        Field surnameField = person1.getClass().getDeclaredField("surname");
        nameField.setAccessible(true);
        surnameField.setAccessible(true);

        //Reflection set
        nameField.set(person1, "Pierre");
        nameField.set(person2, "Marie");
        nameField.set(person3, "Ventura");
        surnameField.set(person1, "Jean");
        surnameField.set(person2, "Georgette");
        surnameField.set(person3, "Arnaud");

        //Launch
        Collections.sort(persons, new PersonNameComparator());

        //Verification
        String result = "";
        for (Person person : persons) {
            result += "" + person.getFullName() + " ";
        }
        assertEquals("Georgette Marie Jean Pierre Arnaud Ventura ", result);
    }
}