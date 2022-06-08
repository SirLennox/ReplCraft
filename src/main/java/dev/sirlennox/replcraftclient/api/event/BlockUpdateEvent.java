package dev.sirlennox.replcraftclient.api.event;

import com.eclipsesource.json.JsonObject;
import dev.sirlennox.replcraftclient.api.block.Block;
import dev.sirlennox.replcraftclient.api.vector.IntVector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

public class BlockUpdateEvent {

    private final IntVector location;
    private final Cause cause;
    private final Block block;
    private final String oldBlock;

    public BlockUpdateEvent(@NotNull final IntVector location, @NotNull final Cause cause, @NotNull final Block block, @Nullable final String oldBlock) {
        this.location = location;
        this.cause = cause;
        this.block = block;
        this.oldBlock = oldBlock;
    }

    public static BlockUpdateEvent fromJson(final JsonObject json) {
        return new BlockUpdateEvent(
                new IntVector(
                        json.get("x").asInt(),
                        json.get("y").asInt(),
                        json.get("z").asInt()
                ),
                Cause.getById(json.get("cause").asString()),
                Block.parse(json.get("block").asString()),
                Objects.nonNull(json.get("old_block")) ? json.get("old_block").asString() : null
        );
    }

    public final @NotNull IntVector getLocation() {
        return this.location;
    }

    public final @NotNull Cause getCause() {
        return this.cause;
    }

    public final @NotNull Block getBlock() {
        return this.block;
    }

    public final @Nullable String getOldBlock() {
        return this.oldBlock;
    }


    public enum Cause {
        BREAK("break"), PLACE("place"), POLL("poll"), UNKNOWN(null);

        private final String id;

        Cause(final String id) {
            this.id = id;
        }

        public final String getId() {
            return this.id;
        }

        public static Cause getById(final String id) {
            return Arrays.stream(Cause.values()).filter(cause -> Objects.equals(cause.getId(), id)).findFirst().orElse(Cause.UNKNOWN);
        }
    }
}
