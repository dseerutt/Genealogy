package Genealogy.MapViewer;

import Genealogy.GUI.MapScreen;
import Genealogy.MapViewer.Structures.MapStructure;
import Genealogy.Model.Person;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dan on 26/04/2016.
 */
public class Worker extends SwingWorker<String, Object> {
    private MapScreen mapScreen;
    private int year1;
    private int year2;
    private boolean directAncestors = true;

    @Override
    protected String doInBackground() throws Exception {
        for (int i = year1 ; i <= year2 ; i++){
            publish(i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                //e.printStackTrace();
            }
        }
        return null;
    }

    protected void process(List<Object> item) {
        //This updates the UI
        //textArea1.append(item + "\n");*
        mapScreen.removeTooltip();
        mapScreen.removeMarkers();
        int i = (int) item.get(0);
        ArrayList<MapStructure> mapStructure;
        if (directAncestors){
            mapStructure = Person.getPeriodsDirectAncestors().get(i);
        } else {
            mapStructure = Person.getPeriods().get(i);
        }
        mapScreen.setSituation(mapStructure);
        mapScreen.getComboDate1().setSelectedItem(i);
    }

    public void setMapScreen(MapScreen mapScreen) {
        this.mapScreen = mapScreen;
    }

    public void setYear1(int year){
        this.year1 = year;
    }

    public void setYear2(int year){
        this.year2 = year;
    }

    public void setDirectAncestors(boolean directAncestors) {
        this.directAncestors = directAncestors;
    }
}
