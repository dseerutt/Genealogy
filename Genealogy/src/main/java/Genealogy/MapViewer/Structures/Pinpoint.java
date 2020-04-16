package Genealogy.MapViewer.Structures;

import Genealogy.Model.Gedcom.Town;

import java.io.Serializable;

/**
 * Pinpoint class : pinpoint the name of a person, his age and the town
 */
public class Pinpoint implements Serializable {
    /**
     * Town town object
     */
    private Town town;
    /**
     * String displayed name
     */
    private String name;
    /**
     * Int age of the person
     */
    private int age;
    /**
     * Minimum year of PinPoint
     */
    public static int minimumYear = 10000;

    /**
     * Pinpoint constructor
     *
     * @param town
     * @param name
     * @param age
     */
    public Pinpoint(Town town, String name, int age) {
        this.town = town;
        this.name = name;
        this.age = age;
    }

    /**
     * Town getter
     *
     * @return
     */
    public Town getTown() {
        return town;
    }

    /**
     * Name getter
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Age getter
     *
     * @return
     */
    public int getAge() {
        return age;
    }

    /**
     * Function toString : Classic object printing
     *
     * @return
     */
    @Override
    public String toString() {
        return "MapStructure{" +
                "age=" + age +
                ", name='" + name + '\'' +
                ", town=" + town +
                '}';
    }
}
