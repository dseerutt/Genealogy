package Genealogy;

import Genealogy.Model.Town;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dan on 15/04/2016.
 */
public class Serializer {

    public static void saveTown(List<Town> town){
        try
        {
            String path = System.getProperty("user.dir") + File.separator + "src"
                    + File.separator + "main" + File.separator + "resources" + File.separator + "cities.ser";
            FileOutputStream fileOut = new FileOutputStream(path);
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

    public static ArrayList<Town> readTowns(){
        String path = System.getProperty("user.dir") + File.separator + "src"
            + File.separator + "main" + File.separator + "resources" + File.separator + "cities.ser";
        //deserialize the file

        try {
            //Check if file is empty
            BufferedReader br = new BufferedReader(new FileReader(path));
            if (br.readLine() == null) {
                System.out.println("cities.ser is empty");
                return null;
            } else {
                InputStream file = new FileInputStream(path);
                InputStream buffer = new BufferedInputStream(file);
                ObjectInput input = new ObjectInputStream (buffer);
                //deserialize the List
                return (ArrayList<Town>) input.readObject();
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
            return null;
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

        Genealogy.genealogy = myGedcomReader.read(path);
        Genealogy.genealogy.parseContents();
        System.out.println(Town.getTowns());
        Town.setCoordinates();
        System.out.println(Town.getTowns());
        saveTown(Town.getTowns());
        //ArrayList<Town> townsInFile = readTowns();
        //System.out.println(getNullCoordinatesCities(Town.getTowns()));
    }
}
