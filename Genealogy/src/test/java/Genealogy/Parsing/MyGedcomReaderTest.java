package Genealogy.Parsing;

import Genealogy.Model.Exception.ParsingException;
import Genealogy.Model.Gedcom.Genealogy;
import Genealogy.URLConnexion.Serializer;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * MyGedcomReader test class
 */
public class MyGedcomReaderTest {

    /**
     * test readTest : makes sure the contents of the gedcom file are listed in the contents of genealogy
     * does not handle the German characters
     *
     * @throws URISyntaxException
     * @throws IOException
     * @throws ParsingException
     */
    @Test
    public void readTest() throws URISyntaxException, IOException, ParsingException {
        //init
        URL url = getClass().getResource("/test/testReadTest.gedTest");
        File file = new File(url.toURI());
        MyGedcomReader myGedcomReader = new MyGedcomReader();
        Serializer serializer = new Serializer();

        //launch
        Genealogy.genealogy = myGedcomReader.read(file.getAbsolutePath());
        MyGedcomReader reader = new MyGedcomReader();
        Genealogy genealogy = reader.read(file.getAbsolutePath());

        //verification
        ArrayList<ParsingStructure> contents = genealogy.getContents();
        assertEquals("ParsingStructure{number=0, id='HEAD', text=''}", contents.get(0).toString());
        assertEquals("ParsingStructure{number=1, id='SOUR', text='AHN'}", contents.get(1).toString());
        assertEquals("ParsingStructure{number=2, id='VERS', text='2.99e'}", contents.get(2).toString());
        assertEquals("ParsingStructure{number=2, id='NAME', text='Ahnenblatt'}", contents.get(3).toString());
        assertEquals("ParsingStructure{number=2, id='CORP', text='Dirk Boettcher'}", contents.get(4).toString());
        assertEquals("ParsingStructure{number=1, id='DATE', text='8 APR 2020'}", contents.get(5).toString());
        assertEquals("ParsingStructure{number=2, id='TIME', text='18:17:40'}", contents.get(6).toString());
        assertEquals("ParsingStructure{number=1, id='SUBM', text='@SUBM@'}", contents.get(7).toString());
        assertEquals("ParsingStructure{number=1, id='FILE', text='testParseContents.ged'}", contents.get(8).toString());
        assertEquals("ParsingStructure{number=1, id='GEDC', text=''}", contents.get(9).toString());
        assertEquals("ParsingStructure{number=2, id='VERS', text='5.5.1'}", contents.get(10).toString());
        assertEquals("ParsingStructure{number=2, id='FORM', text='LINEAGE-LINKED'}", contents.get(11).toString());
        assertEquals("ParsingStructure{number=1, id='CHAR', text='UTF-8'}", contents.get(12).toString());
        assertEquals("ParsingStructure{number=1, id='_NAVM', text='2'}", contents.get(13).toString());
        assertEquals("ParsingStructure{number=2, id='_NAVI', text='@I4@'}", contents.get(14).toString());
        assertEquals("ParsingStructure{number=1, id='_HOME', text='@I4@'}", contents.get(15).toString());
        assertEquals("ParsingStructure{number=0, id='@I1@', text='INDI'}", contents.get(16).toString());
        assertEquals("ParsingStructure{number=1, id='_UID', text='1378C59B32D54C488004B2B48DF80A42411A'}", contents.get(17).toString());
        assertEquals("ParsingStructure{number=1, id='SEX', text='F'}", contents.get(18).toString());
        assertEquals("ParsingStructure{number=1, id='NAME', text='Martha /.../'}", contents.get(19).toString());
        assertEquals("ParsingStructure{number=2, id='SURN', text='...'}", contents.get(20).toString());
        assertEquals("ParsingStructure{number=2, id='GIVN', text='Martha'}", contents.get(21).toString());
        assertEquals("ParsingStructure{number=1, id='FAMS', text='@F1@'}", contents.get(22).toString());
        assertEquals("ParsingStructure{number=1, id='CHAN', text=''}", contents.get(23).toString());
        assertEquals("ParsingStructure{number=2, id='DATE', text='8 APR 2020'}", contents.get(24).toString());
        assertEquals("ParsingStructure{number=3, id='TIME', text='11:12:20'}", contents.get(25).toString());
        assertEquals("ParsingStructure{number=0, id='@I2@', text='INDI'}", contents.get(26).toString());
        assertEquals("ParsingStructure{number=2, id='_GODP', text='Paul Beckman, Catharine Beckman and Josephine Cook'}", contents.get(27).toString());
        assertEquals("ParsingStructure{number=1, id='OCCU', text='technical illustrator'}", contents.get(28).toString());
        assertEquals("ParsingStructure{number=1, id='CONF', text=''}", contents.get(29).toString());
        assertEquals("ParsingStructure{number=2, id='DATE', text='3 MAY 1970'}", contents.get(30).toString());
        assertEquals("ParsingStructure{number=2, id='ADDR', text='Sankt Reinoldi'}", contents.get(31).toString());
        assertEquals("ParsingStructure{number=2, id='PLAC', text='Dortmund'}", contents.get(32).toString());
        assertEquals("ParsingStructure{number=1, id='BIRT', text=''}", contents.get(33).toString());
        assertEquals("ParsingStructure{number=2, id='DATE', text='4 MAR 1956'}", contents.get(34).toString());
        assertEquals("ParsingStructure{number=2, id='PLAC', text='Essen'}", contents.get(35).toString());
        assertEquals("ParsingStructure{number=0, id='@I5@', text='INDI'}", contents.get(36).toString());
        assertEquals("ParsingStructure{number=1, id='_UID', text='A15FE0880BE54341897ADFA8B984F4AC432F'}", contents.get(37).toString());
        assertEquals("ParsingStructure{number=2, id='FILE', text='.\\Beispiel.pics\\JohannBeckmann2.jpg'}", contents.get(38).toString());
        assertEquals("ParsingStructure{number=3, id='FORM', text='jpg'}", contents.get(39).toString());
        assertEquals("ParsingStructure{number=2, id='TITL', text='about 60 years old'}", contents.get(40).toString());
        assertEquals("ParsingStructure{number=1, id='FAMS', text='@F4@'}", contents.get(41).toString());
        assertEquals("ParsingStructure{number=1, id='FAMC', text='@F7@'}", contents.get(42).toString());
        assertEquals("ParsingStructure{number=1, id='OBJE', text=''}", contents.get(43).toString());
        assertEquals("ParsingStructure{number=2, id='FILE', text='.\\Beispiel.pics\\JohannBeckmann-SophieKowalski.jpg'}", contents.get(44).toString());
        assertEquals("ParsingStructure{number=3, id='FORM', text='jpg'}", contents.get(45).toString());
        assertEquals("ParsingStructure{number=2, id='TITL', text='wedding photo'}", contents.get(46).toString());
        assertEquals("ParsingStructure{number=1, id='CHAN', text=''}", contents.get(47).toString());
        assertEquals("ParsingStructure{number=2, id='DATE', text='3 JUL 2014'}", contents.get(48).toString());
        assertEquals("ParsingStructure{number=3, id='TIME', text='23:07:32'}", contents.get(49).toString());
        assertEquals("ParsingStructure{number=0, id='@I6@', text='INDI'}", contents.get(50).toString());
        assertEquals("ParsingStructure{number=1, id='_UID', text='002F38BE5F664AB289656C9C0783D5064167'}", contents.get(51).toString());
        assertEquals("ParsingStructure{number=1, id='SEX', text='M'}", contents.get(52).toString());
        assertEquals("ParsingStructure{number=1, id='NAME', text='Joseph Arthur Wilhelm /Beckman/'}", contents.get(53).toString());
        assertEquals("ParsingStructure{number=2, id='SURN', text='Beckman'}", contents.get(54).toString());
        assertEquals("ParsingStructure{number=2, id='GIVN', text='Joseph Arthur Wilhelm'}", contents.get(55).toString());
        assertEquals("ParsingStructure{number=1, id='BIRT', text=''}", contents.get(56).toString());
        assertEquals("ParsingStructure{number=2, id='DATE', text='15 APR 1929'}", contents.get(57).toString());
        assertEquals("ParsingStructure{number=3, id='_LOC', text='@L2@'}", contents.get(58).toString());
        assertEquals("ParsingStructure{number=1, id='OCCU', text='writer'}", contents.get(59).toString());
        assertEquals("ParsingStructure{number=1, id='DEAT', text=''}", contents.get(60).toString());
        assertEquals("ParsingStructure{number=2, id='DATE', text='2 JAN 1964'}", contents.get(61).toString());
        assertEquals("ParsingStructure{number=2, id='PLAC', text='Oberhausen, DÃ¼sseldorf'}", contents.get(62).toString());
        assertEquals("ParsingStructure{number=3, id='_LOC', text='@L7@'}", contents.get(63).toString());
        assertEquals("ParsingStructure{number=1, id='NOTE', text='was active in the army as a commissioned officer in World War 1'}", contents.get(64).toString());


    }
}