package Genealogy;

import Genealogy.GUI.WelcomeScreen;
import Genealogy.Parsing.MyGedcomReader;
import Genealogy.URLConnexion.MyHttpURLConnexion;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

/**
 * Created by Dan on 04/04/2016.
 */
public class Main {
    public static final String myFile = "famille1.ged";
    public static final String myFolder = "C:\\Users\\Dan\\Desktop\\Programmation\\IntelliJ\\Genealogy\\Genealogy\\src\\main\\resources\\";
    public static final String myJarFolfer = System.getProperty("user.dir") + File.separator + "Properties" + File.separator;
    final static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) throws ParseException, IOException {
        logger.info("Init app");
        WelcomeScreen welcomeScreen = new WelcomeScreen("Ma généalogie");
    }
}
