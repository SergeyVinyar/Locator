package ru.vinyarsky.locator.db;

public final class DbAddress {

    private final long id;

    private final String representation;

    private final double latitude;

    private final double longitude;

    /**
     * Create an existed address
     */
    public DbAddress(long id, String representation, double latitude, double longitude) {
        this.id = id;
        this.representation = representation;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Create new address for saving to db
     * @param representation
     * @param latitude
     * @param longitude
     */
    public DbAddress(String representation, double latitude, double longitude) {
        this(0, representation, latitude, longitude);
    }

    /**
     * @return Address as a string
     */
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
