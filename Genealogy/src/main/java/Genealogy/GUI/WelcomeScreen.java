package Genealogy.GUI;

import Genealogy.Model.Gedcom.Genealogy;
import Genealogy.Model.Gedcom.Town;
import Genealogy.Parsing.MyGedcomReader;
import Genealogy.URLConnexion.Serializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import static Genealogy.GUI.MainScreen.myFolder;
import static Genealogy.GUI.MainScreen.myJarFolder;

/**
 * Created by Dan on 10/04/2016.
 */
public class WelcomeScreen extends JFrame {
    private JButton selFichierButton;
    private JPanel welcomePanel;
    private JTextArea filePath = new JTextArea();
    private JButton chargerFichierButton;
    private JPanel loadingPanel;
    private JProgressBar progressBar1;
    final static Logger logger = LogManager.getLogger(WelcomeScreen.class);

    public WelcomeScreen(String title) {
        super(title);
        //initForm();
        initText();
        initButtons();

        setPreferredSize(new Dimension(500, 300));
        pack();
        setLocationRelativeTo(null);
        progressBar1.setVisible(false);

        setContentPane(welcomePanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void initTownAssociation(Serializer serializer) {
        //Gestion des associations
        try {
            Town.setTownAssociation(serializer.initAssociation());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Problème dans le parsing du fichier d'associations de ville");
        }
    }

    private JFileChooser initSerializer() {
        Serializer<Town> serializer = Serializer.getInstance();
        if (serializer.isJar()) {
            return new JFileChooser(myJarFolder);
        } else {
            return new JFileChooser(myFolder);
        }
    }

    private void initButtons() {
        selFichierButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser c = initSerializer();
                // Demonstrate "Open" dialog:
                int rVal = c.showOpenDialog(WelcomeScreen.this);
                if (rVal == JFileChooser.APPROVE_OPTION) {
                    filePath.setText(c.getCurrentDirectory().toString() + File.separator + c.getSelectedFile().getName());
                    initTownAssociation(Serializer.getInstance());
                }
                if (rVal == JFileChooser.CANCEL_OPTION) {
                }
            }
        });

        chargerFichierButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (filePath.getText().equals("Fichier à charger")) {

                    JOptionPane.showMessageDialog(welcomePanel,
                            "Renseigner le fichier gedcom",
                            "Information",
                            JOptionPane.INFORMATION_MESSAGE);
                    logger.error("Le fichier gedcom n'a pas été renseigné");
                } else {
                    progressBar1.setIndeterminate(true);
                    progressBar1.setVisible(true);
                    new SwingWorker<Void, String>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            // Worken hard or hardly worken...
                            try {
                                MyGedcomReader myGedcomReader = new MyGedcomReader();
                                Genealogy.genealogy = myGedcomReader.read(filePath.getText());
                                Genealogy.genealogy.parseContents();
                                Genealogy.genealogy.sortPersons();
                                Town.setAllCoordinates();
                                //Traitement de villes non trouvées
                                ArrayList<String> lostTowns = Town.getLostTowns();
                                if ((lostTowns != null) && (!lostTowns.isEmpty())) {
                                    String txt = lostTowns.toString();
                                    JOptionPane.showMessageDialog(welcomePanel,
                                            "Les villes suivantes n'ont pas été trouvées : \n" + txt,
                                            "Erreur",
                                            JOptionPane.ERROR_MESSAGE);
                                    logger.error("Les villes suivantes n'ont pas été trouvées : " + txt);
                                }
                                Serializer.getInstance().saveTownSerialized(Town.getTownsToSerialize());
                                ArrayList<Town> myEmptyTowns = Serializer.getNullCoordinatesCities(Town.getTowns());
                                if (!myEmptyTowns.isEmpty()) {
                                    logger.warn("Villes avec Coordonnées nulles : " + myEmptyTowns);
                                }
                                Genealogy.genealogy.initPersonsLifeSpans();
                                setVisible(false);
                                MainScreen mainScreen = new MainScreen("Ma Généalogie");
                            } catch (Exception exception) {
                                exception.printStackTrace();
                                JOptionPane.showMessageDialog(welcomePanel,
                                        "Impossible d'importer le fichier",
                                        "Erreur",
                                        JOptionPane.ERROR_MESSAGE);
                                logger.error("Impossible d'importer le fichier" + exception.getMessage());
                            }
                            return null;
                        }

                        @Override
                        protected void done() {
                        }
                    }.execute();

                }
            }
        });
    }

    private void initText() {
        filePath.setText("Fichier à charger");
        filePath.setEditable(false);
    }

}
