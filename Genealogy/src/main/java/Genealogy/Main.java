package Genealogy;

import Genealogy.Model.Person;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Dan on 04/04/2016.
 */
public class Main {

    public static void main(String[] args) throws ParseException {
        MyGedcomReader myGedcomReader = new MyGedcomReader();
        Genealogy tree = myGedcomReader.read("famille1.ged");
        tree.parseContents();
        //System.out.println("Header : " + tree.getHeader());
        //System.out.println("Auteur : " + tree.getAuthor());
        //System.out.println("Persons : " + tree.getPersons());
        /*for (int i = 0 ; i < tree.getPersons().size() ; i++){
            System.out.println(tree.getPersons().get(i).printPerson() + "\n");
        }*/
    }
}
