package dev.sirlennox.replcraftclient.api.inventory.item;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import dev.sirlennox.replcraftclient.api.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class Item {

    private final int maxDurability;
    private final int amount;
    private final int durability;
    private final Identifier type;
    private final List<Enchantment> enchantments;
    private final JsonObject meta;

    public Item(final int maxDurability, final int durability, final int amount, final Identifier type, final List<Enchantment> enchantments, final JsonObject meta) {
        this.maxDurability = maxDurability;
        this.amount = amount;
        this.durability = durability;
        this.type = type;
        this.enchantments = enchantments;
        this.meta = meta;
    }

    public static Item fromJson(@NotNull final JsonObject json) {
        return new Item(
                json.get("maxDurability").asInt(),
                json.get("durability").asInt(),
                json.get("amount").asInt(),
                Identifier.parse(json.get("type").asString()),
                json.get("enchantments").asArray().values().stream().map(JsonValue::asObject).map(Enchantment::fromJson).collect(Collectors.toList()),
                json.get("meta").asObject()
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

    public final List<Enchantment> getEnchantments() {
        return this.enchantments;
    }

    public final JsonObject getMeta() {
        return this.meta;
    }
}
