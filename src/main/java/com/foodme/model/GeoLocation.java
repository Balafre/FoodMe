package com.foodme.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**This class stores information about a location on Earth.  Locations are
specified using latitude and longitude.  The class includes a method for
computing the distance between two locations **/
@Embeddable
@Data
public class GeoLocation {
    public static final double RADIUS_MILES = 3963.1676;  // Earth radius in miles
    public static final double RADIUS_KM = 6371;  // Earth radius in km

    @Column(nullable = true)
    private double latitude;
    @Column(nullable = true)
    private double longitude;

    public GeoLocation() {}

    /**
     *  constructs a geo location object with given latitude and longitude
     */
    public GeoLocation(double theLatitude, double theLongitude) {
        latitude = theLatitude;
        longitude = theLongitude;
    }

    /** returns the distance in kilometers between this geo location and the given
    other geo location **/
    public double distanceFrom(GeoLocation other) {
        double lat1 = Math.toRadians(latitude);
        double long1 = Math.toRadians(longitude);
        double lat2 = Math.toRadians(other.latitude);
        double long2 = Math.toRadians(other.longitude);
        // apply the spherical law of cosines with a triangle composed of the
        // two locations and the north pole
        double theCos = Math.sin(lat1) * Math.sin(lat2) +
                Math.cos(lat1) * Math.cos(lat2) * Math.cos(long1 - long2);
        double arcLength = Math.acos(theCos);
        return arcLength * RADIUS_KM;
    }
}
