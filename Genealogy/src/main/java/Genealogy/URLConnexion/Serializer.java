package Genealogy.URLConnexion;

import Genealogy.Model.Gedcom.Town;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;

/**
 * Serializer class : handle Town serialization and Town save
 */
public class Serializer {

    /**
     * Class logger
     */
    public final static Logger logger = LogManager.getLogger(Serializer.class);
    /**
     * Instance of the serializer
     */
    private static Serializer instance;
    /**
     * Serialized Town list
     */
    private ArrayList<Town> serializedTowns;
    /**
     * Serializer file string path
     */
    private static String path;
    /**
     * Boolean jar : is launched from jar
     */
    private static boolean jar = false;
    /**
     * String name of townSerializer file
     */
    private static String townSerializerFileName;
    /**
     * String name of townAssociation file
     */
    private static String townAssociationFileName;
    /**
     * String name of cityCoordinates file
     */
    private static String cityCoordinatesFileName;
    /**
     * String ArrayList of Town regex
     */
    private static ArrayList<String> townRegex = new ArrayList<>();
    /**
     * HashMap of String towns and coordinates
     */
    private static HashMap<String, String> townCoordinatesMap;
    /**
     * String separator for Towns in townCoordinatesMap
     */
    private static final String townCoordinatesSeparator = "->";

    /**
     * Serializer instance getter, initialize instance if null
     *
     * @return
     */
    public static Serializer getInstance() {
        if (instance == null) {
            new Serializer();
        }
        return instance;
    }

    /**
     * SerializedTowns Getter
     *
     * @return
     */
    public ArrayList<Town> getSerializedTowns() {
        return serializedTowns;
    }

    /**
     * Path getter
     *
     * @return
     */
    public static String getPath() {
        return path;
    }

    /**
     * Path setter
     *
     * @param path
     */
    public static void setPath(String path) {
        Serializer.path = path;
    }

    /**
     * Jar setter
     *
     * @param jar
     */
    public static void setJar(boolean jar) {
        Serializer.jar = jar;
    }

    /**
     * TownRegex getter
     *
     * @return
     */
    public static ArrayList<String> getTownRegex() {
        if (instance == null) {
            new Serializer();
        }
        return townRegex;
    }

    /**
     * Jar getter
     *
     * @return
     */
    public boolean isJar() {
        return jar;
    }

    /**
     * SerializerConstructor : init path; serializer properties and get townSerializer data
     */
    private Serializer() {
        initPath();
        initProperties();
        initSerializedTowns();
        initTownCoordinatesMap();
        Serializer.instance = this;
    }

    /**
     * Function initSerializedTowns : init serializedTowns from serialized file
     */
    public void initSerializedTowns() {
        File f = new File(path + townSerializerFileName);

        if (f.exists()) {
            logger.info("Serializer file found");
            try {
                serializedTowns = new ArrayList<>();
                InputStream file = new FileInputStream(path + townSerializerFileName);
                InputStream buffer = new BufferedInputStream(file);
                ObjectInput input = new ObjectInputStream(buffer);
                //deserialize the List
                serializedTowns = (ArrayList<Town>) input.readObject();
                logger.info("Serializer file well read");
            } catch (InvalidClassException ex) {
                logger.warn("Failed to recognize Serializer class", ex);
            } catch (Exception ex) {
                logger.warn("Failed to read Serializer file", ex);
            }
        } else {
            logger.info("Serializer file not found");
        }
    }

    /**
     * Function initProperties : init fileName and regex properties from serializer.properties file
     */
    public void initProperties() {
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream(path + "serializer.properties");
            prop.load(input);
            townSerializerFileName = prop.getProperty("townSerializerFileName");
            townAssociationFileName = prop.getProperty("townAssociationFile");
            cityCoordinatesFileName = prop.getProperty("cityCoordinatesFile");
            int indexCityFile = 1;
            String property = prop.getProperty("townRegex" + indexCityFile);
            while (property != null) {
                townRegex.add(property);
                property = prop.getProperty("townRegex" + indexCityFile);
                indexCityFile++;
            }
        } catch (IOException ex) {
            logger.error("Failed to initialize logger properties", ex);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    logger.error("Failed to close inputStream", e);
                }
            }
        }
    }

    /**
     * Function initPath : init path and jar variables according to environment
     */
    private void initPath() {
        path = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" +
                File.separator + "resources" + File.separator;
        String className = Serializer.class.getName().replace('.', '/');
        String classJar = Serializer.class.getClass().getResource("/" + className + ".class").toString();
        if (classJar.startsWith("jar:")) {
            jar = true;
            path = System.getProperty("user.dir") + File.separator + "Properties" + File.separator;
        }
    }

    /**
     * Fonction initTownAssociation : return TownAssociation alias map
     *
     * @throws Exception if the file is not found
     */
    public HashMap<String, String> initTownAssociation() throws Exception {
        HashMap<String, String> association = new HashMap<>();
        File f = new File(path + townAssociationFileName);
        if (f.exists()) {
            logger.info("Association file found");
            String sCurrentLine;
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path + townAssociationFileName), "UTF-8"));
            while ((sCurrentLine = br.readLine()) != null) {
                String[] temp = sCurrentLine.split("->");
                association.put(temp[0], temp[1]);
            }
        } else {
            logger.error("Association file not found");
        }
        return association;
    }

    /**
     * Fonction saveTownAssociation : save TownAssociation alias file
     *
     * @param townAssociation
     */
    public void saveTownAssociation(HashMap<String, String> townAssociation) {
        String content = "";
        for (Map.Entry<String, String> entry : townAssociation.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            content += key + "----" + value + "\n";
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path + townAssociationFileName))) {
            bw.write(content);
            logger.info("Town Association file updated");
        } catch (IOException e) {
            logger.error("Town Association file update failed", e);
        }
    }

    /**
     * Function initTownCoordinatesMap : initialize townCoordinatesMap by reading cityCoordinatesFileName properties value file
     */
    public void initTownCoordinatesMap() {
        townCoordinatesMap = new HashMap<>();
        try {
            File f = new File(path + cityCoordinatesFileName);
            if (f.exists()) {
                logger.info("City file found");
                String sCurrentLine;
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path + cityCoordinatesFileName), "UTF-8"));
                while ((sCurrentLine = br.readLine()) != null) {
                    String[] temp = sCurrentLine.split(townCoordinatesSeparator);
                    townCoordinatesMap.put(temp[0], temp[1]);
                }
            } else {
                logger.warn("city file not found");
            }
        } catch (IOException exception) {
            logger.info("Failed to read townCoordinatesMap file", exception);
            return;
        }
    }

    /**
     * Function addTownToCoordinateMap : add city and his coordinates to townCoordinatesMap
     *
     * @param city
     * @param latitude
     * @param longitude
     */
    public void addTownToCoordinateMap(String city, String latitude, String longitude) {
        String coordinateSeparator = ":";
        String newValue = latitude + coordinateSeparator + longitude;
        townCoordinatesMap.put(city, newValue);
    }

    /**
     * Function printTownCoordinatesMap : print townCoordinatesMap with pretty print
     *
     * @return
     */
    public String printTownCoordinatesMap() {
        String contents = "";
        for (Map.Entry<String, String> entry : townCoordinatesMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            contents += key + townCoordinatesSeparator + value + System.lineSeparator();
        }
        return contents;
    }

    /**
     * Function writeCoordinateMap : read and write cityCoordinatesFileName
     */
    public void writeCoordinateMap() {
        String contents = printTownCoordinatesMap();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path + cityCoordinatesFileName))) {
            bw.write(contents);
            logger.info("City file updated");
        } catch (IOException e) {
            logger.error(e);
            logger.info("Writing City file update failed");
        }
    }

    /**
     * Function getCoordinatesFromFile : return String coordinate from cityCoordinatesFileName file with String and county
     *
     * @param city
     * @param county
     * @return
     */
    public String getCoordinatesFromFile(String city, String county) {
        String name = city + "|" + county;
        String citySeparator = "->";
        if (townCoordinatesMap == null) {
            townCoordinatesMap = new HashMap<>();
            try {
                File f = new File(path + cityCoordinatesFileName);
                if (f.exists()) {
                    logger.info("City file found");
                    String sCurrentLine;
                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path + cityCoordinatesFileName), "UTF-8"));
                    while ((sCurrentLine = br.readLine()) != null) {
                        String[] temp = sCurrentLine.split(citySeparator);
                        townCoordinatesMap.put(temp[0], temp[1]);
                    }
                } else {
                    logger.warn("city file not found");
                }
            } catch (IOException exception) {
                logger.error(exception);
                logger.info("Reading City file update failed");
                return null;
            }
        }
        return townCoordinatesMap.get(name);
    }

    /**
     * Function saveTownSerialized : save town list input serialized into serializerType properties file
     *
     * @param town
     */
    public void saveSerializedTown(List<Town> town) {
        try {
            File file0 = new File(path + townSerializerFileName);
            if (file0.exists()) {
                logger.info("Serializer file found for saving");
            } else {
                logger.info("Serializer file not found for saving");
                file0.createNewFile();
                logger.info("Serialized file created for saving");
            }
            FileOutputStream fileOut = new FileOutputStream(file0.getAbsolutePath());
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(town);
            out.close();
            fileOut.close();
            logger.info("Saved serialized list of town with success");
        } catch (IOException i) {
            logger.error("Failed to save Serialized Town list", i);
        }
    }

    /**
     * Function getNullCoordinatesCities : return list of Town with null coordinates
     *
     * @param input
     * @return
     */
    public static ArrayList<Town> getNullCoordinatesCities(ArrayList<Town> input) {
        ArrayList<Town> towns = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            if (input.get(i).getCoordinates() == null) {
                towns.add(input.get(i));
            }
        }
        return towns;
    }
}
