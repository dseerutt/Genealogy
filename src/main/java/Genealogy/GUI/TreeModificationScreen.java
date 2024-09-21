package Genealogy.GUI;

import Genealogy.URLConnexion.Geneanet.TreeComparator;
import Genealogy.URLConnexion.Geneanet.TreeComparatorManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

public class TreeModificationScreen extends JFrame {
    private JPanel panel1;
    private JButton rafraichirFichierGedcomButton;
    private JButton ajouterModificationButton;
    private JPanel panel2;
    private JButton lienGeneanetButton;
    private JTextArea addReplacementText;
    private JTextArea removeReplacementText;
    private JTextField comparedPerson;
    private JLabel treeLabel;
    private static TreeModificationScreen instance;
    public TreeComparator treeComparator;
    final static Logger logger = LogManager.getLogger(TreeModificationScreen.class);
    private final String GESTION_DES_DIFFERENCES = "Gestion des différences entre l'arbre %ARBRE% et le fichier Gedcom - %STATS%";


    public TreeModificationScreen() {
        super("Comparaison d'arbre Geneanet");

        initButtons();

        setPreferredSize(new Dimension(700, 300));
        pack();
        setLocationRelativeTo(null);
        setContentPane(panel1);
        setVisible(true);
    }

    public void initTreeLabel(String tree, String stats) {
        treeLabel.setText(GESTION_DES_DIFFERENCES.replace("%ARBRE%", tree).replace("%STATS%", stats));
        super.update(this.getGraphics());
    }

    public static TreeModificationScreen getInstance() {
        if (instance == null) {
            instance = new TreeModificationScreen();
        }
        return instance;
    }

    public JTextField getComparedPerson() {
        return comparedPerson;
    }

    public JTextArea getAddReplacementText() {
        return addReplacementText;
    }

    public JTextArea getRemoveReplacementText() {
        return removeReplacementText;
    }

    public void initButtons() {
        lienGeneanetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    try {
                        Desktop.getDesktop().browse(new URI(treeComparator.getPeopleUrlError()));
                    } catch (Exception ex) {
                        logger.error("Failed to open Geneanet link ", ex);
                        JOptionPane.showMessageDialog(panel1, ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    logger.error("Failed to open Geneanet link ");
                    JOptionPane.showMessageDialog(panel1, "Erreur d'ouverture du lien " + treeComparator.getPeopleUrlError(),
                            "Information",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        rafraichirFichierGedcomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (TreeComparatorManager.getInstance().compareTreeOnce(treeComparator.url, true)) {
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
                    if (TreeComparatorManager.getInstance().compareTreeOnce(treeComparator.url, true)) {
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

}
