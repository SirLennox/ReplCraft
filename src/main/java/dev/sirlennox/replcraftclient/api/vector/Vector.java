package dev.sirlennox.replcraftclient.api.vector;

import com.eclipsesource.json.JsonObject;

public abstract class Vector<T extends Number> {

    private final T x;
    private final T y;
    private final T z;

    protected Vector(final T x, final T y, final T z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public abstract void apply(final JsonObject json, final String xName, final String yName, final String zName);


    public final T getX() {
        return this.x;
    }

    public final T getY() {
        return this.y;
    }

    public final T getZ() {
        return this.z;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Vector<?>))
            return super.equals(obj);

        final Vector<?> vector = (Vector<?>) obj;

        return super.equals(obj) || (vector.getX().equals(this.getX()) && vector.getY().equals(this.getY()) && vector.getZ().equals(this.getZ()));
    }
}
