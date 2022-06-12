package dev.sirlennox.replcraftclient;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import dev.sirlennox.replcraftclient.api.Entity;
import dev.sirlennox.replcraftclient.api.GameProfile;
import dev.sirlennox.replcraftclient.api.block.Block;
import dev.sirlennox.replcraftclient.api.fuel.FuelInfo;
import dev.sirlennox.replcraftclient.api.inventory.slot.ItemSlot;
import dev.sirlennox.replcraftclient.api.inventory.slot.SlotReference;
import dev.sirlennox.replcraftclient.api.listener.IListener;
import dev.sirlennox.replcraftclient.api.vector.IntVector;
import dev.sirlennox.replcraftclient.connection.exchange.Request;
import dev.sirlennox.replcraftclient.connection.exchange.Response;
import dev.sirlennox.replcraftclient.connection.listener.WebSocketEventListener;
import dev.sirlennox.replcraftclient.connection.listener.WebSocketReconnectListener;
import dev.sirlennox.replcraftclient.connection.listener.WebSocketResponseListener;
import dev.sirlennox.replcraftclient.token.ReplToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ReplCraftClient {
    private final ReplToken token;
    private WebSocket webSocket;
    private int nonce;
    private final List<IListener> listeners;
    private final boolean autoReconnect;

    /**
     * Will queue packets while you are not connected and send them if you connected successfully
     */
    private final Queue<QueuedMessage> sendQueue;
    private Thread thread;

    public ReplCraftClient(final ReplToken token, final boolean autoReconnect) {
        this.token = token;
        this.nonce = 0;
        this.listeners = Collections.synchronizedList(new ArrayList<>());
        this.sendQueue = new SynchronousQueue<>();
        this.autoReconnect = autoReconnect;
        this.thread = null;
    }


    public ReplCraftClient(final ReplToken token) {
        this(token, true);
    }

    public ReplCraftClient(final String token, final boolean autoReconnect) {
        this(new ReplToken(token), autoReconnect);
    }

    public ReplCraftClient(final String token) {
        this(new ReplToken(token));
    }

    public CompletableFuture<ReplCraftClient> start() {
        final CompletableFuture<ReplCraftClient> finishCallback = new CompletableFuture<>();

        (this.thread = new Thread(() -> {
            try {
                this.webSocket = new WebSocketFactory()
                        .setVerifyHostname(false)
                        .createSocket(String.format("ws://%s/gateway", this.token.getHost()));

                this.webSocket.addListener(new WebSocketEventListener(this));

                if (this.isAutoReconnect())
                    this.webSocket.addListener(new WebSocketReconnectListener(this));

                this.webSocket.connect();

                final Response response = this.authenticate().get();


                if (!response.isOk())
                    throw ReplCraftError.fromJson(response.getData());

                this.sendQueue.forEach(queuedMessage -> this.send(queuedMessage.getAction(), queuedMessage.getData(), queuedMessage.isWaitForResponse(), queuedMessage.getCallback()));
                this.callListener(IListener::onConnect);
                finishCallback.complete(this);
            } catch (WebSocketException | ExecutionException | InterruptedException | IOException | ReplCraftError e) {
                finishCallback.completeExceptionally(e);
            }
        })).start();

        return finishCallback;
    }

    public void close() {
        if (this.thread.isAlive() || !this.thread.isInterrupted())
            this.thread.interrupt();
        if (this.isConnected())
            this.webSocket.disconnect();

        this.nonce = 0;
        this.webSocket = null;
        this.thread = null;
    }


    private CompletableFuture<Response> authenticate() {
        final JsonObject data = new JsonObject();
        data.add("token", this.getToken().toString());

        return this.send("authenticate", data);
    }

    /**
     * @param x Structure relative X
     * @param y Structure relative Y
     * @param z Structure relative Z
     * @return Returns the world relative location of the structure relative location
     */
    public CompletableFuture<IntVector> getWorldLocation(final int x, final int y, final int z) {
        final JsonObject data = new JsonObject();
        data.add("x", x);
        data.add("y", y);
        data.add("z", z);

        final CompletableFuture<IntVector> callback = new CompletableFuture<>();
        this.send("get_location", data).whenComplete(this.inheritException(callback, response -> {
            final JsonObject responseData = response.getData();
            callback.complete(new IntVector(responseData.get("x").asInt(), responseData.get("y").asInt(), responseData.get("z").asInt()));
        }));

        return callback;
    }

    /**
     * Gets the world relative location of the structure
     */
    public CompletableFuture<IntVector> getLocation() {
        return this.getWorldLocation(0, 0, 0);
    }

    /**
     * Retrieves the inner size of the structure
     *
     * @param x The inner size of the structure in the x coordinate
     * @param y The inner size of the structure in the y coordinate
     * @param z The inner size of the structure in the z coordinate
     */
    public CompletableFuture<IntVector> getSize(final int x, final int y, final int z) {
        final JsonObject data = new JsonObject();
        data.add("x", x);
        data.add("y", y);
        data.add("z", z);

        final CompletableFuture<IntVector> callback = new CompletableFuture<>();
        this.send("get_size", data).whenComplete(this.inheritException(callback, response -> {
            final JsonObject responseData = response.getData();
            callback.complete(new IntVector(responseData.get("x").asInt(), responseData.get("y").asInt(), responseData.get("z").asInt()));
        }));

        return callback;
    }

    public CompletableFuture<IntVector> getSize() {
        return this.getSize(0, 0, 0);
    }

    /**
     * Sets a block inside your structure
     *
     * @param location        The location of the block (structure relative)
     * @param block           The block identifier
     * @param sourceContainer The block that will be moved (if null : will be taken from the structure inventory)
     * @param dropsContainer  If a block is being placed, the drops of it will be stored in the given container block (if null: will be put in the structure inventory)
     */
    public CompletableFuture<Response> setBlock(@NotNull final IntVector location, @NotNull final Block block, @Nullable final IntVector sourceContainer, @Nullable final IntVector dropsContainer) {
        final JsonObject data = new JsonObject();
        final CompletableFuture<Response> callback = new CompletableFuture<>();

        location.apply(data);

        data.add("blockData", block.toString());

        if (Objects.isNull(sourceContainer)) {
            data.add("source_x", Json.NULL);
            data.add("source_y", Json.NULL);
            data.add("source_z", Json.NULL);
        } else {
            sourceContainer.apply(data, "source_x", "source_y", "source_z");
        }

        if (Objects.isNull(dropsContainer)) {
            data.add("target_x", Json.NULL);
            data.add("target_y", Json.NULL);
            data.add("target_z", Json.NULL);
        } else {
            dropsContainer.apply(data, "target_x", "target_y", "target_z");
        }

        this.send("set_block", data).whenComplete(this.inheritException(callback, callback::complete));

        return callback;
    }

    /**
     * @param location The structure relative location of the sign
     * @return Returns the text of the sign
     */
    public CompletableFuture<List<String>> getSignText(@NotNull final IntVector location) {
        final JsonObject data = new JsonObject();
        final CompletableFuture<List<String>> callback = new CompletableFuture<>();

        location.apply(data);

        this.send("get_sign_text", data).whenComplete(this.inheritException(callback, response -> callback.complete(response.getData().get("lines").asArray().values().stream().map(JsonValue::asString).collect(Collectors.toList()))));

        return callback;
    }

    /**
     * @param location The location of the sign
     * @param lines    The lines of the new sign text (must be 4)
     * @return Returns a response, useless
     */
    public CompletableFuture<Response> setSignText(@NotNull final IntVector location, @NotNull final String[] lines) {
        if (lines.length != 4)
            throw new IllegalArgumentException("Lines must be exactly 4!");

        final JsonObject data = new JsonObject();
        final CompletableFuture<Response> callback = new CompletableFuture<>();

        location.apply(data);

        final JsonArray linesArray = new JsonArray();
        for (final String line : lines)
            linesArray.add(line);

        data.add("lines", linesArray);

        this.send("set_sign_text", data).whenComplete(this.inheritException(callback, callback::complete));

        return callback;
    }

    /**
     * Watches the given block for updates
     *
     * @param location The location of the block to watch, structure relative
     * @return Returns a response, useless
     */
    public CompletableFuture<Response> watch(@NotNull final IntVector location) {
        final JsonObject data = new JsonObject();
        final CompletableFuture<Response> callback = new CompletableFuture<>();

        location.apply(data);

        this.send("watch", data).whenComplete(this.inheritException(callback, callback::complete));

        return callback;
    }

    /**
     * Stops watching the given block for updates
     *
     * @param location The location of the block to unwatch, structure relative
     * @return Returns a response, useless
     */
    public CompletableFuture<Response> unwatch(@NotNull final IntVector location) {
        final JsonObject data = new JsonObject();
        final CompletableFuture<Response> callback = new CompletableFuture<>();

        location.apply(data);

        this.send("unwatch", data).whenComplete(this.inheritException(callback, callback::complete));

        return callback;
    }

    /**
     * Watches all blocks for updates
     *
     * @return Returns a response, useless
     */
    public CompletableFuture<Response> watchAll() {
        final CompletableFuture<Response> callback = new CompletableFuture<>();

        this.send("watch_all", new JsonObject()).whenComplete(this.inheritException(callback, callback::complete));

        return callback;
    }

    /**
     * Stops watching all blocks for updates
     *
     * @return Returns a response, useless
     */
    public CompletableFuture<Response> unwatchAll() {
        final CompletableFuture<Response> callback = new CompletableFuture<>();

        this.send("unwatch_all", new JsonObject()).whenComplete(this.inheritException(callback, callback::complete));

        return callback;
    }


    /**
     * Begins polling a block for updates.
     * Note that this catches all possible block updates, but only one block is polled per tick.
     * The more blocks you poll, the slower each individual block will be checked.
     * Additionally, if a block changes multiple times between polls, only the latest change
     * will be reported.
     *
     * @param location The location of the block to poll
     * @return Returns a response, useless
     */
    public CompletableFuture<Response> poll(@NotNull final IntVector location) {
        final JsonObject data = new JsonObject();
        final CompletableFuture<Response> callback = new CompletableFuture<>();

        location.apply(data);

        this.send("poll", data).whenComplete(this.inheritException(callback, callback::complete));

        return callback;
    }

    /**
     * Stops polling a block for updates.
     *
     * @param location The location of the block to unpoll
     * @return Returns a response, useless
     */
    public CompletableFuture<Response> unpoll(@NotNull final IntVector location) {
        final JsonObject data = new JsonObject();
        final CompletableFuture<Response> callback = new CompletableFuture<>();

        location.apply(data);

        this.send("unpoll", data).whenComplete(this.inheritException(callback, callback::complete));

        return callback;
    }

    /**
     * Begins polling all blocks in the structure for updates.
     *
     * @return Returns a response, useless
     */
    public CompletableFuture<Response> pollAll() {
        final CompletableFuture<Response> callback = new CompletableFuture<>();

        this.send("poll_all", new JsonObject()).whenComplete(this.inheritException(callback, callback::complete));

        return callback;
    }

    /**
     * Stops polling all blocks in the structure for updates.
     *
     * @return Returns a response, useless
     */
    public CompletableFuture<Response> unpollAll() {
        final CompletableFuture<Response> callback = new CompletableFuture<>();

        this.send("unpoll_all", new JsonObject()).whenComplete(this.inheritException(callback, callback::complete));

        return callback;
    }

    /**
     * @return Returns all entities on the structure
     */
    public CompletableFuture<List<Entity>> getEntities() {
        final CompletableFuture<List<Entity>> callback = new CompletableFuture<>();

        this.send("get_entities", new JsonObject()).whenComplete(this.inheritException(callback, response -> callback.complete(response.getData().get("entities").asArray().values().stream().map(jsonValue -> Entity.fromJson(jsonValue.asObject())).collect(Collectors.toList()))));

        return callback;
    }

    /**
     * @return Returns the inventory of the given container
     */
    public CompletableFuture<List<ItemSlot>> getInventory(@NotNull final IntVector location) {
        final CompletableFuture<List<ItemSlot>> callback = new CompletableFuture<>();
        final JsonObject data = new JsonObject();

        location.apply(data);

        this.send("get_inventory", data).whenComplete(this.inheritException(callback, response -> callback.complete(response.getData().get("items").asArray().values().stream().map(jsonValue -> ItemSlot.fromJson(this, location, jsonValue.asObject())).collect(Collectors.toList()))));

        return callback;
    }

    /**
     * Moves an item from one container to another
     *
     * @param sourceContainer The source container location where the item is from
     * @param itemIndex       The slot index of the item
     * @param amount          The amount of the item (if null: all)
     * @param targetContainer The target container location where the item is being moved to
     * @param targetItemIndex The target slot index of the item (if null: any)
     * @return Returns a response, useless
     */
    public CompletableFuture<Response> moveItem(@NotNull final IntVector sourceContainer, final int itemIndex, @Nullable final Integer amount, @NotNull final IntVector targetContainer, @Nullable final Integer targetItemIndex) {
        final CompletableFuture<Response> callback = new CompletableFuture<>();
        final JsonObject data = new JsonObject();

        if (Objects.nonNull(amount)) {
            data.add("amount", amount);
        } else {
            data.add("amount", Json.NULL);
        }

        data.add("index", itemIndex);
        sourceContainer.apply(data, "source_x", "source_y", "source_z");

        if (Objects.nonNull(targetItemIndex)) {
            data.add("target_index", targetItemIndex);
        } else {
            data.add("target_index", Json.NULL);
        }

        targetContainer.apply(data, "target_x", "target_y", "target_z");

        this.send("move_item", data).whenComplete(this.inheritException(callback, callback::complete));

        return callback;
    }

    /**
     * Gets the redstone power level of a block
     *
     * @param location Structure relative location of target block
     * @return Returns redstone power level
     */
    public CompletableFuture<Integer> getRedstonePowerLevel(@NotNull final IntVector location) {
        final CompletableFuture<Integer> callback = new CompletableFuture<>();
        final JsonObject data = new JsonObject();

        location.apply(data);

        this.send("get_power_level", data).whenComplete(this.inheritException(callback, response -> callback.complete(response.getData().get("power").asInt())));

        return callback;
    }

    /**
     * Send a message to a player
     * Must be online and in the structure
     *
     * @param target  UUID or username of the player
     * @param message The message to send
     * @return Returns a response, useless
     */

    public CompletableFuture<Response> tell(@NotNull final String target, @NotNull final String message) {
        final CompletableFuture<Response> callback = new CompletableFuture<>();
        final JsonObject data = new JsonObject();

        data.add("target", target);
        data.add("message", message);

        this.send("tell", data).whenComplete(this.inheritException(callback, callback::complete));

        return callback;
    }

    public CompletableFuture<Response> tell(@NotNull final UUID target, @NotNull final String message) {
        return this.tell(target.toString(), message);
    }

    public CompletableFuture<Response> tell(@NotNull final GameProfile target, @NotNull final String message) {
        return this.tell(target.getUuid(), message);
    }

    /**
     * Sends money to a player from your own account
     * Must be online and in the structure
     *
     * @param target UUID or username of the player
     * @param amount The amount of money
     * @return Returns a response, useless
     */

    public CompletableFuture<Response> pay(@NotNull final String target, final double amount) {
        final CompletableFuture<Response> callback = new CompletableFuture<>();
        final JsonObject data = new JsonObject();

        data.add("target", target);
        data.add("amount", amount);


        this.send("pay", data).whenComplete(this.inheritException(callback, callback::complete));

        return callback;
    }

    public CompletableFuture<Response> pay(@NotNull final UUID target, final double amount) {
        return this.pay(target.toString(), amount);
    }

    public CompletableFuture<Response> pay(@NotNull final GameProfile target, final double amount) {
        return this.pay(target.getUuid(), amount);
    }


    /**
     * @return Returns fuel info for api requests and sent requests
     */
    public CompletableFuture<FuelInfo> getFuelInfo() {
        final CompletableFuture<FuelInfo> callback = new CompletableFuture<>();

        this.send("fuelinfo", new JsonObject()).whenComplete(this.inheritException(callback, response -> callback.complete(FuelInfo.fromJson(response.getData()))));

        return callback;
    }

    /**
     * @return Returns the block info of the specified structure-relative coordinates
     */
    public CompletableFuture<Block> getBlock(@NotNull final IntVector location) {
        final CompletableFuture<Block> callback = new CompletableFuture<>();
        final JsonObject data = new JsonObject();
        location.apply(data);

        this.send("get_block", data).whenComplete(this.inheritException(callback, response -> callback.complete(Block.parse(response.getData().get("block").asString()))));

        return callback;
    }

    /**
     * Responds to an active transaction
     *
     * @param queryNonce The nonce of the transaction
     * @param accept     True if the transaction was accepted
     */
    public void respondToTransaction(final int queryNonce, final boolean accept) {
        final JsonObject data = new JsonObject();

        data.add("queryNonce", queryNonce);
        data.add("accept", accept);

        this.send("respond", data, false);
    }

    public CompletableFuture<Response> craft(final IntVector outputContainer, final SlotReference[] ingredients) {
        final CompletableFuture<Response> callback = new CompletableFuture<>();
        final JsonObject data = new JsonObject();

        outputContainer.apply(data);
        final JsonArray ingredientsArray = new JsonArray();
        for (SlotReference ingredient : ingredients)
            ingredientsArray.add(ingredient == null ? Json.NULL : ingredient.toJson());

        data.add("ingredients", ingredientsArray);

        this.send("craft", data).whenComplete(this.inheritException(callback, callback::complete));

        return callback;
    }

    private CompletableFuture<Response> send(@NotNull final String action, @Nullable final JsonObject data, final boolean waitForResponse, final CompletableFuture<Response> callback) {
        if (!this.isConnected()) {
            this.sendQueue.offer(new QueuedMessage(callback, action, data, waitForResponse));
            return callback;
        }

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

    private CompletableFuture<Response> send(@NotNull final String action, @Nullable final JsonObject data, final boolean waitForResponse) {
        return this.send(action, data, waitForResponse, new CompletableFuture<>());
    }

    private CompletableFuture<Response> send(@NotNull final String action, @Nullable final JsonObject data) {
        return this.send(action, data, true);
    }


    private <T> BiConsumer<? super T, Throwable> inheritException(final CompletableFuture<?> callback, final Consumer<? super T> consumer) {
        return (object, throwable) -> {
            if (Objects.nonNull(throwable)) {
                callback.completeExceptionally(throwable);
                return;
            }
            consumer.accept(object);
        };
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

    public final WebSocket getWebSocket() {
        return this.webSocket;
    }

    public final boolean isConnected() {
        return Objects.nonNull(this.webSocket) && this.webSocket.isOpen();
    }

    public final boolean isAutoReconnect() {
        return this.autoReconnect;
    }

    public final Thread getThread() {
        return this.thread;
    }

    public static class QueuedMessage {
        private final CompletableFuture<Response> callback;
        private final String action;
        private final JsonObject data;
        private final boolean waitForResponse;

        public QueuedMessage(final CompletableFuture<Response> callback, final String action, final JsonObject data, final boolean waitForResponse) {
            this.callback = callback;
            this.action = action;
            this.data = data;
            this.waitForResponse = waitForResponse;
        }

        public final String getAction() {
            return this.action;
        }

        public final JsonObject getData() {
            return this.data;
        }

        public final boolean isWaitForResponse() {
            return this.waitForResponse;
        }

        public final CompletableFuture<Response> getCallback() {
            return this.callback;
        }
    }
}
