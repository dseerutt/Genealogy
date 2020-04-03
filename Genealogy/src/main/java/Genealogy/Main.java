package Genealogy;

import Genealogy.GUI.WelcomeScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Dan on 04/04/2016.
 * Main class : launch Genealogy project
 */
public class Main {
    /**
     * Main Logger
     */
    final static Logger logger = LogManager.getLogger(Main.class);

    /**
     * Function main : launch the program
     *
     * @param args : not used
     */
    public static void main(String[] args) {
        try {
            logger.info("Init app");
            WelcomeScreen welcomeScreen = new WelcomeScreen("Ma généalogie");
        } catch (Exception e) {
            logger.error("Erreur inattendue", e);
            throw e;
        }
    }
}
