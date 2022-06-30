package dev.sirlennox.replcraftclient.connection.listener;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFrame;
import dev.sirlennox.replcraftclient.context.Context;
import dev.sirlennox.replcraftclient.ReplCraftClient;
import dev.sirlennox.replcraftclient.api.Transaction;
import dev.sirlennox.replcraftclient.api.event.BlockUpdateEvent;
import dev.sirlennox.replcraftclient.context.OpenCause;

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

        final int contextId = data.get("context").asInt();

        switch (data.get("type").asString()) {
            case "contextOpened":
                final Context context = new Context(contextId, this.client, OpenCause.getById(data.get("cause").asString()));
                this.client.getContexts().add(context);
                this.client.callListener(listener -> listener.onContextOpened(context));
                break;
            case "contextClosed":
                final Context closedContext = this.client.getContextById(data.get("context").asInt());
                this.client.removeContext(closedContext.getId());
                closedContext.callListener(listener -> listener.onClose(data.get("cause").asString()));
                break;
            case "block update":
                this.client.getContextById(contextId).callListener(listener -> listener.onBlockUpdate(BlockUpdateEvent.fromJson(data)));
                break;
            case "transact":
                final Context transactContext = this.client.getContextById(contextId);
                transactContext.callListener(listener -> listener.onTransaction(Transaction.fromJson(transactContext, data)));
                break;
        }

        super.onFrame(websocket, frame);
    }

    @Override
    public void onDisconnected(final WebSocket websocket, final WebSocketFrame serverCloseFrame, final WebSocketFrame clientCloseFrame, final boolean closedByServer) throws Exception {
        this.client.callListener(listener -> listener.onDisconnect(closedByServer ? serverCloseFrame.getCloseCode() : clientCloseFrame.getCloseCode()));
        super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
    }

    public final ReplCraftClient getClient() {
        return this.client;
    }
}
