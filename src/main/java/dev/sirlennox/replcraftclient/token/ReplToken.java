package dev.sirlennox.replcraftclient.token;

import dev.sirlennox.replcraftclient.api.GameProfile;
import dev.sirlennox.replcraftclient.api.vector.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class ReplToken extends JWTToken {

    public ReplToken(final String token) {
        super(token);
    }

    public final @NotNull String getHost() {
        return this.getData().get("host").asString();
    }

    public final @NotNull Location getLocation() {
        return new Location(this.getData().get("world").asString(), this.getData().get("x").asInt(), this.getData().get("y").asInt(), this.getData().get("z").asInt());
    }

    public final @NotNull GameProfile getOwner() {
        return new GameProfile(this.getData().get("username").asString(), UUID.fromString(this.getData().get("uuid").asString()));
    }

    public final @NotNull Permission getPermission() {
        return Permission.getById(this.getData().get("permission").asString());
    }

    public enum Permission {
        PUBLIC("public"), PLAYER("player"), UNKNOWN(null);

        private final String id;

        Permission(final String id) {
            this.id = id;
        }

        public final String getId() {
            return this.id;
        }

        public static Permission getById(final String id) {
            return Arrays.stream(Permission.values()).filter(permission -> Objects.equals(permission.getId(), id)).findFirst().orElse(Permission.UNKNOWN);
        }
    }
}
