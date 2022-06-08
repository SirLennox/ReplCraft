package dev.sirlennox.replcraftclient.api.inventory.slot;

import com.eclipsesource.json.JsonObject;
import dev.sirlennox.replcraftclient.api.vector.IntVector;

public class SlotReference {

    private final IntVector source;
    private final int index;

    public SlotReference(final IntVector source, final int index) {
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

    public final IntVector getSource() {
        return this.source;
    }

}
