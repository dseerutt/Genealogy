package Genealogy.GUI;

import Genealogy.Genealogy;
import Genealogy.Model.Act.Act;
import Genealogy.Model.Act.Birth;
import Genealogy.Model.Act.Union;
import Genealogy.Model.Person;
import Genealogy.Model.Town;
import edu.emory.mathcs.backport.java.util.Collections;
import Genealogy.MyGedcomReader;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Dan on 10/04/2016.
 */
public class MainScreen extends JFrame{
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
        /*
        BufferedImage bufferedImage = createImage(printPanel);
        File outputfile = new File("C:\\Users\\Dan\\Desktop\\image.jpg");
        ImageIO.write(bufferedImage, "jpg", outputfile);*/
    }

    private void initTab1() {
        voirLaFicheButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = 0;
                int res = directAncestors.getSelectedIndex();
                for (int i = 0 ; i < Genealogy.genealogy.getPersons().size() ; i++){
                    if (Genealogy.genealogy.getPersons().get(i).isDirectAncestor()){
                        if (index == res){
                            textArea1.setText(Genealogy.genealogy.getPersons().get(i).printPerson());
                        }
                            index++;
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

    private void initComboBox() {
        Genealogy.genealogy.sortPersons();
        Town.sortTowns();
        for (int i = 0 ; i  < Genealogy.genealogy.getPersons().size() ; i++){
            ancestors.addItem(Genealogy.genealogy.getPersons().get(i).getFullNameInverted());
            if (Genealogy.genealogy.getPersons().get(i).isDirectAncestor()){
                directAncestors.addItem(Genealogy.genealogy.getPersons().get(i).getFullNameInverted());
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
                for (int i = 0 ; i < Town.getTowns().size() ; i++){
                    System.out.println(Town.getTowns().get(i) + " " + Town.getTowns().get(i).printCoordinates());
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

    public static void main(String[] args) throws IOException {
        /*MyGedcomReader myGedcomReader = new MyGedcomReader();
        String path = "C:\\Users\\Dan\\Desktop\\Programmation\\IntelliJ\\Genealogy\\Genealogy\\src\\main\\resources\\famille1.ged";

        Genealogy.genealogy = myGedcomReader.read(path);
        Genealogy.genealogy.parseContents();
        MainScreen mainScreen = new MainScreen("Ma Généalogie");*/
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
