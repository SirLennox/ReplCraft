package dev.sirlennox.replcraftclient.api.vector;

import com.eclipsesource.json.JsonObject;

public class IntVector extends Vector<Integer> {
    public IntVector(final int x, final int y, final int z) {
        super(x, y, z);
    }

    @Override
    public void apply(final JsonObject json, final String xName, final String yName, final String zName) {
        json.add(xName, this.getX());
        json.add(yName, this.getY());
        json.add(zName, this.getZ());
    }

    public void apply(final JsonObject json) {
        this.apply(json, "x", "y", "z");
    }
}
