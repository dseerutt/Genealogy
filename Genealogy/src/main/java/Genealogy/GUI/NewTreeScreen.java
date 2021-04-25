package Genealogy.GUI;

import Genealogy.Model.Gedcom.Genealogy;
import Genealogy.Model.Gedcom.Person;
import Genealogy.URLConnexion.Geneanet.GeneanetTreeManager;
import Genealogy.URLConnexion.Geneanet.TreeComparatorManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NewTreeScreen extends JFrame {
    private JPanel newTreePanel;
    private JTextArea nom;
    private JTextArea url;
    private JButton ajouterButton;
    private JButton retourButton;
    private JComboBox personneGedcom;
    private static NewTreeScreen instance;
    final static Logger logger = LogManager.getLogger(TreeModificationScreen.class);

    private NewTreeScreen() {
        super("Nouvel Arbre");

        initFields();


        setPreferredSize(new Dimension(500, 200));
        pack();
        setLocationRelativeTo(null);
        setContentPane(newTreePanel);
        setVisible(true);
    }

    public static NewTreeScreen getInstance() {
        if (instance == null) {
            instance = new NewTreeScreen();
        }
        return instance;
    }

    private void initFields() {
        retourButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                MainScreen.getINSTANCE().setVisible(true);
            }
        });
        ajouterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    MainScreen mainScreen = MainScreen.getINSTANCE();
                    String nomText = nom.getText();
                    String personId = ((ComboItem) personneGedcom.getSelectedItem()).getValue();
                    GeneanetTreeManager geneanetTreeManager = TreeComparatorManager.getInstance().getGeneanetBrowser().getGeneanetTreeManager();
                    geneanetTreeManager.addNewTree(nom.getText(), url.getText(), personId);
                    mainScreen.getArbre().addItem(new ComboItem(nomText, personId));
                    setVisible(false);
                    mainScreen.setVisible(true);
                } catch (Exception ex) {
                    logger.error("Failed to open Geneanet link ");
                    JOptionPane.showMessageDialog(newTreePanel, "Impossible d'ajouter l'arbre " + ex,
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        for (Person person : Genealogy.genealogy.getPersons()) {
            personneGedcom.addItem(new ComboItem(person.printPersonWithDates(), person.getId()));
        }
    }
}
