package Genealogy.GUI;

import Genealogy.Model.Gedcom.Genealogy;
import Genealogy.Model.Gedcom.Person;

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
            }
        });

        for (Person person : Genealogy.genealogy.getPersons()) {
            personneGedcom.addItem(new ComboItem(person.printPersonWithDates(), person.getId()));
        }
    }
}
