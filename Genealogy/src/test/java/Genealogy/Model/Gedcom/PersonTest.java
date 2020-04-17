package Genealogy.Model.Gedcom;

import Genealogy.Model.Exception.ParsingException;
import Genealogy.Parsing.MyGedcomReader;
import Genealogy.URLConnexion.Serializer;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import static Genealogy.Model.Gedcom.Genealogy.genealogy;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

    //@Test
    public void addProofTest() {
    }

    //@Test
    public void testAddProofTest() {
    }

    //@Test
    public void addPinpointTest() {
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