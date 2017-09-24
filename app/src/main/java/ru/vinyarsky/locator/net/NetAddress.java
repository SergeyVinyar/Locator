package ru.vinyarsky.locator.net;

public final class NetAddress {

    private String representation;

    private double latitude;

    private double longitude;

    public NetAddress(String representation, double latitude, double longitude) {
        this.representation = representation;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getRepresentation() {
        return this.representation;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
