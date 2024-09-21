package Genealogy.Parsing;

import Genealogy.Model.Exception.ParsingException;
import Genealogy.Model.Gedcom.Genealogy;
import Genealogy.URLConnexion.Serializer;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

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
    public void readTest() throws IOException, ParsingException {
        //init
        String path = "src/test/resources/read.gedTest";
        File file = new File(path);
        MyGedcomReader myGedcomReader = MyGedcomReader.getInstance();
        Serializer serializer = Serializer.getInstance();

        //launch
        Genealogy.genealogy = myGedcomReader.read(file.getAbsolutePath());
        Genealogy genealogy = myGedcomReader.read(file.getAbsolutePath());

        //verification
        HashMap<String, ArrayList<ParsingStructure>> contents = genealogy.getContents();
        assertEquals("ParsingStructure{number=0, id='HEAD', text=''}", contents.get("HEAD").get(0).toStringComplete());
        assertEquals("ParsingStructure{number=1, id='SOUR', text='AHN'}", contents.get("HEAD").get(1).toStringComplete());
        assertEquals("ParsingStructure{number=2, id='VERS', text='2.99e'}", contents.get("HEAD").get(2).toStringComplete());
        assertEquals("ParsingStructure{number=2, id='NAME', text='Ahnenblatt'}", contents.get("HEAD").get(3).toStringComplete());
        assertEquals("ParsingStructure{number=2, id='CORP', text='Dirk Boettcher'}", contents.get("HEAD").get(4).toStringComplete());
        assertEquals("ParsingStructure{number=1, id='DATE', text='8 APR 2020'}", contents.get("HEAD").get(5).toStringComplete());
        assertEquals("ParsingStructure{number=2, id='TIME', text='18:17:40'}", contents.get("HEAD").get(6).toStringComplete());
        assertEquals("ParsingStructure{number=1, id='SUBM', text='@SUBM@'}", contents.get("HEAD").get(7).toStringComplete());
        assertEquals("ParsingStructure{number=1, id='FILE', text='testParseContents.ged'}", contents.get("HEAD").get(8).toStringComplete());
        assertEquals("ParsingStructure{number=1, id='GEDC', text=''}", contents.get("HEAD").get(9).toStringComplete());
        assertEquals("ParsingStructure{number=2, id='VERS', text='5.5.1'}", contents.get("HEAD").get(10).toStringComplete());
        assertEquals("ParsingStructure{number=2, id='FORM', text='LINEAGE-LINKED'}", contents.get("HEAD").get(11).toStringComplete());
        assertEquals("ParsingStructure{number=1, id='CHAR', text='UTF-8'}", contents.get("HEAD").get(12).toStringComplete());
        assertEquals("ParsingStructure{number=1, id='_NAVM', text='2'}", contents.get("HEAD").get(13).toStringComplete());
        assertEquals("ParsingStructure{number=2, id='_NAVI', text='@I4@'}", contents.get("HEAD").get(14).toStringComplete());
        assertEquals("ParsingStructure{number=1, id='_HOME', text='@I4@'}", contents.get("HEAD").get(15).toStringComplete());

        assertEquals("ParsingStructure{number=0, id='@I1@', text='INDI'}", contents.get("I1").get(0).toStringComplete());
        assertEquals("ParsingStructure{number=1, id='_UID', text='1378C59B32D54C488004B2B48DF80A42411A'}", contents.get("I1").get(1).toStringComplete());
        assertEquals("ParsingStructure{number=1, id='SEX', text='F'}", contents.get("I1").get(2).toStringComplete());
        assertEquals("ParsingStructure{number=1, id='NAME', text='Martha /.../'}", contents.get("I1").get(3).toStringComplete());
        assertEquals("ParsingStructure{number=2, id='SURN', text='...'}", contents.get("I1").get(4).toStringComplete());
        assertEquals("ParsingStructure{number=2, id='GIVN', text='Martha'}", contents.get("I1").get(5).toStringComplete());
        assertEquals("ParsingStructure{number=1, id='FAMS', text='@F1@'}", contents.get("I1").get(6).toStringComplete());
        assertEquals("ParsingStructure{number=1, id='CHAN', text=''}", contents.get("I1").get(7).toStringComplete());
        assertEquals("ParsingStructure{number=2, id='DATE', text='8 APR 2020'}", contents.get("I1").get(8).toStringComplete());
        assertEquals("ParsingStructure{number=3, id='TIME', text='11:12:20'}", contents.get("I1").get(9).toStringComplete());

        assertEquals("ParsingStructure{number=0, id='@I2@', text='INDI'}", contents.get("I2").get(0).toStringComplete());
        assertEquals("ParsingStructure{number=2, id='_GODP', text='Paul Beckman, Catharine Beckman and Josephine Cook'}", contents.get("I2").get(1).toStringComplete());
        assertEquals("ParsingStructure{number=1, id='OCCU', text='technical illustrator'}", contents.get("I2").get(2).toStringComplete());
        assertEquals("ParsingStructure{number=1, id='CONF', text=''}", contents.get("I2").get(3).toStringComplete());
        assertEquals("ParsingStructure{number=2, id='DATE', text='3 MAY 1970'}", contents.get("I2").get(4).toStringComplete());
        assertEquals("ParsingStructure{number=2, id='ADDR', text='Sankt Reinoldi'}", contents.get("I2").get(5).toStringComplete());
        assertEquals("ParsingStructure{number=2, id='PLAC', text='Dortmund'}", contents.get("I2").get(6).toStringComplete());
        assertEquals("ParsingStructure{number=1, id='BIRT', text=''}", contents.get("I2").get(7).toStringComplete());
        assertEquals("ParsingStructure{number=2, id='DATE', text='4 MAR 1956'}", contents.get("I2").get(8).toStringComplete());
        assertEquals("ParsingStructure{number=2, id='PLAC', text='Essen'}", contents.get("I2").get(9).toStringComplete());

        assertEquals("ParsingStructure{number=0, id='@I5@', text='INDI'}", contents.get("I5").get(0).toStringComplete());
        assertEquals("ParsingStructure{number=1, id='_UID', text='A15FE0880BE54341897ADFA8B984F4AC432F'}", contents.get("I5").get(1).toStringComplete());
        assertEquals("ParsingStructure{number=2, id='FILE', text='.\\Beispiel.pics\\JohannBeckmann2.jpg'}", contents.get("I5").get(2).toStringComplete());
        assertEquals("ParsingStructure{number=3, id='FORM', text='jpg'}", contents.get("I5").get(3).toStringComplete());
        assertEquals("ParsingStructure{number=2, id='TITL', text='about 60 years old'}", contents.get("I5").get(4).toStringComplete());
        assertEquals("ParsingStructure{number=1, id='FAMS', text='@F4@'}", contents.get("I5").get(5).toStringComplete());
        assertEquals("ParsingStructure{number=1, id='FAMC', text='@F7@'}", contents.get("I5").get(6).toStringComplete());
        assertEquals("ParsingStructure{number=1, id='OBJE', text=''}", contents.get("I5").get(7).toStringComplete());
        assertEquals("ParsingStructure{number=2, id='FILE', text='.\\Beispiel.pics\\JohannBeckmann-SophieKowalski.jpg'}", contents.get("I5").get(8).toStringComplete());
        assertEquals("ParsingStructure{number=3, id='FORM', text='jpg'}", contents.get("I5").get(9).toStringComplete());
        assertEquals("ParsingStructure{number=2, id='TITL', text='wedding photo'}", contents.get("I5").get(10).toStringComplete());
        assertEquals("ParsingStructure{number=1, id='CHAN', text=''}", contents.get("I5").get(11).toStringComplete());
        assertEquals("ParsingStructure{number=2, id='DATE', text='3 JUL 2014'}", contents.get("I5").get(12).toStringComplete());
        assertEquals("ParsingStructure{number=3, id='TIME', text='23:07:32'}", contents.get("I5").get(13).toStringComplete());

        assertEquals("ParsingStructure{number=0, id='@I6@', text='INDI'}", contents.get("I6").get(0).toStringComplete());
        assertEquals("ParsingStructure{number=1, id='_UID', text='002F38BE5F664AB289656C9C0783D5064167'}", contents.get("I6").get(1).toStringComplete());
        assertEquals("ParsingStructure{number=1, id='SEX', text='M'}", contents.get("I6").get(2).toStringComplete());
        assertEquals("ParsingStructure{number=1, id='NAME', text='Joseph Arthur Wilhelm /Beckman/'}", contents.get("I6").get(3).toStringComplete());
        assertEquals("ParsingStructure{number=2, id='SURN', text='Beckman'}", contents.get("I6").get(4).toStringComplete());
        assertEquals("ParsingStructure{number=2, id='GIVN', text='Joseph Arthur Wilhelm'}", contents.get("I6").get(5).toStringComplete());
        assertEquals("ParsingStructure{number=1, id='BIRT', text=''}", contents.get("I6").get(6).toStringComplete());
        assertEquals("ParsingStructure{number=2, id='DATE', text='15 APR 1929'}", contents.get("I6").get(7).toStringComplete());
        assertEquals("ParsingStructure{number=3, id='_LOC', text='@L2@'}", contents.get("I6").get(8).toStringComplete());
        assertEquals("ParsingStructure{number=1, id='OCCU', text='writer'}", contents.get("I6").get(9).toStringComplete());
        assertEquals("ParsingStructure{number=1, id='DEAT', text=''}", contents.get("I6").get(10).toStringComplete());
        assertEquals("ParsingStructure{number=2, id='DATE', text='2 JAN 1964'}", contents.get("I6").get(11).toStringComplete());
        assertEquals("ParsingStructure{number=2, id='PLAC', text='Oberhausen, DÃ¼sseldorf'}", contents.get("I6").get(12).toStringComplete());
        assertEquals("ParsingStructure{number=3, id='_LOC', text='@L7@'}", contents.get("I6").get(13).toStringComplete());
        assertEquals("ParsingStructure{number=1, id='NOTE', text='was active in the 'army' as a \"commissioned officer\" in World War 1'}", contents.get("I6").get(14).toStringComplete());
    }
}