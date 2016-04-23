package Genealogy.MapViewer;

/**
 * Created by Dan on 17/04/2016.
 */

import Genealogy.MyCoordinate;
import org.jdesktop.swingx.JXMapKit;
import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.OSMTileFactoryInfo;
import org.jdesktop.swingx.mapviewer.DefaultTileFactory;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;
import org.jdesktop.swingx.mapviewer.WaypointPainter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.*;

/**
 * A simple sample application that uses JXMapKit
 * Based on Martin Steiger tutorials
 */
public class MapFrame
{
    public JXMapKit jXMapKit;

    public MapFrame(ArrayList<MapPoint> mapPoints, GeoPosition finalPosition, int zoom) {
        initMap();
        init(mapPoints,finalPosition,zoom);
    }

    public JXMapKit getjXMapKit() {
        return jXMapKit;
    }
/*
    public static void main(String[] args)
    {
        ArrayList<MapPoint> mapPoints = new ArrayList<>();
        mapPoints.add(new MapPoint("City1",new MyCoordinate(50.1109221, 8.6821267),1,Color.YELLOW));
        mapPoints.add(new MapPoint("City2",new MyCoordinate(50, 8.6821267),1,Color.YELLOW));
        mapPoints.add(new MapPoint("City3",new MyCoordinate(50.1109221, 8),1,Color.YELLOW));
        mapPoints.add(new MapPoint("City4",new MyCoordinate(50.1109221, 9),1,Color.YELLOW));
        mapPoints.add(new MapPoint("City5",new MyCoordinate(51, 8.6821267),1,Color.YELLOW));
        MyCoordinate myCoordinate = new MyCoordinate(10,10);
        GeoPosition finalPosition = new GeoPosition(myCoordinate.getLattitude(),myCoordinate.getLongitude());

        MapFrame mapFrame = new MapFrame(mapPoints,finalPosition);
        mapFrame.finishFrame();
    }
*/
    public void initMap(){
        jXMapKit = new JXMapKit();
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        jXMapKit.setTileFactory(tileFactory);
    }

    public Set<MyWaypoint> addCities(ArrayList<MapPoint> list2){
        Set<MyWaypoint> waypoints = new HashSet<MyWaypoint>();
        for (int i = 0 ; i < list2.size() ; i++){
            MapPoint mapPoint = list2.get(i);
            MyCoordinate coo = mapPoint.getMyCoordinate();
            waypoints.add(new MyWaypoint("" + mapPoint.getNbPeople(), mapPoint.getColor(),
                    new GeoPosition(coo.getLattitude(),coo.getLongitude())));
            addTooltip(mapPoint.getTooltip(),new GeoPosition(coo.getLattitude(),coo.getLongitude()));
        }
        return waypoints;
    }

    public void init(ArrayList<MapPoint> list2, GeoPosition finalPosition, int zoom)
    {
        jXMapKit.setZoom(zoom);
        jXMapKit.setAddressLocation(finalPosition);

        Set<MyWaypoint> waypoints = addCities(list2);
        // Create a waypoint painter that takes all the waypoints
        WaypointPainter<MyWaypoint> waypointPainter = new WaypointPainter<MyWaypoint>();
        waypointPainter.setWaypoints(waypoints);
        waypointPainter.setRenderer(new FancyWaypointRenderer());
        JXMapViewer map = jXMapKit.getMainMap();
        map.setOverlayPainter(waypointPainter);
        map.setCenterPosition(finalPosition);
    }

    public void finishFrame(){
        // Display the viewer in a JFrame
        JFrame frame = new JFrame("Example MapViewer");
        frame.getContentPane().add(jXMapKit);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }



    public void addTooltip(String text, final GeoPosition geoPosition){
        final JToolTip tooltip = new JToolTip();
        tooltip.setTipText(text);
        tooltip.setComponent(jXMapKit.getMainMap());
        jXMapKit.getMainMap().add(tooltip);

        jXMapKit.getMainMap().addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // ignore
            }

            @Override
            public void mouseMoved(MouseEvent e)
            {
                JXMapViewer map = jXMapKit.getMainMap();
                // convert to world bitmap
                Point2D worldPos = map.getTileFactory().geoToPixel(geoPosition, map.getZoom());

                // convert to screen
                Rectangle rect = map.getViewportBounds();
                int sx = (int) worldPos.getX() - rect.x;
                int sy = (int) worldPos.getY() - rect.y;
                Point screenPos = new Point(sx, sy);

                // check if near the mouse
                if (screenPos.distance(e.getPoint()) < 20)
                {
                    screenPos.x -= tooltip.getWidth() / 2;

                    tooltip.setLocation(screenPos);
                    tooltip.setVisible(true);
                }
                else
                {
                    tooltip.setVisible(false);
                }
            }
        });
    }
}