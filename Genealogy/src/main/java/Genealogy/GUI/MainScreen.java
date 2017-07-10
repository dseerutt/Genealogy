package Genealogy.GUI;

import Genealogy.Genealogy;
import Genealogy.MapViewer.Structures.MyCoordinate;
import Genealogy.Model.Act.Act;
import Genealogy.Model.Act.Birth;
import Genealogy.Model.Act.Union;
import Genealogy.Model.Person;
import Genealogy.Model.Town;
import Genealogy.URLConnexion.MyHttpURLConnexion;
import Genealogy.URLConnexion.Serializer;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dan on 10/04/2016.
 */
public class MainScreen extends JFrame {
    private JTabbedPane tabbedPane1;
    private JButton retourButton;
    private JPanel mainPanel;
    private JComboBox directAncestors;
    private JComboBox ancestors;
    private JComboBox towns;
    private JTextArea textArea1;
    private JTextArea textArea2;
    private JTextArea naissances;
    private JTextArea unions;
    private JTextArea deces;
    private JPanel printPanel;
    private JButton carteButton;
    private JComboBox NotFoundPlaces;
    private JTextField TownQuery;
    private JTextField searchField;
    private JButton remplacerButton;
    private JButton rechercherButton;
    private static MainScreen INSTANCE;
    final static Logger logger = Logger.getLogger(MainScreen.class);
    private MyHttpURLConnexion HTTPConnexion;

    public static MainScreen getINSTANCE() {
        return INSTANCE;
    }

    public MainScreen(String title) throws IOException {
        super(title);

        initButtons();
        initComboBox();
        initTab1();
        initMissingCitiesTab();
        HTTPConnexion = new MyHttpURLConnexion();

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
        directAncestors.addActionListener(new ActionListener() {
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
            ArrayList<Person> persons = Genealogy.genealogy.getPersons();
            if (persons != null && persons.size() > 0){
            Person p = persons.get(0);
            if (p != null){
                textArea1.setText(p.printPerson());
            }
        }

        ancestors.addActionListener(new ActionListener() {
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

        towns.addActionListener(new ActionListener() {
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

    private void updateMissingCityTab(HashMap<String, String> townAssociation){
        if (!townAssociation.isEmpty()){
            TownQuery.setText(townAssociation.get(NotFoundPlaces.getSelectedItem().toString()));
        }
    }

    private void initMissingCitiesTab() {
        final HashMap<String, String> townAssociation = Town.getTownAssociation();
        if (townAssociation != null){
            for (Map.Entry<String, String> association : townAssociation.entrySet()){
                NotFoundPlaces.addItem(association.getKey());
            }
            NotFoundPlaces.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    TownQuery.setText(townAssociation.get(NotFoundPlaces.getSelectedItem().toString()));
                }
            });
            updateMissingCityTab(townAssociation);
        }
        TownQuery.setEditable(false);
        rechercherVilleNonTrouvee();
        remplacerAction();
    }

    private void remplacerAction(){
        remplacerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String key = (String) NotFoundPlaces.getSelectedItem();
                String value = searchField.getText();
                HashMap<String, String> townAssociation = Town.getTownAssociation();
                townAssociation.remove(key);
                townAssociation.put(key,value);
                Serializer.getSerializer().saveTownAssociation(townAssociation);
                updateMissingCityTab(townAssociation);
                JOptionPane.showMessageDialog(tabbedPane1,
                        "Mise à jour d'alias de ville effectuée avec succès",
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    private void rechercherVilleNonTrouvee(){
        rechercherButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String search = searchField.getText();
                if (search != null && !search.equals("")){
                    try {
                        MyCoordinate result = Town.parseJsonArray(HTTPConnexion.sendAddressRequest(search),search);
                        logger.info("Coordonnées de la ville " + search + " : " + result);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        logger.error("Erreur lors de la recherche de ville");
                        JOptionPane.showMessageDialog(tabbedPane1,
                                "Erreur lors de la recherche de ville",
                                "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
