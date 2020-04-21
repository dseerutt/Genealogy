package Genealogy.Model.Gedcom;

import Genealogy.Model.Act.Birth;
import Genealogy.Model.Act.Death;
import Genealogy.Model.Act.Enum.ActType;
import Genealogy.Model.Act.Enum.UnionType;
import Genealogy.Model.Act.Union;
import Genealogy.Model.Date.FullDate;
import Genealogy.Model.Date.MyDate;
import Genealogy.Model.Date.YearDate;
import Genealogy.Model.Exception.ParsingException;
import Genealogy.Parsing.MyGedcomReader;
import Genealogy.Parsing.PDFStructure;
import Genealogy.URLConnexion.Serializer;
import javafx.util.Pair;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;

import static Genealogy.Model.Gedcom.Genealogy.genealogy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;

/**
 * Person test class
 */
public class PersonTest {

    /**
     * GetFullName test : print surname and name with a composed name and surname and an empty/null surname
     *
     * @throws ParsingException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void getFullNameTest() throws ParsingException, NoSuchFieldException, IllegalAccessException {
        //init
        Person personClassic = new Person(null);
        Person personNullSurname = new Person(null);
        Person personEmptySurname = new Person(null);
        //reflection name surname
        Field nameField = personClassic.getClass().getDeclaredField("name");
        nameField.setAccessible(true);
        Field surnameField;
        surnameField = personClassic.getClass().getDeclaredField("surname");
        surnameField.setAccessible(true);
        nameField.set(personClassic, "Pierre du village");
        surnameField.set(personClassic, "Jean-Marie");
        nameField.set(personEmptySurname, "Pierre");
        nameField.set(personNullSurname, "Pierrot");
        surnameField.set(personEmptySurname, "");

        //launch and verification
        assertEquals("Jean-Marie Pierre du village", personClassic.getFullName());
        assertEquals("Pierrot", personNullSurname.getFullName());
        assertEquals("Pierre", personEmptySurname.getFullName());

    }

    /**
     * GetFullNameInverted test : print name and surname with a composed name and surname and an empty/null name
     *
     * @throws ParsingException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void getFullNameInvertedTest() throws ParsingException, NoSuchFieldException, IllegalAccessException {
        //init
        Person personClassic = new Person(null);
        Person personNullName = new Person(null);
        Person personEmptyName = new Person(null);
        //reflection name surname
        Field nameField = personClassic.getClass().getDeclaredField("name");
        nameField.setAccessible(true);
        Field surnameField = personClassic.getClass().getDeclaredField("surname");
        surnameField.setAccessible(true);
        surnameField.set(personClassic, "Pierre");
        nameField.set(personClassic, "Georges du marché");
        surnameField.set(personEmptyName, "Pierre");
        surnameField.set(personNullName, "Pierrot");
        nameField.set(personEmptyName, "");

        //launch and verification
        assertEquals("Georges du marché Pierre", personClassic.getFullNameInverted());
        assertEquals("Pierrot", personNullName.getFullNameInverted());
        assertEquals("Pierre", personEmptyName.getFullNameInverted());
    }

    /**
     * initLifespanPair test : test 1(null person), 2(birth), 3(death), 4(birth, death), 5(birth, death, union),
     * 6(birth, 2*death, union), 7(union and other act without town), 8(union and other act without date)
     *
     * @throws ParsingException
     * @throws NoSuchFieldException
     * @throws ParseException
     * @throws IllegalAccessException
     */
    @Test
    public void initLifespanPairsTest() throws ParsingException, NoSuchFieldException, ParseException, IllegalAccessException {
        //init null person
        Person personNull1 = new Person(null);
        //reflection name surname
        Field birthField = personNull1.getClass().getDeclaredField("birth");
        birthField.setAccessible(true);
        Field deathField = personNull1.getClass().getDeclaredField("death");
        deathField.setAccessible(true);
        //init birth only person
        Person personBirth2 = new Person(null);
        Birth birth2 = new Birth(personBirth2, new FullDate("01 MAR 1900"), new Town("Ville 1", "Département"));
        birthField.set(personBirth2, birth2);
        //init death only person
        Person personDeath3 = new Person(null);
        Death death3 = new Death(personDeath3, new FullDate("02 MAR 1900"), new Town("Ville 2", "Département"));
        deathField.set(personDeath3, death3);
        //init birth and death person
        Person personBirthDeath4 = new Person(null);
        Birth birth4 = new Birth(personBirthDeath4, new FullDate("03 MAR 1900"), new Town("Ville 3", "Département"));
        birthField.set(personBirthDeath4, birth4);
        Death death4 = new Death(personBirthDeath4, new FullDate("04 MAR 1900"), new Town("Ville 4", "Département"));
        deathField.set(personBirthDeath4, death4);
        //init birth, death and union person
        Person personBirthUnionDeath5 = new Person(null);
        Birth birth5 = new Birth(personBirthDeath4, new FullDate("05 MAR 1900"), new Town("Ville 5", "Département"));
        birthField.set(personBirthUnionDeath5, birth5);
        Death death5 = new Death(personBirthDeath4, new FullDate("07 MAR 1900"), new Town("Ville 6", "Département"));
        deathField.set(personBirthUnionDeath5, death5);
        Person partner = new Person(null);
        Union union5 = new Union(personBirthUnionDeath5, partner, new FullDate("06 MAR 1900"), new Town("Ville 7", "Département"), UnionType.HETERO_MAR);
        personBirthUnionDeath5.addUnion(union5);
        //init birth, death and double union person
        Person personBirthDoubleUnionDeath6 = new Person(null);
        Birth birth6 = new Birth(personBirthDoubleUnionDeath6, new FullDate("08 MAR 1900"), new Town("Ville 8", "Département"));
        birthField.set(personBirthDoubleUnionDeath6, birth6);
        Death death6 = new Death(personBirthDoubleUnionDeath6, new FullDate("11 MAR 1900"), new Town("Ville 11", "Département"));
        deathField.set(personBirthDoubleUnionDeath6, death6);
        Union union6V1 = new Union(personBirthDoubleUnionDeath6, partner, new FullDate("09 MAR 1900"), new Town("Ville 9", "Département"), UnionType.HETERO_MAR);
        personBirthDoubleUnionDeath6.addUnion(union6V1);
        Union union6V2 = new Union(personBirthDoubleUnionDeath6, partner, new FullDate("10 MAR 1900"), new Town("Ville 10", "Département"), UnionType.HETERO_MAR);
        personBirthDoubleUnionDeath6.addUnion(union6V2);
        //init empty town birth, death, union and a correct union
        Person personKOTown7 = new Person(null);
        Birth birth7 = new Birth(personKOTown7, new FullDate("10 MAR 1900"), null);
        birthField.set(personKOTown7, birth7);
        Death death7 = new Death(personKOTown7, new FullDate("10 MAR 1900"), null);
        deathField.set(personKOTown7, death7);
        Union union7V1 = new Union(personKOTown7, partner, new FullDate("10 MAR 1900"), null, UnionType.HETERO_MAR);
        personKOTown7.addUnion(union7V1);
        Union union7V2 = new Union(personKOTown7, partner, new FullDate("12 MAR 1900"), new Town("Ville 12", "Département"), UnionType.HETERO_MAR);
        personKOTown7.addUnion(union7V2);
        //init empty date birth, death, union and a correct union
        Person personKoDate8 = new Person(null);
        Birth birth8 = new Birth(personKoDate8, null, new Town("Ville 5", "Département"));
        birthField.set(personKoDate8, birth8);
        Death death8 = new Death(personKoDate8, null, new Town("Ville 6", "Département"));
        deathField.set(personKoDate8, death8);
        Union union8V1 = new Union(personKoDate8, partner, null, new Town("Ville 7", "Département"), UnionType.HETERO_MAR);
        personKoDate8.addUnion(union8V1);
        Union union8V2 = new Union(personKoDate8, partner, new FullDate("13 MAR 1900"), new Town("Ville 13", "Département"), UnionType.HETERO_MAR);
        personKoDate8.addUnion(union8V2);

        //launch and init
        ArrayList<Pair<MyDate, Town>> pairsNull1 = personNull1.initLifespanPairs();
        ArrayList<Pair<MyDate, Town>> pairsBirth2 = personBirth2.initLifespanPairs();
        ArrayList<Pair<MyDate, Town>> pairsDeath3 = personDeath3.initLifespanPairs();
        ArrayList<Pair<MyDate, Town>> pairsBirthDeath4 = personBirthDeath4.initLifespanPairs();
        ArrayList<Pair<MyDate, Town>> pairsBirthUnionDeath5 = personBirthUnionDeath5.initLifespanPairs();
        ArrayList<Pair<MyDate, Town>> pairsBirthDoubleUnionDeath6 = personBirthDoubleUnionDeath6.initLifespanPairs();
        ArrayList<Pair<MyDate, Town>> pairsKOTown7 = personKOTown7.initLifespanPairs();
        ArrayList<Pair<MyDate, Town>> pairsKoDate8 = personKoDate8.initLifespanPairs();

        //verification
        assertTrue(pairsNull1.isEmpty());
        assertEquals(1, pairsBirth2.size());
        assertTrue(pairsBirth2.contains(new Pair<>(new FullDate("01 MAR 1900"), new Town("Ville 1", "Département"))));
        assertEquals(1, pairsDeath3.size());
        assertTrue(pairsDeath3.contains(new Pair<>(new FullDate("02 MAR 1900"), new Town("Ville 2", "Département"))));
        assertEquals(1, pairsDeath3.size());
        assertTrue(pairsDeath3.contains(new Pair<>(new FullDate("02 MAR 1900"), new Town("Ville 2", "Département"))));
        assertEquals(2, pairsBirthDeath4.size());
        assertTrue(pairsBirthDeath4.contains(new Pair<>(new FullDate("03 MAR 1900"), new Town("Ville 3", "Département"))));
        assertTrue(pairsBirthDeath4.contains(new Pair<>(new FullDate("04 MAR 1900"), new Town("Ville 4", "Département"))));
        assertEquals(3, pairsBirthUnionDeath5.size());
        assertTrue(pairsBirthUnionDeath5.contains(new Pair<>(new FullDate("05 MAR 1900"), new Town("Ville 5", "Département"))));
        assertTrue(pairsBirthUnionDeath5.contains(new Pair<>(new FullDate("07 MAR 1900"), new Town("Ville 6", "Département"))));
        assertTrue(pairsBirthUnionDeath5.contains(new Pair<>(new FullDate("06 MAR 1900"), new Town("Ville 7", "Département"))));
        assertEquals(4, pairsBirthDoubleUnionDeath6.size());
        assertTrue(pairsBirthDoubleUnionDeath6.contains(new Pair<>(new FullDate("08 MAR 1900"), new Town("Ville 8", "Département"))));
        assertTrue(pairsBirthDoubleUnionDeath6.contains(new Pair<>(new FullDate("09 MAR 1900"), new Town("Ville 9", "Département"))));
        assertTrue(pairsBirthDoubleUnionDeath6.contains(new Pair<>(new FullDate("10 MAR 1900"), new Town("Ville 10", "Département"))));
        assertTrue(pairsBirthDoubleUnionDeath6.contains(new Pair<>(new FullDate("11 MAR 1900"), new Town("Ville 11", "Département"))));
        assertEquals(1, pairsKOTown7.size());
        assertTrue(pairsKOTown7.contains(new Pair<>(new FullDate("12 MAR 1900"), new Town("Ville 12", "Département"))));
        assertEquals(1, pairsKoDate8.size());
        assertTrue(pairsKoDate8.contains(new Pair<>(new FullDate("13 MAR 1900"), new Town("Ville 13", "Département"))));
    }

    /**
     * getProofUnionSizeTest test : test with no union to 3 unions with proofs gradually
     *
     * @throws ParsingException
     * @throws ParseException
     */
    @Test
    public void getProofUnionSizeTest() throws ParsingException, ParseException {
        Person person = new Person(null);
        Person partner1 = new Person(null);
        Person partner2 = new Person(null);
        Person partner3 = new Person(null);
        assertEquals(0, person.getProofUnionSize());
        Union union1 = new Union(person, partner1, new FullDate("05 MAR 2020"), new Town("Saintes", "Charente-Maritime"), UnionType.HETERO_MAR);
        person.addUnion(union1);
        assertEquals(0, person.getProofUnionSize());
        Union union2 = new Union(person, partner2, new FullDate("05 MAR 2020"), new Town("Saintes", "Charente-Maritime"), UnionType.HETERO_MAR);
        person.addUnion(union2);
        assertEquals(0, person.getProofUnionSize());
        union1.addProof("Proof1");
        assertEquals(1, person.getProofUnionSize());
        Union union3 = new Union(person, partner3, new FullDate("05 MAR 2020"), new Town("Saintes", "Charente-Maritime"), UnionType.HETERO_MAR);
        person.addUnion(union3);
        assertEquals(1, person.getProofUnionSize());
        union2.addProof("Proof2");
        assertEquals(2, person.getProofUnionSize());
        union3.addProof("Proof3");
        assertEquals(3, person.getProofUnionSize());
    }

    /**
     * Test savePDFCommentAndFile : test the ParsingStructures contents after modifications on PDFStructures
     * Test with no comments, a comment, a PDFStructure, a PDFStructure and a comment
     *
     * @throws ParsingException
     * @throws IOException
     */
    @Test
    public void savePDFCommentAndFileTest() throws ParsingException, IOException, NoSuchFieldException, IllegalAccessException {
        //init
        String path = "src/test/resources/savePDFCommentAndFile.gedTest";
        String pathNewFile = "src/test/resources/savePDFCommentAndFileTest.gedTest";
        File file = new File(path);
        MyGedcomReader myGedcomReader = new MyGedcomReader();
        Serializer serializer = new Serializer();

        //init
        genealogy = myGedcomReader.read(file.getAbsolutePath());
        genealogy.parseContents();
        Field pathField = genealogy.getClass().getDeclaredField("defaultFilePath");
        pathField.setAccessible(true);
        pathField.set(genealogy, pathNewFile);
        //I1 : no comment
        Person personNoComment = genealogy.findPersonById("I1");
        personNoComment.getPdfStructure().addPDFBirth("birthProof");
        //I4 : comments already
        Person personComment = genealogy.findPersonById("I4");
        personComment.getPdfStructure().addPDFBirth("birthProof");
        //I28 : PDFStructure already
        Person personPDFStructure = genealogy.findPersonById("I28");
        personPDFStructure.getPdfStructure().addPDFBirth("birthProof");
        //I26 : comments and PDFStructure already
        Person personBoth = genealogy.findPersonById("I26");
        personBoth.getPdfStructure().addPDFBirth("birthProof");

        //launch
        personNoComment.savePDFCommentAndFile();
        personComment.savePDFCommentAndFile();
        personPDFStructure.savePDFCommentAndFile();
        personBoth.savePDFCommentAndFile();

        //remove generated file
        File fileToRemove = new File(pathNewFile);
        fileToRemove.delete();

        //verification
        assertEquals(92, genealogy.getContents().size());
        assertEquals("1 NOTE ¤PDF¤{birth=[birthProof], unions=[], death=[]}¤PDF¤", genealogy.getContents().get("I1").get(10).toString());
        assertEquals(11, genealogy.getContents().get("I1").size());
        assertEquals("1 NOTE ¤PDF¤{birth=[birthProof], unions=[], death=[]}¤PDF¤", genealogy.getContents().get("I4").get(24).toString());
        assertEquals("2 CONT Premier commentaire", genealogy.getContents().get("I4").get(25).toString());
        assertEquals("2 CONT Second commentaire", genealogy.getContents().get("I4").get(26).toString());
        assertEquals("2 CONT Troisième commentaire", genealogy.getContents().get("I4").get(27).toString());
        assertEquals(28, genealogy.getContents().get("I4").size());
        assertEquals("1 NOTE ¤PDF¤{birth=[birthProof], unions=[], death=[deathProof]}¤PDF¤", genealogy.getContents().get("I28").get(20).toString());
        assertEquals(21, genealogy.getContents().get("I28").size());
        assertEquals("1 NOTE ¤PDF¤{birth=[birthProof], unions=[], death=[deathProof]}¤PDF¤", genealogy.getContents().get("I26").get(19).toString());
        assertEquals("2 CONT Need a comment here", genealogy.getContents().get("I26").get(20).toString());
        assertEquals(21, genealogy.getContents().get("I26").size());
    }

    /**
     * addProof test : test add proof with birth * 2, unions index * 3 and death
     *
     * @throws Exception
     */
    @Test
    public void AddProofTest() throws Exception {
        //init all
        Person person = new Person(null);
        person.setPdfStructure(new PDFStructure(""));
        Union union1 = new Union(person, person, new YearDate("2005"), new Town("Saintes", "Charente-Maritime"), UnionType.HETERO_MAR);
        Union union2 = new Union(person, person, new YearDate("2006"), new Town("Saintes", "Charente-Maritime"), UnionType.HETERO_MAR);
        person.addUnion(union1);
        person.addUnion(union2);
        //birth reflection
        Birth birth = new Birth(person, new YearDate("2005"), new Town("Saintes", "Charente-Maritime"));
        Field birthField = person.getClass().getDeclaredField("birth");
        birthField.setAccessible(true);
        birthField.set(person, birth);
        //death reflection
        Death death = new Death(person, new YearDate("2005"), new Town("Saintes", "Charente-Maritime"));
        Field deathField = person.getClass().getDeclaredField("death");
        deathField.setAccessible(true);
        deathField.set(person, death);
        Genealogy genealogy = Mockito.mock(Genealogy.class);
        Genealogy.genealogy = genealogy;
        String setComment1 = "¤PDF¤{birth=[birthProof], unions=[], death=[]}¤PDF¤";
        doNothing().when(genealogy).setComments(null, setComment1);
        doNothing().when(genealogy).writeFile();

        //launch add birth proof to empty person
        person.addProof(ActType.BIRTH, "birthProof");

        //verifications birth 1
        assertEquals(setComment1, person.getPdfStructure().toString());
        assertEquals("birthProof", person.getBirth().getProofs().get(0));
        Mockito.verify(genealogy, times(1)).setComments(null, setComment1 + System.lineSeparator() + "null");
        Mockito.verify(genealogy, times(1)).writeFile();

        //init birth 2
        String setComment2 = "¤PDF¤{birth=[birthProof, birthProof2], unions=[], death=[]}¤PDF¤";
        doNothing().when(genealogy).setComments(null, setComment2);
        doNothing().when(genealogy).writeFile();

        //launch birth 2
        person.addProof(ActType.BIRTH, "birthProof2");
        assertEquals(setComment2, person.getPdfStructure().toString());
        assertEquals("birthProof", person.getBirth().getProofs().get(0));
        assertEquals("birthProof2", person.getBirth().getProofs().get(1));
        Mockito.verify(genealogy, times(1)).setComments(null, setComment2 + System.lineSeparator() + "null");
        Mockito.verify(genealogy, times(2)).writeFile();

        //init death
        String setComment3 = "¤PDF¤{birth=[birthProof, birthProof2], unions=[], death=[deathProof]}¤PDF¤";
        doNothing().when(genealogy).setComments(null, setComment3);
        doNothing().when(genealogy).writeFile();

        //launch death
        person.addProof(ActType.DEATH, "deathProof");
        assertEquals(setComment3, person.getPdfStructure().toString());
        assertEquals("deathProof", person.getDeath().getProofs().get(0));
        Mockito.verify(genealogy, times(1)).setComments(null, setComment3 + System.lineSeparator() + "null");
        Mockito.verify(genealogy, times(3)).writeFile();

        //init union 1
        String setComment4 = "¤PDF¤{birth=[birthProof, birthProof2], unions=[1)unionProof], death=[deathProof]}¤PDF¤";
        doNothing().when(genealogy).setComments(null, setComment4);
        doNothing().when(genealogy).writeFile();

        //launch union 1
        person.addProof(ActType.UNION, "unionProof", 0);
        assertEquals(setComment4, person.getPdfStructure().toString());
        assertEquals("unionProof", person.getUnions().get(0).getProofs().get(0));
        Mockito.verify(genealogy, times(1)).setComments(null, setComment4 + System.lineSeparator() + "null");
        Mockito.verify(genealogy, times(4)).writeFile();

        //init union 2
        String setComment5 = "¤PDF¤{birth=[birthProof, birthProof2], unions=[1)unionProof, 2)unionProof2], death=[deathProof]}¤PDF¤";
        doNothing().when(genealogy).setComments(null, setComment5);
        doNothing().when(genealogy).writeFile();

        //launch union 2
        person.addProof(ActType.UNION, "unionProof2", 1);
        assertEquals(setComment5, person.getPdfStructure().toString());
        assertEquals("unionProof2", person.getUnions().get(1).getProofs().get(0));
        Mockito.verify(genealogy, times(1)).setComments(null, setComment5 + System.lineSeparator() + "null");
        Mockito.verify(genealogy, times(5)).writeFile();

        //init new comment on union 1
        String setComment6 = "¤PDF¤{birth=[birthProof, birthProof2], unions=[1)unionProof, 2)unionProof2, 1)unionProof3], death=[deathProof]}¤PDF¤";
        doNothing().when(genealogy).setComments(null, setComment6);
        doNothing().when(genealogy).writeFile();

        //launch new comment on union 1
        person.addProof(ActType.UNION, "unionProof3", 0);
        assertEquals(setComment6, person.getPdfStructure().toString());
        assertEquals("unionProof3", person.getUnions().get(0).getProofs().get(1));
        Mockito.verify(genealogy, times(1)).setComments(null, setComment5 + System.lineSeparator() + "null");
        Mockito.verify(genealogy, times(6)).writeFile();
    }

    /**
     * GetAgeWithMonths & getAgeWithoutMonths test : test with no parameter, and with negative period, missing birth, and years to add, and with 0 years to add
     *
     * @throws ParsingException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws ParseException
     */
    @Test
    public void getAgeTest() throws ParsingException, NoSuchFieldException, IllegalAccessException, ParseException {
        //init
        Person person = new Person(null);
        Person personNullBirth = new Person(null);
        //birth reflection
        Birth birth = new Birth(person, new FullDate("16 AUG 1985"), new Town("Saintes", "Charente-Maritime"));
        Field birthField = person.getClass().getDeclaredField("birth");
        birthField.setAccessible(true);
        birthField.set(person, birth);

        LocalDate localDate2015 = LocalDate.parse("2015-08-16");
        LocalDate localDate1975 = LocalDate.parse("1975-08-16");
        LocalDate localDate2005 = LocalDate.parse("2005-06-16");

        //verifications
        assertEquals(-1, person.getAge());
        assertEquals(30, person.getAgeWithMonths(localDate2015, 0));
        assertEquals(30, person.getAgeWithoutMonths(localDate2015, 0));
        assertEquals(40, person.getAgeWithMonths(localDate2015, 10));
        assertEquals(40, person.getAgeWithoutMonths(localDate2015, 10));
        assertEquals(29, person.getAgeWithMonths(localDate2005, 10));
        assertEquals(30, person.getAgeWithoutMonths(localDate2005, 10));
        assertEquals(-1, person.getAgeWithMonths(localDate1975, 10));
        assertEquals(-1, person.getAgeWithoutMonths(localDate1975, 10));
        assertEquals(-1, personNullBirth.getAgeWithMonths(localDate2015, 0));
        assertEquals(-1, personNullBirth.getAgeWithoutMonths(localDate2015, 0));
    }

    /**
     * findIllegitimateChildrenTest test : test a child with 2 parents, 2 with a father only, 1 with a mother only
     *
     * @throws ParsingException
     */
    @Test
    public void findIllegitimateChildrenTest() throws ParsingException {
        //init
        Person childNormal = new Person(null);
        Person childFather = new Person(null);
        Person childMother = new Person(null);
        Person childFather2 = new Person(null);
        Person father = new Person(null);
        Person mother = new Person(null);
        childNormal.setFather(father);
        childNormal.setMother(mother);
        childFather.setFather(father);
        childMother.setMother(mother);
        father.addChild(childNormal);
        father.addChild(childFather);
        father.addChild(childFather2);
        mother.addChild(childNormal);
        mother.addChild(childMother);

        //launch
        ArrayList<Person> illegitimateChildrenFather = father.findIllegitimateChildren();
        ArrayList<Person> illegitimateChildrenMother = mother.findIllegitimateChildren();
        ArrayList<Person> illegitimateChildrenEmpty = childNormal.findIllegitimateChildren();

        //verification
        assertEquals(2, illegitimateChildrenFather.size());
        assertTrue(illegitimateChildrenFather.contains(childFather));
        assertTrue(illegitimateChildrenFather.contains(childFather2));
        assertEquals(1, illegitimateChildrenMother.size());
        assertTrue(illegitimateChildrenMother.contains(childMother));
        assertTrue(illegitimateChildrenEmpty.isEmpty());

    }

    /**
     * getDiffYears test : test differences of dates in years, positive and negative
     */
    @Test
    public void getDiffYearsTest() {
        //init
        LocalDate localDate2015 = LocalDate.parse("2015-08-16");
        LocalDate localDate1975 = LocalDate.parse("1975-08-16");
        LocalDate localDate2025 = LocalDate.parse("2025-08-16");

        //verification
        assertEquals(-40, Person.getDiffYears(localDate2015, localDate1975));
        assertEquals(40, Person.getDiffYears(localDate1975, localDate2015));
        assertEquals(10, Person.getDiffYears(localDate2015, localDate2025));
    }

    /**
     * getHalfSiblings test : test on same parents people * 2  + 2 half brother + 1 half sister + 1 father
     *
     * @throws ParsingException
     */
    @Test
    public void getHalfSiblingsTest() throws ParsingException {
        //init
        Person siblingNormal = new Person(null);
        Person siblingNormal2 = new Person(null);
        Person halfFather = new Person(null);
        Person halfMother = new Person(null);
        Person halfFather2 = new Person(null);
        Person otherPerson = new Person(null);
        Person father = new Person(null);
        Person mother = new Person(null);
        siblingNormal.setFather(father);
        siblingNormal.setMother(mother);
        siblingNormal2.setFather(father);
        siblingNormal2.setMother(mother);
        halfFather.setFather(father);
        halfMother.setMother(mother);
        halfFather2.setFather(father);
        halfFather2.setMother(otherPerson);
        father.addChild(siblingNormal);
        father.addChild(siblingNormal2);
        father.addChild(halfFather);
        father.addChild(halfFather2);
        mother.addChild(siblingNormal);
        mother.addChild(siblingNormal2);
        mother.addChild(halfMother);

        //launch
        ArrayList<Person> halfSiblingsNormal = siblingNormal.getHalfSiblings();
        ArrayList<Person> halfSiblingsNormal2 = siblingNormal2.getHalfSiblings();
        ArrayList<Person> halfSiblingsFather = father.getHalfSiblings();
        ArrayList<Person> halfSiblingHalfBrother = halfFather.getHalfSiblings();
        ArrayList<Person> halfSiblingHalfBrother2 = halfFather2.getHalfSiblings();
        ArrayList<Person> halfSiblingHalfSister = halfMother.getHalfSiblings();

        //verification
        assertEquals(3, halfSiblingsNormal.size());
        assertTrue(halfSiblingsNormal.contains(halfFather));
        assertTrue(halfSiblingsNormal.contains(halfMother));
        assertTrue(halfSiblingsNormal.contains(halfFather2));
        assertEquals(3, halfSiblingsNormal2.size());
        assertTrue(halfSiblingsNormal2.contains(halfFather));
        assertTrue(halfSiblingsNormal2.contains(halfMother));
        assertTrue(halfSiblingsNormal2.contains(halfFather2));
        assertTrue(halfSiblingsFather.isEmpty());
        assertEquals(3, halfSiblingHalfBrother.size());
        assertTrue(halfSiblingHalfBrother.contains(siblingNormal));
        assertTrue(halfSiblingHalfBrother.contains(siblingNormal2));
        assertTrue(halfSiblingHalfBrother.contains(halfFather2));
        assertEquals(3, halfSiblingHalfBrother2.size());
        assertTrue(halfSiblingHalfBrother2.contains(siblingNormal));
        assertTrue(halfSiblingHalfBrother2.contains(siblingNormal2));
        assertTrue(halfSiblingHalfBrother2.contains(halfFather));
        assertEquals(2, halfSiblingHalfSister.size());
        assertTrue(halfSiblingHalfSister.contains(siblingNormal));
        assertTrue(halfSiblingHalfSister.contains(siblingNormal2));
    }

    /**
     * getSiblings test : test on same parents people * 2  + 2 half brother + 1 half sister + 1 father
     *
     * @throws ParsingException
     */
    @Test
    public void getSiblingsTest() throws ParsingException {
        //init
        Person siblingNormal = new Person(null);
        Person siblingNormal2 = new Person(null);
        Person halfFather = new Person(null);
        Person halfMother = new Person(null);
        Person halfFather2 = new Person(null);
        Person father = new Person(null);
        Person mother = new Person(null);
        siblingNormal.setFather(father);
        siblingNormal.setMother(mother);
        siblingNormal2.setFather(father);
        siblingNormal2.setMother(mother);
        halfFather.setFather(father);
        halfMother.setMother(mother);
        father.addChild(siblingNormal);
        father.addChild(siblingNormal2);
        father.addChild(halfFather);
        father.addChild(halfFather2);
        mother.addChild(siblingNormal);
        mother.addChild(halfMother);
        mother.addChild(siblingNormal2);

        //launch
        ArrayList<Person> siblingsNormal = siblingNormal.getSiblings();
        ArrayList<Person> siblingsNormal2 = siblingNormal2.getSiblings();
        ArrayList<Person> siblingsFather = father.getSiblings();
        ArrayList<Person> siblingHalfBrother = halfFather.getSiblings();
        ArrayList<Person> siblingHalfBrother2 = halfFather2.getSiblings();
        ArrayList<Person> siblingHalfSister = halfMother.getSiblings();

        //verification
        assertEquals(1, siblingsNormal.size());
        assertTrue(siblingsNormal.contains(siblingNormal2));
        assertEquals(1, siblingsNormal2.size());
        assertTrue(siblingsNormal2.contains(siblingNormal));
        assertTrue(siblingsFather.isEmpty());
        assertTrue(siblingHalfBrother.isEmpty());
        assertTrue(siblingHalfBrother2.isEmpty());
        assertTrue(siblingHalfSister.isEmpty());
    }

    @Test
    public void printNecessaryResearchTest() throws ParsingException, ParseException, NoSuchFieldException, IllegalAccessException {
        //init void person and void person with a missing union
        Person personVoid = new Person(null);
        Person partnerVoid = new Person(null);
        Person child = new Person(null);
        Person personVoidWithChild = new Person(null);
        personVoidWithChild.addChild(child);
        child.setFather(personVoidWithChild);
        child.setMother(partnerVoid);

        //launch person and void person with a missing union
        String searchVoid = personVoid.printNecessaryResearch();
        String searchVoidWithChild = personVoidWithChild.printNecessaryResearch();

        //verification person and void person with a missing union
        assertEquals("Birth=[Date Town], Death=[Date Town]", searchVoid);
        assertEquals("Birth=[Date Town], Union=[1 Missing Union], Death=[Date Town]", searchVoidWithChild);

        //init child 2
        Person child2 = new Person(null);
        Person partnerVoid2 = new Person(null);
        child2.setFather(personVoidWithChild);
        child2.setMother(partnerVoid2);
        personVoidWithChild.addChild(child2);

        //launch child 2
        String searchVoidWithChildren = personVoidWithChild.printNecessaryResearch();

        //verification child 2
        assertEquals("Birth=[Date Town], Union=[2 Missing Unions], Death=[Date Town]", searchVoidWithChildren);

        //init child 2 with one declared union
        personVoidWithChild.addUnion(new Union(personVoidWithChild, partnerVoid, new FullDate("16 AUG 1985"), new Town("Montbouy", "Loiret"), UnionType.HETERO_MAR));

        //launch child 2 with one declared union
        String searchVoidWithUnionAndChildren = personVoidWithChild.printNecessaryResearch();

        //verification child 2 with one declared union
        assertEquals("Birth=[Date Town], Union=[1 Missing Union], Death=[Date Town]", searchVoidWithUnionAndChildren);

        //init birth without death and death without birth
        Person personMissingBirth = new Person(null);
        Person personMissingDeath = new Person(null);
        Birth birth = new Birth(personMissingDeath, new FullDate("16 AUG 1985"), new Town("Montbouy", "Loiret"));
        Death death = new Death(personMissingBirth, new FullDate("16 AUG 1985"), new Town("Montbouy", "Loiret"));
        //Birth and death refection
        Field birthField = personMissingDeath.getClass().getDeclaredField("birth");
        birthField.setAccessible(true);
        birthField.set(personMissingDeath, birth);
        Field deathField = personMissingBirth.getClass().getDeclaredField("death");
        deathField.setAccessible(true);
        deathField.set(personMissingBirth, death);

        //launch birth without death and death without birth
        String searchMissingBirth = personMissingBirth.printNecessaryResearch();
        String searchMissingDeath = personMissingDeath.printNecessaryResearch();

        //verification birth without death and death without birth
        assertEquals("Birth=[Date Town]", searchMissingBirth);
        assertEquals("Death=[Date Town]", searchMissingDeath);
    }
}