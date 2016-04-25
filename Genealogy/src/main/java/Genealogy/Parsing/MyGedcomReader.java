package Genealogy.Parsing;

import Genealogy.Genealogy;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Dan on 04/04/2016.
 */
public class MyGedcomReader {
    public Genealogy read(String path) throws IOException {

        Genealogy genealogy = new Genealogy();
        ArrayList<ParsingStructure> parsingStructureList = new ArrayList<ParsingStructure>();
            BufferedReader br = null;
            String sCurrentLine;
            br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "Cp1252"));
            while ((sCurrentLine = br.readLine()) != null) {
                String[] temp = sCurrentLine.split(" ");
                if (temp.length == 2){
                    parsingStructureList.add(new ParsingStructure(Integer.parseInt(temp[0]),temp[1],""));
                } else if (temp.length > 2) {
                    String value = temp[0];
                    String id = temp[1];
                    int offset = value.length() + 1 + id.length() + 1;
                    ParsingStructure parsingStructure = new ParsingStructure(Integer.parseInt(value),id,sCurrentLine.substring(offset));
                    parsingStructureList.add(parsingStructure);
                } else {
                    System.out.println("Erreur dans le parsing du fichier Gedcom");
                }
                genealogy.setContents(parsingStructureList);
            }
        return genealogy;
    }
}
