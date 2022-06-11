package dev.sirlennox.replcraftclient.api.inventory.item;

import com.eclipsesource.json.JsonObject;
import dev.sirlennox.replcraftclient.api.Identifier;
import org.jetbrains.annotations.NotNull;

public class Item {

    private final int maxDurability;
    private final int amount;
    private final int durability;
    private final Identifier type;

    public Item(final int maxDurability, final int durability, final int amount, final Identifier type) {
        this.maxDurability = maxDurability;
        this.amount = amount;
        this.durability = durability;
        this.type = type;
    }

    public static Item fromJson(@NotNull final JsonObject json) {
        return new Item(
                json.get("maxDurability").asInt(),
                json.get("durability").asInt(),
                json.get("amount").asInt(),
                Identifier.parse(json.get("type").asString())
        );
    }


    public final int getMaxDurability() {
        return this.maxDurability;
    }

    public final int getAmount() {
        return this.amount;
    }

    public final int getDurability() {
        return this.durability;
    }

    public final Identifier getType() {
        return this.type;
    }
}
