package Genealogy.MapViewer.Structures;

import Genealogy.Model.Date.MyDate;
import Genealogy.Model.Gedcom.Person;
import Genealogy.Model.Gedcom.Town;
import javafx.util.Pair;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

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
     * Map of list of Pinpoints (name, age, town) per year
     */
    private static HashMap<Integer, ArrayList<Pinpoint>> pinpointsYearMap = new HashMap<>();
    /**
     * Map of list of MapStructures (name, age, town) per year only for direct ancestors
     */
    private static HashMap<Integer, ArrayList<Pinpoint>> pinpointsYearMapDirectAncestors = new HashMap<>();

    /**
     * Town getter
     *
     * @return
     */
    public Town getTown() {
        return town;
    }

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
     * PinpointsYearMap getter
     *
     * @return
     */
    public static HashMap<Integer, ArrayList<Pinpoint>> getPinpointsYearMap() {
        return pinpointsYearMap;
    }

    /**
     * PinpointsYearMapDirectAncestors getter
     *
     * @return
     */
    public static HashMap<Integer, ArrayList<Pinpoint>> getPinpointsYearMapDirectAncestors() {
        return pinpointsYearMapDirectAncestors;
    }

    /**
     * Function addPinpoint : add the couple (year,pinpoint) to pinpointsYearMap
     * If the person is a direct ancestor, add it to pinpointsYearMapDirectAncestors
     *
     * @param year
     * @param pinpoint
     * @param directAncestor
     */
    public static void addPinpoint(int year, Pinpoint pinpoint, boolean directAncestor) {
        if (year < minimumYear) {
            minimumYear = year;
        }
        if (pinpointsYearMap.containsKey(year)) {
            pinpointsYearMap.get(year).add(pinpoint);
        } else {
            ArrayList<Pinpoint> structure = new ArrayList<>();
            structure.add(pinpoint);
            pinpointsYearMap.put(year, structure);
        }
        if (directAncestor) {
            if (pinpointsYearMapDirectAncestors.containsKey(year)) {
                pinpointsYearMapDirectAncestors.get(year).add(pinpoint);
            } else {
                ArrayList<Pinpoint> pinpointList = new ArrayList<>();
                pinpointList.add(pinpoint);
                pinpointsYearMapDirectAncestors.put(year, pinpointList);
            }
        }
    }

    /**
     * Function convertToLocalDateFromDate : convert date to LocalDate
     *
     * @param dateToConvert
     * @return
     */
    public static LocalDate convertToLocalDateFromDate(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    /**
     * Function initPinpoints : from a list of pair MyDate and Town, initialize the pinpoint collecting the year periods
     * From 2 pinpoints for the current person, add a pinpoint for every year in this period
     *
     * @param lifespanPairsList
     * @param person
     */
    public static void initPinpoints(ArrayList<Pair<MyDate, Town>> lifespanPairsList, Person person) {
        Boolean directAncestor = person.isDirectAncestor();
        String fullName = person.getFullName();
        if (lifespanPairsList.size() >= 1) {
            if (lifespanPairsList.size() == 1) {
                Date date = new Date(lifespanPairsList.get(0).getKey().getDate().getTime());
                Pinpoint pinPoint =
                        new Pinpoint(lifespanPairsList.get(0).getValue(), fullName, person.getAgeWithoutMonths(convertToLocalDateFromDate(date), 0));
                addPinpoint((int) lifespanPairsList.get(0).getKey().getYear(), pinPoint, directAncestor);
            } else {
                for (int i = 0; i < lifespanPairsList.size() - 1; i++) {
                    int startYear = (int) lifespanPairsList.get(i).getKey().getYear();
                    int endYear = (int) lifespanPairsList.get(i + 1).getKey().getYear();
                    int index = 0;
                    for (int k = startYear; k < endYear; k++) {
                        Date date = new Date(lifespanPairsList.get(i).getKey().getDate().getTime());
                        Pinpoint pinPoint =
                                new Pinpoint(lifespanPairsList.get(i).getValue(), fullName, person.getAgeWithoutMonths(convertToLocalDateFromDate(date), index));
                        addPinpoint(k, pinPoint, directAncestor);
                        index++;
                    }
                }
                Date lastDate = new Date(lifespanPairsList.get(lifespanPairsList.size() - 1).getKey().getDate().getTime());
                Pinpoint pinPoint =
                        new Pinpoint(lifespanPairsList.get(lifespanPairsList.size() - 1).getValue(), fullName, person.getAgeWithMonths(convertToLocalDateFromDate(lastDate), 0));

                addPinpoint((int) lifespanPairsList.get(lifespanPairsList.size() - 1).getKey().getYear(), pinPoint, directAncestor);
            }
        }
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

    /**
     * Function equals : to compare pinpoints with age, town and name
     *
     * @param object
     * @return
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Pinpoint)) return false;
        Pinpoint pinpoint = (Pinpoint) object;
        return getAge() == pinpoint.getAge() &&
                Objects.equals(getTown(), pinpoint.getTown()) &&
                Objects.equals(getName(), pinpoint.getName());
    }
}
