package Genealogy.URLConnexion.Geneanet;

import Genealogy.Model.Act.Birth;
import Genealogy.Model.Act.Death;
import Genealogy.Model.Act.Union;
import Genealogy.Model.Date.MyDate;
import Genealogy.Model.Gedcom.Genealogy;
import Genealogy.Model.Gedcom.Person;
import Genealogy.Model.Gedcom.Sex;
import Genealogy.Model.Gedcom.Town;
import Genealogy.URLConnexion.Serializer;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static Genealogy.Model.Gedcom.Genealogy.genealogy;
import static Genealogy.URLConnexion.Geneanet.GeneanetBrowser.*;
import static Genealogy.URLConnexion.Geneanet.GeneanetPerson.printListofGeneanetPerson;
import static Genealogy.URLConnexion.Geneanet.TreeComparatorManager.exceptionMode;
import static Genealogy.URLConnexion.Geneanet.TreeComparatorManager.searchOnGeneanet;

/**
 * Created by Dan on 02/12/2018.
 * Export en UTF8 à partir de ahnenblatt : export + encoder en utf8, encoder en ansi puis convertir en utf8
 */
public class TreeComparator {
    private GeneanetPerson geneanetRoot;
    private Person gedcomRoot;
    private LinkedHashMap<GeneanetPerson, String> differences = new LinkedHashMap<>();
    private LinkedHashMap<GeneanetPerson, String> differencesForDisplay = new LinkedHashMap<>();
    public HashMap<String, GeneanetPerson> peopleUrl = new HashMap<String, GeneanetPerson>();
    public static ArrayList<String> urlSearched = new ArrayList<String>();
    public static ArrayList<String> urlPartnersSearched = new ArrayList<String>();
    private boolean log = true;
    private boolean errorComparison = false;
    private String treeName;
    private String comparisonResultDisplay;
    private String peopleUrlError = StringUtils.EMPTY;
    private String peopleFullNameError = StringUtils.EMPTY;
    private String comparisonResultToReplace;
    private String comparisonResultReplacement;
    private static LinkedHashMap<String, String> aliasCities;
    private static LinkedHashMap<String, String> aliasRegexCities;
    private static LinkedHashMap<String, String> aliasNames;
    public static final Pattern SPECIAL_REGEX_CHARS = Pattern.compile("[{}()\\[\\].+*?^$\\\\|]");
    private static HashMap<String, String> urlAlias = new HashMap<String, String>();
    public String url;
    private String addReplacement;
    private String removeReplacement;
    private String stats;

    public void initTreeComparator(GeneanetPerson geneanetPerson, Person gedcomPerson, HashMap<String, GeneanetPerson> peopleUrl, String treeName) {
        errorComparison = false;
        urlSearched = new ArrayList<String>();
        urlPartnersSearched = new ArrayList<String>();
        this.geneanetRoot = geneanetPerson;
        this.gedcomRoot = gedcomPerson;
        this.peopleUrl = peopleUrl;
        this.treeName = treeName;
        initAlias(false);
    }

    public TreeComparator() {

    }

    public String getStats() {
        return stats;
    }

    public String getPeopleFullNameError() {
        return peopleFullNameError;
    }

    public String getAddReplacement() {
        return addReplacement;
    }

    public String getRemoveReplacement() {
        return removeReplacement;
    }

    public int getPeopleSize() {
        return peopleUrl.size();
    }

    public GeneanetPerson getGeneanetRoot() {
        return geneanetRoot;
    }

    public LinkedHashMap<GeneanetPerson, String> getDifferences() {
        return differences;
    }

    public void setDifferences(LinkedHashMap<GeneanetPerson, String> differences) {
        this.differences = differences;
    }

    public void setGeneanetRoot(GeneanetPerson geneanetRoot) {
        this.geneanetRoot = geneanetRoot;
    }

    public boolean isErrorComparison() {
        return errorComparison;
    }

    public LinkedHashMap<GeneanetPerson, String> getDifferencesForDisplay() {
        return differencesForDisplay;
    }

    public void setDifferencesForDisplay(LinkedHashMap<GeneanetPerson, String> differencesForDisplay) {
        this.differencesForDisplay = differencesForDisplay;
    }

    public String getTreeName() {
        return treeName;
    }

    public void setTreeName(String treeName) {
        this.treeName = treeName;
    }

    public void setErrorComparison(boolean errorComparison) {
        this.errorComparison = errorComparison;
    }

    public Person getGedcomRoot() {
        return gedcomRoot;
    }

    public void setGedcomRoot(Person gedcomRoot) {
        this.gedcomRoot = gedcomRoot;
    }

    public boolean isLog() {
        return log;
    }

    public String getComparisonResultDisplay() {
        return comparisonResultDisplay;
    }

    public String getPeopleUrlError() {
        return peopleUrlError;
    }

    public String getComparisonResultToReplace() {
        return comparisonResultToReplace;
    }

    public void setComparisonResultToReplace(String comparisonResultToReplace) {
        this.comparisonResultToReplace = comparisonResultToReplace;
    }

    public String getComparisonResultReplacement() {
        return comparisonResultReplacement;
    }

    public void setComparisonResultReplacement(String comparisonResultReplacement) {
        this.comparisonResultReplacement = comparisonResultReplacement;
    }

    public void setComparisonResultDisplay(String comparisonResultDisplay) {
        this.comparisonResultDisplay = comparisonResultDisplay;
    }

    public void setLog(boolean log) {
        this.log = log;
    }

    public String printDifferences(boolean hide, boolean log) {
        String result = StringUtils.EMPTY;
        if (!hide) {
            HashMap<GeneanetPerson, String> tmpDifferences;
            tmpDifferences = differencesForDisplay;
            for (Map.Entry<GeneanetPerson, String> entry : tmpDifferences.entrySet()) {
                if (log) {
                    logger.info("\"" + entry.getKey().getUrl() + "\";" + entry.getValue());
                } else {
                    result += System.lineSeparator() + entry.getKey().getUrl() + ";" + entry.getValue();
                }
            }
        }
        //print alias
        if (comparisonResultToReplace != null && comparisonResultToReplace.contains("&z=")) {
            String[] splitUrl = comparisonResultToReplace.split(";");
            logger.info("Alias url : " + urlAlias.get(splitUrl[0]));
        }
        return result;
    }

    public String getDifferencesWithinString() {
        String result = StringUtils.EMPTY;
        for (Map.Entry<GeneanetPerson, String> entry : differences.entrySet()) {
            result += entry.getKey().getUrl() + ";" + entry.getKey().getFullName() + ";" + entry.getValue() + System.lineSeparator();
        }
        return result;
    }

    public static String getDifferencesWithinString(HashMap<GeneanetPerson, String> differences) {
        String result = StringUtils.EMPTY;
        for (Map.Entry<GeneanetPerson, String> entry : differences.entrySet()) {
            result += entry.getKey().getUrl() + ";" + entry.getValue() + System.lineSeparator();
        }
        return result;
    }

    private void compareRoot() throws Exception {
        if (log) {
            logger.info("Compared " + gedcomRoot.getFullName() + " (" + geneanetRoot.getUrl() + ")");
        }
        compareNames(geneanetRoot, gedcomRoot);
        compareSex(geneanetRoot, gedcomRoot);
        compareBirth(geneanetRoot, gedcomRoot);
        compareDeath(geneanetRoot, gedcomRoot);
        compareMarriage(geneanetRoot, gedcomRoot);

        urlSearched.add(geneanetRoot.getUrl());
        searchFamily(geneanetRoot, gedcomRoot, true);
        searchChildren(geneanetRoot, gedcomRoot);
    }

    private void comparePerson(GeneanetPerson geneanetPerson, Person gedcomPerson) throws Exception {
        if (urlSearched.contains(geneanetPerson.getUrl())) {
            return;
        }
        if (log) {
            logger.info("Compared " + gedcomPerson.getFullName() + "(" + geneanetPerson.getUrl() + ")");
        }
        compareNames(geneanetPerson, gedcomPerson);
        compareSex(geneanetPerson, gedcomPerson);
        compareBirth(geneanetPerson, gedcomPerson);
        compareDeath(geneanetPerson, gedcomPerson);
        compareMarriage(geneanetPerson, gedcomPerson);
        //compareSiblings(geneanetPerson, gedcomPerson);
        //compareHalfSiblings(geneanetPerson, gedcomPerson);

        urlSearched.add(geneanetPerson.getUrl());
        searchFamily(geneanetPerson, gedcomPerson, false);
    }

    private void compareSibling(GeneanetPerson geneanetPerson, Person gedcomPerson, boolean children) throws Exception {
        if (urlSearched.contains(geneanetPerson.getUrl())) {
            return;
        }
        if (log) {
            logger.info("Compared " + gedcomPerson.getFullName() + "(" + geneanetPerson.getUrl() + ")");
        }
        compareNames(geneanetPerson, gedcomPerson);
        compareSex(geneanetPerson, gedcomPerson);
        compareBirth(geneanetPerson, gedcomPerson);
        compareDeath(geneanetPerson, gedcomPerson);
        if (!children) {
            compareMarriage(geneanetPerson, gedcomPerson);
        }
        //compareSiblings(geneanetPerson, gedcomPerson);
        //compareHalfSiblings(geneanetPerson, gedcomPerson);

        urlSearched.add(geneanetPerson.getUrl());
        //Search partners
        if ((geneanetPerson != null || gedcomPerson != null) && (!children)) {
            searchPartners(geneanetPerson, gedcomPerson, false);
        }
    }

    private void partialCompare(GeneanetPerson geneanetPerson, Person gedcomPerson, boolean root) throws Exception {
        if (log) {
            logger.info("Compared partial " + gedcomPerson.getFullName() + "(" + geneanetPerson.getUrl() + ")");
        }
        urlPartnersSearched.add(geneanetPerson.getUrl());
        compareNames(geneanetPerson, gedcomPerson);
        compareSex(geneanetPerson, gedcomPerson);
        compareBirth(geneanetPerson, gedcomPerson);
        compareDeath(geneanetPerson, gedcomPerson);
        compareMarriage(geneanetPerson, gedcomPerson);

        if (root) {
            searchFamily(geneanetPerson, gedcomPerson, false);
        }
    }

    private void compareAndSearchPerson(GeneanetPerson geneanetPerson, Person gedcomPerson) throws Exception {
        comparePerson(geneanetPerson, gedcomPerson);
        searchFamily(geneanetPerson, gedcomPerson, false);
    }

    private void searchFamily(GeneanetPerson geneanetPerson, Person gedcomPerson, boolean root) throws Exception {
        if (geneanetPerson != null || gedcomPerson != null) {
            searchParents(geneanetPerson, gedcomPerson);
            searchSiblings(geneanetPerson, gedcomPerson);
            searchHalfSiblings(geneanetPerson, gedcomPerson);
            searchPartners(geneanetPerson, gedcomPerson, root);
        } // Do not search double null
    }

    private void searchPartners(GeneanetPerson geneanetPerson, Person gedcomPerson, boolean root) throws Exception {
        for (Map.Entry<GeneanetPerson, HashMap<MyDate, String>> entry : geneanetPerson.getMarriage().entrySet()) {
            GeneanetPerson genPersonToSearch = entry.getKey();
            genPersonToSearch = peopleUrl.get((removeDoubleGeneanetSuffix(genPersonToSearch.getUrl())));
            if (genPersonToSearch == null) {
                throw new Exception("Could not find person " + entry.getKey().getUrl());
            }
            for (Union union : gedcomPerson.getUnions()) {
                Person partner = union.getPartner();
                if (superEqualsAlias(genPersonToSearch.getFullName(), partner.getFullName())) {
                    superComparePartner(genPersonToSearch, partner, "partner", root);
                }
                Person citizen = union.getPerson();
                if (superEqualsAlias(genPersonToSearch.getFullName(), citizen.getFullName())) {
                    superComparePartner(genPersonToSearch, citizen, "partner", root);
                }
            }
        }
    }

    private void searchRelatives(GeneanetPerson geneanetPerson, ArrayList<GeneanetPerson> siblingsGen, ArrayList<Person> siblingsGed, String txt) throws Exception {
        ArrayList<Person> gedcomPersonList = new ArrayList<>();
        if (geneanetPerson != null) {
            for (GeneanetPerson siblingGen : siblingsGen) {
                GeneanetPerson newSiblingGen = peopleUrl.get(removeDoubleGeneanetSuffix(siblingGen.getUrl()));
                if (newSiblingGen == null) {
                    throw new Exception("Could not find person " + siblingGen.getUrl());
                }
                for (Person siblingGed : siblingsGed) {
                    if (newSiblingGen != null && superEqualsAlias(newSiblingGen.getFullName(), siblingGed.getFullName()) && !gedcomPersonList.contains(siblingGed)
                            && !urlSearched.contains(newSiblingGen.getUrl())) {
                        compareSibling(newSiblingGen, siblingGed, txt.contains("Children"));
                        gedcomPersonList.add(siblingGed);
                    }
                }
                if (!urlSearched.contains(newSiblingGen.getUrl())) {
                    addDifference(geneanetPerson, "Missing " + txt + " : " + siblingGen.getUrl());
                }
            }
        }
    }

    private void searchChildren(GeneanetPerson geneanetPerson, Person gedcomPerson) throws Exception {
        ArrayList<GeneanetPerson> childrenGen = geneanetPerson.getChildren();
        ArrayList<Person> childrenGed = gedcomPerson.getChildren();
        searchRelatives(geneanetPerson, childrenGen, childrenGed, "Children");
    }

    private void searchSiblings(GeneanetPerson geneanetPerson, Person gedcomPerson) throws Exception {
        ArrayList<GeneanetPerson> siblingsGen = geneanetPerson.getSiblings();
        ArrayList<Person> siblingsGed = gedcomPerson.getSiblings();
        searchRelatives(geneanetPerson, siblingsGen, siblingsGed, "Sibling");
    }

    private void searchHalfSiblings(GeneanetPerson geneanetPerson, Person gedcomPerson) throws Exception {
        ArrayList<GeneanetPerson> siblingsGen = geneanetPerson.getHalfSiblings();
        ArrayList<Person> siblingsGed = gedcomPerson.getHalfSiblings();
        searchRelatives(geneanetPerson, siblingsGen, siblingsGed, "Half Sibling");
    }

    private void superCompareAndSearchPerson(GeneanetPerson geneanetPerson, Person gedcomPerson, String txt) throws Exception {
        if (geneanetPerson != null && gedcomPerson != null) {
            if (!urlSearched.contains(geneanetPerson.getUrl())) {
                comparePerson(geneanetPerson, gedcomPerson);
                //searchFamily(geneanetPerson, gedcomPerson, false);
            }
        } else if (geneanetPerson != null) {
            addDifference(geneanetPerson, "Missing " + txt);
        }
    }

    private void superComparePartner(GeneanetPerson geneanetPerson, Person gedcomPerson, String txt, boolean root) throws Exception {
        if (geneanetPerson != null && gedcomPerson != null) {
            if (!urlSearched.contains(geneanetPerson.getUrl()) && !urlPartnersSearched.contains(geneanetPerson.getUrl())) {
                partialCompare(geneanetPerson, gedcomPerson, root);
            }
        } else if (geneanetPerson != null) {
            addDifference(geneanetPerson, "Missing " + txt + " of " + geneanetPerson.getFullName());
        }
    }

    private void searchParents(GeneanetPerson geneanetPerson, Person gedcomPerson) throws Exception {
        superCompareAndSearchPerson(geneanetPerson.getFather(), gedcomPerson.getFather(), "Father");
        superCompareAndSearchPerson(geneanetPerson.getMother(), gedcomPerson.getMother(), "Mother");
    }

    private void compareChildren(GeneanetPerson geneanetPerson, Person gedcomPerson) {
        ArrayList<GeneanetPerson> childrenGen = geneanetPerson.getChildren();
        ArrayList<Person> children1Ged = gedcomPerson.getChildren();
        if (!superEquals2(childrenGen, children1Ged, peopleUrl) && !childrenGen.isEmpty()) {
            addDifference(geneanetPerson, "Children=" + printListofGeneanetPerson(childrenGen));
        }
    }

    private void compareHalfSiblings(GeneanetPerson geneanetPerson, Person gedcomPerson) {
        ArrayList<GeneanetPerson> halfSiblingsGen = geneanetPerson.getHalfSiblings();
        ArrayList<Person> halfSiblingGed = gedcomPerson.getHalfSiblings();
        if (!superEquals2(halfSiblingsGen, halfSiblingGed, peopleUrl) && !halfSiblingsGen.isEmpty()) {
            addDifference(geneanetPerson, "HalfSiblings=" + printListofGeneanetPerson(halfSiblingsGen));
        }

    }

    private void compareSiblings(GeneanetPerson geneanetPerson, Person gedcomPerson) {
        ArrayList<GeneanetPerson> siblingsGen = geneanetPerson.getSiblings();
        ArrayList<Person> siblingsGed = gedcomPerson.getSiblings();
        if (!superEquals2(siblingsGen, siblingsGed, peopleUrl) && !siblingsGen.isEmpty()) {
            addDifference(geneanetPerson, "Siblings=" + printListofGeneanetPerson(siblingsGen));
        }
    }

    private static boolean superEquals2(ArrayList<GeneanetPerson> siblingsGen, ArrayList<Person> siblingsGed, HashMap<String, GeneanetPerson> peopleUrl) {
        if (siblingsGed.size() < siblingsGen.size()) {
            return false;
        }
        for (GeneanetPerson personGen : siblingsGen) {
            boolean found = false;
            GeneanetPerson tmpPerson = peopleUrl.get((personGen.getUrl()));
            if (tmpPerson != null) {
                for (Person personGed : siblingsGed) {
                    if (superEquals(tmpPerson.getFullName(), personGed.getFullName())) {
                        found = true;
                    }
                }
                if (!found) {
                    return false;
                }
            }
        }
        return true;
    }

    private void compareMarriage(GeneanetPerson geneanetPerson, Person gedcomPerson) throws Exception {
        HashMap<GeneanetPerson, HashMap<MyDate, String>> marriageGen = geneanetPerson.getMarriage();
        ArrayList<Union> marriageGed = gedcomPerson.getUnions();

        for (Map.Entry<GeneanetPerson, HashMap<MyDate, String>> entry : marriageGen.entrySet()) {
            GeneanetPerson person = entry.getKey();
            GeneanetPerson fullGeneanetPerson = peopleUrl.get(removeDoubleGeneanetSuffix(person.getUrl()));
            if (fullGeneanetPerson == null) {
                throw new Exception("Person " + person.getUrl() + " not found");
            }
            HashMap<MyDate, String> value = entry.getValue();
            value.entrySet().stream()
                    .filter(entry2 -> entry2.getKey() != null)
                    .filter(entry2 -> entry2.getValue() != null)
                    .filter(entry2 -> (!supercontains(marriageGed, fullGeneanetPerson, entry2.getKey(), entry2.getValue())))
                    .forEach(entry2 -> addDifference(fullGeneanetPerson, "Marriage=" +
                            fullGeneanetPerson.getFullName() + ";" + entry2.getKey() + ";" + entry2.getValue()));
        }
    }

    private static boolean supercontains(ArrayList<Union> marriageGed, GeneanetPerson person, MyDate date, String town) {
        Boolean isGenPersonMale = Gender.Male.equals(person.getGender());
        for (Union union : marriageGed) {
            Person partner = union.getPerson();
            boolean isGedPersonMale = Sex.MALE.equals(partner.getSex());
            if (isGenPersonMale && !isGedPersonMale || !isGenPersonMale && isGedPersonMale) {
                partner = union.getPartner();
            }
            MyDate date1 = union.getDate();
            Town town1 = union.getTown();
            if (superEqualsAlias(partner.getFullName(), person.getFullName()) && superEquals(date, date1) && town1 != null && superEquals(town, town1.getName())) {
                return true;
            }
        }
        return false;
    }

    private void compareDeath(GeneanetPerson geneanetPerson, Person gedcomPerson) {
        MyDate DeathDateGen = geneanetPerson.getDeathDate();
        Death death = gedcomPerson.getDeath();
        MyDate DeathDateGed = null;
        if (death != null) {
            DeathDateGed = gedcomPerson.getDeath().getDate();
        }
        if (!superEquals(DeathDateGed, DeathDateGen) && DeathDateGen != null) {
            addDifference(geneanetPerson, "DeathDate=" + DeathDateGen);
        }

        String placeOfDeathGen = geneanetPerson.getPlaceOfDeath();
        String placeOfDeathGed = null;
        if (death != null) {
            placeOfDeathGed = gedcomPerson.getDeath().getTown().getName();
        }
        if (!superEquals(placeOfDeathGen, placeOfDeathGed) && placeOfDeathGen != null) {
            addDifference(geneanetPerson, "DeathPlace=" + placeOfDeathGen);
        }
    }

    private void compareBirth(GeneanetPerson geneanetPerson, Person gedcomPerson) {
        MyDate birthDateGen = geneanetPerson.getBirthDate();
        Birth birth = gedcomPerson.getBirth();
        MyDate birthDateGed = null;
        if (birth != null) {
            birthDateGed = birth.getDate();
        }
        if (!superEquals(birthDateGed, birthDateGen) && birthDateGen != null) {
            addDifference(geneanetPerson, "BirthDate=" + birthDateGen);
        }

        String placeOfBirthGen = geneanetPerson.getPlaceOfBirth();
        String placeOfBirthGed = null;
        if (birth != null) {
            Town town = birth.getTown();
            if (town != null) {
                placeOfBirthGed = town.getName();
            }
        }
        if (!superEquals(placeOfBirthGen, placeOfBirthGed) && placeOfBirthGen != null) {
            addDifference(geneanetPerson, "BirthPlace=" + placeOfBirthGen);
        }
    }

    private static boolean superEquals(Object object1, Object object2) {
        if (object1 == null && object2 == null) {
            return true;
        } else if (object1 != null && object2 != null) {
            return object1.equals(object2);
        } else {
            return false;
        }
    }

    private static boolean superEquals(String string1, String string2) {
        if (string1 == null && string2 == null) {
            return true;
        } else if (string1 != null && string2 != null) {
            String newString1 = StringUtils.stripAccents(string1).toLowerCase() + " ";
            String newString2 = StringUtils.stripAccents(string2).toLowerCase() + " ";
            if (!newString1.equals(newString2)) {
                //CityAlias
                for (Map.Entry<String, String> entry : aliasCities.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    newString1 = newString1.replace(key, value);
                    newString2 = newString2.replace(key, value);
                }

                //RegexCityAlias
                for (Map.Entry<String, String> entry : aliasRegexCities.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    newString1 = newString1.replaceAll(key, value);
                    newString2 = newString2.replaceAll(key, value);
                }
                return newString1.trim().equals(newString2.trim());
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    private static boolean superEqualsAlias(String string1, String string2) {
        if (string1 == null && string2 == null) {
            return true;
        } else if (string1 != null && string2 != null) {
            String newString1 = StringUtils.stripAccents(string1).toLowerCase() + " ";
            String newString2 = StringUtils.stripAccents(string2).toLowerCase() + " ";
            if (!newString1.equals(newString2)) {
                //NameAlias
                for (Map.Entry<String, String> entry : aliasNames.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    newString1 = newString1.replace(key, value);
                    newString2 = newString2.replace(key, value);
                }

                //CityAlias
                for (Map.Entry<String, String> entry : aliasCities.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    newString1 = newString1.replace(key, value);
                    newString2 = newString2.replace(key, value);
                }

                //RegexCityAlias
                for (Map.Entry<String, String> entry : aliasRegexCities.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    newString1 = newString1.replaceAll(key, value);
                    newString2 = newString2.replaceAll(key, value);
                }
                String newStringTrimed1 = newString1.trim();
                String newStringTrimed2 = newString2.trim();
                if (newStringTrimed1.equals(newStringTrimed2)) {
                    return true;
                } else {
                    char charArray1[] = newStringTrimed1.toCharArray();
                    Arrays.sort(charArray1);
                    char charArray2[] = newStringTrimed2.toCharArray();
                    Arrays.sort(charArray2);
                    return StringUtils.equals(new String(charArray1), (new String(charArray2)));
                }
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public void initAlias(boolean reset) {
        if ((aliasCities == null && aliasNames == null && aliasRegexCities == null) || reset) {
            logger.info("Initialisation des alias");
            try {
                String path = Serializer.getPath();
                if (path == null) {
                    path = Serializer.getInstance().getPath();
                }
                aliasCities = new LinkedHashMap<>();
                aliasNames = new LinkedHashMap<>();
                aliasRegexCities = new LinkedHashMap<>();
                File f = new File(path + "ComparatorAlias.txt");

                BufferedReader b = new BufferedReader(new FileReader(f));
                String readLine = StringUtils.EMPTY;

                while ((readLine = b.readLine()) != null) {
                    String[] inputTab = readLine.split(";");
                    if (inputTab.length > 2 && inputTab[2] != null) {
                        switch (inputTab[2]) {
                            case "city":
                                aliasCities.put(inputTab[0], inputTab[1]);
                                break;
                            case "name":
                                aliasNames.put(inputTab[0], inputTab[1]);
                                break;
                            case "cityRegex":
                                aliasRegexCities.put(inputTab[0], inputTab[1]);
                                break;
                            default:
                                throw new Exception("Unknown alias type " + inputTab[2]);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Could not initialize cityAlias file : " + e);
            }
        }
    }

    private void compareDifferences(HashMap<GeneanetPerson, String> differences2) {
        String result = StringUtils.EMPTY;
        int cptUnknownPeople = 0;
        String printedUrl = StringUtils.EMPTY;
        //Differences missing in file
        for (Map.Entry<GeneanetPerson, String> entry : differences.entrySet()) {
            GeneanetPerson person = entry.getKey();
            printedUrl = person.getUrl();
            if (printedUrl.contains("&p=x&n=x")) {
                printedUrl = "Person " + cptUnknownPeople++;
            }
            if (!person.isSearched()) {
                person = peopleUrl.get(person.getUrl());
            }
            if (person != null && person.getUrl().contains("&i=")) {
                Pattern pattern = Pattern.compile(".*&i=([0-9]*).*");
                Matcher matcher = pattern.matcher(person.getUrl());
                if (matcher.matches() && matcher.groupCount() == 1) {
                    String oldUrl = person.getUrl();
                    String newURL = person.getUrl().replaceAll(escapeSpecialRegexChars("&i=") + matcher.group(1), "&z=" + person.customHashCode());
                    person.setUrl(newURL);
                    peopleUrl.put(removeDoubleGeneanetSuffix(newURL), person);
                    urlAlias.put(newURL, oldUrl);
                }
            }
            String valueTxt = entry.getValue();
            if (valueTxt.contains("&i=")) {
                Pattern pattern = Pattern.compile(".*&i=([0-9]*).*");
                Matcher matcher = pattern.matcher(valueTxt);
                if (matcher.matches() && matcher.groupCount() == 1) {
                    String oldValueTxt = valueTxt;
                    valueTxt = valueTxt.replaceAll(escapeSpecialRegexChars("&i=") + matcher.group(1), "&z=" + person.customHashCode());
                    urlAlias.put(oldValueTxt, valueTxt);
                }
            }
            if (!differences2.containsKey(person) || differences2.containsKey(person) && !valueTxt.equals(differences2.get(person))) {
                result += valueTxt;
                differencesForDisplay.put(person, valueTxt);
                comparisonResultDisplay = result;
                peopleUrlError = person.getUrl();
                peopleFullNameError = person.getFullName();
                comparisonResultToReplace = printedUrl + ";" + differences2.get(person);
                comparisonResultReplacement = printedUrl + ";" + valueTxt;
                return;
            }
        }

        //Differences added in file - deletion
        cptUnknownPeople = 0;
        for (Map.Entry<GeneanetPerson, String> entry : differences2.entrySet()) {
            GeneanetPerson person = entry.getKey();
            printedUrl = person.getUrl();
            if (printedUrl.contains("&p=x&n=x&oc=")) {
                printedUrl = "Person " + cptUnknownPeople++;
            }
            String valueTxt = entry.getValue();
            if (!person.isSearched()) {
                person = peopleUrl.get(person.getUrl());
                if (person == null) {
                    logger.warn("Could not find person " + entry.getKey().getUrl());
                    comparisonResultReplacement = StringUtils.EMPTY;
                    comparisonResultDisplay = "Deletion of " + valueTxt + " ?";
                    comparisonResultToReplace = printedUrl + ";" + valueTxt;
                    return;
                }
            }
            String newValue = differences.get(person);
            if (newValue != null && newValue.contains("&i=")) {
                Pattern pattern = Pattern.compile(".*&i=([0-9]*).*");
                Matcher matcher = pattern.matcher(newValue);
                if (matcher.matches() && matcher.groupCount() == 1) {
                    String oldValue = newValue;
                    newValue = newValue.replaceAll(escapeSpecialRegexChars("&i=") + matcher.group(1), "&z=" + person.customHashCode());
                    urlAlias.put(oldValue, newValue);
                }
            }

            if (!differences.containsKey(person) || differences.containsKey(person) && !valueTxt.equals(newValue)) {
                result += person.getFullName() + ";" + valueTxt;
                differencesForDisplay.put(person, differences2.get(person));
                comparisonResultDisplay = "Deletion of " + result + " ?" + System.lineSeparator() + person.getUrl();
                if (differences.get(person) == null) {
                    comparisonResultReplacement = StringUtils.EMPTY;
                } else {
                    comparisonResultReplacement = printedUrl + ";" + differences.get(person);
                }
                comparisonResultToReplace = printedUrl + ";" + valueTxt;
                return;
            }
        }
        comparisonResultDisplay = result;
        comparisonResultToReplace = result;
    }

    private void addDifference(GeneanetPerson geneanetPerson, String inputTxt) {
        String txt = inputTxt;
        //Replace random id by custom hashcode
        if (geneanetPerson != null && geneanetPerson.getUrl().contains("&i=")) {
            Pattern pattern = Pattern.compile(".*&i=([0-9]*).*");
            Matcher matcher = pattern.matcher(geneanetPerson.getUrl());
            if (matcher.matches() && matcher.groupCount() == 1) {
                String oldUrl = geneanetPerson.getUrl();
                String newURL = geneanetPerson.getUrl().replaceAll(escapeSpecialRegexChars("&i=") + matcher.group(1), "&z=" + geneanetPerson.customHashCode());
                geneanetPerson.setUrl(newURL);
                peopleUrl.put(removeDoubleGeneanetSuffix(newURL), geneanetPerson);
                urlAlias.put(newURL, oldUrl);
            }
        }

        if (differences.containsKey(geneanetPerson)) {
            String oldTxt = differences.get(geneanetPerson);
            if (!oldTxt.contains(txt)) {
                differences.put(geneanetPerson, oldTxt + ";" + txt);
            }
        } else {
            differences.put(geneanetPerson, geneanetPerson.getFullName() + ";" + txt);
        }
    }

    private void saveDifference(String tree) {
        String path = Serializer.getPath();
        if (path == null) {
            path = Serializer.getInstance().getPath();
        }
        File file = new File(path + File.separator + "comparatorTrees" + File.separator + tree + ".bak2");
        FileWriter fr = null;
        try {
            fr = new FileWriter(file);
            fr.write(getDifferencesWithinString());
        } catch (IOException e) {
            logger.error("Failed to save differences for tree " + tree, e);
        } finally {
            //close resources
            try {
                fr.close();
            } catch (IOException e) {
                logger.error("Failed to close FileWriter", e);
            }
        }
    }

    public void addDifferenceInFile() {
        String difference = printDifferences(false, false);
        String treeName = getTreeName();
        String path = Serializer.getPath();
        if (path == null) {
            path = Serializer.getInstance().getPath();
        }
        //logger.debug("For tree " + treeName + " , add line : " + difference);
        String fileName = path + File.separator + "comparatorTrees" + File.separator + treeName + ".bak";
        File file = new File(fileName);
        FileWriter fr = null;
        try {
            fr = new FileWriter(file, true);
            fr.write(difference);
            fr.close();
        } catch (IOException e) {
            logger.error("Could not write into " + fileName);
        }
    }

    public void replaceDifferenceInFile() {
        changeDifferenceInFile(false);
    }

    public void deleteDifferenceInFile() {
        changeDifferenceInFile(true);
    }

    public void changeDifferenceInFile(boolean delete) {
        String treeName = getTreeName();
        String find = getComparisonResultToReplace();
        String difference = getComparisonResultReplacement();
        if (!delete && StringUtils.isEmpty(difference)) {
            logger.error("Can only remove field");
            return;
        }
        if (StringUtils.isEmpty(difference)) {
            find += System.lineSeparator();
        }
        String resultToPrint = StringUtils.EMPTY;
        int lineNumber = 1;

        //read data
        String path = Serializer.getPath();
        if (path == null) {
            path = Serializer.getInstance().getPath();
        }
        String fileName = path + File.separator + "comparatorTrees" + File.separator + treeName + ".bak";

        try (FileReader reader = new FileReader(fileName);
             BufferedReader br = new BufferedReader(reader)) {

            // read line by line
            String line;
            while ((line = br.readLine()) != null) {
                if (lineNumber > 1) {
                    resultToPrint += System.lineSeparator() + line;
                } else {
                    resultToPrint += line;
                }
                lineNumber++;
            }

        } catch (IOException e) {
            logger.error("IOException replacing differences in file: %s%n", e);
        }

        //replace data
        resultToPrint += System.lineSeparator();
        resultToPrint = resultToPrint.replace(find, difference);
        if (resultToPrint != null && resultToPrint.length() > 0) {
            resultToPrint = resultToPrint.substring(0, resultToPrint.length() - 2);
        }

        //Printing data into file
        File file = new File(fileName);
        FileWriter fr = null;
        try {
            fr = new FileWriter(file, false);
            fr.write(resultToPrint);
            fr.close();
        } catch (IOException e) {
            logger.error("Could not write into " + fileName);
        }
    }

    public HashMap<GeneanetPerson, String> readDifferences(String tree) {
        HashMap<GeneanetPerson, String> result = new HashMap<GeneanetPerson, String>();
        String path = Serializer.getPath();
        if (path == null) {
            path = Serializer.getInstance().getPath();
        }
        try (BufferedReader br = new BufferedReader(new FileReader(path + File.separator + "comparatorTrees" + File.separator + tree + ".bak"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tmpLine = line.split(";");
                if (tmpLine != null && tmpLine.length > 1) {
                    GeneanetPerson person = new GeneanetPerson(removeDoubleGeneanetSuffix(tmpLine[0]));
                    person.setImage(tmpLine[1]);
                    String txt = StringUtils.EMPTY;
                    for (int i = 1; i < tmpLine.length; i++) {
                        if (i != 1) {
                            txt += ";" + tmpLine[i];
                        } else {
                            txt += tmpLine[i];
                        }
                    }
                    result.put(person, txt);
                }
            }
        } catch (FileNotFoundException e) {
            logger.info("Difference file not found for " + tree);
        } catch (IOException e) {
            logger.error("Failed to compare differences for tree " + tree, e);
        }
        return result;
    }

    private void compareNames(GeneanetPerson geneanetPerson, Person gedcomPerson) {
        String firstNameGenea = geneanetPerson.getFirstName();
        String firstNameGed = gedcomPerson.getSurname();
        if (!superEquals(firstNameGenea, firstNameGed)) {
            addDifference(geneanetPerson, "firstName=" + firstNameGenea);
        }

        String familyNameGenea = geneanetPerson.getFamilyName();
        String familyNameGed = gedcomPerson.getName();
        if (!superEquals(familyNameGenea, familyNameGed)) {
            addDifference(geneanetPerson, "familyName=" + familyNameGenea);
        }
    }

    private void compareSex(GeneanetPerson geneanetPerson, Person gedcomPerson) {
        String sexGenea = geneanetPerson.getGender().toString();
        String sexGed = gedcomPerson.getSex().toString();
        if (!superEquals(sexGenea, sexGed)) {
            addDifference(geneanetPerson, "sex=" + sexGenea);
        }
    }

    /**
     * Write the object to a Base64 string.
     */
    private static String toString(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    /**
     * Read the object from Base64 string.
     */
    private static Object fromString(String s) throws IOException,
            ClassNotFoundException {
        byte[] data = Base64.getDecoder().decode(s);
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(data));
        Object o = ois.readObject();
        ois.close();
        return o;
    }

    public static void saveGeneanetBrowserIntoFile(GeneanetBrowser geneanetBrowser, String url) {
        String path = Serializer.getPath();
        if (path == null) {
            path = Serializer.getInstance().getPath();
        }
        File fichier = new File(path + File.separator + "geneanetTrees" + File.separator + url + ".ser");

        // ouverture d'un flux sur un fichier
        ObjectOutputStream oos = null;

        try {
            oos = new ObjectOutputStream(new FileOutputStream(fichier));
            // sérialization de l'objet
            oos.writeObject(geneanetBrowser);
        } catch (IOException e) {
            logger.error("Failed to serialize geneanet tree " + url, e);
        }
    }


    public static String saveGeneanetBrowser(GeneanetBrowser geneanetBrowser) throws IOException {
        String string = toString(geneanetBrowser);
        return string;
    }

    public static GeneanetBrowser getGeneanetBrowserFromString(String input) throws IOException, ClassNotFoundException {
        return (GeneanetBrowser) fromString(input);
    }

    public static GeneanetBrowser getGeneanetBrowserFromFile(String url) {
        GeneanetBrowser geneanetBrowser = null;
        String path = Serializer.getPath();
        if (path == null) {
            path = Serializer.getInstance().getPath();
        }
        File fichier = new File(path + File.separator + "geneanetTrees" + File.separator + url + ".ser");

        // ouverture d'un flux sur un fichier
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(fichier));
            // désérialization de l'objet
            geneanetBrowser = (GeneanetBrowser) ois.readObject();
            geneanetBrowser.init();
        } catch (Exception e) {
            logger.error("Failed to read Serialized tree from url " + url, e);
            return null;
        }
        return geneanetBrowser;
    }

    public static String findTinyUrl(String inputUrl) {
        String result = StringUtils.EMPTY;
        if (inputUrl != null) {
            String[] urlTab = inputUrl.split("&p=");
            if (urlTab != null && urlTab.length > 0) {
                result = urlTab[0];
            }
        }
        return result;
    }

    public static String escapeSpecialRegexChars(String str) {
        return SPECIAL_REGEX_CHARS.matcher(str).replaceAll("\\\\$0");
    }

    public void analyseTree() {
        String[] replaceSplit = comparisonResultToReplace.split(";");
        String resultat = replaceSplit[1];
        List<String> listReplace = Arrays.asList(replaceSplit);
        List<String> listReplacement = Arrays.asList(comparisonResultReplacement.split(";"));
        List<String> replaceOnlyList = new ArrayList<>(listReplace);
        replaceOnlyList.removeAll(listReplacement);
        List<String> removeOnlyList = new ArrayList<>(listReplacement);
        removeOnlyList.removeAll(listReplace);
        if (!replaceOnlyList.isEmpty() && !StringUtils.equals(replaceOnlyList.get(0), "null")) {
            removeReplacement = StringUtils.EMPTY + replaceOnlyList;
            logger.info(removeReplacement + " <- will be removed");
        }
        if (!removeOnlyList.isEmpty() && (removeOnlyList.size() != 1 || !StringUtils.isBlank(removeOnlyList.get(0)))) {
            addReplacement = StringUtils.EMPTY + removeOnlyList;
            logger.info(addReplacement + " <- will be added");
        }
        logger.info("Add line ? (A to add/replace/delete, exit to exit, any other to refresh data)");
    }

    public void writeModification() throws Exception {
        if (StringUtils.isEmpty(comparisonResultReplacement)) {
            logger.info("Deletion carried out");
            deleteDifferenceInFile();
        } else {
            String[] replaceSplit = comparisonResultToReplace.split(";");
            if (StringUtils.equals(replaceSplit[1], "null")) {
                logger.info("Modification added");
                addDifferenceInFile();
            } else {
                logger.info("Modification carried out");
                replaceDifferenceInFile();
            }
        }
    }

    public Genealogy makeModification(String addModification, Genealogy genealogyParam) throws Exception {
        switch (addModification) {
            case "A":
            case "ADD":
                writeModification();
                break;
            case "exit":
                throw new Exception("User exited the program");
            default:
                logger.info("Rerun needed");
                TreeComparatorManager.getInstance().refreshGedcomData();
                initAlias(true);
                return genealogy;
        }
        return genealogyParam;
    }

    public String compareTree(String testUrl, Genealogy genealogy) throws Exception {
        url = testUrl;
        //Geneanet Browser
        String tree = GeneanetBrowser.findTreeName(testUrl);
        GeneanetBrowser geneanetBrowser = null;
        if (!searchOnGeneanet) {
            geneanetBrowser = getGeneanetBrowserFromFile(tree);
            if (geneanetBrowser == null) {
                TreeComparatorManager.getInstance().serializationProblem(tree);
            }
        }

        if (searchOnGeneanet || geneanetBrowser == null) {
            GeneanetBrowser geneanetBrowser0 = mainSearchFullTree(testUrl);
            if (geneanetBrowser0.isKill()) {
                return "killed";
            }

            int people = geneanetBrowser0.getNbPeople();
            int expectedPeople = geneanetBrowser0.getPeopleNumberFromGeneanetTrees();
            if (expectedPeople != people) {
                if (exceptionMode) {
                    logger.error("Test KO for URL " + testUrl + " : expected " + expectedPeople + " but got " + people);
                    throw new Exception("Error : Found new number for search for tree " + tree);
                }
            } else {
                logger.info("Test OK for URL " + tree);
            }
            saveGeneanetBrowserIntoFile(geneanetBrowser0, tree);
            if (searchOnGeneanet) {
                geneanetBrowser0.saveSearchOutput();
            }
            geneanetBrowser = getGeneanetBrowserFromFile(tree);
        }

        GeneanetPerson rootPerson = geneanetBrowser.rootPerson;

        //Gedcom file
        String personId = geneanetBrowser.getGedcomIdFromGeneanetTrees();
        if (StringUtils.isEmpty(personId)) {
            logger.error("Problem getting rootPerson id for " + tree);
            throw new Exception("Could not find id");
        }
        Person person = genealogy.findPersonById(personId);

        //Comparing
        initTreeComparator(rootPerson, person, geneanetBrowser.allPeopleUrl, tree);
        setLog(false);
        compareRoot();
        int nbPeopleComp = getPeopleSize();
        int nbPeopleGen = geneanetBrowser.getNbPeople();
        if (nbPeopleGen == nbPeopleComp) {
            //logger.info("Comparison OK for " + testUrl);
        } else {
            logger.info("Comparison KO for " + testUrl + " Expected " + nbPeopleGen + " but got " + nbPeopleComp);
        }
        if (searchOnGeneanet) {
            saveDifference(tree);
        }
        HashMap<GeneanetPerson, String> geneanetPersonStringHashMap = readDifferences(tree);
        compareDifferences(geneanetPersonStringHashMap);
        return printData(tree, geneanetPersonStringHashMap);
    }

    private String printData(String tree, HashMap<GeneanetPerson, String> geneanetPersonStringHashMap) throws Exception {
        String result = StringUtils.EMPTY;
        String comparison = getComparisonResultDisplay();
        if (comparison != null && StringUtils.isNotEmpty(comparison)) {
            printDifferences(true, true);
            String difference = geneanetPersonStringHashMap.size() + "/" + getDifferences().size() + " differences of " + tree + " tree :";
            stats = geneanetPersonStringHashMap.size() + "/" + getDifferences().size();
            logger.info(difference);
            result += difference + System.lineSeparator();
            logger.error(comparison + System.lineSeparator() + peopleUrlError);
            result += comparison;
            setErrorComparison(true);
            if (exceptionMode) {
                throw new Exception("Error with comparison for tree " + tree);
            }
        } else {
            result += "Tree " + tree + " OK";
        }
        return result;
    }

    private static void printDirectAncestorsToInvestigate() {
        genealogy.getPersons().stream().filter(Person::isDirectAncestor).map(Person::printNecessaryResearch);
    }
}
