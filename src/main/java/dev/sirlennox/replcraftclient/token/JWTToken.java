package dev.sirlennox.replcraftclient.token;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.Base64;

public class JWTToken {

    private final String token;

    public JWTToken(final @NotNull String token) {
        this.token = token;
        if (this.splitSequences().length != 3)
            throw new IllegalArgumentException("Not a valid JWT Token!");
    }

    public final @NotNull JsonObject getHeader() {
        return Json.parse(new String(this.getSequence(0))).asObject();
    }

    public final @NotNull JsonObject getData() {
        return Json.parse(new String(this.getSequence(1))).asObject();
    }

    public final byte[] getSecret() {
        return this.getSequence(2);
    }


    public final @NotNull String[] splitSequences() {
        return this.token.split("\\.");
    }

    private byte[] getSequence(final int index) {
        return Base64.getDecoder().decode(this.splitSequences()[index]);
    }

    public final @NotNull String toString() {
        return this.token;
    }
}
