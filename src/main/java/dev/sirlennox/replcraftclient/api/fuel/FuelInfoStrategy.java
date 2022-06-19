package dev.sirlennox.replcraftclient.api.fuel;

import com.eclipsesource.json.JsonObject;

public class FuelInfoStrategy {

    private final String name;
    private final double spareFuel;

    public FuelInfoStrategy(final String name, final double spareFuel) {
        this.name = name;
        this.spareFuel = spareFuel;
    }

    public static FuelInfoStrategy fromJson(final JsonObject json) {
        return new FuelInfoStrategy(
                json.get("strategy").asString(),
                json.get("spareFuel").asDouble()
        );
    }

    public final String getName() {
        return this.name;
    }

    public final double getSpareFuel() {
        return this.spareFuel;
    }
}
