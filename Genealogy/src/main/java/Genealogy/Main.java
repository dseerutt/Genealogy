package Genealogy;

import java.io.IOException;
import java.text.ParseException;

/**
 * Created by Dan on 04/04/2016.
 */
public class Main {
    public static final String myFile = "famille1.ged";
    public static final String myFolder = "C:\\Users\\Dan\\Desktop\\Programmation\\IntelliJ\\Genealogy\\Genealogy\\src\\main\\resources\\";

    public static void main(String[] args) throws ParseException, IOException {
        MyGedcomReader myGedcomReader = new MyGedcomReader();
        Genealogy tree = myGedcomReader.read(myFolder + myFile);
        tree.parseContents();
        //System.out.println("Header : " + tree.getHeader());
        //System.out.println("Auteur : " + tree.getAuthor());
        //System.out.println("Persons : " + tree.getPersons());
        for (int i = 0 ; i < tree.getPersons().size() ; i++){
            System.out.println(tree.getPersons().get(i).printPerson() + "\n");
        }
    }
}
