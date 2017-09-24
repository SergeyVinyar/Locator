package ru.vinyarsky.locator.net;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class SputnikContract {

    @SerializedName("result")
    @Expose
    private Result result;

    public Result getResult() {
        return result;
    }
}

class Result {

    @SerializedName("address")
    @Expose
    private List<Address> address = null;

    public List<Address> getAddress() {
        return address;
    }
}

class Address {

    @SerializedName("features")
    @Expose
    private List<Feature> features = null;

    public List<Feature> getFeatures() {
        return features;
    }
}

class Feature {

    @SerializedName("properties")
    @Expose
    private Properties properties;

    @SerializedName("geometry")
    @Expose
    private Geometry geometry;

    public Properties getProperties() {
        return properties;
    }

    public Geometry getGeometry() {
        return geometry;
    }
}

class Properties {

    @SerializedName("display_name")
    @Expose
    private String displayName;

    public String getDisplayName() {
        return displayName;
    }
}

class Geometry {

    @SerializedName("geometries")
    @Expose
    private List<Geometry_> geometries = null;

    public List<Geometry_> getGeometries() {
        return geometries;
    }
}

class Geometry_ {

    @SerializedName("coordinates")
    @Expose
    private List<Double> coordinates = null;

    public List<Double> getCoordinates() {
        return coordinates;
    }
}

