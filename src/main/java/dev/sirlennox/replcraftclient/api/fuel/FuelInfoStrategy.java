package dev.sirlennox.replcraftclient.api.fuel;

import com.eclipsesource.json.JsonObject;
import dev.sirlennox.replcraftclient.connection.exchange.Response;
import dev.sirlennox.replcraftclient.context.Context;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class FuelInfoStrategy {

    private final String name;
    private final double spareFuel;
    private final double totalUsed;
    private final double userLimit;
    private final double generatableEstimate;
    private final Context context;

    public FuelInfoStrategy(final String name, final double spareFuel, final double totalUsed, final double userLimit, final double generatableEstimate, final Context context) {
        this.name = name;
        this.spareFuel = spareFuel;
        this.totalUsed = totalUsed;
        this.userLimit = userLimit;
        this.generatableEstimate = generatableEstimate;
        this.context = context;
    }

    public static FuelInfoStrategy fromJson(@NotNull final Context context, @NotNull final JsonObject json) {
        return new FuelInfoStrategy(
                json.get("strategy").asString(),
                json.get("spareFuel").asDouble(),
                json.get("totalUsed").asDouble(),
                json.get("userLimit").asDouble(),
                json.get("generatableEstimate").asDouble(),
                context
        );
    }

    public CompletableFuture<Response> setLimit(final double limit) {
        return this.context.setFuelLimit(this.name, limit);
    }

    public final String getName() {
        return this.name;
    }

    public final double getSpareFuel() {
        return this.spareFuel;
    }

    public final double getTotalUsed() {
        return this.totalUsed;
    }

    public final double getUserLimit() {
        return this.userLimit;
    }

    public final double getGeneratableEstimate() {
        return this.generatableEstimate;
    }

    public final Context getContext() {
        return this.context;
    }
}
