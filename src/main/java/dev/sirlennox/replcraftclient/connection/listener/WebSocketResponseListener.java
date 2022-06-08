package dev.sirlennox.replcraftclient.connection.listener;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFrame;
import dev.sirlennox.replcraftclient.connection.exchange.Response;

import java.util.Objects;
import java.util.function.BiConsumer;

public class WebSocketResponseListener extends WebSocketAdapter {

    private final int nonce;
    private final BiConsumer<Response, WebSocketResponseListener> callback;

    public WebSocketResponseListener(final int nonce, final BiConsumer<Response, WebSocketResponseListener> callback) {
        this.nonce = nonce;
        this.callback = callback;
    }

    @Override
    public void onFrame(final WebSocket websocket, final WebSocketFrame frame) throws Exception {
        final JsonObject data = Json.parse(new String(frame.getPayload())).asObject();

        if (Objects.isNull(data.get("nonce")) || !data.get("nonce").asString().equals(String.valueOf(this.nonce)))
            return;

        callback.accept(Response.fromJson(data), this);

        super.onFrame(websocket, frame);
    }

    public final int getNonce() {
        return this.nonce;
    }

    public final BiConsumer<Response, WebSocketResponseListener> getCallback() {
        return this.callback;
    }
}
