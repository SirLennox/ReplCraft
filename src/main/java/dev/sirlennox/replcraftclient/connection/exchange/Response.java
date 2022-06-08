package dev.sirlennox.replcraftclient.connection.exchange;

import com.eclipsesource.json.JsonObject;
import org.jetbrains.annotations.NotNull;

public class Response {

    private final boolean ok;
    private final int nonce;
    private final JsonObject data;

    public Response(final boolean ok, final int nonce, final JsonObject data) {
        this.ok = ok;
        this.nonce = nonce;
        this.data = data;
    }

    public static Response fromJson(@NotNull final JsonObject json) throws NumberFormatException {
        final JsonObject data = new JsonObject(json);
        data.remove("nonce");
        data.remove("ok");

        return new Response(
                json.get("ok").asBoolean(),
                Integer.parseInt(json.get("nonce").asString()),
                data
        );
    }

    public final boolean isOk() {
        return this.ok;
    }

    public final int getNonce() {
        return this.nonce;
    }

    public final JsonObject getData() {
        return this.data;
    }
}
