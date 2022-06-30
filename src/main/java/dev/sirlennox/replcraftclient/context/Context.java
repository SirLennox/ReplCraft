package dev.sirlennox.replcraftclient.context;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import dev.sirlennox.replcraftclient.ReplCraftClient;
import dev.sirlennox.replcraftclient.api.Entity;
import dev.sirlennox.replcraftclient.api.GameProfile;
import dev.sirlennox.replcraftclient.api.block.Block;
import dev.sirlennox.replcraftclient.api.fuel.FuelInfo;
import dev.sirlennox.replcraftclient.api.inventory.slot.ItemSlot;
import dev.sirlennox.replcraftclient.api.inventory.slot.SlotReference;
import dev.sirlennox.replcraftclient.api.listener.IContextListener;
import dev.sirlennox.replcraftclient.api.vector.Location;
import dev.sirlennox.replcraftclient.connection.exchange.Response;
import dev.sirlennox.replcraftclient.util.CompletableFutureUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Context {

    private final int id;
    private final ReplCraftClient client;
    private final OpenCause openCause;
    private final List<IContextListener> listeners;

    public Context(final int id, final ReplCraftClient client, final OpenCause openCause) {
        this.id = id;
        this.client = client;
        this.openCause = openCause;
        this.listeners = new ArrayList<>();
    }

    public CompletableFuture<Response> close() {
        final CompletableFuture<Response> callback = new CompletableFuture<>();
        this.send("close", new JsonObject()).whenComplete(CompletableFutureUtil.inheritException(callback, callback::complete));

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

        this.send("tell", data).whenComplete(CompletableFutureUtil.inheritException(callback, callback::complete));

        return callback;
    }

    public CompletableFuture<Response> tell(@NotNull final UUID target, @NotNull final String message) {
        return this.tell(target.toString(), message);
    }

    public CompletableFuture<Response> tell(@NotNull final GameProfile target, @NotNull final String message) {
        return this.tell(target.getUuid(), message);
    }

    /**
     * Will split the message at a specified characters limit and send them seperately, will try to avoid splitting the parts
     * @param target UUID or username of the player
     * @param parts The parts that should not be splitted (for color codes or other things)
     * @param maxSize The max size of the string; will be splitted by that amount
     */
    public void tellWithSplitMessages(final String target, final int maxSize, final String... parts) {
        final StringBuilder stringBuilder = new StringBuilder();
        final List<String> pieces = new ArrayList<>();

        for(final String message : parts) {
            if (message.length() + stringBuilder.length() > maxSize) {
                if (!stringBuilder.toString().isEmpty()) {
                    pieces.add(stringBuilder.toString());
                    stringBuilder.setLength(0);
                }
                if (message.length() > maxSize) {
                    for (int i = 0; i < message.length(); i += maxSize) {
                        pieces.add(message.substring(i, Math.min(i + maxSize, message.length())));
                    }
                } else {
                    stringBuilder.append(message);
                }
            } else {
                stringBuilder.append(message);
            }
        }

        if (!stringBuilder.toString().isEmpty()) {
            pieces.add(stringBuilder.toString());
            stringBuilder.setLength(0);
        }

        for (final String piece : pieces) {
            this.tell(target, piece);
        }
    }

    public void tellWithSplitMessages(final String target, final String... parts) {
        this.tellWithSplitMessages(target, 1000, parts);
    }

    public void tellWithSplitMessages(final UUID target, final int maxSize, final String... parts) {
        this.tellWithSplitMessages(target.toString(), maxSize, parts);
    }

    public void tellWithSplitMessages(final UUID target, final String... parts) {
        this.tellWithSplitMessages(target.toString(), 1000, parts);
    }

    public void tellWithSplitMessages(final GameProfile target, final int maxSize, final String... parts) {
        this.tellWithSplitMessages(target.getUuid(), maxSize, parts);
    }

    public void tellWithSplitMessages(final GameProfile target, final String... parts) {
        this.tellWithSplitMessages(target, 1000, parts);
    }

    public CompletableFuture<Response> craft(final Location outputContainer, final SlotReference[] ingredients) {
        final JsonObject data = new JsonObject();

        outputContainer.apply(data);
        final JsonArray ingredientsArray = new JsonArray();
        for (SlotReference ingredient : ingredients)
            ingredientsArray.add(ingredient == null ? Json.NULL : ingredient.toJson());

        data.add("ingredients", ingredientsArray);

        return this.send("craft", data);
    }

    /**
     * @return Returns the world relative location of the structure
     */
    public CompletableFuture<Location> getWorldLocation() {
        final CompletableFuture<Location> callback = new CompletableFuture<>();

        this.send("get_location", new JsonObject()).whenComplete(CompletableFutureUtil.inheritException(callback, response -> {
            final JsonObject responseData = response.getData();
            callback.complete(new Location(responseData.get("x").asInt(), responseData.get("y").asInt(), responseData.get("z").asInt()));
        }));

        return callback;
    }

    /**
     * Retrieves the inner size of the structure
     */
    public CompletableFuture<Location> getSize() {
        final CompletableFuture<Location> callback = new CompletableFuture<>();

        this.send("get_size", new JsonObject()).whenComplete(CompletableFutureUtil.inheritException(callback, response -> {
            final JsonObject responseData = response.getData();
            callback.complete(new Location(responseData.get("x").asInt(), responseData.get("y").asInt(), responseData.get("z").asInt()));
        }));

        return callback;
    }

    /**
     * Sets a block inside your structure
     *
     * @param location        The location of the block (structure relative)
     * @param block           The block identifier
     * @param sourceContainer The block that will be moved (if null : will be taken from the structure inventory)
     * @param dropsContainer  If a block is being placed, the drops of it will be stored in the given container block (if null: will be put in the structure inventory)
     */
    public CompletableFuture<Response> setBlock(@NotNull final Location location, @NotNull final Block block, @Nullable final Location sourceContainer, @Nullable final Location dropsContainer) {
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

        this.send("set_block", data).whenComplete(CompletableFutureUtil.inheritException(callback, callback::complete));

        return callback;
    }

    /**
     * @param location The structure relative location of the sign
     * @return Returns the text of the sign
     */
    public CompletableFuture<List<String>> getSignText(@NotNull final Location location) {
        final JsonObject data = new JsonObject();
        final CompletableFuture<List<String>> callback = new CompletableFuture<>();

        location.apply(data);

        this.send("get_sign_text", data).whenComplete(CompletableFutureUtil.inheritException(callback, response -> callback.complete(response.getData().get("lines").asArray().values().stream().map(JsonValue::asString).collect(Collectors.toList()))));

        return callback;
    }

    /**
     * @param location The location of the sign
     * @param lines    The lines of the new sign text (must be 4)
     * @return Returns a response, useless
     */
    public CompletableFuture<Response> setSignText(@NotNull final Location location, @NotNull final String[] lines) {
        if (lines.length != 4)
            throw new IllegalArgumentException("Lines must be exactly 4!");

        final JsonObject data = new JsonObject();
        final CompletableFuture<Response> callback = new CompletableFuture<>();

        location.apply(data);

        final JsonArray linesArray = new JsonArray();
        for (final String line : lines)
            linesArray.add(line);

        data.add("lines", linesArray);

        this.send("set_sign_text", data).whenComplete(CompletableFutureUtil.inheritException(callback, callback::complete));

        return callback;
    }

    /**
     * Watches the given block for updates
     *
     * @param location The location of the block to watch, structure relative
     * @return Returns a response, useless
     */
    public CompletableFuture<Response> watch(@NotNull final Location location) {
        final JsonObject data = new JsonObject();
        final CompletableFuture<Response> callback = new CompletableFuture<>();

        location.apply(data);

        this.send("watch", data).whenComplete(CompletableFutureUtil.inheritException(callback, callback::complete));

        return callback;
    }

    /**
     * Stops watching the given block for updates
     *
     * @param location The location of the block to unwatch, structure relative
     * @return Returns a response, useless
     */
    public CompletableFuture<Response> unwatch(@NotNull final Location location) {
        final JsonObject data = new JsonObject();
        final CompletableFuture<Response> callback = new CompletableFuture<>();

        location.apply(data);

        this.send("unwatch", data).whenComplete(CompletableFutureUtil.inheritException(callback, callback::complete));

        return callback;
    }

    /**
     * Watches all blocks for updates
     *
     * @return Returns a response, useless
     */
    public CompletableFuture<Response> watchAll() {
        return this.send("watch_all", new JsonObject());
    }

    /**
     * Stops watching all blocks for updates
     *
     * @return Returns a response, useless
     */
    public CompletableFuture<Response> unwatchAll() {
        return this.send("unwatch_all", new JsonObject());
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
    public CompletableFuture<Response> poll(@NotNull final Location location) {
        final JsonObject data = new JsonObject();

        location.apply(data);

        return this.send("poll", data);
    }

    /**
     * Stops polling a block for updates.
     *
     * @param location The location of the block to unpoll
     * @return Returns a response, useless
     */
    public CompletableFuture<Response> unpoll(@NotNull final Location location) {
        final JsonObject data = new JsonObject();
        location.apply(data);
        return this.send("unpoll", data);
    }

    /**
     * Begins polling all blocks in the structure for updates.
     *
     * @return Returns a response, useless
     */
    public CompletableFuture<Response> pollAll() {
        return this.send("poll_all", new JsonObject());
    }

    /**
     * Stops polling all blocks in the structure for updates.
     *
     * @return Returns a response, useless
     */
    public CompletableFuture<Response> unpollAll() {
        return this.send("unpoll_all", new JsonObject());
    }

    /**
     * @return Returns all entities on the structure
     */
    public CompletableFuture<List<Entity>> getEntities() {
        final CompletableFuture<List<Entity>> callback = new CompletableFuture<>();

        this.send("get_entities", new JsonObject()).whenComplete(CompletableFutureUtil.inheritException(callback, response -> callback.complete(response.getData().get("entities").asArray().values().stream().map(jsonValue -> Entity.fromJson(jsonValue.asObject())).collect(Collectors.toList()))));

        return callback;
    }

    /**
     * @return Returns the inventory of the given container
     */
    public CompletableFuture<List<ItemSlot>> getInventory(@NotNull final Location location) {
        final CompletableFuture<List<ItemSlot>> callback = new CompletableFuture<>();
        final JsonObject data = new JsonObject();

        location.apply(data);

        this.send("get_inventory", data).whenComplete(CompletableFutureUtil.inheritException(callback, response -> callback.complete(response.getData().get("items").asArray().values().stream().map(jsonValue -> ItemSlot.fromJson(this, location, jsonValue.asObject())).collect(Collectors.toList()))));

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
    public CompletableFuture<Response> moveItem(@NotNull final Location sourceContainer, final int itemIndex, @Nullable final Integer amount, @NotNull final Location targetContainer, @Nullable final Integer targetItemIndex) {
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

        return this.send("move_item", data);
    }

    /**
     * Gets the redstone power level of a block
     *
     * @param location Structure relative location of target block
     * @return Returns redstone power level
     */
    public CompletableFuture<Integer> getRedstonePowerLevel(@NotNull final Location location) {
        final CompletableFuture<Integer> callback = new CompletableFuture<>();
        final JsonObject data = new JsonObject();

        location.apply(data);

        this.send("get_power_level", data).whenComplete(CompletableFutureUtil.inheritException(callback, response -> callback.complete(response.getData().get("power").asInt())));

        return callback;
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
        final JsonObject data = new JsonObject();

        data.add("target", target);
        data.add("amount", amount);


        return this.send("pay", data);
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

        this.send("fuelinfo", new JsonObject()).whenComplete(CompletableFutureUtil.inheritException(callback, response -> callback.complete(FuelInfo.fromJson(this, response.getData()))));

        return callback;
    }

    /**
     * @return Returns the block info of the specified structure-relative coordinates
     */
    public CompletableFuture<Block> getBlock(@NotNull final Location location) {
        final CompletableFuture<Block> callback = new CompletableFuture<>();
        final JsonObject data = new JsonObject();
        location.apply(data);

        this.send("get_block", data).whenComplete(CompletableFutureUtil.inheritException(callback, response -> callback.complete(Block.parse(response.getData().get("block").asString()))));

        return callback;
    }
    
    
    public CompletableFuture<Response> send(final String action, final JsonObject data, final boolean waitForResponse) {
        data.add("context", this.id);
        return this.client.getConnection().send(action, data, waitForResponse);
    }

    public CompletableFuture<Response> send(final String action, final JsonObject data) {
        return this.send(action, data, true);
    }

    /**
     * Limits the fuel generation for a specific strategy
     * @param strategy The strategy
     * @param limit The new fuel limit the strategy may generate
     */
    public CompletableFuture<Response> setFuelLimit(final String strategy, final double limit) {
        final JsonObject data = new JsonObject();

        data.add("strategy", strategy);
        data.add("limit", limit);

        return this.send("set_fuel_limit", data);
    }


    public final int getId() {
        return this.id;
    }

    public final ReplCraftClient getClient() {
        return this.client;
    }

    public final OpenCause getCause() {
        return this.openCause;
    }

    public final List<IContextListener> getListeners() {
        return this.listeners;
    }

    public void addListener(final IContextListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(final IContextListener listener) {
        this.listeners.remove(listener);
    }

    public void callListener(final Consumer<? super IContextListener> consumer) {
        new Thread(() -> {
            for (IContextListener listener : this.listeners)
                consumer.accept(listener);
        }, "Listener-Thread").start();
    }

}
