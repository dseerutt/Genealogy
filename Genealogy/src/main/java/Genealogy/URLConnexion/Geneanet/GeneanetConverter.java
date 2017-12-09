package Genealogy.URLConnexion.Geneanet;

import Genealogy.Model.Date.FullDate;
import Genealogy.Model.Date.MyDate;
import org.apache.commons.lang.StringUtils;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import us.codecraft.xsoup.Xsoup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dan on 07/11/2017.
 */
public class GeneanetConverter {

    private static String XpathGender;
    private static String XpathFirstName;
    private static String XpathFamilyName;
    private static String XpathBirth;
    private static String XpathDeath;
    private static String XpathFather;
    private static String XpathMother;
    private static String XpathFamily;
    private static String XpathSection;
    private static String geneanetSearchURL;
    private static String XpathMarriageDate;
    private static String XpathMarriagePartner;
    private static String XpathBrother;
    private static String XpathHalfBrother;
    private static String XpathUrl;
    private Document doc;
    private GeneanetPerson person;
    private final static Character space = (char) 160;

    public GeneanetConverter(Document document){
        doc = document;
    }

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

    public static String getXpathFamily() {
        return XpathFamily;
    }

    public static void setXpathFamily(String xpathFamily) {
        XpathFamily = xpathFamily;
    }

    public static String getXpathUrl() {
        return XpathUrl;
    }

    public static void setXpathUrl(String xpathUrl) {
        XpathUrl = xpathUrl;
    }

    public static void setXpathGender(String xpathGender) {
        XpathGender = xpathGender;
    }

    public static String getXpathGender() {
        return XpathGender;
    }

    public static String getGeneanetSearchURL() {
        return geneanetSearchURL;
    }

    public static void setGeneanetSearchURL(String GeneanetSearchURL) {
        geneanetSearchURL = GeneanetSearchURL;
    }

    public Document getDoc() {
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }

    public static String getXpathMarriageDate() {
        return XpathMarriageDate;
    }

    public static void setXpathMarriageDate(String xpathMarriageDate) {
        XpathMarriageDate = xpathMarriageDate;
    }

    public static String getXpathMarriagePartner() {
        return XpathMarriagePartner;
    }

    public static void setXpathMarriagePartner(String xpathMarriagePartner) {
        XpathMarriagePartner = xpathMarriagePartner;
    }

    public static String getXpathBrother() {
        return XpathBrother;
    }

    public static void setXpathBrother(String xpathBrother) {
        XpathBrother = xpathBrother;
    }

    public static String getXpathHalfBrother() {
        return XpathHalfBrother;
    }

    public static void setXpathHalfBrother(String xpathHalfBrother) {
        XpathHalfBrother = xpathHalfBrother;
    }

    public static String getXpathSection() {
        return XpathSection;
    }

    public static void setXpathSection(String xpathSection) {
        XpathSection = xpathSection;
    }

    public GeneanetPerson getPerson() {
        return person;
    }

    public void setPerson(GeneanetPerson person) {
        this.person = person;
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

    public void setBirth(){
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
                person.setPlaceOfBirth(city);
                person.setBirthDate(parseBirthDate(date,person.getGender()));
            }
        }
    }

    public void setDeath(){
        String regex = "(.*?),.*";
        String death = Xsoup.compile(XpathDeath).evaluate(doc).get();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(death);
        if (matcher != null && matcher.find()){
            String dateAndCity = matcher.group(1);
            String[] tab = dateAndCity.split(" - ");
            if (tab != null && tab.length > 1){
                String date = tab[0];
                String city = tab[1];
                person.setPlaceOfDeath(city);
                person.setDeathDate(parseDeathDate(date,person.getGender()));
            }
        }
    }

    public MyDate parseBirthDate(String input, Gender gender){
        if (gender == Gender.Female){
            return parseDate(input,"Née le ");
        }
        return parseDate(input,"Né le ");
    }

    public MyDate parseDeathDate(String input, Gender gender){
        if (gender == Gender.Female){
            return parseDate(input,"Décédée le ");
        }
        return parseDate(input,"Décédé le ");
    }

    public static int compareString(String s1, String s2){
        return StringUtils.indexOfDifference(s1,s2);
    }

    public MyDate parseDate(String input, String regex){

        //Remove the day surrounded by parenthesis
        String[] parenthesisTab = input.split(" \\(");
        String resultDate = parenthesisTab[0];

        //gestion des espaces
        resultDate = resultDate.replace(space,' ');

        //remove Né le
        regex = regex.replace(space,' ');
        String[] dateTab = resultDate.split(regex);
        if (dateTab.length != 1){
            resultDate = dateTab[1];
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.FRANCE);
        //SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE dd MMM yyyy", Locale.FRANCE);
        try {
            Date date0 = dateFormat.parse(resultDate);
            return new FullDate(date0);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setFamily(){
        int index = 2;
        String category = Xsoup.compile(XpathSection.replace("XXX","" + index)).evaluate(doc).get();
        if (category.equals("Union(s) et enfant(s)")){
            setMarriageAndChildren(index+1);
            index++;
        }
        category = Xsoup.compile(XpathSection.replace("XXX","" + index)).evaluate(doc).get();
        if (category.equals("Fratrie")){
            setBrotherhood(index+1);
            index++;
        } else if (category.equals("Demi-frères et demi-sœurs")) {
            setHalfBrotherhood();
            index++;
        }

        category = Xsoup.compile(XpathSection.replace("XXX","" + index)).evaluate(doc).get();
        if (category.equals("Fratrie")){
            setBrotherhood(index+1);
            index++;
        } else if (category.equals("Demi-frères et demi-sœurs")) {
            setHalfBrotherhood();
            index++;
        }
    }

    private void setBrotherhood(int index) {
        int siblingNumber = 1;
        String personString;
        do {
            personString = Xsoup.compile(XpathFamily.replace("XXX","" + index) + XpathBrother.replace("XXX","" + siblingNumber)).evaluate(doc).get();
            if (personString != null&&!personString.equals(person.getUrl())){
                person.addSibling(personString);
            }
            siblingNumber++;
        } while (personString != null);
    }

    private void setHalfBrotherhood() {
        int siblingNumber = 1;
        int siblingBranch = 1;
        boolean exit = false;
        String personString;
        do {
            do {
                personString = Xsoup.compile(XpathHalfBrother.replace("XXX","" + siblingBranch).replace("YYY","" + siblingNumber)).evaluate(doc).get();
                if (personString != null&&!personString.equals(person.getUrl())){
                    person.addHalfSibling(personString);
                } else if (siblingNumber == 1) {
                    exit = true;
                }
                siblingNumber++;
            } while (personString != null);
            siblingBranch++;
            siblingNumber = 1;
            personString = "";
        } while (!exit&&personString != null);
    }

    private void setMarriageAndChildren(int index) {
        int wifeNumber = 1;
        String dateAndCity;
        String person;
        do {
            dateAndCity = Xsoup.compile(XpathFamily.replace("XXX","" + index) + XpathMarriageDate.replace("XXX","" + wifeNumber)).evaluate(doc).get();
            person = Xsoup.compile(XpathFamily.replace("XXX","" + index) + XpathMarriagePartner.replace("XXX","" + wifeNumber)).evaluate(doc).get();
            wifeNumber++;
        } while (dateAndCity != null && person != null);
    }

    public void parseDocument(Document document, String url){
        doc = document;
        String firstName = getFirstName(doc);
        String name = getName(doc);
        person = new GeneanetPerson(url,firstName,name);
        setGender();
        setBirth();
        setDeath();
        setFather();
        setMother();
        setUrl();
        setFamily();
    }

    private void setUrl() {
        String url = Xsoup.compile(XpathUrl).evaluate(doc).get();
        person.setUrl(url);
    }

    private void setGender() {
        String gender = Xsoup.compile(XpathGender).evaluate(doc).get();
        person.setGender(Gender.getGender(gender));
    }

    private boolean setMother() {
        String xpath =(XpathFamily + XpathMother).replace("XXX","2");
        String motherURL = Xsoup.compile(xpath).evaluate(doc).get();
        person.setMother(geneanetSearchURL + motherURL);
        return person.getMother() != null;
    }

    private boolean setFather() {
        String xpath =(XpathFamily + XpathFather).replace("XXX","2");
        String fatherURL = Xsoup.compile(xpath).evaluate(doc).get();
        person.setFather(geneanetSearchURL + fatherURL);
        return person.getFather() != null;
    }

}
