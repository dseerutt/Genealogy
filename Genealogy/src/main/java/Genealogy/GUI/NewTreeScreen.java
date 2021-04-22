package Genealogy.GUI;

import Genealogy.Model.Gedcom.Genealogy;
import Genealogy.Model.Gedcom.Person;

import javax.swing.*;
import java.awt.*;

public class NewTreeScreen extends JFrame {
    private JPanel newTreePanel;
    private JTextArea nom;
    private JTextArea url;
    private JButton ajouterButton;
    private JButton retourButton;
    private JComboBox personneGedcom;

    public NewTreeScreen() {
        super("Nouvel Arbre");

        for (Person person : Genealogy.genealogy.getPersons()) {
            personneGedcom.addItem(new ComboItem(person.printPersonWithDates(), person.getId()));
        }

        setPreferredSize(new Dimension(500, 200));
        pack();
        setLocationRelativeTo(null);
        setContentPane(newTreePanel);
        setVisible(true);
    }
}
