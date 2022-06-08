package dev.sirlennox.replcraftclient.api.fuel;

import com.eclipsesource.json.JsonObject;
import dev.sirlennox.replcraftclient.api.vector.IntVector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class Connection {

    private final IntVector location;
    private final String structure;
    private final HashMap<String, FuelUsage> fuelUsage;


    public Connection(final IntVector location, final String structure, final HashMap<String, FuelUsage> fuelUsage) {
        this.location = location;
        this.structure = structure;
        this.fuelUsage = fuelUsage;
    }

    public static Connection fromJson(@NotNull final JsonObject json) {
        final HashMap<String, FuelUsage> fuelUsage = new HashMap<>();
        json.get("fuelUsage").asObject().iterator().forEachRemaining(member -> fuelUsage.put(member.getName(), FuelUsage.fromJson(member.getName(), member.getValue().asObject())));
        return new Connection(
                new IntVector(
                        json.get("x").asInt(),
                        json.get("y").asInt(),
                        json.get("z").asInt()
                ),
                json.get("structure").asString(),
                fuelUsage
        );
    }

    public final IntVector getLocation() {
        return this.location;
    }

    public final String getStructure() {
        return this.structure;
    }

    public final HashMap<String, FuelUsage> getFuelUsage() {
        return this.fuelUsage;
    }
}
