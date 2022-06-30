package dev.sirlennox.replcraftclient;

import com.eclipsesource.json.JsonObject;
import com.neovisionaries.ws.client.WebSocketException;
import dev.sirlennox.replcraftclient.api.listener.IListener;
import dev.sirlennox.replcraftclient.command.CommandSystem;
import dev.sirlennox.replcraftclient.connection.exchange.Response;
import dev.sirlennox.replcraftclient.context.Context;
import dev.sirlennox.replcraftclient.token.ReplToken;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ReplCraftClient {

    private final List<IListener> listeners;
    private final List<Context> contexts;
    private final ReplToken token;
    private final Timer heartbeatTimer;
    private final CommandSystem commandSystem;
    private Thread thread;
    private final Connection connection;

    public ReplCraftClient(final ReplToken token) {
        this.token = token;
        this.heartbeatTimer = new Timer("Heartbeat-Timer");
        this.connection = new Connection(this);
        this.commandSystem = new CommandSystem();
        this.listeners = Collections.synchronizedList(new ArrayList<>());
        this.contexts = new ArrayList<>();
        this.addListener(this.commandSystem.createListener());
        this.thread = null;
    }

    public ReplCraftClient(final String token) {
        this(new ReplToken(token));
    }

    public CompletableFuture<ReplCraftClient> start() {
        final CompletableFuture<ReplCraftClient> finishCallback = new CompletableFuture<>();

        (this.thread = new Thread(() -> {
            try {

                this.connection.connect();

                final Response response = this.authenticate().get();


                if (!response.isOk())
                    throw ReplCraftError.fromJson(response.getData());

                this.callListener(IListener::onConnect);
                finishCallback.complete(this);

                ReplCraftClient.this.heartbeatTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        ReplCraftClient.this.sendHeartbeat();
                    }
                }, 15000L, 15000L);

            } catch (WebSocketException | ExecutionException | InterruptedException | IOException | ReplCraftError e) {
                finishCallback.completeExceptionally(e);
            }
        })).start();

        return finishCallback;
    }

    public void close() {
        if (this.thread.isAlive() || !this.thread.isInterrupted())
            this.thread.interrupt();

        this.heartbeatTimer.cancel();
        this.connection.disconnect();
        this.thread = null;
    }


    private CompletableFuture<Response> authenticate() {
        final JsonObject data = new JsonObject();
        data.add("token", this.getToken().toString());

        return this.getConnection().send("authenticate", data);
    }

    public CompletableFuture<Response> sendHeartbeat() {
        return this.getConnection().send("heartbeat", new JsonObject(), false);
    }


    public void addListener(final IListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(final IListener listener) {
        this.listeners.remove(listener);
    }

    public void callListener(final Consumer<? super IListener> consumer) {
        new Thread(() -> {
            for (IListener listener : this.listeners)
                consumer.accept(listener);
        }, "Listener-Thread").start();
    }

    public final ReplToken getToken() {
        return this.token;
    }

    public final Thread getThread() {
        return this.thread;
    }

    public final CommandSystem getCommandSystem() {
        return this.commandSystem;
    }

    public final Connection getConnection() {
        return this.connection;
    }

    public final List<Context> getContexts() {
        return this.contexts;
    }

    public final Context getContextById(final int id) {
        return this.contexts.stream().filter(context -> context.getId() == id).findFirst().orElse(null);
    }

    public void removeContext(final int id) {
        this.contexts.removeIf(context -> context.getId() == id);
    }

    public final Timer getHeartbeatTimer() {
        return this.heartbeatTimer;
    }
}
