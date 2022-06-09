package dev.sirlennox.replcraftclient.api.fuel;

import com.eclipsesource.json.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class FuelInfo {

    private final HashMap<String, FuelAPIInfo> apis;
    private final List<Connection> connections;

    public FuelInfo(final HashMap<String, FuelAPIInfo> apis, final List<Connection> connections) {
        this.apis = apis;
        this.connections = connections;
    }

    public static FuelInfo fromJson(@NotNull final JsonObject json) {
        final HashMap<String, FuelAPIInfo> apis = new HashMap<>();
        json.get("apis").asObject().iterator().forEachRemaining(member -> apis.put(member.getName(), FuelAPIInfo.fromJson(member.getName(), member.getValue().asObject())));
        return new FuelInfo(
                apis,
                json.get("connections").asArray().values().stream().map(jsonValue -> Connection.fromJson(jsonValue.asObject())).collect(Collectors.toList())
        );
    }

    public final HashMap<String, FuelAPIInfo> getApis() {
        return this.apis;
    }

    public final List<Connection> getConnections() {
        return this.connections;
    }
}
