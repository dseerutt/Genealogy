package Genealogy.URLConnexion.Geneanet;

import Genealogy.Model.Date.FullDate;
import Genealogy.Model.Date.MonthDate;
import Genealogy.Model.Date.MyDate;
import Genealogy.Model.Date.YearDate;
import fr.followthecode.republican.date.utils.RepublicanCalendarDateConverter;
import org.apache.commons.lang.StringUtils;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import us.codecraft.xsoup.Xsoup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static Genealogy.URLConnexion.Geneanet.GeneanetBrowser.logger;
import static Genealogy.URLConnexion.Geneanet.GeneanetConverter.ActType.*;

/**
 * Created by Dan on 07/11/2017.
 */
public class GeneanetConverter {

    private static String XpathGender;
    private static String XpathFirstName;
    private static String XpathFamilyName;
    private static String XpathBirthAndDeath;
    private static String XpathFather;
    private static String XpathMother;
    private static String XpathFamily;
    private static String XpathSection;
    private static String geneanetSearchURL;
    private static String XpathMarriageDate;
    private static String XpathMarriagePartner;
    private static String XpathBrother;
    private static String XpathHalfBrother;
    private static String XpathChildren;
    private static String XpathUrl;
    private static ArrayList<String> geneanetTrees;
    private Document doc;
    private final static Character space = (char) 160;
    private static ArrayList<String> wrongCities;

    public GeneanetConverter(Document document){
        doc = document;
        initWrongCities();
    }

    private void initWrongCities() {
        wrongCities = new ArrayList<>();
        wrongCities.add("le bourg");
        wrongCities.add("la Croix-Lambert");
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

    public static String getXpathBirthAndDeath() {
        return XpathBirthAndDeath;
    }

    public static void setXpathBirthAndDeath(String xpathBirthAndDeath) {
        XpathBirthAndDeath = xpathBirthAndDeath;
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

    public static ArrayList<String> getGeneanetTrees() {
        return geneanetTrees;
    }

    public static void setGeneanetTrees(ArrayList<String> geneanetTrees) {
        GeneanetConverter.geneanetTrees = geneanetTrees;
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

    public static String getXpathChildren() {
        return XpathChildren;
    }

    public static void setXpathChildren(String xpathChildren) {
        XpathChildren = xpathChildren;
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

    public int setChristening(GeneanetPerson person, int index){
        return index;
    }

    public int setBurial(GeneanetPerson person, int index){
        return index;
    }

    public enum ActType {
        BIRTH,
        CHRISTENING,
        DEATH,
        BURIAL;
    }

    public int setPersonDates(GeneanetPerson person, int index, ActType act){
        String regex = "(.*?)$|,.*";
        String birth = Xsoup.compile(XpathBirthAndDeath.replace("XXX","" + index)).evaluate(doc).get();
        if (birth == null){
            return index;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(birth);
        if (matcher != null && matcher.find()){
            String dateAndCity = matcher.group(1);
            if (dateAndCity.contains("Né")||dateAndCity.contains("Baptisé")||dateAndCity.contains("Décédé")||dateAndCity.contains("Inhumé")) {
                String[] tab = dateAndCity.split(" - ");
                String date = null;
                String city = null;
                if (tab != null && tab.length > 1){
                    date = tab[0];
                    String cityTmp = tab[1];
                    String[] cityTab = cityTmp.split(",");
                    if (cityTab != null && cityTab.length > 0){
                        city = cityTab[0];
                    }
                } else if (tab != null && tab.length == 1){
                    date = tab[0];
                }
                if (date != null || city != null){
                    switch (act) {
                        case BIRTH:
                            if (date.contains("Né")){
                                person.setBirthDate(parseDateWithSex(date,person.getGender(),"Né le "));
                                person.setPlaceOfBirth(city);
                                index++;
                            }
                            break;
                        case CHRISTENING:
                            if (date.contains("Baptisé")) {
                                person.setChristeningDate(parseDateWithSex(date, person.getGender(), "Baptisé le "));
                                person.setPlaceOfChristening(city);
                                index++;
                            }
                            break;
                        case DEATH:
                            if (date.contains("Décédé")) {
                                person.setDeathDate(parseDateWithSex(date,person.getGender(),"Décédé le "));
                                person.setPlaceOfDeath(city);
                                index++;
                            }
                            break;
                        case BURIAL:
                            if (date.contains("Inhumé")) {
                                person.setBurialDate(parseDateWithSex(date,person.getGender(),"Inhumé le "));
                                person.setPlaceOfBurial(city);
                                index++;
                            }
                            break;
                        default:
                            logger.error("Type acte Geneanet non trouvé");
                            break;
                    }
                }
            }
        }
        return index;
    }

    public MyDate parseDateWithSex(String input, Gender gender, String text){
        if (gender == Gender.Female){
            return parseDate(input,text.replace("é ","ée "));
        }
        return parseDate(input,text);
    }

    public MyDate parseMarriageDate(String input, Gender gender){
        return parseDate(input,"le ");
    }

    public String parseMarriageCity(String input, Gender gender){
        String[] temptab = input.split(", ");
        if (temptab.length > 1){
            return temptab[1];
        }
        return null;
    }

    public static int compareString(String s1, String s2){
        return StringUtils.indexOfDifference(s1,s2);
    }

    public MyDate parseDate(String input, String regex){

        //Remove the day surrounded by parenthesis
        String[] parenthesisTab = input.split(" \\(");
        String resultDate = parenthesisTab[0];

        //gestion des er
        resultDate = resultDate.replace("1er","1");

        //gestion des espaces
        resultDate = resultDate.replace(space,' ');

        //gestion des en
        resultDate = resultDate.replace("en ","le ");

        //remove Né le
        regex = regex.replace(space,' ');
        String[] dateTab = resultDate.split(regex);
        if (dateTab.length != 1){
            resultDate = dateTab[1];
        }
        return parseDate(resultDate);
    }

    private MyDate parseDate(String inputDate) {
        SimpleDateFormat dateFormatFullMonth = new SimpleDateFormat("dd MMMM yyyy", Locale.FRANCE);
        SimpleDateFormat dateFormatFullMonthOnly = new SimpleDateFormat("MMMM yyyy", Locale.FRANCE);
        dateFormatFullMonthOnly.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date date0 = dateFormatFullMonth.parse(inputDate);
            return new FullDate(date0);
        } catch (ParseException e) {
        }
        try {
            Date date0 = dateFormatFullMonthOnly.parse(inputDate);
            LocalDate localDate = date0.toInstant().atZone(ZoneOffset.UTC).toLocalDate();
            int year  = localDate.getYear();
            int month = localDate.getMonthValue();
            return new MonthDate(year,month);
        } catch (ParseException e) {
        }

        if (inputDate.length() == 4){
            int year  = Integer.parseInt(inputDate);
            return new YearDate(year);
        } else {
            return parseRepublicanDate(inputDate);
        }
    }

    private MyDate parseRepublicanDate(String inputDate){
        RepublicanCalendarDateConverter converter = RepublicanCalendarDateConverter.getConverter();
        Date date = converter.convertAsDate(inputDate);
        return new FullDate(date);
    }

    public void setFamily(int index, GeneanetPerson person){
        String category = Xsoup.compile(XpathSection.replace("XXX","" + index)).evaluate(doc).get();
        if (category != null && category.contains("Union(s)")){
            setMarriageAndChildren(index+1, person);
            index++;
        }
        category = Xsoup.compile(XpathSection.replace("XXX","" + index)).evaluate(doc).get();
        if (category != null && (category.equals("Fratrie")||category.equals("Frères et sœurs"))){
            setBrotherhood(index+1, person);
            index++;
        } else if (category != null && category.equals("Demi-frères et demi-sœurs")) {
            setHalfBrotherhood(person);
            index++;
        }
    }

    private void setBrotherhood(int index, GeneanetPerson person) {
        int siblingNumber = 1;
        String personString;
        do {
            personString = Xsoup.compile(XpathFamily.replace("XXX","" + index) + XpathBrother.replace("XXX","" + siblingNumber)).evaluate(doc).get();
            if (personString != null&&!(geneanetSearchURL + personString).equals(person.getGeneanetUrl())){
                GeneanetPerson sibling = new GeneanetPerson(geneanetSearchURL + personString);
                person.addSibling(sibling);
            }
            siblingNumber++;
        } while (personString != null);
    }

    private void setHalfBrotherhood(GeneanetPerson person) {
        int siblingNumber = 1;
        int siblingBranch = 1;
        boolean exit = false;
        String personString;
        do {
            do {
                personString = Xsoup.compile(XpathHalfBrother.replace("XXX","" + siblingBranch).replace("YYY","" + siblingNumber)).evaluate(doc).get();
                if (personString != null &&!(geneanetSearchURL + personString).equals(person.getUrl())){
                    GeneanetPerson halfSibling = new GeneanetPerson(geneanetSearchURL + personString);
                    person.addHalfSibling(halfSibling);
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

    private void setMarriageAndChildren(int index, GeneanetPerson person) {
        //Marriage
        int partnerNumber = 1;
        int aNumber = 1;
        MyDate date = null;
        String city = null;
        String dateAndCity = Xsoup.compile(XpathFamily.replace("XXX","" + index) + XpathMarriageDate.replace("XXX","" + partnerNumber)).evaluate(doc).get();
        String personString = Xsoup.compile(XpathFamily.replace("XXX","" + index) + XpathMarriagePartner.replace("XXX","" + partnerNumber).replace("YYY","" + aNumber)).evaluate(doc).get();

        while (dateAndCity != null && personString != null) {
            if (!personString.contains("&p=")&&!personString.contains("&i=")){
                aNumber++;
                personString = Xsoup.compile(XpathFamily.replace("XXX","" + index) + XpathMarriagePartner.replace("XXX","" + partnerNumber).replace("YYY","" + aNumber)).evaluate(doc).get();
                aNumber = 1;
            }
            //unknown date case
            if (!dateAndCity.contains("avant")&&!dateAndCity.contains("après")){
                date = parseMarriageDate(dateAndCity, person.getGender());
            }
            city = parseMarriageCity(dateAndCity, person.getGender());
            person.addMarriage(geneanetSearchURL + personString,date,city);

            //Enfants
            int children = 1;
            String child = Xsoup.compile(XpathFamily.replace("XXX","" + index) + XpathChildren.replace("XXX","" + partnerNumber).replace("YYY","" + aNumber).replace("ZZZ","" + children)).evaluate(doc).get();
            while (child != null){
                if (!child.contains("&p=")){
                    aNumber++;
                    child = Xsoup.compile(XpathFamily.replace("XXX","" + index) + XpathChildren.replace("XXX","" + partnerNumber).replace("YYY","" + aNumber).replace("ZZZ","" + children)).evaluate(doc).get();
                }
                GeneanetPerson childPerson = new GeneanetPerson(geneanetSearchURL + child);
                person.addChild(childPerson);
                children++;
                aNumber = 1;
                child = Xsoup.compile(XpathFamily.replace("XXX","" + index) + XpathChildren.replace("XXX","" + partnerNumber).replace("YYY","" + aNumber).replace("ZZZ","" + children)).evaluate(doc).get();
            }
            partnerNumber++;
            dateAndCity = Xsoup.compile(XpathFamily.replace("XXX","" + index) + XpathMarriageDate.replace("XXX","" + partnerNumber)).evaluate(doc).get();
            personString = Xsoup.compile(XpathFamily.replace("XXX","" + index) + XpathMarriagePartner.replace("XXX","" + partnerNumber).replace("YYY","" + aNumber)).evaluate(doc).get();
        }
    }

    public void parseDocument(Document document, GeneanetPerson person){
        doc = document;
        String firstName = getFirstName(doc);
        String name = getName(doc);
        person.setFirstName(firstName);
        person.setFamilyName(name);
        setGender(person);
        setGeneanetUrl(person);
        int index = setPersonDates(person,1,BIRTH);
        index = setPersonDates(person,index,CHRISTENING);
        index = setPersonDates(person,index,DEATH);
        setPersonDates(person,index,BURIAL);
        int offset = setParents(person);
        setFamily(2 + offset,person);
        person.setSearched(true);
    }

    private int setParents(GeneanetPerson person){
        String category = Xsoup.compile(XpathSection.replace("XXX","" + 1)).evaluate(doc).get();
        if (category != null && category.equals("Parents")){
            setFather(person);
            setMother(person);
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * Search Geneanet person special url
     * if does not exist, the person does not appear on brothers list (nominal)
     * @param person
     */
    private void setGeneanetUrl(GeneanetPerson person) {
        String url = Xsoup.compile(XpathUrl).evaluate(doc).get();
        if (url != null){
            person.setGeneanetUrl(geneanetSearchURL + url);
        }
    }

    private void setGender(GeneanetPerson person) {
        String gender = Xsoup.compile(XpathGender).evaluate(doc).get();
        person.setGender(Gender.getGender(gender));
    }

    private boolean setMother(GeneanetPerson person) {
        int nbmother = 2;
        String xpath =(XpathFamily.replace("XXX","2") + XpathMother.replace("XXX",nbmother + ""));
        if (person.getGeneanetUrl() == null){
            nbmother--;
            xpath =(XpathFamily.replace("XXX","2") + XpathMother.replace("XXX",nbmother + ""));
        }
        String motherURL = Xsoup.compile(xpath).evaluate(doc).get();
        if (motherURL != null && motherURL.contains("i1=")){
            nbmother++;
            xpath =(XpathFamily.replace("XXX","2") + XpathMother.replace("XXX",nbmother + ""));
            motherURL = Xsoup.compile(xpath).evaluate(doc).get();
        }
        if (motherURL == null ){
            nbmother--;
            xpath =(XpathFamily.replace("XXX","2") + XpathMother.replace("XXX",nbmother + ""));
            motherURL = Xsoup.compile(xpath).evaluate(doc).get();
        }
        if (motherURL != null){
            GeneanetPerson mother = new GeneanetPerson(geneanetSearchURL + motherURL);
            person.setMother(mother);
        }
        return person.getMother() != null;
    }

    private boolean setFather(GeneanetPerson person) {
        int nbfather = 2;
        String xpath =(XpathFamily.replace("XXX","2") + XpathFather.replace("XXX",nbfather + ""));
        if (person.getGeneanetUrl() == null){
            nbfather--;
            xpath =(XpathFamily.replace("XXX","2") + XpathFather.replace("XXX",nbfather + ""));
        }
        String fatherURL = Xsoup.compile(xpath).evaluate(doc).get();
        if (fatherURL != null && fatherURL.contains("i1=")){
            nbfather++;
            xpath =(XpathFamily.replace("XXX","2") + XpathFather.replace("XXX",nbfather + ""));
            fatherURL = Xsoup.compile(xpath).evaluate(doc).get();
        }
        if (fatherURL == null ){
            nbfather--;
            xpath =(XpathFamily.replace("XXX","2") + XpathFather.replace("XXX",nbfather + ""));
            fatherURL = Xsoup.compile(xpath).evaluate(doc).get();
        }
        if (fatherURL != null){
            GeneanetPerson father = new GeneanetPerson(geneanetSearchURL + fatherURL);
            person.setFather(father);
        }
        return person.getFather() != null;
    }

}
