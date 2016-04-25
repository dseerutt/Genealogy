package Genealogy.Parsing;

import Genealogy.Genealogy;

/**
 * Created by Dan on 04/04/2016.
 */
public class ParsingStructure {
    private int number;
    private String id;
    private String text;

    public ParsingStructure(int number, String id, String text) {
        this.number = number;
        this.id = id;
        this.text = text;
    }

    @Override
    public String toString() {
        return "ParsingStructure{" +
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
