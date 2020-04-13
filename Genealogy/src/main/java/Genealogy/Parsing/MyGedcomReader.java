package Genealogy.Parsing;

import Genealogy.Model.Exception.ParsingException;
import Genealogy.Model.Gedcom.Genealogy;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * MyGedcomReader class : read a Gedcom file
 */
public class MyGedcomReader {

    /**
     * Function read : read a gedcom file from a String path and return a Genealogy object with a ParsingStructure list
     *
     * @param path
     * @return
     * @throws IOException
     * @throws ParsingException
     */
    public Genealogy read(String path) throws IOException, ParsingException {
        Genealogy genealogy = new Genealogy();
        ArrayList<ParsingStructure> parsingStructureList = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "ISO8859_1"));
        String sCurrentLine;

        //Reading all the lines
        while ((sCurrentLine = br.readLine()) != null) {
            String[] temp = sCurrentLine.split(" ");
            if (parsingStructureList.isEmpty()) {
                temp = sCurrentLine.split(" ");
            }
            if (temp.length == 2) {
                parsingStructureList.add(new ParsingStructure(Integer.parseInt("" + temp[0]), temp[1], ""));
            } else if (temp.length > 2) {
                String value = temp[0];
                String id = temp[1];
                int offset = value.length() + 1 + id.length() + 1;
                ParsingStructure parsingStructure = new ParsingStructure(Integer.parseInt(value), id, sCurrentLine.substring(offset));
                parsingStructureList.add(parsingStructure);
            } else {
                throw new ParsingException("Erreur parsing the Gedcom file : the number of lines is incorrect " + sCurrentLine);
            }
        }
        genealogy.setContents(parsingStructureList);
        return genealogy;
    }
}
