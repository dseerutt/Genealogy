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
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static Genealogy.Model.Act.Enum.UnionType.parseUnionType;

/**
 * Class Genealogy : class that hosts the elements of a genealogical tree
 */
public class Genealogy {

    /**
     * HashMap of <idObject,list of ParsingStructures></id,list> - core of the genealogy file
     */
    private HashMap<String, ArrayList<ParsingStructure>> contents;
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
     * Author gedcom String id
     */
    private final String authorId = "SUBM";

    /**
     * String default gedcom file path
     */
    private String defaultFilePath;

    /**
     * Genealogy Constructor from path
     */
    public Genealogy(String path) {
        defaultFilePath = path;
    }

    /**
     * Function writeFile : write gedcom file from contents to the input String path
     *
     * @param path
     * @throws IOException
     */
    public void writeFile(String path) throws IOException {
        StringBuilder text = new StringBuilder(StringUtils.EMPTY);
        for (ArrayList<ParsingStructure> parsingStructureList : contents.values()) {
            for (ParsingStructure parsingStructure : parsingStructureList) {
                text = text.append(parsingStructure.toString()).append(System.lineSeparator());
            }
        }
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "ISO8859_1"));
        writer.write(text.toString());
        writer.close();
    }

    /**
     * Function writeFile : write gedcom file from contents to the default path
     *
     * @throws IOException
     */
    public void writeFile() throws IOException {
        StringBuilder text = new StringBuilder(StringUtils.EMPTY);
        for (ArrayList<ParsingStructure> parsingStructureList : contents.values()) {
            for (ParsingStructure parsingStructure : parsingStructureList) {
                text = text.append(parsingStructure.toString()).append(System.lineSeparator());
            }
        }
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(defaultFilePath), "ISO8859_1"));
        writer.write(text.toString());
        writer.close();
    }

    /**
     * Function setComments : set comments of a Person within the persons list and the write the file
     *
     * @param idPerson
     * @param comments
     */
    public void setComments(String idPerson, String comments) {
        Person person = findPersonById(idPerson);
        person.setComments(comments);
        //Update contents
        ParsingStructure noteStructure = new ParsingStructure(1, "NOTE", StringUtils.EMPTY);
        ArrayList<ParsingStructure> contLines = new ArrayList<>();
        String[] tmpArray = comments.split(System.lineSeparator());
        if (tmpArray != null && tmpArray.length > 0) {
            noteStructure.setFieldValue(tmpArray[0]);
        }
        for (int i = 1; i < tmpArray.length; i++) {
            contLines.add(new ParsingStructure(2, "CONT", tmpArray[i]));
        }
        ArrayList<ParsingStructure> parsingStructureList = contents.get(idPerson);
        parsingStructureList.removeIf(parsingStructure -> parsingStructure.getFieldName().matches("NOTE|CONT"));
        parsingStructureList.add(noteStructure);
        parsingStructureList.addAll(contLines);
    }

    /**
     * Function initPersonsLifeSpans : initialize the lifespan of each Person in persons list
     */
    public void initPersonsLifeSpans() {
        for (int i = 0; i < persons.size(); i++) {
            persons.get(i).initLifespans();
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
            if (id.replace("@", StringUtils.EMPTY).equals(person.getId())) {
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
    public HashMap<String, ArrayList<ParsingStructure>> getContents() {
        return contents;
    }

    /**
     * Contents setter
     *
     * @param contents
     */
    public void setContents(HashMap<String, ArrayList<ParsingStructure>> contents) {
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
        logger.debug("Sort persons");
        Collections.sort(persons, new PersonNameComparator());
    }


    /**
     * Function parseAuthor : from SUBM list, get second element with id name and set author
     *
     * @throws ParsingException
     */
    protected void parseAuthor() throws ParsingException {
        ArrayList<ParsingStructure> subm = contents.get(authorId);
        if (subm != null && subm.size() >= 2 && subm.get(1).getFieldName().equals("NAME")) {
            author = subm.get(1).getFieldValue();
        } else {
            throw new ParsingException("Failed to find author with " + subm.toString());
        }
    }

    /**
     * Function parsePerson : call Person constructor with list parameter and add the person to the persons list
     *
     * @param listInput
     * @throws ParsingException
     */
    protected void parsePerson(ArrayList<ParsingStructure> listInput) throws ParsingException {
        Person person = new Person(listInput);
        persons.add(person);
    }

    /**
     * Function parseContents : create the list of Persons from String contents
     * set the direct ancestors recursively, set the root as the first person
     *
     * @throws ParsingException
     */
    public void parseContents() throws ParsingException {
        logger.debug("Start parsing");
        header = new Header(this);
        parseAuthor();
        //Do not treat header or author or end of file
        Pattern pattern = Pattern.compile("HEAD|SUBM|TRLR");
        Matcher matcher = pattern.matcher(StringUtils.EMPTY);
        for (Map.Entry<String, ArrayList<ParsingStructure>> entry : contents.entrySet()) {
            String id = entry.getKey();
            matcher = matcher.reset(id);
            if (!matcher.matches()) {
                switch (id.charAt(0)) {
                    case 'I':
                        parsePerson(entry.getValue());
                        break;
                    case 'F':
                        parseFamily(entry.getValue());
                        break;
                    case 'L':
                        //no treatment for localizations
                        break;
                    default:
                        throw new ParsingException("Unrecognized Parsing Structure " + id);
                }
            }
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
     * Function parseUnionDate : return the date of the union given a list and indexes to browse for
     *
     * @param listInput
     * @param minIndex
     * @param maxIndex
     * @return
     * @throws ParsingException
     */
    protected MyDate parseUnionDate(ArrayList<ParsingStructure> listInput, int minIndex, int maxIndex) throws ParsingException {
        String date = findFieldInContents("DATE", listInput, minIndex, maxIndex);
        MyDate unionDay = null;
        try {
            unionDay = (MyDate) MyDate.MyDate(date);
        } catch (Exception e) {
            logger.debug("Failed to parse the union date of " + listInput.get(minIndex).getFieldName(), e);
        }
        return unionDay;
    }

    /**
     * Function parseUnionTown : return the town of the union given a list and indexes to browse for
     *
     * @param listInput
     * @param minIndex
     * @param maxIndex
     * @return
     */
    protected Town parseUnionTown(ArrayList<ParsingStructure> listInput, int minIndex, int maxIndex) {
        Town marriageTown = null;
        try {
            marriageTown = new Town(findFieldInContents("PLAC", listInput, minIndex, maxIndex));
        } catch (Exception e) {
            logger.debug("Failed to parse the marriage city of " + listInput.get(minIndex).getFieldName(), e);
        }
        return marriageTown;
    }

    /**
     * Function parseMarriage : parse the marriage and add it to the partners
     *
     * @param partner1
     * @param partner2
     * @param listInput      the input data
     * @param unionTypeInput
     * @throws ParsingException
     */
    protected void parseMarriage(Person partner1, Person partner2, ArrayList<ParsingStructure> listInput, UnionType unionTypeInput) throws ParsingException {
        int minIndex = findIndexIdString("MARR", listInput);
        MyDate marriageDay = null;
        Town marriageTown = null;
        UnionType unionType = unionTypeInput;
        if (minIndex != -1) {
            marriageDay = parseUnionDate(listInput, minIndex, listInput.size());
            marriageTown = parseUnionTown(listInput, minIndex, listInput.size());
        } else {
            int index = findIndexIdString("_MARR", listInput);
            if (index != -1) {
                unionType = UnionType.COHABITATION;
            }
        }
        if ((partner2 != null) && (partner1 != null)) {
            Union union = new Union(partner1, partner2, marriageDay, marriageTown, unionType);
            partner1.addUnion(union);
            partner2.addUnion(union);
        } else {
            logger.debug("Failed to find one of the wedding partner " + listInput.toString());
        }
    }

    /**
     * Function parseDivorce : parse the divorce and add it to the partners
     *
     * @param partner1
     * @param partner2
     * @param listInput the input data
     * @param minIndex
     * @throws ParsingException
     */
    protected void parseDivorce(Person partner1, Person partner2, ArrayList<ParsingStructure> listInput, int minIndex) throws ParsingException {
        if (minIndex != -1) {
            MyDate divorceDay = parseUnionDate(listInput, minIndex, listInput.size());
            Town divorceTown = parseUnionTown(listInput, minIndex, listInput.size());
            Union divorce = new Union(partner1, partner2, divorceDay, divorceTown, UnionType.DIVORCE);
            partner1.addUnion(divorce);
            partner2.addUnion(divorce);
        }
    }

    /**
     * Function parseChildren : parse the children of the family and add them to the partner(s)
     *
     * @param partner1
     * @param partner2
     * @param listInput the input data
     */
    protected void parseChildren(Person partner1, Person partner2, ArrayList<ParsingStructure> listInput) {
        for (ParsingStructure parsingStructure : listInput) {
            if (parsingStructure.getFieldName().equals("CHIL")) {
                String childId = parsingStructure.getFieldValue();
                Person child = findPersonById(childId);
                if (partner1 != null) {
                    partner1.addChild(child);
                    child.setFather(partner1);
                }
                if (partner2 != null) {
                    partner2.addChild(child);
                    child.setMother(partner2);
                }
            }
        }
    }

    /**
     * Function getPersonByPartner : find the id of the person from the list and returns the person linked in persons
     *
     * @param field
     * @param listInput
     * @return
     * @throws ParsingException
     */
    protected Person parsePerson(String field, ArrayList<ParsingStructure> listInput) throws ParsingException {
        String partnerId = findFieldInContents(field, listInput);
        return findPersonById(partnerId);
    }


    /**
     * Function findPartner1 : return the first partner of the family
     *
     * @param listInput
     * @return
     * @throws ParsingException
     */
    protected Person findPartner1(ArrayList<ParsingStructure> listInput) throws ParsingException {
        Person person = parsePerson("HUSB", listInput);
        if (person != null) {
            parsePerson("WIFE", listInput);
        }
        return person;
    }

    /**
     * Function findPartner1 : return the second partner of the family
     *
     * @param listInput
     * @return
     * @throws ParsingException
     */
    protected Person findPartner2(ArrayList<ParsingStructure> listInput) throws ParsingException {
        Person person = parsePerson("WIFE", listInput);
        if (person != null) {
            parsePerson("HUSB", listInput);
        }
        return person;
    }

    /**
     * Function parseFamily : find the partners, their union, divorce and children
     *
     * @param listInput
     * @throws ParsingException
     */
    protected void parseFamily(ArrayList<ParsingStructure> listInput) throws ParsingException {
        Person partner1 = findPartner1(listInput);
        Person partner2 = findPartner2(listInput);

        //Init unionType and divorce
        String statusString = findFieldInContents("_STAT", listInput);
        int indexDivorce = -1;
        if (StringUtils.isBlank(statusString)) {
            indexDivorce = findIndexIdString("DIV", listInput);
        }

        parseMarriage(partner1, partner2, listInput, parseUnionType(statusString));
        parseDivorce(partner1, partner2, listInput, indexDivorce);
        parseChildren(partner1, partner2, listInput);
    }


    /**
     * Function findFieldInContents : find fieldId in the input list
     *
     * @param fieldId
     * @param listInput
     * @return
     */
    public static String findFieldInContents(String fieldId, ArrayList<ParsingStructure> listInput) {
        for (ParsingStructure parsingStructure : listInput) {
            if (StringUtils.equals(fieldId, parsingStructure.getFieldName())) {
                return parsingStructure.getFieldValue();
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * Function findFieldInContents : find field in input list with offset and maximum index
     *
     * @param fieldId
     * @param listInput
     * @param offset
     * @param maxIndexInput
     * @return
     */
    public static String findFieldInContents(String fieldId, ArrayList<ParsingStructure> listInput, int offset, int maxIndexInput) {
        int maxIndex = maxIndexInput;
        if (maxIndex > listInput.size()) {
            maxIndex = listInput.size();
        }
        for (int i = offset; i < maxIndex; i++) {
            ParsingStructure parsingStructure = listInput.get(i);
            if (StringUtils.equals(fieldId, parsingStructure.getFieldName())) {
                return parsingStructure.getFieldValue();
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * Function findIndexIdString : find the list index of the id from the input list, call findIndexIdString wjtb indexes
     *
     * @param id
     * @param listInput
     * @return
     * @throws ParsingException
     */
    public static int findIndexIdString(String id, ArrayList<ParsingStructure> listInput) throws ParsingException {
        return findIndexIdString(id, listInput, 0, listInput.size());
    }

    /**
     * Function findIndexIdString : find the list index of the id from the input list with indexes
     *
     * @param id
     * @param listInput
     * @param offset
     * @param maxIndex
     * @return
     * @throws ParsingException
     */
    public static int findIndexIdString(String id, ArrayList<ParsingStructure> listInput, int offset, int maxIndex) throws ParsingException {
        if (offset > listInput.size()) {
            throw new ParsingException("Could not find id " + id + " , index " + offset + " is too big for contents size of " + listInput.size());
        }
        for (int i = offset; i < maxIndex; i++) {
            if (listInput.get(i).getFieldName().equals(id)) {
                return i;
            }
        }
        return -1;
    }

}
