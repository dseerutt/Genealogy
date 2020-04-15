package Genealogy.Model.Gedcom;

import Genealogy.Model.Act.Enum.UnionType;
import Genealogy.Model.Act.Union;
import Genealogy.Model.Date.MyDate;
import Genealogy.Model.Exception.ParsingException;
import Genealogy.Parsing.ParsingStructure;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;

import static Genealogy.Model.Act.Enum.UnionType.parseUnionType;

/**
 * Class Genealogy : class that hosts the elements of a genealogical tree
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
    public final static Logger logger = LogManager.getLogger(Genealogy.class);

    /**
     * Genealogy : Default Constructor
     */
    public Genealogy() {
    }

    /**
     * Function writeFile : write gedcom file from contents to the parametered String path
     *
     * @param path
     * @throws IOException
     */
    public void writeFile(String path) throws IOException {
        StringBuilder text = new StringBuilder("");
        for (ParsingStructure parsingStructure : contents) {
            text = text.append(parsingStructure.toString()).append(System.lineSeparator());
        }
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "ISO8859_1"));
        writer.write(text.toString());
        writer.close();
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
        return "Genealogy.Model.Gedcom.Genealogy{" +
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
     * Function parseHeader : parse the header from contents
     *
     * @return the index of the last element of the header
     * @throws ParsingException if header not found
     */
    protected int parseHeader() throws ParsingException {
        int index = 0;

        //Loop in header contents
        for (int i = 0; i < contents.size(); i++) {
            if ("@I1@".equals(contents.get(i).getFieldName())) {
                index = i;
                break;
            }
        }
        if (index == 0) {
            throw new ParsingException("Failed to parse header");
        }
        header = new Header(this);
        return index;
    }

    /**
     * Function parseAuthor : find and set the author of the file
     *
     * @throws ParsingException if the author can't be found near the NAME id
     */
    protected void parseAuthor() throws ParsingException {
        for (int i = 0; i < contents.size(); i++) {
            if ("@SUBM@".equals(contents.get(i).getFieldName()) && (contents.get(i).getNumber() == 0)) {
                ParsingStructure line = contents.get(i + 1);
                if ("NAME".equals(line.getFieldName())) {
                    author = line.getFieldValue();
                    break;
                } else {
                    throw new ParsingException("Failed to define author");
                }
            }
        }
    }

    /**
     * Function parsePersons : set all the Person objects from the file, set the root person with the first person added
     *
     * @param indexInput the index from where to search
     * @return the index where the search ended
     * @throws ParsingException if it could not parse the file fields
     */
    protected int parsePersons(int indexInput) throws ParsingException {
        int index = indexInput;
        int newIndex = findIndexNumberInteger(0, index + 1);

        //Loop in Person contents
        while (contents.get(newIndex).getFieldValue().equals("INDI")) {
            Person person = new Person(this, contents, index, newIndex);
            index = newIndex;
            newIndex = findIndexNumberInteger(0, index + 1);
            persons.add(person);
        }
        Person person = new Person(this, contents, index, newIndex);
        persons.add(person);
        root = persons.get(0);
        return newIndex;
    }

    /**
     * Function parseFamilies : parse and set families calling parseMarriageContents
     *
     * @param index the index from where to search
     * @throws ParsingException if it could not parse the file fields
     */
    protected void parseFamilies(int index) throws ParsingException {
        //Family
        int maxFamillyIndex = 0;
        while (!contents.get(maxFamillyIndex).getFieldValue().equals("_LOC")) {
            maxFamillyIndex = findIndexNumberInteger(0, index + 1);
            if ("FAM".equals(contents.get(index).getFieldValue())) {
                parseMarriageContents(index, maxFamillyIndex);
            }
            index = maxFamillyIndex;
        }
    }

    /**
     * Function parseContents : create the list of Persons from String contents
     * set the direct ancestors recursively
     *
     * @throws ParsingException if the header is not handled
     */
    public void parseContents() throws ParsingException {
        int index = parseHeader();
        parseAuthor();
        index = parsePersons(index);
        parseFamilies(index);
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
     * Function parseUnionDate : find union date from parameter indexes
     *
     * @param minIndex
     * @param maxIndex
     * @return MyDate found
     */
    protected MyDate parseUnionDate(int minIndex, int maxIndex) throws ParsingException {
        String date = findFieldInContents("DATE", minIndex, maxIndex);
        MyDate unionDay = null;
        try {
            unionDay = (MyDate) MyDate.Mydate(date);
        } catch (Exception e) {
            logger.debug("Failed to parse the union date of " + contents.get(minIndex).getFieldName(), e);
        }
        return unionDay;
    }

    /**
     * Function parseUnionTown : find town date from parameter indexes
     *
     * @param minIndex
     * @param maxIndex
     * @return Town found
     */
    protected Town parseUnionTown(int minIndex, int maxIndex) {
        Town marriageTown = null;
        try {
            marriageTown = new Town(findFieldInContents("PLAC", minIndex, maxIndex));
        } catch (Exception e) {
            logger.debug("Failed to parse the marriage city of " + contents.get(minIndex).getFieldName(), e);
        }
        return marriageTown;
    }

    /**
     * Function parseMarriage : find the marriage date and town, and add the union to the partners
     *
     * @param partner1  Person
     * @param partner2  Person
     * @param unionType enum
     * @param minIndex
     * @param maxIndex
     * @throws ParsingException if it could not parse the file fields
     */
    protected void parseMarriage(Person partner1, Person partner2, UnionType unionType, int minIndex, int maxIndex) throws ParsingException {
        MyDate marriageDay = parseUnionDate(minIndex, maxIndex);
        Town marriageTown = parseUnionTown(minIndex, maxIndex);
        if ((partner2 != null) && (partner1 != null)) {
            Union union = new Union(partner1, partner2, marriageDay, marriageTown, unionType);
            partner1.addUnion(union);
            partner2.addUnion(union);
        } else {
            logger.debug("Failed to find the people linked to the marriage " + contents.get(minIndex).getFieldName());
        }
    }

    /**
     * Function parseDivorce : find the divorce date and town, and add the union to the partners
     *
     * @param partner1
     * @param partner2
     * @param minIndex
     * @param maxIndex
     * @throws ParsingException if it could not parse the file fields
     */
    protected void parseDivorce(Person partner1, Person partner2, int minIndex, int maxIndex) throws ParsingException {
        if (minIndex != -1) {
            MyDate divorceDay = parseUnionDate(minIndex, maxIndex);
            Town divorceTown = parseUnionTown(minIndex, maxIndex);
            Union divorce = new Union(partner1, partner2, divorceDay, divorceTown, UnionType.DIVORCE);
            partner1.addUnion(divorce);
            partner2.addUnion(divorce);
        }
    }

    /**
     * Function parseChildren : find the children related to the partners and add those children to the partners
     *
     * @param partner1
     * @param partner2
     * @param minIndex
     * @param maxIndex
     */
    protected void parseChildren(Person partner1, Person partner2, int minIndex, int maxIndex) {
        for (int i = minIndex; i < maxIndex; i++) {
            if (contents.get(i).getFieldName().equals("CHIL")) {
                String childId = contents.get(i).getFieldValue();
                Person child = findPersonById(childId);
                if (partner1 != null) {
                    partner1.addChildren(child);
                    child.setFather(partner1);
                }
                if (partner2 != null) {
                    partner2.addChildren(child);
                    child.setMother(partner2);
                }
            }
        }
    }

    /**
     * Function parsePerson : find the person from the index and the field, HUSB for husband, WIFE for wife
     *
     * @param minIndex
     * @param maxIndex
     * @param field
     * @return
     * @throws ParsingException if it could not parse the file fields
     */
    protected Person parsePerson(int minIndex, int maxIndex, String field) throws ParsingException {
        String partnerId = findFieldInContents(field, minIndex, maxIndex);
        return findPersonById(partnerId);
    }

    /**
     * Function parsePartner1 : call findPartner for HUSB and then WIFE if no result are shown
     *
     * @param minIndex
     * @param maxIndex
     * @return
     * @throws ParsingException if it could not parse the file fields
     */
    protected Person parsePartner1(int minIndex, int maxIndex) throws ParsingException {
        Person person = parsePerson(minIndex, maxIndex, "HUSB");
        if (person != null) {
            parsePerson(minIndex, maxIndex, "WIFE");
        }
        return person;
    }

    /**
     * Function parsePartner2 : call findPartner for WIFE and then HUSB if no result are shown
     *
     * @param minIndex
     * @param maxIndex
     * @return
     * @throws ParsingException if it could not parse the file fields
     */
    protected Person parsePartner2(int minIndex, int maxIndex) throws ParsingException {
        Person person = parsePerson(minIndex, maxIndex, "WIFE");
        if (person != null) {
            parsePerson(minIndex, maxIndex, "HUSB");
        }
        return person;
    }

    /**
     * Function parseMarriageContents : find the partners, their unions and children
     *
     * @param minIndex
     * @param maxIndex
     * @throws ParsingException if it could not parse the file fields
     */
    protected void parseMarriageContents(int minIndex, int maxIndex) throws ParsingException {
        Person partner1 = parsePartner1(minIndex, maxIndex);
        Person partner2 = parsePartner2(minIndex, maxIndex);

        //Init unionType and divorce
        String statusString = findFieldInContents("_STAT", minIndex, maxIndex);
        int indexDivorce = -1;
        if (StringUtils.isBlank(statusString)) {
            indexDivorce = findIndexIdString("DIV", minIndex, maxIndex);
        }

        parseMarriage(partner1, partner2, parseUnionType(statusString), minIndex, maxIndex);
        parseDivorce(partner1, partner2, indexDivorce, maxIndex);
        parseChildren(partner1, partner2, minIndex, maxIndex);
    }

    /**
     * Function findFieldInContents : call findFieldInContents with no index reductions,
     * find the parameter String field in contents list
     *
     * @param field
     * @return
     * @throws ParsingException if the offset provided is too big
     */
    public String findFieldInContents(String field) throws ParsingException {
        return findFieldInContents(field, 0, contents.size());
    }

    /**
     * Function findFieldInContents : find the parameter String field in contents list with offset and maxvalue
     *
     * @param field    the string to find
     * @param offset
     * @param maxValue
     * @return
     * @throws ParsingException if the offset is too big
     */
    public String findFieldInContents(String field, int offset, int maxValue) throws ParsingException {
        if (offset > contents.size()) {
            throw new ParsingException("Could not find field " + field + " , index " + offset + " is too big for contents size of " + contents.size());
        }
        for (int i = offset; i < maxValue; i++) {
            if (contents.get(i).getFieldName().equals(field)) {
                return contents.get(i).getFieldValue();
            }
        }
        return "";
    }

    /**
     * Function findIndexNumberInteger : find the parameter int number in contents list with offset and maxvalue
     *
     * @param number
     * @param offset
     * @return
     * @throws ParsingException
     */
    public int findIndexNumberInteger(int number, int offset) throws ParsingException {
        if (offset > contents.size()) {
            throw new ParsingException("Could not find number " + number + " , index " + offset + " is too big for contents size of " + contents.size());
        }
        for (int i = offset; i < contents.size(); i++) {
            if (contents.get(i).getNumber() == number) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Function findIndexIdString : find the parameter String id in contents list with offset and maxvalue
     *
     * @param id       to search
     * @param offset   the index to start with
     * @param maxIndex the index to stop
     * @return
     * @throws ParsingException
     */
    public int findIndexIdString(String id, int offset, int maxIndex) throws ParsingException {
        if (offset > contents.size()) {
            throw new ParsingException("Could not find id " + id + " , index " + offset + " is too big for contents size of " + contents.size());
        }
        for (int i = offset; i < maxIndex; i++) {
            if (contents.get(i).getFieldName().equals(id)) {
                return i;
            }
        }
        return -1;
    }

}
