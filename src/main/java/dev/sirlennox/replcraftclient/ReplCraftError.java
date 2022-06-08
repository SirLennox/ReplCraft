package dev.sirlennox.replcraftclient;

import com.eclipsesource.json.JsonObject;
import org.jetbrains.annotations.NotNull;

public class ReplCraftError extends Exception {

    private final String error;
    private final String message;

    public ReplCraftError(final String error, final String message) {
        this.error = error;
        this.message = message;
    }

    public static ReplCraftError fromJson(@NotNull final JsonObject json) {
        return new ReplCraftError(
                json.get("error").asString(),
                json.get("message").asString()
        );
    }

    public final String getError() {
        return this.error;
    }

    @Override
    public final String getMessage() {
        return this.message;
    }
}
