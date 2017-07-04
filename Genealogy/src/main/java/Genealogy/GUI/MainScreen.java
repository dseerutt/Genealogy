package Genealogy.GUI;

import Genealogy.Genealogy;
import Genealogy.Model.Act.Act;
import Genealogy.Model.Act.Birth;
import Genealogy.Model.Act.Union;
import Genealogy.Model.Person;
import Genealogy.Model.Town;
import Genealogy.URLConnexion.MyHttpURLConnexion;
import Genealogy.URLConnexion.URLException;
import edu.emory.mathcs.backport.java.util.Collections;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Dan on 10/04/2016.
 */
public class MainScreen extends JFrame {
    private JTabbedPane tabbedPane1;
    private JButton retourButton;
    private JPanel mainPanel;
    private JComboBox directAncestors;
    private JButton voirLaFicheButton1;
    private JComboBox ancestors;
    private JButton voirLaFicheButton2;
    private JComboBox towns;
    private JTextArea textArea1;
    private JTextArea textArea2;
    private JButton voirLesActesButton;
    private JTextArea naissances;
    private JTextArea unions;
    private JTextArea deces;
    private JPanel printPanel;
    private JButton carteButton;
    private JComboBox NotFoundPlaces;
    private JTextField TownQuery;
    private JTextField TownAnswer;
    private JPanel mapPanel;
    private static MainScreen INSTANCE;
    final static Logger logger = Logger.getLogger(MainScreen.class);

    public static MainScreen getINSTANCE() {
        return INSTANCE;
    }

    public MainScreen(String title) throws IOException {
        super(title);

        initButtons();
        initComboBox();
        initTab1();

        setPreferredSize(new Dimension(700,500));
        pack();
        setLocationRelativeTo(null);

        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        INSTANCE = this;
    }

    /**
     * Fonction initTab1
     * initialise la liste des ancêtres directs
     */
    private void initTab1() {
        voirLaFicheButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = 0;
                int res = directAncestors.getSelectedIndex();
                ArrayList<Person> persons = Genealogy.genealogy.getPersons();
                if (persons != null){
                    for (int i = 0; i < persons.size() ; i++){
                        if (persons.get(i).isDirectAncestor()){
                            Person p = persons.get(i);
                            if ((p != null) && (index == res)){
                                textArea1.setText(p.printPerson());
                            }
                            index++;
                        }
                    }
                }
            }
        });

        voirLaFicheButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea2.setText(Genealogy.genealogy.getPersons().get(ancestors.getSelectedIndex()).printPerson());
            }
        });
    }

    /**
     * Fonction initComboBox
     * Initialise les combobox ancestors, directAncestors et towns
     */
    private void initComboBox() {
        Town.sortTowns();
        for (int i = 0 ; i  < Genealogy.genealogy.getPersons().size() ; i++){
            Person person = Genealogy.genealogy.getPersons().get(i);
            if ((person !=null)&&(person.isPrintable())){
                ancestors.addItem(person.getFullNameInverted());
                if (person.isDirectAncestor()){
                    directAncestors.addItem(Genealogy.genealogy.getPersons().get(i).getFullNameInverted());
                }
            }
        }
        for (int i = 0 ; i < Town.getTowns().size() ; i++){
            towns.addItem(Town.getTowns().get(i).getName());
        }
    }

    private void initButtons() {
        retourButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                WelcomeScreen welcomeScreen = new WelcomeScreen("Ma généalogie");
            }
        });

        carteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //Test connexion internet
                    if (!MyHttpURLConnexion.testInternetConnexion()){
                        throw new URLException("Impossible d'afficher la carte sans connexion internet");
                    }
                    MapScreen mapScreen = new MapScreen();
                    setVisible(false);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(mainPanel,e1.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        voirLesActesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                naissances.setText("");
                unions.setText("");
                deces.setText("");
                int index = towns.getSelectedIndex();
                Town thisTown = Town.getTowns().get(index);
                ArrayList<Act> list = Town.getListOfTown().get(thisTown);
                ArrayList<String> birthList = new ArrayList<String>();
                ArrayList<String> unionsList = new ArrayList<String>();
                ArrayList<String> deathList = new ArrayList<String>();
                for (int i = 0 ; i < list.size() ; i++){
                    if (list.get(i) instanceof Birth){
                        birthList.add(naissances.getText() + list.get(i).getCitizen().getFullNameInverted() + "\n");
                    } else if (list.get(i) instanceof Union){
                        unionsList.add(unions.getText() + list.get(i).getCitizen().getFullNameInverted() + " \navec " +
                                ((Union) list.get(i)).getPartner().getFullNameInverted() + "\n");
                    } else {
                        deathList.add(deces.getText() + list.get(i).getCitizen().getFullNameInverted() + "\n");
                    }
                }
                Collections.sort(birthList);
                Collections.sort(unionsList);
                Collections.sort(deathList);
                for (String s : birthList){
                    naissances.setText(naissances.getText() + s);
                }
                for (String s : unionsList){
                    unions.setText(unions.getText() + s);
                }
                for (String s : deathList){
                    deces.setText(deces.getText() + s);
                }
            }
        });
    }

    public BufferedImage createImage(JPanel panel) {

        int w = panel.getWidth();
        int h = panel.getHeight();
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();
        panel.paint(g);
        return bi;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
