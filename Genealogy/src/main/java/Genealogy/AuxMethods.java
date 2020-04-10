package Genealogy;

import Genealogy.Parsing.ParsingStructure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Dan on 05/04/2016.
 */
public class AuxMethods {

    public static String DATE_FORMAT2 = "dd MMM yyyy";
    public static String DATE_FORMAT3 = "dd/MM/yyyy";
    public static String DATE_FORMAT4 = "MMM yyyy";
    public static SimpleDateFormat SDF3 = new SimpleDateFormat(DATE_FORMAT3, Locale.ENGLISH);
    final static Logger logger = LogManager.getLogger(AuxMethods.class);

    public static int findIndexNumberInteger(ArrayList<ParsingStructure> list, int number, int offset) {
        if (offset > list.size()) {
            logger.error("Erreur dans le parsing, l'index est trop grand");
            return -1;
        }
        for (int i = offset; i < list.size(); i++) {
            if (list.get(i).getNumber() == number) {
                return i;
            }
        }
        return -1;
    }

    public static int findIndexNumberString(ArrayList<ParsingStructure> list, String field, int offset, int maxIndex) {
        if (offset > list.size()) {
            logger.error("Erreur dans le parsing, l'index est trop grand");
            return -1;
        }
        for (int i = offset; i < maxIndex; i++) {
            if (list.get(i).getId().equals(field)) {
                return i;
            }
        }
        return -1;
    }

    public static String getStringDate(Date date) {
        if (date == null) {
            return "null";
        }
        return SDF3.format(date);
    }

    public static int getMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH);
    }

    public static int getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    public static int getDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    public static Color getColor(int age) {
        if (age >= 100) {
            return new Color(16, 52, 166);
        } else if (age >= 90) {
            return new Color(16, 80, 166);
        } else if (age >= 80) {
            return new Color(21, 96, 189);
        } else if (age >= 70) {
            return new Color(49, 140, 231);
        } else if (age >= 60) {
            return new Color(10, 186, 181);
        } else if (age >= 55) {
            return new Color(9, 106, 9);
        } else if (age >= 50) {
            return new Color(20, 148, 20);
        } else if (age >= 45) {
            return new Color(0, 255, 0);
        } else if (age >= 40) {
            return new Color(1, 215, 88);
        } else if (age >= 35) {
            return new Color(135, 233, 144);
        } else if (age >= 30) {
            return new Color(205, 205, 13);
        } else if (age >= 20) {
            return new Color(255, 255, 0);
        } else if (age >= 10) {
            return new Color(239, 155, 15);
        } else if (age >= 05) {
            return new Color(231, 62, 1);
        } else if (age >= 0) {
            return new Color(255, 0, 0);
        } else {
            return Color.lightGray;
        }
    }

    public static Color getColor2(int age) {
        if (age >= 30) {
            return new Color(21, 96, 189);
        } else if (age >= 25) {
            return new Color(49, 140, 231);
        } else if (age >= 18) {
            return new Color(10, 186, 181);
        } else if (age >= 16) {
            return new Color(116, 208, 241);
        } else if (age >= 14) {
            return new Color(169, 234, 234);
        } else if (age >= 12) {
            return new Color(9, 106, 9);
        } else if (age >= 10) {
            return new Color(86, 130, 3);
        } else if (age >= 8) {
            return new Color(20, 148, 20);
        } else if (age >= 6) {
            return new Color(0, 255, 0);
        } else if (age >= 5) {
            return new Color(1, 215, 88);
        } else if (age >= 4) {
            return new Color(205, 205, 13);
        } else if (age >= 3) {
            return new Color(255, 255, 0);
        } else if (age >= 2) {
            return new Color(255, 203, 96);
        } else if (age >= 1) {
            return new Color(239, 155, 15);
        } else {
            return Color.lightGray;
        }
    }

    public static String removeDoubleGeneanetSuffix(String input) {
        if (input != null) {
            return input.replace("&ocz=0", "").replaceAll("&iz=.*?&", "&").replaceAll("&pz=.*?&", "&").replaceAll("&nz=.*?&", "&");
        } else {
            return null;
        }
    }

    public static String removeGeneanetSuffix(String input) {
        if (input != null) {
            return input.replace("&ocz=0", "").replaceAll("&pz=.*?&", "&").replaceAll("&nz=.*?&", "&");
        } else {
            return null;
        }
    }
}
