package dev.sirlennox.replcraftclient.api.fuel;

import com.eclipsesource.json.JsonObject;
import dev.sirlennox.replcraftclient.context.Context;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class FuelInfo {

    private final HashMap<String, FuelAPIInfo> apis;
    private final List<Connection> connections;
    private final List<FuelInfoStrategy> strategies;
    private final double fuelPerTick;

    public FuelInfo(final HashMap<String, FuelAPIInfo> apis, final List<FuelInfoStrategy> strategies, final List<Connection> connections, final double fuelPerTick) {
        this.apis = apis;
        this.strategies = strategies;
        this.connections = connections;
        this.fuelPerTick = fuelPerTick;
    }

    public static FuelInfo fromJson(@NotNull final Context context, @NotNull final JsonObject json) {
        final HashMap<String, FuelAPIInfo> apis = new HashMap<>();
        json.get("apis").asObject().iterator().forEachRemaining(member -> apis.put(member.getName(), FuelAPIInfo.fromJson(member.getName(), member.getValue().asObject())));
        return new FuelInfo(
                apis,
                json.get("strategies").asArray().values().stream().map(jsonValue -> FuelInfoStrategy.fromJson(context, jsonValue.asObject())).collect(Collectors.toList()),
                json.get("connections").asArray().values().stream().map(jsonValue -> Connection.fromJson(jsonValue.asObject())).collect(Collectors.toList()),
                json.get("fuelPerTick").asDouble()
        );
    }

    public final HashMap<String, FuelAPIInfo> getApis() {
        return this.apis;
    }

    public final List<Connection> getConnections() {
        return this.connections;
    }

    public final List<FuelInfoStrategy> getStrategies() {
        return this.strategies;
    }

    public final double getFuelPerTick() {
        return this.fuelPerTick;
    }
}
