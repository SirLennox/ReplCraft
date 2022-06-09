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

public class WebSocketReconnectListener extends WebSocketAdapter {

    private final ReplCraftClient client;

    public WebSocketReconnectListener(final ReplCraftClient client) {
        this.client = client;
    }

    @Override
    public void onDisconnected(final WebSocket websocket, final WebSocketFrame serverCloseFrame, final WebSocketFrame clientCloseFrame, final boolean closedByServer) throws Exception {
        System.out.println("disconnect");
        this.client.close();
        System.out.println("client closed");
        this.client.start().get();
        System.out.println("client started");
        super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
    }

    public final ReplCraftClient getClient() {
        return this.client;
    }
}
