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
import java.util.Properties;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static Genealogy.URLConnexion.Geneanet.GeneanetBrowser.logger;
import static Genealogy.URLConnexion.Geneanet.GeneanetConverter.ActType.*;

/**
 * Created by Dan on 07/11/2017.
 */
public class GeneanetConverter {

    private static GeneanetConverter instance;
    private static final String REGEX_TRAILING_NUMBERS = "(.*) [0-9]+";
    private String XpathGender;
    private String XpathGender2;
    private String XpathGender3;
    private String XpathNames;
    private String XpathNames2;
    private String XpathNames3;
    private String XpathNames4;
    private String XpathNames5;
    private String XpathBirthAndDeath;
    private String XpathFather;
    private String XpathMother;
    private String XpathParents2;
    private String XpathFamily;
    private String XpathFamily2;
    private String XpathFamily3;
    private String XpathSection;
    private String XpathSection2;
    private String geneanetSearchURL;
    private String XpathMarriageDate;
    private String XpathMarriageDate2;
    private String XpathMarriagePartner;
    private String XpathMarriagePartner2;
    private String XpathBrother;
    private String XpathBrother2;
    private String XpathHalfBrother;
    private String XpathChildren;
    private String XpathUrl;
    private String XpathImage;
    private String XpathImage2;
    private String XpathImage3;
    private Document doc;
    private final static Character SPACE = (char) 160;
    private final String REGEX_PERSON_DATES = "(.*?)$|,.*";
    private final SimpleDateFormat dateFormatFullMonth = new SimpleDateFormat("dd MMMM yyyy", Locale.FRANCE);
    private final SimpleDateFormat dateFormatFullMonthOnly = new SimpleDateFormat("MMMM yyyy", Locale.FRANCE);
    //FirstName Xpath
    public String XpathFirstName1;
    public String XpathFirstName2;
    public String XpathFirstName4;
    public String XpathFirstName3;
    public String XpathFirstName5;
    public String XpathFirstName6;
    //FamilyName Xpath
    public String XpathFamilyName1;
    public String XpathFamilyName2;
    public String XpathFamilyName3;
    public String XpathFamilyName4;
    public String XpathFamilyName5;
    public String XpathFamilyName6;
    //Dates Xpath
    public String XpathDates1;
    public String XpathDates2;
    public String XpathDates3;
    //Family Xpath
    public String xPathFamilyBlock1;
    public String XpathFamilyBlock2;
    //Siblings
    public String XPathSibling1;
    public String XPathSibling2;
    public String XPathSibling3;
    //HalfSiblings
    public String XPathHalfSibling1;
    public String XPathHalfSibling2;
    //Parents
    public String XPathParents;
    //Mother
    public String XPathMother1;
    public String XPathMother2;
    public String XPathMother3;
    public String XPathMother4;
    public String XPathMother5;
    public String XPathMother6;
    public String XPathMother7;
    //Father
    public String XPathFather1;
    public String XPathFather2;
    public String XPathFather3;
    public String XPathFather4;
    public String XPathFather5;
    public String XPathFather6;
    public String XPathFather7;
    //Marriage and Children
    public String XPathMarriageDateCity1;
    public String XPathMarriagePerson1;
    public String XPathMarriageDateCity2;
    public String XPathMarriagePerson2;
    public String XPathMarriageDateCity3;
    public String XPathMarriagePerson3;
    public String XPathChild;

    private GeneanetConverter() {
    }

    public static GeneanetConverter getInstance() {
        if (instance == null) {
            instance = new GeneanetConverter();
        }
        return instance;
    }

    public void initDynamicXpath(Properties prop) {
        if (StringUtils.isEmpty(XpathGender)) {
            initXpathFromProperties(prop);
            initFirstNameXpath();
            initFamilyNameXpath();
            initParentsXpath();
            initMotherXPath();
            initFatherXPath();
        }
    }

    private void initXpathFromProperties(Properties prop) {
        XpathGender = prop.getProperty("XpathGender");
        XpathGender2 = prop.getProperty("XpathGender2");
        XpathGender3 = prop.getProperty("XpathGender3");
        XpathNames = prop.getProperty("XpathNames");
        XpathNames2 = prop.getProperty("XpathNames2");
        XpathNames3 = prop.getProperty("XpathNames3");
        XpathNames4 = prop.getProperty("XpathNames4");
        XpathNames5 = prop.getProperty("XpathNames5");
        XpathBirthAndDeath = prop.getProperty("XpathBirthAndDeath");
        XpathFather = prop.getProperty("XpathFather");
        XpathMother = prop.getProperty("XpathMother");
        XpathFamily = prop.getProperty("XpathFamily");
        XpathFamily2 = prop.getProperty("XpathFamily2");
        XpathFamily3 = prop.getProperty("XpathFamily3");
        XpathParents2 = prop.getProperty("XpathParents2");
        geneanetSearchURL = prop.getProperty("geneanetSearchURL");
        XpathSection = prop.getProperty("XpathSection");
        XpathSection2 = prop.getProperty("XpathSection2");
        XpathMarriageDate = prop.getProperty("XpathMarriageDate");
        XpathMarriageDate2 = prop.getProperty("XpathMarriageDate2");
        XpathMarriagePartner = prop.getProperty("XpathMarriagePartner");
        XpathMarriagePartner2 = prop.getProperty("XpathMarriagePartner2");
        XpathBrother = prop.getProperty("XpathBrother");
        XpathBrother2 = prop.getProperty("XpathBrother2");
        XpathHalfBrother = prop.getProperty("XpathHalfBrother");
        XpathChildren = prop.getProperty("XpathChildren");
        XpathUrl = prop.getProperty("XpathUrl");
        XpathImage = prop.getProperty("XpathImage");
        XpathImage2 = prop.getProperty("XpathImage2");
        XpathImage3 = prop.getProperty("XpathImage3");
    }

    public void initDynamicDateXpath(int index) {
        XpathDates1 = XpathFamily.replace("XXX", StringUtils.EMPTY + 1) + XpathBirthAndDeath.replace("XXX", StringUtils.EMPTY + index);
        XpathDates2 = XpathFamily2 + XpathBirthAndDeath.replace("XXX", StringUtils.EMPTY + index);
        XpathDates3 = XpathFamily3 + XpathBirthAndDeath.replace("XXX", StringUtils.EMPTY + index);
    }

    private void initFamilyNameXpath() {
        XpathFamilyName1 = XpathNames.replace("XXX", StringUtils.EMPTY + 2);
        XpathFamilyName2 = XpathNames2.replace("XXX", StringUtils.EMPTY + 2).replace("YYY", StringUtils.EMPTY + 2);
        XpathFamilyName3 = XpathNames2.replace("XXX", StringUtils.EMPTY + 2).replace("YYY", StringUtils.EMPTY + 1);
        XpathFamilyName4 = XpathNames3.replace("XXX", StringUtils.EMPTY + 2);
        XpathFamilyName5 = XpathNames4.replace("XXX", StringUtils.EMPTY + 2);
        XpathFamilyName6 = XpathNames5.replace("XXX", StringUtils.EMPTY + 2);
    }

    public void initDynamicXpathSibling(int index, int siblingNumber) {
        XPathSibling1 = XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathBrother.replace("XXX", StringUtils.EMPTY + siblingNumber).replace("YYY", StringUtils.EMPTY + 1);
        XPathSibling2 = XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathBrother.replace("XXX", StringUtils.EMPTY + siblingNumber).replace("YYY", StringUtils.EMPTY + 2);
        XPathSibling3 = XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathBrother2.replace("XXX", StringUtils.EMPTY + siblingNumber);
    }

    public void initDynamicXpathHalfSibling(int siblingBranch, int siblingNumber, int parentNumber) {
        XPathHalfSibling1 = XpathHalfBrother.replace("XXX", StringUtils.EMPTY + siblingBranch).replace("YYY", StringUtils.EMPTY + siblingNumber).replace("ZZZ", StringUtils.EMPTY + parentNumber).replace("WWW", StringUtils.EMPTY + 1);
        XPathHalfSibling2 = XpathHalfBrother.replace("XXX", StringUtils.EMPTY + siblingBranch).replace("YYY", StringUtils.EMPTY + siblingNumber).replace("ZZZ", StringUtils.EMPTY + parentNumber).replace("WWW", StringUtils.EMPTY + 2);

    }

    private void initFirstNameXpath() {
        XpathFirstName1 = XpathNames.replace("XXX", StringUtils.EMPTY + 1);
        XpathFirstName2 = XpathNames2.replace("XXX", StringUtils.EMPTY + 1).replace("YYY", StringUtils.EMPTY + 1);
        XpathFirstName3 = XpathNames2.replace("XXX", StringUtils.EMPTY + 1).replace("YYY", StringUtils.EMPTY + 2);
        XpathFirstName4 = XpathNames3.replace("XXX", StringUtils.EMPTY + 1);
        XpathFirstName5 = XpathNames4.replace("XXX", StringUtils.EMPTY + 1);
        XpathFirstName6 = XpathNames5.replace("XXX", StringUtils.EMPTY + 1);
    }

    private void initDynamicFamilyXpath(int sectionIndex) {
        xPathFamilyBlock1 = XpathSection.replace("XXX", StringUtils.EMPTY + sectionIndex);
        XpathFamilyBlock2 = XpathSection2.replace("XXX", StringUtils.EMPTY + sectionIndex);
    }

    public void initDynamicMarriageXpath(int index, int partnerNumber, int aNumber, String tmpXpathMarriagePartner, String tmpXpathMarriageDate, int children) {
        XPathMarriageDateCity1 = XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathMarriageDate.replace("XXX", StringUtils.EMPTY + partnerNumber);
        XPathMarriagePerson1 = XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathMarriagePartner.replace("XXX", StringUtils.EMPTY + partnerNumber).replace("YYY", StringUtils.EMPTY + aNumber);
        XPathMarriageDateCity2 = XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathMarriageDate2.replace("XXX", StringUtils.EMPTY + partnerNumber);
        XPathMarriagePerson2 = XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathMarriagePartner2.replace("XXX", StringUtils.EMPTY + partnerNumber).replace("YYY", StringUtils.EMPTY + aNumber);
        XPathMarriagePerson3 = XpathFamily.replace("XXX", StringUtils.EMPTY + index) + tmpXpathMarriagePartner.replace("XXX", StringUtils.EMPTY + partnerNumber).replace("YYY", StringUtils.EMPTY + aNumber);
        XPathMarriageDateCity3 = XpathFamily.replace("XXX", StringUtils.EMPTY + index) + tmpXpathMarriageDate.replace("XXX", StringUtils.EMPTY + partnerNumber);
        XPathChild = XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathChildren.replace("XXX", StringUtils.EMPTY + partnerNumber).replace("YYY", StringUtils.EMPTY + aNumber).replace("ZZZ", StringUtils.EMPTY + children);
    }

    private void initParentsXpath() {
        XPathParents = XpathSection.replace("XXX", StringUtils.EMPTY + 1);
    }

    private void initMotherXPath() {
        XPathMother1 = XpathParents2.replace("XXX", StringUtils.EMPTY + 2).replace("YYY", StringUtils.EMPTY + 1);
        XPathMother2 = XpathParents2.replace("XXX", StringUtils.EMPTY + 2).replace("YYY", StringUtils.EMPTY + 2);
    }

    private void initDynamicMotherXPath(int index, int nbmother) {
        XPathMother3 = XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathMother.replace("XXX", nbmother + StringUtils.EMPTY);
        XPathMother4 = XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathMother.replace("XXX", nbmother + StringUtils.EMPTY);
        XPathMother5 = XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathMother.replace("XXX", nbmother + StringUtils.EMPTY);
        XPathMother6 = XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathMother.replace("XXX", nbmother + StringUtils.EMPTY);
        XPathMother7 = XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathMother.replace("XXX", nbmother + StringUtils.EMPTY);
    }

    private void initFatherXPath() {
        XPathFather1 = XpathParents2.replace("XXX", StringUtils.EMPTY + 1).replace("YYY", StringUtils.EMPTY + 1);
        XPathFather2 = XpathParents2.replace("XXX", StringUtils.EMPTY + 1).replace("YYY", StringUtils.EMPTY + 2);
    }

    private void initDynamicFatherXPath(int index, int nbfather) {
        XPathFather3 = XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathFather.replace("XXX", nbfather + StringUtils.EMPTY);
        XPathFather4 = XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathFather.replace("XXX", nbfather + StringUtils.EMPTY);
        XPathFather5 = XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathFather.replace("XXX", nbfather + StringUtils.EMPTY);
        XPathFather6 = XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathFather.replace("XXX", nbfather + StringUtils.EMPTY);
        XPathFather7 = XpathFamily.replace("XXX", StringUtils.EMPTY + index) + XpathFather.replace("XXX", nbfather + StringUtils.EMPTY);
    }

    public String getXpathNames() {
        return XpathNames;
    }

    public void setXpathNames(String xpathNames) {
        XpathNames = xpathNames;
    }

    public String getXpathNames2() {
        return XpathNames2;
    }

    public void setXpathNames2(String xpathNames2) {
        XpathNames2 = xpathNames2;
    }

    public String getXpathGender2() {
        return XpathGender2;
    }

    public void setXpathGender2(String xpathGender2) {
        XpathGender2 = xpathGender2;
    }

    public String getXpathGender3() {
        return XpathGender3;
    }

    public void setXpathGender3(String xpathGender3) {
        XpathGender3 = xpathGender3;
    }

    public String getXpathBirthAndDeath() {
        return XpathBirthAndDeath;
    }

    public void setXpathBirthAndDeath(String xpathBirthAndDeath) {
        XpathBirthAndDeath = xpathBirthAndDeath;
    }

    public String getXpathFather() {
        return XpathFather;
    }

    public void setXpathFather(String xpathFather) {
        XpathFather = xpathFather;
    }

    public String getXpathMother() {
        return XpathMother;
    }

    public String getXpathParents2() {
        return XpathParents2;
    }

    public void setXpathParents2(String xpathParents2) {
        XpathParents2 = xpathParents2;
    }

    public String getXpathImage() {
        return XpathImage;
    }

    public void setXpathImage(String xpathImage) {
        XpathImage = xpathImage;
    }

    public String getXpathImage2() {
        return XpathImage2;
    }

    public void setXpathImage2(String xpathImage2) {
        XpathImage2 = xpathImage2;
    }

    public String getXpathImage3() {
        return XpathImage3;
    }

    public void setXpathImage3(String xpathImage3) {
        XpathImage3 = xpathImage3;
    }

    public void setXpathMother(String xpathMother) {
        XpathMother = xpathMother;
    }

    public String getXpathFamily() {
        return XpathFamily;
    }

    public void setXpathFamily(String xpathFamily) {
        XpathFamily = xpathFamily;
    }

    public String getXpathUrl() {
        return XpathUrl;
    }

    public void setXpathUrl(String xpathUrl) {
        XpathUrl = xpathUrl;
    }

    public void setXpathGender(String xpathGender) {
        XpathGender = xpathGender;
    }

    public String getXpathGender() {
        return XpathGender;
    }

    public String getXpathFamily2() {
        return XpathFamily2;
    }

    public void setXpathFamily2(String xpathFamily2) {
        XpathFamily2 = xpathFamily2;
    }

    public String getXpathFamily3() {
        return XpathFamily3;
    }

    public void setXpathFamily3(String xpathFamily3) {
        XpathFamily3 = xpathFamily3;
    }

    public String getGeneanetSearchURL() {
        return geneanetSearchURL;
    }

    public void setGeneanetSearchURL(String GeneanetSearchURL) {
        geneanetSearchURL = GeneanetSearchURL;
    }

    public String getXpathNames3() {
        return XpathNames3;
    }

    public void setXpathNames3(String xpathNames3) {
        XpathNames3 = xpathNames3;
    }

    public String getXpathNames4() {
        return XpathNames4;
    }

    public void setXpathNames4(String xpathNames4) {
        XpathNames4 = xpathNames4;
    }

    public String getXpathNames5() {
        return XpathNames5;
    }

    public void setXpathNames5(String xpathNames5) {
        XpathNames5 = xpathNames5;
    }

    public Document getDoc() {
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }

    public String getXpathMarriageDate() {
        return XpathMarriageDate;
    }

    public void setXpathMarriageDate(String xpathMarriageDate) {
        XpathMarriageDate = xpathMarriageDate;
    }

    public String getXpathMarriagePartner() {
        return XpathMarriagePartner;
    }

    public void setXpathMarriagePartner(String xpathMarriagePartner) {
        XpathMarriagePartner = xpathMarriagePartner;
    }

    public String getXpathMarriageDate2() {
        return XpathMarriageDate2;
    }

    public void setXpathMarriageDate2(String xpathMarriageDate2) {
        XpathMarriageDate2 = xpathMarriageDate2;
    }

    public String getXpathMarriagePartner2() {
        return XpathMarriagePartner2;
    }

    public void setXpathMarriagePartner2(String xpathMarriagePartner2) {
        XpathMarriagePartner2 = xpathMarriagePartner2;
    }

    public String getXpathBrother() {
        return XpathBrother;
    }

    public void setXpathBrother(String xpathBrother) {
        XpathBrother = xpathBrother;
    }

    public String getXpathChildren() {
        return XpathChildren;
    }

    public void setXpathChildren(String xpathChildren) {
        XpathChildren = xpathChildren;
    }

    public String getXpathHalfBrother() {
        return XpathHalfBrother;
    }

    public void setXpathHalfBrother(String xpathHalfBrother) {
        XpathHalfBrother = xpathHalfBrother;
    }

    public String getXpathSection() {
        return XpathSection;
    }

    public void setXpathSection(String xpathSection) {
        XpathSection = xpathSection;
    }

    public String getXpathSection2() {
        return XpathSection2;
    }

    public void setXpathSection2(String xpathSection2) {
        XpathSection2 = xpathSection2;
    }

    public String getXpathBrother2() {
        return XpathBrother2;
    }

    public void setXpathBrother2(String xpathBrother2) {
        XpathBrother2 = xpathBrother2;
    }

    public String getFirstName(Document doc) {
        String firstName = Xsoup.compile(XpathFirstName1).evaluate(doc).get();
        if (StringUtils.isBlank(firstName)) {
            firstName = Xsoup.compile(XpathFirstName2).evaluate(doc).get();
            if (firstName != null) {
                //Integer case
                try {
                    Integer.parseInt(firstName.replace(" ", StringUtils.EMPTY));
                    firstName = Xsoup.compile(XpathFirstName3).evaluate(doc).get();
                } catch (NumberFormatException e) {
                    //do nothing
                }
            } else {
                firstName = Xsoup.compile(XpathFirstName3).evaluate(doc).get();
            }
        }
        if (StringUtils.isBlank(firstName)) {
            firstName = Xsoup.compile(XpathFirstName4).evaluate(doc).get();
        }
        if (StringUtils.isBlank(firstName)) {
            firstName = Xsoup.compile(XpathFirstName5).evaluate(doc).get();
        }
        if (StringUtils.isBlank(firstName)) {
            firstName = Xsoup.compile(XpathFirstName6).evaluate(doc).get();
        }
        if (!StringUtils.isBlank(firstName)) {
            return firstName;
        }
        return null;
    }

    public String getName(Document doc) {
        String name = Xsoup.compile(XpathFamilyName1).evaluate(doc).get();
        if (StringUtils.isBlank(name)) {
            name = Xsoup.compile(XpathFamilyName2).evaluate(doc).get();
            if (StringUtils.isBlank(name)) {
                name = Xsoup.compile(XpathFamilyName3).evaluate(doc).get();
            }
        }
        if (StringUtils.isBlank(name)) {
            name = Xsoup.compile(XpathFamilyName4).evaluate(doc).get();
        }
        if (StringUtils.isBlank(name)) {
            name = Xsoup.compile(XpathFamilyName5).evaluate(doc).get();
        }
        if (StringUtils.isBlank(name)) {
            name = Xsoup.compile(XpathFamilyName6).evaluate(doc).get();
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

    public static String removeSpace(String input) {
        return StringUtils.replace(input, " ", StringUtils.EMPTY);
    }

    public int setPersonDates(GeneanetPerson person, int index, ActType act) {
        initDynamicDateXpath(index);
        String birth = Xsoup.compile(XpathDates1).evaluate(doc).get();
        if (StringUtils.isBlank(birth) || (birth.matches(".*Marié.*avec.*"))) {
            birth = Xsoup.compile(XpathDates2).evaluate(doc).get();
            if (StringUtils.isBlank(birth) || (birth.matches(".*Marié.*|.*avec.*"))) {
                birth = Xsoup.compile(XpathDates3).evaluate(doc).get();
                if (StringUtils.isBlank(birth)) {
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
            if (dateAndCity.matches(".*Né.*|.*Baptisé.*|.*Décédé.*|.*Inhumé.*")) {
                String[] tab = dateAndCity.split(" - ");
                String date = null;
                String city = null;
                if (tab != null && tab.length > 1) {
                    date = tab[0];
                    String cityTmp = tab[1];
                    if (tab.length > 2 && (cityTmp.matches(".*Canton de.*|^Vue .*"))) {
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
                if (newTab.length > 1 && (cityTmp.matches(".*Canton de.*|") || newTemptab.startsWith("Vue "))) {
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
        resultDate = resultDate.replace(SPACE, ' ');

        //gestion des en
        resultDate = resultDate.replace("en ", "le ");

        //remove Né le
        regex = regex.replace(SPACE, ' ');
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
            initDynamicFamilyXpath(sectionIndex);
            category = Xsoup.compile(xPathFamilyBlock1).evaluate(doc).get();
            if (category == null) {
                category = Xsoup.compile(XpathFamilyBlock2).evaluate(doc).get();
            }
            if (category != null) {
                category = removeSpace(category);
                if (category.contains("Union(s)")) {
                    setMarriageAndChildren(index, person);
                    index++;
                } else if (category.matches("Fratrie|Frèresetsœurs")) {
                    setSibling(index, person);
                    index++;
                } else if (StringUtils.equals(category, "Demi-frèresetdemi-sœurs")) {
                    setHalfSiblings(person);
                    index++;
                }
            }
        } while (category != null);
    }

    private void setSibling(int index, GeneanetPerson person) {
        int siblingNumber = 1;
        String personString;
        do {
            initDynamicXpathSibling(index, siblingNumber);
            personString = Xsoup.compile(XPathSibling1).evaluate(doc).get();
            if (personString != null && !(geneanetSearchURL + personString).equals(person.getGeneanetUrl()) && personString.contains("i1=")) {
                personString = Xsoup.compile(XPathSibling2).evaluate(doc).get();
            }
            String isThisMe = Xsoup.compile(XPathSibling3).evaluate(doc).get();
            if (personString == null) {
                personString = isThisMe;
            } else if (personString != null && !(geneanetSearchURL + personString).equals(person.getGeneanetUrl()) && !personString.contains("i1=") && !(person.getFullName()).equals(isThisMe)) {
                GeneanetPerson sibling = new GeneanetPerson(geneanetSearchURL + personString);
                person.addSibling(sibling);
            }
            siblingNumber++;
        } while (personString != null);
    }

    private void setHalfSiblings(GeneanetPerson person) {
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
                    initDynamicXpathHalfSibling(siblingBranch, siblingNumber, parentNumber);
                    personString = Xsoup.compile(XPathHalfSibling1).evaluate(doc).get();
                    if (personString != null && personString.contains("&i1=")) {
                        personString = Xsoup.compile(XPathHalfSibling2).evaluate(doc).get();
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
        int children = 1;
        MyDate date;
        String city;
        String tmpXpathMarriagePartner = XpathMarriagePartner;
        String tmpXpathMarriageDate = XpathMarriageDate;
        initDynamicMarriageXpath(index, partnerNumber, aNumber, tmpXpathMarriagePartner, tmpXpathMarriageDate, children);
        String dateAndCity = Xsoup.compile(XPathMarriageDateCity1).evaluate(doc).get();
        String personString = Xsoup.compile(XPathMarriagePerson1).evaluate(doc).get();
        if (dateAndCity == null && personString == null) {
            initDynamicMarriageXpath(index, partnerNumber, aNumber, tmpXpathMarriagePartner, tmpXpathMarriageDate, children);
            dateAndCity = Xsoup.compile(XPathMarriageDateCity2).evaluate(doc).get();
            personString = Xsoup.compile(XPathMarriagePerson2).evaluate(doc).get();
            tmpXpathMarriagePartner = XpathMarriagePartner2;
            tmpXpathMarriageDate = XpathMarriageDate2;
        }

        while (dateAndCity != null || personString != null) {
            if (personString != null && !personString.matches(".*&p=.*|.*&i=.*")) {
                aNumber++;
                initDynamicMarriageXpath(index, partnerNumber, aNumber, tmpXpathMarriagePartner, tmpXpathMarriageDate, children);
                personString = Xsoup.compile(XPathMarriagePerson3).evaluate(doc).get();
                aNumber = 1;
            }
            //unknown date case
            if (dateAndCity != null && !dateAndCity.matches(".*avant.*|.*après.*")) {
                date = parseMarriageDate(dateAndCity, person.getGender());
            } else {
                date = null;
            }
            city = parseMarriageCity(dateAndCity, person.getGender());
            person.addMarriage(geneanetSearchURL + personString, date, city);

            //Enfants
            children = 1;
            initDynamicMarriageXpath(index, partnerNumber, aNumber, tmpXpathMarriagePartner, tmpXpathMarriageDate, children);
            String child = Xsoup.compile(XPathChild).evaluate(doc).get();
            while (child != null) {
                if (!child.contains("&p=")) {
                    aNumber++;
                    initDynamicMarriageXpath(index, partnerNumber, aNumber, tmpXpathMarriagePartner, tmpXpathMarriageDate, children);
                    child = Xsoup.compile(XPathChild).evaluate(doc).get();
                }
                GeneanetPerson childPerson = new GeneanetPerson(geneanetSearchURL + child);
                person.addChild(childPerson);
                children++;
                aNumber = 1;
                initDynamicMarriageXpath(index, partnerNumber, aNumber, tmpXpathMarriagePartner, tmpXpathMarriageDate, children);
                child = Xsoup.compile(XPathChild).evaluate(doc).get();
            }
            partnerNumber++;
            initDynamicMarriageXpath(index, partnerNumber, aNumber, tmpXpathMarriagePartner, tmpXpathMarriageDate, children);
            dateAndCity = Xsoup.compile(XPathMarriageDateCity3).evaluate(doc).get();
            personString = Xsoup.compile(XPathMarriagePerson3).evaluate(doc).get();
            if (dateAndCity == null && personString == null) {
                initDynamicMarriageXpath(index, partnerNumber, aNumber, tmpXpathMarriagePartner, tmpXpathMarriageDate, children);
                dateAndCity = Xsoup.compile(XPathMarriageDateCity2).evaluate(doc).get();
                personString = Xsoup.compile(XPathMarriagePerson2).evaluate(doc).get();
                tmpXpathMarriagePartner = XpathMarriagePartner2;
                tmpXpathMarriageDate = XpathMarriageDate2;
                if (dateAndCity == null && personString == null) {
                    initDynamicMarriageXpath(index, partnerNumber, aNumber, tmpXpathMarriagePartner, tmpXpathMarriageDate, children);
                    dateAndCity = Xsoup.compile(XPathMarriageDateCity1).evaluate(doc).get();
                    personString = Xsoup.compile(XPathMarriagePerson1).evaluate(doc).get();
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
        String category = Xsoup.compile(XPathParents).evaluate(doc).get();
        String image = person.getImage();
        int offset = 0;
        if (image != null) {
            offset = -1;
        }
        if (StringUtils.equals(category, "Parents")) {
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
        int nbmother = 2;
        if (doc.toString().contains("<!-- Parents photo -->")) {
            String motherURL = Xsoup.compile(XPathMother1).evaluate(doc).get();
            if (motherURL != null && motherURL.contains("i1=")) {
                motherURL = Xsoup.compile(XPathMother2).evaluate(doc).get();
            }
            GeneanetPerson mother = new GeneanetPerson(geneanetSearchURL + motherURL);
            person.setMother(mother);
            return 0;
        } else {
            initDynamicMotherXPath(index, nbmother);
            String motherURL = Xsoup.compile(XPathMother3).evaluate(doc).get();
            if (person.getGeneanetUrl() == null || (motherURL != null && motherURL.contains("&t="))) {
                nbmother--;
                initDynamicMotherXPath(index, nbmother);
                motherURL = Xsoup.compile(XPathMother4).evaluate(doc).get();
            }
            if (motherURL != null && motherURL.contains("i1=")) {
                nbmother++;
                initDynamicMotherXPath(index, nbmother);
                motherURL = Xsoup.compile(XPathMother5).evaluate(doc).get();
            }
            if (motherURL == null) {
                index++;
                initDynamicMotherXPath(index, nbmother);
                motherURL = Xsoup.compile(XPathMother6).evaluate(doc).get();
            }
            if (motherURL == null || motherURL.matches(".*#note-wed-1.*|.*&t=.*")) {
                index--;
                nbmother--;
                initDynamicMotherXPath(index, nbmother);
                motherURL = Xsoup.compile(XPathMother7).evaluate(doc).get();
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
        int nbfather = 2;
        if (doc.toString().contains("<!-- Parents photo -->")) {
            String fatherURL = Xsoup.compile(XPathFather1).evaluate(doc).get();
            if (fatherURL != null && fatherURL.contains("i1=")) {
                fatherURL = Xsoup.compile(XPathFather2).evaluate(doc).get();
            }
            GeneanetPerson father = new GeneanetPerson(geneanetSearchURL + fatherURL);
            person.setFather(father);
            return 0;
        } else {
            initDynamicFatherXPath(index, nbfather);
            String fatherURL = Xsoup.compile(XPathFather3).evaluate(doc).get();
            if (person.getGeneanetUrl() == null || (fatherURL != null && fatherURL.contains("&t="))) {
                nbfather--;
                initDynamicFatherXPath(index, nbfather);
                fatherURL = Xsoup.compile(XPathFather4).evaluate(doc).get();
            }
            if (fatherURL != null && fatherURL.contains("i1=")) {
                nbfather++;
                initDynamicFatherXPath(index, nbfather);
                fatherURL = Xsoup.compile(XPathFather5).evaluate(doc).get();
            }
            if (fatherURL == null) {
                index++;
                initDynamicFatherXPath(index, nbfather);
                fatherURL = Xsoup.compile(XPathFather6).evaluate(doc).get();
            }
            if (fatherURL == null || fatherURL.matches(".*#note-wed-1.*|.*&t=.*")) {
                index--;
                nbfather--;
                initDynamicFatherXPath(index, nbfather);
                fatherURL = Xsoup.compile(XPathFather7).evaluate(doc).get();
            }
            if (fatherURL != null) {
                GeneanetPerson father = new GeneanetPerson(geneanetSearchURL + fatherURL);
                person.setFather(father);
            }
            return result;
        }
    }

}
