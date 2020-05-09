package Genealogy;

import Genealogy.GUI.MainScreen;
import Genealogy.GUI.WelcomeScreen;
import Genealogy.Model.Gedcom.Genealogy;
import Genealogy.Model.Gedcom.Town;
import Genealogy.Parsing.MyGedcomReader;
import Genealogy.URLConnexion.Serializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;

/**
 * Main class : launch Genealogy project
 */
public class Main {
    /**
     * Main Logger
     */
    final static Logger logger = LogManager.getLogger(Main.class);

    /**
     * Function main : launch the program, no argument starts the default gedcom file
     *
     * @param args : not used
     */
    public static void main(String[] args) throws Exception {
        try {
            logger.info("Init app");
            if (args.length != 0) {
                new WelcomeScreen("Ma généalogie");
            } else {
                initQuickMainScreen();
            }
        } catch (Exception e) {
            logger.error("Erreur inattendue", e);
            throw e;
        }
    }

    /**
     * Function initQuickMainScreen : initialize main screen with default gedcom file
     *
     * @throws Exception
     */
    public static void initQuickMainScreen() throws Exception {
        MyGedcomReader myGedcomReader = new MyGedcomReader();
        Genealogy.genealogy = myGedcomReader.read("src\\main\\resources\\famille1.ged");
        Genealogy.genealogy.parseContents();
        logger.info("3 " + Instant.now());
        Genealogy.genealogy.sortPersons();
        Town.setAllCoordinates();
        Serializer.getInstance().saveSerializedTownList();
        Genealogy.genealogy.initPersonsLifeSpans();
        new MainScreen("Ma Généalogie");
    }
}
