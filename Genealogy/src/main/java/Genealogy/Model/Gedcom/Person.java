package Genealogy.Model.Gedcom;

import Genealogy.MapViewer.Structures.Pinpoint;
import Genealogy.Model.Act.Birth;
import Genealogy.Model.Act.Christening;
import Genealogy.Model.Act.Death;
import Genealogy.Model.Act.Enum.ActType;
import Genealogy.Model.Act.Enum.UnionType;
import Genealogy.Model.Act.Union;
import Genealogy.Model.Date.FullDate;
import Genealogy.Model.Date.MyDate;
import Genealogy.Model.Exception.ParsingException;
import Genealogy.Parsing.PDFStructure;
import Genealogy.Parsing.ParsingStructure;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Period;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static Genealogy.MapViewer.Structures.Pinpoint.minimumYear;
import static Genealogy.Model.Gedcom.Genealogy.findFieldInContents;
import static Genealogy.Model.Gedcom.Genealogy.findIndexIdString;
import static Genealogy.Model.Gedcom.Sex.parseSex;

/**
 * Class Person : person object from genealogical tree
 */
public class Person {
    /**
     * String gedcom id of the person
     */
    private String id;
    /**
     * Sex enum of the person
     */
    private Sex sex;
    /**
     * String family name
     */
    private String name;
    /**
     * String first name
     */
    private String surname;
    /**
     * Birth of the person
     */
    private Birth birth;
    /**
     * Christening of the person, not very used
     */
    private Christening christening;
    /**
     * Death of the person
     */
    private Death death;
    /**
     * String profession of the person
     */
    private String profession;
    /**
     * String comments of the person
     */
    private String comments;
    /**
     * PDFStructure of the person
     */
    private PDFStructure pdfStructure;
    /**
     * List of unions
     */
    private ArrayList<Union> unions = new ArrayList<>();
    /**
     * Person father
     */
    private Person father;
    /**
     * Person mother
     */
    private Person mother;
    /**
     * Children are list of Person
     */
    private ArrayList<Person> children = new ArrayList<>();
    /**
     * Boolean directAncestor if related to the root
     */
    private boolean directAncestor = false;
    /**
     * Int age
     */
    private int age;
    /**
     * Boolean stillAlive if the person is still alive today
     */
    private boolean stillAlive = false;
    /**
     * Logger of the class Person
     */
    final static Logger logger = LogManager.getLogger(Person.class);
    /**
     * Map of list of Pinpoints (name, age, town) per year
     */
    private static HashMap<Integer, ArrayList<Pinpoint>> pinpointsYearMap = new HashMap<>();
    /**
     * Map of list of MapStructures (name, age, town) per year only for direct ancestors
     */
    private static HashMap<Integer, ArrayList<Pinpoint>> pinpointsYearMapDirectAncestors = new HashMap<>();

    /**
     * Function initSimpleFields : init name, surname, id, sex, profession and comments from input list
     *
     * @param parsingStructurelist
     */
    private void initSimpleFields(ArrayList<ParsingStructure> parsingStructurelist) {
        if (parsingStructurelist != null) {
            String newId = parsingStructurelist.get(0).getFieldName();
            if (newId.contains("@I")) {
                id = newId;
            } else {
                id = "";
            }
            name = findFieldInContents("SURN", parsingStructurelist);
            surname = findFieldInContents("GIVN", parsingStructurelist);
            if (StringUtils.isEmpty(name) && StringUtils.isEmpty(surname)) {
                name = findFieldInContents("NAME", parsingStructurelist);
            }
            sex = parseSex(findFieldInContents("SEX", parsingStructurelist));
            profession = findFieldInContents("OCCU", parsingStructurelist);
            comments = findFieldInContents("NOTE", parsingStructurelist);
        }
    }

    /**
     * Person constructor from the list of parsingStructures, call the initialization of fields,
     * birth, christening, death and calculate age
     *
     * @param parsingStructurelist
     * @throws ParsingException
     */
    public Person(ArrayList<ParsingStructure> parsingStructurelist) throws ParsingException {
        initSimpleFields(parsingStructurelist);
        initBirth(parsingStructurelist);
        initChristening(parsingStructurelist);
        initDeath(parsingStructurelist);
        calculateAge();
    }

    /**
     * Function findDate : find the date from the input list given the index and maxIndex
     * The string type of search is used in the failure message
     *
     * @param parsingStructurelist
     * @param index
     * @param maxIndex
     * @param typeSearch
     * @return
     */
    private MyDate findDate(ArrayList<ParsingStructure> parsingStructurelist, int index, int maxIndex, String typeSearch) {
        String input = findFieldInContents("DATE", parsingStructurelist, index, maxIndex);
        MyDate birthDay = null;
        try {
            birthDay = (MyDate) MyDate.Mydate(input);
        } catch (Exception e) {
            logger.debug("Failed to parse the " + typeSearch + " date of " + id, e);
        }
        return birthDay;
    }

    /**
     * Function findTown : find the town from the input list given the index and maxIndex
     * The string type of search is used in the failure message
     *
     * @param parsingStructurelist
     * @param index
     * @param maxIndex
     * @param typeSearch
     * @return
     */
    private Town findTown(ArrayList<ParsingStructure> parsingStructurelist, int index, int maxIndex, String typeSearch) {
        Town town = null;
        try {
            town = new Town(findFieldInContents("PLAC", parsingStructurelist, index, maxIndex));
        } catch (Exception e) {
            logger.debug("Failed to parse the " + typeSearch + " town of " + id, e);
        }
        return town;
    }

    /**
     * Function initBirth : find the birth date and town and initialize it if found
     *
     * @param parsingStructurelist
     * @throws ParsingException
     */
    private void initBirth(ArrayList<ParsingStructure> parsingStructurelist) throws ParsingException {
        if (parsingStructurelist != null) {
            int indexBirthday = findIndexIdString("BIRT", parsingStructurelist);
            if (indexBirthday != -1) {
                indexBirthday++;
                MyDate birthDay = findDate(parsingStructurelist, indexBirthday, indexBirthday + 3, "birth");
                Town birthTown = findTown(parsingStructurelist, indexBirthday, indexBirthday + 3, "birth");
                birth = new Birth(this, birthDay, birthTown);
            }
        }
    }

    /**
     * Function initDeath : find the death date and town and initialize it if found
     *
     * @param parsingStructurelist
     * @throws ParsingException
     */
    private void initDeath(ArrayList<ParsingStructure> parsingStructurelist) throws ParsingException {
        if (parsingStructurelist != null) {
            int indexDeath = findIndexIdString("DEAT", parsingStructurelist);
            if (indexDeath != -1) {
                indexDeath++;
                MyDate deathDay = findDate(parsingStructurelist, indexDeath, indexDeath + 3, "death");
                Town deathTown = findTown(parsingStructurelist, indexDeath, indexDeath + 3, "death");
                death = new Death(this, deathDay, deathTown);
            }
        }
    }

    /**
     * Function initChristening : find the christening date, town, place and godparents and initialize it if found
     *
     * @param parsingStructurelist
     * @throws ParsingException
     */
    private void initChristening(ArrayList<ParsingStructure> parsingStructurelist) throws ParsingException {
        if (parsingStructurelist != null) {
            int indexChristening = findIndexIdString("CHR", parsingStructurelist);
            if (indexChristening != -1) {
                indexChristening++;
                MyDate christeningDay = findDate(parsingStructurelist, indexChristening, indexChristening + 5, "christening");
                Town ChristeningTown = findTown(parsingStructurelist, indexChristening, indexChristening + 5, "christening");
                String christeningPlace = findFieldInContents("ADDR", parsingStructurelist);
                String godParents = findFieldInContents("_GODP", parsingStructurelist);
                christening = new Christening(this, christeningDay, ChristeningTown, godParents, christeningPlace);
            }
        }
    }

    /**
     * Function calculateAge : init stillAlive and set age from birth and death.
     * Set it to -1 if not found birth or death are not found
     */
    private void calculateAge() {
        if ((birth == null) || (death == null)) {
            age = -1;
            if ((death == null) && (birth != null) && (birth.getDate() != null) && (AuxMethods.getYear(birth.getDate().getDate()) > 1916)) {
                stillAlive = true;
            }
        } else if ((birth.getDate() == null) || (death.getDate() == null)) {
            age = -1;
        } else {
            Date birthDate = birth.getDate().getDate();
            Date deathDate = death.getDate().getDate();
            age = getDiffYears(birthDate, deathDate);
        }
    }

    /**
     * PinpointsYearMap getter
     *
     * @return
     */
    public static HashMap<Integer, ArrayList<Pinpoint>> getPinpointsYearMap() {
        return pinpointsYearMap;
    }

    /**
     * PinpointsYearMapDirectAncestors getter
     *
     * @return
     */
    public static HashMap<Integer, ArrayList<Pinpoint>> getPinpointsYearMapDirectAncestors() {
        return pinpointsYearMapDirectAncestors;
    }

    /**
     * StillAlive boolean getter
     *
     * @return
     */
    public boolean isStillAlive() {
        return stillAlive;
    }

    /**
     * Age getter
     *
     * @return
     */
    public int getAge() {
        return age;
    }

    /**
     * Id getter
     *
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Sex getter
     *
     * @return
     */
    public Sex getSex() {
        return sex;
    }

    /**
     * Name getter
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Surname getter
     *
     * @return
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Birth getter
     *
     * @return
     */
    public Birth getBirth() {
        return birth;
    }

    /**
     * Death getter
     *
     * @return
     */
    public Death getDeath() {
        return death;
    }

    /**
     * Profession getter
     *
     * @return
     */
    public String getProfession() {
        return profession;
    }

    /**
     * Comments getter
     *
     * @return
     */
    public String getComments() {
        return comments;
    }

    /**
     * Comments setter
     *
     * @param comments
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     * Unions getter
     *
     * @return
     */
    public ArrayList<Union> getUnions() {
        return unions;
    }

    /**
     * Father getter
     *
     * @return
     */
    public Person getFather() {
        return father;
    }

    /**
     * Mother getter
     *
     * @return
     */
    public Person getMother() {
        return mother;
    }

    /**
     * Children getter
     *
     * @return
     */
    public ArrayList<Person> getChildren() {
        return children;
    }

    /**
     * DirectAncestor boolean getter
     *
     * @return
     */
    public boolean isDirectAncestor() {
        return directAncestor;
    }

    /**
     * DirectAncestor setter
     *
     * @param directAncestor
     */
    public void setDirectAncestor(boolean directAncestor) {
        this.directAncestor = directAncestor;
    }

    /**
     * Mother setter
     *
     * @param mother
     */
    public void setMother(Person mother) {
        this.mother = mother;
    }

    /**
     * Father setter
     *
     * @param father
     */
    public void setFather(Person father) {
        this.father = father;
    }

    /**
     * Function addUnion : add union input to unions list
     *
     * @param union
     */
    public void addUnion(Union union) {
        unions.add(union);
    }

    /**
     * Function addChildren : add person input to children list
     *
     * @param person
     */
    public void addChildren(Person person) {
        children.add(person);
    }

    /**
     * Function getFullName : return surname and name, name only if surname is empty
     *
     * @return
     */
    public String getFullName() {
        if (StringUtils.isEmpty(surname)) {
            return name;
        } else {
            return surname + " " + name;
        }
    }

    /**
     * Function getFullNameInverted : return name and surname, surname only if name is empty
     *
     * @return
     */
    public String getFullNameInverted() {
        if (!"".equals(name)) {
            return name + " " + surname;
        }
        return surname;
    }

    /**
     * Function getComparatorName : return name and surname prefixed with z if the name or surname equal ...
     *
     * @return
     */
    public String getComparatorName() {
        String txt = "";
        if (("...".equals(name)) || ("...".equals(surname))) {
            txt = "z";
        }
        return txt + name + " " + surname;
    }

    public void initLifeSpans() {
        initPeriods2(initPeriods1());
    }

    public ArrayList<Pair<MyDate, Town>> initPeriods1() {
        ArrayList<Pair<MyDate, Town>> tempPeriods = new ArrayList<>();

        //Naissance
        if ((birth != null) && (birth.getTown() != null) && (birth.getTown().getName() != null) && (birth.getDate() != null)) {
            tempPeriods.add(new Pair<MyDate, Town>(birth.getDate(), birth.getTown()));
        }

        //Unions
        for (int i = 0; i < unions.size(); i++) {
            if ((unions.get(i).getDate() != null) && (unions.get(i).getTown() != null) && (unions.get(i).getTown().getName() != null)) {
                tempPeriods.add(new Pair<MyDate, Town>(unions.get(i).getDate(), unions.get(i).getTown()));
            }
        }

        //Enfants
        for (int i = 0; i < children.size(); i++) {
            if ((children.get(i).getBirth() != null) &&
                    (children.get(i).getBirth().getDate() != null) &&
                    (children.get(i).getBirth().getTown() != null) &&
                    (children.get(i).getBirth().getTown().getName() != null)) {
                tempPeriods.add(new Pair<MyDate, Town>(children.get(i).getBirth().getDate(), children.get(i).getBirth().getTown()));
            }
        }

        //Décès
        if ((death != null) && (death.getTown() != null) && (death.getTown().getName() != null) && (death.getDate() != null)) {
            tempPeriods.add(new Pair<MyDate, Town>(death.getDate(), death.getTown()));

            Collections.sort(tempPeriods, new Comparator<Pair<MyDate, Town>>() {
                @Override
                public int compare(Pair<MyDate, Town> o1, Pair<MyDate, Town> o2) {
                    return o1.getKey().getDate().compareTo(o2.getKey().getDate());
                }
            });

            //Cas des enfants nés après la mort
            while (tempPeriods.get(tempPeriods.size() - 1).getKey().getDate().getTime() > death.getDate().getDate().getTime()) {
                tempPeriods.remove(tempPeriods.size() - 1);
            }
        } else {
            Collections.sort(tempPeriods, new Comparator<Pair<MyDate, Town>>() {
                @Override
                public int compare(Pair<MyDate, Town> o1, Pair<MyDate, Town> o2) {
                    return o1.getKey().getDate().compareTo(o2.getKey().getDate());
                }
            });
            if ((stillAlive) && (!tempPeriods.isEmpty())) {
                tempPeriods.add(new Pair<MyDate, Town>(new FullDate(), tempPeriods.get(tempPeriods.size() - 1).getValue()));
            }
        }
        return tempPeriods;
    }

    public void initPeriods2(ArrayList<Pair<MyDate, Town>> tempPeriods) {
        if (tempPeriods.size() >= 1) {
            if (tempPeriods.size() == 1) {
                Date date = new Date(tempPeriods.get(0).getKey().getDate().getTime());
                Pinpoint pinPoint =
                        new Pinpoint(tempPeriods.get(0).getValue(), getFullName(), getAge(date, 0));
                addPinpoint((int) tempPeriods.get(0).getKey().getYear(), pinPoint);
            } else {
                for (int i = 0; i < tempPeriods.size() - 1; i++) {
                    int date1 = (int) tempPeriods.get(i).getKey().getYear();
                    int date2 = (int) tempPeriods.get(i + 1).getKey().getYear();
                    int index = 0;
                    for (int k = date1; k < date2; k++) {
                        Date date = new Date(tempPeriods.get(i).getKey().getDate().getTime());
                        Pinpoint pinPoint =
                                new Pinpoint(tempPeriods.get(i).getValue(), getFullName(), getAge(date, index));
                        addPinpoint(k, pinPoint);
                        index++;
                    }
                }
                Date date = new Date(tempPeriods.get(tempPeriods.size() - 1).getKey().getDate().getTime());
                Pinpoint pinPoint =
                        new Pinpoint(tempPeriods.get(tempPeriods.size() - 1).getValue(), getFullName(), getAge(date, 0));
                addPinpoint((int) tempPeriods.get(tempPeriods.size() - 1).getKey().getYear(), pinPoint);
            }
        }
    }

    /**
     * Function getProofUnionSize : return the total of proofs from all unions of the person
     *
     * @return
     */
    public int getProofUnionSize() {
        int nbUnions = 0;
        for (Union union : unions) {
            nbUnions += union.getProofs().size();
        }
        return nbUnions;
    }

    /**
     * Function removePDFStructure : remove PDFStructure in note
     */
    public void removePDFStructure() {
        //TODO
        /*String PDFStructureRegex = "¤PDF" + id + "¤";
        String PDFRegex = "(.*)" + PDFStructureRegex + "{.*}(.*)";*/
    }

    public void savePDFStructure() {
        //TODO
    }

    /**
     * Fonction addProof
     *
     * @param actType    Birth, Mariage, Death
     * @param proof      Nom du PDF String
     * @param unionIndex numéro de l'union
     */
    public void addProof(ActType actType, String proof, int unionIndex) throws Exception {
        //TODO
        switch (actType) {
            case BIRTH:
                removePDFStructure();
                birth.addProof(proof);
                pdfStructure.addToPDFBirthList(proof);
                savePDFStructure();
                break;
            case MARRIAGE:
                removePDFStructure();
                if (unionIndex < unions.size()) {
                    unions.get(unionIndex).addProof(proof);
                    pdfStructure.addToPDFMarriageList(proof);
                    savePDFStructure();
                } else {
                    throw new Exception("Index trop élevé");
                }
                break;
            case DEATH:
                removePDFStructure();
                death.addProof(proof);
                pdfStructure.addToPDGDeathList(proof);
                savePDFStructure();
                break;
            default:
                return;
        }
    }

    /**
     * Fonction addProof : call addProof except for marriages
     *
     * @param actType Birth, Death
     * @param proof   Nom du PDF String
     */
    public void addProof(ActType actType, String proof) throws Exception {
        addProof(actType, proof, 0);
    }

    /**
     * Function isPrintable : return true if the surname or name does not equal ...
     *
     * @return
     */
    public boolean isPrintable() {
        return (name != null) && (surname != null) && (!name.equals("...")) && (!surname.equals("..."));
    }

    /**
     * Function addPinpoint : add the couple (year,pinpoint) to pinpointsYearMap
     * If the person is a direct ancestor, add it to pinpointsYearMapDirectAncestors
     *
     * @param year
     * @param pinpoint
     */
    public void addPinpoint(int year, Pinpoint pinpoint) {
        if (year < minimumYear) {
            minimumYear = year;
        }
        if (pinpointsYearMap.containsKey(year)) {
            pinpointsYearMap.get(year).add(pinpoint);
        } else {
            ArrayList<Pinpoint> structure = new ArrayList<>();
            structure.add(pinpoint);
            pinpointsYearMap.put(year, structure);
        }
        if (directAncestor) {
            if (pinpointsYearMapDirectAncestors.containsKey(year)) {
                pinpointsYearMapDirectAncestors.get(year).add(pinpoint);
            } else {
                ArrayList<Pinpoint> pinpointList = new ArrayList<>();
                pinpointList.add(pinpoint);
                pinpointsYearMapDirectAncestors.put(year, pinpointList);
            }
        }
    }

    /**
     * Function getAge : calculate the age of the person at the parameter date and add parameter yearsToAdd
     * Return -1 if birth is not found
     *
     * @param date
     * @param yearsToAdd
     * @return
     */
    public int getAge(Date date, int yearsToAdd) {
        if ((birth != null) && (birth.getDate() != null)) {
            DateTime dateTime0 = new DateTime(birth.getDate().getDate().getTime());
            DateTime dateTime1 = new DateTime(date.getTime());
            Period period = new Period(dateTime0, dateTime1);
            return period.getYears() + yearsToAdd;
        } else {
            return -1;
        }
    }

    /**
     * Function findUnion : return the union between the current person and the partner
     *
     * @param partner
     * @return
     */
    private Union findUnion(Person partner) {
        for (int i = 0; i < unions.size(); i++) {
            if (unions.get(i).getPartner().getId().equals(partner.getId())) {
                return unions.get(i);
            }
            if (unions.get(i).getPerson().getId().equals(partner.getId())) {
                return unions.get(i);
            }
        }
        return null;
    }

    /**
     * Function findChildren : return the list of person/child of the current person and the partner
     *
     * @param partner
     * @return
     */
    private ArrayList<Person> findChildren(Person partner) {
        ArrayList<Person> list = new ArrayList<>();
        for (int i = 0; i < children.size(); i++) {
            Person thisFather = children.get(i).getFather();
            Person thisMother = children.get(i).getMother();
            if ((thisFather != null) && (thisMother != null)) {
                if ((thisFather.getId().equals(partner.getId())) && (thisMother.getId().equals(getId()))) {
                    list.add(children.get(i));
                }
                if ((thisMother.getId().equals(partner.getId())) && (thisFather.getId().equals(getId()))) {
                    list.add(children.get(i));
                }
            }
        }
        return list;
    }

    /**
     * Function findIllegitimateChildren : return the list of person/child of the current person with a partner missing
     *
     * @return
     */
    public ArrayList<Person> findIllegitimateChildren() {
        ArrayList<Person> list = new ArrayList<>();
        for (int i = 0; i < children.size(); i++) {
            Person thisFather = children.get(i).getFather();
            Person thisMother = children.get(i).getMother();
            if ((thisFather == null) || (thisMother == null)) {
                list.add(children.get(i));
            }
        }
        return list;
    }

    /**
     * Function getDiffYears : get the difference in years between the input date1 and date2
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int getDiffYears(Date date1, Date date2) {
        LocalDate localDate1 = date1.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate localDate2 = date2.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        java.time.Period period = java.time.Period.between(localDate1, localDate2);
        return period.getYears();
    }

    /**
     * Function getHalfSiblings : get children of the father and mother with other people and return the list
     *
     * @return
     */
    public ArrayList<Person> getHalfSiblings() {
        ArrayList<Person> result = new ArrayList<>();
        if (father != null) {
            ArrayList<Person> children = father.getChildren();
            for (Person person : children) {
                if (person.getMother() != null && !person.getMother().equals(mother) && !person.equals(this)) {
                    result.add(person);
                }
            }
        }
        if (mother != null) {
            ArrayList<Person> children = mother.getChildren();
            for (Person person : children) {
                if (!person.getMother().equals(father) && !person.equals(this)) {
                    result.add(person);
                }
            }
        }
        return result;
    }

    /**
     * Function getSiblings : return all the list of siblings of the current person
     *
     * @return
     */
    public ArrayList<Person> getSiblings() {
        ArrayList<Person> result = new ArrayList<>();
        if (father != null) {
            ArrayList<Person> children = father.getChildren();
            for (Person person : children) {
                if (person.getMother() != null && person.getMother().equals(mother) && !person.equals(this)) {
                    result.add(person);
                }
            }
        }
        return result;
    }

    /**
     * Function printNecessaryResearchBirth : format and return birth getNecessaryResearch
     *
     * @return
     */
    private String printNecessaryResearchBirth() {
        String result = "";
        if (birth != null) {
            String birthTxt = birth.getNecessaryResearch();
            if (birthTxt != null) {
                result += ", Birth=[" + birthTxt + "]";
            }
        } else {
            result += ", Birth=[Date Town]";
        }
        return result;
    }

    /**
     * Function printNecessaryResearchUnions : format and return unions getNecessaryResearch
     *
     * @return
     */
    private String printNecessaryResearchUnions() {
        String result = "";
        ArrayList<Union> unions = getUnions();
        if (unions != null) {
            String unionTxt = "";
            for (Union union : unions) {
                String inputUnion = union.getNecessaryResearch();
                if (inputUnion != null) {
                    unionTxt += inputUnion;
                }
            }
            if (!unionTxt.equals("")) {
                result += ", Union=[" + unionTxt + "]";
            }
        } else {
            result += ", Union=[Date Town]";
        }
        return result;
    }

    /**
     * Function printNecessaryResearchDeath : format and return death getNecessaryResearch
     *
     * @return
     */
    private String printNecessaryResearchDeath() {
        String result = "";
        if (death != null) {
            String deathTxt = death.getNecessaryResearch();
            if (deathTxt != null) {
                result += ", Death=[" + deathTxt + "]";
            }
        } else {
            result += ", Death=[Date Town]";
        }
        return result;
    }

    /**
     * Function printNecessaryResearch : print the empty or blank fields to search in birth, unions and death
     */
    public void printNecessaryResearch() {
        if (!StringUtils.equals(getSurname(), "...")) {
            String result = printNecessaryResearchBirth();
            result += printNecessaryResearchUnions();
            result += printNecessaryResearchDeath();
            if (!StringUtils.isBlank(result)) {
                logger.info(getFullName() + " : " + result.substring(2));
            }
        }
    }

    /**
     * Function toString : English pretty print of the object Person
     *
     * @return the final String
     */
    @Override
    public String toString() {
        String res = "Person";
        if (!StringUtils.isBlank(id)) {
            res += "_" + id.substring(1, id.length() - 1);
        }
        res += "{";
        if (age != -1) {
            res += age + " y/o ";
        }
        res += sex.toStringPrettyPrint() + ", name='" + name + '\'';
        if (!StringUtils.isEmpty(surname)) {
            res += ", surname='" + surname + '\'';
        }
        if (birth != null) {
            res += ", " + birth.toStringPrettyPrint();
        }
        if (christening != null) {
            res += ", " + christening.toStringPrettyPrint();
        }
        if (death != null) {
            res += ", " + death.toStringPrettyPrint();
        }
        if (!StringUtils.isEmpty(profession)) {
            res += ", profession of " + profession;
        }
        if (father != null) {
            res += ", father='" + father.getFullName() + '\'';
        }
        if (mother != null) {
            res += ", mother='" + mother.getFullName() + '\'';
        }
        if (!unions.isEmpty()) {
            if (unions.size() == 1) {
                res += ", " + unions.get(0).toStringPrettyPrint(id);
            } else {
                res += ", unions=[" + unions.get(0).toStringPrettyPrint(id);
                for (int i = 1; i < unions.size(); i++) {
                    res += ", " + unions.get(i).toStringPrettyPrint(id);
                }
                res += "]";
            }
        }
        if (directAncestor) {
            res += ", directAncestor";
        }
        if (!children.isEmpty()) {
            if (children.size() == 1) {
                res += ", child=[" + children.get(0).getFullName();
            } else {
                res += ", children=[" + children.get(0).getFullName();
                for (int i = 1; i < children.size(); i++) {
                    res += ", " + children.get(i).getFullName();
                }
            }
            res += "]";
        }
        if (!StringUtils.isEmpty(comments)) {
            res += ", comments='" + comments + '\'';
        }
        return res + "}";
    }

    /**
     * Function printPerson : return a string containing the French description of the person
     *
     * @return
     */
    public String printPerson() {
        String text = "";
        if (surname.equals("...") || name.equals("...")) {
            return "";
        }
        String pronoun = "Il";
        String agreement = "";
        String childFull = "le fils";
        String child = "fils";
        boolean foundText = false;
        if (sex == Sex.FEMALE) {
            pronoun = "Elle";
            agreement = "e";
            childFull = "la fille";
            child = "fille";
        }
        text += surname + " " + name;
        if (birth != null) {
            if ((birth.getDate() != null) && (birth.getTown() != null) && (birth.getTown().getName() != null)) {
                foundText = true;
                text += " est né" + agreement + " " + birth.getDate().descriptionDate() + " à "
                        + birth.getTown().getName() + " (" + birth.getTown().getDetail() + ")";
            } else if (birth.getDate() != null) {
                foundText = true;
                text += " est né" + agreement + " " + birth.getDate().descriptionDate() + "";
            } else if ((birth.getTown() != null) && (birth.getTown().getName() != null)) {
                foundText = true;
                text += " est né" + agreement + " à " + birth.getTown().getName() + " (" + birth.getTown().getDetail() + ")";
            }
        }
        if (foundText) {
            if ((mother != null) && (father != null)) {
                if (mother.findUnion(father).getUnionType() == UnionType.HETERO_MAR) {
                    text += " du mariage de " + father.getFullName() + " et de " + mother.getFullName();
                } else {
                    text += " de l'union de " + father.getFullName() + " et de " + mother.getFullName();
                }
            } else if (mother != null) {
                text += ", " + child + " de " + mother.getFullName();
            } else if (father != null) {
                text += ", " + child + " de " + father.getFullName();
            }
        } else {
            if ((mother != null) && (father != null)) {
                text += " est " + childFull + " de ";
                text += father.getFullName() + " et de " + mother.getFullName();
            } else if (mother != null) {
                text += " est " + childFull + " de ";
                text += mother.getFullName();
            } else if (father != null) {
                text += " est " + childFull + " de ";
                text += father.getFullName();
            }
        }
        //Alive ?
        String singularForm = "est";
        String pluralForm = "sont";
        if (death != null) {
            singularForm = "était";
            pluralForm = "étaient";
        }
        //Profession
        if ((profession == null) || (!profession.equals(""))) {
            foundText = true;
            String prof = getProfession();
            if (prof.indexOf(',') == -1) {
                //Case where nothing was written before except names
                if (text.equals(surname + " " + name)) {
                    text += " " + singularForm + " " + prof;
                } else {
                    text += "\nSon métier " + singularForm + " " + prof;
                }
            } else {
                if (text.equals(surname + " " + name)) {
                    text += " " + pluralForm + " " + prof;
                } else {
                    text += "\nSes métiers " + pluralForm + " " + prof;
                }
            }
        }
        //Marriage
        for (int i = 0; i < unions.size(); i++) {
            Union union = unions.get(i);
            Person partner = union.getOtherPerson(this);
            String married = " s'est marié" + agreement;
            if (union.getUnionType() == UnionType.COHABITATION) {
                married = " a vécu";
            } else {
                if (union.getUnionType() == UnionType.DIVORCE) {
                    married = " a divorcé";
                }
            }
            if (foundText) {
                text += "\n" + pronoun + married + " avec " +
                        partner.getFullName();
            } else {
                foundText = true;
                text += married + " avec " +
                        partner.getFullName();
            }
            if ((partner.getProfession() != null) && (!partner.getProfession().equals(""))) {
                text += ", " + partner.getProfession();
            }
            if (union.getDate() != null) {
                text += " " + union.getDate().descriptionDate() + " ";
            }
            if ((union.getTown() != null) && (union.getTown().getName() != null)) {
                text += "à " + union.getTown().getName() + " (" + union.getTown().getDetail() + ")";
            }
            //Children
            ArrayList<Person> myChildren = findChildren(partner);
            if (!myChildren.isEmpty()) {
                text += " et a eu de cette union ";
                int nbChildren = myChildren.size();
                if (nbChildren > 1) {
                    text += nbChildren + " enfants nommés " + myChildren.get(0).getFullName();
                    for (int j = 1; j < myChildren.size() - 1; j++) {
                        text += ", " + myChildren.get(j).getFullName();
                    }
                    text += " et " + myChildren.get(myChildren.size() - 1).getFullName();
                } else {
                    text += nbChildren + " enfant nommé " + myChildren.get(0).getFullName();
                }
            }
        }
        //natural children
        ArrayList<Person> naturalChildren = findIllegitimateChildren();
        if (!naturalChildren.isEmpty()) {
            if (naturalChildren.size() > 1) {
                text += "\n" + pronoun + " a eu " + naturalChildren.size() + " enfants naturels nommés " + naturalChildren.get(0).getFullName();
                for (int j = 1; j < naturalChildren.size() - 1; j++) {
                    text += ", " + naturalChildren.get(j).getFullName();
                }
                text += " et " + naturalChildren.get(naturalChildren.size() - 1).getFullName();
            } else {
                text += "\n" + pronoun + " a eu 1 enfant naturel nommé " + naturalChildren.get(0).getFullName();
            }
        }
        //death
        if (death != null) {
            if ((death.getDate() != null) && (death.getTown() != null) && (death.getTown().getName() != null)) {
                if (foundText) {
                    text += "\n" + pronoun;
                }
                text += " est décédé" + agreement + " " + death.getDate().descriptionDate() + " à "
                        + death.getTown().getName() + " (" + death.getTown().getDetail() + ")";
            } else if (death.getDate() != null) {
                if (foundText) {
                    text += "\n" + pronoun;
                }
                text += " est décédé" + agreement + " " + death.getDate().descriptionDate();
            } else if ((death.getTown() != null) && (death.getTown().getName() != null)) {
                if (foundText) {
                    text += "\n" + pronoun;
                }
                text += " est décédé" + agreement + " à " + death.getTown().getName() + " (" + death.getTown().getDetail() + ")";
            }
        }
        //age
        if (age != -1) {
            if (age < 2) {
                text += " à l'âge de " + age + " an";
            } else {
                text += " à l'âge de " + age + " ans";
            }
        }
        if (text.equals(surname + " " + name)) {
            text = "";
        } else {
            text += ".";
        }
        return text.replace("\n", ".\n");
    }
}
