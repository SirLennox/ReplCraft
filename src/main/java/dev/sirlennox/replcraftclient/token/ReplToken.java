package dev.sirlennox.replcraftclient.token;

import dev.sirlennox.replcraftclient.api.GameProfile;
import dev.sirlennox.replcraftclient.api.vector.WorldLocation;
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

    public final @NotNull WorldLocation getLocation() {
        return new WorldLocation(this.getData().get("world").asString(), this.getData().get("x").asInt(), this.getData().get("y").asInt(), this.getData().get("z").asInt());
    }

    public final @NotNull GameProfile getOwner() {
        return new GameProfile(this.getData().get("username").asString(), UUID.fromString(this.getData().get("uuid").asString()));
    }

    public final @NotNull Scope getScope() {
        return Scope.getById(this.getData().get("scope").asString());
    }





    public enum Permission {
        PUBLIC("public"), PLAYER("player"), ADMIN("admin");

        private final String id;

        Permission(final String id) {
            this.id = id;
        }

        public final String getId() {
            return this.id;
        }

        public static Permission getById(final String id) {
            return Arrays.stream(Permission.values()).filter(permission -> Objects.equals(permission.getId(), id)).findFirst().orElse(null);
        }
    }

    public enum Scope {
        STRUCTURE("structure"), ITEM("item");

        private final String id;

        Scope(final String id) {
            this.id = id;
        }

        public final String getId() {
            return this.id;
        }

        public static Scope getById(final String id) {
            return Arrays.stream(Scope.values()).filter(scope -> Objects.equals(scope.getId(), id)).findFirst().orElse(null);
        }
    }


}
