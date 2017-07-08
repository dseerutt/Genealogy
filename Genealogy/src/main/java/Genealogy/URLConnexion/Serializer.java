package Genealogy.URLConnexion;

import Genealogy.AuxMethods;
import Genealogy.Model.Town;
import Genealogy.Parsing.MyGedcomReader;
import org.apache.log4j.Logger;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dan on 15/04/2016.
 */
public class Serializer {

    final static Logger logger = Logger.getLogger(Serializer.class);
    private static Serializer serializer = new Serializer();
    private ArrayList<Town> towns;
    private static HashMap<String,String> townAssociation;
    private String path;
    private boolean jar = false;
    private static final String citiesFile = "cities.ser";
    private static final String townAssociationFile = "City names.txt";

    public static Serializer getSerializer() {
        return serializer;
    }

    public ArrayList<Town> getTowns() {
        return towns;
    }

    public static HashMap<String, String> getTownAssociation() {
        return townAssociation;
    }

    public Serializer(boolean x) {
        initPath();
    }

    public Serializer() {
        initPath();
        File f = new File(path + citiesFile);

        if(f.exists()){
            logger.info("Serializer file found");
            try {
                towns = new ArrayList<>();
                InputStream file = new FileInputStream(path + citiesFile);
                InputStream buffer = new BufferedInputStream(file);
                ObjectInput input = new ObjectInputStream (buffer);
                //deserialize the List
                towns = (ArrayList<Town>) input.readObject();
                logger.info("Serializer file well read");
            }
            catch(InvalidClassException ex){
                logger.error("Serializer Class not OK");
                ex.printStackTrace();
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
        }else{
            logger.info("Serializer file not found");
        }
        //Gestion des associations
        try {
            initTownAssociation();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Problème dans le parsing du fichier d'associations de ville");
        }
    }

    public boolean isJar() {
        return jar;
    }

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
     * Fonction initTownAssociation
     * Initialise la liste des associations de villes
     * Permet de gérer les alias
     * @throws Exception Si le fichier n'est pas trouvé
     */
    public void initTownAssociation() throws Exception {
        townAssociation = new HashMap<String,String>();
        File f = new File(path + townAssociationFile);
        if(f.exists()) {
            logger.info("City Association found");

            BufferedReader br = null;
            String sCurrentLine;
            br = new BufferedReader(new InputStreamReader(new FileInputStream(path + townAssociationFile), "UTF-8"));
            while ((sCurrentLine = br.readLine()) != null) {
                String[] temp = sCurrentLine.split("----");
                townAssociation.put(temp[0],temp[1]);
            }
        } else {
            logger.error("City Association not found");
        }
    }

    /**
     * Fonction saveTownAssociation
     * Met à jour le fichier des associations d'alias de villes
     * @param townAssociation
     */
    public void saveTownAssociation(HashMap<String,String> townAssociation){
        String content = "";
        for(Map.Entry<String, String> entry : townAssociation.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            content += key + "----" + value + "\n";
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path + townAssociationFile))) {
            bw.write(content);
            logger.info("Town Association file updated");
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("Town Association file update failed");
        }
    }

    public void saveTown(List<Town> town){
        try
        {
            File file0 = new File(path + citiesFile);
            if(file0.exists()){
                logger.info("Serializer file found for saving");
            }else{
                logger.info("Serializer file not found for saving");
                file0.createNewFile();
                logger.info("Serialized file created for saving");
            }
            BufferedReader br = new BufferedReader(new FileReader(path + citiesFile));
            FileOutputStream fileOut = new FileOutputStream(file0.getAbsolutePath());
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(town);
            out.close();
            fileOut.close();
            logger.info("Saved serialized list of town with success");
        }catch(IOException i)
        {
            i.printStackTrace();
        }
    }

    public static ArrayList<Town> getNullCoordinatesCities(ArrayList<Town> input){
        ArrayList<Town> towns = new ArrayList<>();
        for (int i = 0 ; i < input.size() ; i++){
            if (input.get(i).getCoordinates() == null){
                towns.add(input.get(i));
            }
        }
        return towns;
    }

    public static void main(String[] args) throws IOException {
        MyGedcomReader myGedcomReader = new MyGedcomReader();
        String path = "C:\\Users\\Dan\\Desktop\\Programmation\\IntelliJ\\Genealogy\\Genealogy\\src\\main\\resources\\famille1.ged";

        Serializer serializer = new Serializer();
        //Genealogy.genealogy = myGedcomReader.read(path);
        //Genealogy.genealogy.parseContents();
        //System.out.println(Town.getTowns());
        //Town.setCoordinates();
        //System.out.println(Town.getTowns());
        //serializer.saveTown(Town.getTowns());
        serializer.saveTown(new ArrayList<Town>());
        //System.out.println(getNullCoordinatesCities(Town.getTowns()));
    }
}
