package dev.sirlennox.replcraftclient.api.vector;

import com.eclipsesource.json.JsonObject;

public class Location extends Vector<Double> {

    public Location(final double x, final double y, final double z) {
        super(x, y, z);
    }

    public void apply(final JsonObject json, final String xName, final String yName, final String zName) {
        json.add(xName, this.getX());
        json.add(yName, this.getY());
        json.add(zName, this.getZ());
    }

    public void apply(final JsonObject json) {
        this.apply(json, "x", "y", "z");
    }
}
