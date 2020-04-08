package Genealogy;

import Genealogy.Model.Act.Union;
import Genealogy.Model.Date.MyDate;
import Genealogy.Model.Exception.ParsingException;
import Genealogy.Model.Header;
import Genealogy.Model.Person;
import Genealogy.Model.PersonNameComparator;
import Genealogy.Model.Town;
import Genealogy.Parsing.ParsingStructure;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Dan on 05/04/2016.
 * Class Genealogy : class that hosts the elements of a genealogic tree
 */
public class Genealogy {

    /**
     * List of ParsingStructures - core of the genealogy file
     */
    private ArrayList<ParsingStructure> contents;
    /**
     * Root person of the tree
     */
    private Person root;
    /**
     * Header of the genealogy file
     */
    private Header header;
    /**
     * Author String of the genealogy file
     */
    private String author;
    /**
     * List of Person in the tree
     */
    private ArrayList<Person> persons = new ArrayList<>();
    /**
     * The Genealogy itself
     */
    public static Genealogy genealogy;
    /**
     * Logger of the class
     */
    final static Logger logger = LogManager.getLogger(Genealogy.class);

    /**
     * Genealogy : Default Constructor
     */
    public Genealogy() {
    }

    /**
     * Function initPersonsLifeSpans : initialize the lifespan of each Person in persons list
     */
    public void initPersonsLifeSpans() {
        for (int i = 0; i < persons.size(); i++) {
            persons.get(i).initLifeSpans();
        }
    }

    /**
     * Function findPersonById : find a person by its String id
     *
     * @param id : the String to find
     * @return : the Person
     */
    public Person findPersonById(String id) {
        for (Person person : persons) {
            if (id.equals(person.getId())) {
                return person;
            }
        }
        return null;
    }

    /**
     * Contents getter
     *
     * @return
     */
    public ArrayList<ParsingStructure> getContents() {
        return contents;
    }

    /**
     * Contents setter
     *
     * @param contents
     */
    public void setContents(ArrayList<ParsingStructure> contents) {
        this.contents = contents;
    }

    /**
     * Root getter
     *
     * @return
     */
    public Person getRoot() {
        return root;
    }

    /**
     * Author getter
     *
     * @return
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Header getter
     *
     * @return
     */
    public Header getHeader() {
        return header;
    }

    /**
     * Persons getter
     *
     * @return
     */
    public ArrayList<Person> getPersons() {
        return persons;
    }

    /**
     * Function toString : return contents only
     *
     * @return
     */
    @Override
    public String toString() {
        return "Genealogy.Genealogy{" +
                "contents=" + contents +
                '}';
    }

    /**
     * Function sortPersons : sort persons list with PersonNameComparator
     */
    public void sortPersons() {
        Collections.sort(persons, new PersonNameComparator());
    }

    /**
     * Function parseContents : create the list of Persons from String contents
     * set the direct ancestors recursively
     *
     * @throws ParsingException if the header is not handled
     */
    public void parseContents() throws ParsingException {
        int index = 0;

        //Header
        ArrayList<ParsingStructure> fileHeader = new ArrayList<>();
        for (int i = 0; i < contents.size(); i++) {
            if ("@I1@".equals(contents.get(i).getId())) {
                index = i;
                break;
            }
            fileHeader.add(contents.get(i));
        }
        if (index == 0) {
            throw new ParsingException("Failed to parse header");
        }
        header = new Header(fileHeader);

        //Define Author
        for (int i = 0; i < contents.size(); i++) {
            if ("@SUBM@".equals(contents.get(i).getId()) && (contents.get(i).getNumber() == 0)) {
                ParsingStructure line = contents.get(i + 1);
                if ("NAME".equals(line.getId())) {
                    author = line.getText();
                    break;
                } else {
                    throw new ParsingException("Failed to define author");
                }
            }
        }
        //Persons
        int newIndex = AuxMethods.findIndexNumberInteger(contents, 0, index + 1);

        while (contents.get(newIndex).getText().equals("INDI")) {
            Person person = new Person(contents, index, newIndex);
            index = newIndex;
            newIndex = AuxMethods.findIndexNumberInteger(contents, 0, index + 1);
            persons.add(person);
        }
        Person person = new Person(contents, index, newIndex);
        index = newIndex;
        newIndex = AuxMethods.findIndexNumberInteger(contents, 0, index + 1);
        persons.add(person);

        //Family
        int maxFamillyIndex = 0;
        while (!contents.get(maxFamillyIndex).getText().equals("_LOC")) {
            maxFamillyIndex = AuxMethods.findIndexNumberInteger(contents, 0, index + 1);
            parseMarriageContents(persons, contents, index, maxFamillyIndex);
            index = maxFamillyIndex;
        }
        root = persons.get(0);

        setDirectAncestors(root);
    }

    /**
     * Function setDirectAncestors : spread directAncestor boolean recursively to all direct ancestors
     *
     * @param person
     */
    protected void setDirectAncestors(Person person) {
        if (person != null) {
            person.setDirectAncestor(true);
            if (person.getFather() != null) {
                setDirectAncestors(person.getFather());
            }
            if (person.getMother() != null) {
                setDirectAncestors(person.getMother());
            }
        }
    }

    /**
     * Function parseMarriageContents : create Union structures from dates and places
     *
     * @param persons
     * @param contents
     * @param index
     * @param maxIndex
     */
    protected void parseMarriageContents(ArrayList<Person> persons, ArrayList<ParsingStructure> contents, int index, int maxIndex) {
        String husbId = AuxMethods.findField(contents, "HUSB", index, maxIndex);
        int husbIndex = AuxMethods.findIDInStructure(persons, husbId);
        String wifeId = AuxMethods.findField(contents, "WIFE", index, maxIndex);
        int wifeIndex = AuxMethods.findIDInStructure(persons, wifeId);

        String statutString = AuxMethods.findField(contents, "_STAT", index, maxIndex);
        boolean divorce = false;
        int indexDivorce = -1;
        if (StringUtils.isBlank(statutString)) {
            indexDivorce = AuxMethods.findIndexNumberString(contents, "DIV", index, maxIndex);
            divorce = (indexDivorce != -1);
        }
        Union.UnionType unionType = Union.parseUnionType(statutString);

        //Marriage date
        String date = AuxMethods.findField(contents, "DATE", index, maxIndex);
        MyDate marriageDay = null;
        try {
            marriageDay = (MyDate) MyDate.Mydate(date);
        } catch (Exception e) {
            logger.debug("Impossible de parser la date de mariage de " + contents.get(index).getId(), e);
        }

        //Marriage city
        Town marriageTown = null;
        try {
            marriageTown = new Town(AuxMethods.findField(contents, "PLAC", index, maxIndex));
        } catch (Exception e) {
            logger.debug("Impossible de parser la ville de mariage de " + contents.get(index).getId(), e);
        }

        Person father = null;
        if (husbIndex != -1) {
            father = persons.get(husbIndex);
        }
        Person mother = null;
        if (wifeIndex != -1) {
            mother = persons.get(wifeIndex);
        }

        if ((mother != null) && (father != null)) {
            Union union = new Union(father, mother, marriageDay, marriageTown, unionType);
            father.addUnion(union);
            mother.addUnion(union);
        }

        //Divorce
        if (divorce) {
            //Divorce date
            String dateDivorce = AuxMethods.findField(contents, "DATE", indexDivorce, maxIndex);
            MyDate divorceDay = null;
            try {
                divorceDay = (MyDate) MyDate.Mydate(dateDivorce);
            } catch (Exception e) {
                logger.debug("Impossible de parser la date de divorce de " + contents.get(index).getId(), e);
            }

            //Divorce city
            Town divorceTown = null;
            try {
                divorceTown = new Town(AuxMethods.findField(contents, "PLAC", indexDivorce, maxIndex));
            } catch (Exception e) {
                logger.debug("Impossible de parser la ville de divorce de " + contents.get(index).getId(), e);
            }
            Union union = new Union(father, mother, divorceDay, divorceTown, Union.UnionType.DIVORCE);
            father.addUnion(union);
            mother.addUnion(union);
        }

        for (int i = index; i < maxIndex; i++) {
            if (contents.get(i).getId().equals("CHIL")) {
                String childId = contents.get(i).getText();
                int childIndex = AuxMethods.findIDInStructure(persons, childId);
                Person child = persons.get(childIndex);
                if (father != null) {
                    father.addChildren(child);
                    child.setFather(father);
                }
                if (mother != null) {
                    mother.addChildren(child);
                    child.setMother(mother);
                }
            }
        }
    }
}
