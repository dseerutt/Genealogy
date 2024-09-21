package Genealogy.MapViewer.Structures;

import Genealogy.Model.Act.Birth;
import Genealogy.Model.Date.FullDate;
import Genealogy.Model.Date.MyDate;
import Genealogy.Model.Date.YearDate;
import Genealogy.Model.Exception.ParsingException;
import Genealogy.Model.Gedcom.Person;
import Genealogy.Model.Gedcom.Town;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pinpoint test class
 */
public class PinpointTest {


    /**
     * addPinpoint test : test add pinpoints of pinpointsYearMapDirectAncestors and pinpointsYearMap
     *
     * @throws ParsingException
     */
    @Test
    public void addPinpointTest() throws ParsingException {
        //init direct ancestor + other
        Pinpoint pinpointNotDA = new Pinpoint(new Town("Saintes", "Charente-Maritime"), "Robert", 25);
        Pinpoint pinpointDA = new Pinpoint(new Town("Rennes", "Ille-et-Vilaine"), "Roberta", 26);
        Person personNotDA = new Person(null);
        Person personDA = new Person(null);
        personDA.setDirectAncestor(true);
        personNotDA.setDirectAncestor(false);

        //launch direct ancestor + other
        Pinpoint.addPinpoint(2005, pinpointDA, personDA.isDirectAncestor());
        Pinpoint.addPinpoint(2005, pinpointNotDA, personNotDA.isDirectAncestor());

        //verification direct ancestor + other
        assertEquals(1, Pinpoint.getPinpointsYearMapDirectAncestors().get(2005).size());
        assertEquals("MapStructure{age=26, name='Roberta', town=Town{name='Rennes', county='Ille-et-Vilaine'}}", Pinpoint.getPinpointsYearMapDirectAncestors().get(2005).get(0).toString());
        assertEquals(2, Pinpoint.getPinpointsYearMap().get(2005).size());
        assertEquals("MapStructure{age=26, name='Roberta', town=Town{name='Rennes', county='Ille-et-Vilaine'}}", Pinpoint.getPinpointsYearMap().get(2005).get(0).toString());
        assertEquals("MapStructure{age=25, name='Robert', town=Town{name='Saintes', county='Charente-Maritime'}}", Pinpoint.getPinpointsYearMap().get(2005).get(1).toString());

        //init direct ancestor 2
        Person personDA2 = new Person(null);
        Pinpoint pinpointDA2 = new Pinpoint(new Town("Bressuire", "Deux-Sèvres"), "Michel", 27);
        personDA2.setDirectAncestor(true);

        //launch direct ancestor 2
        Pinpoint.addPinpoint(2005, pinpointDA2, personDA2.isDirectAncestor());

        //verification direct ancestor 2
        assertEquals(2, Pinpoint.getPinpointsYearMapDirectAncestors().get(2005).size());
        assertEquals("MapStructure{age=27, name='Michel', town=Town{name='Bressuire', county='Deux-Sèvres'}}", Pinpoint.getPinpointsYearMapDirectAncestors().get(2005).get(1).toString());
        assertEquals(3, Pinpoint.getPinpointsYearMap().get(2005).size());
        assertEquals("MapStructure{age=27, name='Michel', town=Town{name='Bressuire', county='Deux-Sèvres'}}", Pinpoint.getPinpointsYearMap().get(2005).get(2).toString());
    }

    @Test
    public void initPinpointsTest() throws ParsingException, NoSuchFieldException, IllegalAccessException, ParseException {
        //init empty pinpoints
        ArrayList<Pair<MyDate, Town>> lifespanPairsNull = new ArrayList<>();
        Person personNoBirth = new Person(null);
        Person personBirth1 = new Person(null);
        Person personBirth2 = new Person(null);
        Person personBirth3 = new Person(null);
        //birth reflection
        Birth birth1 = new Birth(personBirth1, new YearDate("1950"), new Town("Ville 1", "Département"));
        Birth birth2 = new Birth(personBirth2, new FullDate("01 JAN 1950"), new Town("Ville 2", "Département"));
        Birth birth3 = new Birth(personBirth3, new FullDate("01 DEC 1954"), new Town("Ville 3", "Département"));
        Field birthField = personBirth1.getClass().getDeclaredField("birth");
        birthField.setAccessible(true);
        birthField.set(personBirth1, birth1);
        birthField.set(personBirth2, birth2);
        birthField.set(personBirth3, birth3);
        //name reflection
        Field nameField = personBirth1.getClass().getDeclaredField("name");
        nameField.setAccessible(true);
        nameField.set(personBirth1, "Jean");
        nameField.set(personBirth2, "Paul");
        nameField.set(personBirth3, "Pierre");
        //init one pinpoint list
        ArrayList<Pair<MyDate, Town>> lifespanPairs1 = new ArrayList<>();
        lifespanPairs1.add(new MutablePair<>(new YearDate("1950"), new Town("Ville 1", "Département")));
        //init 2 pinpoints list
        ArrayList<Pair<MyDate, Town>> lifespanPairs2 = new ArrayList<>();
        lifespanPairs2.add(new MutablePair<>(new FullDate("01 JAN 1950"), new Town("Ville 2", "Département")));
        lifespanPairs2.add(new MutablePair<>(new FullDate("01 MAR 1955"), new Town("Ville 3", "Département")));
        //init 3 pinpoints list
        ArrayList<Pair<MyDate, Town>> lifespanPairs3 = new ArrayList<>();
        lifespanPairs3.add(new MutablePair<>(new FullDate("01 DEC 1954"), new Town("Ville 3", "Département")));
        lifespanPairs3.add(new MutablePair<>(new FullDate("01 MAR 1958"), new Town("Ville 4", "Département")));
        lifespanPairs3.add(new MutablePair<>(new FullDate("01 MAR 1962"), new Town("Ville 5", "Département")));

        //launch
        Pinpoint.initPinpoints(lifespanPairsNull, personNoBirth);
        HashMap<Integer, ArrayList<Pinpoint>> listPinPointsEmpty = deepCopy(Pinpoint.getPinpointsYearMap());
        Pinpoint.initPinpoints(lifespanPairs1, personBirth1);
        HashMap<Integer, ArrayList<Pinpoint>> listPinpoints1 = deepCopy(Pinpoint.getPinpointsYearMap());
        Pinpoint.initPinpoints(lifespanPairs2, personBirth2);
        HashMap<Integer, ArrayList<Pinpoint>> listPinpoints2 = deepCopy(Pinpoint.getPinpointsYearMap());
        Pinpoint.initPinpoints(lifespanPairs3, personBirth3);
        HashMap<Integer, ArrayList<Pinpoint>> listPinpoints3 = deepCopy(Pinpoint.getPinpointsYearMap());

        //verifications empty list
        assertTrue(listPinPointsEmpty.isEmpty());
        //verification list one pinpoint
        assertEquals(1, listPinpoints1.size());
        assertEquals(1, listPinpoints1.get(1950).size());
        assertEquals(new Pinpoint(new Town("Ville 1", "Département"), "Jean", 0), listPinpoints1.get(1950).get(0));
        //verification list 2 pinpoints
        assertEquals(2, listPinpoints2.get(1950).size());
        assertEquals(new Pinpoint(new Town("Ville 1", "Département"), "Jean", 0), listPinpoints2.get(1950).get(0));
        assertEquals(new Pinpoint(new Town("Ville 2", "Département"), "Paul", 0), listPinpoints2.get(1950).get(1));
        assertEquals(1, listPinpoints2.get(1951).size());
        assertEquals(new Pinpoint(new Town("Ville 2", "Département"), "Paul", 1), listPinpoints2.get(1951).get(0));
        assertEquals(1, listPinpoints2.get(1952).size());
        assertEquals(new Pinpoint(new Town("Ville 2", "Département"), "Paul", 2), listPinpoints2.get(1952).get(0));
        assertEquals(1, listPinpoints2.get(1953).size());
        assertEquals(new Pinpoint(new Town("Ville 2", "Département"), "Paul", 3), listPinpoints2.get(1953).get(0));
        assertEquals(1, listPinpoints2.get(1954).size());
        assertEquals(new Pinpoint(new Town("Ville 2", "Département"), "Paul", 4), listPinpoints2.get(1954).get(0));
        assertEquals(1, listPinpoints2.get(1955).size());
        assertEquals(new Pinpoint(new Town("Ville 3", "Département"), "Paul", 5), listPinpoints2.get(1955).get(0));
        //verification list 3 pinpoints
        assertEquals(2, listPinpoints3.get(1950).size());
        assertEquals(new Pinpoint(new Town("Ville 1", "Département"), "Jean", 0), listPinpoints3.get(1950).get(0));
        assertEquals(new Pinpoint(new Town("Ville 2", "Département"), "Paul", 0), listPinpoints3.get(1950).get(1));
        assertEquals(1, listPinpoints3.get(1951).size());
        assertEquals(new Pinpoint(new Town("Ville 2", "Département"), "Paul", 1), listPinpoints3.get(1951).get(0));
        assertEquals(1, listPinpoints3.get(1952).size());
        assertEquals(new Pinpoint(new Town("Ville 2", "Département"), "Paul", 2), listPinpoints3.get(1952).get(0));
        assertEquals(1, listPinpoints3.get(1953).size());
        assertEquals(new Pinpoint(new Town("Ville 2", "Département"), "Paul", 3), listPinpoints3.get(1953).get(0));
        assertEquals(2, listPinpoints3.get(1954).size());
        assertEquals(new Pinpoint(new Town("Ville 2", "Département"), "Paul", 4), listPinpoints3.get(1954).get(0));
        assertEquals(new Pinpoint(new Town("Ville 3", "Département"), "Pierre", 0), listPinpoints3.get(1954).get(1));
        assertEquals(2, listPinpoints3.get(1955).size());
        assertEquals(new Pinpoint(new Town("Ville 3", "Département"), "Paul", 5), listPinpoints3.get(1955).get(0));
        assertEquals(new Pinpoint(new Town("Ville 3", "Département"), "Pierre", 1), listPinpoints3.get(1955).get(1));
        assertEquals(1, listPinpoints3.get(1956).size());
        assertEquals(new Pinpoint(new Town("Ville 3", "Département"), "Pierre", 2), listPinpoints3.get(1956).get(0));
        assertEquals(1, listPinpoints3.get(1957).size());
        assertEquals(new Pinpoint(new Town("Ville 3", "Département"), "Pierre", 3), listPinpoints3.get(1957).get(0));
        assertEquals(1, listPinpoints3.get(1958).size());
        assertEquals(new Pinpoint(new Town("Ville 4", "Département"), "Pierre", 4), listPinpoints3.get(1958).get(0));
        assertEquals(1, listPinpoints3.get(1959).size());
        assertEquals(new Pinpoint(new Town("Ville 4", "Département"), "Pierre", 5), listPinpoints3.get(1959).get(0));
        assertEquals(1, listPinpoints3.get(1960).size());
        assertEquals(new Pinpoint(new Town("Ville 4", "Département"), "Pierre", 6), listPinpoints3.get(1960).get(0));
        assertEquals(1, listPinpoints3.get(1961).size());
        assertEquals(new Pinpoint(new Town("Ville 4", "Département"), "Pierre", 7), listPinpoints3.get(1961).get(0));
        assertEquals(1, listPinpoints3.get(1962).size());
        assertEquals(new Pinpoint(new Town("Ville 5", "Département"), "Pierre", 7), listPinpoints3.get(1962).get(0));
    }

    /**
     * Test function deepCopy : makes a deep copy of hashmaps to avoid the reuse of the arraylist pointers
     */
    private static HashMap<Integer, ArrayList<Pinpoint>> deepCopy(HashMap<Integer, ArrayList<Pinpoint>> object) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream outputStrm = new ObjectOutputStream(outputStream);
            outputStrm.writeObject(object);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            ObjectInputStream objInputStream = new ObjectInputStream(inputStream);
            return (HashMap<Integer, ArrayList<Pinpoint>>) objInputStream.readObject();
        } catch (Exception e) {
            Pinpoint.logger.error("Failed to deep copy the pinpoint", e);
            return null;
        }
    }

}