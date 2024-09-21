package Genealogy.Model.Gedcom;

import java.util.Comparator;

/**
 * Town Comparator class : comparator between towns using names
 */
public class TownComparator implements Comparator<Town> {

    /**
     * Function compare : compare towns names
     *
     * @param o1
     * @param o2
     * @return
     */
    @Override
    public int compare(Town o1, Town o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
