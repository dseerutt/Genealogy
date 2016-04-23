package Genealogy;

import Genealogy.Model.Act.Union;
import Genealogy.Model.Date.MyDate;
import Genealogy.Model.Header;
import Genealogy.Model.Person;
import Genealogy.Model.Structure;
import Genealogy.Model.Town;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Dan on 05/04/2016.
 */
public class Genealogy {

    private ArrayList<Structure> contents;
    private Person root;
    private Header header;
    private String author;
    private ArrayList<Person> persons = new ArrayList<Person>();
    public static Genealogy genealogy;

    public Genealogy() {
    }

    public void initPersonsPeriods() {
        for (int i = 0 ; i < persons.size() ; i++){
            persons.get(i).initPeriods();
        }
    }

    public ArrayList<Structure> getContents() {
        return contents;
    }

    public void setContents(ArrayList<Structure> contents) {
        this.contents = contents;
    }

    public Person getRoot() {
        return root;
    }

    public String getAuthor() {
        return author;
    }

    public Header getHeader() {
        return header;
    }

    public ArrayList<Person> getPersons() {
        return persons;
    }

    @Override
    public String toString() {
        return "Genealogy.Genealogy{" +
                "contents=" + contents +
                '}';
    }

    public class NameComparator implements Comparator<Person> {
        @Override
        public int compare(Person o1, Person o2) {
            return o1.getName4Comparator().compareTo(o2.getName4Comparator());
        }
    }

    public void sortPersons(){
        Collections.sort(persons, new Genealogy.NameComparator());
    }

    public void parseContents(){
        ArrayList<Structure> fileHeader = new ArrayList<Structure>();
        int index = 0;
        //Entête
        for (int i = 0 ; i < contents.size() ; i++){
            if (contents.get(i).getId().equals("@SUBM@")){
                index = i+1;
                break;
            }
            fileHeader.add(contents.get(i));
        }
        if (index == 0){
            System.out.println("Erreur dans le parsing");
            return ;
        }
        header = new Header(fileHeader);
        author = contents.get(index).getText();
        index++;

        //Personnes
        int newIndex = AuxMethods.findIndexNumberInteger(contents,0,index+1);

        while(contents.get(newIndex).getText().equals("INDI")){
            Person person = new Person(contents,index,newIndex);
            index = newIndex;
            newIndex = AuxMethods.findIndexNumberInteger(contents,0,index+1);
            persons.add(person);
        }
        Person person = new Person(contents,index,newIndex);
        index = newIndex;
        newIndex = AuxMethods.findIndexNumberInteger(contents,0,index+1);
        persons.add(person);

        //Famille
        int maxFamillyIndex = 0;
        while (!contents.get(maxFamillyIndex).getText().equals("_LOC")){
            maxFamillyIndex = AuxMethods.findIndexNumberInteger(contents,0,index+1);
            handleWedding(persons,contents,index,maxFamillyIndex);
            index = maxFamillyIndex;
        }
        root = persons.get(0);

        prepareDirectAncestors(root);
    }

    private void prepareDirectAncestors(Person p) {
        if (p == null){
            return;
        }
        p.setDirectAncestor(true);
        if (p.getFather() != null){
            prepareDirectAncestors(p.getFather());
        }
        if (p.getMother() != null){
            prepareDirectAncestors(p.getMother());
        }
    }

    private void handleWedding(ArrayList<Person> persons, ArrayList<Structure> contents, int index, int maxIndex) {
        String husbId = AuxMethods.findField(contents,"HUSB",index,maxIndex);
        int husbIndex = AuxMethods.findIDInStructure(persons,husbId);
        String wifeId = AuxMethods.findField(contents,"WIFE",index,maxIndex);
        int wifeIndex = AuxMethods.findIDInStructure(persons,wifeId);

        String statutString = AuxMethods.findField(contents,"_STAT",index,maxIndex);
        Union.State state = Union.parseState(statutString);

        //Date de mariage
        String date = AuxMethods.findField(contents,"DATE",index,maxIndex);
        MyDate marriageDay = null;
        try{
            marriageDay = (MyDate) MyDate.Mydate(date);
        }
        catch (Exception e){
            //System.out.println("Impossible de parser la date de mariage de " + contents.get(index).getId());
        }

        //Ville de mariage
        Town marriageTown = null;
        try {
            marriageTown = new Town(AuxMethods.findField(contents,"PLAC",index,maxIndex));
        } catch (Exception e) {
            //System.out.println("Impossible de parser la ville de mariage de " + contents.get(index).getId());
        }

        Person father = null;
        if (husbIndex != -1){
            father = persons.get(husbIndex);
        }
        Person mother = null;
        if (wifeIndex != -1){
            mother = persons.get(wifeIndex);
        }

        if ((mother != null)&&(father != null)){
            Union union = new Union(father,mother,marriageDay,marriageTown,state);
            father.addUnion(union);
            mother.addUnion(union);
        }

        for (int i = index ; i < maxIndex ; i++){
            if (contents.get(i).getId().equals("CHIL")){
                String childId = contents.get(i).getText();
                int childIndex = AuxMethods.findIDInStructure(persons,childId);
                Person child = persons.get(childIndex);
                if (father != null){
                    father.addChildren(child);
                    child.setFather(father);
                }
                if (mother != null){
                    mother.addChildren(child);
                    child.setMother(mother);
                }
            }
        }
    }


}
