package Genealogy.Model.Gedcom;

import Genealogy.MapViewer.Structures.Pinpoint;
import Genealogy.Model.Act.Birth;
import Genealogy.Model.Act.Death;
import Genealogy.Model.Act.Enum.ActType;
import Genealogy.Model.Act.Enum.UnionType;
import Genealogy.Model.Act.Union;
import Genealogy.Model.Date.FullDate;
import Genealogy.Model.Date.YearDate;
import Genealogy.Model.Exception.ParsingException;
import Genealogy.Parsing.MyGedcomReader;
import Genealogy.Parsing.PDFStructure;
import Genealogy.URLConnexion.Serializer;
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

    //@Test
    public void getPinpointsYearMapTest() {
    }

    //@Test
    public void getPinpointsYearMapDirectAncestorsTest() {
    }

    //@Test
    public void getFullNameTest() {
    }

    //@Test
    public void getFullNameInvertedTest() {
    }

    //@Test
    public void initLifespansTest() {
    }

    //@Test
    public void initLifespanPairsTest() {
    }

    //@Test
    public void initPinpointsTest() {
    }

    //@Test
    public void getProofUnionSizeTest() {
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
        personDA.addPinpoint(2005, pinpointDA);
        personNotDA.addPinpoint(2005, pinpointNotDA);

        //verification direct ancestor + other
        assertEquals(1, Person.getPinpointsYearMapDirectAncestors().get(2005).size());
        assertEquals("MapStructure{age=26, name='Roberta', town=Town{name='Rennes', detail='Ille-et-Vilaine'}}", Person.getPinpointsYearMapDirectAncestors().get(2005).get(0).toString());
        assertEquals(2, Person.getPinpointsYearMap().get(2005).size());
        assertEquals("MapStructure{age=26, name='Roberta', town=Town{name='Rennes', detail='Ille-et-Vilaine'}}", Person.getPinpointsYearMap().get(2005).get(0).toString());
        assertEquals("MapStructure{age=25, name='Robert', town=Town{name='Saintes', detail='Charente-Maritime'}}", Person.getPinpointsYearMap().get(2005).get(1).toString());

        //init direct ancestor 2
        Person personDA2 = new Person(null);
        Pinpoint pinpointDA2 = new Pinpoint(new Town("Bressuire", "Deux-Sèvres"), "Michel", 27);
        personDA2.setDirectAncestor(true);

        //launch direct ancestor 2
        personDA2.addPinpoint(2005, pinpointDA2);

        //verification direct ancestor 2
        assertEquals(2, Person.getPinpointsYearMapDirectAncestors().get(2005).size());
        assertEquals("MapStructure{age=27, name='Michel', town=Town{name='Bressuire', detail='Deux-Sèvres'}}", Person.getPinpointsYearMapDirectAncestors().get(2005).get(1).toString());
        assertEquals(3, Person.getPinpointsYearMap().get(2005).size());
        assertEquals("MapStructure{age=27, name='Michel', town=Town{name='Bressuire', detail='Deux-Sèvres'}}", Person.getPinpointsYearMap().get(2005).get(2).toString());
    }

    /**
     * GetAge test : test getAge with no parameter, and with negative period, missing birth, and years to add, and with 0 years to add
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

        //verifications
        assertEquals(-1, person.getAge());
        assertEquals(30, person.getAge(localDate2015, 0));
        assertEquals(40, person.getAge(localDate2015, 10));
        assertEquals(-1, person.getAge(localDate1975, 10));
        assertEquals(-1, personNullBirth.getAge(localDate2015, 0));
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