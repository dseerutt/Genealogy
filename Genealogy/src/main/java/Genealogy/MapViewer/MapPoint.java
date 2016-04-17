package Genealogy.MapViewer;

import Genealogy.MyCoordinate;

import java.awt.*;

/**
 * Created by Dan on 17/04/2016.
 */
public class MapPoint {
    private String tooltip;
    private MyCoordinate myCoordinate;
    private int nbPeople;
    private Color color;

    public MapPoint(String tooltip, MyCoordinate myCoordinate, int nbPeople, Color color) {
        this.tooltip = tooltip;
        this.myCoordinate = myCoordinate;
        this.nbPeople = nbPeople;
        this.color = color;
    }

    public String getTooltip() {
        return tooltip;
    }

    public MyCoordinate getMyCoordinate() {
        return myCoordinate;
    }

    public int getNbPeople() {
        return nbPeople;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "MapPoint{" +
                "tooltip='" + tooltip + '\'' +
                ", myCoordinate=" + myCoordinate +
                ", nbPeople=" + nbPeople +
                ", color=" + color +
                '}';
    }
}
