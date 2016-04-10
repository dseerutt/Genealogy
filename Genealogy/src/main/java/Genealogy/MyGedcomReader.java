package Genealogy;

import Genealogy.Model.Structure;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Dan on 04/04/2016.
 */
public class MyGedcomReader {

    public Genealogy read(String name){

        Genealogy genealogy = new Genealogy();
        ArrayList<Structure> structureList = new ArrayList<Structure>();
        try {

            BufferedReader br = null;
            String sCurrentLine;
            br = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Users\\Dan\\Desktop\\Programmation\\IntelliJ\\Genealogy\\Genealogy\\src\\main\\resources\\famille1.ged"), "Cp1252"));
            while ((sCurrentLine = br.readLine()) != null) {
                String[] temp = sCurrentLine.split(" ");
                if (temp.length == 2){
                    structureList.add(new Structure(Integer.parseInt(temp[0]),temp[1],""));
                } else if (temp.length > 2) {
                    String value = temp[0];
                    String id = temp[1];
                    int offset = value.length() + 1 + id.length() + 1;
                    Structure structure = new Structure(Integer.parseInt(value),id,sCurrentLine.substring(offset));
                    structureList.add(structure);
                } else {
                    System.out.println("Erreur dans le parsing du fichier Gedcom");
                }
                genealogy.setContents(structureList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return genealogy;
    }
}
