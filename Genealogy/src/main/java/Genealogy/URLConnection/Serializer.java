package Genealogy.URLConnection;

import Genealogy.Genealogy;
import Genealogy.Model.Town;
import Genealogy.Parsing.MyGedcomReader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dan on 15/04/2016.
 */
public class Serializer {

    private static Serializer serializer = new Serializer();
    private ArrayList<Town> towns;
    private String path;
    private boolean jar = false;

    public static Serializer getSerializer() {
        return serializer;
    }

    public ArrayList<Town> getTowns() {
        return towns;
    }

    public Serializer(boolean x) {
        initPath();
    }

    public Serializer() {
        initPath();
        File f = new File(path);

        if(f.exists()){
            System.out.println("Serializer file found for reading");
            try {
                towns = new ArrayList<>();
                InputStream file = new FileInputStream(path);
                InputStream buffer = new BufferedInputStream(file);
                ObjectInput input = new ObjectInputStream (buffer);
                //deserialize the List
                towns = (ArrayList<Town>) input.readObject();
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Serializer file not found");
        }
    }

    public boolean isJar() {
        return jar;
    }

    private void initPath() {
        path = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" +
                File.separator + "resources" + File.separator + "cities.ser";
        String className = Serializer.class.getName().replace('.', '/');
        String classJar = Serializer.class.getClass().getResource("/" + className + ".class").toString();
        if (classJar.startsWith("jar:")) {
            //System.out.println("*** running from jar!");
            jar = true;
            path = System.getProperty("user.dir") + File.separator + "Properties" + File.separator + "cities.ser";
        }
    }

    public void saveTown(List<Town> town){
        try
        {
            File file0 = new File(path);
            if(file0.exists()){
                System.out.println("Serializer file found for saving");
            }else{
                System.out.println("Serializer file not found for saving");
                file0.createNewFile();
                System.out.println("Serialized file created for saving");
            }
            BufferedReader br = new BufferedReader(new FileReader(path));
            FileOutputStream fileOut = new FileOutputStream(file0.getAbsolutePath());
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(town);
            out.close();
            fileOut.close();
            System.out.println("Saved serialized list of town with success");
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
