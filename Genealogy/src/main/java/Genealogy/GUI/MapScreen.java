package Genealogy.GUI;

import Genealogy.Genealogy;
import Genealogy.MapViewer.FancyWaypointRenderer;
import Genealogy.MapViewer.MapFrame;
import Genealogy.MapViewer.MapPoint;
import Genealogy.MapViewer.MyWaypoint;
import Genealogy.Model.Act.Act;
import Genealogy.Model.Act.Union;
import Genealogy.Model.Date.ActStructure;
import Genealogy.Model.MapStructure;
import Genealogy.Model.Person;
import Genealogy.Model.Town;
import Genealogy.MyCoordinate;
import Genealogy.AuxMethods;
import org.jdesktop.swingx.JXMapKit;
import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.WaypointPainter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * Created by Dan on 17/04/2016.
 */
public class MapScreen extends JFrame{
    private JButton lancerButton;
    private JRadioButton generationAutomatiqueRadioButton;
    private JRadioButton generationALaDateRadioButton;
    private JRadioButton generationEntreLaDateRadioButton;
    private JComboBox comboDate;
    private JComboBox comboDate1;
    private JComboBox comboDate2;
    private JPanel mapPanel;
    private JButton retourButton;
    private JPanel panelForMap;
    private JRadioButton actesDeDecesRadioButton;
    private JRadioButton actesDeMariageRadioButton;
    private JRadioButton actesDeNaissanceRadioButton;
    private JRadioButton tousLesActesRadioButton;
    private JRadioButton tousLesAncetresRadioButton;
    private JRadioButton toutesLesPersonnesRadioButton;
    private JXMapKit jXMapKit;
    private ArrayList<MapPoint> mapPoints;
    private MapFrame mapFrame;
    private GeoPosition initPosition = new GeoPosition(47.41022,2.925037);
    private int zoom = 11;
    private boolean allPeople = false;

    public MapScreen(){
        super("Répartition territoriale dans le temps");

        initRadioButtons();
        initComboBox();
        initButtons();
        initListeners();

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        pack();
        setLocationRelativeTo(null);

        setContentPane(mapPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panelForMap.setPreferredSize(new Dimension(5000,5000));
        setVisible(true);
    }

    private void initListeners() {
        tousLesAncetresRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tousLesAncetresRadioButton.isSelected()){
                    allPeople = false;
                } else {
                    allPeople = true;
                }
            }
        });

        toutesLesPersonnesRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (toutesLesPersonnesRadioButton.isSelected()){
                    allPeople = true;
                } else {
                    allPeople = true;
                }
            }
        });
    }

    private void initMap() {
        if (mapPoints == null){
            mapPoints = new ArrayList<>();
        }
        initPosition = new GeoPosition(47.41022,2.925037);
        zoom = 13;
        ArrayList<MapPoint> mapPoints = new ArrayList<>();
        mapFrame = new MapFrame(mapPoints,initPosition,zoom);
        panelForMap = new JPanel();
        panelForMap.setLayout(new BorderLayout());
        panelForMap.add(mapFrame.getjXMapKit());
        mapPanel = new JPanel();
        mapPanel.setLayout(new FlowLayout());
        mapPanel.add(panelForMap);
        jXMapKit = mapFrame.getjXMapKit();
    }

    private void initComboBox() {
        for (int i = Act.getMinimumYear(); i < 2017 ; i++){
            comboDate.addItem(i);
            comboDate1.addItem(i);
            comboDate2.addItem(i);
        }
    }

    private void initRadioButtons() {
        ButtonGroup button = new ButtonGroup();
        button.add(generationALaDateRadioButton);
        button.add(generationAutomatiqueRadioButton);
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
    }

    public void removeTooltip(){
        jXMapKit.getMainMap().removeAll();
    }

    public void addMarkers(ArrayList<MapPoint> mapPoints){
        Set<MyWaypoint> waypoints =  mapFrame.addCities(mapPoints);
        WaypointPainter<MyWaypoint> waypointPainter = new WaypointPainter<MyWaypoint>();
        waypointPainter.setWaypoints(waypoints);
        waypointPainter.setRenderer(new FancyWaypointRenderer());
        JXMapViewer map = jXMapKit.getMainMap();
        map.setOverlayPainter(waypointPainter);
    }

    public void removeMarkers(){
        Set<MyWaypoint> waypoints =  new HashSet<>();
        WaypointPainter<MyWaypoint> waypointPainter = new WaypointPainter<MyWaypoint>();
        waypointPainter.setWaypoints(waypoints);
        waypointPainter.setRenderer(new FancyWaypointRenderer());
        JXMapViewer map = jXMapKit.getMainMap();
        map.setOverlayPainter(waypointPainter);
    }

    private ArrayList<MapPoint> getItAll(){
        HashMap<MyCoordinate,ActStructure> map = new HashMap<>();
        ArrayList<Person> persons = Genealogy.genealogy.getPersons();
        for (int i = 0; i < persons.size() ; i++){
            Person p = persons.get(i);
            if ((allPeople)||(p.isDirectAncestor())){
                if ((p.getBirth() != null)&&(p.getBirth().getDate() != null)&&(p.getBirth().getTown() != null)&&(p.getBirth().getTown().getName() != null)){
                    Town town = p.getBirth().getTown();
                    MyCoordinate coo = town.findCoordinate();
                    if (map.containsKey(coo)){
                        map.get(coo).addNom(p.getFullName());
                    } else {
                        map.put(coo,new ActStructure(p.getFullName(),p.getBirth().getTown().getName()));
                    }
                }
                if ((p.getDeath() != null)&&(p.getDeath().getDate() != null)&&(p.getDeath().getTown() != null)&&(p.getDeath().getTown().getName() != null)){
                    Town town = p.getDeath().getTown();
                    MyCoordinate coo = town.findCoordinate();
                    if (map.containsKey(coo)){
                        map.get(coo).addNom(p.getFullName());
                    } else {
                        map.put(coo,new ActStructure(p.getFullName(),p.getDeath().getTown().getName()));
                    }
                }
                for (int j = 0 ; j < p.getUnions().size() ; j++){
                    Union union = p.getUnions().get(j);
                    if ((union != null)&&(union.getDate() != null)&&(union.getTown() != null)&&(union.getTown().getName() != null)){
                        Town town = union.getTown();
                        if (p.isDirectAncestor()) {
                            MyCoordinate coo = town.findCoordinate();
                            if (map.containsKey(coo)){
                                map.get(coo).addNom(p.getFullName() + " - " + union.getPartner().getFullName());
                            } else {
                                map.put(coo,new ActStructure(p.getFullName() + " - " + union.getPartner().getFullName(),union.getTown().getName()));
                            }
                        }
                    }
                }
            }
        }

        return convertStructureToMapPoints(map);
    }

    private ArrayList<MapPoint> getBirth(){
        HashMap<MyCoordinate,ActStructure> map = new HashMap<>();
        ArrayList<Person> persons = Genealogy.genealogy.getPersons();
        for (int i = 0; i < persons.size() ; i++){
            Person p = persons.get(i);
            if ((allPeople)||(p.isDirectAncestor())){
                if ((p.getBirth() != null)&&(p.getBirth().getDate() != null)&&(p.getBirth().getTown() != null)&&(p.getBirth().getTown().getName() != null)){
                    Town town = p.getBirth().getTown();
                    MyCoordinate coo = town.findCoordinate();
                    if (map.containsKey(coo)){
                        map.get(coo).addNom(p.getFullName());
                    } else {
                        map.put(coo,new ActStructure(p.getFullName(),p.getBirth().getTown().getName()));
                    }
                }
            }
        }

        return convertStructureToMapPoints(map);
    }

    private ArrayList<MapPoint> convertStructureToMapPoints(HashMap<MyCoordinate,ActStructure> map){
        ArrayList<MapPoint> fullMapPoints = new ArrayList<>();
        for(Map.Entry<MyCoordinate, ActStructure> entry : map.entrySet()) {
            ActStructure actStructure = entry.getValue();
            MapPoint mapPoint = new MapPoint(actStructure.getTooltip(), entry.getKey(),
                    actStructure.getNombre(), AuxMethods.getColor2(actStructure.getNombre()), 0);
            fullMapPoints.add(mapPoint);
        }
        return fullMapPoints;
    }

    private ArrayList<MapPoint> getDeath(){
        HashMap<MyCoordinate,ActStructure> map = new HashMap<>();
        ArrayList<Person> persons = Genealogy.genealogy.getPersons();
        for (int i = 0; i < persons.size() ; i++){
            Person p = persons.get(i);
            if ((allPeople)||(p.isDirectAncestor())){
                if ((p.getDeath() != null)&&(p.getDeath().getDate() != null)&&(p.getDeath().getTown() != null)&&(p.getDeath().getTown().getName() != null)){
                    Town town = p.getDeath().getTown();
                    MyCoordinate coo = town.findCoordinate();
                    if (map.containsKey(coo)){
                        map.get(coo).addNom(p.getFullName());
                    } else {
                        map.put(coo,new ActStructure(p.getFullName(),p.getDeath().getTown().getName()));
                    }
                }
            }
        }
        return convertStructureToMapPoints(map);
    }

    private ArrayList<MapPoint> getUnions(){
        ArrayList<MapPoint> fullMapPoints = new ArrayList<>();
        HashMap<MyCoordinate,ActStructure> map = new HashMap<>();
        ArrayList<Person> persons = Genealogy.genealogy.getPersons();
        for (int i = 0; i < persons.size() ; i++){
            Person p = persons.get(i);
            if ((allPeople)||(p.isDirectAncestor())){
                for (int j = 0 ; j < p.getUnions().size() ; j++){
                    Union union = p.getUnions().get(j);
                    if ((union != null)&&(union.getDate() != null)&&(union.getTown() != null)&&(union.getTown().getName() != null)){
                        Town town = union.getTown();
                        if (p.isDirectAncestor()) {
                            MyCoordinate coo = town.findCoordinate();
                            if (map.containsKey(coo)){
                                map.get(coo).addNom(p.getFullName() + " - " + union.getPartner().getFullName());
                            } else {
                                map.put(coo,new ActStructure(p.getFullName() + " - " + union.getPartner().getFullName(),union.getTown().getName()));
                            }
                        }
                    }
                }
            }
        }
        return convertStructureToMapPoints(map);
    }

    public void setSituation(ArrayList<MapStructure> mapStructure){
        if ((mapStructure == null)||(mapStructure.isEmpty())){
            return;
        }
        ArrayList<MapPoint> mapPoints = convertMapPoints(mapStructure);
        addMarkers(mapPoints);
        //setMapParameter(initPosition,zoom);
    }

    public void initButtons(){
        lancerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeTooltip();
                removeMarkers();
                if (generationALaDateRadioButton.isSelected()){
                    int year = (int) comboDate.getSelectedItem();
                    ArrayList<MapStructure> mapStructure = null;
                    if (tousLesAncetresRadioButton.isSelected()){
                        mapStructure = Person.getPeriodsDirectAncestors().get(year);
                    } else {
                        mapStructure = Person.getPeriods().get(year);
                    }

                    setSituation(mapStructure);
                } else if (actesDeNaissanceRadioButton.isSelected()){
                    ArrayList<MapPoint> mapPoints = getBirth();
                    addMarkers(mapPoints);
                } else if (actesDeMariageRadioButton.isSelected()){
                    ArrayList<MapPoint> mapPoints = getUnions();
                    addMarkers(mapPoints);
                } else if (actesDeDecesRadioButton.isSelected()){
                    ArrayList<MapPoint> mapPoints = getDeath();
                    addMarkers(mapPoints);
                } else if (tousLesActesRadioButton.isSelected()){
                    ArrayList<MapPoint> mapPoints = getItAll();
                    addMarkers(mapPoints);
                } else if (generationEntreLaDateRadioButton.isSelected()){
                    int date1 = (int) comboDate1.getSelectedItem();
                    int date2 = (int) comboDate2.getSelectedItem();
                    if (date2 < date1){
                        comboDate1.setSelectedItem(date2);
                        comboDate2.setSelectedItem(date1);
                        int temp = date1;
                        date1 = date2;
                        date2 = temp;
                    }
                    for (int i = date1 ; i <= date2 ; i++){
                        ArrayList<MapStructure> mapStructure = Person.getPeriods().get(i);
                        setSituation(mapStructure);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                } else if (generationAutomatiqueRadioButton.isSelected()){
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
    }

    private static int getMapStructure(ArrayList<MapPoint> list, MyCoordinate coo){
        for (int i  = 0 ; i < list.size() ; i++){
            if (list.get(i).getMyCoordinate().equals(coo)){
                return i;
            }
        }
        return -1;
    }

    private ArrayList<MapPoint> convertMapPoints(ArrayList<MapStructure> mapStructure) {
        ArrayList<MapPoint> mapPoints = new ArrayList<>();
        //Town town, String name, int age
        for (int i = 0 ; i < mapStructure.size() ; i++){
            //String tooltip, MyCoordinate myCoordinate, int nbPeople, Color color
            MyCoordinate myCoordinate = mapStructure.get(i).getTown().findCoordinate();
            int index = getMapStructure(mapPoints,myCoordinate);
            MapStructure structure = mapStructure.get(i);
            if (index == -1){
                MapPoint mappoint = new MapPoint("<u><font size=\"5\"><b>" + mapStructure.get(i).getTown().getName()
                        + " :</b></font></u><br>" + structure.getName(),myCoordinate,
                        1, AuxMethods.getColor(structure.getAge()),structure.getAge());
                mapPoints.add(mappoint);
            } else {
                MapPoint mappoint = mapPoints.get(index);
                mappoint.setTooltip(mappoint.getTooltip() + "<br>" + structure.getName());
                mappoint.setNbPeople(mappoint.getNbPeople()+1);
                int age2 = mappoint.getMaxAge();
                if (age2 < structure.getAge()){
                    mappoint.setColor(AuxMethods.getColor(structure.getAge()));
                    mappoint.setMaxAge(structure.getAge());
                }
                mapPoints.remove(index);
                mapPoints.add(mappoint);
            }
        }
        for (int i = 0 ; i < mapPoints.size() ; i++){
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
    }

    public static void main(String[] args){
        System.out.println("Hello");
    }
}
