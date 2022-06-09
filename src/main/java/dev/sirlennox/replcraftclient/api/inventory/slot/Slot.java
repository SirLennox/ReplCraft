package dev.sirlennox.replcraftclient.api.inventory.slot;

import com.eclipsesource.json.JsonObject;
import dev.sirlennox.replcraftclient.ReplCraftClient;
import dev.sirlennox.replcraftclient.api.vector.IntVector;
import dev.sirlennox.replcraftclient.connection.exchange.Response;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class Slot extends SlotReference {

    private final ReplCraftClient client;

    public Slot(final ReplCraftClient client, final IntVector source, final int index) {
        super(source, index);
        this.client = client;
    }

    public static Slot fromJson(@NotNull final ReplCraftClient client, @NotNull final IntVector source, @NotNull final JsonObject json) {
        return new Slot(
                client,
                source,
                json.get("index").asInt()
        );
    }

    /**
     * Moves the item in the slot to a specific container
     *
     * @param amount          The amount that should be moved
     * @param targetContainer The target container where the item should be moved in
     * @param targetItemIndex The item index in the target container where the item should be (If null: any)
     * @return Returns a response, useless
     */
    public CompletableFuture<Response> moveTo(@Nullable final Integer amount, @NotNull final IntVector targetContainer, @Nullable Integer targetItemIndex) {
        return this.client.moveItem(this.getSource(), this.getIndex(), amount, targetContainer, targetItemIndex);
    }

    public CompletableFuture<Response> moveTo(@Nullable final Integer amount, @NotNull final IntVector targetContainer) {
        return this.moveTo(amount, targetContainer, null);
    }

    public CompletableFuture<Response> moveTo(@NotNull final IntVector targetContainer) {
        return this.moveTo(null, targetContainer);
    }

    public final ReplCraftClient getClient() {
        return this.client;
    }
}
