package Genealogy.Model;

import java.util.ArrayList;

import Genealogy.AuxMethods;
import Genealogy.Model.Act.Birth;
import Genealogy.Model.Act.Death;
import Genealogy.Model.Act.Union;
import Genealogy.Model.Date.MyDate;

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

    @Override
    public String toString() {
        String res =  "Person{" +
                "id='" + id + '\'' +
                ", sex=" + sex +
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
}
