package Genealogy.Model.Gedcom;

import Genealogy.MapViewer.Structures.MyCoordinate;
import Genealogy.Model.Act.Birth;
import Genealogy.Model.Act.Death;
import Genealogy.Model.Date.YearDate;
import Genealogy.Model.Exception.ParsingException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TownTest {

    /**
     * Town constructor test with name and county constructor, 4 regex with parenthesis, commas,
     * slash, name only and same name and county
     */
    @Test
    public void townConstructorTest() {
        //launch
        Town townFromNameAndCounty = new Town("Ville 1", "Département");
        Town townFromParenthesis = new Town("Mon village 1 (mon département 1)");
        Town townFromCommas = new Town("Mon village 2,mon département 2");
        Town townFromSlash = new Town("Mon village 3/mon département 3");
        Town townFromOnlyName = new Town("Mon village 4");
        Town townFromOnlyName2 = new Town("Mon village 4");

        //verification
        assertEquals("Town{name='Ville 1', county='Département'}", townFromNameAndCounty.toString());
        assertEquals("Town{name='Mon village 1', county='mon département 1'}", townFromParenthesis.toString());
        assertEquals("Town{name='Mon village 2', county='mon département 2'}", townFromCommas.toString());
        assertEquals("Town{name='Mon village 3', county='mon département 3'}", townFromSlash.toString());
        assertEquals("Town{name='Mon village 4', county=''}", townFromOnlyName.toString());
        assertEquals("Town{name='Mon village 4', county=''}", townFromOnlyName2.toString());
        assertEquals(5, Town.getTowns().size());
        assertEquals(townFromNameAndCounty, Town.getTowns().get(0));
        assertEquals(townFromParenthesis, Town.getTowns().get(1));
        assertEquals(townFromCommas, Town.getTowns().get(2));
        assertEquals(townFromSlash, Town.getTowns().get(3));
        assertEquals(townFromOnlyName, Town.getTowns().get(4));
    }

    /**
     * Test addLostTowns : add to lost towns and town association with empty hashmap value
     */
    @Test
    public void addLostTownsTest() {
        //launch
        Town.addLostTowns("Ville", "Département");

        //verification
        assertEquals(1, Town.getLostTowns().size());
        assertEquals("Ville", Town.getLostTowns().get(0));
        assertEquals(1, Town.getTownAssociation().size());
        assertEquals("", Town.getTownAssociation().get("Ville Département"));
    }

    /**
     * Test getFullName : test name and county town, name only and county only town
     */
    @Test
    public void getFullNameTest() {
        //launch
        Town townClassic = new Town("Ville 1", "Département");
        Town townEmptyName = new Town("", "Département");
        Town townEmptyCounty = new Town("Ville 1", "");

        //verification
        assertEquals("Ville 1 Département", townClassic.getFullName());
        assertEquals("Département", townEmptyName.getFullName());
        assertEquals("Ville 1", townEmptyCounty.getFullName());
    }

    /**
     * Test getFullNameWithParenthesis : test name and county town, name only and county only town
     */
    @Test
    public void getFullNameWithParenthesisTest() {
        //launch
        Town townClassic = new Town("Ville 1", "Département");
        Town townEmptyName = new Town("", "Département");
        Town townEmptyCounty = new Town("Ville 1", "");

        //verification
        assertEquals("Ville 1 (Département)", townClassic.getFullNameWithParenthesis());
        assertEquals("(Département)", townEmptyName.getFullNameWithParenthesis());
        assertEquals("Ville 1", townEmptyCounty.getFullNameWithParenthesis());
    }

    /**
     * Test parseJsonArray : test on Nominatim Json Rennes, Tataouine and no result
     */
    @Test
    public void parseJsonArrayTest() {
        //init
        String jsonRennes = "[{\"place_id\":235358007,\"licence\":\"Data © OpenStreetMap contributors, ODbL 1.0. https://osm.org/copyright\",\"osm_type\":\"relation\",\"osm_id\":54517,\"boundingbox\":[\"48.0769155\",\"48.1549705\",\"-1.7525876\",\"-1.6244045\"],\"lat\":\"48.1113387\",\"lon\":\"-1.6800198\",\"display_name\":\"Rennes, Ille-et-Vilaine, Bretagne, France métropolitaine, France\",\"class\":\"boundary\",\"type\":\"administrative\",\"importance\":1.0351117378386865,\"icon\":\"https://nominatim.openstreetmap.org/images/mapicons/poi_boundary_administrative.p.20.png\"},{\"place_id\":235633690,\"licence\":\"Data © OpenStreetMap contributors, ODbL 1.0. https://osm.org/copyright\",\"osm_type\":\"relation\",\"osm_id\":1655027,\"boundingbox\":[\"47.9340931\",\"48.379636\",\"-2.288984\",\"-1.283944\"],\"lat\":\"48.156836049999995\",\"lon\":\"-1.8144255084289234\",\"display_name\":\"Rennes, Ille-et-Vilaine, Bretagne, France métropolitaine, France\",\"class\":\"boundary\",\"type\":\"administrative\",\"importance\":0.8977518265986077,\"icon\":\"https://nominatim.openstreetmap.org/images/mapicons/poi_boundary_administrative.p.20.png\"}]";
        String jsonTataouine = "[{\"place_id\":772371,\"licence\":\"Data © OpenStreetMap contributors, ODbL 1.0. https://osm.org/copyright\",\"osm_type\":\"node\",\"osm_id\":264885332,\"boundingbox\":[\"32.8915727\",\"32.9715727\",\"10.410413\",\"10.490413\"],\"lat\":\"32.9315727\",\"lon\":\"10.450413\",\"display_name\":\"Tataouine, Tataouine Ville, Tataouine Sud, Tataouine, 3200, Tunisie\",\"class\":\"place\",\"type\":\"town\",\"importance\":0.5499040266462482,\"icon\":\"https://nominatim.openstreetmap.org/images/mapicons/poi_place_town.p.20.png\"}]";
        String jsonEmpty = "[]";

        //launch
        MyCoordinate myCoordinateRennes = Town.parseJsonArray(jsonRennes);
        MyCoordinate myCoordinateTataouine = Town.parseJsonArray(jsonTataouine);
        MyCoordinate myCoordinateEmpty = Town.parseJsonArray(jsonEmpty);

        //verification
        assertEquals("MyCoordinate{latitude=48.1113387, longitude=-1.6800198}", myCoordinateRennes.toString());
        assertEquals("MyCoordinate{latitude=32.9315727, longitude=10.450413}", myCoordinateTataouine.toString());
        assertNull(myCoordinateEmpty);
    }

    /**
     * Test addActTest : test addAct with new town, and town that already exists
     *
     * @throws ParsingException
     */
    @Test
    public void addActTest() throws ParsingException {
        //init
        Person person = new Person(null);
        Town townParis = new Town("Paris (Paris)");

        //launch : the function is called in Act constructor
        Birth birth = new Birth(person, new YearDate("2005"), townParis);

        //verification
        assertEquals(1, Town.getMapTownAct().get(townParis).size());
        assertEquals(birth, Town.getMapTownAct().get(townParis).get(0));

        //launch
        Death death = new Death(person, new YearDate("2005"), townParis);

        //verification
        assertEquals(2, Town.getMapTownAct().get(townParis).size());
        assertEquals(death, Town.getMapTownAct().get(townParis).get(1));

        //launch
        Town townSaintes = new Town("Saintes (Charente-Maritime)");
        Death death2 = new Death(person, new YearDate("2005"), townSaintes);

        //verification
        assertEquals(1, Town.getMapTownAct().get(townSaintes).size());
        assertEquals(death2, Town.getMapTownAct().get(townSaintes).get(0));
    }

    /**
     * FindTown test : check if a town is inside an arraylist parameter, check if it is, check if not
     */
    @Test
    public void findTownTest() {
        //init
        Town townParis = new Town("Paris (Paris@)");
        Town townParis5 = new Town("Paris 5e (Paris 5)");
        ArrayList towns = new ArrayList();
        towns.add(townParis);

        //launch
        Town townParisResult = Town.findTown(towns, townParis);
        Town townParis5Result = Town.findTown(towns, townParis5);

        //verification
        assertEquals(townParisResult, townParis);
        assertNull(townParis5Result);
    }

    /**
     * FindCoordinateFromTowns test : from the current city check if returns the coordinates set
     * , or the one in Town list.
     * Test if null is returned if the coordinates are not found
     */
    @Test
    public void findCoordinateFromTownsTest() {
        //init
        Town townParis = new Town("Paris (Paris)");
        townParis.setCoordinates(new MyCoordinate(10, 10));
        Town townParisWithoutCoordinates = new Town("Paris (Paris)");
        Town otherTown = new Town("Reims (Marne)");
        Town townNice = new Town("Nice (Alpes-Maritimes)");
        townNice.setCoordinates(new MyCoordinate(11, 11));

        //verification
        assertEquals("MyCoordinate{latitude=10.0, longitude=10.0}", townParis.findCoordinateFromTowns().toString());
        assertEquals("MyCoordinate{latitude=10.0, longitude=10.0}", townParisWithoutCoordinates.findCoordinateFromTowns().toString());
        assertEquals("MyCoordinate{latitude=11.0, longitude=11.0}", townNice.findCoordinateFromTowns().toString());
        assertNull(otherTown.findCoordinateFromTowns());
    }

    /**
     * FindCoordinateFromTowns test : from the fullname string check if returns the coordinates set
     * , or the one in Town list.
     * Test if null is returned if the coordinates are not found
     */
    @Test
    public void testFindCoordinateFromTownsTest() {
        //init
        Town townParis = new Town("Paris (Paris)");
        townParis.setCoordinates(new MyCoordinate(10, 10));
        Town townParisWithoutCoordinates = new Town("Paris (Paris)");
        Town otherTown = new Town("Reims (Marne)");
        Town townNice = new Town("Nice (Alpes-Maritimes)");
        townNice.setCoordinates(new MyCoordinate(11, 11));

        //verification
        assertEquals("MyCoordinate{latitude=10.0, longitude=10.0}", Town.findCoordinateFromTowns("Paris (Paris)").toString());
        assertEquals("MyCoordinate{latitude=11.0, longitude=11.0}", Town.findCoordinateFromTowns("Nice (Alpes-Maritimes)").toString());
        assertNull(Town.findCoordinateFromTowns("Nice (Alpes-Maritimes"));
    }

    @Test
    public void setAllCoordinatesFromFileTest() {
    }

    //@Test
    public void setAllCoordinatesFromSerializerTest() {
    }

    //@Test
    public void sortTownsTest() {
    }

    //@Test
    public void testSetCoordinatesTest() {
    }

    //@Test
    public void getCoordinatesPrettyPrintTest() {
    }

    //@Test
    public void isEmptyTest() {
    }

    //@Test
    public void testToStringTest() {
    }

    //@Test
    public void toStringPrettyStringTest() {
    }
}