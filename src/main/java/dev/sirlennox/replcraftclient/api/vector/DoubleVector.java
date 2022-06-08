package dev.sirlennox.replcraftclient.api.vector;

import com.eclipsesource.json.JsonObject;

public class DoubleVector {

    private final double x;
    private final double y;
    private final double z;

    public DoubleVector(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void apply(final JsonObject json, final String xName, final String yName, final String zName) {
        json.add(xName, this.x);
        json.add(yName, this.y);
        json.add(zName, this.z);
    }

    public void apply(final JsonObject json) {
        this.apply(json, "x", "y", "z");
    }

    public final double getX() {
        return this.x;
    }

    public final double getY() {
        return this.y;
    }

    public final double getZ() {
        return this.z;
    }
}
