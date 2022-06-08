package dev.sirlennox.replcraftclient.api;

import java.util.UUID;

public class GameProfile {

    private final String username;
    private final UUID uuid;

    public GameProfile(final String username, final UUID uuid) {
        this.username = username;
        this.uuid = uuid;
    }

    public final String getUsername() {
        return this.username;
    }

    public final UUID getUuid() {
        return this.uuid;
    }
}
