package dev.sirlennox.replcraftclient.api.vector;

public class WorldLocation extends Location {

    private final String world;

    public WorldLocation(final String world, final int x, final int y, final int z) {
        super(x, y, z);
        this.world = world;
    }

    public final String getWorld() {
        return this.world;
    }
}
