package Genealogy.Model;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import Genealogy.AuxMethods;
import Genealogy.Model.Act.Birth;
import Genealogy.Model.Act.Death;
import Genealogy.Model.Act.Union;
import Genealogy.Model.Date.FullDate;
import Genealogy.Model.Date.MonthDate;
import Genealogy.Model.Date.MyDate;
import Genealogy.Model.Date.YearDate;
import javafx.scene.Parent;
import org.joda.time.Interval;

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
    private ArrayList<Union> unions = new ArrayList<Union>();
    private Person father;
    private Person mother;
    private ArrayList<Person> children = new ArrayList<Person>();
    private boolean directAncestor = false;
    private int age;


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

    public String getFullNameInverted(){
        return name + " " + surname;
    }

    public String getName4Comparator(){
        String txt = "";
        if ((name.equals("..."))||(surname.equals("..."))){
            txt = "z";
        }
        return txt + name + " " + surname;
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
                ", profession='" + profession + '\'' +
                ", note='" + note + '\'' +
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

    public Person(ArrayList<Structure> list, int offset, int indexMax) {
        int index = offset;
        if (offset >= indexMax) {
            System.out.println("Erreur dans le parsing de personne, offset >= indexMax");
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
                deathTown = new Town(AuxMethods.findField(list,"PLAC",indexBirthday,indexBirthday+2));
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
        } else if ((birth.getDate() == null)||(death.getDate() == null)){
            age = -1;
        } else {
            Date birthDate = birth.getDate().getDate();
            Date deathDate = death.getDate().getDate();
            age = getDiffYears(birthDate,deathDate);
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

        //Métier
        if ((profession == null)||(!profession.equals(""))){
            foundText = true;
            String prof = getProfession();
            if (prof.indexOf(',') == -1){
                if (txt.equals(surname + " " + name)){
                    txt += " était " + prof;
                } else {
                    txt += "\nSon métier était " + prof;
                }
            } else {
                if (txt.equals(surname + " " + name)){
                    txt += " était " + prof;
                } else {
                    txt += "\nSes métiers étaient " + prof;
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
}
