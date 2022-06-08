package dev.sirlennox.replcraftclient.api;

import com.eclipsesource.json.JsonObject;
import dev.sirlennox.replcraftclient.api.vector.DoubleVector;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class Entity {

    private final Double maxHealth;
    private final String name;
    private final DoubleVector location;
    private final Double health;
    private final String type;
    private final UUID playerUuid;

    public Entity(final Double maxHealth, final String name, final Double health, final DoubleVector location, final String type, final UUID playerUuid) {
        this.maxHealth = maxHealth;
        this.name = name;
        this.health = health;
        this.location = location;
        this.type = type;
        this.playerUuid = playerUuid;
    }

    public static Entity fromJson(@NotNull final JsonObject json) {
        return new Entity(
                Objects.nonNull(json.get("max_health")) ? json.get("max_health").asDouble() : null,
                json.get("name").asString(),
                Objects.nonNull(json.get("health")) ? json.get("health").asDouble() : null,
                new DoubleVector(
                        json.get("x").asDouble(),
                        json.get("y").asDouble(),
                        json.get("z").asDouble()
                ),
                json.get("type").asString(),
                Objects.nonNull(json.get("player_uuid")) ? UUID.fromString(json.get("player_uuid").asString()) : null
        );
    }

    public final Double getMaxHealth() {
        return this.maxHealth;
    }

    public final String getName() {
        return this.name;
    }

    public final DoubleVector getLocation() {
        return this.location;
    }

    public final String getType() {
        return this.type;
    }

    public final UUID getPlayerUuid() {
        return this.playerUuid;
    }

    public final Double getHealth() {
        return this.health;
    }
}
