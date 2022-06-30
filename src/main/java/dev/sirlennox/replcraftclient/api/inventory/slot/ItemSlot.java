package dev.sirlennox.replcraftclient.api.inventory.slot;

import com.eclipsesource.json.JsonObject;
import dev.sirlennox.replcraftclient.api.inventory.item.Item;
import dev.sirlennox.replcraftclient.api.vector.Location;
import dev.sirlennox.replcraftclient.context.Context;
import org.jetbrains.annotations.NotNull;

public class ItemSlot extends Slot {

    private final Item item;

    public ItemSlot(final Context context, final Location source, final int index, final Item item) {
        super(context, source, index);
        this.item = item;
    }

    public static ItemSlot fromJson(@NotNull final Context context, @NotNull final Location source, @NotNull final JsonObject json) {
        return new ItemSlot(
                context,
                source,
                json.get("index").asInt(),
                Item.fromJson(json)
        );
    }

    public final Item getItem() {
        return this.item;
    }
}
