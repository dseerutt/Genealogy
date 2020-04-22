package Genealogy.GUI;

import Genealogy.MapViewer.MapFrame;
import Genealogy.MapViewer.Structures.*;
import Genealogy.MapViewer.Worker;
import Genealogy.Model.Act.Union;
import Genealogy.Model.GUI.ActStructure;
import Genealogy.Model.GUI.Governor;
import Genealogy.Model.GUI.GovernorContainer;
import Genealogy.Model.Gedcom.Genealogy;
import Genealogy.Model.Gedcom.Person;
import Genealogy.Model.Gedcom.Town;
import org.jdesktop.swingx.JXMapKit;
import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.WaypointPainter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.*;

/**
 * Created by Dan on 17/04/2016.
 */
public class MapScreen extends JFrame {
    private static MapScreen INSTANCE;
    private JButton lancerButton;
    private JRadioButton generationAutomatiqueRadioButton = new JRadioButton();
    private JRadioButton generationALaDateRadioButton = new JRadioButton();
    private JRadioButton generationEntreLaDateRadioButton = new JRadioButton();
    private JComboBox comboDate;
    private JComboBox comboDate1;
    private JComboBox comboDate2;
    private JButton retourButton;
    private JRadioButton actesDeDecesRadioButton = new JRadioButton();
    private JRadioButton actesDeMariageRadioButton = new JRadioButton();
    private JRadioButton actesDeNaissanceRadioButton = new JRadioButton();
    private JRadioButton tousLesActesRadioButton = new JRadioButton();
    private JRadioButton tousLesAncetresRadioButton = new JRadioButton();
    private JRadioButton toutesLesPersonnesRadioButton = new JRadioButton();
    private JButton stopButton;
    private JButton effacerMarqueursButton;
    private JTextField FrenchHistoryText;
    private JTextField MauritianHistoryText;
    private ImageIcon frenchGovernorPicture;
    private ImageIcon mauritianGovernorPicture;
    private JPanel mapPanel;
    private JPanel captionPanel;
    private JPanel panelForMap;
    private JPanel FrenchHistoryPanel;
    private JPanel MauritianHistoryPanel;
    private JXMapKit jXMapKit;
    private ArrayList<MapPoint> mapPoints;
    private MapFrame mapFrame;
    private GeoPosition initPosition = new GeoPosition(47.41022, 2.925037);
    private int zoom = 11;
    private boolean allPeople = false;
    private static final int maxDate = Calendar.getInstance().get(Calendar.YEAR);
    private static Worker currentWorker = null;
    private ImageIcon image1;
    private ImageIcon image2;
    private JLabel labelImage;
    private GovernorContainer frenchGovernorContainer;
    private GovernorContainer mauritianGovernorContainer;
    private JLabel labelImageFrenchGovernors;
    private JLabel labelImageMauritianGovernors;

    public MapScreen() throws Exception {
        super("Répartition territoriale dans le temps");

        //initForm();
        initRadioButtons();
        initComboBox();
        initButtons();
        initListeners();
        initGovernorsPanel();
        initGovernorsPictures();
        INSTANCE = this;

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        pack();
        setLocationRelativeTo(null);

        setContentPane(mapPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panelForMap.setPreferredSize(new Dimension(5000, 5000));
        //throw new Exception();
        setVisible(true);
    }

    private void initForm() {
        lancerButton = new JButton();
        generationAutomatiqueRadioButton = new JRadioButton();
        generationALaDateRadioButton = new JRadioButton();
        generationEntreLaDateRadioButton = new JRadioButton();
        comboDate = new JComboBox();
        comboDate1 = new JComboBox();
        comboDate2 = new JComboBox();
        retourButton = new JButton();
        actesDeDecesRadioButton = new JRadioButton();
        actesDeMariageRadioButton = new JRadioButton();
        actesDeNaissanceRadioButton = new JRadioButton();
        tousLesActesRadioButton = new JRadioButton();
        tousLesAncetresRadioButton = new JRadioButton();
        toutesLesPersonnesRadioButton = new JRadioButton();
        stopButton = new JButton();
        effacerMarqueursButton = new JButton();
        FrenchHistoryText = new JTextField();
        MauritianHistoryText = new JTextField();
        frenchGovernorPicture = new ImageIcon();
        mauritianGovernorPicture = new ImageIcon();
    }

    private void initGovernorsPanel() {
        frenchGovernorContainer = new GovernorContainer("FrenchGovernors");
        int minDate = (Integer) comboDate1.getItemAt(0);
        updateFrenchGovernors(minDate);
        mauritianGovernorContainer = new GovernorContainer("MauritianGovernors");
        updateMauritianGovernors(minDate);
    }


    public JComboBox getComboDate1() {
        return comboDate1;
    }

    private void initListeners() {
        tousLesAncetresRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tousLesAncetresRadioButton.isSelected()) {
                    allPeople = false;
                } else {
                    allPeople = true;
                }
            }
        });

        toutesLesPersonnesRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (toutesLesPersonnesRadioButton.isSelected()) {
                    allPeople = true;
                } else {
                    allPeople = true;
                }
            }
        });
    }

    private void initMap() {
        if (mapPoints == null) {
            mapPoints = new ArrayList<>();
        }
        initPosition = new GeoPosition(47.41022, 2.925037);
        zoom = 13;
        ArrayList<MapPoint> mapPoints = new ArrayList<>();
        mapFrame = new MapFrame(mapPoints, initPosition, zoom);
        panelForMap = new JPanel();
        panelForMap.setLayout(new BorderLayout());
        panelForMap.add(mapFrame.getjXMapKit());
        mapPanel = new JPanel();
        mapPanel.setLayout(new FlowLayout());
        mapPanel.add(panelForMap);
        jXMapKit = mapFrame.getjXMapKit();
    }

    private void initComboBox() {
        for (int i = Pinpoint.minimumYear; i < maxDate + 1; i++) {
            comboDate.addItem(i);
            comboDate1.addItem(i);
            comboDate2.addItem(i);
        }
        comboDate2.setSelectedItem(maxDate);
    }

    private void initRadioButtons() {
        ButtonGroup button = new ButtonGroup();
        button.add(generationAutomatiqueRadioButton);
        button.add(generationALaDateRadioButton);
        button.add(generationEntreLaDateRadioButton);

        button.add(actesDeNaissanceRadioButton);
        button.add(actesDeMariageRadioButton);
        button.add(actesDeDecesRadioButton);
        button.add(tousLesActesRadioButton);
        generationAutomatiqueRadioButton.doClick();

        ButtonGroup button2 = new ButtonGroup();
        button2.add(tousLesAncetresRadioButton);
        button2.add(toutesLesPersonnesRadioButton);
        tousLesAncetresRadioButton.doClick();

        tousLesActesRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tousLesActesRadioButton.isSelected()) {
                    changePicture(image1);
                }
            }
        });
        actesDeMariageRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (actesDeMariageRadioButton.isSelected()) {
                    changePicture(image1);
                }
            }
        });
        actesDeNaissanceRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (actesDeNaissanceRadioButton.isSelected()) {
                    changePicture(image1);
                }
            }
        });
        actesDeDecesRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (actesDeDecesRadioButton.isSelected()) {
                    changePicture(image1);
                }
            }
        });
        generationAutomatiqueRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (generationAutomatiqueRadioButton.isSelected()) {
                    changePicture(image2);
                }
            }
        });
        generationEntreLaDateRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (generationEntreLaDateRadioButton.isSelected()) {
                    changePicture(image2);
                }
            }
        });
        generationALaDateRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (generationALaDateRadioButton.isSelected()) {
                    changePicture(image2);
                }
            }
        });
    }

    public void removeTooltip() {
        jXMapKit.getMainMap().removeAll();
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
    }

    private ArrayList<MapPoint> getItAll() {
        HashMap<MyCoordinate, ActStructure> map = new HashMap<>();
        ArrayList<Person> persons = Genealogy.genealogy.getPersons();
        for (int i = 0; i < persons.size(); i++) {
            Person p = persons.get(i);
            if ((allPeople) || (p.isDirectAncestor())) {
                if ((p.getBirth() != null) && (p.getBirth().getDate() != null) && (p.getBirth().getTown() != null) && (p.getBirth().getTown().getName() != null)) {
                    Town town = p.getBirth().getTown();
                    MyCoordinate coo = town.findCoordinateFromTowns();
                    if (map.containsKey(coo)) {
                        map.get(coo).addNom(p.getFullName());
                    } else {
                        map.put(coo, new ActStructure(p.getFullName(), p.getBirth().getTown().getName()));
                    }
                }
                if ((p.getDeath() != null) && (p.getDeath().getDate() != null) && (p.getDeath().getTown() != null) && (p.getDeath().getTown().getName() != null)) {
                    Town town = p.getDeath().getTown();
                    MyCoordinate coo = town.findCoordinateFromTowns();
                    if (map.containsKey(coo)) {
                        map.get(coo).addNom(p.getFullName());
                    } else {
                        map.put(coo, new ActStructure(p.getFullName(), p.getDeath().getTown().getName()));
                    }
                }
                for (int j = 0; j < p.getUnions().size(); j++) {
                    Union union = p.getUnions().get(j);
                    if ((union != null) && (union.getDate() != null) && (union.getTown() != null) && (union.getTown().getName() != null)) {
                        Town town = union.getTown();
                        if (p.isDirectAncestor()) {
                            MyCoordinate coo = town.findCoordinateFromTowns();
                            if (map.containsKey(coo)) {
                                map.get(coo).addNom(p.getFullName() + " - " + union.getPartner().getFullName());
                            } else {
                                map.put(coo, new ActStructure(p.getFullName() + " - " + union.getPartner().getFullName(), union.getTown().getName()));
                            }
                        }
                    }
                }
            }
        }

        return convertStructureToMapPoints(map);
    }

    private ArrayList<MapPoint> getBirth() {
        HashMap<MyCoordinate, ActStructure> map = new HashMap<>();
        ArrayList<Person> persons = Genealogy.genealogy.getPersons();
        for (int i = 0; i < persons.size(); i++) {
            Person p = persons.get(i);
            if ((allPeople) || (p.isDirectAncestor())) {
                if ((p.getBirth() != null) && (p.getBirth().getDate() != null) && (p.getBirth().getTown() != null) && (p.getBirth().getTown().getName() != null)) {
                    Town town = p.getBirth().getTown();
                    MyCoordinate coo = town.findCoordinateFromTowns();
                    if (map.containsKey(coo)) {
                        map.get(coo).addNom(p.getFullName());
                    } else {
                        map.put(coo, new ActStructure(p.getFullName(), p.getBirth().getTown().getName()));
                    }
                }
            }
        }

        return convertStructureToMapPoints(map);
    }

    private ArrayList<MapPoint> convertStructureToMapPoints(HashMap<MyCoordinate, ActStructure> map) {
        ArrayList<MapPoint> fullMapPoints = new ArrayList<>();
        for (Map.Entry<MyCoordinate, ActStructure> entry : map.entrySet()) {
            ActStructure actStructure = entry.getValue();
            MapPoint mapPoint = new MapPoint(actStructure.getTooltip(), entry.getKey(),
                    actStructure.getNombre(), getColor2(actStructure.getNombre()), 0);
            fullMapPoints.add(mapPoint);
        }
        return fullMapPoints;
    }

    private ArrayList<MapPoint> getDeath() {
        HashMap<MyCoordinate, ActStructure> map = new HashMap<>();
        ArrayList<Person> persons = Genealogy.genealogy.getPersons();
        for (int i = 0; i < persons.size(); i++) {
            Person p = persons.get(i);
            if ((allPeople) || (p.isDirectAncestor())) {
                if ((p.getDeath() != null) && (p.getDeath().getDate() != null) && (p.getDeath().getTown() != null) && (p.getDeath().getTown().getName() != null)) {
                    Town town = p.getDeath().getTown();
                    MyCoordinate coo = town.findCoordinateFromTowns();
                    if (map.containsKey(coo)) {
                        map.get(coo).addNom(p.getFullName());
                    } else {
                        map.put(coo, new ActStructure(p.getFullName(), p.getDeath().getTown().getName()));
                    }
                }
            }
        }
        return convertStructureToMapPoints(map);
    }

    private ArrayList<MapPoint> getUnions() {
        ArrayList<MapPoint> fullMapPoints = new ArrayList<>();
        HashMap<MyCoordinate, ActStructure> map = new HashMap<>();
        ArrayList<Person> persons = Genealogy.genealogy.getPersons();
        for (int i = 0; i < persons.size(); i++) {
            Person p = persons.get(i);
            if ((allPeople) || (p.isDirectAncestor())) {
                for (int j = 0; j < p.getUnions().size(); j++) {
                    Union union = p.getUnions().get(j);
                    if ((union != null) && (union.getDate() != null) && (union.getTown() != null) && (union.getTown().getName() != null)) {
                        Town town = union.getTown();
                        if (p.isDirectAncestor()) {
                            MyCoordinate coo = town.findCoordinateFromTowns();
                            if (map.containsKey(coo)) {
                                map.get(coo).addNom(p.getFullName() + " - " + union.getPartner().getFullName());
                            } else {
                                map.put(coo, new ActStructure(p.getFullName() + " - " + union.getPartner().getFullName(), union.getTown().getName()));
                            }
                        }
                    }
                }
            }
        }
        return convertStructureToMapPoints(map);
    }

    public static Color getColor(int age) {
        if (age >= 100) {
            return new Color(16, 52, 166);
        } else if (age >= 90) {
            return new Color(16, 80, 166);
        } else if (age >= 80) {
            return new Color(21, 96, 189);
        } else if (age >= 70) {
            return new Color(49, 140, 231);
        } else if (age >= 60) {
            return new Color(10, 186, 181);
        } else if (age >= 55) {
            return new Color(9, 106, 9);
        } else if (age >= 50) {
            return new Color(20, 148, 20);
        } else if (age >= 45) {
            return new Color(0, 255, 0);
        } else if (age >= 40) {
            return new Color(1, 215, 88);
        } else if (age >= 35) {
            return new Color(135, 233, 144);
        } else if (age >= 30) {
            return new Color(205, 205, 13);
        } else if (age >= 20) {
            return new Color(255, 255, 0);
        } else if (age >= 10) {
            return new Color(239, 155, 15);
        } else if (age >= 05) {
            return new Color(231, 62, 1);
        } else if (age >= 0) {
            return new Color(255, 0, 0);
        } else {
            return Color.lightGray;
        }
    }

    public static Color getColor2(int age) {
        if (age >= 30) {
            return new Color(21, 96, 189);
        } else if (age >= 25) {
            return new Color(49, 140, 231);
        } else if (age >= 18) {
            return new Color(10, 186, 181);
        } else if (age >= 16) {
            return new Color(116, 208, 241);
        } else if (age >= 14) {
            return new Color(169, 234, 234);
        } else if (age >= 12) {
            return new Color(9, 106, 9);
        } else if (age >= 10) {
            return new Color(86, 130, 3);
        } else if (age >= 8) {
            return new Color(20, 148, 20);
        } else if (age >= 6) {
            return new Color(0, 255, 0);
        } else if (age >= 5) {
            return new Color(1, 215, 88);
        } else if (age >= 4) {
            return new Color(205, 205, 13);
        } else if (age >= 3) {
            return new Color(255, 255, 0);
        } else if (age >= 2) {
            return new Color(255, 203, 96);
        } else if (age >= 1) {
            return new Color(239, 155, 15);
        } else {
            return Color.lightGray;
        }
    }

    public void setSituation(ArrayList<Pinpoint> pinPoint) {
        if ((pinPoint == null) || (pinPoint.isEmpty())) {
            return;
        }
        ArrayList<MapPoint> mapPoints = convertMapPoints(pinPoint);
        addMarkers(mapPoints);
    }

    public void initButtons() {
        lancerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeTooltip();
                removeMarkers();
                if (generationALaDateRadioButton.isSelected()) {
                    int year = (int) comboDate.getSelectedItem();
                    ArrayList<Pinpoint> pinPoint;
                    updateFrenchGovernors(year);
                    updateMauritianGovernors(year);
                    if (tousLesAncetresRadioButton.isSelected()) {
                        pinPoint = Pinpoint.getPinpointsYearMapDirectAncestors().get(year);
                    } else {
                        pinPoint = Pinpoint.getPinpointsYearMap().get(year);
                    }
                    setSituation(pinPoint);
                } else if (actesDeNaissanceRadioButton.isSelected()) {
                    ArrayList<MapPoint> mapPoints = getBirth();
                    addMarkers(mapPoints);
                } else if (actesDeMariageRadioButton.isSelected()) {
                    ArrayList<MapPoint> mapPoints = getUnions();
                    addMarkers(mapPoints);
                } else if (actesDeDecesRadioButton.isSelected()) {
                    ArrayList<MapPoint> mapPoints = getDeath();
                    addMarkers(mapPoints);
                } else if (tousLesActesRadioButton.isSelected()) {
                    ArrayList<MapPoint> mapPoints = getItAll();
                    addMarkers(mapPoints);
                } else if (generationEntreLaDateRadioButton.isSelected()) {
                    int date1 = (int) comboDate1.getSelectedItem();
                    int date2 = (int) comboDate2.getSelectedItem();
                    if (date2 < date1) {
                        comboDate1.setSelectedItem(date2);
                        comboDate2.setSelectedItem(date1);
                        int temp = date1;
                        date1 = date2;
                        date2 = temp;
                    }
                    updateFrenchGovernors(date1);
                    updateMauritianGovernors(date1);
                    handleWorker(date1, date2);
                } else if (generationAutomatiqueRadioButton.isSelected()) {
                    int date1 = Pinpoint.minimumYear;
                    int date2 = maxDate;
                    comboDate2.setSelectedItem(maxDate);
                    updateFrenchGovernors(date1);
                    updateMauritianGovernors(date1);
                    handleWorker(date1, date2);
                    comboDate1.setSelectedItem(date1);
                }
            }
        });

        retourButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                MainScreen.getINSTANCE().setVisible(true);
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentWorker != null) {
                    currentWorker.cancel(true);
                }
                removeTooltip();
                removeMarkers();
                generationEntreLaDateRadioButton.setSelected(true);
            }
        });

        effacerMarqueursButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeTooltip();
                removeMarkers();
            }
        });
    }

    private void changePicture(ImageIcon image) {
        removeTooltip();
        removeMarkers();
        labelImage.setIcon(image);
    }

    private void updateFrenchGovernorPicture(String filename) {
        ImageIcon image = frenchGovernorContainer.getImage(filename);
        labelImageFrenchGovernors.setIcon(image);
    }

    private void updateMauritianGovernorPicture(String filename) {
        ImageIcon image = mauritianGovernorContainer.getImage(filename);
        labelImageMauritianGovernors.setIcon(image);
    }

    private void handleWorker(int date1, int date2) {
        if (currentWorker != null) {
            currentWorker.cancel(true);
            currentWorker = null;
            removeTooltip();
            removeMarkers();
        }
        Worker worker = new Worker();
        currentWorker = worker;
        if (toutesLesPersonnesRadioButton.isSelected()) {
            worker.setDirectAncestors(false);
        }
        worker.setMapScreen(INSTANCE);
        worker.setYear1(date1);
        worker.setYear2(date2);
        worker.execute();
    }

    public GovernorContainer getFrenchGovernorContainer() {
        return frenchGovernorContainer;
    }

    public void setFrenchGovernorContainer(GovernorContainer frenchGovernorContainer) {
        this.frenchGovernorContainer = frenchGovernorContainer;
    }

    public GovernorContainer getMauritianGovernorContainer() {
        return mauritianGovernorContainer;
    }

    public void setMauritianGovernorContainer(GovernorContainer mauritianGovernorContainer) {
        this.mauritianGovernorContainer = mauritianGovernorContainer;
    }

    public void updateMauritianGovernors(int date) {
        String mauritianGovernorName = mauritianGovernorContainer.getGovernor(date);
        MauritianHistoryText.setText(mauritianGovernorName);
        updateMauritianGovernorPicture(mauritianGovernorName);
    }

    public void updateFrenchGovernors(int date) {
        String frenchGovernorName = frenchGovernorContainer.getGovernor(date);
        FrenchHistoryText.setText(frenchGovernorName);
        updateFrenchGovernorPicture(frenchGovernorName);
    }

    private static int getMapStructure(ArrayList<MapPoint> list, MyCoordinate coo) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getMyCoordinate().equals(coo)) {
                return i;
            }
        }
        return -1;
    }

    private ArrayList<MapPoint> convertMapPoints(ArrayList<Pinpoint> pinPoint) {
        ArrayList<MapPoint> mapPoints = new ArrayList<>();
        //Town town, String name, int age
        for (int i = 0; i < pinPoint.size(); i++) {
            //String tooltip, MyCoordinate myCoordinate, int nbPeople, Color color
            MyCoordinate myCoordinate = pinPoint.get(i).getTown().findCoordinateFromTowns();
            int index = getMapStructure(mapPoints, myCoordinate);
            Pinpoint structure = pinPoint.get(i);
            if (index == -1) {
                MapPoint mappoint = new MapPoint("<u><font size=\"5\"><b>" + pinPoint.get(i).getTown().getName()
                        + " :</b></font></u><br>" + structure.getName(), myCoordinate,
                        1, getColor(structure.getAge()), structure.getAge());
                mapPoints.add(mappoint);
            } else {
                MapPoint mappoint = mapPoints.get(index);
                mappoint.setTooltip(mappoint.getTooltip() + "<br>" + structure.getName());
                mappoint.setNbPeople(mappoint.getNbPeople() + 1);
                int age2 = mappoint.getMaxAge();
                if (age2 < structure.getAge()) {
                    mappoint.setColor(getColor(structure.getAge()));
                    mappoint.setMaxAge(structure.getAge());
                }
                mapPoints.remove(index);
                mapPoints.add(mappoint);
            }
        }
        for (int i = 0; i < mapPoints.size(); i++) {
            MapPoint mapPoint = mapPoints.get(i);
            mapPoint.setTooltip("<html>" + mapPoint.getTooltip() + "</html>");
        }
        return mapPoints;
    }

    private void setMapParameter(GeoPosition initPosition, int zoom) {
        jXMapKit.setZoom(zoom);
        jXMapKit.setAddressLocation(initPosition);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        initMap();
        initCaptions();
        labelImage = new JLabel("", image2, JLabel.CENTER);
        captionPanel = new JPanel(new BorderLayout());
        captionPanel.add(labelImage, BorderLayout.CENTER);

        labelImageFrenchGovernors = new JLabel("", frenchGovernorPicture, JLabel.CENTER);
        FrenchHistoryPanel = new JPanel(new BorderLayout());
        FrenchHistoryPanel.add(labelImageFrenchGovernors, BorderLayout.CENTER);
        labelImageMauritianGovernors = new JLabel("", mauritianGovernorPicture, JLabel.CENTER);
        MauritianHistoryPanel = new JPanel(new BorderLayout());
        MauritianHistoryPanel.add(labelImageMauritianGovernors, BorderLayout.CENTER);
    }

    private void initCaptions() {
        URL resource1 = getClass().getResource("maximum.png");
        URL resource2 = getClass().getResource("actes.png");
        image2 = new ImageIcon(resource1);
        image1 = new ImageIcon(resource2);
    }

    private void initGovernorsPictures() throws Exception {
        initFrenchGovernorsPictures();
        initMauritianGovernorsPictures();
    }

    private void initFrenchGovernorsPictures() {
        String path = "FrenchGovernors/";
        String extension = ".jpg";
        for (Governor governor : frenchGovernorContainer.getGovernors()) {
            String name = governor.getName();
            URL resource = getClass().getResource(path + name + extension);
            ImageIcon image = new ImageIcon(resource);
            governor.addImage(image, 200, 200);
        }
        //mindate is periods mindate used by comboDate1
        int minDate = (int) comboDate1.getSelectedItem();
        String governor = frenchGovernorContainer.getMappingDate().get(minDate);
        updateFrenchGovernorPicture(governor);
    }

    private void initMauritianGovernorsPictures() throws Exception {
        String path = "MauritianGovernors/";
        String extension = ".jpg";
        for (Governor governor : mauritianGovernorContainer.getGovernors()) {
            String name = governor.getName();
            URL resource = getClass().getResource(path + name + extension);
            if (resource != null) {
                ImageIcon image = new ImageIcon(resource);
                governor.addImage(image, 200, 200);
            } else {
                throw new Exception("Impossible de retrouver la ressource " + path + name + extension);
            }
        }
        //mindate is periods mindate used by comboDate1
        int minDate = (int) comboDate1.getSelectedItem();
        String governor = mauritianGovernorContainer.getMappingDate().get(minDate);
        updateMauritianGovernorPicture(governor);
    }
}
