package Genealogy.Model;

/**
 * Created by Dan on 19/04/2016.
 */
public class MapStructure {
    private Town town;
    private String name;
    private int age;

    public MapStructure(Town town, String name, int age) {
        this.town = town;
        this.name = name;
        this.age = age;
    }

    public Town getTown() {
        return town;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "MapStructure{" +
                "age=" + age +
                ", name='" + name + '\'' +
                ", town=" + town +
                '}';
    }
}
