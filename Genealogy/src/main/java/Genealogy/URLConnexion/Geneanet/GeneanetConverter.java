package Genealogy.URLConnexion.Geneanet;

import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import us.codecraft.xsoup.Xsoup;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dan on 07/11/2017.
 */
public class GeneanetConverter {

    private static String XpathFirstName;
    private static String XpathFamilyName;
    private static String XpathBirth;
    private static String XpathDeath;
    private static String XpathFather;
    private static String XpathMother;
    private static String XpathWeddingAndChildren;
    private static String XpathBrotherhood;

    public static String getXpathFirstName() {
        return XpathFirstName;
    }

    public static void setXpathFirstName(String xpathFirstName) {
        XpathFirstName = xpathFirstName;
    }

    public static String getXpathFamilyName() {
        return XpathFamilyName;
    }

    public static void setXpathFamilyName(String xpathFamilyName) {
        XpathFamilyName = xpathFamilyName;
    }

    public static String getXpathBirth() {
        return XpathBirth;
    }

    public static void setXpathBirth(String xpathBirth) {
        XpathBirth = xpathBirth;
    }

    public static String getXpathDeath() {
        return XpathDeath;
    }

    public static void setXpathDeath(String xpathDeath) {
        XpathDeath = xpathDeath;
    }

    public static String getXpathFather() {
        return XpathFather;
    }

    public static void setXpathFather(String xpathFather) {
        XpathFather = xpathFather;
    }

    public static String getXpathMother() {
        return XpathMother;
    }

    public static void setXpathMother(String xpathMother) {
        XpathMother = xpathMother;
    }

    public static String getXpathWeddingAndChildren() {
        return XpathWeddingAndChildren;
    }

    public static void setXpathWeddingAndChildren(String xpathWeddingAndChildren) {
        XpathWeddingAndChildren = xpathWeddingAndChildren;
    }

    public static String getXpathBrotherhood() {
        return XpathBrotherhood;
    }

    public static void setXpathBrotherhood(String xpathBrotherhood) {
        XpathBrotherhood = xpathBrotherhood;
    }

    public String getFirstName(Document doc){
        String firstName = Xsoup.compile(XpathFirstName).evaluate(doc).get();
        if (!StringUtil.isBlank(firstName)){
            return firstName;
        }
        return null;
    }

    public String getName(Document doc){
        String name = Xsoup.compile(XpathFamilyName).evaluate(doc).get();
        if (!StringUtil.isBlank(name)){
            return name;
        }
        return null;
    }

    public void setBirth(Document doc){
        String regex = "(.*?),.*";
        String birth = Xsoup.compile(XpathBirth).evaluate(doc).get();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(birth);
        if (matcher != null && matcher.find()){
            String dateAndCity = matcher.group(1);
            String[] tab = dateAndCity.split(" - ");
            if (tab != null && tab.length > 1){
                String date = tab[0];
                String city = tab[1];
                System.out.println(date);
                System.out.println(city);
            }
        }
    }

    public GeneanetPerson parseDocument(Document doc, String url){
        String firstName = getFirstName(doc);
        String name = getName(doc);
        GeneanetPerson person = new GeneanetPerson(url,firstName,name);
        setBirth(doc);
        return person;
    }

}
