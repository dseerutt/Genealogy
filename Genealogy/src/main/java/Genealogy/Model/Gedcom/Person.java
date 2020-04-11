package Genealogy.Model.Gedcom;

import Genealogy.MapViewer.Structures.MapStructure;
import Genealogy.Model.Act.Birth;
import Genealogy.Model.Act.Christening;
import Genealogy.Model.Act.Death;
import Genealogy.Model.Act.Enum.ActType;
import Genealogy.Model.Act.Enum.UnionType;
import Genealogy.Model.Act.Union;
import Genealogy.Model.Date.FullDate;
import Genealogy.Model.Date.MyDate;
import Genealogy.Model.Exception.ParsingException;
import Genealogy.Parsing.PDFStructure;
import Genealogy.Parsing.ParsingStructure;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class Person : person object from genealogical tree
 */
public class Person {
    /**
     * String gedcom id of the person
     */
    private String id;
    /**
     * Sex enum of the person
     */
    private Sex sex;
    /**
     * String family name
     */
    private String name;
    /**
     * String first name
     */
    private String surname;
    /**
     * Birth of the person
     */
    private Birth birth;
    /**
     * Christening of the person, not very used
     */
    private Christening christening;
    /**
     * Death of the person
     */
    private Death death;
    /**
     * String profession of the person
     */
    private String profession;
    /**
     * String comments of the person
     */
    private String comments;
    /**
     * PDFStructure of the person
     */
    private PDFStructure pdfStructure;
    /**
     * List of unions
     */
    private ArrayList<Union> unions = new ArrayList<>();
    /**
     * Person father
     */
    private Person father;
    /**
     * Person mother
     */
    private Person mother;
    /**
     * Children are list of Person
     */
    private ArrayList<Person> children = new ArrayList<>();
    /**
     * Boolean directAncestor if related to the root
     */
    private boolean directAncestor = false;
    /**
     * Int age
     */
    private int age;
    /**
     * Boolean stillAlive if the person is still alive today
     */
    private boolean stillAlive = false;
    /**
     * Logger of the class Person
     */
    final static Logger logger = LogManager.getLogger(Person.class);
    /**
     * Map of list of MapStructures (name, age, town) per year
     */
    private static HashMap<Integer, ArrayList<MapStructure>> periods = new HashMap<>();
    /**
     * Map of list of MapStructures (name, age, town) per year only for direct ancestors
     */
    private static HashMap<Integer, ArrayList<MapStructure>> periodsDirectAncestors = new HashMap<>();
    private static int minimumPeriod = 10000;

    public static HashMap<Integer, ArrayList<MapStructure>> getPeriods() {
        return periods;
    }

    public static HashMap<Integer, ArrayList<MapStructure>> getPeriodsDirectAncestors() {
        return periodsDirectAncestors;
    }

    public boolean isStillAlive() {
        return stillAlive;
    }

    public static Sex parseSex(String s) {
        switch (s) {
            case "M":
                return Sex.MALE;
            case "F":
                return Sex.FEMALE;
            default:
                return Sex.UNKNOWN;
        }
    }

    public int getAge() {
        return age;
    }

    public String getId() {
        return id;
    }

    public Sex getSex() {
        return sex;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public Birth getBirth() {
        return birth;
    }

    public Death getDeath() {
        return death;
    }

    public String getProfession() {
        return profession;
    }

    public String getComments() {
        return comments;
    }

    public ArrayList<Union> getUnions() {
        return unions;
    }

    public Person getFather() {
        return father;
    }

    public Person getMother() {
        return mother;
    }

    public ArrayList<Person> getChildren() {
        return children;
    }

    public boolean isDirectAncestor() {
        return directAncestor;
    }

    public void setDirectAncestor(boolean directAncestor) {
        this.directAncestor = directAncestor;
    }

    public String getFullName() {
        if (StringUtils.isEmpty(surname)) {
            return name;
        } else {
            return surname + " " + name;
        }
    }

    /**
     * Fonction getFullNameInverted
     *
     * @return le nom puis le prénom
     */
    public String getFullNameInverted() {
        if ((name != null) && (!name.equals(""))) {
            return name + " " + surname;
        }
        return surname;
    }

    public String getComparatorName() {
        String txt = "";
        if ((name.equals("...")) || (surname.equals("..."))) {
            txt = "z";
        }
        return txt + name + " " + surname;
    }

    public void initPeriods2(ArrayList<Pair<MyDate, Town>> tempPeriods) {
        //System.out.println(tempPeriods);
        if (tempPeriods.size() >= 1) {
            if (tempPeriods.size() == 1) {
                Date date = new Date(tempPeriods.get(0).getKey().getDate().getTime());
                MapStructure mapStructure =
                        new MapStructure(tempPeriods.get(0).getValue(), getFullName(), getAge(date, 0));
                addPeriod((int) tempPeriods.get(0).getKey().getYear(), mapStructure);
            } else {
                for (int i = 0; i < tempPeriods.size() - 1; i++) {
                    int date1 = (int) tempPeriods.get(i).getKey().getYear();
                    int date2 = (int) tempPeriods.get(i + 1).getKey().getYear();
                    int index = 0;
                    for (int k = date1; k < date2; k++) {
                        Date date = new Date(tempPeriods.get(i).getKey().getDate().getTime());
                        MapStructure mapStructure =
                                new MapStructure(tempPeriods.get(i).getValue(), getFullName(), getAge(date, index));
                        addPeriod(k, mapStructure);
                        //System.out.println(periods);
                        index++;
                    }
                }
                Date date = new Date(tempPeriods.get(tempPeriods.size() - 1).getKey().getDate().getTime());
                MapStructure mapStructure =
                        new MapStructure(tempPeriods.get(tempPeriods.size() - 1).getValue(), getFullName(), getAge(date, 0));
                addPeriod((int) tempPeriods.get(tempPeriods.size() - 1).getKey().getYear(), mapStructure);
            }
        }
    }

    public ArrayList<Pair<MyDate, Town>> initPeriods1() {
        ArrayList<Pair<MyDate, Town>> tempPeriods = new ArrayList<>();

        //Naissance
        if ((birth != null) && (birth.getTown() != null) && (birth.getTown().getName() != null) && (birth.getDate() != null)) {
            tempPeriods.add(new Pair<MyDate, Town>(birth.getDate(), birth.getTown()));
        }

        //Unions
        for (int i = 0; i < unions.size(); i++) {
            if ((unions.get(i).getDate() != null) && (unions.get(i).getTown() != null) && (unions.get(i).getTown().getName() != null)) {
                tempPeriods.add(new Pair<MyDate, Town>(unions.get(i).getDate(), unions.get(i).getTown()));
            }
        }

        //Enfants
        for (int i = 0; i < children.size(); i++) {
            if ((children.get(i).getBirth() != null) &&
                    (children.get(i).getBirth().getDate() != null) &&
                    (children.get(i).getBirth().getTown() != null) &&
                    (children.get(i).getBirth().getTown().getName() != null)) {
                tempPeriods.add(new Pair<MyDate, Town>(children.get(i).getBirth().getDate(), children.get(i).getBirth().getTown()));
            }
        }

        //Décès
        if ((death != null) && (death.getTown() != null) && (death.getTown().getName() != null) && (death.getDate() != null)) {
            tempPeriods.add(new Pair<MyDate, Town>(death.getDate(), death.getTown()));

            Collections.sort(tempPeriods, new Comparator<Pair<MyDate, Town>>() {
                @Override
                public int compare(Pair<MyDate, Town> o1, Pair<MyDate, Town> o2) {
                    return o1.getKey().getDate().compareTo(o2.getKey().getDate());
                }
            });

            //Cas des enfants nés après la mort
            while (tempPeriods.get(tempPeriods.size() - 1).getKey().getDate().getTime() > death.getDate().getDate().getTime()) {
                tempPeriods.remove(tempPeriods.size() - 1);
            }
        } else {
            Collections.sort(tempPeriods, new Comparator<Pair<MyDate, Town>>() {
                @Override
                public int compare(Pair<MyDate, Town> o1, Pair<MyDate, Town> o2) {
                    return o1.getKey().getDate().compareTo(o2.getKey().getDate());
                }
            });
            if ((stillAlive) && (!tempPeriods.isEmpty())) {
                tempPeriods.add(new Pair<MyDate, Town>(new FullDate(), tempPeriods.get(tempPeriods.size() - 1).getValue()));
            }
        }
        return tempPeriods;
    }

    public int getProofUnionSize() {
        int nbUnions = 0;
        for (Union union : unions) {
            nbUnions += union.getProofs().size();
        }
        return nbUnions;
    }

    /**
     * Fonction removePDFStructure
     * Remove PDFStructure in note
     */
    public void removePDFStructure() {
        String PDFStructureRegex = "¤PDF" + id + "¤";
        if (comments.contains(PDFStructureRegex)) {
            String PDFRegex = "(.*)" + PDFStructureRegex + "{.*}(.*)";
            Pattern pattern = Pattern.compile(PDFRegex, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(comments);
            if (matcher.find() && matcher.groupCount() == 2) {
                comments = matcher.group(1) + matcher.group(2);
            }
        }
    }

    public void savePDFStructure() {
        comments += pdfStructure;
    }

    /**
     * Fonction addProof
     *
     * @param actType    Birth, Mariage, Death
     * @param proof      Nom du PDF String
     * @param unionIndex numéro de l'union
     */
    public void addProof(ActType actType, String proof, int unionIndex) throws Exception {
        switch (actType) {
            case BIRTH:
                removePDFStructure();
                birth.addProof(proof);
                pdfStructure.addToPDFBirthList(proof);
                savePDFStructure();
                break;
            case MARRIAGE:
                removePDFStructure();
                if (unionIndex < unions.size()) {
                    unions.get(unionIndex).addProof(proof);
                    pdfStructure.addToPDFMarriageList(proof);
                    savePDFStructure();
                } else {
                    throw new Exception("Index trop élevé");
                }
                break;
            case DEATH:
                removePDFStructure();
                death.addProof(proof);
                pdfStructure.addToPDGDeathList(proof);
                savePDFStructure();
                break;
            default:
                return;
        }
    }

    /**
     * Fonction addProof except marriage
     *
     * @param actType Birth, Death
     * @param proof   Nom du PDF String
     */
    public void addProof(ActType actType, String proof) throws Exception {
        addProof(actType, proof, 0);
    }

    public static int getMinimumPeriod() {
        return minimumPeriod;
    }

    public void initLifeSpans() {
        initPeriods2(initPeriods1());
    }

    /**
     * isPrintable retourne vrai si la personne a un prénom et un nom de famille différent de ...
     *
     * @return
     */
    public boolean isPrintable() {
        return (name != null) && (surname != null) && (!name.equals("...")) && (!surname.equals("..."));
    }

    public void addPeriod(int year, MapStructure mapStructure) {
        if (year < minimumPeriod) {
            minimumPeriod = year;
        }
        if (periods.containsKey(year)) {
            periods.get(year).add(mapStructure);
        } else {
            ArrayList<MapStructure> structure = new ArrayList<>();
            structure.add(mapStructure);
            periods.put(year, structure);
        }
        if (directAncestor) {
            if (periodsDirectAncestors.containsKey(year)) {
                periodsDirectAncestors.get(year).add(mapStructure);
            } else {
                ArrayList<MapStructure> structure = new ArrayList<>();
                structure.add(mapStructure);
                periodsDirectAncestors.put(year, structure);
            }
        }
    }

    public int getAge(Date date, int years) {
        if ((birth != null) && (birth.getDate() != null)) {
            Date d0 = birth.getDate().getDate();
            long diff = date.getTime() - birth.getDate().getDate().getTime();
            int days = (int) TimeUnit.DAYS.toDays(diff);
            int a = days / 365;
            DateTime dateTime0 = new DateTime(birth.getDate().getDate().getTime());
            DateTime dateTime1 = new DateTime(date.getTime());
            Period period = new Period(dateTime0, dateTime1);
            return period.getYears() + years;
        } else {
            return -1;
        }
    }

    /**
     * Function toString : pretty print of the object Person
     *
     * @return the final String
     */
    @Override
    public String toString() {
        String res = "Person";
        if (!StringUtils.isBlank(id)) {
            res += "_" + id.substring(1, id.length() - 1);
        }
        res += "{";
        if (age != -1) {
            res += age + " y/o ";
        }
        res += sex.toStringPrettyPrint() + ", name='" + name + '\'';
        if (!StringUtils.isEmpty(surname)) {
            res += ", surname='" + surname + '\'';
        }
        if (birth != null) {
            res += ", " + birth.toStringPrettyPrint();
        }
        if (christening != null) {
            res += ", " + christening.toStringPrettyPrint();
        }
        if (death != null) {
            res += ", " + death.toStringPrettyPrint();
        }
        if (!StringUtils.isEmpty(profession)) {
            res += ", profession of " + profession;
        }
        if (father != null) {
            res += ", father='" + father.getFullName() + '\'';
        }
        if (mother != null) {
            res += ", mother='" + mother.getFullName() + '\'';
        }
        if (!unions.isEmpty()) {
            if (unions.size() == 1) {
                res += ", " + unions.get(0).toStringPrettyPrint(id);
            } else {
                res += ", unions=[" + unions.get(0).toStringPrettyPrint(id);
                for (int i = 1; i < unions.size(); i++) {
                    res += ", " + unions.get(i).toStringPrettyPrint(id);
                }
                res += "]";
            }
        }
        if (directAncestor) {
            res += ", directAncestor";
        }
        if (!children.isEmpty()) {
            if (children.size() == 1) {
                res += ", child=[" + children.get(0).getFullName();
            } else {
                res += ", children=[" + children.get(0).getFullName();
                for (int i = 1; i < children.size(); i++) {
                    res += ", " + children.get(i).getFullName();
                }
            }
            res += "]";
        }
        if (!StringUtils.isEmpty(comments)) {
            res += ", comments='" + comments + '\'';
        }
        return res + "}";
    }

    /**
     * @param genealogy
     * @param list
     * @param offset
     * @param indexMax
     * @throws ParsingException if it could not parse the file fields
     */
    public Person(Genealogy genealogy, ArrayList<ParsingStructure> list, int offset, int indexMax) throws ParsingException {
        if (list != null) {

            int index = offset;
            if (offset >= indexMax) {
                logger.error("Erreur dans le parsing de personne, offset >= indexMax");
                return;
            }
            String newId = list.get(index++).getFieldName();
            if (newId.contains("@I")) {
                id = newId;
            } else {
                id = "";
            }
            name = genealogy.findFieldInContents("SURN", index++, indexMax);
            surname = genealogy.findFieldInContents("GIVN", index++, indexMax);
            if (StringUtils.isEmpty(name) && StringUtils.isEmpty(surname)) {
                name = genealogy.findFieldInContents("NAME", offset - 1, indexMax);
            }
            int indexBirthday = genealogy.findIndexIdString("BIRT", index, indexMax);

            if (indexBirthday != -1) {
                indexBirthday++;
                String input = genealogy.findFieldInContents("DATE", indexBirthday, indexBirthday + 3);
                MyDate birthDay = null;
                try {
                    birthDay = (MyDate) MyDate.Mydate(input);
                } catch (Exception e) {
                    logger.debug("Impossible de parser la date de naissance de " + id, e);
                }

                Town birthTown = null;
                try {
                    birthTown = new Town(genealogy.findFieldInContents("PLAC", indexBirthday, indexBirthday + 3));
                } catch (Exception e) {
                    logger.debug("Impossible de parser la ville de naissance de " + id, e);
                }
                birth = new Birth(this, birthDay, birthTown);
            }

            int indexChristening = genealogy.findIndexIdString("CHR", index, indexMax);

            if (indexChristening != -1) {
                indexChristening++;
                String input = genealogy.findFieldInContents("DATE", indexChristening, indexChristening + 5);
                MyDate christeningDay = null;
                try {
                    christeningDay = (MyDate) MyDate.Mydate(input);
                } catch (Exception e) {
                    logger.debug("Impossible de parser la date de bapteme de " + id, e);
                }

                //christening Town
                Town ChristeningTown = null;
                try {
                    ChristeningTown = new Town(genealogy.findFieldInContents("PLAC", indexChristening, indexChristening + 5));
                } catch (Exception e) {
                    logger.debug("Impossible de parser la ville de baptème de " + id, e);
                }
                //christening place
                String christeningPlace = null;
                try {
                    christeningPlace = genealogy.findFieldInContents("ADDR", indexChristening, indexChristening + 5);
                } catch (Exception e) {
                    logger.debug("Impossible de parser le lieu du baptème de " + id, e);
                }
                //godparents
                String godParents = null;
                try {
                    godParents = genealogy.findFieldInContents("_GODP", indexChristening, indexChristening + 5);
                } catch (Exception e) {
                    logger.debug("Impossible de trouver les parrains et marraines " + id, e);
                }
                christening = new Christening(this, christeningDay, ChristeningTown, godParents, christeningPlace);
            }

            sex = parseSex(genealogy.findFieldInContents("SEX", offset, indexMax));
            profession = genealogy.findFieldInContents("OCCU", offset, indexMax);
            comments = genealogy.findFieldInContents("NOTE", offset, indexMax);
            pdfStructure = PDFStructure.parsePDFStucture(comments, id);

            int indexDeath = genealogy.findIndexIdString("DEAT", index, indexMax);

            if (indexDeath != -1) {
                indexDeath++;
                String input = genealogy.findFieldInContents("DATE", indexDeath, indexDeath + 3);
                MyDate deathDay = null;
                try {
                    deathDay = (MyDate) MyDate.Mydate(input);
                } catch (Exception e) {
                    logger.debug("Impossible de parser la date de décès de " + id, e);
                }

                Town deathTown = null;
                try {
                    deathTown = new Town(genealogy.findFieldInContents("PLAC", indexDeath, indexDeath + 3));
                } catch (Exception e) {
                    logger.debug("Impossible de parser la ville de décès de " + id, e);
                }
                death = new Death(this, deathDay, deathTown);
            }
            calculateAge();
        }
    }

    private Union findUnion(Person partner) {
        for (int i = 0; i < unions.size(); i++) {
            if (unions.get(i).getPartner().getId().equals(partner.getId())) {
                return unions.get(i);
            }
            if (unions.get(i).getPerson().getId().equals(partner.getId())) {
                return unions.get(i);
            }
        }
        return null;
    }

    private ArrayList<Person> findChildren(Person partner) {
        ArrayList<Person> list = new ArrayList<Person>();
        for (int i = 0; i < children.size(); i++) {
            Person thisFather = children.get(i).getFather();
            Person thisMother = children.get(i).getMother();
            if ((thisFather != null) && (thisMother != null)) {
                if ((thisFather.getId().equals(partner.getId())) && (thisMother.getId().equals(getId()))) {
                    list.add(children.get(i));
                }
                if ((thisMother.getId().equals(partner.getId())) && (thisFather.getId().equals(getId()))) {
                    list.add(children.get(i));
                }
            }
        }
        return list;
    }

    public ArrayList<Person> findNonDesire() {
        ArrayList<Person> list = new ArrayList<Person>();
        for (int i = 0; i < children.size(); i++) {
            Person thisFather = children.get(i).getFather();
            Person thisMother = children.get(i).getMother();
            if ((thisFather == null) || (thisMother == null)) {
                list.add(children.get(i));
            }
        }
        return list;
    }

    private void calculateAge() {
        if ((birth == null) || (death == null)) {
            age = -1;
            if ((death == null) && (birth != null) && (birth.getDate() != null) && (AuxMethods.getYear(birth.getDate().getDate()) > 1916)) {
                stillAlive = true;
            }
        } else if ((birth.getDate() == null) || (death.getDate() == null)) {
            age = -1;
        } else {
            Date birthDate = birth.getDate().getDate();
            Date deathDate = death.getDate().getDate();
            age = getDiffYears(birthDate, deathDate);
            String res = "";
        }
    }

    public static int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) ||
                (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
            diff--;
        }
        return diff;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }

    public void addUnion(Union union) {
        unions.add(union);
    }

    public void addChildren(Person person) {
        children.add(person);
    }

    public void setMother(Person mother) {
        this.mother = mother;
    }

    public void setFather(Person father) {
        this.father = father;
    }

    public String printPerson() {
        String txt = "";
        if (surname.equals("...") || name.equals("...")) {
            return "";
        }
        String pronoun = "Il";
        String accord = "";
        String fils = "le fils";
        String sfils = "fils";
        boolean foundText = false;
        if (sex == Sex.FEMALE) {
            pronoun = "Elle";
            accord = "e";
            fils = "la fille";
            sfils = "fille";
        }
        txt += surname + " " + name;
        if (birth != null) {
            if ((birth.getDate() != null) && (birth.getTown() != null) && (birth.getTown().getName() != null)) {
                foundText = true;
                txt += " est né" + accord + " " + birth.getDate().descriptionDate() + " à "
                        + birth.getTown().getName() + " (" + birth.getTown().getDetail() + ")";
            } else if (birth.getDate() != null) {
                foundText = true;
                txt += " est né" + accord + " " + birth.getDate().descriptionDate() + "";
            } else if ((birth.getTown() != null) && (birth.getTown().getName() != null)) {
                foundText = true;
                txt += " est né" + accord + " à " + birth.getTown().getName() + " (" + birth.getTown().getDetail() + ")";
            }
        }
        if (foundText) {
            if ((mother != null) && (father != null)) {
                if (mother.findUnion(father).getUnionType() == UnionType.HETERO_MAR) {
                    txt += " du mariage de " + father.getFullName() + " et de " + mother.getFullName();
                } else {
                    txt += " de l'union de " + father.getFullName() + " et de " + mother.getFullName();
                }
            } else if (mother != null) {
                txt += ", " + sfils + " de " + mother.getFullName();
            } else if (father != null) {
                txt += ", " + sfils + " de " + father.getFullName();
            }
        } else {
            if ((mother != null) && (father != null)) {
                txt += " est " + fils + " de ";
                txt += father.getFullName() + " et de " + mother.getFullName();
            } else if (mother != null) {
                txt += " est " + fils + " de ";
                txt += mother.getFullName();
            } else if (father != null) {
                txt += " est " + fils + " de ";
                txt += father.getFullName();
            }
        }

        //Vivant ?
        String singularForm = "est";
        String pluralForm = "sont";
        if (death != null) {
            singularForm = "était";
            pluralForm = "étaient";
        }
        //Métier
        if ((profession == null) || (!profession.equals(""))) {
            foundText = true;
            String prof = getProfession();
            if (prof.indexOf(',') == -1) {
                //Case where nothing was written before except names
                if (txt.equals(surname + " " + name)) {
                    txt += " " + singularForm + " " + prof;
                } else {
                    txt += "\nSon métier " + singularForm + " " + prof;
                }
            } else {
                if (txt.equals(surname + " " + name)) {
                    txt += " " + pluralForm + " " + prof;
                } else {
                    txt += "\nSes métiers " + pluralForm + " " + prof;
                }
            }
        }

        for (int i = 0; i < unions.size(); i++) {
            //Mariage
            Union union = unions.get(i);
            Person partner = union.getOtherPerson(this);
            String marie = " s'est marié" + accord;
            if (union.getUnionType() != UnionType.HETERO_MAR) {
                marie = " a vécu";
            }
            if (foundText) {
                txt += "\n" + pronoun + marie + " avec " +
                        partner.getFullName();
            } else {
                foundText = true;
                txt += marie + " avec " +
                        partner.getFullName();
            }

            if ((partner.getProfession() != null) && (!partner.getProfession().equals(""))) {
                txt += ", " + partner.getProfession();
            }
            if (union.getDate() != null) {
                txt += " " + union.getDate().descriptionDate() + " ";
            }
            if ((union.getTown() != null) && (union.getTown().getName() != null)) {
                txt += "à " + union.getTown().getName() + " (" + union.getTown().getDetail() + ")";
            }

            //Enfants
            ArrayList<Person> myChildren = findChildren(partner);
            if (!myChildren.isEmpty()) {
                txt += " et a eu de cette union ";
                int nbChildren = myChildren.size();
                if (nbChildren > 1) {
                    txt += nbChildren + " enfants nommés " + myChildren.get(0).getFullName();
                    for (int j = 1; j < myChildren.size() - 1; j++) {
                        txt += ", " + myChildren.get(j).getFullName();
                    }
                    txt += " et " + myChildren.get(myChildren.size() - 1).getFullName();
                } else {
                    txt += nbChildren + " enfant nommé " + myChildren.get(0).getFullName();
                }
            }
        }
        ArrayList<Person> naturelChildren = findNonDesire();
        if (!naturelChildren.isEmpty()) {
            if (naturelChildren.size() > 1) {
                txt += "\n" + pronoun + " a eu " + naturelChildren.size() + " enfants naturels nommés " + naturelChildren.get(0).getFullName();
                for (int j = 1; j < naturelChildren.size() - 1; j++) {
                    txt += ", " + naturelChildren.get(j).getFullName();
                }
                txt += " et " + naturelChildren.get(naturelChildren.size() - 1).getFullName();
            } else {
                txt += "\n" + pronoun + " a eu 1 enfant naturel nommé " + naturelChildren.get(0).getFullName();
            }
        }

        boolean foundDeath = false;

        if (death != null) {
            if ((death.getDate() != null) && (death.getTown() != null) && (death.getTown().getName() != null)) {
                if (foundText) {
                    txt += "\n" + pronoun;
                }
                txt += " est décédé" + accord + " " + death.getDate().descriptionDate() + " à "
                        + death.getTown().getName() + " (" + death.getTown().getDetail() + ")";
                foundText = true;
                foundDeath = true;
            } else if (death.getDate() != null) {
                if (foundText) {
                    txt += "\n" + pronoun;
                }
                foundText = true;
                foundDeath = true;
                txt += " est décédé" + accord + " " + death.getDate().descriptionDate();
            } else if ((death.getTown() != null) && (death.getTown().getName() != null)) {
                if (foundText) {
                    txt += "\n" + pronoun;
                }
                foundText = true;
                foundDeath = true;
                txt += " est décédé" + accord + " à " + death.getTown().getName() + " (" + death.getTown().getDetail() + ")";
            }
        }

        if (age != -1) {
            if (age < 2) {
                txt += " à l'âge de " + age + " an";
            } else {
                txt += " à l'âge de " + age + " ans";
            }
        }

        if (txt.equals(surname + " " + name)) {
            txt = "";
        } else {
            txt += ".";
        }
        return txt.replace("\n", ".\n");
    }


    public ArrayList<Person> getHalfSiblings() {
        ArrayList<Person> result = new ArrayList<Person>();
        if (father != null) {
            ArrayList<Person> children = father.getChildren();
            for (Person person : children) {
                if (person.getMother() != null && !person.getMother().equals(mother) && !person.equals(this)) {
                    result.add(person);
                }
            }
        }
        if (mother != null) {
            ArrayList<Person> children = mother.getChildren();
            for (Person person : children) {
                if (!person.getMother().equals(father) && !person.equals(this)) {
                    result.add(person);
                }
            }
        }
        return result;
    }

    public ArrayList<Person> getSiblings() {
        ArrayList<Person> result = new ArrayList<Person>();
        if (father != null) {
            ArrayList<Person> children = father.getChildren();
            for (Person person : children) {
                if (person.getMother() != null && person.getMother().equals(mother) && !person.equals(this)) {
                    result.add(person);
                }
            }
        }
        return result;
    }

    public void printNecessaryResearch() {
        if (!StringUtils.equals(getSurname(), "...")) {
            boolean found = false;
            String result = "";
            if (birth != null) {
                String birthTxt = birth.getNecessaryResearch();
                if (birthTxt != null) {
                    found = true;
                    result += ", Birth=[" + birthTxt + "]";
                }
            } else {
                result += ", Birth=[Date Town]";
            }

            ArrayList<Union> unions = getUnions();
            if (unions != null) {
                String unionTxt = "";
                for (Union union : unions) {
                    String inputUnion = union.getNecessaryResearch();
                    if (inputUnion != null) {
                        unionTxt += inputUnion;
                    }
                }
                if (!unionTxt.equals("")) {
                    found = true;
                    result += ", Union=[" + unionTxt + "]";
                }
            } else {
                result += ", Union=[Date Town]";
            }

            if (death != null) {
                String deathTxt = death.getNecessaryResearch();
                if (deathTxt != null) {
                    found = true;
                    result += ", Death=[" + deathTxt + "]";
                }
            } else {
                result += ", Death=[Date Town]";
            }
            if (found) {
                logger.info(getFullName() + " : " + result.substring(2));
            }
        }
    }
}
