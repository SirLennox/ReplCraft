package dev.sirlennox.replcraftclient.api.inventory.slot;

import com.eclipsesource.json.JsonObject;
import dev.sirlennox.replcraftclient.ReplCraftClient;
import dev.sirlennox.replcraftclient.api.vector.Location;
import dev.sirlennox.replcraftclient.connection.exchange.Response;
import dev.sirlennox.replcraftclient.context.Context;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class Slot extends SlotReference {

    private final Context context;

    public Slot(final Context context, final Location source, final int index) {
        super(source, index);
        this.context = context;
    }

    public static Slot fromJson(@NotNull final Context context, @NotNull final Location source, @NotNull final JsonObject json) {
        return new Slot(
                context,
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
    public CompletableFuture<Response> moveTo(@Nullable final Integer amount, @NotNull final Location targetContainer, @Nullable Integer targetItemIndex) {
        return this.context.moveItem(this.getSource(), this.getIndex(), amount, targetContainer, targetItemIndex);
    }

    public CompletableFuture<Response> moveTo(@Nullable final Integer amount, @NotNull final Location targetContainer) {
        return this.moveTo(amount, targetContainer, null);
    }

    public CompletableFuture<Response> moveTo(@NotNull final Location targetContainer) {
        return this.moveTo(null, targetContainer);
    }

    public final Context getContext() {
        return this.context;
    }
}
