package Genealogy.URLConnexion;

import Genealogy.MapViewer.Structures.MyCoordinate;
import Genealogy.Model.Act.Proof;
import Genealogy.Model.Gedcom.Town;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SerializerTest test : Serializer test class
 */
public class SerializerTest {
    /**
     * Test initSerializedTownsTest : initialize not found serialized town file and with 3 towns with coordinates
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    //@Test
    public void initSerializedTownsTest() throws NoSuchFieldException, IllegalAccessException {
        //init
        Serializer serializer = Serializer.getInstance();

        //init reflection fileNotFound
        Field townSerializerFileNameField = serializer.getClass().getDeclaredField("townSerializerFileName");
        Field pathField = serializer.getClass().getDeclaredField("path");
        Field serializedTownsField = serializer.getClass().getDeclaredField("serializedTowns");
        townSerializerFileNameField.setAccessible(true);
        pathField.setAccessible(true);
        serializedTownsField.setAccessible(true);
        townSerializerFileNameField.set(serializer, "initSerializedTowns.serTestFailure");
        pathField.set(serializer, "src/test/resources/");
        serializedTownsField.set(serializer, new ArrayList<>());

        //verification with no serializer file
        assertTrue(serializer.getSerializedTowns().isEmpty());

        //launch with no serializer file
        serializer.initSerializedTowns();

        //verification with no serializer file
        assertTrue(serializer.getSerializedTowns().isEmpty());

        //init reflection with 3 towns
        townSerializerFileNameField.set(serializer, "initSerializedTowns.serTest");

        //launch with 3 towns
        serializer.initSerializedTowns();

        //verification with 3 towns
        assertEquals(3, serializer.getSerializedTowns().size());
        assertEquals("Lille Nord", serializer.getSerializedTowns().get(0).getFullName());
        assertEquals("(50.0,52.0)", serializer.getSerializedTowns().get(0).getCoordinatesPrettyPrint());
        assertEquals("Paris Paris", serializer.getSerializedTowns().get(1).getFullName());
        assertEquals("(20.0,22.0)", serializer.getSerializedTowns().get(1).getCoordinatesPrettyPrint());
        assertEquals("Troyes Aube", serializer.getSerializedTowns().get(2).getFullName());
        assertEquals("(30.0,32.0)", serializer.getSerializedTowns().get(2).getCoordinatesPrettyPrint());
    }

    /**
     * Test initProperties : test filename init and regex list with 4 and 1 regex list
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void initPropertiesTest() throws NoSuchFieldException, IllegalAccessException {
        //init
        Serializer serializer = Serializer.getInstance();
        Field pathField = serializer.getClass().getDeclaredField("path");
        Field serializerPropertiesFileNameField = serializer.getClass().getDeclaredField("serializerPropertiesFileName");
        pathField.setAccessible(true);
        serializerPropertiesFileNameField.setAccessible(true);
        pathField.set(serializer, "src/test/resources/");
        serializerPropertiesFileNameField.set(serializer, "initProperties4Regex.propertiesTest");

        //launch 4 regex
        serializer.initProperties();

        //verification
        assertEquals("cities.ser", serializer.getTownSerializerFileName());
        assertEquals("City names.txt", serializer.getTownAssociationFileName());
        assertEquals("cities.txt", serializer.getCityCoordinatesFileName());
        assertEquals("Preuves.txt", serializer.getProofFileName());
        assertEquals(4, Serializer.getTownRegex().size());
        assertEquals("(.*)\\((.*)\\)", Serializer.getTownRegex().get(0));
        assertEquals("(.*),(.*)", Serializer.getTownRegex().get(1));
        assertEquals("(.*)\\/(.*)", Serializer.getTownRegex().get(2));
        assertEquals("(.+)", Serializer.getTownRegex().get(3));

        //init
        serializerPropertiesFileNameField.set(serializer, "initProperties1Regex.propertiesTest");

        //launch 1 regex
        serializer.initProperties();

        //verification
        assertEquals("cities.ser", serializer.getTownSerializerFileName());
        assertEquals("City names.txt", serializer.getTownAssociationFileName());
        assertEquals("cities.txt", serializer.getCityCoordinatesFileName());
        assertEquals("Preuves.txt", serializer.getProofFileName());
        assertEquals(1, Serializer.getTownRegex().size());
        assertEquals("a(.*)\\((.*)\\)", Serializer.getTownRegex().get(0));
    }

    /**
     * Test initTownAssociation : check if insert townAssociationFileName contents properly into
     * Serializer TownAssociationMap
     *
     * @throws IOException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void initTownAssociationTest() throws NoSuchFieldException, IllegalAccessException {
        //init
        Serializer serializer = Serializer.getInstance();
        Field pathField = serializer.getClass().getDeclaredField("path");
        Field townAssociationFileNameField = serializer.getClass().getDeclaredField("townAssociationFileName");
        pathField.setAccessible(true);
        townAssociationFileNameField.setAccessible(true);
        pathField.set(serializer, "src/test/resources/");
        townAssociationFileNameField.set(serializer, "initTownAssociation.txtTest");

        //launch
        serializer.initTownAssociation();

        //verification
        HashMap<String, String> townCoordinatesMap = Serializer.getTownAssociationMap();
        assertEquals(3, townCoordinatesMap.size());
        assertEquals("Châtillon Coligny (Loiret)", townCoordinatesMap.get("Châtillon sur Loing (Loiret)"));
        assertEquals("Castilly (Calvados)", townCoordinatesMap.get("Mestry (Calvados)"));
        assertEquals("Brienon sur Armançon (Yonne)", townCoordinatesMap.get("Brienon l'archevêque (Yonne)"));
        assertNull(townCoordinatesMap.get("Saint Sauveur de Bonfossé (Manche)"));
    }

    /**
     * Test initProofList : check if insert proofFileName contents properly into
     * Serializer ProofList
     *
     * @throws IOException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void initProofListTest() throws NoSuchFieldException, IllegalAccessException {
        //init
        Serializer serializer = Serializer.getInstance();
        Field pathField = serializer.getClass().getDeclaredField("path");
        Field proofFileFileNameField = serializer.getClass().getDeclaredField("proofFileName");
        pathField.setAccessible(true);
        proofFileFileNameField.setAccessible(true);
        pathField.set(serializer, "src/test/resources/");
        proofFileFileNameField.set(serializer, "initProofList.txtTest");

        //launch
        serializer.initProofList();

        //verification
        List<Proof> proofList = Serializer.getProofList();
        assertEquals(5, proofList.size());
        assertEquals("Proof{date='19/04/1888', town='Grenoble (Isère)', people='Henry Michel Legendre', typeAct='Décès'}", proofList.get(0).toString());
        assertEquals("Proof{date='06/08/1605', town='L'Haÿ Les Roses (Val de Marne)', people='Eudes Morsou', typeAct='Naissance'}", proofList.get(1).toString());
        assertEquals("Proof{date='19/11/1900', town='Issoire (Puy-de-Dome)', people='Jean Garilla', typeAct='Décès'}", proofList.get(2).toString());
        assertEquals("Proof{date='15/04/1906', town='Issoire (Puy-de-Dome)', people='Michel Siroco Marie Louab', typeAct='Mariage'}", proofList.get(3).toString());
        assertEquals("Proof{date='25/03/1985', town='Clermont-Ferrand (Puy-de-Dome)', people='Ornella Mizou', typeAct='Décès'}", proofList.get(4).toString());
    }

    /**
     * Test saveTownAssociation : check if saveTownAssociation writes saveTownAssociation HashMap
     * in townAssociationFileName properly
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws IOException
     */
    @Test
    public void saveTownAssociationTest() throws NoSuchFieldException, IllegalAccessException, IOException {
        //init
        String fileInput = "src/test/resources/saveTownAssociation.txtTest";
        String fileResult = "src/test/resources/saveTownAssociationResult.txtTest";
        Serializer serializer = Serializer.getInstance();
        HashMap<String, String> townAssociation = new HashMap<>();
        townAssociation.put("Châtillon sur Loing (Loiret)", "Châtillon Coligny (Loiret)");
        townAssociation.put("Brienon l'archevêque (Yonne)", "Brienon sur Armançon (Yonne)");
        townAssociation.put("Mestry (Calvados)", "Castilly (Calvados)");

        //init reflection
        Field pathField = serializer.getClass().getDeclaredField("path");
        Field townAssociationFileNameField = serializer.getClass().getDeclaredField("townAssociationFileName");
        pathField.setAccessible(true);
        townAssociationFileNameField.setAccessible(true);
        pathField.set(serializer, "src/test/resources/");
        townAssociationFileNameField.set(serializer, "saveTownAssociation.txtTest");

        //launch
        serializer.saveTownAssociation(townAssociation);

        //verification md5sum
        try {
            String fileInputContents = readFile(fileInput);
            String fileResultContents = readFile(fileResult);
            assertEquals(fileResultContents, fileInputContents);
        } finally {
            File file = new File(fileInput + StringUtils.EMPTY);
            System.out.println(fileInput + " " + file.exists());
            file.delete();
        }
    }

    /**
     * Function readFile : read file from String path and return it
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static String readFile(String path) throws IOException {
        String result = StringUtils.EMPTY;
        File f = new File(path);
        if (f.exists()) {
            String sCurrentLine;
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
            while ((sCurrentLine = br.readLine()) != null) {
                result += sCurrentLine + System.lineSeparator();
            }
        }
        return result;
    }

    /**
     * Test initTownCoordinatesMap : check if townCoordinatesMap is loaded properly from file
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void initTownCoordinatesMapTest() throws NoSuchFieldException, IllegalAccessException {
        //init
        Serializer serializer = Serializer.getInstance();
        Field pathField = serializer.getClass().getDeclaredField("path");
        Field cityCoordinatesFileNameField = serializer.getClass().getDeclaredField("cityCoordinatesFileName");
        pathField.setAccessible(true);
        cityCoordinatesFileNameField.setAccessible(true);
        pathField.set(serializer, "src/test/resources/");
        cityCoordinatesFileNameField.set(serializer, "initTownCoordinatesMap.txtTest");

        //launch
        serializer.initTownCoordinatesMap();

        //verification
        HashMap<String, String> townCoordinatesMap = serializer.getTownCoordinatesMap();
        assertEquals(3, townCoordinatesMap.size());
        assertEquals("47.9315391:3.5257298", townCoordinatesMap.get("Bonnard|Yonne"));
        assertEquals("49.0987788:-1.242418", townCoordinatesMap.get("Marigny|Manche"));
        assertEquals("49.4610057:2.8162485", townCoordinatesMap.get("Coudun|Oise"));
        assertNull(townCoordinatesMap.get("Reims|Marne"));
    }

    /**
     * Test addTownToCoordinateMap : test add Town and coordinates to serializer hashmap
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void addTownToCoordinateMapTest() throws NoSuchFieldException, IllegalAccessException {
        //init
        Serializer serializer = Serializer.getInstance();
        Field townCoordinatesMapField = serializer.getClass().getDeclaredField("townCoordinatesMap");
        townCoordinatesMapField.setAccessible(true);
        townCoordinatesMapField.set(serializer, new HashMap<>());

        //verification
        assertTrue(serializer.getTownCoordinatesMap().isEmpty());

        //launch
        serializer.addTownToCoordinateMap("Rennes", "30", "50");

        //verification
        assertEquals(1, serializer.getTownCoordinatesMap().size());
        assertEquals("30:50", serializer.getTownCoordinatesMap().get("Rennes"));

        //launch
        serializer.addTownToCoordinateMap("Reims", "10", "20");

        //verification
        assertEquals(2, serializer.getTownCoordinatesMap().size());
        assertEquals("10:20", serializer.getTownCoordinatesMap().get("Reims"));
        assertEquals("30:50", serializer.getTownCoordinatesMap().get("Rennes"));
    }

    /**
     * Test printTownCoordinatesMap : test pretty print with empty map and map with data
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void printTownCoordinatesMapTest() throws NoSuchFieldException, IllegalAccessException {
        //init reflection and empty map
        Serializer serializer = Serializer.getInstance();
        HashMap<String, String> townCoordinatesMap = new HashMap<>();
        Field townCoordinatesMapField = serializer.getClass().getDeclaredField("townCoordinatesMap");
        townCoordinatesMapField.setAccessible(true);
        townCoordinatesMapField.set(serializer, townCoordinatesMap);

        //verification empty map
        assertEquals(StringUtils.EMPTY, serializer.printTownCoordinatesMap());

        //init data to map
        townCoordinatesMap.put("Bonnard|Yonne", "47.9315391:3.5257298");
        townCoordinatesMap.put("Ouzouer des Champs|Loiret", "47.8810784:2.7054265");
        townCoordinatesMap.put("Coudun|Oise", "49.4610057:2.8162485");

        //verification map with data
        assertEquals("Bonnard|Yonne->47.9315391:3.5257298" + System.lineSeparator() +
                "Coudun|Oise->49.4610057:2.8162485" + System.lineSeparator() +
                "Ouzouer des Champs|Loiret->47.8810784:2.7054265" + System.lineSeparator(), serializer.printTownCoordinatesMap());
    }

    /**
     * Test writeCoordinatesMapTest : test write cityCoordinatesFileName with md5
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws IOException
     */
    @Test
    public void writeCoordinatesMapTest() throws NoSuchFieldException, IllegalAccessException, IOException {
        //init reflection and map
        Serializer serializer = Serializer.getInstance();
        HashMap<String, String> townCoordinatesMap = new HashMap<>();
        townCoordinatesMap.put("Bonnard|Yonne", "47.9315391:3.5257298");
        townCoordinatesMap.put("Marignys|Manche", "55.0987788:-1.242418");
        townCoordinatesMap.put("Couduns|Oise", "50.4610057:2.8162485");
        Field townCoordinatesMapField = serializer.getClass().getDeclaredField("townCoordinatesMap");
        Field pathField = serializer.getClass().getDeclaredField("path");
        Field cityCoordinatesFileNameField = serializer.getClass().getDeclaredField("cityCoordinatesFileName");
        townCoordinatesMapField.setAccessible(true);
        pathField.setAccessible(true);
        cityCoordinatesFileNameField.setAccessible(true);
        townCoordinatesMapField.set(serializer, townCoordinatesMap);
        pathField.set(serializer, "src/test/resources/");
        cityCoordinatesFileNameField.set(serializer, "writeCoordinatesMap.txtTest");

        //launch
        serializer.writeCoordinatesMap();

        //verification md5sum
        String fileInput = "src/test/resources/writeCoordinatesMap.txtTest";
        String fileResult = "src/test/resources/writeCoordinatesMapResult.txtTest";
        try {
            String fileInputContents = readFile(fileInput);
            String fileResultContents = readFile(fileResult);
            assertEquals(fileInputContents, fileResultContents);
        } finally {
            File file = new File(fileInput);
            file.delete();
        }
    }

    /**
     * Test readCoordinatesMapTest : test if readCoordinatesMap returns the coordinates of
     * the city if found, null otherwise, needs initTownCoordinatesMap
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void readCoordinatesMapTest() throws NoSuchFieldException, IllegalAccessException {
        //init
        Serializer serializer = Serializer.getInstance();
        Field pathField = serializer.getClass().getDeclaredField("path");
        Field cityCoordinatesFileNameField = serializer.getClass().getDeclaredField("cityCoordinatesFileName");
        pathField.setAccessible(true);
        cityCoordinatesFileNameField.setAccessible(true);
        pathField.set(serializer, "src/test/resources/");
        cityCoordinatesFileNameField.set(serializer, "readCoordinatesMap.txtTest");
        serializer.initTownCoordinatesMap();

        //launch && verification
        assertNull(serializer.readCoordinatesMap("Paris", "Paris"));
        assertEquals("47.9315391:3.5257298", serializer.readCoordinatesMap("Bonnard", "Yonne"));
        assertEquals("49.0987788:-1.242418", serializer.readCoordinatesMap("Marigny", "Manche"));
        assertEquals("49.4610057:2.8162485", serializer.readCoordinatesMap("Coudun", "Oise"));
    }

    /**
     * Function saveSerializedTownTest : test saveSerializedTown serialization file
     * with md5sum with 3 cities with coordinates
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws IOException
     */
    //@Test
    public void saveSerializedTownTest() throws NoSuchFieldException, IllegalAccessException, IOException {
        //init
        ArrayList<Town> towns = new ArrayList<>();
        Town townLille = new Town("Lille", "Nord");
        townLille.setCoordinates(new MyCoordinate(50, 52));
        towns.add(townLille);
        Town townParis = new Town("Paris", "Paris");
        townParis.setCoordinates(new MyCoordinate(20, 22));
        towns.add(townParis);
        Town townTroyes = new Town("Troyes", "Aube");
        townTroyes.setCoordinates(new MyCoordinate(30, 32));
        towns.add(townTroyes);
        //init reflection
        Serializer serializer = Serializer.getInstance();
        Field townsToSerializeField = serializer.getClass().getDeclaredField("townsToSerialize");
        Field townSerializerFileNameField = serializer.getClass().getDeclaredField("townSerializerFileName");
        Field pathField = serializer.getClass().getDeclaredField("path");
        townsToSerializeField.setAccessible(true);
        townSerializerFileNameField.setAccessible(true);
        pathField.setAccessible(true);
        townsToSerializeField.set(serializer, towns);
        townSerializerFileNameField.set(serializer, "saveSerializedTownTest.serTest");
        pathField.set(serializer, "src/test/resources/");

        //launch
        serializer.saveSerializedTownList();

        //verification md5sum
        String fileInput = "src/test/resources/saveSerializedTownTest.serTest";
        String fileResult = "src/test/resources/saveSerializedTownTestResult.serTest";
        try {
            String fileInputContents = readFile(fileInput);
            String fileResultContents = readFile(fileResult);
            assertEquals(fileInputContents, fileResultContents);
        } finally {
            File file = new File(fileInput);
            file.delete();
        }
    }
}