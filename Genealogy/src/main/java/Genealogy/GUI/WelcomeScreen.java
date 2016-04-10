package Genealogy.GUI;

import Genealogy.Genealogy;
import Genealogy.Main;
import Genealogy.Model.Person;
import Genealogy.MyGedcomReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by Dan on 10/04/2016.
 */
public class WelcomeScreen extends JFrame{
    private JButton selFichierButton;
    private JPanel welcomePanel;
    private JTextArea filePath;
    private JButton chargerFichierButton;

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
                JFileChooser c = new JFileChooser(Main.myFolder);
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
                } else {
                    try{
                        MyGedcomReader myGedcomReader = new MyGedcomReader();
                        Genealogy.genealogy = myGedcomReader.read(filePath.getText());
                        Genealogy.genealogy.parseContents();
                        System.out.println(Genealogy.genealogy.getPersons());
                    }
                    catch (Exception exception){
                        exception.printStackTrace();
                        JOptionPane.showMessageDialog(welcomePanel,
                                "Impossible d'importer le fichier\n" + exception,
                                "Erreur",
                                JOptionPane.ERROR_MESSAGE);
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
