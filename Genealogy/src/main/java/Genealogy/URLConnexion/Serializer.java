package Genealogy.URLConnexion;

import Genealogy.AuxMethods;
import Genealogy.Model.Town;
import Genealogy.Parsing.MyGedcomReader;
import org.apache.log4j.Logger;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by Dan on 15/04/2016.
 */
public class Serializer<T> {

    final static Logger logger = Logger.getLogger(Serializer.class);
    private static Serializer serializer;
    private ArrayList<T> serializedData;
    private String path;
    private boolean jar = false;
    private static String serializerType;
    private static String townAssociationFile;

    public static Serializer getSerializer() {
        return serializer;
    }

    public ArrayList<T> getTowns() {
        return serializedData;
    }

    /**
     * Fonction initProperties
     * initialise les propriétés de la classe Serializer
     */
    public void initProperties(){
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream(path + "serializer.properties");
            prop.load(input);
            serializerType = prop.getProperty("serializerType");
            townAssociationFile = prop.getProperty("townAssociationFile");
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Serializer(boolean x) {
        initPath();
        initProperties();
        Serializer.serializer = this;
    }

    public Serializer(Class<T> cls) {
        initPath();
        initProperties();
        File f = new File(path + serializerType);

        if(f.exists()){
            logger.info("Serializer file found");
            try {
                serializedData = new ArrayList<>();
                InputStream file = new FileInputStream(path + serializerType);
                InputStream buffer = new BufferedInputStream(file);
                ObjectInput input = new ObjectInputStream (buffer);
                //deserialize the List
                serializedData = (ArrayList<T>) input.readObject();
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
        Serializer.serializer = this;
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
     * Fonction initAssociation
     * Initialise la liste des associations de villes
     * Permet de gérer les alias
     * @throws Exception Si le fichier n'est pas trouvé
     */
    public HashMap<String,String> initAssociation() throws Exception {
        HashMap<String,String> association = new HashMap<String,String>();
        File f = new File(path + townAssociationFile);
        if(f.exists()) {
            logger.info("Association file found");
            String sCurrentLine;
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path + townAssociationFile), "UTF-8"));
            while ((sCurrentLine = br.readLine()) != null) {
                String[] temp = sCurrentLine.split("----");
                association.put(temp[0],temp[1]);
            }
        } else {
            logger.error("Association file not found");
        }
        return association;
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
            File file0 = new File(path + serializerType);
            if(file0.exists()){
                logger.info("Serializer file found for saving");
            }else{
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
}
