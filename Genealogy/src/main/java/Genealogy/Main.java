package Genealogy;

import Genealogy.Model.Person;
import Genealogy.Model.Town;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;

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
        tree.sortPersons();
        tree.initPersonsPeriods();
        //.out.println("Persons : " + tree.getPersons());
        /*ArrayList<Person> list = new ArrayList<>();
        for (int i = 0 ; i < tree.getPersons().size() ; i++){
            if (tree.getPersons().get(i).isDirectAncestor()){
                list.add(tree.getPersons().get(i));
            }
        }
        int index = 10;
        System.out.println(list.get(index));
        //System.out.println(list.get(index).getChildren().get(0));
        list.get(index).initPeriods();
        System.out.println(Person.getPeriods());*/
        /*for (int i = 0 ; i < tree.getPersons().size() ; i++){
            System.out.println(tree.getPersons().get(i).printPerson() + "\n");
        }*/
    }
}
