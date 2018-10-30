package Genealogy.URLConnexion.Geneanet;

import Genealogy.AuxMethods;
import Genealogy.Model.Date.MyDate;
import Genealogy.URLConnexion.MyHttpURLConnexion;
import Genealogy.URLConnexion.Serializer;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;
import us.codecraft.xsoup.Xsoup;

import javax.security.auth.login.LoginException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.security.spec.ECField;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dan on 01/11/2017.
 */
public class GeneanetBrowser {

    public static String geneanetURL;
    public static String geneanetURLpart2;
    public static String username;
    public static String password;
    public static String formRegex;
    public String url;
    public Map<String, String> cookie;
    private GeneanetConverter geneanetConverter;
    public GeneanetPerson rootPerson;
    final static Logger logger = Logger.getLogger(GeneanetBrowser.class);

    public GeneanetBrowser(String url0) throws Exception {
        url = url0;
        init();
    }

    public void init() throws Exception {
        Document doc = initConnexion();
        geneanetConverter = new GeneanetConverter(doc);
    }

    /**
     * Fonction initGovernors
     * initialise les propriétés de la classe GeneanetBrowser
     */
    public void initProperties() throws Exception {
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
            geneanetConverter.setXpathFirstName(prop.getProperty("XpathFirstName"));
            geneanetConverter.setXpathFamilyName(prop.getProperty("XpathFamilyName"));
            geneanetConverter.setXpathBirth(prop.getProperty("XpathBirth"));
            geneanetConverter.setXpathDeath(prop.getProperty("XpathDeath"));
            geneanetConverter.setXpathFather(prop.getProperty("XpathFather"));
            geneanetConverter.setXpathMother(prop.getProperty("XpathMother"));
            geneanetConverter.setXpathFamily(prop.getProperty("XpathFamily"));
            geneanetConverter.setGeneanetSearchURL(prop.getProperty("geneanetSearchURL"));
            geneanetConverter.setXpathSection(prop.getProperty("XpathSection"));
            geneanetConverter.setXpathMarriageDate(prop.getProperty("XpathMarriageDate"));
            geneanetConverter.setXpathMarriagePartner(prop.getProperty("XpathMarriagePartner"));
            geneanetConverter.setXpathBrother(prop.getProperty("XpathBrother"));
            geneanetConverter.setXpathHalfBrother(prop.getProperty("XpathHalfBrother"));
            geneanetConverter.setXpathChildren(prop.getProperty("XpathChildren"));
            geneanetConverter.setXpathUrl(prop.getProperty("XpathUrl"));
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

    /**
     * Méthode initConnexion
     * initialise les propriétés et la connexion
     *
     * @throws Exception
     */
    public Document initConnexion() throws Exception {
        initProperties();
        String csrfValue = "";
        Connection.Response loginForm = null;
        int cptConnexion = 1;
        do {
            logger.info("Connexion " + cptConnexion + " to " + geneanetURL);
            try {
                loginForm = Jsoup.connect(geneanetURL)
                        .method(Connection.Method.GET)
                        .execute();
            } catch (Exception exception) {
                logger.info("Failed to connect " + exception.getMessage());
                cptConnexion++;
            }
        }
        while (loginForm == null || cptConnexion == 5);
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
        //searchPartner(person);
    }

    public void searchRoot(){
        rootPerson =  new GeneanetPerson(url);
        searchPerson(rootPerson);
        searchFather(rootPerson);
        searchMother(rootPerson);
    }

    public void searchFather(GeneanetPerson person){
        GeneanetPerson father = person.getFather();
        if (father != null && !father.getUrl().equals("") && !father.isSearched()){
            String fatherUrl = father.getUrl();
            searchTree(father);
            rootPerson.setFather(father);
        }
    }

    public void searchMother(GeneanetPerson person){
        GeneanetPerson mother = person.getMother();
        if (mother != null && !mother.getUrl().equals("") && !mother.isSearched()) {
            String motherUrl = mother.getUrl();
            searchTree( mother);
            rootPerson.setFather(mother);
        }
    }

    public void searchPartner(GeneanetPerson person){
        HashMap<GeneanetPerson, HashMap<MyDate, String>> marriage = person.getMarriage();
        for (GeneanetPerson partner : marriage.keySet()){
            if (!partner.getUrl().equals("") && !partner.isSearched()) {
                String partnerUrl = partner.getUrl();
                //rootPerson.setMarriage(searchTree(partnerUrl, partner));
            }
        }

    }

    private void searchPerson(GeneanetPerson person) {
        try {
            String url = person.getUrl();
            Document inputDocument = connect(url);
            geneanetConverter.parseDocument(inputDocument, url, person);
            logger.info("Searched " + person.getFirstName() + " " + person.getFamilyName() + " : " + person);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la recherche de l'url " + url);
        }
    }


    public static void main44(String[] args) {
        BasicConfigurator.configure();
        try {
            //String url = "https://gw.geneanet.org/dil?lang=fr&iz=0&p=louis+claude&n=vincent";
            String url = "https://gw.geneanet.org/dil?lang=fr&iz=0&p=louise&n=vincent";
            GeneanetBrowser browser = new GeneanetBrowser(url);
            //Document doc = browser.connect(url);
            browser.searchRoot();
            System.out.println(browser.rootPerson);
            //System.out.println(browser.rootPerson.getFather());
            //System.out.println(browser.rootPerson.getMother());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        BasicConfigurator.configure();
        try {
            //String url = "https://gw.geneanet.org/dil?lang=fr&iz=0&p=louis+claude&n=vincent";
            String url = "https://gw.geneanet.org/dil?lang=fr&iz=0&p=pierre&n=collet";
            GeneanetBrowser browser = new GeneanetBrowser(url);
            browser.init();
            //Document doc = browser.connect(url);
            browser.searchPerson(new GeneanetPerson(url));
            System.out.println(browser.rootPerson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
/*
    public static void main3(String[] args) {
        try {
            GeneanetBrowser browser = new GeneanetBrowser("");
            Document doc = browser.connect("https://gw.geneanet.org/dil?lang=fr&iz=0&p=louis&n=thierry");
            String pattern = "/html/body/div/div/div/div[5]/div/div/div/div/div/div/div/div/div/div/div[2]/div/div/ul[3]/li/ul/li[2]/a/@href";
            String result = Xsoup.compile(pattern).evaluate(doc).get();
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main2(String[] args) {
        try {
            String url = "https://gw.geneanet.org/dil?lang=fr&iz=0&p=louise&n=vincent";
            GeneanetBrowser browser = new GeneanetBrowser(url);
            Document doc = browser.connect(url);
            String data = browser.findXPath(doc, "1805-1844/");
            System.out.println(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
