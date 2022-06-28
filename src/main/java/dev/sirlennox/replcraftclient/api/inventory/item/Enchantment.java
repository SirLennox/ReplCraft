package dev.sirlennox.replcraftclient.api.inventory.item;

import com.eclipsesource.json.JsonObject;
import dev.sirlennox.replcraftclient.api.Identifier;

public class Enchantment {

    private final Identifier id;
    private final int level;

    public Enchantment(final Identifier id, final int level) {
        this.id = id;
        this.level = level;
    }

    public static Enchantment fromJson(final JsonObject json) {
        return new Enchantment(
                Identifier.parse(json.get("id").asString()),
                json.get("lvl").asInt()
        );
    }

    public final Identifier getId() {
        return this.id;
    }

    public final int getLevel() {
        return this.level;
    }
}
