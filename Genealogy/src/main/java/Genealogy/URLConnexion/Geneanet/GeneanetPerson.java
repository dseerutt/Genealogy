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
    private GeneanetPerson father;
    private GeneanetPerson mother;
    private ArrayList<GeneanetPerson> siblings;
    private ArrayList<GeneanetPerson> halfSiblings;
    private ArrayList<GeneanetPerson> children;
    private HashMap<GeneanetPerson,HashMap<MyDate,String>> marriage;
    private boolean searched = false;
    private boolean directAncestor = false;
    private String geneanetUrl;

    public GeneanetPerson(String url, String firstName, String familyName) {
        this.url = url;
        this.firstName = firstName;
        this.familyName = familyName;
        siblings = new ArrayList<>();
        halfSiblings = new ArrayList<>();
        marriage = new HashMap<>();
        children = new ArrayList<>();
    }

    public GeneanetPerson(String url) {
        this.url = url;
        siblings = new ArrayList<>();
        halfSiblings = new ArrayList<>();
        marriage = new HashMap<>();
        children = new ArrayList<>();
    }

    public void setSearched(boolean searched) {
        this.searched = searched;
    }

    public boolean isSearched() {
        return searched;
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

    public GeneanetPerson getFather() {
        return father;
    }

    public void setFather(GeneanetPerson father) {
        this.father = father;
    }

    public GeneanetPerson getMother() {
        return mother;
    }

    public void setMother(GeneanetPerson mother) {
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

    public HashMap<GeneanetPerson, HashMap<MyDate, String>> getMarriage() {
        return marriage;
    }

    public void setMarriage(HashMap<GeneanetPerson, HashMap<MyDate, String>> marriage) {
        this.marriage = marriage;
    }

    public ArrayList<GeneanetPerson> getHalfSiblings() {
        return halfSiblings;
    }

    public void setHalfSiblings(ArrayList<GeneanetPerson> halfSiblings) {
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

    public String getGeneanetUrl() {
        return geneanetUrl;
    }

    public void setGeneanetUrl(String geneanetUrl) {
        this.geneanetUrl = geneanetUrl;
    }

    public boolean isDirectAncestor() {
        return directAncestor;
    }

    public void setDirectAncestor(boolean directAncestor) {
        this.directAncestor = directAncestor;
    }

    @Override
    public String toString() {
        if (firstName != null ) {
            String result =  "GeneanetPerson{";
            result += (url != null) ? "url='" + url + '\'' : "";
            result += (geneanetUrl != null) ? ", geneanetURL='" + geneanetUrl + '\'' : "";
            result += (firstName != null) ? ", firstName='" + firstName + '\'' : "";
            result += (familyName != null) ? ", familyName='" + familyName + '\'' : "";
            result += (gender != null) ? ", gender='" + gender + '\'' : "";
            result += (birthDate != null) ? ", birthDate='" + birthDate + '\'' : "";
            result += (placeOfBirth != null) ? ", placeOfBirth='" + placeOfBirth + '\'' : "";
            result += (deathDate != null) ? ", deathDate='" + deathDate + '\'' : "";
            result += (placeOfDeath != null) ? ", placeOfDeath='" + placeOfDeath + '\'' : "";
            result += (father != null) ? ", father='" + father + '\'' : "";
            result += (mother != null) ? ", mother='" + mother + '\'' : "";
            result += (siblings != null && !siblings.isEmpty()) ? ", siblings='" + siblings + '\'' : "";
            result += (halfSiblings != null && !halfSiblings.isEmpty()) ? ", halfSiblings='" + halfSiblings + '\'' : "";
            result += (children != null && !children.isEmpty()) ? ", children='" + children + '\'' : "";
            result += (marriage != null && !marriage.isEmpty()) ? ", marriage='" + marriage + '\'' : "";
            result += ", searched='" + searched + '\'';
            result += ", directAncestor='" + directAncestor + '\'';
            return result;
        } else {
            return  "url='" + url;
        }

    }

    public void addSibling(GeneanetPerson personString) {
        siblings.add(personString);
    }

    public void addHalfSibling(GeneanetPerson personString) {
        halfSiblings.add(personString);
    }


    public void addChild(GeneanetPerson child) {
        children.add(child);
    }

    public void addMarriage(String personString, MyDate date, String city) {
        HashMap<MyDate, String> partnerMap = new HashMap<MyDate, String>();
        partnerMap.put(date,city);
        GeneanetPerson person = new GeneanetPerson(personString);
        marriage.put(person, partnerMap);
    }
}
