package Genealogy.URLConnexion.Geneanet;

import Genealogy.URLConnexion.MyHttpURLConnexion;
import Genealogy.URLConnexion.Serializer;
import org.apache.commons.codec.binary.StringUtils;
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
    public Map<String, String> cookie;
    private String path;
    private boolean jar = false;
    private GeneanetConverter geneanetConverter;

    public GeneanetBrowser() throws  Exception{
        initConnexion();
        geneanetConverter = new GeneanetConverter();
    }

    /**
     * Fonction initPath
     * gère le path des propriétés
     */
    private void initPath() {
        path = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" +
                File.separator + "resources" + File.separator;
        String className = Serializer.class.getName().replace('.', '/');
        String classJar = Serializer.class.getClass().getResource("/" + className + ".class").toString();
        if (classJar.startsWith("jar:")) {
            jar = true;
            path = System.getProperty("user.dir") + File.separator + "Properties" + File.separator;
        }
    }

    /**
     * Fonction initProperties
     * initialise les propriétés de la classe GeneanetBrowser
     */
    public void initProperties(){
        initPath();
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream(path + "geneanet.properties");
            prop.load(input);
            geneanetURL = prop.getProperty("geneanetURL");
            geneanetURLpart2 = prop.getProperty("geneanetURLpart2");
            username = prop.getProperty("u2");
            password = prop.getProperty("p2");
            formRegex = prop.getProperty("formRegex");
            geneanetConverter.setXpathFirstName(prop.getProperty("XpathFirstName"));
            geneanetConverter.setXpathFamilyName(prop.getProperty("XpathFamilyName"));
            geneanetConverter.setXpathBirth(prop.getProperty("XpathBirth"));
            geneanetConverter.setXpathDeath(prop.getProperty("XpathDeath"));
            geneanetConverter.setXpathFather(prop.getProperty("XpathFather"));
            geneanetConverter.setXpathMother(prop.getProperty("XpathMother"));
            geneanetConverter.setXpathWeddingAndChildren(prop.getProperty("XpathWeddingAndChildren"));
            geneanetConverter.setXpathBrotherhood(prop.getProperty("XpathBrotherhood"));
        } catch (IOException ex) {
            ex.printStackTrace();
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
     * @throws Exception
     */
    public void initConnexion() throws Exception{
            initProperties();
            String csrfValue = "";
            Connection.Response loginForm = Jsoup.connect(geneanetURL)
                    .method(Connection.Method.GET)
                    .execute();
            Document doc = loginForm.parse();

            Pattern pattern = Pattern.compile(formRegex);
            Matcher matcher = pattern.matcher(doc.toString());
            while(matcher.find()) {
                csrfValue = matcher.group(1);
            }

            Connection.Response res = Jsoup.connect(geneanetURL + geneanetURLpart2)
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
    }

    /**
     * Fonction connect
     * @param url de connexion
     * @return
     * @throws Exception
     */
    public Document connect(String url) throws Exception{
        Connection.Response res2 = Jsoup.connect(url)
                .cookies(cookie)
                .method(Connection.Method.POST)
                .execute();
        return res2.parse();
    }

    /**
     * Fonction findXPath
     * Détermine le Xpath du String text dans le document
     * @param document
     * @param text
     * @return
     */
    public String findXPath(Document document, String text){
        String result = null;
        for (Node node : document.childNodes()){
            result = findXPath2(node,text);
            if (result != null && (node instanceof Element)){
                Element e = (Element) node;
                String tag = e.tag() + "";
                return "/" + tag + "/" +  result;
            }
        }
        return "";
    }

    /**
     * Fonction findXPath2
     * Détermine le Xpath du String text dans le document de façon récursive
     * @param document
     * @param text
     * @return
     */
    public String findXPath2(Node document, String text){
        HashMap<String,Integer> tagList = new HashMap<String,Integer>();
        if (document.toString().contains(text)){
            if (document.childNodes().size() != 0){
                for (Node node : document.childNodes()){
                    if (node instanceof Element){
                        Element e = (Element) node;
                        String value = findXPath2(node,text);
                        String tag = e.tag() + "";
                        if (tagList.containsKey(tag)){
                            tagList.put(tag,tagList.get(tag)+1);
                        } else {
                            tagList.put(tag,1);
                        }
                        String separator = "/";
                        if (value != null && value.equals("")){
                            separator = "";
                        }
                        if (value != null){
                            if ((tagList.containsKey(e.tag() + ""))&& (tagList.get(tag) != 1)){
                                return  e.tag() + "[" + tagList.get(tag) + "]" + separator + value;
                            } else {
                                return  e.tag() + separator + value;
                            }
                        }
                    } else if (node instanceof TextNode){
                        if (node.toString().equals(text)){
                            return "";
                        }
                    }
                }
            }
        }
        else {
            return null;
        }

        return null;
    }

    private GeneanetPerson search(String url){
        try {
            Document inputDocument = connect(url);
            GeneanetPerson person = geneanetConverter.parseDocument(inputDocument, url);
            return person;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la recherche de l'url " + url);
        }
        return null;
    }


    public static void main2(String[] args) {
        try {
            GeneanetBrowser browser = new GeneanetBrowser();
            Document doc = browser.connect("https://gw.geneanet.org/slebruman?lang=fr&iz=6847&p=georges+auguste+louis&n=leroy");
            String pattern = "/html/body/div/div/div/div[5]/div/div/div/div/div/div/div/div/div/div/div[2]/div/div/ul/li/text()";
            String result = Xsoup.compile(pattern).evaluate(doc).get();
            //String result = browser.findXPath(doc,"Georges Auguste Louis LEROY");
            System.out.println(result);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            String url = "https://gw.geneanet.org/slebruman?lang=fr&iz=6847&p=georges+auguste+louis&n=leroy";
            GeneanetBrowser browser = new GeneanetBrowser();
            //Document doc = browser.connect(url);
            GeneanetPerson person = browser.search(url);
            System.out.println(person);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main0(String[] args) {
        try {
            String regex = "(.*?),.*";
            String birth = "Né le 11 février 1855 (dimanche) - Nehou, , Manche, , France";
            System.out.println(birth);
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(birth);
            System.out.println(matcher.find());
            String result = matcher.group(1);
            System.out.println(result);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
