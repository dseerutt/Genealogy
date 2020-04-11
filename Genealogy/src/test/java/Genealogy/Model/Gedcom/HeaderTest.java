package Genealogy.Model.Gedcom;

import Genealogy.Model.Exception.ParsingException;
import Genealogy.Parsing.ParsingStructure;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class of Header
 */
public class HeaderTest {

    /**
     * Test of Header constructor 3 main fields
     *
     * @throws ParsingException
     * @throws ParseException
     */
    @Test
    public void HeaderTest() throws ParsingException, ParseException {
        //init
        Genealogy genealogy = new Genealogy();
        ArrayList<ParsingStructure> contents = new ArrayList<>();
        contents.add(new ParsingStructure(0, "HEAD", null));
        contents.add(new ParsingStructure(1, "SOUR", "AHN"));
        contents.add(new ParsingStructure(2, "VERS", "2.99e"));
        contents.add(new ParsingStructure(2, "NAME", "Ahnenblatt"));
        contents.add(new ParsingStructure(2, "CORP", "Dirk Boettcher"));
        contents.add(new ParsingStructure(1, "DATE", "8 APR 2020"));
        contents.add(new ParsingStructure(2, "TIME", "18:17:40"));
        contents.add(new ParsingStructure(2, "VERS", "5.5.1"));
        genealogy.setContents(contents);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss", Locale.ENGLISH);
        Date lastModified = simpleDateFormat.parse("8 APR 2020 18:17:40");

        //launch
        Header header = new Header(genealogy);

        //Verification
        assertEquals("Ahnenblatt", header.getSoftware());
        assertEquals("2.99e", header.getVersion());
        assertEquals(lastModified, header.getLastModified());

    }

}