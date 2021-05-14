package Genealogy.URLConnexion.Geneanet;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * Created by Dan on 02/12/2018.
 */
public class GeneanetTree implements Serializable {
    private String name;
    private String url;
    private int peopleNumber;
    private String gedcomId = StringUtils.EMPTY;

    public GeneanetTree(String name, String url, String peopleNumber) {
        this.name = name;
        this.url = url;
        this.peopleNumber = Integer.parseInt(peopleNumber);
    }

    public GeneanetTree(String name, String url, String peopleNumber, String gedcomId) {
        this.name = name;
        this.url = url;
        this.peopleNumber = Integer.parseInt(peopleNumber);
        this.gedcomId = gedcomId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPeopleNumber() {
        return peopleNumber;
    }

    public void setPeopleNumber(int peopleNumber) {
        this.peopleNumber = peopleNumber;
    }

    public String getGedcomId() {
        return gedcomId;
    }

    public void setGedcomId(String gedcomId) {
        this.gedcomId = gedcomId;
    }

    @Override
    public String toString() {
        return "GeneanetTree{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", peopleNumber=" + peopleNumber +
                ", gedcomId=" + gedcomId +
                '}';
    }

    public String print() {
        String separator = ";";
        return name + separator + url + separator + peopleNumber + separator + gedcomId;
    }
}
