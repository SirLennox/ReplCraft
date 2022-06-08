package dev.sirlennox.replcraftclient.api.fuel;

import com.eclipsesource.json.JsonObject;
import org.jetbrains.annotations.NotNull;

public class FuelUsage {

    private final String endpoint;
    private final int second;
    private final int minute;

    public FuelUsage(final String endpoint, final int second, final int minute) {
        this.endpoint = endpoint;
        this.second = second;
        this.minute = minute;
    }

    public static FuelUsage fromJson(@NotNull final String endpoint, @NotNull final JsonObject json) {
        return new FuelUsage(
                endpoint,
                json.get("second").asInt(),
                json.get("minute").asInt()
        );
    }

    public final String getEndpoint() {
        return this.endpoint;
    }

    public final int getSecond() {
        return this.second;
    }

    public final int getMinute() {
        return this.minute;
    }
}
