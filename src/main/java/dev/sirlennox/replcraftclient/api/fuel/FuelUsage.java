package dev.sirlennox.replcraftclient.api.fuel;

import com.eclipsesource.json.JsonObject;
import org.jetbrains.annotations.NotNull;

public class FuelUsage {

    private final String endpoint;
    private final double second;
    private final double minute;

    public FuelUsage(final String endpoint, final double second, final double minute) {
        this.endpoint = endpoint;
        this.second = second;
        this.minute = minute;
    }

    public static FuelUsage fromJson(@NotNull final String endpoint, @NotNull final JsonObject json) {
        return new FuelUsage(
                endpoint,
                json.get("second").asDouble(),
                json.get("minute").asDouble()
        );
    }

    public final String getEndpoint() {
        return this.endpoint;
    }

    public final double getSecond() {
        return this.second;
    }

    public final double getMinute() {
        return this.minute;
    }
}
