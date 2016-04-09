package Genealogy.Model;

import Genealogy.Genealogy;

/**
 * Created by Dan on 04/04/2016.
 */
public class Structure {
    private int number;
    private String id;
    private String text;

    public Structure(int number, String id, String text) {
        this.number = number;
        this.id = id;
        this.text = text;
    }

    @Override
    public String toString() {
        return "Structure{" +
                "number=" + number +
                ", id='" + id + '\'' +
                ", text='" + text + '\'' +
                '}';
    }

    public String toString2() {
        return number + " " + id + " " + text;
    }

    public int getNumber() {
        return number;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}
