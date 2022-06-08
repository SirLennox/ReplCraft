package dev.sirlennox.replcraftclient.api.inventory.slot;

import com.eclipsesource.json.JsonObject;
import dev.sirlennox.replcraftclient.ReplCraftClient;
import dev.sirlennox.replcraftclient.api.inventory.item.Item;
import dev.sirlennox.replcraftclient.api.vector.IntVector;
import org.jetbrains.annotations.NotNull;

public class ItemSlot extends Slot {

    private final Item item;

    public ItemSlot(final ReplCraftClient client, final IntVector source, final int index, final Item item) {
        super(client, source, index);
        this.item = item;
    }

    public static ItemSlot fromJson(@NotNull final ReplCraftClient client, @NotNull final IntVector source, @NotNull final JsonObject json) {
        return new ItemSlot(
                client,
                source,
                json.get("index").asInt(),
                Item.fromJson(json)
        );
    }

    public final Item getItem() {
        return this.item;
    }
}
