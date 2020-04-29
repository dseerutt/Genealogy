package Genealogy.MapViewer;

import Genealogy.GUI.MapScreen;
import Genealogy.MapViewer.Structures.Pinpoint;

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
    protected String doInBackground() {
        for (int i = year1; i <= year2 + 1; i++) {
            publish(i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return null;
    }

    protected void process(List<Object> item) {
        //This updates the UI
        int i = (int) item.get(0);
        if (i == year2 + 1) {
            mapScreen.getComboDate1().setSelectedItem(year1);
            return;
        }
        mapScreen.removeTooltip();
        mapScreen.removeMarkers();
        ArrayList<Pinpoint> pinPoint;
        if (directAncestors) {
            pinPoint = Pinpoint.getPinpointsYearMapDirectAncestors().get(i);
        } else {
            pinPoint = Pinpoint.getPinpointsYearMap().get(i);
        }
        mapScreen.setSituation(pinPoint);
        mapScreen.getComboDate1().setSelectedItem(i);
        mapScreen.updateFrenchGovernors(i);
        mapScreen.updateMauritianGovernors(i);
    }

    public void setMapScreen(MapScreen mapScreen) {
        this.mapScreen = mapScreen;
    }

    public void setYear1(int year) {
        this.year1 = year;
    }

    public void setYear2(int year) {
        this.year2 = year;
    }

    public void setDirectAncestors(boolean directAncestors) {
        this.directAncestors = directAncestors;
    }
}
