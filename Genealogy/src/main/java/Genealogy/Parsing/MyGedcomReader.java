package Genealogy.Parsing;

import Genealogy.Genealogy;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Dan on 04/04/2016.
 */
public class MyGedcomReader {
    final static Logger logger = Logger.getLogger(MyGedcomReader.class);
    public static final String UTF8_BOM = "\uFEFF";

    private static String removeUTF8BOM(String s) {
        if (s.startsWith(UTF8_BOM)) {
            s = s.substring(1);
        }
        return s;
    }

    public Genealogy read(String path) throws IOException {
        //prepareFile(path);
        Genealogy genealogy = new Genealogy();
        ArrayList<ParsingStructure> parsingStructureList = new ArrayList<ParsingStructure>();
            BufferedReader br = null;
            String sCurrentLine;
            br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "ISO8859_1"));
            while ((sCurrentLine = br.readLine()) != null) {
                String[] temp = sCurrentLine.split(" ");
                if (parsingStructureList.isEmpty()){
                    temp = removeUTF8BOM(sCurrentLine).split(" ");
                }
                if (temp.length == 2){
                    parsingStructureList.add(new ParsingStructure(Integer.parseInt("" + temp[0]),temp[1],""));
                } else if (temp.length > 2) {
                    String value = temp[0];
                    String id = temp[1];
                    int offset = value.length() + 1 + id.length() + 1;
                    ParsingStructure parsingStructure = new ParsingStructure(Integer.parseInt(value),id,sCurrentLine.substring(offset));
                    parsingStructureList.add(parsingStructure);
                } else {
                    logger.error("Erreur dans le parsing du fichier Gedcom");
                }
            }
        genealogy.setContents(parsingStructureList);
        return genealogy;
    }
}
