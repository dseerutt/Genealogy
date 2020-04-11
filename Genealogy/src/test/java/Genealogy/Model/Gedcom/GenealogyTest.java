package Genealogy.Model.Gedcom;

import Genealogy.Model.Exception.ParsingException;
import Genealogy.Parsing.MyGedcomReader;
import Genealogy.URLConnexion.Serializer;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Genealogy
 */
public class GenealogyTest {

    /**
     * findPersonById test
     * Test 4 id in list and an id not found
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws ParsingException
     */
    @Test
    public void findPersonByIdTest() throws NoSuchFieldException, IllegalAccessException, ParsingException {
        //init
        Genealogy genealogy = new Genealogy();
        ArrayList<Person> persons = new ArrayList<Person>();
        Person person1 = new Person(genealogy, null, -1, -1);

        //Reflection init
        Field personsField = genealogy.getClass().getDeclaredField("persons");
        personsField.setAccessible(true);
        Field idField = person1.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        //Reflection set
        idField.set(person1, "1");
        persons.add(person1);
        Person person2 = new Person(genealogy, null, -1, -1);
        idField.set(person2, "2");
        persons.add(person2);
        Person person3 = new Person(genealogy, null, -1, -1);
        idField.set(person3, "3");
        persons.add(person3);
        Person person4 = new Person(genealogy, null, -1, -1);
        idField.set(person4, "4");
        persons.add(person4);
        personsField.set(genealogy, persons);

        //Launch
        Person newPerson1 = genealogy.findPersonById("1");
        Person newPerson2 = genealogy.findPersonById("2");
        Person newPerson3 = genealogy.findPersonById("3");
        Person newPerson4 = genealogy.findPersonById("4");
        Person newPerson5 = genealogy.findPersonById("5");

        //Control
        assertEquals(person1, newPerson1);
        assertEquals(person2, newPerson2);
        assertEquals(person3, newPerson3);
        assertEquals(person4, newPerson4);
        assertEquals(null, newPerson5);
    }

    /**
     * setDirectAncestors test
     * test direct ancestors with siblings and direct
     */
    @Test
    public void setDirectAncestorsTest() throws ParsingException {
        //init
        Genealogy genealogy = new Genealogy();
        Person person1 = new Person(genealogy, null, -1, -1);
        Person person2 = new Person(genealogy, null, -1, -1);
        Person person3 = new Person(genealogy, null, -1, -1);
        Person person4 = new Person(genealogy, null, -1, -1);
        Person person5 = new Person(genealogy, null, -1, -1);
        Person person6 = new Person(genealogy, null, -1, -1);
        Person person7 = new Person(genealogy, null, -1, -1);
        Person person8 = new Person(genealogy, null, -1, -1);
        person1.setFather(person2);
        person2.setFather(person3);
        person3.setFather(person4);
        person1.setMother(person5);
        person2.setMother(person6);
        person7.setMother(person6);
        person7.setFather(person3);

        //launch
        genealogy.setDirectAncestors(person1);

        //Control
        assertTrue(person1.isDirectAncestor());
        assertTrue(person2.isDirectAncestor());
        assertTrue(person3.isDirectAncestor());
        assertTrue(person4.isDirectAncestor());
        assertTrue(person5.isDirectAncestor());
        assertTrue(person6.isDirectAncestor());
        assertFalse(person7.isDirectAncestor());
        assertFalse(person8.isDirectAncestor());
    }

    /**
     * parseContents integration test with birth, christening, death, multiple marriages,
     * divorces, siblings, half siblings, missing parents, children and notes
     */
    @Test
    public void parseContentsTest() throws IOException, ParsingException, URISyntaxException {
        //init
        URL url = getClass().getResource("/test/testParseContents.gedTest");
        File file = new File(url.toURI());
        MyGedcomReader myGedcomReader = new MyGedcomReader();
        Serializer serializer = new Serializer();

        //launch
        Genealogy.genealogy = myGedcomReader.read(file.getAbsolutePath());
        Genealogy.genealogy.parseContents();

        //control
        assertEquals(1060, Genealogy.genealogy.getContents().size());
        assertEquals("demo data", Genealogy.genealogy.getAuthor());
        assertEquals(59, Genealogy.genealogy.getPersons().size());
        assertEquals("Person_I1{woman, name='...', surname='Martha', HETERO_MAR with Konrad Ferdinand Miller on 23/05/1874, directAncestor, child=[Gesine Miller]}", Genealogy.genealogy.getPersons().get(0).toString());
        assertEquals("Person_I2{62 y/o man, name='Beckman', surname='Bartholomaeus Ferdinand', born on 24/01/1791 at Tilsit (East Prussia), died on 10/08/1853 at Tilsit (East Prussia), profession of draper, father='Carl Friedrich Emanuel Beckman', mother='Charlotte Weber', HETERO_MAR with Minna Lehner on 19/04/1820 at Tilsit (East Prussia), child=[Kurt Friedrich Beckman]}", Genealogy.genealogy.getPersons().get(1).toString());
        assertEquals("Person_I3{man, name='Beckman', surname='Carl Friedrich Emanuel', born on 1760, profession of merchant, father='Hubertus Beckman', HETERO_MAR with Charlotte Weber on 1788, child=[Bartholomaeus Ferdinand Beckman]}", Genealogy.genealogy.getPersons().get(2).toString());
        assertEquals("Person_I4{man, name='Beckman', surname='Gregor', born on 04/03/1956 at Essen, christened on 05/03/1956 at the place of 'Sankt Reinoldi' at Dortmund with godparents='Paul Beckman, Catharine Beckman and Josephine Cook', profession of technical illustrator, father='Joseph Arthur Wilhelm Beckman', mother='Regina Martha Klara Willner', COHABITATION with Helga Timmen, child=[Sabine Timmen]}", Genealogy.genealogy.getPersons().get(3).toString());
        assertEquals("Person_I5{71 y/o man, name='Beckman', surname='Johann Friedrich', born on 03/01/1864 at Tilsit (East Prussia), died on 04/05/1935 at Allenstein (East Prussia), profession of mayor, father='Kurt Friedrich Beckman', mother='Caroline Schmidt', HETERO_MAR with Sophie Kowalski on 27/05/1889 at Allenstein (East Prussia), children=[Robert Martin Beckman, Martin Heinrich Beckman, Gertrud Catharina Beckman]}", Genealogy.genealogy.getPersons().get(4).toString());
        assertEquals("Person_I6{man, name='Beckman', surname='Joseph Arthur Wilhelm', born on 15/04/1929 at Allenstein (East Prussia), profession of clerk, father='Robert Martin Beckman', mother='Josephine Cook', HETERO_MAR with Regina Martha Klara Willner on 01/10/1953 at Dortmund, children=[Carl Beckman, Gregor Beckman]}", Genealogy.genealogy.getPersons().get(5).toString());
        assertEquals("Person_I7{man, name='Beckman', surname='Carl', born on 03/05/1955 at Dortmund, profession of teacher, father='Joseph Arthur Wilhelm Beckman', mother='Regina Martha Klara Willner', HETERO_MAR with Roswitha Touther on 07/09/1985 at Wesel}", Genealogy.genealogy.getPersons().get(6).toString());
        assertEquals("Person_I8{40 y/o man, name='Beckman', surname='Kurt Friedrich', born on 07/09/1829 at Tilsit (East Prussia), died on 23/12/1869 at Tilsit (East Prussia), profession of merchant, father='Bartholomaeus Ferdinand Beckman', mother='Minna Lehner', HETERO_MAR with Caroline Schmidt on 23/06/1862 at Tilsit (East Prussia), children=[Johann Friedrich Beckman, Francisca Minna Elisabeth Beckman, Berthold Ferdinand Beckman, Magdalena Charlotte Beckman]}", Genealogy.genealogy.getPersons().get(7).toString());
        assertEquals("Person_I9{73 y/o man, name='Beckman', surname='Robert Martin', born on 21/11/1890 at Allenstein (East Prussia), died on 02/01/1964 at Oberhausen (DÃ¼sseldorf), profession of writer, father='Johann Friedrich Beckman', mother='Sophie Kowalski', HETERO_MAR with Josephine Cook on 16/07/1923 at Allenstein (East Prussia), children=[Joseph Arthur Wilhelm Beckman, Catharine Elisabeth Beckman, Paul Arthur Ferdinand Beckman], comments='was active in the army as a commissioned officer in World War 1'}", Genealogy.genealogy.getPersons().get(8).toString());
        assertEquals("Person_I10{53 y/o woman, name='Frankikeit', surname='Emma', born on 11/03/1869 at Allenstein (East Prussia), died on 17/07/1922 at Allenstein (East Prussia), father='Ludwig Konstantin Frankikeit', mother='Leonore Elisabeth Bremer', HETERO_MAR with Eberhard Cook on 11/03/1889 at Allenstein (East Prussia), children=[Josephine Cook, Klaus Berthold Cook, Maria Caroline Cook]}", Genealogy.genealogy.getPersons().get(9).toString());
        assertEquals("Person_I11{48 y/o man, name='Cook', surname='Eberhard', born on 15/09/1867 at Allenstein (East Prussia), died on 14/05/1916 at Allenstein (East Prussia), profession of smith, father='Frank Herbert Cook', mother='Brunhilde Magarethe Dillborn', HETERO_MAR with Emma Frankikeit on 11/03/1889 at Allenstein (East Prussia), children=[Josephine Cook, Klaus Berthold Cook, Maria Caroline Cook]}", Genealogy.genealogy.getPersons().get(10).toString());
        assertEquals("Person_I12{73 y/o woman, name='Cook', surname='Josephine', born on 02/12/1897 at Allenstein (East Prussia), died on 17/06/1971 at Oberhausen (DÃ¼sseldorf), profession of seamstress, father='Eberhard Cook', mother='Emma Frankikeit', HETERO_MAR with Robert Martin Beckman on 16/07/1923 at Allenstein (East Prussia), children=[Joseph Arthur Wilhelm Beckman, Catharine Elisabeth Beckman, Paul Arthur Ferdinand Beckman]}", Genealogy.genealogy.getPersons().get(11).toString());
        assertEquals("Person_I13{82 y/o woman, name='Kowalski', surname='Sophie', born on 05/02/1864 at Allenstein (East Prussia), died on 13/12/1946 at Oberhausen (DÃ¼sseldorf), profession of wench, father='Martin Kowalski', mother='Dorothe Esser', HETERO_MAR with Johann Friedrich Beckman on 27/05/1889 at Allenstein (East Prussia), children=[Robert Martin Beckman, Martin Heinrich Beckman, Gertrud Catharina Beckman]}", Genealogy.genealogy.getPersons().get(12).toString());
        assertEquals("Person_I14{woman, name='Lehner', surname='Minna', HETERO_MAR with Bartholomaeus Ferdinand Beckman on 19/04/1820 at Tilsit (East Prussia), child=[Kurt Friedrich Beckman]}", Genealogy.genealogy.getPersons().get(13).toString());
        assertEquals("Person_I15{56 y/o woman, name='Miller', surname='Gesine', born on 28/07/1878 at Balve, died on 15/09/1934 at Lendringsen (Sauerland), father='Konrad Ferdinand Miller', mother='Martha ...', HETERO_MAR with Jacob Willner on 13/11/1902 at Lendringsen (Sauerland), children=[Tobias Oswald Willner, Hermann Willner]}", Genealogy.genealogy.getPersons().get(14).toString());
        assertEquals("Person_I16{man, name='Miller', surname='Konrad Ferdinand', christened on 01/06/1844 at the place of 'Thomaskirche' at Balve, father='Paul Hermann Miller', HETERO_MAR with Martha ... on 23/05/1874, child=[Gesine Miller]}", Genealogy.genealogy.getPersons().get(15).toString());
        assertEquals("Person_I17{man, name='Miller', surname='Paul Hermann', christened on 3/1811, child=[Konrad Ferdinand Miller]}", Genealogy.genealogy.getPersons().get(16).toString());
        assertEquals("Person_I18{86 y/o woman, name='Scherer', surname='Anna', born on 31/03/1880 at Iserlohn, died on 29/04/1966 at Hagen, profession of laundress, HETERO_MAR with Peter Schoepken on 07/06/1906 at Iserlohn, children=[Johanna Schoepken, Frederike Gesine Schoepken, Magdalena Hermine Schoepken]}", Genealogy.genealogy.getPersons().get(17).toString());
        assertEquals("Person_I19{woman, name='Schmidt', surname='Caroline', HETERO_MAR with Kurt Friedrich Beckman on 23/06/1862 at Tilsit (East Prussia), children=[Johann Friedrich Beckman, Francisca Minna Elisabeth Beckman, Berthold Ferdinand Beckman, Magdalena Charlotte Beckman]}", Genealogy.genealogy.getPersons().get(18).toString());
        assertEquals("Person_I20{70 y/o woman, name='Schoepken', surname='Johanna', born on 13/05/1909 at Iserlohn, died on 27/08/1979 at Lendringsen (Sauerland), profession of cook, father='Peter Schoepken', mother='Anna Scherer', HETERO_MAR with Hermann Willner on 18/03/1930 at Lendringsen (Sauerland), children=[Regina Martha Klara Willner, Herbert Konrad Willner]}", Genealogy.genealogy.getPersons().get(19).toString());
        assertEquals("Person_I21{79 y/o man, name='Schoepken', surname='Peter', born on 01/04/1881 at Iserlohn, died on 23/10/1960 at Hagen, profession of locksmith, HETERO_MAR with Anna Scherer on 07/06/1906 at Iserlohn, children=[Johanna Schoepken, Frederike Gesine Schoepken, Magdalena Hermine Schoepken]}", Genealogy.genealogy.getPersons().get(20).toString());
        assertEquals("Person_I22{woman, name='Touther', surname='Roswitha', HETERO_MAR with Carl Beckman on 07/09/1985 at Wesel}", Genealogy.genealogy.getPersons().get(21).toString());
        assertEquals("Person_I23{woman, name='Timmen', surname='Helga', COHABITATION with Gregor Beckman, child=[Sabine Timmen]}", Genealogy.genealogy.getPersons().get(22).toString());
        assertEquals("Person_I24{woman, name='Timmen', surname='Sabine', father='Gregor Beckman', mother='Helga Timmen'}", Genealogy.genealogy.getPersons().get(23).toString());
        assertEquals("Person_I25{woman, name='Weber', surname='Charlotte', HETERO_MAR with Carl Friedrich Emanuel Beckman on 1788, child=[Bartholomaeus Ferdinand Beckman]}", Genealogy.genealogy.getPersons().get(24).toString());
        assertEquals("Person_I26{74 y/o man, name='Willner', surname='Hermann', born on 05/09/1906 at Lendringsen (Sauerland), died on 07/07/1981 at Lendringsen (Sauerland), profession of farmer, father='Jacob Willner', mother='Gesine Miller', HETERO_MAR with Johanna Schoepken on 18/03/1930 at Lendringsen (Sauerland), children=[Regina Martha Klara Willner, Herbert Konrad Willner]}", Genealogy.genealogy.getPersons().get(25).toString());
        assertEquals("Person_I27{56 y/o man, name='Willner', surname='Jacob', born on 19/03/1876 at Lendringsen (Sauerland), died on 30/01/1933 at Menden (Sauerland), profession of farmer, HETERO_MAR with Gesine Miller on 13/11/1902 at Lendringsen (Sauerland), children=[Tobias Oswald Willner, Hermann Willner]}", Genealogy.genealogy.getPersons().get(26).toString());
        assertEquals("Person_I28{woman, name='Willner', surname='Regina Martha Klara', born on 13/02/1931 at Lendringsen (Sauerland), profession of sales clerk, father='Hermann Willner', mother='Johanna Schoepken', HETERO_MAR with Joseph Arthur Wilhelm Beckman on 01/10/1953 at Dortmund, children=[Carl Beckman, Gregor Beckman]}", Genealogy.genealogy.getPersons().get(27).toString());
        assertEquals("Person_I29{man, name='Beckman', surname='Paul Arthur Ferdinand', father='Robert Martin Beckman', mother='Josephine Cook'}", Genealogy.genealogy.getPersons().get(28).toString());
        assertEquals("Person_I30{woman, name='Beckman', surname='Catharine Elisabeth', born on 1931, father='Robert Martin Beckman', mother='Josephine Cook', unions=[HETERO_MAR with Manfred Oswald Homann on 02/03/1952 at Hamburg, DIVORCE with Manfred Oswald Homann on 1954 at Hamburg, HETERO_MAR with Otto Miller on 1956 at Lunenburg (Lower Saxony), HETERO_MAR with Fritz Ferdinand Hansen on 1962 at Bremen], children=[Mathias Homann, Simone Miller]}", Genealogy.genealogy.getPersons().get(29).toString());
        assertEquals("Person_I31{man, name='Beckman', surname='Martin Heinrich', born on 1892, father='Johann Friedrich Beckman', mother='Sophie Kowalski', HETERO_MAR with Sabine Lehnhoff on 05/03/1911, children=[Willi Beckman, Frank Beckman]}", Genealogy.genealogy.getPersons().get(30).toString());
        assertEquals("Person_I32{woman, name='Beckman', surname='Gertrud Catharina', profession of housemaid, father='Johann Friedrich Beckman', mother='Sophie Kowalski'}", Genealogy.genealogy.getPersons().get(31).toString());
        assertEquals("Person_I33{man, name='Beckman', surname='Berthold Ferdinand', father='Kurt Friedrich Beckman', mother='Caroline Schmidt'}", Genealogy.genealogy.getPersons().get(32).toString());
        assertEquals("Person_I34{woman, name='Beckman', surname='Magdalena Charlotte', father='Kurt Friedrich Beckman', mother='Caroline Schmidt'}", Genealogy.genealogy.getPersons().get(33).toString());
        assertEquals("Person_I35{woman, name='Beckman', surname='Francisca Minna Elisabeth', born on 27/05/1865, father='Kurt Friedrich Beckman', mother='Caroline Schmidt'}", Genealogy.genealogy.getPersons().get(34).toString());
        assertEquals("Person_I36{man, name='Cook', surname='Klaus Berthold', father='Eberhard Cook', mother='Emma Frankikeit'}", Genealogy.genealogy.getPersons().get(35).toString());
        assertEquals("Person_I37{woman, name='Cook', surname='Maria Caroline', father='Eberhard Cook', mother='Emma Frankikeit'}", Genealogy.genealogy.getPersons().get(36).toString());
        assertEquals("Person_I38{man, name='Willner', surname='Herbert Konrad', born on 13/07/1934, father='Hermann Willner', mother='Johanna Schoepken', HETERO_MAR with Ingeborg Vieths at Bremerhaven, children=[Claudia Willner, Michael Willner]}", Genealogy.genealogy.getPersons().get(37).toString());
        assertEquals("Person_I39{man, name='Willner', surname='Tobias Oswald', born on 05/11/1904, father='Jacob Willner', mother='Gesine Miller'}", Genealogy.genealogy.getPersons().get(38).toString());
        assertEquals("Person_I40{woman, name='Schoepken', surname='Frederike Gesine', born on 04/05/1911 at Iserlohn, father='Peter Schoepken', mother='Anna Scherer'}", Genealogy.genealogy.getPersons().get(39).toString());
        assertEquals("Person_I41{woman, name='Schoepken', surname='Magdalena Hermine', born on 02/03/1913 at Iserlohn, profession of wench, father='Peter Schoepken', mother='Anna Scherer'}", Genealogy.genealogy.getPersons().get(40).toString());
        assertEquals("Person_I42{man, name='Homann', surname='Manfred Oswald', unions=[HETERO_MAR with Catharine Elisabeth Beckman on 02/03/1952 at Hamburg, DIVORCE with Catharine Elisabeth Beckman on 1954 at Hamburg], child=[Mathias Homann]}", Genealogy.genealogy.getPersons().get(41).toString());
        assertEquals("Person_I43{man, name='Miller', surname='Otto', HETERO_MAR with Catharine Elisabeth Beckman on 1956 at Lunenburg (Lower Saxony), child=[Simone Miller]}", Genealogy.genealogy.getPersons().get(42).toString());
        assertEquals("Person_I44{man, name='Homann', surname='Mathias', born on 20/09/1952 at Hamburg-Eimsbuttel, profession of teacher, father='Manfred Oswald Homann', mother='Catharine Elisabeth Beckman'}", Genealogy.genealogy.getPersons().get(43).toString());
        assertEquals("Person_I45{woman, name='Miller', surname='Simone', father='Otto Miller', mother='Catharine Elisabeth Beckman'}", Genealogy.genealogy.getPersons().get(44).toString());
        assertEquals("Person_I46{man, name='Hansen', surname='Fritz Ferdinand', HETERO_MAR with Catharine Elisabeth Beckman on 1962 at Bremen}", Genealogy.genealogy.getPersons().get(45).toString());
        assertEquals("Person_I47{woman, name='Vieths', surname='Ingeborg', HETERO_MAR with Herbert Konrad Willner at Bremerhaven, children=[Claudia Willner, Michael Willner]}", Genealogy.genealogy.getPersons().get(46).toString());
        assertEquals("Person_I48{woman, name='Willner', surname='Claudia', father='Herbert Konrad Willner', mother='Ingeborg Vieths'}", Genealogy.genealogy.getPersons().get(47).toString());
        assertEquals("Person_I49{man, name='Willner', surname='Michael', father='Herbert Konrad Willner', mother='Ingeborg Vieths'}", Genealogy.genealogy.getPersons().get(48).toString());
        assertEquals("Person_I50{woman, name='Lehnhoff', surname='Sabine', HETERO_MAR with Martin Heinrich Beckman on 05/03/1911, children=[Willi Beckman, Frank Beckman]}", Genealogy.genealogy.getPersons().get(49).toString());
        assertEquals("Person_I51{man, name='Beckman', surname='Willi', father='Martin Heinrich Beckman', mother='Sabine Lehnhoff'}", Genealogy.genealogy.getPersons().get(50).toString());
        assertEquals("Person_I52{man, name='Beckman', surname='Frank', father='Martin Heinrich Beckman', mother='Sabine Lehnhoff'}", Genealogy.genealogy.getPersons().get(51).toString());
        assertEquals("Person_I53{man, name='Kowalski', surname='Martin', christened on 04/05/1843 at the place of 'Sankt-Martin-Kirche' at Kroppenstedt with godparents='Ewald Kowalski, Emma Brandt and Peter Schmid', HETERO_MAR with Dorothe Esser, child=[Sophie Kowalski]}", Genealogy.genealogy.getPersons().get(52).toString());
        assertEquals("Person_I54{woman, name='Esser', surname='Dorothe', HETERO_MAR with Martin Kowalski, child=[Sophie Kowalski]}", Genealogy.genealogy.getPersons().get(53).toString());
        assertEquals("Person_I55{man, name='Cook', surname='Frank Herbert', HETERO_MAR with Brunhilde Magarethe Dillborn, child=[Eberhard Cook]}", Genealogy.genealogy.getPersons().get(54).toString());
        assertEquals("Person_I56{woman, name='Dillborn', surname='Brunhilde Magarethe', HETERO_MAR with Frank Herbert Cook, child=[Eberhard Cook]}", Genealogy.genealogy.getPersons().get(55).toString());
        assertEquals("Person_I57{man, name='Frankikeit', surname='Ludwig Konstantin', HETERO_MAR with Leonore Elisabeth Bremer, child=[Emma Frankikeit]}", Genealogy.genealogy.getPersons().get(56).toString());
        assertEquals("Person_I58{woman, name='Bremer', surname='Leonore Elisabeth', HETERO_MAR with Ludwig Konstantin Frankikeit, child=[Emma Frankikeit]}", Genealogy.genealogy.getPersons().get(57).toString());
        assertEquals("Person_I59{man, name='Beckman', surname='Hubertus', child=[Carl Friedrich Emanuel Beckman]}", Genealogy.genealogy.getPersons().get(58).toString());
    }
}