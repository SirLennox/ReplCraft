package dev.sirlennox.replcraftclient.api.inventory.slot;

import com.eclipsesource.json.JsonObject;
import dev.sirlennox.replcraftclient.api.vector.Location;

public class SlotReference {

    private final Location source;
    private final int index;

    public SlotReference(final Location source, final int index) {
        this.source = source;
        this.index = index;
    }

    public final JsonObject toJson() {
        final JsonObject json = new JsonObject();

        json.add("index", this.index);
        this.source.apply(json);

        return json;
    }

    public final int getIndex() {
        return this.index;
    }

    public final Location getSource() {
        return this.source;
    }

}
