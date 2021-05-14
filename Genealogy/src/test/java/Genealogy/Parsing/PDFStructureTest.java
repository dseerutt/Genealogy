package Genealogy.Parsing;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PDFStructureTest {

    @Test
    public void parsePDFStructureTest() {
        //launch
        PDFStructure pdfStructureNull = new PDFStructure(null);
        PDFStructure pdfStructureEmpty = new PDFStructure(StringUtils.EMPTY);
        PDFStructure pdfStructureKO = new PDFStructure("KO");
        PDFStructure pdfStructureClassic = new PDFStructure("¤PDF¤{birth=[proofBirth], unions=[proofUnion], death=[proofDeath]}¤PDF¤");
        PDFStructure pdfStructureClassicMultipleUnions = new PDFStructure("¤PDF¤{birth=[proofBirth], unions=[proofUnion1,proofUnion2,proofUnion3], death=[proofDeath]}¤PDF¤");
        PDFStructure pdfStructureNoBirth = new PDFStructure("¤PDF¤{birth=[], unions=[proofUnion], death=[proofDeath]}¤PDF¤");
        PDFStructure pdfStructureCommentsBefore = new PDFStructure("comment1" + System.lineSeparator() + "¤PDF¤{birth=[proofBirth], unions=[proofUnion], death=[proofDeath]}¤PDF¤");
        PDFStructure pdfStructureCommentsAfter = new PDFStructure("¤PDF¤{birth=[proofBirth], unions=[proofUnion], death=[proofDeath]}¤PDF¤" + System.lineSeparator() + "comment2");
        PDFStructure pdfStructureCommentsBoth = new PDFStructure("comment1" + System.lineSeparator() + "¤PDF¤{birth=[proofBirth], unions=[proofUnion], death=[proofDeath]}¤PDF¤ + System.lineSeparator() + \"comment2\"");

        //verification empty structures
        assertTrue(pdfStructureNull.isEmpty());
        assertTrue(pdfStructureEmpty.isEmpty());
        assertTrue(pdfStructureKO.isEmpty());
        //verification classic structure : one birth, one union, one death
        assertFalse(pdfStructureClassic.isEmpty());
        assertEquals("[proofBirth]", pdfStructureClassic.getBirth().toString());
        assertEquals("[proofUnion]", pdfStructureClassic.getUnions().toString());
        assertEquals("[proofDeath]", pdfStructureClassic.getDeath().toString());
        //verification multiple unions
        assertFalse(pdfStructureClassicMultipleUnions.isEmpty());
        assertEquals("[proofBirth]", pdfStructureClassicMultipleUnions.getBirth().toString());
        assertEquals("[proofUnion1,proofUnion2,proofUnion3]", pdfStructureClassicMultipleUnions.getUnions().toString());
        assertEquals("[proofDeath]", pdfStructureClassicMultipleUnions.getDeath().toString());
        //verification missing birth
        assertFalse(pdfStructureNoBirth.isEmpty());
        assertEquals("[]", pdfStructureNoBirth.getBirth().toString());
        assertEquals("[proofUnion]", pdfStructureNoBirth.getUnions().toString());
        assertEquals("[proofDeath]", pdfStructureNoBirth.getDeath().toString());
        //verification comments before
        assertFalse(pdfStructureCommentsBefore.isEmpty());
        assertEquals("[proofBirth]", pdfStructureCommentsBefore.getBirth().toString());
        assertEquals("[proofUnion]", pdfStructureCommentsBefore.getUnions().toString());
        assertEquals("[proofDeath]", pdfStructureCommentsBefore.getDeath().toString());
        //verification comments after
        assertFalse(pdfStructureCommentsAfter.isEmpty());
        assertEquals("[proofBirth]", pdfStructureCommentsAfter.getBirth().toString());
        assertEquals("[proofUnion]", pdfStructureCommentsAfter.getUnions().toString());
        assertEquals("[proofDeath]", pdfStructureCommentsAfter.getDeath().toString());
        //verification comments before and after
        assertFalse(pdfStructureCommentsBoth.isEmpty());
        assertEquals("[proofBirth]", pdfStructureCommentsBoth.getBirth().toString());
        assertEquals("[proofUnion]", pdfStructureCommentsBoth.getUnions().toString());
        assertEquals("[proofDeath]", pdfStructureCommentsBoth.getDeath().toString());
    }
}