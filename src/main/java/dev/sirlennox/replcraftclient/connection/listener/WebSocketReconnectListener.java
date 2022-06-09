package dev.sirlennox.replcraftclient.connection.listener;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFrame;
import dev.sirlennox.replcraftclient.ReplCraftClient;

public class WebSocketReconnectListener extends WebSocketAdapter {

    private final ReplCraftClient client;

    public WebSocketReconnectListener(final ReplCraftClient client) {
        this.client = client;
    }

    @Override
    public void onDisconnected(final WebSocket websocket, final WebSocketFrame serverCloseFrame, final WebSocketFrame clientCloseFrame, final boolean closedByServer) throws Exception {
        this.client.close();
        this.client.start().get();
        super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
    }

    public final ReplCraftClient getClient() {
        return this.client;
    }
}
