package dev.sirlennox.replcraftclient.api.fuel;

import com.eclipsesource.json.JsonObject;
import org.jetbrains.annotations.NotNull;

public class FuelAPIInfo {

    private final String endpoint;
    private final double baseFuelCost;
    private final double fuelCost;

    public FuelAPIInfo(final String endpoint, final double baseFuelCost, final double fuelCost) {
        this.endpoint = endpoint;
        this.baseFuelCost = baseFuelCost;
        this.fuelCost = fuelCost;
    }

    public static FuelAPIInfo fromJson(@NotNull final String endpoint, final JsonObject json) {
        return new FuelAPIInfo(
                endpoint,
                json.get("baseFuelCost").asDouble(),
                json.get("fuelCost").asDouble()
        );
    }

    public final String getEndpoint() {
        return this.endpoint;
    }

    public final double getBaseFuelCost() {
        return this.baseFuelCost;
    }

    public final double getFuelCost() {
        return this.fuelCost;
    }
}
