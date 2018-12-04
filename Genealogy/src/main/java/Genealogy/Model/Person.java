package Genealogy.Model;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Genealogy.AuxMethods;
import Genealogy.MapViewer.Structures.MapStructure;
import Genealogy.Model.Act.Act;
import Genealogy.Model.Act.Birth;
import Genealogy.Model.Act.Death;
import Genealogy.Model.Act.Union;
import Genealogy.Model.Date.FullDate;
import Genealogy.Model.Date.MyDate;
import Genealogy.Parsing.PDFStructure;
import Genealogy.Parsing.ParsingStructure;
import javafx.util.Pair;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Period;

/**
 * Created by Dan on 05/04/2016.
 */
public class Person {

    private String id;

    public enum Sex {
        HOMME,
        FEMME,
        INCONNU;
    }
    private Sex sex;
    private String name;
    private String surname;
    private Birth birth;
    private Death death;
    private String profession;
    private String note;
    private PDFStructure pdfStructure;
    private ArrayList<Union> unions = new ArrayList<Union>();
    private Person father;
    private Person mother;
    private ArrayList<Person> children = new ArrayList<Person>();
    private boolean directAncestor = false;
    private int age;
    private static HashMap<Integer,ArrayList<MapStructure>> periods = new HashMap<>();
    private static HashMap<Integer,ArrayList<MapStructure>> periodsDirectAncestors = new HashMap<>();
    private static int minimumPeriod = 10000;
    private boolean stillAlive = false;
    final static Logger logger = Logger.getLogger(Person.class);

    public static HashMap<Integer,ArrayList<MapStructure>> getPeriods() {
        return periods;
    }

    public static HashMap<Integer, ArrayList<MapStructure>> getPeriodsDirectAncestors() {
        return periodsDirectAncestors;
    }

    public boolean isStillAlive() {
        return stillAlive;
    }

    public static Sex parseSex(String s){
        switch (s){
            case "M":
                return Sex.HOMME;
            case "F":
                return Sex.FEMME;
            default:
                return Sex.INCONNU;
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

    public String getNote() {
        return note;
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

    public String getFullName(){
        return surname + " " + name;
    }

    /**
     * Fonction getFullNameInverted
     * @return le nom puis le prénom
     */
    public String getFullNameInverted(){
        if ((name != null)&&(!name.equals(""))){
            return name + " " + surname;
        }
        return surname;
    }

    public String getName4Comparator(){
        String txt = "";
        if ((name.equals("..."))||(surname.equals("..."))){
            txt = "z";
        }
        return txt + name + " " + surname;
    }

    public void initPeriods2(ArrayList<Pair<MyDate,Town>> tempPeriods){
        //System.out.println(tempPeriods);
        if (tempPeriods.size() >= 1){
            if (tempPeriods.size() == 1){
                Date date = new Date(tempPeriods.get(0).getKey().getDate().getTime());
                MapStructure mapStructure =
                        new MapStructure(tempPeriods.get(0).getValue(),getFullName(),getAge(date,0));
                addPeriod((int) tempPeriods.get(0).getKey().getYear(),mapStructure);
            } else {
                for (int i = 0 ; i < tempPeriods.size()-1 ; i++){
                    int date1 = (int) tempPeriods.get(i).getKey().getYear();
                    int date2 = (int) tempPeriods.get(i+1).getKey().getYear();
                    int index = 0;
                    for (int k = date1 ; k < date2 ; k++){
                        Date date = new Date(tempPeriods.get(i).getKey().getDate().getTime());
                        MapStructure mapStructure =
                                new MapStructure(tempPeriods.get(i).getValue(),getFullName(),getAge(date,index));
                        addPeriod(k,mapStructure);
                        //System.out.println(periods);
                        index++;
                    }
                }
                Date date = new Date(tempPeriods.get(tempPeriods.size()-1).getKey().getDate().getTime());
                MapStructure mapStructure =
                        new MapStructure(tempPeriods.get(tempPeriods.size()-1).getValue(),getFullName(),getAge(date,0));
                addPeriod((int) tempPeriods.get(tempPeriods.size()-1).getKey().getYear(),mapStructure);
            }
        }
    }

    public ArrayList<Pair<MyDate,Town>> initPeriods1(){
        ArrayList<Pair<MyDate,Town>> tempPeriods = new ArrayList<>();

        //Naissance
        if ((birth != null)&&(birth.getTown() != null)&&(birth.getTown().getName() != null)&&(birth.getDate() != null)){
            tempPeriods.add(new Pair<MyDate, Town>(birth.getDate(),birth.getTown()));
        }

        //Unions
        for (int i = 0 ; i < unions.size() ; i++){
            if ((unions.get(i).getDate() != null)&&(unions.get(i).getTown()!=null)&&(unions.get(i).getTown().getName()!=null)){
                tempPeriods.add(new Pair<MyDate, Town>(unions.get(i).getDate(),unions.get(i).getTown()));
            }
        }

        //Enfants
        for (int i = 0 ; i < children.size() ; i++){
            if ((children.get(i).getBirth() != null) &&
                    (children.get(i).getBirth().getDate() != null)&&
                    (children.get(i).getBirth().getTown()!=null)&&
                    (children.get(i).getBirth().getTown().getName()!=null)){
                tempPeriods.add(new Pair<MyDate, Town>(children.get(i).getBirth().getDate(),children.get(i).getBirth().getTown()));
            }
        }

        //Décès
        if ((death != null)&&(death.getTown() != null)&&(death.getTown().getName() != null)&&(death.getDate() != null)){
            tempPeriods.add(new Pair<MyDate, Town>(death.getDate(),death.getTown()));

            Collections.sort(tempPeriods, new Comparator<Pair<MyDate, Town>>() {
                @Override
                public int compare(Pair<MyDate, Town> o1, Pair<MyDate, Town> o2) {
                    return o1.getKey().getDate().compareTo(o2.getKey().getDate());
                }
            });

            //Cas des enfants nés après la mort
            while (tempPeriods.get(tempPeriods.size()-1).getKey().getDate().getTime() > death.getDate().getDate().getTime()){
                tempPeriods.remove(tempPeriods.size()-1);
            }
        } else {
            Collections.sort(tempPeriods, new Comparator<Pair<MyDate, Town>>() {
                @Override
                public int compare(Pair<MyDate, Town> o1, Pair<MyDate, Town> o2) {
                    return o1.getKey().getDate().compareTo(o2.getKey().getDate());
                }
            });
            if ((stillAlive)&&(!tempPeriods.isEmpty())){
                tempPeriods.add(new Pair<MyDate, Town>(new FullDate(),tempPeriods.get(tempPeriods.size()-1).getValue()));
            }
        }
        return tempPeriods;
    }


    public int getProofUnionSize(){
        int nbUnions = 0;
        for (Union union : unions){
            nbUnions += union.getProofs().size();
        }
        return nbUnions;
    }

    /**
     * Fonction removePDFStructure
     * Remove PDFStructure in note
     */
    public void removePDFStructure(){
        String PDFStructureRegex = "¤PDF" + id + "¤";
        if (note.contains(PDFStructureRegex)){
            String PDFRegex = "(.*)" + PDFStructureRegex + "{.*}(.*)";
            Pattern pattern = Pattern.compile(PDFRegex,Pattern.DOTALL);
            Matcher matcher = pattern.matcher(note);
            if (matcher.find() && matcher.groupCount() == 2){
                note = matcher.group(1) + matcher.group(2);
            }
        }
    }

    public void savePDFStructure(){
        note += pdfStructure;
    }

    /**
     * Fonction addProof
     * @param typeActe Birth, Mariage, Death
     * @param proof Nom du PDF String
     * @param unionIndex numéro de l'union
     */
    public void addProof(Act.TypeActe typeActe, String proof, int unionIndex) throws Exception {
        switch (typeActe){
            case Birth:
                removePDFStructure();
                birth.addProof(proof);
                pdfStructure.addToPDFBirthList(proof);
                savePDFStructure();
                break;
            case Mariage:
                removePDFStructure();
                if (unionIndex < unions.size()){
                    unions.get(unionIndex).addProof(proof);
                    pdfStructure.addToPDFMarriageList(proof);
                    savePDFStructure();
                } else {
                    throw new Exception("Index trop élevé");
                }
                break;
            case Death:
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
     * @param typeActe Birth, Death
     * @param proof Nom du PDF String
     */
    public void addProof(Act.TypeActe typeActe, String proof) throws Exception {
        addProof(typeActe,proof,0);
    }

    public static int getMinimumPeriod() {
        return minimumPeriod;
    }

    public void initPeriods(){
        initPeriods2(initPeriods1());
    }

    /**
     * isPrintable retourne vrai si la personne a un prénom et un nom de famille différent de ...
     * @return
     */
    public boolean isPrintable(){
        return (name!=null) && (surname!=null) && (!name.equals("...")) && (!surname.equals("..."));
    }

    public void addPeriod(int year, MapStructure mapStructure){
        if (year < minimumPeriod){
            minimumPeriod = year;
        }
        if (periods.containsKey(year)){
            periods.get(year).add(mapStructure);
        } else {
            ArrayList<MapStructure> structure = new ArrayList<>();
            structure.add(mapStructure);
            periods.put(year,structure);
        }
        if (directAncestor){
            if (periodsDirectAncestors.containsKey(year)){
                periodsDirectAncestors.get(year).add(mapStructure);
            } else {
                ArrayList<MapStructure> structure = new ArrayList<>();
                structure.add(mapStructure);
                periodsDirectAncestors.put(year,structure);
            }
        }
    }

    public int getAge(Date date, int years){
        if ((birth != null)&&(birth.getDate() != null)){
            Date d0 = birth.getDate().getDate();
            long diff = date.getTime() - birth.getDate().getDate().getTime();
            int days = (int) TimeUnit.DAYS.toDays(diff);
            int a = days/365;
            DateTime dateTime0 = new DateTime(birth.getDate().getDate().getTime());
            DateTime dateTime1 = new DateTime(date.getTime());
            Period period = new Period(dateTime0, dateTime1);
            return period.getYears() + years;
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        String res =  "Person{" +
                "id='" + id + '\'' +
                ", sex=" + sex +
                ", age=" + age +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", birth=" + birth +
                ", death=" + death +
                ", profession='" + profession + '\'';


        if (mother != null){
            res += ", mother='" + mother.getFullName() + '\'';
        }
        if (father != null){
            res += ", father='" + father.getFullName() + '\'';
        }

        res += ", note='" + note + '\'' +
                ", unions=" + unions + '\'' +
                ", directAncestor=" + directAncestor + '\'' +
                ", children={";

        if (!children.isEmpty()){
            res += children.get(0).getFullName();
        }
        for (int i = 1; i < children.size() ; i++){
            res += ", " + children.get(i).getFullName();
        }

        return res + "}}";
    }

    public Person(ArrayList<ParsingStructure> list, int offset, int indexMax) {
        int index = offset;
        if (offset >= indexMax) {
            logger.error("Erreur dans le parsing de personne, offset >= indexMax");
            return;
        }
            id = list.get(index++).getId();
            name = AuxMethods.findField(list,"SURN",index++,indexMax);
            surname = AuxMethods.findField(list,"GIVN",index++,indexMax);
            int indexBirthday = AuxMethods.findIndexNumberString(list,"BIRT",index,indexMax);

        if (indexBirthday != -1){
            indexBirthday++;
            String input = AuxMethods.findField(list,"DATE",indexBirthday,indexBirthday+2);
            MyDate birthDay = null;
            try{
                birthDay = (MyDate) MyDate.Mydate(input);
            }
            catch (Exception e){
                //System.out.println("Impossible de parser la date de naissance de " + id);
            }

            Town birthTown = null;
            try {
                birthTown = new Town(AuxMethods.findField(list,"PLAC",indexBirthday,indexBirthday+2));
            } catch (Exception e) {
                //System.out.println("Impossible de parser la ville de naissance de " + id);
            }
            birth = new Birth(this,birthDay,birthTown);
        }

        sex = parseSex(AuxMethods.findField(list,"SEX",offset,indexMax));
        profession = AuxMethods.findField(list,"OCCU",offset,indexMax);
        note = AuxMethods.findField(list,"NOTE",offset,indexMax);
        pdfStructure = PDFStructure.parsePDFStucture(note,id);

        int indexDeath = AuxMethods.findIndexNumberString(list,"DEAT",index,indexMax);

        if (indexDeath != -1){
            indexDeath++;
            String input = AuxMethods.findField(list,"DATE",indexDeath,indexDeath+2);
            MyDate deathDay = null;
            try{
                deathDay = (MyDate) MyDate.Mydate(input);
            }
            catch (Exception e){
                //System.out.println("Impossible de parser la date de décès de " + id);
            }

            Town deathTown = null;
            try {
                deathTown = new Town(AuxMethods.findField(list,"PLAC",indexDeath,indexBirthday+2));
            } catch (Exception e) {
                //System.out.println("Impossible de parser la ville de décès de " + id);
            }
            death = new Death(this,deathDay,deathTown);
        }

        calculateAge();
    }

    private Union findUnion(Person partner){
        for (int i = 0 ; i < unions.size() ; i++){
            if (unions.get(i).getPartner().getId().equals(partner.getId())){
                return unions.get(i);
            }
            if (unions.get(i).getCitizen().getId().equals(partner.getId())){
                return unions.get(i);
            }
        }
        return null;
    }

    private ArrayList<Person> findChildren(Person partner){
        ArrayList<Person> list = new ArrayList<Person>();
        for (int i = 0 ; i < children.size() ; i++){
            Person thisFather = children.get(i).getFather();
            Person thisMother = children.get(i).getMother();
            if ((thisFather != null)&&(thisMother != null)){
                if ((thisFather.getId().equals(partner.getId()))&&(thisMother.getId().equals(getId())))
                {
                    list.add(children.get(i));
                }
                if ((thisMother.getId().equals(partner.getId()))&&(thisFather.getId().equals(getId())))
                {
                    list.add(children.get(i));
                }
            }
        }
        return list;
    }

    public ArrayList<Person> findNonDesire(){
        ArrayList<Person> list = new ArrayList<Person>();
        for (int i = 0 ; i < children.size() ; i++){
            Person thisFather = children.get(i).getFather();
            Person thisMother = children.get(i).getMother();
            if ((thisFather == null)||(thisMother == null)){
                list.add(children.get(i));
            }
        }
        return list;
    }

    private void calculateAge() {
        if ((birth == null)||(death == null)){
            age = -1;
            if ((death == null)&&(birth != null)&&(birth.getDate() != null)&&(AuxMethods.getYear(birth.getDate().getDate()) > 1916)){
                stillAlive = true;
            }
        } else if ((birth.getDate() == null)||(death.getDate() == null)){
            age = -1;
        } else {
            Date birthDate = birth.getDate().getDate();
            Date deathDate = death.getDate().getDate();
            age = getDiffYears(birthDate,deathDate);
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

    public void addUnion(Union union){
        unions.add(union);
    }

    public void addChildren(Person person){
        children.add(person);
    }

    public void setMother(Person mother) {
        this.mother = mother;
    }

    public void setFather(Person father) {
        this.father = father;
    }

    public String printPerson(){
        String txt = "";
        if (surname.equals("...")||name.equals("...")){
            return "";
        }
        String pronoun = "Il";
        String accord = "";
        String fils = "le fils";
        String sfils = "fils";
        boolean foundText = false;
        if (sex == Sex.FEMME){
            pronoun = "Elle";
            accord = "e";
            fils = "la fille";
            sfils = "fille";
        }
        txt += surname + " " + name ;
        if (birth != null){
            if ((birth.getDate() != null)&&(birth.getTown() != null)&&(birth.getTown().getName() != null)){
                foundText = true;
                txt += " est né" + accord + " " + birth.getDate().descriptionDate() + " à "
                + birth.getTown().getName() + " (" + birth.getTown().getDetail() + ")";
            } else if (birth.getDate() != null){
                foundText = true;
                txt += " est né" + accord + " " + birth.getDate().descriptionDate() + "";
            } else if ((birth.getTown() != null)&&(birth.getTown().getName() != null)){
                foundText = true;
                txt+= " est né" + accord + " à " + birth.getTown().getName() + " (" + birth.getTown().getDetail() + ")";
            }
        }
        if (foundText){
            if ((mother != null)&&(father != null)){
                if (mother.findUnion(father).getState() == Union.State.MARIAGE_HETERO){
                    txt += " du mariage de " + father.getFullName() + " et de " + mother.getFullName();
                } else {
                    txt += " de l'union de " + father.getFullName() + " et de " + mother.getFullName();
                }
            } else if (mother != null){
                txt += ", " + sfils + " de " + mother.getFullName();
            } else if (father != null){
                txt += ", " + sfils + " de " + father.getFullName();
            }
        } else {
            if ((mother != null)&&(father != null)){
                txt += " est " + fils + " de ";
                txt += father.getFullName() + " et de " + mother.getFullName();
            } else if (mother != null){
                txt += " est " + fils + " de ";
                txt += mother.getFullName();
            } else if (father != null){
                txt += " est " + fils + " de ";
                txt += father.getFullName();
            }
        }

        //Vivant ?
        String singularForm = "est";
        String pluralForm = "sont";
        if (death != null){
            singularForm = "était";
            pluralForm = "étaient";
        }
        //Métier
        if ((profession == null)||(!profession.equals(""))){
            foundText = true;
            String prof = getProfession();
            if (prof.indexOf(',') == -1){
                //Case where nothing was written before except names
                if (txt.equals(surname + " " + name)){
                    txt += " " + singularForm + " " + prof;
                } else {
                    txt += "\nSon métier " + singularForm + " " + prof;
                }
            } else {
                if (txt.equals(surname + " " + name)){
                    txt += " " + pluralForm + " " + prof;
                } else {
                    txt += "\nSes métiers " + pluralForm + " " + prof;
                }
            }
        }

        for (int i = 0 ; i < unions.size() ; i++){
                //Mariage
                Union union = unions.get(i);
                Person partner = union.getOtherPerson(this);
                String marie = " s'est marié" + accord;
                if (union.getState() != Union.State.MARIAGE_HETERO){
                    marie = " a vécu";
                }
            if (foundText){
                txt += "\n" + pronoun + marie + " avec " +
                        partner.getFullName();
            } else {
                foundText = true;
                txt += marie + " avec " +
                        partner.getFullName();
            }

                if ((partner.getProfession() != null)&&(!partner.getProfession().equals(""))){
                    txt += ", " + partner.getProfession();
                }
                if (union.getDate() != null){
                    txt += " " + union.getDate().descriptionDate() + " ";
                }
                if ((union.getTown() != null)&&(union.getTown().getName() != null)){
                    txt += "à " + union.getTown().getName() + " (" + union.getTown().getDetail() + ")";
                }

                //Enfants
                ArrayList<Person> myChildren = findChildren(partner);
                if (!myChildren.isEmpty()){
                    txt += " et a eu de cette union ";
                    int nbChildren =  myChildren.size();
                    if (nbChildren > 1){
                        txt += nbChildren + " enfants nommés " + myChildren.get(0).getFullName();
                        for (int j = 1 ; j < myChildren.size()-1 ; j++){
                            txt += ", " + myChildren.get(j).getFullName();
                        }
                        txt += " et " + myChildren.get(myChildren.size()-1).getFullName();
                    } else {
                        txt += nbChildren + " enfant nommé " + myChildren.get(0).getFullName();
                    }
                }
        }
        ArrayList<Person> naturelChildren = findNonDesire();
        if (!naturelChildren.isEmpty()){
            if (naturelChildren.size() > 1){
                txt += "\n" + pronoun + " a eu " + naturelChildren.size() + " enfants naturels nommés " + naturelChildren.get(0).getFullName();
                for (int j = 1 ; j < naturelChildren.size()-1 ; j++){
                    txt += ", " + naturelChildren.get(j).getFullName();
                }
                txt += " et " + naturelChildren.get(naturelChildren.size()-1).getFullName();
            } else {
                txt += "\n" + pronoun + " a eu 1 enfant naturel nommé " + naturelChildren.get(0).getFullName();
            }
        }

        boolean foundDeath = false;

        if (death != null){
            if ((death.getDate() != null)&&(death.getTown() != null)&&(death.getTown().getName() != null)){
                if (foundText){
                    txt += "\n" + pronoun;
                }
                txt += " est décédé" + accord + " " + death.getDate().descriptionDate() + " à "
                        + death.getTown().getName() + " (" + death.getTown().getDetail() + ")";
                foundText = true;
                foundDeath = true;
            } else if (death.getDate() != null){
                if (foundText){
                    txt += "\n" + pronoun;
                }
                foundText = true;
                foundDeath = true;
                txt += " est décédé" + accord + " " + death.getDate().descriptionDate();
            } else if ((death.getTown() != null)&&(death.getTown().getName() != null)){
                if (foundText){
                    txt += "\n" + pronoun;
                }
                foundText = true;
                foundDeath = true;
                txt+= " est décédé" + accord + " à " + death.getTown().getName() + " (" + death.getTown().getDetail() + ")";
            }
        }

        if (age != -1){
            if (age < 2){
                txt += " à l'âge de " + age + " an";
            } else {
                txt += " à l'âge de " + age + " ans";
            }
        }

        if (txt.equals(surname + " " + name)){
            txt = "";
        } else {
            txt += ".";
        }
        return txt.replace("\n",".\n");
    }



    public ArrayList<Person> getHalfSiblings() {
        ArrayList<Person> result = new ArrayList<Person>();
        if (father != null){
            ArrayList<Person> children = father.getChildren();
            for (Person person : children){
                if (!person.getMother().equals(mother) && !person.equals(this)){
                    result.add(person);
                }
            }
        }
        if (mother != null){
            ArrayList<Person> children = mother.getChildren();
            for (Person person : children){
                if (!person.getMother().equals(father) && !person.equals(this)){
                    result.add(person);
                }
            }
        }
        return result;
    }

    public ArrayList<Person> getSiblings() {
        ArrayList<Person> result = new ArrayList<Person>();
        if (father != null){
            ArrayList<Person> children = father.getChildren();
            for (Person person : children){
                if (person.getMother().equals(mother) && !person.equals(this)){
                    result.add(person);
                }
            }
        }
        return result;
    }
}
