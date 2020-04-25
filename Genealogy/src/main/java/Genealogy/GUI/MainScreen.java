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
import Genealogy.URLConnexion.MyHttpUrlConnection;
import Genealogy.URLConnexion.Serializer;
import edu.emory.mathcs.backport.java.util.Collections;
import org.apache.commons.lang3.StringUtils;
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

/**
 * Created by Dan on 10/04/2016.
 */
public class MainScreen extends JFrame {
    public static final String myFolder = "C:\\Users\\Dan\\Desktop\\Programmation\\IntelliJ\\Genealogy\\Genealogy\\src\\main\\resources\\";
    public static final String myJarFolder = System.getProperty("user.dir") + File.separator + "Properties" + File.separator;
    private JTabbedPane tabbedPane1;
    private JButton retourButton = new JButton();
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
    private JXMapKit jXMapKit;
    private ArrayList<MapPoint> mapPoints;
    private MapFrame mapFrame;
    private static MainScreen INSTANCE;
    final static Logger logger = LogManager.getLogger(MainScreen.class);
    private MyHttpUrlConnection HTTPConnexion;

    public static MainScreen getINSTANCE() {
        return INSTANCE;
    }

    public MainScreen(String title) throws IOException {
        super(title);

        //initForm();
        initButtons();
        initComboBox();
        initTab1();
        initMissingCitiesTab();
        HTTPConnexion = new MyHttpUrlConnection();

        setPreferredSize(new Dimension(700, 500));
        pack();
        setLocationRelativeTo(null);

        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        INSTANCE = this;
    }

    private void initForm() {
        tabbedPane1 = new JTabbedPane();
        retourButton = new JButton();
        mainPanel = new JPanel();
        directAncestors = new JComboBox();
        ancestors = new JComboBox();
        towns = new JComboBox();
        textArea1 = new JTextArea();
        textArea2 = new JTextArea();
        naissances = new JTextArea();
        unions = new JTextArea();
        deces = new JTextArea();
        printPanel = new JPanel();
        carteButton = new JButton();
        NotFoundPlaces = new JComboBox();
        TownQuery = new JTextField();
        searchField = new JTextField();
        remplacerButton = new JButton();
        rechercherButton = new JButton();
        mapPanel = new JPanel();
        panelForMap = new JPanel();
        retrouverTousLesPDFButton = new JButton();
        voirButton = new JButton();
        remplacerPDFButton = new JButton();
        naissanceRadioButton = new JRadioButton();
        mariageRadioButton = new JRadioButton();
        deathRadioButton = new JRadioButton();
        ActePDF = new JComboBox();
        ajouterPDFButton = new JButton();
        verifierLesPDFButton = new JButton();
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
        Serializer<Town> serializer = new Serializer<Town>(Town.class);
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
                    e1.printStackTrace();
                }
            }
        });
        remplacerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO
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
                    e1.printStackTrace();
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
                    e1.printStackTrace();
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
                ArrayList<String> birthList = new ArrayList<String>();
                ArrayList<String> unionsList = new ArrayList<String>();
                ArrayList<String> deathList = new ArrayList<String>();
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

    //TODO
    private void updateMissingCitiesColor() {
        NotFoundPlaces.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                if (c instanceof JLabel) {
                    JLabel l = (JLabel) c;
                    ArrayList<String> notFoundCitiesList = new ArrayList<>();
                    notFoundCitiesList.add("Mestry (Calvados)");
                    if (notFoundCitiesList.contains(value)) {
                        l.setBackground(Color.YELLOW);
                        if (isSelected) {
                            list.setSelectionForeground(Color.RED);
                            list.setSelectionBackground(Color.BLUE);
                        }
                    }
                    return l;
                }
                return c;
            }
        });
    }

    private void initMissingCitiesTab() {
        final HashMap<String, String> townAssociation = Town.getTownAssociation();
        if (townAssociation != null) {
            for (Map.Entry<String, String> association : townAssociation.entrySet()) {
                NotFoundPlaces.addItem(association.getKey());
            }
            updateMissingCitiesColor();
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
        rechercherVilleNonTrouvee();
        remplacerAction();
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

    private void remplacerAction() {
        remplacerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String key = (String) NotFoundPlaces.getSelectedItem();
                    String value = searchField.getText();
                    //MyCoordinate result = Town.parseJsonArray(HTTPConnexion.sendAddressRequest(value),value);
                    MyCoordinate result = null;//TODO
                    Town.setCoordinates(result, key);
                    HashMap<String, String> townAssociation = Town.getTownAssociation();
                    townAssociation.remove(key);
                    townAssociation.put(key, value);
                    Serializer.getInstance().saveTownAssociation(townAssociation);
                    Serializer.getInstance().saveTownSerialized(Town.getTowns());
                    String city = NotFoundPlaces.getSelectedItem().toString();
                    updateMissingCityTab(townAssociation, city);
                    JOptionPane.showMessageDialog(tabbedPane1,
                            "Mise à jour d'alias de ville effectuée avec succès",
                            "Information",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private void rechercheVille(boolean remplace) {
        String search = searchField.getText();
        if (search != null && (!search.equals("") && (search.contains(" ")))) {
            try {
                String[] tmpSplit = search.split(" ");
                String concat = "";
                for (int i = 0; i < tmpSplit.length - 1; i++) {
                    concat += tmpSplit[i] + " ";
                }
                String city = StringUtils.strip(concat);
                String county = tmpSplit[tmpSplit.length - 1];
                String fullCity = city + " (" + county + ")";
                MyCoordinate result = Town.parseJsonArray(HTTPConnexion.sendGpsRequest(city, county, false));
                logger.info("Coordonnées de la ville " + fullCity + " : " + result);
                if (result != null) {
                    HashMap<String, String> townAssociation = Town.getTownAssociation();
                    updateMissingCityTab(townAssociation, fullCity, result);
                }
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

    private void rechercherVilleNonTrouvee() {
        rechercherButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rechercheVille(false);
            }
        });
    }

    private void createUIComponents() {
        initMap();
    }
}
