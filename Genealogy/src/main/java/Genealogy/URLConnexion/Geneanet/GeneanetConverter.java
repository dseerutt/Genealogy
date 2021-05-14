package Genealogy.URLConnexion.Geneanet;

import Genealogy.Model.Date.FullDate;
import Genealogy.Model.Date.MonthDate;
import Genealogy.Model.Date.MyDate;
import Genealogy.Model.Date.RepublicanDate.RepublicanCalendarDateConverter;
import Genealogy.Model.Date.YearDate;
import Genealogy.Model.Exception.RepublicanDateOutOfRangeException;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import us.codecraft.xsoup.Xsoup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static Genealogy.URLConnexion.Geneanet.GeneanetBrowser.logger;
import static Genealogy.URLConnexion.Geneanet.GeneanetConverter.ActType.*;

/**
 * Created by Dan on 07/11/2017.
 */
public class GeneanetConverter {

    private static final String REGEX_TRAILING_NUMBERS = "(.*) [0-9]+";
    private static String XpathGender;
    private static String XpathGender2;
    private static String XpathGender3;
    private static String XpathNames;
    private static String XpathNames2;
    private static String XpathNames3;
    private static String XpathNames4;
    private static String XpathNames5;
    private static String XpathBirthAndDeath;
    private static String XpathFather;
    private static String XpathMother;
    private static String XpathParents2;
    private static String XpathFamily;
    private static String XpathFamily2;
    private static String XpathFamily3;
    private static String XpathSection;
    private static String XpathSection2;
    private static String geneanetSearchURL;
    private static String XpathMarriageDate;
    private static String XpathMarriageDate2;
    private static String XpathMarriagePartner;
    private static String XpathMarriagePartner2;
    private static String XpathBrother;
    private static String XpathBrother2;
    private static String XpathHalfBrother;
    private static String XpathChildren;
    private static String XpathUrl;
    private static String XpathImage;
    private static String XpathImage2;
    private static String XpathImage3;
    private Document doc;
    private final static Character space = (char) 160;
    private final String REGEX_PERSON_DATES = "(.*?)$|,.*";
    private final SimpleDateFormat dateFormatFullMonth = new SimpleDateFormat("dd MMMM yyyy", Locale.FRANCE);
    private final SimpleDateFormat dateFormatFullMonthOnly = new SimpleDateFormat("MMMM yyyy", Locale.FRANCE);

    public GeneanetConverter(Document document) throws Exception {
        doc = document;
    }

    public GeneanetConverter() throws Exception {
    }

    public static String getXpathNames() {
        return XpathNames;
    }

    public static void setXpathNames(String xpathNames) {
        XpathNames = xpathNames;
    }

    public static String getXpathNames2() {
        return XpathNames2;
    }

    public static void setXpathNames2(String xpathNames2) {
        XpathNames2 = xpathNames2;
    }

    public static String getXpathGender2() {
        return XpathGender2;
    }

    public static void setXpathGender2(String xpathGender2) {
        XpathGender2 = xpathGender2;
    }

    public static String getXpathGender3() {
        return XpathGender3;
    }

    public static void setXpathGender3(String xpathGender3) {
        XpathGender3 = xpathGender3;
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

    public static String getXpathParents2() {
        return XpathParents2;
    }

    public static void setXpathParents2(String xpathParents2) {
        XpathParents2 = xpathParents2;
    }

    public static String getXpathImage() {
        return XpathImage;
    }

    public static void setXpathImage(String xpathImage) {
        XpathImage = xpathImage;
    }

    public static String getXpathImage2() {
        return XpathImage2;
    }

    public static void setXpathImage2(String xpathImage2) {
        XpathImage2 = xpathImage2;
    }

    public static String getXpathImage3() {
        return XpathImage3;
    }

    public static void setXpathImage3(String xpathImage3) {
        XpathImage3 = xpathImage3;
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

    public static String getXpathFamily2() {
        return XpathFamily2;
    }

    public static void setXpathFamily2(String xpathFamily2) {
        XpathFamily2 = xpathFamily2;
    }

    public static String getXpathFamily3() {
        return XpathFamily3;
    }

    public static void setXpathFamily3(String xpathFamily3) {
        XpathFamily3 = xpathFamily3;
    }

    public static String getGeneanetSearchURL() {
        return geneanetSearchURL;
    }

    public static void setGeneanetSearchURL(String GeneanetSearchURL) {
        geneanetSearchURL = GeneanetSearchURL;
    }

    public static String getXpathNames3() {
        return XpathNames3;
    }

    public static void setXpathNames3(String xpathNames3) {
        XpathNames3 = xpathNames3;
    }

    public static String getXpathNames4() {
        return XpathNames4;
    }

    public static void setXpathNames4(String xpathNames4) {
        XpathNames4 = xpathNames4;
    }

    public static String getXpathNames5() {
        return XpathNames5;
    }

    public static void setXpathNames5(String xpathNames5) {
        XpathNames5 = xpathNames5;
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

    public static String getXpathMarriageDate2() {
        return XpathMarriageDate2;
    }

    public static void setXpathMarriageDate2(String xpathMarriageDate2) {
        XpathMarriageDate2 = xpathMarriageDate2;
    }

    public static String getXpathMarriagePartner2() {
        return XpathMarriagePartner2;
    }

    public static void setXpathMarriagePartner2(String xpathMarriagePartner2) {
        XpathMarriagePartner2 = xpathMarriagePartner2;
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

    public static String getXpathSection2() {
        return XpathSection2;
    }

    public static void setXpathSection2(String xpathSection2) {
        XpathSection2 = xpathSection2;
    }

    public static String getXpathBrother2() {
        return XpathBrother2;
    }

    public static void setXpathBrother2(String xpathBrother2) {
        XpathBrother2 = xpathBrother2;
    }

    public String getFirstName(Document doc) {
        String firstName = Xsoup.compile(XpathNames.replace("XXX", StringUtils.EMPTY + 1)).evaluate(doc).get();
        if (StringUtils.isBlank(firstName)) {
            firstName = Xsoup.compile(XpathNames2.replace("XXX", StringUtils.EMPTY + 1).replace("YYY", StringUtils.EMPTY + 1)).evaluate(doc).get();
            if (firstName != null) {
                //Integer case
                try {
                    Integer.parseInt(firstName.replace(" ", StringUtils.EMPTY));
                    firstName = Xsoup.compile(XpathNames2.replace("XXX", StringUtils.EMPTY + 1).replace("YYY", StringUtils.EMPTY + 2)).evaluate(doc).get();
                } catch (NumberFormatException e) {
                    //do nothing
                }
            } else {
                firstName = Xsoup.compile(XpathNames2.replace("XXX", StringUtils.EMPTY + 1).replace("YYY", StringUtils.EMPTY + 2)).evaluate(doc).get();
            }
        }
        if (StringUtils.isBlank(firstName)) {
            firstName = Xsoup.compile(XpathNames3.replace("XXX", StringUtils.EMPTY + 1)).evaluate(doc).get();
        }
        if (StringUtils.isBlank(firstName)) {
            firstName = Xsoup.compile(XpathNames4.replace("XXX", StringUtils.EMPTY + 1)).evaluate(doc).get();
        }
        if (StringUtils.isBlank(firstName)) {
            firstName = Xsoup.compile(XpathNames5.replace("XXX", StringUtils.EMPTY + 1)).evaluate(doc).get();
        }
        if (!StringUtils.isBlank(firstName)) {
            return firstName;
        }
        return null;
    }

    public String getName(Document doc) {
        String name = Xsoup.compile(XpathNames.replace("XXX", StringUtils.EMPTY + 2)).evaluate(doc).get();
        if (StringUtils.isBlank(name)) {
            name = Xsoup.compile(XpathNames2.replace("XXX", StringUtils.EMPTY + 2).replace("YYY", StringUtils.EMPTY + 2)).evaluate(doc).get();
            if (StringUtils.isBlank(name)) {
                name = Xsoup.compile(XpathNames2.replace("XXX", StringUtils.EMPTY + 2).replace("YYY", StringUtils.EMPTY + 1)).evaluate(doc).get();
            }
        }
        if (StringUtils.isBlank(name)) {
            name = Xsoup.compile(XpathNames3.replace("XXX", StringUtils.EMPTY + 2)).evaluate(doc).get();
        }
        if (StringUtils.isBlank(name)) {
            name = Xsoup.compile(XpathNames4.replace("XXX", StringUtils.EMPTY + 2)).evaluate(doc).get();
        }
        if (StringUtils.isBlank(name)) {
            name = Xsoup.compile(XpathNames5.replace("XXX", StringUtils.EMPTY + 2)).evaluate(doc).get();
        }
        if (!StringUtils.isBlank(name)) {
            return name;
        }
        return null;
    }

    public String getImage(Document doc) {
        String image = Xsoup.compile(XpathImage).evaluate(doc).get();
        if (image == null) {
            image = Xsoup.compile(XpathImage2).evaluate(doc).get();
            if (image == null) {
                image = Xsoup.compile(XpathImage3).evaluate(doc).get();
            }
        }
        return image;
    }

    public int setChristening(GeneanetPerson person, int index) {
        return index;
    }

    public int setBurial(GeneanetPerson person, int index) {
        return index;
    }

    public enum ActType {
        BIRTH,
        CHRISTENING,
        DEATH,
        BURIAL;
    }

    public int setPersonDates(GeneanetPerson person, int index, ActType act) {
        String birth = Xsoup.compile(XpathFamily.replace("XXX", StringUtils.EMPTY + 1) + XpathBirthAndDeath.replace("XXX", StringUtils.EMPTY + index)).evaluate(doc).get();
        if (birth == null || StringUtils.isEmpty(birth.replace(" ", StringUtils.EMPTY)) || (birth.contains("Marié") && birth.contains("avec"))) {
            birth = Xsoup.compile(XpathFamily2 + XpathBirthAndDeath.replace("XXX", StringUtils.EMPTY + index)).evaluate(doc).get();
            if (birth == null || StringUtils.isEmpty(birth.replace(" ", StringUtils.EMPTY)) || (birth.contains("Marié") && birth.contains("avec"))) {
                birth = Xsoup.compile(XpathFamily3 + XpathBirthAndDeath.replace("XXX", StringUtils.EMPTY + index)).evaluate(doc).get();
                if (birth == null || StringUtils.isEmpty(birth.replace(" ", StringUtils.EMPTY))) {
                    return index;
                } else {
                    person.setUsingDateTable(true);
                }
            }
        }
        Pattern pattern = Pattern.compile(REGEX_PERSON_DATES);
        Matcher matcher = pattern.matcher(birth);
        if (matcher != null && matcher.find()) {
            String dateAndCity = matcher.group(1);
            if (dateAndCity.contains("Né") || dateAndCity.contains("Baptisé") || dateAndCity.contains("Décédé") || dateAndCity.contains("Inhumé")) {
                String[] tab = dateAndCity.split(" - ");
                String date = null;
                String city = null;
                if (tab != null && tab.length > 1) {
                    date = tab[0];
                    String cityTmp = tab[1];
                    if (tab.length > 2 && (cityTmp.contains("Canton de") || (cityTmp.startsWith("Vue ")))) {
                        cityTmp = tab[2];
                    }
                    String[] cityTab = cityTmp.split(",");
                    if (cityTab != null && cityTab.length > 0) {
                        if (cityTab[0].contains("(")) {
                            String[] newcityTab = cityTab[0].split("\\(");
                            if (newcityTab != null && newcityTab.length > 0) {
                                city = removeTrailingNumbers(newcityTab[0]);
                            }
                        } else {
                            city = removeTrailingNumbers(cityTab[0]);
                        }
                    }
                } else if (tab != null && tab.length == 1) {
                    date = tab[0];
                }
                if (date != null || city != null) {
                    switch (act) {
                        case BIRTH:
                            if (date.contains("Né")) {
                                person.setBirthDate(parseDateWithSex(date, person.getGender(), "Né le "));
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
                                person.setDeathDate(parseDateWithSex(date, person.getGender(), "Décédé le "));
                                person.setPlaceOfDeath(city);
                                index++;
                            }
                            break;
                        case BURIAL:
                            if (date.contains("Inhumé")) {
                                person.setBurialDate(parseDateWithSex(date, person.getGender(), "Inhumé le "));
                                person.setPlaceOfBurial(city);
                                index++;
                            }
                            break;
                        default:
                            logger.error("Type acte Geneanet non trouvé");
                            break;
                    }
                }
            } else if (act.equals(BURIAL) && index == 1) {
                String document = doc.toString();
                String[] ulPart = document.split("<!-- Parents");
                if (ulPart != null && ulPart.length > 0 && ulPart[0].contains("<ul>")) {
                    return index + 1;
                }
                return index;
            }
        }
        return index;
    }

    public MyDate parseDateWithSex(String input, Gender gender, String text) {
        if (gender == Gender.Female) {
            return parseDate(input, text.replace("é ", "ée "));
        }
        return parseDate(input, text);
    }

    public MyDate parseMarriageDate(String input, Gender gender) {
        return parseDate(input, "le ");
    }

    /**
     * Fonction removeTrailingNumbers
     * Supprime les nombres à la fin d'un string
     *
     * @param inputString string en entree
     * @return le string sans ces nombres
     */
    public String removeTrailingNumbers(String inputString) {
        Pattern pattern = Pattern.compile(REGEX_TRAILING_NUMBERS);
        Matcher matcher = pattern.matcher(inputString);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return inputString;
    }

    public String parseMarriageCity(String input, Gender gender) {
        if (input != null) {
            String[] temptab = input.split(", ");
            if (temptab.length > 1) {
                String cityTmp = temptab[1];
                String newTemptab = cityTmp.split(",")[0];
                String[] newTab = newTemptab.split(" - ");
                if (newTab.length > 1 && (cityTmp.contains("Canton de") || newTemptab.startsWith("Vue "))) {
                    if (newTemptab.contains("acte du")) {
                        newTemptab = newTab[2];
                    } else {
                        newTemptab = newTab[1];
                    }
                }
                if (newTemptab.contains("(")) {
                    return removeTrailingNumbers(newTemptab.split("\\(")[0]);
                } else {
                    return removeTrailingNumbers(newTemptab);
                }
            }
        }
        return null;
    }

    public static int compareString(String s1, String s2) {
        return StringUtils.indexOfDifference(s1, s2);
    }

    public MyDate parseDate(String input, String regex) {

        //Remove the day surrounded by parenthesis
        String[] parenthesisTab = input.split(" \\(");
        String resultDate = parenthesisTab[0];

        //gestion des er
        resultDate = resultDate.replace("1er", "1");

        //gestion des espaces
        resultDate = resultDate.replace(space, ' ');

        //gestion des en
        resultDate = resultDate.replace("en ", "le ");

        //remove Né le
        regex = regex.replace(space, ' ');
        String[] dateTab = resultDate.split(regex);
        if (dateTab.length != 1) {
            resultDate = dateTab[1];
        }
        if (resultDate.contains(",")) {
            dateTab = resultDate.split(",");
            resultDate = dateTab[0];
        }
        try {
            return parseDate(resultDate);
        } catch (RepublicanDateOutOfRangeException e) {
            logger.error("Failed to parse Republican date " + input, e);
        }
        return null;
    }

    private MyDate parseDate(String inputDate) throws RepublicanDateOutOfRangeException {
        dateFormatFullMonthOnly.setTimeZone(TimeZone.getTimeZone("UTC"));
        dateFormatFullMonth.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date date0 = dateFormatFullMonth.parse(inputDate);
            return new FullDate(date0);
        } catch (ParseException e) {
        }
        try {
            Date date0 = dateFormatFullMonthOnly.parse(inputDate);
            LocalDate localDate = date0.toInstant().atZone(ZoneOffset.UTC).toLocalDate();
            int year = localDate.getYear();
            int month = localDate.getMonthValue();
            return new MonthDate(month, year);
        } catch (ParseException e) {
        }

        if (inputDate.length() == 4) {
            int year = Integer.parseInt(inputDate);
            return new YearDate(year);
        } else {
            return parseRepublicanDate(inputDate);
        }
    }

    /**
     * Function parseRepublicanDate : convert input republican date to MyDate
     *
     * @param inputDate
     * @return
     * @throws RepublicanDateOutOfRangeException if the date is out of the range of the RepublicanCalendar
     */
    protected MyDate parseRepublicanDate(String inputDate) throws RepublicanDateOutOfRangeException {
        RepublicanCalendarDateConverter converter = RepublicanCalendarDateConverter.getConverter();
        LocalDate date = converter.convertAsLocalDate(inputDate);
        if (date == null) {
            return null;
        } else {
            return new FullDate(date);
        }
    }

    public void setFamily(int index, GeneanetPerson person) {
        int sectionIndex = 0;
        String category;
        do {
            sectionIndex++;
            category = Xsoup.compile(XpathSection.replace("XXX", StringUtils.EMPTY + sectionIndex)).evaluate(doc).get();
            if (category == null) {
                category = Xsoup.compile(XpathSection2.replace("XXX", StringUtils.EMPTY + sectionIndex)).evaluate(doc).get();
            }
            if (category != null) {
                category = category.replaceAll(" ", StringUtils.EMPTY);
                if (category.contains("Union(s)")) {
                    setMarriageAndChildren(index, person);
                    index++;
                } else if ((category.equals("Fratrie") || category.equals("Frèresetsœurs"))) {
                    setBrotherhood(index, person);
                    index++;
                } else if (category.equals("Demi-frèresetdemi-sœurs")) {
                    setHalfBrotherhood(person);
                    index++;
                }
            }
        } while (category != null);
    }

    private void setBrotherhood(int index, GeneanetPerson person) {
        int siblingNumber = 1;
        String personString;
        do {
            personString = Xsoup.compile(XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathBrother.replace("XXX", StringUtils.EMPTY + siblingNumber).replace("YYY", StringUtils.EMPTY + 1)).evaluate(doc).get();
            if (personString != null && !(geneanetSearchURL + personString).equals(person.getGeneanetUrl()) && personString.contains("i1=")) {
                personString = Xsoup.compile(XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathBrother.replace("XXX", StringUtils.EMPTY + siblingNumber).replace("YYY", StringUtils.EMPTY + 2)).evaluate(doc).get();
            }
            String isThisMe = Xsoup.compile(XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathBrother2.replace("XXX", StringUtils.EMPTY + siblingNumber)).evaluate(doc).get();
            if (personString == null) {
                personString = isThisMe;
            } else if (personString != null && !(geneanetSearchURL + personString).equals(person.getGeneanetUrl()) && !personString.contains("i1=") && !(person.getFullName()).equals(isThisMe)) {
                GeneanetPerson sibling = new GeneanetPerson(geneanetSearchURL + personString);
                person.addSibling(sibling);
            }
            siblingNumber++;
        } while (personString != null);
    }

    private void setHalfBrotherhood(GeneanetPerson person) {
        int siblingNumber = 1;
        int siblingBranch = 1;
        int parentNumber = 1;
        boolean exitLoop1;
        boolean exitLoop2;
        String personString;
        do {
            exitLoop1 = false;
            exitLoop2 = false;
            do {
                do {
                    personString = Xsoup.compile(XpathHalfBrother.replace("XXX", StringUtils.EMPTY + siblingBranch).replace("YYY", StringUtils.EMPTY + siblingNumber).replace("ZZZ", StringUtils.EMPTY + parentNumber).replace("WWW", StringUtils.EMPTY + 1)).evaluate(doc).get();
                    if (personString != null && personString.contains("&i1=")) {
                        personString = Xsoup.compile(XpathHalfBrother.replace("XXX", StringUtils.EMPTY + siblingBranch).replace("YYY", StringUtils.EMPTY + siblingNumber).replace("ZZZ", StringUtils.EMPTY + parentNumber).replace("WWW", StringUtils.EMPTY + 2)).evaluate(doc).get();
                    }

                    if (personString != null && !(geneanetSearchURL + personString).equals(person.getGeneanetUrl())) {
                        GeneanetPerson halfSibling = new GeneanetPerson(geneanetSearchURL + personString);
                        person.addHalfSibling(halfSibling);
                    } else if (siblingNumber == 1) {
                        exitLoop1 = true;
                    }
                    siblingNumber++;
                } while (personString != null);
                siblingBranch++;
                siblingNumber = 1;
                personString = StringUtils.EMPTY;
            } while (!exitLoop1 && personString != null);
            parentNumber++;
            siblingNumber = 1;
            siblingBranch = 1;
            personString = StringUtils.EMPTY;
            if (parentNumber > 2) {
                exitLoop2 = true;
            }
        } while (!exitLoop2 && personString != null);
    }

    private void setMarriageAndChildren(int index, GeneanetPerson person) {
        //Marriage
        int partnerNumber = 1;
        int aNumber = 1;
        MyDate date = null;
        String city = null;
        String dateAndCity = Xsoup.compile(XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathMarriageDate.replace("XXX", StringUtils.EMPTY + partnerNumber)).evaluate(doc).get();
        String personString = Xsoup.compile(XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathMarriagePartner.replace("XXX", StringUtils.EMPTY + partnerNumber).replace("YYY", StringUtils.EMPTY + aNumber)).evaluate(doc).get();
        String tmpXpathMarriagePartner = XpathMarriagePartner;
        String tmpXpathMarriageDate = XpathMarriageDate;

        if (dateAndCity == null && personString == null) {
            dateAndCity = Xsoup.compile(XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathMarriageDate2.replace("XXX", StringUtils.EMPTY + partnerNumber)).evaluate(doc).get();
            personString = Xsoup.compile(XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathMarriagePartner2.replace("XXX", StringUtils.EMPTY + partnerNumber).replace("YYY", StringUtils.EMPTY + aNumber)).evaluate(doc).get();
            tmpXpathMarriagePartner = XpathMarriagePartner2;
            tmpXpathMarriageDate = XpathMarriageDate2;
        }

        while (dateAndCity != null || personString != null) {
            if (personString != null && !personString.contains("&p=") && !personString.contains("&i=")) {
                aNumber++;
                personString = Xsoup.compile(XpathFamily.replace("XXX", StringUtils.EMPTY + index) + tmpXpathMarriagePartner.replace("XXX", StringUtils.EMPTY + partnerNumber).replace("YYY", StringUtils.EMPTY + aNumber)).evaluate(doc).get();
                aNumber = 1;
            }
            //unknown date case
            if (dateAndCity != null && !dateAndCity.contains("avant") && !dateAndCity.contains("après")) {
                date = parseMarriageDate(dateAndCity, person.getGender());
            } else {
                date = null;
            }
            city = parseMarriageCity(dateAndCity, person.getGender());
            person.addMarriage(geneanetSearchURL + personString, date, city);

            //Enfants
            int children = 1;
            String child = Xsoup.compile(XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathChildren.replace("XXX", StringUtils.EMPTY + partnerNumber).replace("YYY", StringUtils.EMPTY + aNumber).replace("ZZZ", StringUtils.EMPTY + children)).evaluate(doc).get();
            while (child != null) {
                if (!child.contains("&p=")) {
                    aNumber++;
                    child = Xsoup.compile(XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathChildren.replace("XXX", StringUtils.EMPTY + partnerNumber).replace("YYY", StringUtils.EMPTY + aNumber).replace("ZZZ", StringUtils.EMPTY + children)).evaluate(doc).get();
                }
                GeneanetPerson childPerson = new GeneanetPerson(geneanetSearchURL + child);
                person.addChild(childPerson);
                children++;
                aNumber = 1;
                child = Xsoup.compile(XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathChildren.replace("XXX", StringUtils.EMPTY + partnerNumber).replace("YYY", StringUtils.EMPTY + aNumber).replace("ZZZ", StringUtils.EMPTY + children)).evaluate(doc).get();
            }
            partnerNumber++;
            dateAndCity = Xsoup.compile(XpathFamily.replace("XXX", StringUtils.EMPTY + index) + tmpXpathMarriageDate.replace("XXX", StringUtils.EMPTY + partnerNumber)).evaluate(doc).get();
            personString = Xsoup.compile(XpathFamily.replace("XXX", StringUtils.EMPTY + index) + tmpXpathMarriagePartner.replace("XXX", StringUtils.EMPTY + partnerNumber).replace("YYY", StringUtils.EMPTY + aNumber)).evaluate(doc).get();
            if (dateAndCity == null && personString == null) {
                dateAndCity = Xsoup.compile(XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathMarriageDate2.replace("XXX", StringUtils.EMPTY + partnerNumber)).evaluate(doc).get();
                personString = Xsoup.compile(XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathMarriagePartner2.replace("XXX", StringUtils.EMPTY + partnerNumber).replace("YYY", StringUtils.EMPTY + aNumber)).evaluate(doc).get();
                tmpXpathMarriagePartner = XpathMarriagePartner2;
                tmpXpathMarriageDate = XpathMarriageDate2;
                if (dateAndCity == null && personString == null) {
                    dateAndCity = Xsoup.compile(XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathMarriageDate.replace("XXX", StringUtils.EMPTY + partnerNumber)).evaluate(doc).get();
                    personString = Xsoup.compile(XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathMarriagePartner.replace("XXX", StringUtils.EMPTY + partnerNumber).replace("YYY", StringUtils.EMPTY + aNumber)).evaluate(doc).get();
                    tmpXpathMarriagePartner = XpathMarriagePartner;
                    tmpXpathMarriageDate = XpathMarriageDate;
                }
            }
        }
    }

    public void parseDocument(Document document, GeneanetPerson person) {
        doc = document;
        person.setFirstName(getFirstName(doc));
        person.setFamilyName(getName(doc));
        person.setImage(getImage(doc));
        setGender(person);
        setGeneanetUrl(person);
        int index = setPersonDates(person, 1, BIRTH);
        index = setPersonDates(person, index, CHRISTENING);
        index = setPersonDates(person, index, DEATH);
        setPersonDates(person, index, BURIAL);
        int offset = setParents(person, 2);
        setFamily(offset, person);
        person.setSearched(true);
    }

    private int setParents(GeneanetPerson person, int index) {
        String category = Xsoup.compile(XpathSection.replace("XXX", StringUtils.EMPTY + 1)).evaluate(doc).get();
        String image = person.getImage();
        int offset = 0;
        if (image != null) {
            offset = -1;
        }
        if (category != null && category.equals("Parents")) {
            int fatherId = setFather(person, index + offset);
            int motherId = setMother(person, index + offset);
            if (fatherId == 1 || motherId == 1) {
                return index + 1 + offset;
            } else {
                return index + offset;
            }
        } else {
            return index + offset;
        }
    }

    /**
     * Search Geneanet person special url
     * if does not exist, the person does not appear on brothers list (nominal)
     *
     * @param person
     */
    private void setGeneanetUrl(GeneanetPerson person) {
        String url = Xsoup.compile(XpathUrl).evaluate(doc).get();
        if (url != null) {
            person.setGeneanetUrl(geneanetSearchURL + url);
        }
    }

    private void setGender(GeneanetPerson person) {
        String gender = Xsoup.compile(XpathGender).evaluate(doc).get();
        if (StringUtils.isBlank(gender)) {
            gender = Xsoup.compile(XpathGender2).evaluate(doc).get();
            if (StringUtils.isBlank(gender)) {
                gender = Xsoup.compile(XpathGender3).evaluate(doc).get();
            }
        }
        person.setGender(Gender.getGender(gender));
    }

    private int setMother(GeneanetPerson person, int index0) {
        int index = index0;
        int result = 1;
        if (doc.toString().contains("<!-- Parents photo -->")) {
            String xpath = (XpathParents2.replace("XXX", StringUtils.EMPTY + 2).replace("YYY", StringUtils.EMPTY + 1));
            String motherURL = Xsoup.compile(xpath).evaluate(doc).get();
            if (motherURL != null && motherURL.contains("i1=")) {
                xpath = (XpathParents2.replace("XXX", StringUtils.EMPTY + 2).replace("YYY", StringUtils.EMPTY + 2));
                motherURL = Xsoup.compile(xpath).evaluate(doc).get();
            }
            GeneanetPerson mother = new GeneanetPerson(geneanetSearchURL + motherURL);
            person.setMother(mother);
            return 0;
        } else {
            int nbmother = 2;
            String xpath = (XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathMother.replace("XXX", nbmother + StringUtils.EMPTY));
            String motherURL = Xsoup.compile(xpath).evaluate(doc).get();
            if (person.getGeneanetUrl() == null || (motherURL != null && motherURL.contains("&t="))) {
                nbmother--;
                xpath = (XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathMother.replace("XXX", nbmother + StringUtils.EMPTY));
                motherURL = Xsoup.compile(xpath).evaluate(doc).get();
            }
            if (motherURL != null && motherURL.contains("i1=")) {
                nbmother++;
                xpath = (XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathMother.replace("XXX", nbmother + StringUtils.EMPTY));
                motherURL = Xsoup.compile(xpath).evaluate(doc).get();
            }
            if (motherURL == null) {
                index++;
                xpath = (XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathMother.replace("XXX", nbmother + StringUtils.EMPTY));
                motherURL = Xsoup.compile(xpath).evaluate(doc).get();
            }
            if (motherURL == null || motherURL.contains("#note-wed-1") || motherURL.contains("&t=")) {
                index--;
                nbmother--;
                xpath = (XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathMother.replace("XXX", nbmother + StringUtils.EMPTY));
                motherURL = Xsoup.compile(xpath).evaluate(doc).get();
            }
            if (motherURL != null) {
                GeneanetPerson mother = new GeneanetPerson(geneanetSearchURL + motherURL);
                person.setMother(mother);
            }
            return result;
        }
    }

    private int setFather(GeneanetPerson person, int index0) {
        int index = index0;
        int result = 1;
        if (doc.toString().contains("<!-- Parents photo -->")) {
            String xpath = (XpathParents2.replace("XXX", StringUtils.EMPTY + 1).replace("YYY", StringUtils.EMPTY + 1));
            String fatherURL = Xsoup.compile(xpath).evaluate(doc).get();
            if (fatherURL != null && fatherURL.contains("i1=")) {
                xpath = (XpathParents2.replace("XXX", StringUtils.EMPTY + 1).replace("YYY", StringUtils.EMPTY + 2));
                fatherURL = Xsoup.compile(xpath).evaluate(doc).get();
            }
            GeneanetPerson father = new GeneanetPerson(geneanetSearchURL + fatherURL);
            person.setFather(father);
            return 0;
        } else {
            int nbfather = 2;
            String xpath = (XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathFather.replace("XXX", nbfather + StringUtils.EMPTY));
            String fatherURL = Xsoup.compile(xpath).evaluate(doc).get();
            if (person.getGeneanetUrl() == null || (fatherURL != null && fatherURL.contains("&t="))) {
                nbfather--;
                xpath = (XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathFather.replace("XXX", nbfather + StringUtils.EMPTY));
                fatherURL = Xsoup.compile(xpath).evaluate(doc).get();
            }
            if (fatherURL != null && fatherURL.contains("i1=")) {
                nbfather++;
                xpath = (XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathFather.replace("XXX", nbfather + StringUtils.EMPTY));
                fatherURL = Xsoup.compile(xpath).evaluate(doc).get();
            }
            if (fatherURL == null) {
                index++;
                xpath = (XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathFather.replace("XXX", nbfather + StringUtils.EMPTY));
                fatherURL = Xsoup.compile(xpath).evaluate(doc).get();
            }
            if (fatherURL == null || fatherURL.contains("#note-wed-1") || fatherURL.contains("&t=")) {
                index--;
                nbfather--;
                xpath = (XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathFather.replace("XXX", nbfather + StringUtils.EMPTY));
                fatherURL = Xsoup.compile(xpath).evaluate(doc).get();
            }
            if (fatherURL != null) {
                GeneanetPerson father = new GeneanetPerson(geneanetSearchURL + fatherURL);
                person.setFather(father);
            }
            return result;
        }
    }

}
