package Genealogy.GUI;

import Genealogy.MapViewer.MapFrame;
import Genealogy.MapViewer.Structures.FancyWaypointRenderer;
import Genealogy.MapViewer.Structures.MapPoint;
import Genealogy.MapViewer.Structures.MyCoordinate;
import Genealogy.MapViewer.Structures.MyWaypoint;
import Genealogy.Model.Act.Act;
import Genealogy.Model.Act.Birth;
import Genealogy.Model.Act.Enum.ActType;
import Genealogy.Model.Act.Union;
import Genealogy.Model.Exception.URLException;
import Genealogy.Model.Gedcom.Genealogy;
import Genealogy.Model.Gedcom.Person;
import Genealogy.Model.Gedcom.Town;
import Genealogy.URLConnexion.Geneanet.GeneanetTree;
import Genealogy.URLConnexion.Geneanet.TreeComparatorManager;
import Genealogy.URLConnexion.MyHttpUrlConnection;
import Genealogy.URLConnexion.Serializer;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdesktop.swingx.JXMapKit;
import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.WaypointPainter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static Genealogy.URLConnexion.Geneanet.TreeComparatorManager.getInstance;

/**
 * Created by Dan on 10/04/2016.
 */
public class MainScreen extends JFrame {
    public static final String myFolder = "C:\\Users\\Dan\\Desktop\\Programmation\\IntelliJ\\Genealogy\\Genealogy\\src\\main\\resources\\";
    public static final String myJarFolder = System.getProperty("user.dir") + File.separator + "Properties" + File.separator;
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
    private JPanel mapPanel;
    private JPanel panelForMap;
    private JButton retrouverTousLesPDFButton;
    private JButton voirButton;
    private JButton remplacerPDFButton;
    private JRadioButton naissanceRadioButton;
    private JRadioButton mariageRadioButton;
    private JRadioButton deathRadioButton;
    private JComboBox ActePDF;
    private JButton ajouterPDFButton;
    private JButton verifierLesPDFButton;
    private JButton comparerArbreButton;
    private JCheckBox rechercheGeneanetCheckBox;
    private JButton ajouterUnArbreButton;
    private JComboBox arbre;
    private JButton comparerTousLesArbresButton;
    private JXMapKit jXMapKit;
    private ArrayList<MapPoint> mapPoints;
    private MapFrame mapFrame;
    private static MainScreen INSTANCE;
    final static Logger logger = LogManager.getLogger(MainScreen.class);
    private MyHttpUrlConnection HTTPConnexion;

    public static MainScreen getINSTANCE() {
        return INSTANCE;
    }

    public JComboBox getArbre() {
        return arbre;
    }

    public MainScreen(String title) throws IOException {
        super(title);

        //initForm();
        initButtons();
        initComboBox();
        initTab1();
        initMissingCitiesTab();
        HTTPConnexion = MyHttpUrlConnection.getInstance();

        setPreferredSize(new Dimension(700, 500));
        pack();
        setLocationRelativeTo(null);

        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        INSTANCE = this;
    }

    private void initMap() {
        if (mapPoints == null) {
            mapPoints = new ArrayList<>();
        }
        GeoPosition initPosition = new GeoPosition(47.41022, 2.925037);
        int zoom = 13;
        ArrayList<MapPoint> mapPoints = new ArrayList<>();
        mapFrame = new MapFrame(mapPoints, initPosition, zoom);
        panelForMap = new JPanel();
        panelForMap.setLayout(new BorderLayout());
        panelForMap.add(mapFrame.getjXMapKit());
        mapPanel = new JPanel();
        mapPanel.setLayout(new FlowLayout());
        mapPanel.add(panelForMap);
        jXMapKit = mapFrame.getjXMapKit();
        GeoPosition initPosition2 = new GeoPosition(1, 1);
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
                if (persons != null) {
                    for (int i = 0; i < persons.size(); i++) {
                        if (persons.get(i).isDirectAncestor()) {
                            Person p = persons.get(i);
                            if ((p != null) && (index == res)) {
                                textArea1.setText(p.printPerson());
                            }
                            index++;
                        }
                    }
                }
            }
        });
        ArrayList<Person> persons = Genealogy.genealogy.getPersons();
        if (persons != null && persons.size() > 0) {
            Person p = persons.get(0);
            if (p != null) {
                textArea1.setText(p.printPerson());
            }
        }

        for (int i = 0; i < Genealogy.genealogy.getPersons().size(); i++) {
            Person person = Genealogy.genealogy.getPersons().get(i);
            if ((person != null) && (person.isPrintable())) {
                ancestors.addItem(person.getFullNameInverted());
                if (person.isDirectAncestor()) {
                    textArea2.setText(Genealogy.genealogy.getPersons().get(ancestors.getSelectedIndex()).printPerson());
                    break;
                }
            }
        }

        ancestors.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea2.setText(Genealogy.genealogy.getPersons().get(ancestors.getSelectedIndex()).printPerson());
            }
        });

        initPDFButtons();
    }

    /**
     * Fonction initPDFRadioButton
     * Initialise les radiobuttons de gestion de PDF
     */
    private void initPDFRadioButton() {
        naissanceRadioButton.setSelected(true);
        naissanceRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mariageRadioButton.setSelected(false);
                deathRadioButton.setSelected(false);
            }
        });
        mariageRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                naissanceRadioButton.setSelected(false);
                deathRadioButton.setSelected(false);
            }
        });
        deathRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mariageRadioButton.setSelected(false);
                naissanceRadioButton.setSelected(false);
            }
        });
    }

    protected static JFileChooser initJFileChooser() {
        Serializer serializer = Serializer.getInstance();
        String myFolder = "D:\\Genealogie\\Preuves\\";
        String myJarFolder = System.getProperty("user.dir") + File.separator + "Preuves" + File.separator;

        if (serializer.isJar()) {

            return new JFileChooser(myJarFolder);
        } else {
            return new JFileChooser(myFolder);
        }
    }

    /**
     * Fonction initPDFButtons
     * Initialise les boutons de gestion de PDF
     */
    private void initPDFButtons() {
        initPDFRadioButton();
        ajouterPDFButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //get file
                    String file = null;
                    JFileChooser c = initJFileChooser();
                    // Demonstrate "Open" dialog:
                    int rVal = c.showOpenDialog(MainScreen.this);
                    if (rVal == JFileChooser.APPROVE_OPTION) {
                        file = c.getSelectedFile().getName();
                    }
                    if (rVal == JFileChooser.CANCEL_OPTION) {
                    }
                    //handle file
                    Person person = Genealogy.genealogy.getPersons().get(ancestors.getSelectedIndex());
                    if (naissanceRadioButton.isSelected()) {
                        person.addProof(ActType.BIRTH, file);
                    } else if (mariageRadioButton.isSelected()) {
                        person.addProof(ActType.UNION, "", person.getProofUnionSize());
                        //TODO
                    } else if (deathRadioButton.isSelected()) {
                        person.addProof(ActType.DEATH, file);
                    }
                } catch (Exception e1) {
                    logger.error("Failed to initialize PDFButtons", e1);
                }
            }
        });
        voirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Person person = Genealogy.genealogy.getPersons().get(ancestors.getSelectedIndex());
                    if (naissanceRadioButton.isSelected()) {
                        String file = person.getBirth().getProofs().get(0);
                    } else if (mariageRadioButton.isSelected()) {
                        person.addProof(ActType.UNION, "", person.getProofUnionSize());
                        //TODO
                    } else if (deathRadioButton.isSelected()) {
                        String file = person.getDeath().getProofs().get(0);
                    }
                } catch (Exception e1) {
                    logger.error("Failed to initialize voirButton actions", e1);
                }
            }
        });
        retrouverTousLesPDFButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO
            }
        });
        remplacerPDFButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO
            }
        });
    }

    /**
     * Fonction initComboBox
     * Initialise les combobox ancestors, directAncestors et towns
     */
    private void initComboBox() {
        Town.sortTowns();
        for (int i = 0; i < Genealogy.genealogy.getPersons().size(); i++) {
            Person person = Genealogy.genealogy.getPersons().get(i);
            if ((person != null) && (person.isPrintable())) {
                ancestors.addItem(person.getFullNameInverted());
                if (person.isDirectAncestor()) {
                    directAncestors.addItem(Genealogy.genealogy.getPersons().get(i).getFullNameInverted());
                }
            }
        }
        for (int i = 0; i < Town.getTowns().size(); i++) {
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
                    if (!MyHttpUrlConnection.testInternetConnection()) {
                        throw new URLException("Impossible d'afficher la carte sans connexion internet");
                    }
                    MapScreen mapScreen = new MapScreen();
                    setVisible(false);
                } catch (Exception e1) {
                    logger.error("Failed to initialize carteButton actions", e1);
                    JOptionPane.showMessageDialog(mainPanel, e1.getMessage(),
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
                ArrayList<Act> list = Town.getMapTownAct().get(thisTown);
                ArrayList<String> birthList = new ArrayList<>();
                ArrayList<String> unionsList = new ArrayList<>();
                ArrayList<String> deathList = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) instanceof Birth) {
                        birthList.add(naissances.getText() + list.get(i).getPerson().getFullNameInverted() + "\n");
                    } else if (list.get(i) instanceof Union) {
                        unionsList.add(unions.getText() + list.get(i).getPerson().getFullNameInverted() + " \navec " +
                                ((Union) list.get(i)).getPartner().getFullNameInverted() + "\n");
                    } else {
                        deathList.add(deces.getText() + list.get(i).getPerson().getFullNameInverted() + "\n");
                    }
                }
                Collections.sort(birthList);
                Collections.sort(unionsList);
                Collections.sort(deathList);
                for (String s : birthList) {
                    naissances.setText(naissances.getText() + s);
                }
                for (String s : unionsList) {
                    unions.setText(unions.getText() + s);
                }
                for (String s : deathList) {
                    deces.setText(deces.getText() + s);
                }
            }
        });

        try {
            int index = 1;
            for (GeneanetTree geneanetTree : TreeComparatorManager.getInstance().getGeneanetBrowser().getGeneanetTreeManager().getGeneanetTrees()) {
                if (index >= 1) {
                    arbre.addItem(geneanetTree.getName());
                }
                index++;
            }
        } catch (Exception ex) {
            logger.error("Impossible d'initialiser la liste des arbres", ex);
        }

        ajouterUnArbreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    NewTreeScreen newTreeScreen = NewTreeScreen.getInstance();
                    newTreeScreen.setVisible(true);
                    setVisible(false);
                } catch (Exception e1) {
                    logger.error("Failed to add a tree", e1);
                    JOptionPane.showMessageDialog(mainPanel, e1.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        comparerArbreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String tree = arbre.getSelectedItem().toString();
                    TreeComparatorManager treeComparatorManager = getInstance();
                    treeComparatorManager.refreshGedcomData();
                    treeComparatorManager.searchOnGeneanet = rechercheGeneanetCheckBox.isSelected();
                    if (treeComparatorManager.compareTreeFromName(tree)) {
                        JOptionPane.showMessageDialog(mainPanel, "Comparaison arbre " + tree + " OK",
                                "Information",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        setVisible(false);
                    }
                } catch (Exception e1) {
                    logger.error("Failed to compare trees", e1);
                    JOptionPane.showMessageDialog(mainPanel, e1.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        comparerTousLesArbresButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    TreeComparatorManager treeComparatorManager = getInstance();
                    treeComparatorManager.refreshGedcomData();
                    treeComparatorManager.indexTree = 1;
                    treeComparatorManager.searchOnGeneanet = rechercheGeneanetCheckBox.isSelected();
                    if (treeComparatorManager.compareTreesWithScreen()) {
                        JOptionPane.showMessageDialog(mainPanel, "Comparaison de tous les arbres OK",
                                "Information",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception e1) {
                    logger.error("Failed to compare all trees", e1);
                    JOptionPane.showMessageDialog(mainPanel, e1.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
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

    private void updateMissingCityTab(HashMap<String, String> townAssociation, String city) {
        if (!townAssociation.isEmpty()) {
            String newCity = townAssociation.get(city);
            TownQuery.setText(newCity);
            MyCoordinate coordinate = Town.findCoordinateFromTowns(newCity);
            if (coordinate == null) {
                coordinate = Town.findCoordinateFromTowns(city);
            }
            if (coordinate != null) {
                GeoPosition geoPosition = new GeoPosition(coordinate.getLatitude(), coordinate.getLongitude());
                jXMapKit.setCenterPosition(geoPosition);
                ArrayList<MapPoint> list = new ArrayList<>();
                list.add(new MapPoint(coordinate, city));
                removeMarkers();
                addMarkers(list);
            } else {
                JOptionPane.showMessageDialog(tabbedPane1,
                        "Impossible de trouver la coordonnée dans le fichier",
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE);
                TownQuery.setBackground(Color.ORANGE);
            }
        }
    }

    private void updateMissingCityTab(HashMap<String, String> townAssociation, String city, MyCoordinate coordinates) {
        if (!townAssociation.isEmpty()) {
            //View
            TownQuery.setText(townAssociation.get(city));
            GeoPosition geoPosition = new GeoPosition(coordinates.getLatitude(), coordinates.getLongitude());
            jXMapKit.setCenterPosition(geoPosition);
            ArrayList<MapPoint> list = new ArrayList<>();
            list.add(new MapPoint(coordinates, city));
            removeMarkers();
            addMarkers(list);
        }
    }

    private void updateMissingCitiesColor() {
        NotFoundPlaces.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                if (c instanceof JLabel) {
                    JLabel l = (JLabel) c;
                    if (Town.getLostTowns().contains(value)) {
                        l.setBackground(Color.YELLOW);
                        if (isSelected) {
                            l.setBackground(Color.GRAY);
                            l.setForeground(Color.RED);
                        }
                    }
                    return l;
                }
                return c;
            }
        });
        if (Town.getLostTowns() != null && !Town.getLostTowns().isEmpty()) {
            TownQuery.setBackground(Color.RED);
        }
    }

    private void initMissingCitiesTab() {
        final HashMap<String, String> townAssociation = Serializer.getTownAssociationMap();
        updateMissingCitiesColor();
        if (townAssociation != null) {
            for (Map.Entry<String, String> association : townAssociation.entrySet()) {
                String key = association.getKey();
                NotFoundPlaces.addItem(key);
                if (Town.getLostTowns().contains(key)) {
                    NotFoundPlaces.setSelectedItem(key);
                }
            }
            NotFoundPlaces.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String town = NotFoundPlaces.getSelectedItem().toString();
                    TownQuery.setText(townAssociation.get(town));
                    updateMissingCityTab(townAssociation, town);
                }
            });
            if (NotFoundPlaces.getSelectedItem() != null) {
                updateMissingCityTab(townAssociation, NotFoundPlaces.getSelectedItem().toString());
            }
        }
        TownQuery.setEditable(false);
        remplaceVilleNonTrouvee();
    }

    public void addMarkers(ArrayList<MapPoint> mapPoints) {
        Set<MyWaypoint> waypoints = mapFrame.addCities(mapPoints);
        WaypointPainter<MyWaypoint> waypointPainter = new WaypointPainter<MyWaypoint>();
        waypointPainter.setWaypoints(waypoints);
        waypointPainter.setRenderer(new FancyWaypointRenderer());
        JXMapViewer map = jXMapKit.getMainMap();
        map.setOverlayPainter(waypointPainter);
    }

    public void removeMarkers() {
        Set<MyWaypoint> waypoints = new HashSet<>();
        WaypointPainter<MyWaypoint> waypointPainter = new WaypointPainter<MyWaypoint>();
        waypointPainter.setWaypoints(waypoints);
        waypointPainter.setRenderer(new FancyWaypointRenderer());
        JXMapViewer map = jXMapKit.getMainMap();
        map.setOverlayPainter(waypointPainter);
        jXMapKit.getMainMap().removeAll();
    }

    private void remplaceVille() {
        String search = searchField.getText();
        if (search != null && (!search.equals("") && (search.contains(" ")))) {
            try {
                Pair<String, String> searchPair = Town.readTown(search);
                String city = searchPair.getKey();
                String county = searchPair.getValue();
                String fullCity = city + " (" + county + ")";
                MyCoordinate result = Town.parseJsonArray(HTTPConnexion.sendGpsRequest(city, county, false));
                logger.info("Coordonnées de la ville " + fullCity + " : " + result);
                if (result != null) {
                    HashMap<String, String> townAssociation = Serializer.getTownAssociationMap();
                    updateMissingCityTab(townAssociation, fullCity, result);
                    TownQuery.setText(fullCity);
                    TownQuery.setBackground(UIManager.getColor("Panel.background"));
                    String key = (String) NotFoundPlaces.getSelectedItem();
                    townAssociation.remove(key);
                    townAssociation.put(key, search);
                    Town.getLostTowns().remove(key);
                    Serializer.getInstance().saveTownAssociation(townAssociation);
                    Serializer.getInstance().saveSerializedTownList();
                }
                JOptionPane.showMessageDialog(tabbedPane1,
                        "Mise à jour d'alias de ville effectuée avec succès",
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e1) {
                logger.error("Erreur lors de la recherche de ville", e1);
                JOptionPane.showMessageDialog(tabbedPane1,
                        "Erreur lors de la recherche de ville",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(tabbedPane1,
                    "Impossible d'effectuer la recherche : le format de recherche est \"ville département\"",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void remplaceVilleNonTrouvee() {
        remplacerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remplaceVille();
            }
        });
    }

    private void createUIComponents() {
        initMap();
    }
}
