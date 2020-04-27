package Genealogy.Model.Gedcom;

import Genealogy.MapViewer.Structures.MyCoordinate;
import Genealogy.Model.Act.Birth;
import Genealogy.Model.Act.Death;
import Genealogy.Model.Date.YearDate;
import Genealogy.Model.Exception.ParsingException;
import Genealogy.URLConnexion.MyHttpUrlConnection;
import Genealogy.URLConnexion.Serializer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class TownTest {

    /**
     * Town constructor test with name and county constructor, 4 regex with parenthesis, commas,
     * slash, name only and same name and county
     */
    @Test
    public void townConstructorTest() throws NoSuchFieldException, IllegalAccessException {
        //init reflection
        Town townTest = new Town("Ville 1", "Département");
        Field mapTownsField = townTest.getClass().getDeclaredField("towns");
        mapTownsField.setAccessible(true);
        mapTownsField.set(townTest, new ArrayList());

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
        assertEquals("Ville (Département)", Town.getLostTowns().get(0));
        assertEquals(1, Town.getTownAssociation().size());
        assertEquals("", Town.getTownAssociation().get("Ville (Département)"));
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
    public void addActTest() throws ParsingException, NoSuchFieldException, IllegalAccessException {
        //init
        Person person = new Person(null);
        Town townParis = new Town("Paris (Paris)");

        //reflection
        Field mapTownActField = townParis.getClass().getDeclaredField("mapTownAct");
        mapTownActField.setAccessible(true);
        mapTownActField.set(townParis, new HashMap<>());

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
    public void findCoordinateFromTownsTest() throws NoSuchFieldException, IllegalAccessException {
        //init reflection
        Town townTest = new Town("Ville 1", "Département");
        Field mapTownsField = townTest.getClass().getDeclaredField("towns");
        mapTownsField.setAccessible(true);
        mapTownsField.set(townTest, new ArrayList());
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
    public void findCoordinateFromTownsStaticTest() throws NoSuchFieldException, IllegalAccessException {
        //init reflection
        Town townTest = new Town("Ville 1", "Département");
        Field mapTownsField = townTest.getClass().getDeclaredField("towns");
        mapTownsField.setAccessible(true);
        mapTownsField.set(townTest, new ArrayList());
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

    /**
     * SetAllCoordinatesFromFile test : test with empty coordinates cities, already coordinates, and no coordinates,
     * alias with coordinates and alias without coordinates, alias not found, city with coordinates in cityFileMap
     *
     * @throws Exception
     */
    @Test
    public void setAllCoordinatesFromFileTest() throws Exception {
        //init input list
        ArrayList<Town> townCoordinatesList = new ArrayList();
        Town townMarseille = new Town("Marseille (Bouches du Rhône)");
        townMarseille.setCoordinates(new MyCoordinate(10, 10));
        Town townLyon = new Town("Lyon (Rhône)");
        townLyon.setCoordinates(new MyCoordinate(20, 20));
        Town townRennes = new Town("Rennes (Ille et Vilaine)");
        townRennes.setCoordinates(new MyCoordinate(30, 30));
        Town townLille = new Town("Lille (Nord)");
        townLille.setCoordinates(new MyCoordinate(40, 40));
        townCoordinatesList.add(townMarseille);
        townCoordinatesList.add(townLyon);
        townCoordinatesList.add(townRennes);
        //reflection towns, saveCoordinatesTxtFile, MyHttpUrlConnection, cityFileMap
        Field townField = townMarseille.getClass().getDeclaredField("towns");
        Field saveCoordinatesTxtFileField = townMarseille.getClass().getDeclaredField("saveCoordinatesTxtFile");
        townField.setAccessible(true);
        saveCoordinatesTxtFileField.setAccessible(true);
        townField.set(townMarseille, new ArrayList<>());
        saveCoordinatesTxtFileField.set(townMarseille, false);
        MyHttpUrlConnection connection = mock(MyHttpUrlConnection.class);
        Field instanceField = MyHttpUrlConnection.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, connection);
        Field cityFileMapField = Serializer.class.getDeclaredField("cityFileMap");
        cityFileMapField.setAccessible(true);
        HashMap<String, String> cityFileMap = new HashMap<>();
        cityFileMap.put("Gratot|Manche", "49:-1");
        cityFileMap.put("Orléans|Loiret", "25:-5");
        cityFileMapField.set(null, cityFileMap);
        String jsonResultReims = "[{\"place_id\":235317389,\"licence\":\"Data © OpenStreetMap contributors, ODbL 1.0. https://osm.org/copyright\",\"osm_type\":\"relation\",\"osm_id\":7379,\"boundingbox\":[\"48.5152693\",\"49.407418\",\"3.3958932\",\"5.0401048\"],\"lat\":\"48.961264\",\"lon\":\"4.31224359285714\",\"display_name\":\"Marne, Grand Est, France métropolitaine, France\",\"class\":\"boundary\",\"type\":\"administrative\",\"importance\":0.7216023420982088,\"icon\":\"https://nominatim.openstreetmap.org/images/mapicons/poi_boundary_administrative.p.20.png\"}]";
        when(connection.sendGpsRequest("Reims", "Marne")).thenReturn(jsonResultReims);
        String jsonResultNowhere = "[]";
        when(connection.sendGpsRequest("Nowhere", "Nowhere")).thenReturn(jsonResultNowhere);
        String jsonResultBrest = "[{\"place_id\":235233191,\"licence\":\"Data © OpenStreetMap contributors, ODbL 1.0. https://osm.org/copyright\",\"osm_type\":\"relation\",\"osm_id\":1076124,\"boundingbox\":[\"48.3572972\",\"48.4595521\",\"-4.5689169\",\"-4.4278311\"],\"lat\":\"48.3905283\",\"lon\":\"-4.4860088\",\"display_name\":\"Brest, Finistère, Bretagne, France métropolitaine, 29200, France\",\"class\":\"boundary\",\"type\":\"administrative\",\"importance\":0.8222453311672093,\"icon\":\"https://nominatim.openstreetmap.org/images/mapicons/poi_boundary_administrative.p.20.png\"},{\"place_id\":235504528,\"licence\":\"Data © OpenStreetMap contributors, ODbL 1.0. https://osm.org/copyright\",\"osm_type\":\"relation\",\"osm_id\":1650034,\"boundingbox\":[\"48.2959567\",\"48.6857905\",\"-5.1440329\",\"-4.0457299\"],\"lat\":\"48.487141050000005\",\"lon\":\"-4.485837994884294\",\"display_name\":\"Brest, Finistère, Bretagne, France métropolitaine, France\",\"class\":\"boundary\",\"type\":\"administrative\",\"importance\":0.5438072630301356,\"icon\":\"https://nominatim.openstreetmap.org/images/mapicons/poi_boundary_administrative.p.20.png\"}]";
        when(connection.sendGpsRequest("Brest", "Finistère")).thenReturn(jsonResultBrest);
        when(connection.sendGpsRequest("Nowhere2", "Nowhere")).thenReturn(jsonResultNowhere);

        //init verification
        assertEquals(0, Town.getTowns().size());

        //init towns without coordinates
        Town townNCMarseille = new Town("Marseille (Bouches du Rhône)");
        townMarseille.setCoordinates(new MyCoordinate(11, 11));
        Town townNCLyon = new Town("Lyon (Rhône)");
        Town townNCRennes = new Town("Rennes (Ille et Vilaine)");
        Town townNCReims = new Town("Reims (Marne)");
        Town townNCNowhere = new Town("Nowhere (Nowhere)");
        Town townNCAliasSearched = new Town("Alias1S (AliasDep)");
        Town townNCAliasToSearch = new Town("Alias2TS (AliasDep)");
        Town townNCAliasNotFound = new Town("Alias3NF (AliasDep)");
        Town townNCOrleansWithCoo = new Town("Orléans (Loiret)");
        Town.getTownAssociation().put("Alias1S (AliasDep)", "Marseille (Bouches du Rhône)");
        Town.getTownAssociation().put("Alias2TS (AliasDep)", "Brest (Finistère)");
        Town.getTownAssociation().put("Alias3NF (AliasDep)", "Nowhere2 (Nowhere)");
        //init failed search within MyHttpUrlConnection
        Town.getLostTowns().add("Nowhere (Nowhere)");
        Town.getLostTowns().add("Nowhere2 (Nowhere)");

        //init verification
        assertEquals(9, Town.getTowns().size());
        assertNull(townNCMarseille.getCoordinates());
        assertNull(townNCLyon.getCoordinates());
        assertNull(townNCRennes.getCoordinates());
        assertNull(townNCReims.getCoordinates());
        assertNull(townNCNowhere.getCoordinates());
        assertNull(townNCAliasSearched.getCoordinates());
        assertNull(townNCAliasToSearch.getCoordinates());
        assertNull(townNCAliasNotFound.getCoordinates());
        assertNull(townNCOrleansWithCoo.getCoordinates());

        //launch
        Town.setAllCoordinatesFromFile(townCoordinatesList);

        //verification
        Mockito.verify(connection, times(1)).sendGpsRequest("Reims", "Marne");
        Mockito.verify(connection, times(1)).sendGpsRequest("Nowhere", "Nowhere");
        Mockito.verify(connection, times(1)).sendGpsRequest("Brest", "Finistère");
        assertEquals("MyCoordinate{latitude=11.0, longitude=11.0}", townNCMarseille.getCoordinates().toString());
        assertEquals("MyCoordinate{latitude=20.0, longitude=20.0}", townNCLyon.getCoordinates().toString());
        assertEquals("MyCoordinate{latitude=30.0, longitude=30.0}", townNCRennes.getCoordinates().toString());
        assertEquals("MyCoordinate{latitude=48.961264, longitude=4.31224359285714}", townNCReims.getCoordinates().toString());
        assertNull(townNCNowhere.getCoordinates());
        assertEquals("MyCoordinate{latitude=11.0, longitude=11.0}", townNCAliasSearched.getCoordinates().toString());
        assertEquals("MyCoordinate{latitude=48.3905283, longitude=-4.4860088}", townNCAliasToSearch.getCoordinates().toString());
        assertEquals("MyCoordinate{latitude=25.0, longitude=-5.0}", townNCOrleansWithCoo.getCoordinates().toString());
        assertNull(townNCAliasNotFound.getCoordinates());
        //verification pointer
        assertEquals(townNCMarseille.getCoordinates(), Town.getTowns().get(0).getCoordinates());
        assertEquals(townNCLyon.getCoordinates(), Town.getTowns().get(1).getCoordinates());
        assertEquals(townNCRennes.getCoordinates(), Town.getTowns().get(2).getCoordinates());
        assertEquals(townNCReims.getCoordinates(), Town.getTowns().get(3).getCoordinates());
        assertNull(Town.getTowns().get(4).getCoordinates());
        assertEquals(townNCAliasSearched.getCoordinates(), Town.getTowns().get(5).getCoordinates());
        assertEquals(townNCAliasToSearch.getCoordinates(), Town.getTowns().get(6).getCoordinates());
        assertNull(Town.getTowns().get(7).getCoordinates());
        assertEquals(townNCOrleansWithCoo.getCoordinates(), Town.getTowns().get(8).getCoordinates());
        assertEquals(7, Town.getTownsToSerialize().size());
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