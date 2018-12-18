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
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static Genealogy.URLConnexion.Geneanet.GeneanetBrowser.logger;
import static Genealogy.URLConnexion.Geneanet.GeneanetBrowser.mainSearchFullTree;
import static Genealogy.URLConnexion.Geneanet.GeneanetPerson.printListofGeneanetPerson;

/**
 * Created by Dan on 02/12/2018.
 */
public class TreeComparator {
    private GeneanetPerson geneanetRoot;
    private Person gedcomRoot;
    private HashMap<GeneanetPerson,String> differences = new HashMap<>();
    public HashMap<String, GeneanetPerson> peopleUrl = new HashMap<String, GeneanetPerson>();
    public ArrayList<String> urlSearched = new ArrayList<String>();
    public ArrayList<String> urlPartnersSearched = new ArrayList<String>();
    public int nbPeople = 0;

    public TreeComparator(GeneanetPerson geneanetPerson, Person gedcomPerson, HashMap<String, GeneanetPerson> peopleUrl) {
        this.geneanetRoot = geneanetPerson;
        this.gedcomRoot = gedcomPerson;
        this.peopleUrl = peopleUrl;
    }

    public GeneanetPerson getGeneanetRoot() {
        return geneanetRoot;
    }

    public void setGeneanetRoot(GeneanetPerson geneanetRoot) {
        this.geneanetRoot = geneanetRoot;
    }

    public Person getGedcomRoot() {
        return gedcomRoot;
    }

    public void setGedcomRoot(Person gedcomRoot) {
        this.gedcomRoot = gedcomRoot;
    }

    public void printDifferences(){
        for(Map.Entry<GeneanetPerson, String> entry : differences.entrySet()) {
            System.out.println("\"" + entry.getKey().getUrl() + "\";" + entry.getKey().getFullName() + ";" + entry.getValue());
        }
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
        logger.info("Compared " + gedcomRoot.getFullName() + " (" + geneanetRoot.getUrl() + ")");
        nbPeople++;
        compareNames(geneanetRoot, gedcomRoot);
        compareBirth(geneanetRoot, gedcomRoot);
        compareDeath(geneanetRoot, gedcomRoot);
        compareMarriage(geneanetRoot, gedcomRoot);
        compareSiblings(geneanetRoot, gedcomRoot);
        compareHalfSiblings(geneanetRoot, gedcomRoot);
        compareChildren(geneanetRoot, gedcomRoot);

        urlSearched.add(geneanetRoot.getUrl());
        searchFamily(geneanetRoot, gedcomRoot, true);
    }

    private void comparePerson(GeneanetPerson geneanetPerson, Person gedcomPerson) throws Exception {
        logger.info("Compared " + gedcomPerson.getFullName() + "(" + geneanetPerson.getUrl() + ")");
        if (!urlPartnersSearched.contains(geneanetPerson.getUrl())){
            nbPeople++;
        }
        compareNames(geneanetPerson, gedcomPerson);
        compareBirth(geneanetPerson, gedcomPerson);
        compareDeath(geneanetPerson, gedcomPerson);
        compareMarriage(geneanetPerson, gedcomPerson);
        compareSiblings(geneanetPerson, gedcomPerson);
        compareHalfSiblings(geneanetPerson, gedcomPerson);

        urlSearched.add(geneanetPerson.getUrl());
        searchFamily(geneanetPerson, gedcomPerson, false);
    }

    private void partialCompare(GeneanetPerson geneanetPerson, Person gedcomPerson, boolean root) throws Exception {
        logger.info("Compared partial " + gedcomPerson.getFullName() + "(" + geneanetPerson.getUrl() + ")");
        if (!urlSearched.contains(geneanetPerson.getUrl())){
            nbPeople++;
        }
        urlPartnersSearched.add(geneanetPerson.getUrl());
        compareNames(geneanetPerson, gedcomPerson);
        compareBirth(geneanetPerson, gedcomPerson);
        compareDeath(geneanetPerson, gedcomPerson);
        compareMarriage(geneanetPerson, gedcomPerson);
        compareSiblings(geneanetPerson, gedcomPerson);
        compareHalfSiblings(geneanetPerson, gedcomPerson);
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
            genPersonToSearch = peopleUrl.get((genPersonToSearch.getUrl()));
            for (Union union : gedcomPerson.getUnions()){
                Person partner = union.getPartner();
                if (superEquals(genPersonToSearch.getFullName(),partner.getFullName())){
                    superComparePartner(genPersonToSearch,partner,"partner", root);
                }
                Person citizen = union.getCitizen();
                if (superEquals(genPersonToSearch.getFullName(),citizen.getFullName())){
                    superComparePartner(genPersonToSearch,citizen,"partner", root);
                }
            }
        }
    }

    private void searchHalfSiblings(GeneanetPerson geneanetPerson, Person gedcomPerson) throws Exception {
        if (geneanetPerson != null){
            ArrayList<GeneanetPerson> siblingsGen = geneanetPerson.getHalfSiblings();
            ArrayList<Person> siblingsGed = gedcomPerson.getHalfSiblings();
            for (GeneanetPerson siblingGen : siblingsGen){
                for (Person siblingGed : siblingsGed){
                    if (superEquals(siblingGen.getFullName(),siblingGed.getFullName())){
                        comparePerson(siblingGen,siblingGed);
                    }
                }
            }
        }
    }

    private void searchSiblings(GeneanetPerson geneanetPerson, Person gedcomPerson) throws Exception {
        if (geneanetPerson != null){
            ArrayList<GeneanetPerson> siblingsGen = geneanetPerson.getSiblings();
            ArrayList<Person> siblingsGed = gedcomPerson.getSiblings();
            for (GeneanetPerson siblingGen : siblingsGen){
                for (Person siblingGed : siblingsGed){
                    if (superEquals(siblingGen.getFullName(),siblingGed.getFullName())){
                        comparePerson(siblingGen,siblingGed);
                    }
                }
            }
        }
    }

    private void superCompareAndSearchPerson(GeneanetPerson geneanetPerson, Person gedcomPerson, String txt) throws Exception {
        if (geneanetPerson != null && gedcomPerson != null){
            if (!urlSearched.contains(geneanetPerson.getUrl())){
                comparePerson(geneanetPerson, gedcomPerson);
                searchFamily(geneanetPerson, gedcomPerson, false);
            }
        } else if (geneanetPerson != null){
            addDifference(geneanetPerson,"Missing " + txt + " for " + geneanetPerson.getUrl() );
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
        if (!superEquals(children1Ged, childrenGen) && !childrenGen.isEmpty()){
            addDifference(geneanetPerson,"Children=" + childrenGen );
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
            GeneanetPerson fullGeneanetPerson = peopleUrl.get(person.getUrl());
            if (fullGeneanetPerson == null){
                throw new Exception("Person " + person.getUrl() + " not found");
            }
            HashMap<MyDate, String> value = entry.getValue();
            for(Map.Entry<MyDate, String> entry2 : value.entrySet()) {
                MyDate date = entry2.getKey();
                String town = entry2.getValue();
                if (!supercontains(marriageGed,fullGeneanetPerson,date,town) && (date != null || town != null)){
                    addDifference(fullGeneanetPerson,"Marriage=" + person.getFullName() + ";" + date + ";" + town );
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
            if (superEquals(partner.getFullName(),person.getFullName())&&superEquals(date,date1)&&superEquals(town,town1.getName())){
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
            String newString1 = StringUtils.stripAccents(string1).toLowerCase();
            String newString2 = StringUtils.stripAccents(string2).toLowerCase();
            if (!newString1.equals(newString2)){
                //Synonyms
                newString1 = newString1.replace("boiau","boyau").replaceAll("\\d","").replace(" ","").replace("-","").replace("chatilloncoligny","chatillonsurloing").replace("loiret","");
                newString2 = newString2.replace("boiau","boyau").replaceAll("\\d","").replace(" ","").replace("-","").replace("chatilloncoligny","chatillonsurloing").replace("loiret","");
                return newString1.equals(newString2);
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    private String compareDifferences(HashMap<GeneanetPerson,String> differences2){
        String result = "";
        for(Map.Entry<GeneanetPerson, String> entry : differences.entrySet()) {
            GeneanetPerson person = entry.getKey();
            String valueTxt = entry.getValue();
            if (differences2.containsKey(person) && !valueTxt.equals(differences2.get(person))){
                result += valueTxt;
            }
        }

        for(Map.Entry<GeneanetPerson, String> entry : differences2.entrySet()) {
            GeneanetPerson person = entry.getKey();
            String valueTxt = entry.getValue();
            if (differences.containsKey(person) && !valueTxt.equals(differences.get(person))){
                result += valueTxt;
            }
        }
        return result;
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
        File file = new File(path + File.separator + "comparatorTrees" + File.separator + tree + ".bak");
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
            e.printStackTrace();
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

    public static String saveGeneanetBrowser(GeneanetBrowser geneanetBrowser) throws IOException {
        String string = toString( geneanetBrowser );
        return string;
    }

    public static GeneanetBrowser getGeneanetBrowserFromString(String input) throws IOException, ClassNotFoundException {
        return ( GeneanetBrowser ) fromString( input );
    }

    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();

        //Geneanet Browser
        String testUrl = "https://gw.geneanet.org/roalda?lang=fr&pz=ronald+guy&nz=arnaux&p=marie+anne&n=bardin";
        //GeneanetBrowser geneanetBrowser0 = mainSearchFullTree(testUrl, false);

        //String result = saveGeneanetBrowser(geneanetBrowser0);
        String result = "rO0ABXNyAC9HZW5lYWxvZ3kuVVJMQ29ubmV4aW9uLkdlbmVhbmV0LkdlbmVhbmV0QnJvd3NlcrQ0UQ85E/wHAgAJSQAQZXhwZWN0ZWROYlBlb3BsZUkACG5iUGVvcGxlTAAMYWxsUGVvcGxlVXJsdAATTGphdmEvdXRpbC9IYXNoTWFwO0wABmNvb2tpZXQAD0xqYXZhL3V0aWwvTWFwO0wADWdlbmVhbmV0VHJlZXN0ABVMamF2YS91dGlsL0FycmF5TGlzdDtMAAlwZW9wbGVVcmxxAH4AA0wACnJvb3RQZXJzb250ADBMR2VuZWFsb2d5L1VSTENvbm5leGlvbi9HZW5lYW5ldC9HZW5lYW5ldFBlcnNvbjtMAAxzZWFyY2hPdXRwdXR0ABJMamF2YS9sYW5nL1N0cmluZztMAAN1cmxxAH4ABXhwAAAAAAAAAAhzcgARamF2YS51dGlsLkhhc2hNYXAFB9rBwxZg0QMAAkYACmxvYWRGYWN0b3JJAAl0aHJlc2hvbGR4cD9AAAAAAAAMdwgAAAAQAAAACHQAVWh0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL3JvYWxkYT9sYW5nPWZyJnB6PXJvbmFsZCtndXkmbno9YXJuYXV4JnA9amVhbitqYWNxdWVzJm49Ym95YXVzcgAuR2VuZWFsb2d5LlVSTENvbm5leGlvbi5HZW5lYW5ldC5HZW5lYW5ldFBlcnNvbpfOj0hpp1zBAgAWWgAQaXNVc2luZ0RhdGVUYWJsZVoACnJvb3RwZXJzb25aAAhzZWFyY2hlZEwACWJpcnRoRGF0ZXQAHUxHZW5lYWxvZ3kvTW9kZWwvRGF0ZS9NeURhdGU7TAAKYnVyaWFsRGF0ZXEAfgALTAAIY2hpbGRyZW5xAH4AA0wAD2NocmlzdGVuaW5nRGF0ZXEAfgALTAAJZGVhdGhEYXRlcQB+AAtMAApmYW1pbHlOYW1lcQB+AAVMAAZmYXRoZXJxAH4ABEwACWZpcnN0TmFtZXEAfgAFTAAGZ2VuZGVydAAoTEdlbmVhbG9neS9VUkxDb25uZXhpb24vR2VuZWFuZXQvR2VuZGVyO0wAC2dlbmVhbmV0VXJscQB+AAVMAAxoYWxmU2libGluZ3NxAH4AA0wACG1hcnJpYWdlcQB+AAFMAAZtb3RoZXJxAH4ABEwADHBsYWNlT2ZCaXJ0aHEAfgAFTAANcGxhY2VPZkJ1cmlhbHEAfgAFTAAScGxhY2VPZkNocmlzdGVuaW5ncQB+AAVMAAxwbGFjZU9mRGVhdGhxAH4ABUwACHNpYmxpbmdzcQB+AANMAAN1cmxxAH4ABXhwAAABc3IAHUdlbmVhbG9neS5Nb2RlbC5EYXRlLkZ1bGxEYXRlV6VIZLh2g/oCAAFMAARkYXRldAAQTGphdmEvdXRpbC9EYXRlO3hyABtHZW5lYWxvZ3kuTW9kZWwuRGF0ZS5NeURhdGWb8qK/1a5BjQIAAHhwc3IADmphdmEudXRpbC5EYXRlaGqBAUtZdBkDAAB4cHcI///7bLEgGYB4cHNyABNqYXZhLnV0aWwuQXJyYXlMaXN0eIHSHZnHYZ0DAAFJAARzaXpleHAAAAAAdwQAAAAAeHBwdAAFQk9ZQVVzcQB+AAoAAAFwcHNxAH4AFAAAAAF3BAAAAAFzcQB+AAoAAABwcHNxAH4AFAAAAAB3BAAAAAB4cHBwcHBwcHNxAH4AFAAAAAB3BAAAAAB4c3EAfgAHP0AAAAAAAAB3CAAAABAAAAAAeHBwcHBwc3EAfgAUAAAAAHcEAAAAAHh0AFVodHRwczovL2d3LmdlbmVhbmV0Lm9yZy9yb2FsZGE/bGFuZz1mciZwej1yb25hbGQrZ3V5Jm56PWFybmF1eCZwPWplYW4ramFjcXVlcyZuPWJveWF1eHBwdAAFQk9ZQVVwdAAGTWFydGlufnIAJkdlbmVhbG9neS5VUkxDb25uZXhpb24uR2VuZWFuZXQuR2VuZGVyAAAAAAAAAAASAAB4cgAOamF2YS5sYW5nLkVudW0AAAAAAAAAABIAAHhwdAAETWFsZXBzcQB+ABQAAAAAdwQAAAAAeHNxAH4ABz9AAAAAAAAMdwgAAAAQAAAAAXNxAH4ACgAAAHBwc3EAfgAUAAAAAHcEAAAAAHhwcHBwcHBwc3EAfgAUAAAAAHcEAAAAAHhzcQB+AAc/QAAAAAAAAHcIAAAAEAAAAAB4cHBwcHBzcQB+ABQAAAAAdwQAAAAAeHQAUmh0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL3JvYWxkYT9sYW5nPWZyJnB6PXJvbmFsZCtndXkmbno9YXJuYXV4JnA9Y2F0aGVyaW5lJm49YmVycnlzcQB+AAc/QAAAAAAA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EAAAAAN0AAhnbnRzZXNzNXQAGm1pdjExc2szY2Zxb2dkaDRtM2k3ZDhzYTg3dAASVmlzaXRvckxpbWl0Q29va2lldAAHZGVsZXRlZHQAClJFTUVNQkVSTUV0ALBSMlZ1WldGdVpYUmNRblZ1Wkd4bFhGVnpaWEpDZFc1a2JHVmNSVzUwYVhSNVhGVnpaWEk2WkVkNGFHTnRPVEZqTTA1c09qRTFORGszTXprNU16ZzZOamcwTXpCa1l6aGtORFE0TnpBd01UWXpNbUU0WkRSaFpUVXdNREpsWkdJNU9XSTNOak16T0dKall6SmhOMll6WVRkaFltVXpOak0xWXpOaFpHWmpNZyUzRCUzRHgAc3EAfgAUAAAAKncEAAAAKnNyACxHZW5lYWxvZ3kuVVJMQ29ubmV4aW9uLkdlbmVhbmV0LkdlbmVhbmV0VHJlZb68M+R22VbaAgAESQAIZ2VkY29tSWRJAAxwZW9wbGVOdW1iZXJMAARuYW1lcQB+AAVMAAN1cmxxAH4ABXhw/////wAAAAd0AAZyb2FsZGF0AFpodHRwczovL2d3LmdlbmVhbmV0Lm9yZy9yb2FsZGE/bGFuZz1mciZwej1yb25hbGQrZ3V5Jm56PWFybmF1eCZvY3o9MCZwPW1hcmllK2FubmUmbj1iYXJkaW5zcQB+AVD/////AAAALnQAC2ZhbWlseXNveWVydABIaHR0cHM6Ly9ndy5nZW5lYW5ldC5vcmcvZmFtaWx5c295ZXI/bGFuZz1mciZpej0wJnA9bG91aXMrbGVvbm9yZSZuPWJhZGluc3EAfgFQ/////wAAADd0AARnbHVjdABpaHR0cHM6Ly9ndy5nZW5lYW5ldC5vcmcvZ2x1Yz9sYW5nPWZyJnB6PWlyaXMrYnJpZ2l0dGUrZG9taW5pcXVlJm56PXBpZ25hdWQmb2N6PTAmcD1sb3VpcytlbGVvbm9yZSZuPWJhZGluc3EAfgFQ/////wAAAEZ0AAhzeWx2aWViNHQAemh0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL3N5bHZpZWI0P2xhbmc9ZnImcHo9c3lsdmllK2phY3F1ZWxpbmUrbWFyaWUrYmVybmFkZXR0ZSZuej1iZXJnZXJlYXUmb2N6PTAmcD1qZWFuK2phY3F1ZXMmbj1ib2lsZWF1c3EAfgFQ/////wAAAAt0AA5qZWFubmluZWxlZ2VyMXQAR2h0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL2plYW5uaW5lbGVnZXIxP2xhbmc9ZnImcD1tYXJpZSZuPWJvdWNoZXJvbiZvYz0yc3EAfgFQ/////wAAAAN0AAhqbHJlbmF1ZHQAWmh0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL2pscmVuYXVkP2xhbmc9ZnImcHo9amVhbitsb3VpcyZuej1yZW5hdWQmb2N6PTAmcD1hbnRvaW5lJm49YmF1bGdldHNxAH4BUP////8AAAAKdAANbWljaGVsbm9ybWFuZHQAQmh0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL21pY2hlbG5vcm1hbmQ/bGFuZz1mciZwPWNhdGhlcmluZSZuPWNvbGxldHNxAH4BUP////8AAAAGdAAHaGFtb24yMnQAY2h0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL2hhbW9uMjI/bGFuZz1mciZwej1yb21haW4rYWltZSZuej1hYmJydXp6ZXNlJm9jej0wJnA9aW5jb25udSZuPWNvbGxldCZvYz0xNnNxAH4BUP////8AAAAEdAAIZHV2YWNoYXR0AEJodHRwczovL2d3LmdlbmVhbmV0Lm9yZy9kdXZhY2hhdD9sYW5nPWZyJnA9Z3VpbGxhdW1lJm49Y29sbGV0Jm9jPTFzcQB+AVD/////AAAABXQABnBoaWw2OHQAO2h0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL3BoaWw2OD9sYW5nPWZyJnA9Z3VpbGxhdW1lJm49Y29sbGV0c3EAfgFQ/////wAAAG10AAlqdmlsbGV0dGV0AFVodHRwczovL2d3LmdlbmVhbmV0Lm9yZy9qdmlsbGV0dGU/bGFuZz1mciZwej1qdWxpZW4mbno9ZHVyYW5kJm9jej0wJnA9cGllcnJlJm49Y29sbGV0c3EAfgFQ/////wAAAB10AAhtYW5nZXJldHQAZWh0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL21hbmdlcmV0P2xhbmc9ZnImcHo9cm9tYWluK21pY2hlbCtmcmFuY29pcyZuej1tYW5nZXJldCZvY3o9MCZwPWFuZHJlJm49Y291cnR5c3EAfgFQ/////wAAAB50AAdwaWVycmVmdABVaHR0cHM6Ly9ndy5nZW5lYW5ldC5vcmcvcGllcnJlZj9sYW5nPWZyJnB6PWFubmllJm56PWZlcm1pZXImb2N6PTAmcD1tYXJpZSZuPWRlbGF2aWxsZXNxAH4BUP////8AAAAtdAAKZ2F1ZGlsbGF0cHQAbWh0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL2dhdWRpbGxhdHA/bGFuZz1mciZwej1hcm5hdWQrbWFyYytwYXRyaWNrJm56PWdhdWRpbGxhdCZvY3o9MCZwPW1hcmllJm49ZGVsYXZpbGxlJm9jPTJzcQB+AVD/////AAAAA3QAB21nYW5pZXJ0AGJodHRwczovL2d3LmdlbmVhbmV0Lm9yZy9tZ2FuaWVyP2xhbmc9ZnImcHo9bG91aXMrYXVndXN0ZSZuej1nYW5pZXImb2N6PTAmcD1lbW1hbnVlbCthbGJlcnQmbj1kcmlldXNxAH4BUP////8AAAADdAAKamVhbmhpbmFyZHQAQmh0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL2plYW5oaW5hcmQ/bj1mcmVtaW5lJm9jPSZwPW1hcmllK21hZGVsZWluZXNxAH4BUP////8AAAAMdAAObXVycGh5YXV6ZXJhaXN0AEZodHRwczovL2d3LmdlbmVhbmV0Lm9yZy9tdXJwaHlhdXplcmFpcz9uPWZyZW1pbmUmb2M9JnA9bWFyaWUrbWFkZWxlaW5lc3EAfgFQ/////wAAAA90AAVjcmFsdXQAPWh0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL2NyYWx1P249ZnJlbWluZSZvYz0mcD1tYXJpZSttYWRlbGFpbmVzcQB+AVD/////AAAABHQABXNvYm91dAA2aHR0cHM6Ly9ndy5nZW5lYW5ldC5vcmcvc29ib3U/bj1oaW5hcmQmb2M9JnA9Y2F0aGVyaW5lc3EAfgFQ/////wAAAA50AA9vbGl2aWVyY2FpbGxlYXV0AEBodHRwczovL2d3LmdlbmVhbmV0Lm9yZy9vbGl2aWVyY2FpbGxlYXU/bj1oaW5hcmQmb2M9JnA9Y2F0aGVyaW5lc3EAfgFQ/////wAAAAN0AA5waWVycmVwZXRyZXF1aXQAP2h0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL3BpZXJyZXBldHJlcXVpP249aGluYXJkJm9jPSZwPWNhdGhlcmluZXNxAH4BUP////8AAAAHdAAJZnJhbmNvaXN0dAA6aHR0cHM6Ly9ndy5nZW5lYW5ldC5vcmcvZnJhbmNvaXN0P249aGluYXJkJm9jPSZwPWNhdGhlcmluZXNxAH4BUP////8AAAAJdAAFYmV0YTF0AHFodHRwczovL2d3LmdlbmVhbmV0Lm9yZy9iZXRhMT9sYW5nPWZyJnB6PWJlcm5hcmQrZnJhbmNpcyZuej1hbHF1aWVyJm9jej0wJnA9bWFyaWUrbWFyZ3Vlcml0ZStlbGlzYWJldGgmbj1sYW5nbG9pc3NxAH4BUP////8AAABLdAAFcm9nbzF0ADxodHRwczovL2d3LmdlbmVhbmV0Lm9yZy9yb2dvMT9uPWxlYmFyZ3kmb2M9JnA9bWFyaWUrY2V6YXJpbmVzcQB+AVD/////AAAAD3QAC2JhcnRocmljaGVydABraHR0cHM6Ly9ndy5nZW5lYW5ldC5vcmcvYmFydGhyaWNoZXI/bGFuZz1mciZwej1jYXRoZXJpbmUrZmFubnkmbno9amV1dnJleSZvY3o9MCZwPWd1aWxsYXVtZSZuPWxlZmVidnJlJm9jPTFzcQB+AVD/////AAAARnQADGRlbmlzY2h1cXVldHQAdGh0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL2RlbmlzY2h1cXVldD9sYW5nPWZyJnB6PWplYW4rbWljaGVsK2Jlcm5hcmQmbno9Y2h1cXVldCZvY3o9MCZwPWphY3F1ZXMrcGllcnJlK2pvc2VwaCZuPWxlcm95c3EAfgFQ/////wAAAEh0AAttamRlc29ldXZyZXQAO2h0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL21qZGVzb2V1dnJlP249bWFiaWxhdCZvYz0mcD10aGVyZXNlc3EAfgFQ/////wAAAB10AAV0cnVjeXQAPWh0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL3RydWN5P2xhbmc9ZnImcD1qZWFubmUmbj1tb2NxdW90Jm9jPTFzcQB+AVD/////AAAAJXQACWdlcnV3ZW5lMXQAPmh0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL2dlcnV3ZW5lMT9sYW5nPWZyJnA9amFjcXVlcyZuPXJhYmlsbG9uc3EAfgFQ/////wAAAC50AAdqYWNxYnJpdABWaHR0cHNzOi8vZ3cuZ2VuZWFuZXQub3JnL2phY3Ficmk/bGFuZz1mciZwej1taWNoZWwmbno9YmFyYmlldXgmb2N6PTAmcD1tYXJpZTImbj1zYXZhcnlzcQB+AVD/////AAAAWXQABWFtYzUwdAA5aHR0cHM6Ly9ndy5nZW5lYW5ldC5vcmcvYW1jNTA/bj1zaW1vbiZvYz0mcD1jZWxlc3RlK2p1bGllc3EAfgFQ/////wAAACF0AAdtaWNoYTEydAA7aHR0cHM6Ly9ndy5nZW5lYW5ldC5vcmcvbWljaGExMj9uPXNpbW9uJm9jPSZwPWNlbGVzdGUranVsaWVzcQB+AVD/////AAAABXQADmZhYnJpY2VsZW5vYmxldABJaHR0cHM6Ly9ndy5nZW5lYW5ldC5vcmcvZmFicmljZWxlbm9ibGU/bGFuZz1mciZwPXBpZXJyZStsb3VpcyZuPXRoaWViYXVsdHNxAH4BUP////8AAAAWdAAEZnVmdXQAPWh0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL2Z1ZnU/bj1yaWdsZXQmb2M9JnA9YXVndXN0aW5lK3Jvc2FsaWVzcQB+AVD/////AAAAB3QACW1hbXluYW5uZXQAW2h0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL21hbXluYW5uZT9sYW5nPWZyJnB6PWV0aWVubmUmbno9ZGVuaXMmb2N6PTAmcD1qZWFuK2JhcHRpc3RlJm49bWFyaWVzcQB+AVD/////AAAACHQACm1kb252aWxsZTF0AEVodHRwczovL2d3LmdlbmVhbmV0Lm9yZy9tZG9udmlsbGUxP2xhbmc9ZnImaXo9MzA0NiZwPW1hcmllJm49ZG9udmlsbGVzcQB+AVD/////AAAASnQACWh1YmVydHA4M3QAUWh0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL2h1YmVydHA4Mz9sYW5nPWZyJnA9bWFyaWUrbWFyZ3Vlcml0ZStlbGlzYWJldGgmbj1sYW5nbG9pc3NxAH4BUP////8AAAAWdAAJc2xlYnJ1bWFudABKaHR0cHM6Ly9ndy5nZW5lYW5ldC5vcmcvc2xlYnJ1bWFuP2xhbmc9ZnImcD1sb3Vpc2UrdmVyb25pcXVlK21hcmllJm49bGVyb3lzcQB+AVD/////AAACQ3QAC21zZWJhc3RpZW4xdABhaHR0cHM6Ly9ndy5nZW5lYW5ldC5vcmcvbXNlYmFzdGllbjE/bGFuZz1mciZwej1zZWJhc3RpZW4rZGF2aWQmbno9bWVyY2llciZvY3o9MCZwPWxvdWlzJm49dGhpZXJyeXNxAH4BUP////8AAAIrdAAIbGlzZXRveGV0AEVodHRwczovL2d3LmdlbmVhbmV0Lm9yZy9saXNldG94ZT9sYW5nPWZyJnA9bG91aXNlK2pvc2VwaGluZSZuPXRoaWVycnlzcQB+AVD/////AAACt3QAA2RpbHQAP2h0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL2RpbD9sYW5nPWZyJml6PTAmcD1lcm5lc3QrbGVvbiZuPWJlcnRvbnNxAH4BUP////8AAARHdAAKZ2VuZWE1MGNvbXQATGh0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL2dlbmVhNTBjb20/bGFuZz1mciZwPW1hcmllK21hZGVsZWluZSZuPWRvdXZpbGxlJm9jPTV4c3EAfgAUAAAABncEAAAABnEAfgDLcQB+AOpxAH4BD3EAfgAJcQB+AC9xAH4AVnhxAH4AzHQWsUdlbmVhbmV0UGVyc29ue3VybD0naHR0cHM6Ly9ndy5nZW5lYW5ldC5vcmcvcm9hbGRhP2xhbmc9ZnImcHo9cm9uYWxkK2d1eSZuej1hcm5hdXgmcD1tYXJpZSthbm5lJm49YmFyZGluJywgZmlyc3ROYW1lPSdNYXJpZSBBbm5lJywgZmFtaWx5TmFtZT0nQkFSRElOJywgZ2VuZGVyPSdGZW1hbGUnLCBiaXJ0aERhdGU9JzE4MTYnLCBmYXRoZXI9J2h0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL3JvYWxkYT9sYW5nPWZyJnB6PXJvbmFsZCtndXkmbno9YXJuYXV4JnA9amFjcXVlcyZuPWJhcmRpbicsIG1vdGhlcj0naHR0cHM6Ly9ndy5nZW5lYW5ldC5vcmcvcm9hbGRhP2xhbmc9ZnImcHo9cm9uYWxkK2d1eSZuej1hcm5hdXgmcD1hbm5lJm49Y29ubmluJywgc2libGluZ3M9J2h0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL3JvYWxkYT9sYW5nPWZyJnB6PXJvbmFsZCtndXkmbno9YXJuYXV4JnA9cGllcnJlK2phY3F1ZXMmbj1iYXJkaW4nLCBtYXJyaWFnZT0naHR0cHM6Ly9ndy5nZW5lYW5ldC5vcmcvcm9hbGRhP2xhbmc9ZnImcHo9cm9uYWxkK2d1eSZuej1hcm5hdXgmcD1qZWFuK2phY3F1ZXMmbj1ib3lhdTt7MDUvMTIvMTgzNz1DaGF0aWxsb24gQ29saWdueSA0NX0nLCBzZWFyY2hlZD0ndHJ1ZScsIHJvb3RwZXJzb249J3RydWUnDQpHZW5lYW5ldFBlcnNvbnt1cmw9J2h0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL3JvYWxkYT9sYW5nPWZyJnB6PXJvbmFsZCtndXkmbno9YXJuYXV4JnA9amFjcXVlcyZuPWJhcmRpbicsIGZpcnN0TmFtZT0nSmFjcXVlcycsIGZhbWlseU5hbWU9J0JBUkRJTicsIGdlbmRlcj0nTWFsZScsIGNoaWxkcmVuPSdodHRwczovL2d3LmdlbmVhbmV0Lm9yZy9yb2FsZGE/bGFuZz1mciZwej1yb25hbGQrZ3V5Jm56PWFybmF1eCZwPXBpZXJyZStqYWNxdWVzJm49YmFyZGluO2h0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL3JvYWxkYT9sYW5nPWZyJnB6PXJvbmFsZCtndXkmbno9YXJuYXV4JnA9bWFyaWUrYW5uZSZuPWJhcmRpbicsIG1hcnJpYWdlPSdodHRwczovL2d3LmdlbmVhbmV0Lm9yZy9yb2FsZGE/bGFuZz1mciZwej1yb25hbGQrZ3V5Jm56PWFybmF1eCZwPWFubmUmbj1jb25uaW47e251bGw9bnVsbH0nLCBzZWFyY2hlZD0ndHJ1ZScNCkdlbmVhbmV0UGVyc29ue3VybD0naHR0cHM6Ly9ndy5nZW5lYW5ldC5vcmcvcm9hbGRhP2xhbmc9ZnImcHo9cm9uYWxkK2d1eSZuej1hcm5hdXgmcD1hbm5lJm49Y29ubmluJywgZmlyc3ROYW1lPSdBbm5lJywgZmFtaWx5TmFtZT0nQ09OTklOJywgZ2VuZGVyPSdGZW1hbGUnLCBkZWF0aERhdGU9JzI0LzEyLzE4MTcnLCBwbGFjZU9mRGVhdGg9J0NoYXRpbGxvbiBDb2xpZ255IDQ1JywgY2hpbGRyZW49J2h0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL3JvYWxkYT9sYW5nPWZyJnB6PXJvbmFsZCtndXkmbno9YXJuYXV4JnA9cGllcnJlK2phY3F1ZXMmbj1iYXJkaW47aHR0cHM6Ly9ndy5nZW5lYW5ldC5vcmcvcm9hbGRhP2xhbmc9ZnImcHo9cm9uYWxkK2d1eSZuej1hcm5hdXgmcD1tYXJpZSthbm5lJm49YmFyZGluJywgbWFycmlhZ2U9J2h0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL3JvYWxkYT9sYW5nPWZyJnB6PXJvbmFsZCtndXkmbno9YXJuYXV4JnA9amFjcXVlcyZuPWJhcmRpbjt7bnVsbD1udWxsfScsIHNlYXJjaGVkPSd0cnVlJw0KR2VuZWFuZXRQZXJzb257dXJsPSdodHRwczovL2d3LmdlbmVhbmV0Lm9yZy9yb2FsZGE/bGFuZz1mciZwej1yb25hbGQrZ3V5Jm56PWFybmF1eCZwPXBpZXJyZStqYWNxdWVzJm49YmFyZGluJywgZmlyc3ROYW1lPSdQaWVycmUgSmFjcXVlcycsIGZhbWlseU5hbWU9J0JBUkRJTicsIGdlbmRlcj0nTWFsZScsIGJpcnRoRGF0ZT0nMTgvMDkvMTgwOCcsIHBsYWNlT2ZCaXJ0aD0nQ2jDonRpbGxvbi1Db2xpZ255JywgZGVhdGhEYXRlPScyOS8wMy8xODcyJywgcGxhY2VPZkRlYXRoPSdDaMOidGlsbG9uLUNvbGlnbnknLCBmYXRoZXI9J2h0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL3JvYWxkYT9sYW5nPWZyJnB6PXJvbmFsZCtndXkmbno9YXJuYXV4JnA9amFjcXVlcyZuPWJhcmRpbicsIG1vdGhlcj0naHR0cHM6Ly9ndy5nZW5lYW5ldC5vcmcvcm9hbGRhP2xhbmc9ZnImcHo9cm9uYWxkK2d1eSZuej1hcm5hdXgmcD1hbm5lJm49Y29ubmluJywgc2libGluZ3M9J2h0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL3JvYWxkYT9sYW5nPWZyJnB6PXJvbmFsZCtndXkmbno9YXJuYXV4JnA9bWFyaWUrYW5uZSZuPWJhcmRpbicsIGNoaWxkcmVuPSdodHRwczovL2d3LmdlbmVhbmV0Lm9yZy9yb2FsZGE/bGFuZz1mciZwej1yb25hbGQrZ3V5Jm56PWFybmF1eCZwPXBpZXJyZSZuPWJhcmRpbjtodHRwczovL2d3LmdlbmVhbmV0Lm9yZy9yb2FsZGE/bGFuZz1mciZwej1yb25hbGQrZ3V5Jm56PWFybmF1eCZwPWZyYW5jb2lzJm49YmFyZGluO2h0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL3JvYWxkYT9sYW5nPWZyJnB6PXJvbmFsZCtndXkmbno9YXJuYXV4JnA9YW1lZGUmbj1iYXJkaW4nLCBtYXJyaWFnZT0naHR0cHM6Ly9ndy5nZW5lYW5ldC5vcmcvcm9hbGRhP2xhbmc9ZnImcHo9cm9uYWxkK2d1eSZuej1hcm5hdXgmcD12ZXJvbmlxdWUmbj1mYW5pY2hldDt7MzAvMDQvMTgzOT1DaMOidGlsbG9uLUNvbGlnbnl9Jywgc2VhcmNoZWQ9J3RydWUnDQpHZW5lYW5ldFBlcnNvbnt1cmw9J2h0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL3JvYWxkYT9sYW5nPWZyJnB6PXJvbmFsZCtndXkmbno9YXJuYXV4JnA9dmVyb25pcXVlJm49ZmFuaWNoZXQnLCBmaXJzdE5hbWU9J1bDqXJvbmlxdWUnLCBmYW1pbHlOYW1lPSdGQU5JQ0hFVCcsIGdlbmRlcj0nRmVtYWxlJywgYmlydGhEYXRlPScwNy8wMy8xODEyJywgcGxhY2VPZkJpcnRoPSdTYWludGUgR2VuZXZpw6h2ZSBkZXMgQm9pcyA0NTI3OCBMb2lyZXQgJywgZGVhdGhEYXRlPScxNC8wNy8xODY2JywgcGxhY2VPZkRlYXRoPSdDaMOidGlsbG9uLUNvbGlnbnknLCBmYXRoZXI9J2h0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL3JvYWxkYT9sYW5nPWZyJnB6PXJvbmFsZCtndXkmbno9YXJuYXV4JnA9cGllcnJlJm49ZmFuaWNoZXIrZmFuaWNoZXQnLCBtb3RoZXI9J2h0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL3JvYWxkYT9sYW5nPWZyJnB6PXJvbmFsZCtndXkmbno9YXJuYXV4JnA9amVhbm5lJm49cGluc29uJywgc2libGluZ3M9J2h0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL3JvYWxkYT9sYW5nPWZyJnB6PXJvbmFsZCtndXkmbno9YXJuYXV4JnA9amVhbm5lK2NhdGhlcmluZSZuPWZhbmljaGVyO2h0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL3JvYWxkYT9sYW5nPWZyJnB6PXJvbmFsZCtndXkmbno9YXJuYXV4JnA9cGllcnJlK2VkbWUmbj1mYW5pY2hlcjtodHRwczovL2d3LmdlbmVhbmV0Lm9yZy9yb2FsZGE/bGFuZz1mciZwej1yb25hbGQrZ3V5Jm56PWFybmF1eCZwPWxvdWlzJm49ZmFuaWNoZXI7aHR0cHM6Ly9ndy5nZW5lYW5ldC5vcmcvcm9hbGRhP2xhbmc9ZnImcHo9cm9uYWxkK2d1eSZuej1hcm5hdXgmcD1mcmFuY29pcyZuPWZhbmljaGVyJm9jPTM7aHR0cHM6Ly9ndy5nZW5lYW5ldC5vcmcvcm9hbGRhP2xhbmc9ZnImcHo9cm9uYWxkK2d1eSZuej1hcm5hdXgmcD1jYXRoZXJpbmUrZWxpc2FiZXRoJm49ZmFuaWNoZXI7aHR0cHM6Ly9ndy5nZW5lYW5ldC5vcmcvcm9hbGRhP2xhbmc9ZnImcHo9cm9uYWxkK2d1eSZuej1hcm5hdXgmcD1waWVycmUreGF2aWVyJm49ZmFuaWNoZXI7aHR0cHM6Ly9ndy5nZW5lYW5ldC5vcmcvcm9hbGRhP2xhbmc9ZnImcHo9cm9uYWxkK2d1eSZuej1hcm5hdXgmcD1jaGFybGVzJm49ZmFuaWNoZXImb2M9MTtodHRwczovL2d3LmdlbmVhbmV0Lm9yZy9yb2FsZGE/bGFuZz1mciZwej1yb25hbGQrZ3V5Jm56PWFybmF1eCZwPWpvc2VwaCthbGV4YW5kcmUmbj1mYW5pY2hlcjtodHRwczovL2d3LmdlbmVhbmV0Lm9yZy9yb2FsZGE/bGFuZz1mciZwej1yb25hbGQrZ3V5Jm56PWFybmF1eCZwPWxvdWlzZStmZWxpbmUmbj1mYW5pY2hlcjtodHRwczovL2d3LmdlbmVhbmV0Lm9yZy9yb2FsZGE/bGFuZz1mciZwej1yb25hbGQrZ3V5Jm56PWFybmF1eCZwPXhhdmllciZuPWZhbmljaGV0JywgY2hpbGRyZW49J2h0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL3JvYWxkYT9sYW5nPWZyJnB6PXJvbmFsZCtndXkmbno9YXJuYXV4JnA9cGllcnJlJm49YmFyZGluO2h0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL3JvYWxkYT9sYW5nPWZyJnB6PXJvbmFsZCtndXkmbno9YXJuYXV4JnA9ZnJhbmNvaXMmbj1iYXJkaW47aHR0cHM6Ly9ndy5nZW5lYW5ldC5vcmcvcm9hbGRhP2xhbmc9ZnImcHo9cm9uYWxkK2d1eSZuej1hcm5hdXgmcD1hbWVkZSZuPWJhcmRpbicsIG1hcnJpYWdlPSdodHRwczovL2d3LmdlbmVhbmV0Lm9yZy9yb2FsZGE/bGFuZz1mciZwej1yb25hbGQrZ3V5Jm56PWFybmF1eCZwPXBpZXJyZStqYWNxdWVzJm49YmFyZGluO3szMC8wNC8xODM5PUNow6J0aWxsb24tQ29saWdueX0nLCBzZWFyY2hlZD0ndHJ1ZScNCkdlbmVhbmV0UGVyc29ue3VybD0naHR0cHM6Ly9ndy5nZW5lYW5ldC5vcmcvcm9hbGRhP2xhbmc9ZnImcHo9cm9uYWxkK2d1eSZuej1hcm5hdXgmcD1qZWFuK2phY3F1ZXMmbj1ib3lhdScsIGZpcnN0TmFtZT0nSmVhbiBKYWNxdWVzJywgZmFtaWx5TmFtZT0nQk9ZQVUnLCBnZW5kZXI9J01hbGUnLCBiaXJ0aERhdGU9JzAyLzA4LzE4MTAnLCBwbGFjZU9mQmlydGg9J1NhaW50ZSBHZW5ldmlldmUgZGVzIEJvaXMgNDUnLCBmYXRoZXI9J2h0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL3JvYWxkYT9sYW5nPWZyJnB6PXJvbmFsZCtndXkmbno9YXJuYXV4JnA9bWFydGluJm49Ym95YXUnLCBtb3RoZXI9J2h0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL3JvYWxkYT9sYW5nPWZyJnB6PXJvbmFsZCtndXkmbno9YXJuYXV4JnA9Y2F0aGVyaW5lJm49YmVycnknLCBtYXJyaWFnZT0naHR0cHM6Ly9ndy5nZW5lYW5ldC5vcmcvcm9hbGRhP2xhbmc9ZnImcHo9cm9uYWxkK2d1eSZuej1hcm5hdXgmcD1tYXJpZSthbm5lJm49YmFyZGluO3swNS8xMi8xODM3PUNoYXRpbGxvbiBDb2xpZ255IDQ1fScsIHNlYXJjaGVkPSd0cnVlJw0KR2VuZWFuZXRQZXJzb257dXJsPSdodHRwczovL2d3LmdlbmVhbmV0Lm9yZy9yb2FsZGE/bGFuZz1mciZwej1yb25hbGQrZ3V5Jm56PWFybmF1eCZwPW1hcnRpbiZuPWJveWF1JywgZmlyc3ROYW1lPSdNYXJ0aW4nLCBmYW1pbHlOYW1lPSdCT1lBVScsIGdlbmRlcj0nTWFsZScsIGNoaWxkcmVuPSdodHRwczovL2d3LmdlbmVhbmV0Lm9yZy9yb2FsZGE/bGFuZz1mciZwej1yb25hbGQrZ3V5Jm56PWFybmF1eCZwPWplYW4ramFjcXVlcyZuPWJveWF1JywgbWFycmlhZ2U9J2h0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL3JvYWxkYT9sYW5nPWZyJnB6PXJvbmFsZCtndXkmbno9YXJuYXV4JnA9Y2F0aGVyaW5lJm49YmVycnk7e251bGw9bnVsbH0nLCBzZWFyY2hlZD0ndHJ1ZScNCkdlbmVhbmV0UGVyc29ue3VybD0naHR0cHM6Ly9ndy5nZW5lYW5ldC5vcmcvcm9hbGRhP2xhbmc9ZnImcHo9cm9uYWxkK2d1eSZuej1hcm5hdXgmcD1jYXRoZXJpbmUmbj1iZXJyeScsIGZpcnN0TmFtZT0nQ2F0aGVyaW5lJywgZmFtaWx5TmFtZT0nQkVSUlknLCBnZW5kZXI9J0ZlbWFsZScsIGRlYXRoRGF0ZT0nMTgxNCcsIHBsYWNlT2ZEZWF0aD0nQ2hhdGlsbG9uIENvbGlnbnkgNDUnLCBjaGlsZHJlbj0naHR0cHM6Ly9ndy5nZW5lYW5ldC5vcmcvcm9hbGRhP2xhbmc9ZnImcHo9cm9uYWxkK2d1eSZuej1hcm5hdXgmcD1qZWFuK2phY3F1ZXMmbj1ib3lhdScsIG1hcnJpYWdlPSdodHRwczovL2d3LmdlbmVhbmV0Lm9yZy9yb2FsZGE/bGFuZz1mciZwej1yb25hbGQrZ3V5Jm56PWFybmF1eCZwPW1hcnRpbiZuPWJveWF1O3tudWxsPW51bGx9Jywgc2VhcmNoZWQ9J3RydWUnDQpxAH4Ayw==";

        GeneanetBrowser geneanetBrowser = getGeneanetBrowserFromString(result);
        String tree = GeneanetBrowser.findTreeName(testUrl);
        GeneanetPerson rootPerson = geneanetBrowser.rootPerson;
        int nbPeopleGen = geneanetBrowser.getNbPeople();
        System.out.println(geneanetBrowser.rootPerson);

        //Gedcom file
        String gedcomFile = "C:\\Users\\Dan\\Desktop\\Programmation\\IntelliJ\\Genealogy\\Genealogy\\src\\main\\resources\\famille1.ged";
        MyGedcomReader myGedcomReader = new MyGedcomReader();
        Genealogy.genealogy = myGedcomReader.read(gedcomFile);
        Genealogy.genealogy.parseContents();
        Genealogy.genealogy.sortPersons();
        Person person = Genealogy.genealogy.findPersonById("@I93@");
        System.out.println(person);

        //Comparing
        TreeComparator treeComparator = new TreeComparator(rootPerson,person, geneanetBrowser.allPeopleUrl);
        treeComparator.compareRoot();
        treeComparator.printDifferences();
        int nbPeopleComp = treeComparator.nbPeople;
        if (nbPeopleGen == nbPeopleComp){
            logger.info("Comparison OK for " + testUrl);
        } else {
            logger.info("Comparison KO for " + testUrl + " Expected " + nbPeopleGen + " but got " + nbPeopleComp);
        }
        treeComparator.saveDifference(tree);
        HashMap<GeneanetPerson, String> geneanetPersonStringHashMap = treeComparator.readDifferences(tree);
        String comparison = treeComparator.compareDifferences(geneanetPersonStringHashMap);
        System.out.println(comparison);
    }

}
