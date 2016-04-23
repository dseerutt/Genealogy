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
    private int maxAge;

    public MapPoint(String tooltip, MyCoordinate myCoordinate, int nbPeople, Color color, int maxAge) {
        this.tooltip = tooltip;
        this.myCoordinate = myCoordinate;
        this.nbPeople = nbPeople;
        this.color = color;
        this.maxAge = maxAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
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

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public void setMyCoordinate(MyCoordinate myCoordinate) {
        this.myCoordinate = myCoordinate;
    }

    public void setNbPeople(int nbPeople) {
        this.nbPeople = nbPeople;
    }

    public void setColor(Color color) {
        this.color = color;
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
