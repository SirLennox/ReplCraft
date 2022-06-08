package dev.sirlennox.replcraftclient.connection.listener;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFrame;
import dev.sirlennox.replcraftclient.ReplCraftClient;
import dev.sirlennox.replcraftclient.api.Transaction;
import dev.sirlennox.replcraftclient.api.event.BlockUpdateEvent;

import java.util.Objects;

public class WebSocketEventListener extends WebSocketAdapter {

    private final ReplCraftClient client;

    public WebSocketEventListener(final ReplCraftClient client) {
        this.client = client;
    }

    @Override
    public void onFrame(final WebSocket websocket, final WebSocketFrame frame) throws Exception {
        final JsonObject data = Json.parse(new String(frame.getPayload())).asObject();

        if (Objects.isNull(data.get("type")))
            return;


        switch (data.get("type").asString()) {
            case "block update":
                this.client.callListener(listener -> listener.onBlockUpdate(BlockUpdateEvent.fromJson(data)));
                break;
            case "transact":
                this.client.callListener(listener -> listener.onTransaction(Transaction.fromJson(this.client, data)));
                break;
        }

        super.onFrame(websocket, frame);
    }

    public final ReplCraftClient getClient() {
        return this.client;
    }
}
