package Genealogy.MapViewer.Structures;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MyCoordinate)) return false;

        MyCoordinate that = (MyCoordinate) o;

        if (Double.compare(that.getLattitude(), getLattitude()) != 0) return false;
        return Double.compare(that.getLongitude(), getLongitude()) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(getLattitude());
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getLongitude());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public double getLattitude() {
        return lattitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
