package dev.sirlennox.replcraftclient.connection.exchange;

import com.eclipsesource.json.JsonObject;
import org.jetbrains.annotations.NotNull;

public class Request {

    private final int nonce;
    private final String action;
    private final JsonObject data;

    public Request(final int nonce, @NotNull final String action, @NotNull final JsonObject data) {
        this.nonce = nonce;
        this.action = action;
        this.data = data;
    }

    @NotNull
    public JsonObject toJson() {
        final JsonObject json = new JsonObject(this.data);

        json.add("nonce", String.valueOf(this.nonce));
        json.add("action", this.action);

        return json;
    }

    public final int getNonce() {
        return this.nonce;
    }

    public final @NotNull String getAction() {
        return this.action;
    }


    public final @NotNull JsonObject getData() {
        return this.data;
    }
}
