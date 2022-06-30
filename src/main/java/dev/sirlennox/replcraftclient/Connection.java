package dev.sirlennox.replcraftclient;

import com.eclipsesource.json.JsonObject;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import dev.sirlennox.replcraftclient.connection.exchange.Request;
import dev.sirlennox.replcraftclient.connection.exchange.Response;
import dev.sirlennox.replcraftclient.connection.listener.WebSocketEventListener;
import dev.sirlennox.replcraftclient.connection.listener.WebSocketResponseListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class Connection {

    private WebSocket webSocket;
    private final ReplCraftClient client;
    private int nonce;

    public Connection(final ReplCraftClient client) {
        this.client = client;
        this.nonce = 0;
    }

    public void connect() throws IOException, WebSocketException {
        this.webSocket = new WebSocketFactory()
                .setVerifyHostname(false)
                .createSocket(String.format("ws://%s/gateway/v2", this.client.getToken().getHost()));

        this.webSocket.addListener(new WebSocketEventListener(this.getClient()));

        this.webSocket.connect();
    }

    public CompletableFuture<Response> send(@NotNull final String action, @Nullable final JsonObject data, final boolean waitForResponse) {
        return this.send(action, data, waitForResponse, new CompletableFuture<>());
    }

    public CompletableFuture<Response> send(@NotNull final String action, @Nullable final JsonObject data) {
        return this.send(action, data, true);
    }

    public CompletableFuture<Response> send(@NotNull final String action, @Nullable final JsonObject data, final boolean waitForResponse, final CompletableFuture<Response> callback) {
        final Request request = new Request(this.nonce++, action, Objects.isNull(data) ? new JsonObject() : data);
        this.webSocket.sendText(request.toJson().toString());

        if (waitForResponse) {

            final WebSocketResponseListener responseListener = new WebSocketResponseListener(request.getNonce(), (response, instance) -> {
                if (!response.isOk()) {
                    callback.completeExceptionally(ReplCraftError.fromJson(response.getData()));
                    return;
                }
                callback.complete(response);
                this.webSocket.removeListener(instance);
            });

            this.webSocket.addListener(responseListener);
            return callback;
        }

        return null;
    }

    public void disconnect() {
        if (this.isConnected())
            this.webSocket.disconnect();
        this.webSocket = null;
    }

    public final ReplCraftClient getClient() {
        return this.client;
    }

    public final WebSocket getWebSocket() {
        return this.webSocket;
    }

    public final int getNonce() {
        return this.nonce;
    }

    public final boolean isConnected() {
        return Objects.nonNull(this.webSocket) && this.webSocket.isOpen();
    }
}
