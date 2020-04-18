package Genealogy.Model.Gedcom;

import Genealogy.MapViewer.Structures.Pinpoint;
import Genealogy.Model.Act.Birth;
import Genealogy.Model.Act.Death;
import Genealogy.Model.Act.Enum.ActType;
import Genealogy.Model.Act.Enum.UnionType;
import Genealogy.Model.Act.Union;
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

import static Genealogy.Model.Gedcom.Genealogy.genealogy;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    //@Test
    public void getAgeTest() {
    }

    //@Test
    public void findIllegitimateChildrenTest() {
    }

    //@Test
    public void getDiffYearsTest() {
    }

    //@Test
    public void getHalfSiblingsTest() {
    }

    //@Test
    public void getSiblingsTest() {
    }

    //@Test
    public void printNecessaryResearchTest() {
    }
}