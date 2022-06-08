package dev.sirlennox.replcraftclient.api.vector;

import com.eclipsesource.json.JsonObject;

public class IntVector {

    private final int x;
    private final int y;
    private final int z;

    public IntVector(final int x, final int y, final int z) {
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

    public final int getX() {
        return this.x;
    }

    public final int getY() {
        return this.y;
    }

    public final int getZ() {
        return this.z;
    }
}
