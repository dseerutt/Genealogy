package Genealogy.Parsing;

import Genealogy.Model.Exception.ParsingException;
import Genealogy.Model.Gedcom.Genealogy;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * MyGedcomReader class : read a Gedcom file
 */
public class MyGedcomReader {
    /**
     * Singleton instance
     */
    private static MyGedcomReader instance;
    /**
     * class logger
     */
    public final static Logger logger = LogManager.getLogger(MyGedcomReader.class);

    /**
     * Singleton getter
     *
     * @return
     */
    public static MyGedcomReader getInstance() {
        if (instance == null) {
            instance = new MyGedcomReader();
        }
        return instance;
    }

    /**
     * Private default constructor
     */
    private MyGedcomReader() {
    }

    /**
     * Function read : read a gedcom file from a String path and return a Genealogy object with a ParsingStructure list
     *
     * @param path
     * @return
     * @throws IOException
     * @throws ParsingException
     */
    public Genealogy read(String path) throws IOException, ParsingException {
        logger.debug("Start reading file " + path);
        Genealogy genealogy = new Genealogy(path);
        HashMap<String, ArrayList<ParsingStructure>> contents = new LinkedHashMap<>();
        ArrayList<ParsingStructure> parsingStructureList = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "ISO8859_1"));
        String sCurrentLine;
        String idObject = "HEAD";

        //Reading all the lines
        while ((sCurrentLine = br.readLine()) != null) {
            String[] temp = sCurrentLine.split(" ");
            String fieldValue = "";
            //assign temp[2+] in fieldValue
            if (temp.length > 3) {
                fieldValue = temp[2];
                for (int i = 3; i < temp.length; i++) {
                    fieldValue += " " + temp[i];
                }
            }
            if (temp.length >= 2) {
                int number = Integer.parseInt(temp[0]);
                String fieldName = temp[1];
                if (!StringUtils.isBlank(fieldName) && temp.length == 3) {
                    fieldValue = temp[2];
                }
                if (number == 0 && !"HEAD".equals(fieldName)) {
                    //add new element
                    contents.put(idObject, parsingStructureList);
                    //init new element and get id
                    idObject = fieldName.replace("@", "");
                    parsingStructureList = new ArrayList<>();
                }
                parsingStructureList.add(new ParsingStructure(number, fieldName, fieldValue));
            } else {
                throw new ParsingException("Erreur parsing the gedcom file : the number of fields is incorrect " + sCurrentLine);
            }
        }
        //add last block
        if (!"HEAD".equals(idObject)) {
            contents.put(idObject, parsingStructureList);
        }
        genealogy.setContents(contents);
        return genealogy;
    }
}
