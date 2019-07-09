package Genealogy.URLConnexion.Geneanet;

import Genealogy.Genealogy;
import Genealogy.Model.Act.Birth;
import Genealogy.Model.Act.Death;
import Genealogy.Model.Act.Union;
import Genealogy.Model.Date.MyDate;
import Genealogy.Model.Person;
import Genealogy.Model.Town;
import Genealogy.Parsing.MyGedcomReader;
import Genealogy.URLConnexion.Serializer;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.BasicConfigurator;

import java.io.*;
import java.util.*;

import static Genealogy.AuxMethods.removeDoubleGeneanetSuffix;
import static Genealogy.Genealogy.genealogy;
import static Genealogy.URLConnexion.Geneanet.GeneanetBrowser.logger;
import static Genealogy.URLConnexion.Geneanet.GeneanetBrowser.mainSearchFullTree;
import static Genealogy.URLConnexion.Geneanet.GeneanetPerson.printListofGeneanetPerson;

/**
 * Created by Dan on 02/12/2018.
 * Export en UTF8 à partir de ahnenblatt : export + encoder en utf8, encoder en ansi puis convertir en utf8
 */
public class TreeComparator {
    private GeneanetPerson geneanetRoot;
    private Person gedcomRoot;
    private LinkedHashMap<GeneanetPerson,String> differences = new LinkedHashMap<>();
    private LinkedHashMap<GeneanetPerson,String> differencesForDisplay = new LinkedHashMap<>();
    public HashMap<String, GeneanetPerson> peopleUrl = new HashMap<String, GeneanetPerson>();
    public static ArrayList<String> urlSearched = new ArrayList<String>();
    public static ArrayList<String> urlPartnersSearched = new ArrayList<String>();
    public static String gedcomFile;
    private boolean log = true;
    private boolean errorComparison = false;
    private String treeName;
    private String comparisonResultDisplay;
    private String comparisonResultToReplace;
    private String comparisonResultReplacement;
    private static LinkedHashMap<String,String> aliasCities;
    private static LinkedHashMap<String,String> aliasRegexCities;
    private static LinkedHashMap<String,String> aliasNames;

    public TreeComparator(GeneanetPerson geneanetPerson, Person gedcomPerson, HashMap<String, GeneanetPerson> peopleUrl, String treeName) {
        urlSearched = new ArrayList<String>();
        urlPartnersSearched = new ArrayList<String>();
        this.geneanetRoot = geneanetPerson;
        this.gedcomRoot = gedcomPerson;
        this.peopleUrl = peopleUrl;
        this.treeName = treeName;
        initAlias(false);
    }

    public int getPeopleSize(){
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

    public String printDifferences(boolean displayModeFull, boolean hide, boolean log){
        String result = "";
        if (!hide){
            HashMap<GeneanetPerson,String> tmpDifferences;
            if (displayModeFull) {
                tmpDifferences = differences;
            } else {
                tmpDifferences = differencesForDisplay;
            }
            for (Map.Entry<GeneanetPerson, String> entry : tmpDifferences.entrySet()) {
                if (log){
                    logger.info("\"" + entry.getKey().getUrl() + "\";" + entry.getKey().getFullName() + ";" + entry.getValue());
                } else {
                    result += System.lineSeparator() + entry.getKey().getUrl() + ";" + entry.getKey().getFullName() + ";" + entry.getValue();
                }
            }
        }
        return result;
    }

    public String getDifferencesWithinString(){
        String result = "";
        for(Map.Entry<GeneanetPerson, String> entry : differences.entrySet()) {
            result += entry.getKey().getUrl() + ";" + entry.getKey().getFullName() + ";" + entry.getValue() + System.lineSeparator();
        }
        return result;
    }

    public static String getDifferencesWithinString(HashMap<GeneanetPerson,String> differences){
        String result = "";
        for(Map.Entry<GeneanetPerson, String> entry : differences.entrySet()) {
            result += entry.getKey().getUrl() + ";" + entry.getValue() + System.lineSeparator();
        }
        return result;
    }

    private void compareRoot() throws Exception {
        if (log){
            logger.info("Compared " + gedcomRoot.getFullName() + " (" + geneanetRoot.getUrl() + ")");
        }
        compareNames(geneanetRoot, gedcomRoot);
        compareSex(geneanetRoot, gedcomRoot);
        compareBirth(geneanetRoot, gedcomRoot);
        compareDeath(geneanetRoot, gedcomRoot);
        compareMarriage(geneanetRoot, gedcomRoot);
        //compareSiblings(geneanetRoot, gedcomRoot);
        //compareHalfSiblings(geneanetRoot, gedcomRoot);
        //compareChildren(geneanetRoot, gedcomRoot);

        urlSearched.add(geneanetRoot.getUrl());
        searchFamily(geneanetRoot, gedcomRoot, true);
        searchChildren(geneanetRoot, gedcomRoot);
    }

    private void comparePerson(GeneanetPerson geneanetPerson, Person gedcomPerson) throws Exception {
        if (urlSearched.contains(geneanetPerson.getUrl())){
            return;
        }
        if (log){
            logger.info("Compared " + gedcomPerson.getFullName() + "(" + geneanetPerson.getUrl() + ")");
        }
        if (geneanetPerson != null && geneanetPerson.getFullName().equals("Marie-Louise DUPIN (LEPIN)")){
            String res = "";
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
        if (urlSearched.contains(geneanetPerson.getUrl())){
            return;
        }
        if (log){
            logger.info("Compared " + gedcomPerson.getFullName() + "(" + geneanetPerson.getUrl() + ")");
        }
        compareNames(geneanetPerson, gedcomPerson);
        compareSex(geneanetPerson, gedcomPerson);
        compareBirth(geneanetPerson, gedcomPerson);
        compareDeath(geneanetPerson, gedcomPerson);
        if (!children){
            compareMarriage(geneanetPerson, gedcomPerson);
        }
        //compareSiblings(geneanetPerson, gedcomPerson);
        //compareHalfSiblings(geneanetPerson, gedcomPerson);

        urlSearched.add(geneanetPerson.getUrl());
        //Search partners
        if ((geneanetPerson != null || gedcomPerson != null)&&(!children)){
            searchPartners(geneanetPerson, gedcomPerson, false);
        }
    }

    private void partialCompare(GeneanetPerson geneanetPerson, Person gedcomPerson, boolean root) throws Exception {
        if (log){
            logger.info("Compared partial " + gedcomPerson.getFullName() + "(" + geneanetPerson.getUrl() + ")");
        }
        urlPartnersSearched.add(geneanetPerson.getUrl());
        compareNames(geneanetPerson, gedcomPerson);
        compareSex(geneanetPerson, gedcomPerson);
        compareBirth(geneanetPerson, gedcomPerson);
        compareDeath(geneanetPerson, gedcomPerson);
        compareMarriage(geneanetPerson, gedcomPerson);
        //compareSiblings(geneanetPerson, gedcomPerson);
        //compareHalfSiblings(geneanetPerson, gedcomPerson);
        if (root){
            searchFamily(geneanetPerson, gedcomPerson,false);
        }
    }

    private void compareAndSearchPerson(GeneanetPerson geneanetPerson, Person gedcomPerson) throws Exception {
        comparePerson(geneanetPerson, gedcomPerson);
        searchFamily(geneanetPerson, gedcomPerson, false);
    }

    private void searchFamily(GeneanetPerson geneanetPerson, Person gedcomPerson, boolean root) throws Exception {
        if (geneanetPerson != null || gedcomPerson != null){
            searchParents(geneanetPerson, gedcomPerson);
            searchSiblings(geneanetPerson, gedcomPerson);
            searchHalfSiblings(geneanetPerson, gedcomPerson);
            searchPartners(geneanetPerson, gedcomPerson, root);
        } // Do not search double null
    }

    private void searchPartners(GeneanetPerson geneanetPerson, Person gedcomPerson, boolean root) throws Exception {
        for(Map.Entry<GeneanetPerson, HashMap<MyDate, String>> entry : geneanetPerson.getMarriage().entrySet()) {
            GeneanetPerson genPersonToSearch = entry.getKey();
            genPersonToSearch = peopleUrl.get((removeDoubleGeneanetSuffix(genPersonToSearch.getUrl())));
            if (genPersonToSearch == null){
                throw new Exception("Could not find person " + entry.getKey().getUrl());
            }
            for (Union union : gedcomPerson.getUnions()){
                Person partner = union.getPartner();
                if (superEqualsAlias(genPersonToSearch.getFullName(),partner.getFullName())){
                    superComparePartner(genPersonToSearch,partner,"partner", root);
                }
                Person citizen = union.getCitizen();
                if (superEqualsAlias(genPersonToSearch.getFullName(),citizen.getFullName())){
                    superComparePartner(genPersonToSearch,citizen,"partner", root);
                }
            }
        }
    }

    private void searchRelatives(GeneanetPerson geneanetPerson, Person gedcomPerson, ArrayList<GeneanetPerson> siblingsGen, ArrayList<Person> siblingsGed, String txt) throws Exception {
        ArrayList<Person> gedcomPersonList = new ArrayList<>();
        if (geneanetPerson != null){
            for (GeneanetPerson siblingGen : siblingsGen){
                GeneanetPerson newSiblingGen = peopleUrl.get(removeDoubleGeneanetSuffix(siblingGen.getUrl()));
                if (newSiblingGen == null){
                    throw new Exception("Could not find person " + siblingGen.getUrl());
                }
                for (Person siblingGed : siblingsGed){
                    if (newSiblingGen != null && superEqualsAlias(newSiblingGen.getFullName(),siblingGed.getFullName()) && !gedcomPersonList.contains(siblingGed)
                            && !urlSearched.contains(newSiblingGen.getUrl())){
                        compareSibling(newSiblingGen,siblingGed, txt.equals("Children"));
                        gedcomPersonList.add(siblingGed);
                    }
                }
                if (!urlSearched.contains(newSiblingGen.getUrl())){
                    addDifference(geneanetPerson,"Missing " + txt + " : " + siblingGen.getUrl() );
                }
            }
        }
    }

    private void searchChildren(GeneanetPerson geneanetPerson, Person gedcomPerson) throws Exception {
        ArrayList<GeneanetPerson> childrenGen = geneanetPerson.getChildren();
        ArrayList<Person> childrenGed = gedcomPerson.getChildren();
        searchRelatives(geneanetPerson, gedcomPerson, childrenGen, childrenGed, "Children");
    }

    private void searchSiblings(GeneanetPerson geneanetPerson, Person gedcomPerson) throws Exception {
        ArrayList<GeneanetPerson> siblingsGen = geneanetPerson.getSiblings();
        ArrayList<Person> siblingsGed = gedcomPerson.getSiblings();
        searchRelatives(geneanetPerson, gedcomPerson, siblingsGen, siblingsGed, "Sibling");
    }

    private void searchHalfSiblings(GeneanetPerson geneanetPerson, Person gedcomPerson) throws Exception {
        ArrayList<GeneanetPerson> siblingsGen = geneanetPerson.getHalfSiblings();
        ArrayList<Person> siblingsGed = gedcomPerson.getHalfSiblings();
        searchRelatives(geneanetPerson, gedcomPerson, siblingsGen, siblingsGed, "Half Sibling");
    }

    private void superCompareAndSearchPerson(GeneanetPerson geneanetPerson, Person gedcomPerson, String txt) throws Exception {
        if (geneanetPerson != null && gedcomPerson != null){
            if (!urlSearched.contains(geneanetPerson.getUrl())){
                comparePerson(geneanetPerson, gedcomPerson);
                //searchFamily(geneanetPerson, gedcomPerson, false);
            }
        } else if (geneanetPerson != null){
            addDifference(geneanetPerson,"Missing " + txt + " : " + geneanetPerson.getUrl() );
        }
    }

    private void superComparePartner(GeneanetPerson geneanetPerson, Person gedcomPerson, String txt, boolean root) throws Exception {
        if (geneanetPerson != null && gedcomPerson != null ){
            if (!urlSearched.contains(geneanetPerson.getUrl()) && !urlPartnersSearched.contains(geneanetPerson.getUrl())){
                partialCompare(geneanetPerson, gedcomPerson, root);
            }
        } else if (geneanetPerson != null){
            addDifference(geneanetPerson,"Missing " + txt + " for " + geneanetPerson.getUrl() );
        }
    }

    private void searchParents(GeneanetPerson geneanetPerson, Person gedcomPerson) throws Exception {
        superCompareAndSearchPerson(geneanetPerson.getFather(), gedcomPerson.getFather(), "Father");
        superCompareAndSearchPerson(geneanetPerson.getMother(), gedcomPerson.getMother(), "Mother");
    }

    private void compareChildren(GeneanetPerson geneanetPerson, Person gedcomPerson) {
        ArrayList<GeneanetPerson> childrenGen = geneanetPerson.getChildren();
        ArrayList<Person> children1Ged = gedcomPerson.getChildren();
        if (!superEquals2(childrenGen, children1Ged, peopleUrl) && !childrenGen.isEmpty()){
            addDifference(geneanetPerson,"Children=" + printListofGeneanetPerson(childrenGen) );
        }
    }

    private void compareHalfSiblings(GeneanetPerson geneanetPerson, Person gedcomPerson) {
        ArrayList<GeneanetPerson> halfSiblingsGen = geneanetPerson.getHalfSiblings();
        ArrayList<Person> halfSiblingGed = gedcomPerson.getHalfSiblings();
        if (!superEquals2(halfSiblingsGen, halfSiblingGed, peopleUrl) && !halfSiblingsGen.isEmpty()){
            addDifference(geneanetPerson,"HalfSiblings=" + printListofGeneanetPerson(halfSiblingsGen) );
        }

    }

    private void compareSiblings(GeneanetPerson geneanetPerson, Person gedcomPerson) {
        ArrayList<GeneanetPerson> siblingsGen = geneanetPerson.getSiblings();
        ArrayList<Person> siblingsGed = gedcomPerson.getSiblings();
        if (!superEquals2(siblingsGen, siblingsGed, peopleUrl) && !siblingsGen.isEmpty()){
            addDifference(geneanetPerson,"Siblings=" + printListofGeneanetPerson(siblingsGen) );
        }
    }

    private static boolean superEquals2(ArrayList<GeneanetPerson> siblingsGen, ArrayList<Person> siblingsGed, HashMap<String, GeneanetPerson> peopleUrl){
        if (siblingsGed.size() < siblingsGen.size()){
            return false;
        }
        for (GeneanetPerson personGen : siblingsGen){
            boolean found = false;
            GeneanetPerson tmpPerson = peopleUrl.get((personGen.getUrl()));
            if (tmpPerson != null){
                for (Person personGed : siblingsGed){
                    if (superEquals(tmpPerson.getFullName(),personGed.getFullName())){
                        found = true;
                    }
                }
                if (!found){
                    return false;
                }
            }
        }
        return true;
    }

    private void compareMarriage(GeneanetPerson geneanetPerson, Person gedcomPerson) throws Exception {
        HashMap<GeneanetPerson, HashMap<MyDate, String>> marriageGen = geneanetPerson.getMarriage();
        ArrayList<Union> marriageGed = gedcomPerson.getUnions();

        for(Map.Entry<GeneanetPerson, HashMap<MyDate, String>> entry : marriageGen.entrySet()) {
            GeneanetPerson person = entry.getKey();
            GeneanetPerson fullGeneanetPerson = peopleUrl.get(removeDoubleGeneanetSuffix(person.getUrl()));
            if (fullGeneanetPerson == null){
                throw new Exception("Person " + person.getUrl() + " not found");
            }
            HashMap<MyDate, String> value = entry.getValue();
            for(Map.Entry<MyDate, String> entry2 : value.entrySet()) {
                MyDate date = entry2.getKey();
                String town = entry2.getValue();
                if (!supercontains(marriageGed,fullGeneanetPerson,date,town) && (date != null || town != null)){
                    addDifference(fullGeneanetPerson,"Marriage=" + fullGeneanetPerson.getFullName() + ";" + date + ";" + town );
                }
            }
        }
    }

    private static boolean supercontains(ArrayList<Union> marriageGed, GeneanetPerson person, MyDate date, String town) {
        Boolean isGenPersonMale = Gender.Male.equals(person.getGender());
        for (Union union : marriageGed){
            Person partner = union.getCitizen();
            boolean isGedPersonMale = Person.Sex.HOMME.equals(partner.getSex());
            if (isGenPersonMale && !isGedPersonMale || !isGenPersonMale && isGedPersonMale){
                partner = union.getPartner();
            }
            MyDate date1 = union.getDate();
            Town town1 = union.getTown();
            if (superEqualsAlias(partner.getFullName(),person.getFullName())&&superEquals(date,date1)&&superEquals(town,town1.getName())){
                return true;
            }
        }
        return false;
    }

    private void compareDeath(GeneanetPerson geneanetPerson, Person gedcomPerson) {
        MyDate DeathDateGen = geneanetPerson.getDeathDate();
        Death death = gedcomPerson.getDeath();
        MyDate DeathDateGed = null;
        if (death != null){
            DeathDateGed = gedcomPerson.getDeath().getDate();
        }
        if (!superEquals(DeathDateGed,DeathDateGen) && DeathDateGen != null){
            addDifference(geneanetPerson,"DeathDate=" + DeathDateGen );
        }

        String placeOfDeathGen = geneanetPerson.getPlaceOfDeath();
        String placeOfDeathGed = null;
        if (death != null){
            placeOfDeathGed = gedcomPerson.getDeath().getTown().getName();
        }
        if (!superEquals(placeOfDeathGen,placeOfDeathGed) && placeOfDeathGen != null){
            addDifference(geneanetPerson,"DeathPlace=" + placeOfDeathGen );
        }
    }

    private void compareBirth(GeneanetPerson geneanetPerson, Person gedcomPerson) {
        MyDate birthDateGen = geneanetPerson.getBirthDate();
        Birth birth = gedcomPerson.getBirth();
        MyDate birthDateGed = null;
        if (birth != null){
            birthDateGed = birth.getDate();
        }
        if (!superEquals(birthDateGed,birthDateGen) && birthDateGen != null){
            addDifference(geneanetPerson,"BirthDate=" + birthDateGen );
        }

        String placeOfBirthGen = geneanetPerson.getPlaceOfBirth();
        String placeOfBirthGed = null;
        if (birth != null){
            Town town = birth.getTown();
            if (town != null){
                placeOfBirthGed = town.getName();
            }
        }
        if (!superEquals(placeOfBirthGen,placeOfBirthGed) && placeOfBirthGen != null){
            addDifference(geneanetPerson,"BirthPlace=" + placeOfBirthGen );
        }
    }
    private static boolean superEquals(Object object1, Object object2){
        if (object1 == null && object2 == null){
            return true;
        } else if (object1 != null && object2 != null){
            return object1.equals(object2);
        } else {
            return false;
        }
    }

    private static boolean superContains(ArrayList<String> list, String input){
        for (String string : list){
            if (superEquals(string, input)){
                return true;
            }
        }
        return false;
    }

    private static boolean superEquals(ArrayList<String> children1, ArrayList<Person> children2){
        for (Person person : children2){
            if (!superContains(children1, person.getFullName())){
                return false;
            }
        }
        return true;
    }

    private static boolean superEquals(String string1, String string2){
        if (string1 == null && string2 == null){
            return true;
        } else if (string1 != null && string2 != null){
            /*if (string1.contains("Laurent PIAT")){
                String res = "";
            }*/
            String newString1 = StringUtils.stripAccents(string1).toLowerCase() + " ";
            String newString2 = StringUtils.stripAccents(string2).toLowerCase() + " ";
            if (!newString1.equals(newString2)){
                //CityAlias
                for (Map.Entry<String, String> entry : aliasCities.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    newString1 = newString1.replace(key,value);
                    newString2 = newString2.replace(key,value);
                }

                //RegexCityAlias
                for (Map.Entry<String, String> entry : aliasRegexCities.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    newString1 = newString1.replaceAll(key,value);
                    newString2 = newString2.replaceAll(key,value);
                }
                return newString1.trim().equals(newString2.trim());
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    private static boolean superEqualsAlias(String string1, String string2){
        if (string1 == null && string2 == null){
            return true;
        } else if (string1 != null && string2 != null){
            /*if (string1.contains("Laurent PIAT")){
                String res = "";
            }*/
            String newString1 = StringUtils.stripAccents(string1).toLowerCase() + " ";
            String newString2 = StringUtils.stripAccents(string2).toLowerCase() + " ";
            if (!newString1.equals(newString2)){
                //NameAlias
                for (Map.Entry<String, String> entry : aliasNames.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    newString1 = newString1.replace(key,value);
                    newString2 = newString2.replace(key,value);
                }

                //CityAlias
                for (Map.Entry<String, String> entry : aliasCities.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    newString1 = newString1.replace(key,value);
                    newString2 = newString2.replace(key,value);
                }

                //RegexCityAlias
                for (Map.Entry<String, String> entry : aliasRegexCities.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    newString1 = newString1.replaceAll(key,value);
                    newString2 = newString2.replaceAll(key,value);
                }
                return newString1.trim().equals(newString2.trim());
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    private void initAlias(boolean reset){
        if ((aliasCities == null && aliasNames == null && aliasRegexCities == null) || reset){
            try {
                String path = Serializer.getPath();
                if (path == null){
                    Serializer serializer = new Serializer();
                    path = serializer.getPath();
                }
                aliasCities = new LinkedHashMap<>();
                aliasNames = new LinkedHashMap<>();
                aliasRegexCities = new LinkedHashMap<>();
                File f = new File(path + "ComparatorAlias.txt");

                BufferedReader b = new BufferedReader(new FileReader(f));
                String readLine = "";

                while ((readLine = b.readLine()) != null) {
                    String[] inputTab = readLine.split(";");
                    if (inputTab.length > 2 && inputTab[2] != null){
                        switch (inputTab[2]){
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
            }
            catch (Exception e){
                logger.error("Could not initialize cityAlias file : " + e);
            }
        }
    }

    private void compareDifferences(HashMap<GeneanetPerson,String> differences2, boolean displayModeFull, boolean hideComparisons) throws Exception {
        String result = "";
        //Differences missing in file
        for(Map.Entry<GeneanetPerson, String> entry : differences.entrySet()) {
            GeneanetPerson person = entry.getKey();
            if (!person.isSearched()){
                person = peopleUrl.get(person.getUrl());
            }
            String valueTxt = entry.getValue();
            if (!differences2.containsKey(person) || differences2.containsKey(person) && !valueTxt.equals(differences2.get(person))){
                result += person.getFullName() + ";" + valueTxt;
                differencesForDisplay.put(person, entry.getValue());
                if ((!displayModeFull && differencesForDisplay.size() == 10)||(hideComparisons)){
                    comparisonResultDisplay = result + System.lineSeparator() + person.getUrl();
                    comparisonResultToReplace = person.getUrl() + ";" + person.getFullName() + ";" + differences2.get(person);
                    comparisonResultReplacement = person.getUrl() + ";" + person.getFullName() + ";" + valueTxt;
                    return;
                }
            }
        }

        //Differences added in file
        for(Map.Entry<GeneanetPerson, String> entry : differences2.entrySet()) {
            GeneanetPerson person = entry.getKey();
            if (!person.isSearched()){
                person = peopleUrl.get(person.getUrl());
                if (person == null){
                    throw new Exception("Could not find person " + entry.getKey().getUrl());
                }
            }
            String valueTxt = entry.getValue();
            if (!differences.containsKey(person) || differences.containsKey(person) && !valueTxt.equals(differences.get(person))){
                result += person.getFullName() + ";" + valueTxt;
                differencesForDisplay.put(person, differences2.get(person));
                if (!displayModeFull){
                    comparisonResultDisplay = result + System.lineSeparator() + person.getUrl();
                    if (differences.get(person) == null){
                        comparisonResultReplacement = "";
                    } else {
                        comparisonResultReplacement = person.getUrl() + ";" + person.getFullName() + ";" + differences.get(person);
                    }
                    comparisonResultToReplace = person.getUrl() + ";" + person.getFullName() + ";" + valueTxt;
                    return;
                }
            }
        }
        comparisonResultDisplay = result;
        comparisonResultToReplace = result;
    }

    private void addDifference(GeneanetPerson geneanetPerson, String txt){
        if (differences.containsKey(geneanetPerson)){
            String oldTxt = differences.get(geneanetPerson);
            if (!oldTxt.contains(txt)){
                differences.put(geneanetPerson,oldTxt + ";" + txt);
            }
        } else {
            differences.put(geneanetPerson, txt);
        }
    }

    private void saveDifference(String tree){
        String path = Serializer.getPath();
        if (path == null){
            Serializer serializer = new Serializer();
            path = Serializer.getPath();
        }
        File file = new File(path + File.separator + "comparatorTrees" + File.separator + tree + ".bak2");
        FileWriter fr = null;
        try {
            fr = new FileWriter(file);
            fr.write(getDifferencesWithinString());
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            //close resources
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addDifferenceInFile(String treeName, String difference){
        String path = Serializer.getPath();
        if (path == null){
            Serializer serializer = new Serializer();
            path = Serializer.getPath();
        }
        logger.info("For tree " + treeName + " , add line : " + difference);
        String fileName = path + File.separator + "comparatorTrees" + File.separator + treeName + ".bak";
        File file = new File(fileName);
        FileWriter fr = null;
        try {
            fr = new FileWriter(file, true);
            fr.write(difference);
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Could not write into " + fileName);
        }
    }

    public static void replaceDifferenceInFile(TreeComparator treeComparator, boolean delete){
        String treeName = treeComparator.getTreeName();
        String find = treeComparator.getComparisonResultToReplace();
        String difference = treeComparator.getComparisonResultReplacement();
        if (difference == null){
            difference = "";
        }
        if (!delete && difference.equals("")){
            logger.error("Can only remove field");
            return;
        }
        if (difference.equals("")){
            find += System.lineSeparator();
        }
        String resultToPrint = "";
        int lineNumber = 1;

        //read data
        String path = Serializer.getPath();
        if (path == null){
            Serializer serializer = new Serializer();
            path = Serializer.getPath();
        }
        String fileName = path + File.separator + "comparatorTrees" + File.separator + treeName + ".bak";

        try (FileReader reader = new FileReader(fileName);
             BufferedReader br = new BufferedReader(reader)) {

            // read line by line
            String line;
            while ((line = br.readLine()) != null) {
                if (lineNumber > 1){
                    resultToPrint += System.lineSeparator() + line;
                } else {
                    resultToPrint += line;
                }
                lineNumber++;
            }

        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }

        //replace data
        resultToPrint += System.lineSeparator();
        resultToPrint = resultToPrint.replace(find, difference);
        if (resultToPrint != null && resultToPrint.length() > 0){
            resultToPrint = resultToPrint.substring(0, resultToPrint.length()-2);
        }


        //Printing data into file
        File file = new File(fileName);
        FileWriter fr = null;
        try {
            fr = new FileWriter(file, false);
            fr.write(resultToPrint);
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Could not write into " + fileName);
        }
    }

    public HashMap<GeneanetPerson,String> readDifferences(String tree){
        HashMap<GeneanetPerson,String> result = new HashMap<GeneanetPerson,String>();
        String path = Serializer.getPath();
        if (path == null){
            Serializer serializer = new Serializer();
            path = Serializer.getPath();
        }
        try (BufferedReader br = new BufferedReader(new FileReader(path + File.separator + "comparatorTrees" + File.separator + tree + ".bak"))) {
            String line;
            try {
                while ((line = br.readLine()) != null) {
                    String[] tmpLine = line.split(";");
                    if (tmpLine != null && tmpLine.length > 1){
                        GeneanetPerson person = new GeneanetPerson(tmpLine[0]);
                        String txt = "";
                        for (int i = 2 ; i < tmpLine.length ; i++){
                            if (i != 2){
                                txt += ";" + tmpLine[i];
                            } else {
                                txt += tmpLine[i];
                            }
                        }
                        result.put(person, txt);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            logger.info("Difference file not found for " + tree);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void compareNames(GeneanetPerson geneanetPerson, Person gedcomPerson) {
        String firstNameGenea = geneanetPerson.getFirstName();
        String firstNameGed = gedcomPerson.getSurname();
        if (!superEquals(firstNameGenea, firstNameGed)){
            addDifference(geneanetPerson,"firstName=" + firstNameGenea );
        }

        String familyNameGenea = geneanetPerson.getFamilyName();
        String familyNameGed = gedcomPerson.getName();
        if (!superEquals(familyNameGenea, familyNameGed)){
            addDifference(geneanetPerson,"familyName=" + familyNameGenea );
        }
    }

    private void compareSex(GeneanetPerson geneanetPerson, Person gedcomPerson) {
        String sexGenea = geneanetPerson.getGender().toString();
        String sexGed = gedcomPerson.getSex().toString();
        if (!superEquals(sexGenea, sexGed)){
            addDifference(geneanetPerson,"sex=" + sexGenea );
        }
    }

    /** Write the object to a Base64 string. */
    private static String toString( Serializable o ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( o );
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    /** Read the object from Base64 string. */
    private static Object fromString( String s ) throws IOException ,
            ClassNotFoundException {
        byte [] data = Base64.getDecoder().decode( s );
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(  data ) );
        Object o  = ois.readObject();
        ois.close();
        return o;
    }

    public static void saveGeneanetBrowserIntoFile(GeneanetBrowser geneanetBrowser, String url){
        String path = Serializer.getPath();
        if (path == null){
            Serializer serializer = new Serializer();
            path = Serializer.getPath();
        }
        File fichier =  new File(path + File.separator + "geneanetTrees" + File.separator + url + ".ser") ;

        // ouverture d'un flux sur un fichier
        ObjectOutputStream oos = null;

        try {
            oos = new ObjectOutputStream(new FileOutputStream(fichier));
            // sérialization de l'objet
            oos.writeObject(geneanetBrowser) ;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static String saveGeneanetBrowser(GeneanetBrowser geneanetBrowser) throws IOException {
        String string = toString( geneanetBrowser );
        return string;
    }

    public static GeneanetBrowser getGeneanetBrowserFromString(String input) throws IOException, ClassNotFoundException {
        return ( GeneanetBrowser ) fromString( input );
    }

    public static GeneanetBrowser getGeneanetBrowserFromFile(String url) {
        GeneanetBrowser geneanetBrowser = null;
        String path = Serializer.getPath();
        if (path == null){
            Serializer serializer = new Serializer();
            path = Serializer.getPath();
        }
        File fichier =  new File(path + File.separator + "geneanetTrees" + File.separator + url + ".ser") ;

        // ouverture d'un flux sur un fichier
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(fichier));
            // désérialization de l'objet
            geneanetBrowser = (GeneanetBrowser) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return geneanetBrowser;
    }

    public static void loopCompareTree(String testUrl, boolean search, Genealogy genealogyInput, boolean saveComparison, boolean saveGeneanet, boolean displayModeFull, boolean exceptionMode, boolean hideComparisons) throws Exception {
        Genealogy genealogyParameter = genealogyInput;
        TreeComparator treeComparator = compareTree(testUrl, search, genealogyParameter, saveComparison, saveGeneanet, displayModeFull, exceptionMode, hideComparisons);
        boolean error = treeComparator.isErrorComparison();
        while (error){
            logger.info("Add line ? (A to add, R to replace, D to delete, exit to exit, any other to refresh data)");
            Scanner in = new Scanner(System.in);
            String addModification = in.nextLine();
            if (addModification != null){
                switch ( addModification){
                    case "A":
                    case "ADD":
                        logger.info("Modification added");
                        addDifferenceInFile(treeComparator.getTreeName(),treeComparator.printDifferences(false, false, false));
                        break;
                    case "exit":
                        throw new Exception("User exited the program");
                    case "R":
                    case "REPLACE":
                        logger.info("Modification carried out");
                        replaceDifferenceInFile(treeComparator, false);
                        break;
                    case "D":
                    case "DELETE":
                        logger.info("Deletion carried out");
                        replaceDifferenceInFile(treeComparator, true);
                        break;
                        default:
                        logger.info("Rerun needed");
                        setGedcomData();
                        treeComparator.initAlias(true);
                        genealogyParameter = genealogy;
                }
            }
            treeComparator = compareTree(testUrl, search, genealogyParameter, saveComparison, saveGeneanet, displayModeFull, exceptionMode, hideComparisons);
            error = treeComparator.isErrorComparison();
        }
    }

    public static TreeComparator compareTree(String testUrl, boolean search, Genealogy genealogy, boolean saveComparison, boolean saveGeneanet, boolean displayModeFull, boolean exceptionMode, boolean hideComparisons) throws Exception {
        //Geneanet Browser
        String tree = GeneanetBrowser.findTreeName(testUrl);
        GeneanetBrowser geneanetBrowser = null;
        if (!search){
            geneanetBrowser = getGeneanetBrowserFromFile(tree);
        }

        if (search || geneanetBrowser == null){
            GeneanetBrowser geneanetBrowser0 = mainSearchFullTree(testUrl);

            int people = geneanetBrowser0.getNbPeople();
            int expectedPeople = geneanetBrowser0.getPeopleNumberFromGeneanetTrees();
            if (expectedPeople != people){
                if (exceptionMode){
                    logger.error("Test KO for URL " + testUrl + " : expected " + expectedPeople + " but got " + people);
                    throw new Exception("Error : Found new number for search for tree " + tree);
                }
            } else {
                logger.info("Test OK for URL " + tree);
            }
            saveGeneanetBrowserIntoFile(geneanetBrowser0, tree);
            if (saveGeneanet){
                geneanetBrowser0.saveSearchOutput();
            }
            geneanetBrowser = getGeneanetBrowserFromFile(tree);
        }

        GeneanetPerson rootPerson = geneanetBrowser.rootPerson;
        int nbPeopleGen = geneanetBrowser.getNbPeople();

        //Gedcom file
        String personId = geneanetBrowser.getGedcomIdFromGeneanetTrees();
        if (personId.equals("")){
            logger.error("Problem getting rootPerson id for " + tree);
            throw new Exception("Could not find id");
        }
        Person person = genealogy.findPersonById(personId);

        //Comparing
        TreeComparator treeComparator = new TreeComparator(rootPerson,person, geneanetBrowser.allPeopleUrl, tree);
        treeComparator.setLog(false);
        treeComparator.compareRoot();
        int nbPeopleComp = treeComparator.getPeopleSize();
        if (nbPeopleGen == nbPeopleComp){
            //logger.info("Comparison OK for " + testUrl);
        } else {
            logger.info("Comparison KO for " + testUrl + " Expected " + nbPeopleGen + " but got " + nbPeopleComp);
        }
        if (saveComparison){
            treeComparator.saveDifference(tree);
        }
        HashMap<GeneanetPerson, String> geneanetPersonStringHashMap = treeComparator.readDifferences(tree);
        treeComparator.compareDifferences(geneanetPersonStringHashMap, displayModeFull, hideComparisons);
        String comparison = treeComparator.getComparisonResultDisplay();
        boolean logDifferences = true;
        if (comparison != null && !comparison.equals("")){
            treeComparator.printDifferences(displayModeFull, hideComparisons, logDifferences);
            logger.info("Root Geneanet person : " + geneanetBrowser.rootPerson);
            logger.info("Main Gedcom person : " + person);
            logger.info(geneanetPersonStringHashMap.size() + "/" + treeComparator.getDifferences().size() + " differences of " + tree + " tree :");
            logger.error(comparison);
            treeComparator.setErrorComparison(true);
            if (exceptionMode && !hideComparisons) {
                throw new Exception("Error with comparison for tree " + tree);
            }

        } else {
            logger.info("Tree " + tree + " OK");
        }
        return treeComparator;
    }

    public static void setGedcomData() throws IOException {
        MyGedcomReader myGedcomReader = new MyGedcomReader();
        genealogy = myGedcomReader.read(gedcomFile);
        genealogy.parseContents();
        genealogy.sortPersons();
    }

    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();

        //init gedcomfile
        gedcomFile = "C:\\Users\\Dan\\Desktop\\famille1.ged";
        setGedcomData();

        boolean saveComparisonInFile = false;
        GeneanetBrowser urlBrowser = new GeneanetBrowser();
        ArrayList<GeneanetTree> geneanetTrees = urlBrowser.getGeneanetTrees();
        boolean searchOnGeneanet = false;
        boolean saveGeneanetSearch = false;
        boolean displayModeFull = false;
        boolean exceptionMode = true;
        boolean hideComparisons = true;
        int index = 1;
        boolean errorCompareTree = false;
        for (GeneanetTree geneanetTree : geneanetTrees){
            if (index >= 39){
                String url = geneanetTree.getUrl();
                loopCompareTree(url, searchOnGeneanet, genealogy, saveComparisonInFile, saveGeneanetSearch, displayModeFull, exceptionMode, hideComparisons);
            }
            index++;
        }
    }
}
