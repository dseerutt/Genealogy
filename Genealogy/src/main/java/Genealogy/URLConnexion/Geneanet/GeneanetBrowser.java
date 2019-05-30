package Genealogy.URLConnexion.Geneanet;

import Genealogy.Model.Date.MyDate;
import Genealogy.URLConnexion.Serializer;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import us.codecraft.xsoup.Xsoup;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dan on 01/11/2017.
 */
public class GeneanetBrowser implements Serializable {

    public static String geneanetURL;
    public static String geneanetURLpart2;
    public static String username;
    public static String password;
    public static String formRegex;
    public String url;
    public String searchOutput = "";
    public Map<String, String> cookie;
    private transient GeneanetConverter geneanetConverter;
    public GeneanetPerson rootPerson;
    public int expectedNbPeople = 0;
    public HashSet<String> peopleUrl = new HashSet<String>();
    public HashMap<String, GeneanetPerson> allPeopleUrl = new HashMap<String, GeneanetPerson>();
    final static Logger logger = Logger.getLogger(GeneanetBrowser.class);
    private static transient ArrayList<GeneanetTree> geneanetTrees = new ArrayList<>();

    public GeneanetBrowser(String url0) throws Exception {
        url = url0;
        init();
    }

    public GeneanetBrowser() throws Exception {
        init();
    }

    public void init() throws Exception {
        initProperties();
        if (url != null) {
            Document doc = initConnexion();
            geneanetConverter = new GeneanetConverter(doc);
        }
    }

    /**
     * Fonction initProperties
     * initialise les propriétés de la classe GeneanetBrowser
     */
    public void initProperties() throws Exception {
        initGeneanetPathProperties();
        initGeneanetTrees();
    }

    public void initGeneanetTrees() throws Exception {
        if (!geneanetTrees.isEmpty()){
            return;
        }
        Properties prop = new Properties();
        InputStream input = null;
        try {
            String path = Serializer.getPath();
            if (path == null){
                Serializer serializer = new Serializer();
                path = Serializer.getPath()
                ;
            }
            input = new FileInputStream(Serializer.getPath() + "geneanetTrees.properties");
            prop.load(input);
            String property = "tree";
            String tmpProperty = "";
            int index = 1;
            while (index <= prop.size()){
                tmpProperty = prop.getProperty(property + index);
                if (tmpProperty != null) {
                    String[] tmpTab = tmpProperty.split(";");
                    if (tmpTab.length == 4) {
                        geneanetTrees.add(new GeneanetTree(tmpTab[0], tmpTab[1], tmpTab[2], tmpTab[3]));
                    } else if (tmpTab.length < 4 && tmpTab.length > 1) {
                        geneanetTrees.add(new GeneanetTree(tmpTab[0], tmpTab[1], tmpTab[2]));
                    }
                }
                index++;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Impossible de lire le fichier de propriétés");
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void initGeneanetPathProperties() throws Exception {
        Properties prop = new Properties();
        InputStream input = null;
        try {
            String path = Serializer.getPath();
            if (path == null){
                Serializer serializer = new Serializer();
                path = Serializer.getPath()
;
            }
            input = new FileInputStream(Serializer.getPath() + "geneanet.properties");
            prop.load(input);
            geneanetURL = prop.getProperty("geneanetConnexionURL");
            geneanetURLpart2 = prop.getProperty("geneanetConnexionURLpart2");
            username = prop.getProperty("u2");
            password = prop.getProperty("p2");
            formRegex = prop.getProperty("formRegex");
            geneanetConverter.setXpathGender(prop.getProperty("XpathGender"));
            geneanetConverter.setXpathNames(prop.getProperty("XpathNames"));
            geneanetConverter.setXpathNames2(prop.getProperty("XpathNames2"));
            geneanetConverter.setXpathNames3(prop.getProperty("XpathNames3"));
            geneanetConverter.setXpathBirthAndDeath(prop.getProperty("XpathBirthAndDeath"));
            geneanetConverter.setXpathFather(prop.getProperty("XpathFather"));
            geneanetConverter.setXpathMother(prop.getProperty("XpathMother"));
            geneanetConverter.setXpathFamily(prop.getProperty("XpathFamily"));
            geneanetConverter.setXpathFamily2(prop.getProperty("XpathFamily2"));
            geneanetConverter.setXpathParents2(prop.getProperty("XpathParents2"));
            geneanetConverter.setGeneanetSearchURL(prop.getProperty("geneanetSearchURL"));
            geneanetConverter.setXpathSection(prop.getProperty("XpathSection"));
            geneanetConverter.setXpathMarriageDate(prop.getProperty("XpathMarriageDate"));
            geneanetConverter.setXpathMarriagePartner(prop.getProperty("XpathMarriagePartner"));
            geneanetConverter.setXpathBrother(prop.getProperty("XpathBrother"));
            geneanetConverter.setXpathBrother2(prop.getProperty("XpathBrother2"));
            geneanetConverter.setXpathHalfBrother(prop.getProperty("XpathHalfBrother"));
            geneanetConverter.setXpathChildren(prop.getProperty("XpathChildren"));
            geneanetConverter.setXpathUrl(prop.getProperty("XpathUrl"));
            geneanetConverter.setXpathImage(prop.getProperty("XpathImage"));
            if (geneanetURL == null){
                throw new Exception("Impossible de récupérer le fichier de propriétés");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new Exception("Impossible de lire le fichier de propriétés");
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ArrayList<GeneanetTree> getGeneanetTrees() {
        return geneanetTrees;
    }

    public void setGeneanetTrees(ArrayList<GeneanetTree> geneanetTrees) {
        this.geneanetTrees = geneanetTrees;
    }

    public String getGedcomIdFromGeneanetTrees(){
        for (GeneanetTree geneanetTree : geneanetTrees)
        {
            if (geneanetTree.getUrl().replace("&ocz=0","").equals(url.replace("&ocz=0",""))){
                return geneanetTree.getGedcomId();
            }
        }
        return "";
    }
    public int getPeopleNumberFromGeneanetTrees(){
        for (GeneanetTree geneanetTree : geneanetTrees)
        {
            if (geneanetTree.getUrl().replace("&ocz=0","").equals(url.replace("&ocz=0",""))){
                return geneanetTree.getPeopleNumber();
            }
        }
        return -1;
    }

    public GeneanetConverter getGeneanetConverter() {
        return geneanetConverter;
    }


    /**
     * Méthode initConnexion
     * initialise les propriétés et la connexion
     *
     * @throws Exception
     */
    public Document initConnexion() throws Exception {
        String csrfValue = "";
        Connection.Response loginForm = null;
        int cptConnexion = 1;
        boolean exit = false;
        do {
            logger.info("Connexion " + cptConnexion + " to " + geneanetURL);
            try {
                loginForm = Jsoup.connect(geneanetURL)
                        .method(Connection.Method.GET)
                        .execute();
                exit = true;
            } catch (Exception exception) {
                logger.info("Failed to connect " + exception.getMessage());
                Thread.sleep(1000);
                if (cptConnexion >= 3){
                    logger.error("Exiting, failed to connect " + exception.getMessage());
                    throw new Exception("Cannot connect to url " + geneanetURL);
                }
                cptConnexion++;
            }
        }
        while (!exit);
        Document doc = loginForm.parse();

        Pattern pattern = Pattern.compile(formRegex);
        Matcher matcher = pattern.matcher(doc.toString());
        while (matcher.find()) {
            csrfValue = matcher.group(1);
        }

        Connection.Response res;
        res = Jsoup.connect(geneanetURL + geneanetURLpart2)
                .data("_username", username)
                .data("_password", password)
                .data("_remember_me", "1")
                .data("_submit", "")
                .data("_csrf_token", csrfValue)
                .cookies(loginForm.cookies())
                .method(Connection.Method.POST)
                .execute();

        //login OK
        if (res.cookies().isEmpty()) {
            throw new Exception("fail to login");
        }
        cookie = res.cookies();
        return doc;
    }

    /**
     * Fonction connect
     *
     * @param url de connexion
     * @return
     * @throws Exception
     */
    public Document connect(String url) throws Exception {
        Connection.Response res2 = Jsoup.connect(url)
                .cookies(cookie)
                .method(Connection.Method.POST)
                .execute();
        return res2.parse();
    }

    /**
     * Fonction findXPath
     * Détermine le Xpath du String text dans le document
     *
     * @param document
     * @param text
     * @return
     */
    public String findXPath(Document document, String text) {
        String result = null;
        for (Node node : document.childNodes()) {
            result = findXPath2(node, text);
            if (result != null && (node instanceof Element)) {
                Element e = (Element) node;
                String tag = e.tag() + "";
                return "/" + tag + "/" + result;
            }
        }
        return "";
    }

    /**
     * Fonction findXPath2
     * Détermine le Xpath du String text dans le document de façon récursive
     *
     * @param document
     * @param text
     * @return
     */
    public String findXPath2(Node document, String text) {
        HashMap<String, Integer> tagList = new HashMap<String, Integer>();
        if (document.toString().contains(text)) {
            if (document.childNodes().size() != 0) {
                for (Node node : document.childNodes()) {
                    if (node instanceof Element) {
                        Element e = (Element) node;
                        String value = findXPath2(node, text);
                        String tag = e.tag() + "";
                        if (tagList.containsKey(tag)) {
                            tagList.put(tag, tagList.get(tag) + 1);
                        } else {
                            tagList.put(tag, 1);
                        }
                        String separator = "/";
                        if (value != null && value.equals("")) {
                            separator = "";
                        }
                        if (value != null) {
                            if ((tagList.containsKey(e.tag() + "")) && (tagList.get(tag) != 1)) {
                                return e.tag() + "[" + tagList.get(tag) + "]" + separator + value;
                            } else {
                                return e.tag() + separator + value;
                            }
                        }
                    } else if (node instanceof TextNode) {
                        if (node.toString().equals(text)) {
                            return "";
                        }
                    }
                }
            }
        } else {
            return null;
        }

        return null;
    }

    public void searchTree(GeneanetPerson person){
        searchPerson(person);

        //Call others
        searchFather(person);
        searchMother(person);
        searchSiblings(person);
        searchHalfSiblings(person);
        searchPartner(person, false);
    }

    private void searchSiblings(GeneanetPerson person) {
        ArrayList<GeneanetPerson> newSiblings = new ArrayList<GeneanetPerson>();
        for (GeneanetPerson sibling : person.getSiblings()){
            searchPerson(sibling, true);
            searchSiblingPartner(sibling, "Sibling Partner");
            newSiblings.add(sibling);
        }
        person.setSiblings(newSiblings);
    }

    private void searchHalfSiblings(GeneanetPerson person) {
        ArrayList<GeneanetPerson> newHalfSiblings = new ArrayList<GeneanetPerson>();
        for (GeneanetPerson halfSibling : person.getHalfSiblings()){
            searchPerson(halfSibling, true);
            searchSiblingPartner(halfSibling, "Half Sibling Partner");
            newHalfSiblings.add(halfSibling);
        }
        person.setHalfSiblings(newHalfSiblings);
    }

    public void searchRoot(){
        rootPerson =  new GeneanetPerson(url);
        rootPerson.setRootperson(true);

        searchPerson(rootPerson);
        searchFather(rootPerson);
        searchMother(rootPerson);
        searchSiblings(rootPerson);
        searchHalfSiblings(rootPerson);
        searchPartner(rootPerson, true);
        searchChildren(rootPerson);
    }

    private void searchChildren(GeneanetPerson rootPerson) {
        ArrayList<GeneanetPerson> newChildren = new ArrayList<GeneanetPerson>();
        for (GeneanetPerson child : rootPerson.getChildren()){
            searchPerson(child);
            newChildren.add(child);
        }
        rootPerson.setChildren(newChildren);
    }

    public void searchFather(GeneanetPerson person){
        GeneanetPerson father = person.getFather();
        if (father != null && !father.getUrl().equals("") && !father.isSearched()){
            searchTree(father);
            person.setFather(father);
        }
    }

    public void searchMother(GeneanetPerson person){
        GeneanetPerson mother = person.getMother();
        if (mother != null && !mother.getUrl().equals("") && !mother.isSearched()) {
            searchTree( mother);
            person.setMother(mother);
        }
    }

    public void researchPartner(GeneanetPerson person, boolean root, String type){
        boolean found = false;
        HashMap<GeneanetPerson, HashMap<MyDate, String>> marriage = person.getMarriage();
        HashMap<GeneanetPerson, HashMap<MyDate, String>> newMarriage = new HashMap<GeneanetPerson, HashMap<MyDate, String>>();
        for (Map.Entry<GeneanetPerson, HashMap<MyDate, String>> entry :  marriage.entrySet()){
            GeneanetPerson partner = entry.getKey();
            if (!partner.getUrl().equals("") && !partner.isSearched()) {
                if (!found){
                    logger.info(type + " (" + marriage.size() +  ") research for " + person.getFirstName() + " " + person.getFamilyName());
                    found = true;
                }
                if (root){
                    searchTree(partner);
                } else {
                    searchPerson(partner, true);
                    researchPartnerNoRecursive(partner, type);
                }
                newMarriage.put(partner,entry.getValue());
            }
        }
        person.setMarriage(newMarriage);
    }

    public void researchPartnerNoRecursive(GeneanetPerson person, String type){
        if (person != null && person.getMarriage().size() > 1){
            logger.info("Recursive (" + type + ") partner research (" + person.getMarriage().size() + ") for " + person.getFirstName() + " " + person.getFamilyName());
            HashMap<GeneanetPerson, HashMap<MyDate, String>> marriage = person.getMarriage();
            HashMap<GeneanetPerson, HashMap<MyDate, String>> newMarriage = new HashMap<GeneanetPerson, HashMap<MyDate, String>>();
            for (Map.Entry<GeneanetPerson, HashMap<MyDate, String>> entry :  marriage.entrySet()){
                GeneanetPerson partner = entry.getKey();
                if (!partner.getUrl().equals("") && !partner.isSearched()) {
                    searchPerson(partner, true);
                    newMarriage.put(partner,entry.getValue());
                }
            }
            person.setMarriage(newMarriage);
        }
    }

    public void searchPartner(GeneanetPerson person, boolean root){
        if (person != null){
            if (person.getMarriage() != null && (person.getMarriage().size() > 1 || person.isRootperson())){
                researchPartner(person, root , "Partner");
            }
        }
    }

    public void searchSiblingPartner(GeneanetPerson person, String type){
        if (person != null){
            if (person.getMarriage() != null ){
                researchPartner(person, false, type);
            }
        }
    }

    public void addPersonToSearchedOutputPersons(String input, GeneanetPerson person){
        searchOutput += input + person.toString() + System.getProperty("line.separator");
    }

    public void saveSearchOutput(){
        String path = Serializer.getPath();
        if (path == null){
            Serializer serializer = new Serializer();
            path = Serializer.getPath();
        }
        File file = new File(path + File.separator + "geneanetTrees" + File.separator + findTreeName(url) + ".bak");
        FileWriter fr = null;
        try {
            fr = new FileWriter(file);
            fr.write(searchOutput);
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

    private void searchPerson(GeneanetPerson person) {
        searchPerson(person,false);
    }

    private void searchPerson(GeneanetPerson person, boolean partialSearch) {
        if (!peopleUrl.contains(person.getUrl()) && (!partialSearch ||  !allPeopleUrl.containsKey(person.getUrl()))){
            int tries = 1;
            String inputTxt = "";
            do {
                try {
                    String url = person.getUrl();
                    url = url.replace("&ocz=0","").replace("&iz=0","");
                    Document inputDocument = connect(url);
                    //Double search for partners/siblings : don't add if partner
                    if (!partialSearch){
                        peopleUrl.add(url);
                    }
                    allPeopleUrl.put(url,person);
                    geneanetConverter.parseDocument(inputDocument, person);
                    if (expectedNbPeople != 0){
                        inputTxt = getNbPeople() + "/" + expectedNbPeople + " ";
                    }
                    if (partialSearch){
                        logger.info(inputTxt + "Partial Search " + person.getFirstName() + " " + person.getFamilyName() + " : " + person);
                    } else {
                        logger.info(inputTxt + "Search " + person.getFirstName() + " " + person.getFamilyName() + " : " + person);
                    }
                    addPersonToSearchedOutputPersons(inputTxt, person);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Connexion " + tries + ":  erreur lors de la recherche de l'url " + url);
                    tries++;
                }
            } while (tries <= 3);
        } else {
            logger.info("Stopped search for " + person.getUrl());
        }

    }

    private static void testSearch(String url){
        try {
            GeneanetBrowser browser = new GeneanetBrowser(url);
            GeneanetPerson person = new GeneanetPerson(url);
            browser.searchPerson(person);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static GeneanetBrowser mainSearchFullTree(String url, int expectedNbPeople0) {
        try {
            GeneanetBrowser browser = new GeneanetBrowser(url);
            browser.expectedNbPeople = expectedNbPeople0;
            browser.searchRoot();
            System.out.println("People searched : " + browser.getNbPeople());
            return browser;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static GeneanetBrowser mainSearchFullTree(String url) {
        return mainSearchFullTree(url, 0);
    }

    public static String findTreeName(String tree){
        String regex = ".*\\.org/(.*)\\?.*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(tree);
        if (matcher != null && matcher.find()) {
            String name = matcher.group(1);
            return name;
        }
        return null;
    }


    public static void searchAllTrees(boolean save) {
        hidePrintOut();
        try {
            GeneanetBrowser browser = new GeneanetBrowser("");
            int cpt = 1;
            ArrayList<GeneanetTree> localGeneanetTrees = browser.geneanetTrees;
            for (GeneanetTree tree : localGeneanetTrees){
                if (cpt >= 0){
                    logger.info("Searching " + tree.getUrl());
                    String treeName = tree.getName();
                    Integer value = tree.getPeopleNumber();
                    GeneanetBrowser newBrowser = mainSearchFullTree(tree.getUrl(), tree.getPeopleNumber());
                    int people = newBrowser.getNbPeople();
                    if (value != null && (value != people)){
                        logger.error("Test KO for URL " + tree.getUrl() + " : expected " + value + " but got " + people);
                        newBrowser.saveSearchOutput();
                        return;
                    } else {
                        logger.info("Test OK for URL " + treeName);
                        if (save){
                            newBrowser.saveSearchOutput();
                        }
                    }
                }
                cpt++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getNbPeople() {
        return allPeopleUrl.size();
    }

    public static void mainTestXpath(String url, String pattern) {
        try {
            GeneanetBrowser browser = new GeneanetBrowser("");
            Document doc = browser.connect(url);
            String result = Xsoup.compile(pattern).evaluate(doc).get();
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void mainFindXpath(String url, String text) {
        try {
            GeneanetBrowser browser = new GeneanetBrowser(url);
            Document doc = browser.connect(url);
            String data = browser.findXPath(doc, text);
            System.out.println(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void hidePrintOut(){
        System.setOut(new PrintStream(new OutputStream() {
            public void write(int b) {
                // Do nothing
            }
        }));
    }

    public static void main(String[] args) {
        BasicConfigurator.configure();
        String testUrl = "https://gw.geneanet.org/msebastien1?lang=fr&pz=sebastien+david&nz=mercier&p=estienne&n=porcher";
        String testUrl2 = "https://gw.geneanet.org/msebastien1?lang=fr&pz=sebastien+david&nz=mercier&p=louis+claude&n=vincent";
        String testUrl3 = "https://gw.geneanet.org/lisetoxe?lang=fr&p=louise&n=olivier";
        String xpathPattern = "/html/body/div/div/div/div[5]/div/div/div/div/div/div/div/div/div/div/div[2]/div/div/h2[2]/span[2]/text()";
        String xpathText = "Agnès Des VOISINES";

        //searchAllTrees(true);
        //mainSearchFullTree(testUrl, false);
        //GeneanetPerson person = mainSearchFullTree(testUrl, false).rootPerson;
        //System.out.println(person.getFather());
        //System.out.println(person.getMother());
        testSearch(testUrl3);
        //mainTestSearchTree();
        //mainTestXpath(testUrl3,xpathPattern);
        //mainFindXpath(testUrl3, xpathText);
    }
}
