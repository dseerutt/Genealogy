package Genealogy.URLConnexion.Geneanet;

import Genealogy.Model.Date.MyDate;
import Genealogy.Model.Town;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Dan on 07/11/2017.
 */
public class GeneanetPerson {
    private String url;
    private Gender gender;
    private String firstName;
    private String familyName;
    private MyDate birthDate;
    private String placeOfBirth;
    private MyDate deathDate;
    private String placeOfDeath;
    private String father;
    private String mother;
    private ArrayList<String> siblings;
    private ArrayList<String> halfSiblings;
    private ArrayList<String> children;
    private HashMap<String,HashMap<MyDate,String>> marriage;

    public GeneanetPerson(String url, String firstName, String familyName) {
        this.url = url;
        this.firstName = firstName;
        this.familyName = familyName;
        siblings = new ArrayList<>();
        halfSiblings = new ArrayList<>();
        marriage = new HashMap<>();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public MyDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(MyDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public MyDate getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(MyDate deathDate) {
        this.deathDate = deathDate;
    }

    public String getPlaceOfDeath() {
        return placeOfDeath;
    }

    public void setPlaceOfDeath(String placeOfDeath) {
        this.placeOfDeath = placeOfDeath;
    }

    public String getFather() {
        return father;
    }

    public void setFather(String father) {
        this.father = father;
    }

    public String getMother() {
        return mother;
    }

    public void setMother(String mother) {
        this.mother = mother;
    }

    public ArrayList<String> getSiblings() {
        return siblings;
    }

    public void setSiblings(ArrayList<String> siblings) {
        this.siblings = siblings;
    }

    public ArrayList<String> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<String> children) {
        this.children = children;
    }

    public HashMap<String, HashMap<MyDate, String>> getMarriage() {
        return marriage;
    }

    public void setMarriage(HashMap<String, HashMap<MyDate, String>> marriage) {
        this.marriage = marriage;
    }

    public ArrayList<String> getHalfSiblings() {
        return halfSiblings;
    }

    public void setHalfSiblings(ArrayList<String> halfSiblings) {
        this.halfSiblings = halfSiblings;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "GeneanetPerson{" +
                "url='" + url + '\'' +
                ", firstName='" + firstName + '\'' +
                ", familyName='" + familyName + '\'' +
                ", gender='" + gender + '\'' +
                ", birthDate=" + birthDate +
                ", placeOfBirth=" + placeOfBirth +
                ", deathDate=" + deathDate +
                ", placeOfDeath=" + placeOfDeath +
                ", father='" + father + '\'' +
                ", mother='" + mother + '\'' +
                ", siblings=" + siblings +
                ", halfSiblings=" + halfSiblings +
                ", children=" + children +
                ", marriage=" + marriage +
                '}';
    }

    public void addSibling(String personString) {
        siblings.add(personString);
    }

    public void addHalfSibling(String personString) {
        halfSiblings.add(personString);
    }

    public void addMarriage(String personString, MyDate date, String city) {
        HashMap<MyDate, String> partnerMap = new HashMap<MyDate, String>();
        partnerMap.put(date,city);
        marriage.put(personString, partnerMap);
    }
}
