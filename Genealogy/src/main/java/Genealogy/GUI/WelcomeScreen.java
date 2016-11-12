package Genealogy.GUI;

import Genealogy.Genealogy;
import Genealogy.Main;
import Genealogy.Model.Town;
import Genealogy.Parsing.MyGedcomReader;
import Genealogy.URLConnexion.MyHttpURLConnexion;
import Genealogy.URLConnexion.Serializer;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by Dan on 10/04/2016.
 */
public class WelcomeScreen extends JFrame{
    private JButton selFichierButton;
    private JPanel welcomePanel;
    private JTextArea filePath;
    private JButton chargerFichierButton;
    final static Logger logger = Logger.getLogger(WelcomeScreen.class);

    public WelcomeScreen(String title) {
        super(title);
        initText();
        initButtons();

        setPreferredSize(new Dimension(500,300));
        pack();
        setLocationRelativeTo(null);

        setContentPane(welcomePanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void initButtons() {
        selFichierButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser c;
                Serializer serializer = new Serializer(true);
                if (serializer.isJar()){
                    c = new JFileChooser(Main.myJarFolfer);
                } else {
                    c = new JFileChooser(Main.myFolder);
                }

                // Demonstrate "Open" dialog:
                int rVal = c.showOpenDialog(WelcomeScreen.this);
                if (rVal == JFileChooser.APPROVE_OPTION) {
                    filePath.setText(c.getCurrentDirectory().toString() + File.separator + c.getSelectedFile().getName());
                }
                if (rVal == JFileChooser.CANCEL_OPTION) {
                }
            }
        });

        chargerFichierButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (filePath.getText().equals("Fichier à charger")){

                    JOptionPane.showMessageDialog(welcomePanel,
                            "Renseigner le fichier gedcom",
                            "Information",
                            JOptionPane.INFORMATION_MESSAGE);
                    logger.error("Le fichier gedcom n'a pas été renseigné");
                } else {
                    try{
                        MyGedcomReader myGedcomReader = new MyGedcomReader();
                        Genealogy.genealogy = myGedcomReader.read(filePath.getText());
                        Genealogy.genealogy.parseContents();
                        Genealogy.genealogy.sortPersons();
                        Town.setCoordinates();
                        //Traitement de villes non trouvées
                        ArrayList<String> lostTowns = Town.getLostTowns();
                        if ((lostTowns != null)&&(!lostTowns.isEmpty())){
                            String txt = lostTowns.toString();
                            JOptionPane.showMessageDialog(welcomePanel,
                                    "Les villes suivantes n'ont pas été trouvées : \n" + txt,
                                    "Erreur",
                                    JOptionPane.ERROR_MESSAGE);
                            logger.error("Les villes suivantes n'ont pas été trouvées : "+ txt);
                        }
                        Serializer.getSerializer().saveTown(Town.getTownsToSave());
                        logger.warn("Villes avec Coordonnées nulles : " + Serializer.getNullCoordinatesCities(Town.getTowns()));
                        Genealogy.genealogy.initPersonsPeriods();
                        setVisible(false);
                        MainScreen mainScreen = new MainScreen("Ma Généalogie");
                    }
                    catch (Exception exception){
                        exception.printStackTrace();
                        JOptionPane.showMessageDialog(welcomePanel,
                                "Impossible d'importer le fichier : \n" + exception.getMessage(),
                                "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                        logger.error("Impossible d'importer le fichier");
                    }
                }
            }
        });
    }

    private void initText() {
        filePath.setText("Fichier à charger");
        filePath.setEditable(false);
    }

    public static void main(String[] args){
        WelcomeScreen welcomeScreen = new WelcomeScreen("Ma généalogie");
    }

}
