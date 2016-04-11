package Genealogy;

import Genealogy.Model.Person;
import Genealogy.Model.Structure;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Dan on 05/04/2016.
 */
public class AuxMethods {

    public static String DATE_FORMAT = "dd-MM-yyyy hh:mm:ss";
    public static String DATE_FORMAT2 = "dd MMM yyyy";
    public static String DATE_FORMAT3 = "dd/MM/yyyy";
    public static String DATE_FORMAT4 = "MMM yyyy";
    public static SimpleDateFormat SDF = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
    public static SimpleDateFormat SDF2 = new SimpleDateFormat(DATE_FORMAT2, Locale.ENGLISH);
    public static SimpleDateFormat SDF3 = new SimpleDateFormat(DATE_FORMAT3, Locale.ENGLISH);
    public static SimpleDateFormat SDF4 = new SimpleDateFormat(DATE_FORMAT3, Locale.ENGLISH);

    public static String findField(ArrayList<Structure> list, String field){
        return findField(list,field,0,list.size());
    }

    public static String findField(ArrayList<Structure> list, String field, int offset, int maxValue){
        if (offset > list.size()){
            System.out.println("Erreur dans le parsing, l'index est trop grand");
            return "";
        }
        for (int i = offset ; i < maxValue ; i++) {
            if (list.get(i).getId().equals(field)) {
                return list.get(i).getText();
            }
        }
        return "";
    }

    public static int findIndexNumberInteger(ArrayList<Structure> list, int number, int offset){
        if (offset > list.size()){
            System.out.println("Erreur dans le parsing, l'index est trop grand");
            return -1;
        }
        for (int i = offset ; i < list.size() ; i++) {
            if (list.get(i).getNumber() == number) {
                return i;
            }
        }
        return -1;
    }

    public static int findIndexNumberInteger(ArrayList<Structure> list, int number){
        return findIndexNumberInteger(list,number,0);
    }

    public static int findIndexNumberString(ArrayList<Structure> list, String field, int offset, int maxIndex){
        if (offset > list.size()){
            System.out.println("Erreur dans le parsing, l'index est trop grand");
            return -1;
        }
        for (int i = offset ; i < maxIndex ; i++) {
            if (list.get(i).getId().equals(field)) {
                return i;
            }
        }
        return -1;
    }

    public static void printList(ArrayList<Structure> contents, int index) {
        for (int i = index ; i < contents.size() ; i++){
            System.out.println(contents.get(i).toString2());
        }
    }

    public static String getStringDate(Date date){
        if (date == null){
            return "null";
        }
        return SDF3.format(date);
    }

    public static Date getDate(String input){
        try {
            SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT2, Locale.ENGLISH);
            return format.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("Impossible de parser la date " + input);
            return null;
        }
    }

    public static int findIDInStructure(ArrayList<Person> persons, String id){
        for (int i = 0 ; i < persons.size() ; i++){
            if (persons.get(i).getId().equals(id)){
                return i;
            }
        }
        return -1;
    }

    public static int getMonth(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH);
    }

    public static int getYear(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    public static int getDay(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }
}
