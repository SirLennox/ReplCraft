package dev.sirlennox.replcraftclient.api;

import com.eclipsesource.json.JsonObject;
import dev.sirlennox.replcraftclient.ReplCraftClient;
import dev.sirlennox.replcraftclient.connection.exchange.Response;
import dev.sirlennox.replcraftclient.context.Context;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Transaction {

    private final Context context;
    private final double amount;
    private final String query;
    private final GameProfile player;
    private final int nonce;

    public Transaction(final Context context, final double amount, final String query, final GameProfile player, final int nonce) {
        this.context = context;
        this.amount = amount;
        this.query = query;
        this.player = player;
        this.nonce = nonce;
    }

    public void accept() {
        this.respond(true);
    }

    public void deny() {
        this.respond(false);
    }

    private void respond(final boolean accept) {
        final JsonObject data = new JsonObject();

        data.add("queryNonce", this.nonce);
        data.add("accept", accept);

        this.context.send("respond", data, false);
    }

    public CompletableFuture<Response> tell(final String message) {
        return this.context.tell(this.getPlayer(), message);
    }

    public void tellWithSplitMessages(final int maxSize, final String... parts) {
        this.context.tellWithSplitMessages(this.getPlayer(), maxSize, parts);
    }

    public void tellWithSplitMessages(final String... parts) {
        this.context.tellWithSplitMessages(this.getPlayer(), parts);
    }

    public static Transaction fromJson(@NotNull final Context context, @NotNull final JsonObject json) throws NumberFormatException {
        return new Transaction(
                context,
                json.get("amount").asDouble(),
                json.get("query").asString(),
                new GameProfile(
                        json.get("player").asString(),
                        UUID.fromString(json.get("player_uuid").asString())
                ),
                json.get("queryNonce").asInt()
        );
    }

    public final double getAmount() {
        return this.amount;
    }

    public final String getQuery() {
        return this.query;
    }

    public final GameProfile getPlayer() {
        return this.player;
    }

    public final int getNonce() {
        return this.nonce;
    }

    public final Context getContext() {
        return this.context;
    }
}
