package Genealogy.Model.Gedcom;

import java.util.Comparator;

/**
 * PersonNameComparator class : compare persons with getComparatorName function
 */
public class PersonNameComparator implements Comparator<Person> {
    /**
     * Function compare : use getComparatorName to compare Persons
     *
     * @param person1
     * @param person2
     * @return
     */
    @Override
    public int compare(Person person1, Person person2) {
        return person1.getComparatorName().compareTo(person2.getComparatorName());
    }
}
