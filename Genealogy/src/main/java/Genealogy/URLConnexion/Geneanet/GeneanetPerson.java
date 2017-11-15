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
    private String firstName;
    private String familyName;
    private MyDate birthDate;
    private Town placeOfBirth;
    private MyDate deathDate;
    private Town placeOfDeath;
    private String father;
    private String mother;
    private ArrayList<GeneanetPerson> siblings;
    private ArrayList<GeneanetPerson> children;
    private HashMap<GeneanetPerson,HashMap<MyDate,Town>> marriage;

    public GeneanetPerson(String url, String firstName, String familyName) {
        this.url = url;
        this.firstName = firstName;
        this.familyName = familyName;
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

    public Town getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(Town placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public MyDate getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(MyDate deathDate) {
        this.deathDate = deathDate;
    }

    public Town getPlaceOfDeath() {
        return placeOfDeath;
    }

    public void setPlaceOfDeath(Town placeOfDeath) {
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

    public ArrayList<GeneanetPerson> getSiblings() {
        return siblings;
    }

    public void setSiblings(ArrayList<GeneanetPerson> siblings) {
        this.siblings = siblings;
    }

    public ArrayList<GeneanetPerson> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<GeneanetPerson> children) {
        this.children = children;
    }

    public HashMap<GeneanetPerson, HashMap<MyDate, Town>> getMarriage() {
        return marriage;
    }

    public void setMarriage(HashMap<GeneanetPerson, HashMap<MyDate, Town>> marriage) {
        this.marriage = marriage;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "GeneanetPerson{" +
                "url='" + url + '\'' +
                ", firstName='" + firstName + '\'' +
                ", familyName='" + familyName + '\'' +
                ", birthDate=" + birthDate +
                ", placeOfBirth=" + placeOfBirth +
                ", deathDate=" + deathDate +
                ", placeOfDeath=" + placeOfDeath +
                ", father='" + father + '\'' +
                ", mother='" + mother + '\'' +
                ", siblings=" + siblings +
                ", children=" + children +
                ", marriage=" + marriage +
                '}';
    }
}
