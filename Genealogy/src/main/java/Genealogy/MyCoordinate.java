package Genealogy;

import java.io.Serializable;

/**
 * Created by Dan on 16/04/2016.
 */
public class MyCoordinate implements Serializable{
    private double lattitude;
    private double longitude;

    public MyCoordinate(double lattitude, double longitude) {
        this.lattitude = lattitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "MyCoordinate{" +
                "lattitude=" + lattitude +
                ", longitude=" + longitude +
                '}';
    }

    public double getLattitude() {
        return lattitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
