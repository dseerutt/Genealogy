package Genealogy.GUI;

import Genealogy.URLConnexion.Geneanet.TreeComparator;
import Genealogy.URLConnexion.Geneanet.TreeComparatorManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TreeModificationScreen extends JFrame {
    private JPanel panel1;
    private JTextArea comparaisonText;
    private JButton rafraichirFichierGedcomButton;
    private JButton ajouterModificationButton;
    private JButton retourButton;
    private JPanel panel2;
    private static TreeModificationScreen instance;
    public TreeComparator treeComparator;
    final static Logger logger = LogManager.getLogger(TreeModificationScreen.class);


    public TreeModificationScreen() {
        super("Comparaison d'arbre");

        comparaisonText.setEditable(false);
        initButtons();

        setPreferredSize(new Dimension(700, 300));
        pack();
        setLocationRelativeTo(null);
        setContentPane(panel1);
        setVisible(true);
    }

    public static TreeModificationScreen getInstance() {
        if (instance == null) {
            instance = new TreeModificationScreen();
        }
        return instance;
    }

    public void initButtons() {
        retourButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        rafraichirFichierGedcomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (TreeComparatorManager.getInstance().compareTreeOnce(treeComparator.url)) {
                        JOptionPane.showMessageDialog(panel1, "Comparaison arbre " + treeComparator.getTreeName() + " OK",
                                "Erreur",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception e1) {
                    logger.error("Failed to compare all trees", e1);
                    JOptionPane.showMessageDialog(panel1, e1.getMessage(),
                            "Information",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        ajouterModificationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    treeComparator.writeModification();
                    if (TreeComparatorManager.getInstance().compareTreeOnce(treeComparator.url)) {
                        JOptionPane.showMessageDialog(panel1, "Comparaison arbre " + treeComparator.getTreeName() + " OK",
                                "Erreur",
                                JOptionPane.INFORMATION_MESSAGE);
                        setVisible(false);
                    }
                } catch (Exception e1) {
                    logger.error("Failed to compare all trees", e1);
                    JOptionPane.showMessageDialog(panel1, e1.getMessage(),
                            "Information",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public JTextArea getComparaisonText() {
        return comparaisonText;
    }
}
